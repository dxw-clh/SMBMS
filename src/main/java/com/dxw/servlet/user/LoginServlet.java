package com.dxw.servlet.user;

import com.dxw.pojo.User;
import com.dxw.service.user.UserService;
import com.dxw.service.user.UserServiceImpl;
import com.dxw.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    private UserService userService;
    public LoginServlet(){
        this.userService = new UserServiceImpl();
    }

    //Servlet控制层，调用业务层（Service）
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //从前端获取数据
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");

        //调用业务层，登录业务，获取需要登录的用户
        User user = userService.login(userCode, userPassword);

        //获得项目路径
        String contextPath = req.getContextPath();

        //判定数据库是否存在该人
        if (user!=null){
            //查到有这个人，给这个人分配一个Session，设置Session的值就为user，方便我们获取这个人的信息
            //这也是为了之后的登录拦截做准备，登录了有Session就可以访问网站的其他页面，否则需要登录
            req.getSession().setAttribute(Constants.USER_SESSION,user);
            if (user.getUserCode().equals(userCode)&&user.getUserPassword().equals(userPassword)) {
                //页面跳转到主页   使用重定向
                resp.sendRedirect(contextPath+"/jsp/frame.jsp");
            }else {//如果用户名或者密码不正确，转发回登录页面
                //转发会登录页面的时候，顺带加上提示信息，转发回去
                req.setAttribute("error","用户名或者密码错误");
                req.getRequestDispatcher("/login.jsp").forward(req,resp);
            }
        }else {
            //如果用户名或者密码不正确，转发回登录页面
            //转发会登录页面的时候，顺带加上提示信息，转发回去
            req.setAttribute("error","用户名或者密码错误");
            req.getRequestDispatcher("/login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
