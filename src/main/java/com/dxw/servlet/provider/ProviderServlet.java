package com.dxw.servlet.provider;

import com.alibaba.fastjson.JSONArray;
import com.dxw.pojo.Provider;
import com.dxw.pojo.User;
import com.dxw.service.provider.ProviderService;
import com.dxw.service.provider.ProviderServiceImpl;
import com.dxw.service.user.UserServiceImpl;
import com.mysql.jdbc.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class ProviderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取前端参数
        String method = req.getParameter("method");
        if (!StringUtils.isNullOrEmpty(method)){
            if (method.equals("query")){
                queryProviderList(req,resp);
            }else if (method.equals("add")){
                addNewProvider(req,resp);
            }else if (method.equals("view")){
                queryProviderInfo(req,resp);
            }else if (method.equals("delprovider")){
                deleteProvider(req,resp);
            }else if (method.equals("modify")){
                checkModifyProvider(req,resp);
            }else if (method.equals("modifyexe")){
                modifyProviderInfo(req,resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void queryProviderList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String queryProCode = req.getParameter("queryProCode");
        String queryProName = req.getParameter("queryProName");
        String proCode = "";
        String proName = "";

        if (!StringUtils.isNullOrEmpty(queryProCode)){
            proCode = queryProCode;
        }
        if (!StringUtils.isNullOrEmpty(queryProName)){
            proName = queryProName;
        }

        ProviderService providerService = new ProviderServiceImpl();

        List<Provider> proList = providerService.getProList(proCode, proName);

        req.setAttribute("providerList",proList);
        req.setAttribute("queryProCode",proCode);
        req.setAttribute("queryProName",proName);
        req.getRequestDispatcher("providerlist.jsp").forward(req,resp);
    }

    public void addNewProvider(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        //从前端获取数据
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");

        //创建提供商用户
        Provider provider = new Provider();
        //添加数据
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setProDesc(proDesc);

        //判断添加业务是否成功
        boolean flag = false;
        //调用service层的添加业务
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        flag = providerService.addNewProvider(provider);

        String contextPath = req.getContextPath();
        //跳转页面，添加成功就进入供应商列表页面，失败，页面不跳转
        if (flag){
            resp.sendRedirect(contextPath+"/jsp/provider.do?method=query");
        }else{
            resp.sendRedirect(contextPath+"/jsp/provideradd.jsp");
        }
    }

    public void queryProviderInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //获取前端数据
        String proid = req.getParameter("proid");
        Provider provider = null;

        boolean flag = false;

        if (!StringUtils.isNullOrEmpty(proid)){
            ProviderServiceImpl providerService = new ProviderServiceImpl();
            List<Provider> proList = providerService.getProList(null,null);
            for (Provider provider1 : proList) {
                if (provider1.getId()==Integer.parseInt(proid)){
                    provider = provider1;
                    flag = true;
                }
            }
        }

        if (flag&&provider!=null){
            req.setAttribute("provider",provider);
            req.getRequestDispatcher("providerview.jsp").forward(req,resp);
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/error.jsp");
        }
    }

    public void deleteProvider(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String providerId = req.getParameter("proid");
        HashMap<String, String> hashMap = new HashMap<>();

        int count;
        if (!StringUtils.isNullOrEmpty(providerId)){
            int proId = Integer.parseInt(providerId);
            ProviderServiceImpl providerService = new ProviderServiceImpl();
            List<Provider> proList = providerService.getProList(null,null);
            for (Provider provider : proList) {
                if (provider.getId()==proId){//查找，发现存在这个供应商
                    //调用Service层的删除供应商的业务
                    count = providerService.deleteProvider(proId);
                    if (count>0){
                        //有订单，返回订单数量
                        hashMap.put("delResult",count+"");
                    }else {
                        //没有订单
                        if (count==0){
                            hashMap.put("delResult","false");
                        }else if (count==-1){
                            hashMap.put("delResult","true");
                        }
                    }
                }
            }
        }else {
            hashMap.put("delResult","notexist");
        }

        if (hashMap.isEmpty()){
            hashMap.put("delResult","notexist");
        }

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        //转化为Json格式输出
        writer.write(JSONArray.toJSONString(hashMap));
        writer.flush();
        writer.close();
    }

    public void checkModifyProvider(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String proId = req.getParameter("proid");

        Provider provider = null;
        if (!StringUtils.isNullOrEmpty(proId)){
            int providerId = Integer.parseInt(proId);
            ProviderServiceImpl providerService = new ProviderServiceImpl();
            List<Provider> proList = providerService.getProList(null,null);
            for (Provider provider1 : proList) {
                if (provider1.getId()==providerId){
                    provider = provider1;
                    req.setAttribute("provider",provider);
                    //一定要加forward()函数，不然页面不会跳转！！！
                    req.getRequestDispatcher("providermodify.jsp").forward(req,resp);
                }
            }
            if (provider==null){
                resp.sendRedirect(req.getContextPath()+"/jsp/error.jsp");
            }
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/error.jsp");
        }
    }

    public void modifyProviderInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //从前端获取数据
        String proId = req.getParameter("proid");
        String proCode = req.getParameter("proCode");
        String proName = req.getParameter("proName");
        String proContact = req.getParameter("proContact");
        String proPhone = req.getParameter("proPhone");
        String proAddress = req.getParameter("proAddress");
        String proFax = req.getParameter("proFax");
        String proDesc = req.getParameter("proDesc");

        //创建提供商用户
        Provider provider = new Provider();
        //添加数据
        provider.setId(Integer.parseInt(proId));
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProAddress(proAddress);
        provider.setProFax(proFax);
        provider.setProDesc(proDesc);
        boolean flag = false;
        //将用户传给业务层
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        flag = providerService.updateProviderInfo(provider);

        //跳转页面
        if (flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/provider.do?method=query");
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/providermodify.jsp");
        }
    }
}
