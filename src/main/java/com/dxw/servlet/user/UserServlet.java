package com.dxw.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.dxw.pojo.Role;
import com.dxw.pojo.User;
import com.dxw.service.role.RoleServiceImpl;
import com.dxw.service.user.UserService;
import com.dxw.service.user.UserServiceImpl;
import com.dxw.util.Constants;
import com.dxw.util.PageSupport;
import com.mysql.jdbc.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

//实现代码的复用！！！
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取隐藏域
        //这个隐藏域是在用户修改密码里面的，就执行修改密码的方法
        String method = req.getParameter("method");
        if (method!=null&&method.equals("savepwd")){
            //自定义的修改密码的方法，这边直接调用，实现Servlet复用(重载方法的复用)
            modifyPassWord(req, resp);
        }else if (method!=null&&method.equals("pwdmodify")){
            //检查旧密码是否正确
            checkOldPassword(req, resp);
        } else if (method!=null&&method.equals("query")) {
            userQuery(req, resp);
        }else if(method!=null&&method.equals("add")){
            addUser(req, resp);
        }else if(method!=null&&method.equals("ucexist")){
            judgeUser(req, resp);
        }else if(method!=null&&method.equals("view")){
            queryUserInfo(req, resp);
        }else if(method!=null&&method.equals("deluser")){
            deleteUser(req, resp);
        }else if(method!=null&&method.equals("modify")){
            checkOldInfo(req, resp);
        }else if(method!=null&&method.equals("getrolelist")){
            checkOldRoleList(req, resp);
        }else if(method!=null&&method.equals("modifyexe")){
            modifyUserInfo(req, resp);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    //修改密码
    public void modifyPassWord(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //获取前端数据
        //拿到新密码
        String newpassword = req.getParameter("newpassword");
        //获取当前用户（从Session中获取，Session中保存了用户的完整信息）！！！
        Object user = req.getSession().getAttribute(Constants.USER_SESSION);

        boolean flag = false;

        //判断用户和新密码是否为空
        if (user!=null&&newpassword!=null&&newpassword.length()>6){
            UserService userService = new UserServiceImpl();
            //获取当前用户id,
            int id = ((User) user).getId();
            flag = userService.modifyPassword(id, newpassword);
            if (flag){
                req.setAttribute("message","密码修改成功，请重写登录！");
                //清除Session信息
                req.getSession().removeAttribute(Constants.USER_SESSION);
            }else {
                req.setAttribute("message","密码修改失败！");
            }
        }else {
            req.setAttribute("message","新密码有问题！");
        }

        //返回错误信息
        //String contextPath = req.getContextPath();
        req.getRequestDispatcher("pwdmodify.jsp").forward(req,resp);
    }

    //验证旧密码,Session中存在旧密码，使用这里面的密码进行对比检查
    public void checkOldPassword(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        //获取请求的参数
        String oldpassword = req.getParameter("oldpassword");

        //获取Session
        Object user = req.getSession().getAttribute(Constants.USER_SESSION);

        //Map集合，用于保存需要传递给前端的数据
        Map<String, String> resultMap = new HashMap<>();

        if (user==null){//user为空，说明Session过期了
            resultMap.put("result","sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){
            resultMap.put("result","error");//旧密码为空
        }else {
            String userPassword = ((User) user).getUserPassword();
            if (userPassword.equals(oldpassword)){
                resultMap.put("result","true");//旧密码正确
            }else {
                resultMap.put("result","false");//旧密码不正确
            }
        }

        //把数据响应给前端
        resp.setContentType("application/json");    //设置相应的数据的类型
        PrintWriter writer = resp.getWriter();      //
        //JSONArray 阿里巴巴工具类，格式转换,把Map转换成了Json格式的数据，写给前端
        writer.write(JSONArray.toJSONString(resultMap));
        //写出数据一定要记得flush缓存
        writer.flush();
        //关闭流，避免造成内存溢出，不关闭会占用大量资源
        writer.close();
    }

    //用户列表，实现分页，下拉框查询，模糊查询功能
    public void userQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //从前端获取数据
        //前端输入的，想查询的用户名称
        String queryName = req.getParameter("queryname");
        //前端输入的，想查询的用户角色
        String queryUserRole = req.getParameter("queryUserRole");
        //pageIndex,前端设置的默认初始页面
        String pageIndex = req.getParameter("pageIndex");

        //如果没有输入查询的名字，那么就给空字符串
        if (queryName == null){
            queryName = "";
        }

        int queryRole = 0;
        //将前端传回来的角色数据从字符串解析回数字
        //避免空值错误
        if (!StringUtils.isNullOrEmpty(queryUserRole)){
            queryRole = Integer.parseInt(queryUserRole);
        }

        //获取用户数量
        UserServiceImpl userService = new UserServiceImpl();
        int userCount = userService.getUserCount(queryName,queryRole);


        //当前页面
        //设置默认初始状态时第一页
        int pageCurrentNo = 1;
        //页面大小
        int pageSize = 5;
        //总页数
        int totalPageCount = (int)(userCount/pageSize)+1;
        //避免空指针异常
        if (!StringUtils.isNullOrEmpty(pageIndex)){
            pageCurrentNo = Integer.parseInt(pageIndex);
        }
        //分页支持（上一页、下一页）
//        PageSupport pageSupport = new PageSupport();
//        pageSupport.setPageSize(pageSize);
//        pageSupport.setCurrentPageNo(pageCurrentNo);
//        pageSupport.setTotalCount(userCount);
//        pageSupport.setTotalPageCount(totalPageCount);
//
//        //避免页面溢出
//        if (pageSupport.getCurrentPageNo()<1){
//            pageSupport.setCurrentPageNo(1);
//        }else if (pageSupport.getCurrentPageNo()>pageSupport.getTotalPageCount()){
//            pageSupport.setCurrentPageNo(pageSupport.getTotalPageCount());
//        }


        //用户列表显示
        //获取用户列表
        List<User> userList = userService.getUserList(queryName,queryRole,pageCurrentNo,pageSize);

        //获取角色列表
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();

        //总用户数量
        req.setAttribute("totalCount",userCount);
        //当前页面
        req.setAttribute("currentPageNo",pageCurrentNo);
        //总页面数
        req.setAttribute("totalPageCount",totalPageCount);
        //用户角色
        req.setAttribute("roleList",roleList);
        //用户列表
        req.setAttribute("userList",userList);
        //将查询输入的数据也传回去,不然点击查询后，查询的条件就清空了
        req.setAttribute("queryUserName",queryName);
        req.setAttribute("queryUserRole",queryUserRole);


        req.getRequestDispatcher("userlist.jsp").forward(req,resp);
    }

    //判断用户名是否已经存在
    public void judgeUser(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        //获取前端传递的数据(用户用户登录的用户名)
        String newUserCode = req.getParameter("userCode");

        HashMap<String, String> hashMap = null;

        if (!StringUtils.isNullOrEmpty(newUserCode)) {
            //获取全部的用户
            UserServiceImpl userService = new UserServiceImpl();
            List<User> userList = userService.getUserList(null, 0, 1, 0);

            //创建Map集合，用于保存需要返回的键值对
            hashMap = new HashMap<>();

            //遍历全部的用户，获取用户用于登录的用户名
            for (User user : userList) {
                String userCode = user.getUserCode();
                if (userCode.equals(newUserCode)) {
                    //发现有相同的数据(用于登录的用户名)
                    hashMap.put("userCode", "exist");
                }
            }
            //没有相同的用户名
            if (hashMap.isEmpty()) {
                hashMap.put("userCode", "noExist");
            }
        }else {
            if (newUserCode==""){
                hashMap.put("userCode", "none");
            }else {
                hashMap.put("userCode", "exist");
            }
        }

        //把数据响应给前端
        resp.setContentType("application/json");    //设置响应的数据类型
        //使用流输出
        PrintWriter writer = resp.getWriter();
        //把Map转换成Json格式的数据
        writer.write(JSONArray.toJSONString(hashMap));
        //刷新缓冲
        writer.flush();
        //关闭流
        writer.close();
    }

    //添加用户
    public void addUser(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        //获取前端数据
        String userCode = req.getParameter("userCode");
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        System.out.println(birthday);

        //创建用户对象，存放数据
        User user = new User();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setGender(Integer.parseInt(gender));
        //user.setBirthday(new Date(birthday));//不能这么写！！！
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
            //user.setAge(user.getAge());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.parseInt(userRole));

        //获取事务是否成功的结果
        boolean flag = false;

        //将新添加的User对象传递回后端
        UserServiceImpl userService = new UserServiceImpl();
        flag = userService.addNewUser(user);

        //查询所有的用户列表

        String contextPath = req.getContextPath();

        //重定向回用户列表页面
        if (flag){
            //这样写虽然可以重定向到用户列表页面，但是不会有任何数据，不好看
            //resp.sendRedirect(contextPath+"/jsp/userlist.jsp");
            //直接给予重定向的路径和参数，可以显示数据
            resp.sendRedirect(contextPath+"/jsp/user.do?method=query");
        }else {
            resp.sendRedirect(contextPath+"/jsp/add.jsp");
        }
    }

    //产看用户详细信息
    public void queryUserInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //获取前端数据
        String userid = req.getParameter("uid");

        User user = null;

        if (!StringUtils.isNullOrEmpty(userid)){
            //把查询的产生传给Service层,获得对应id的用户
            UserServiceImpl userService = new UserServiceImpl();
            user = userService.queryUserInfo(Integer.parseInt(userid));
        }

        //页面跳转
        if (user!=null){
            //传递用户信息回到前端页面
            req.setAttribute("user",user);
            req.getRequestDispatcher("userview.jsp").forward(req,resp);
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/error.jsp");
        }

    }

    //删除用户
    public void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String userid = req.getParameter("uid");

        //存放键值对
        HashMap<String, String> hashMap = new HashMap<>();

        //判断用户是否存在
        //获取全部用户
        UserServiceImpl userService = new UserServiceImpl();



        if (!StringUtils.isNullOrEmpty(userid)){
            List<User> userList = userService.getUserList(null,0,0,0);
            for (User user : userList) {
                //存在这个对象
                if (user.getId()==Integer.parseInt(userid)){
                    //判断删除事务是否成功
                    boolean b = userService.deleteUserInfo(Integer.parseInt(userid));
                    if (b){
                        hashMap.put("delResult","true");
                    }else {
                        hashMap.put("delResult","false");
                    }
                }
            }
        }else {
            hashMap.put("delResult","notexist");
        }

        //没查到该用户个人
        if (hashMap.isEmpty()){
            hashMap.put("delResult","notexist");
        }

        //设置响应数据的类型
        resp.setContentType("application/json");
        //获取流
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(hashMap));
        writer.flush();
        //关闭流
        writer.close();
    }

    //显示用户需要修改的信息
    public void checkOldInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String uid = req.getParameter("uid");

        User user = null;
        if (!StringUtils.isNullOrEmpty(uid)){
            int userId = Integer.parseInt(uid);
            UserServiceImpl userService = new UserServiceImpl();
            user = userService.queryUserInfo(userId);
            if (user!=null){
                req.setAttribute("user",user);
                //一定要加forward()函数，不然页面不会跳转！！！
                req.getRequestDispatcher("usermodify.jsp").forward(req,resp);
            }else {
                resp.sendRedirect(req.getContextPath()+"/jsp/error.jsp");
            }
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/error.jsp");
        }
    }

    //修改或者添加用户，响应用户角色列表
    public void checkOldRoleList(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(roleList));
        writer.flush();
        writer.close();
    }

    //修改用户信息
    public void modifyUserInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String userId = req.getParameter("uid");
        String userName = req.getParameter("userName");
        String gender = req.getParameter("gender");
        String birthday = req.getParameter("birthday");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String userRole = req.getParameter("userRole");

        User user = new User();
        user.setId(Integer.parseInt(userId));
        user.setUserName(userName);
        user.setGender(Integer.parseInt(gender));
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setPhone(phone);
        user.setAddress(address);
        user.setUserRole(Integer.parseInt(userRole));

        boolean flag = false;
        //将用户传给业务层
        UserServiceImpl userService = new UserServiceImpl();
        flag = userService.updateUserInfo(user);

        //跳转页面
        if (flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/user.do?method=query");
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/usermodify.jsp");
        }
    }
}
