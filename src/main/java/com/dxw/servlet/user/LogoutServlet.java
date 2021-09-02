package com.dxw.servlet.user;

import com.dxw.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //注销登录
        //只需要删除Session对象返回登录页面即可
        //获取Session对象
        //获得项目路径
        String contextPath = req.getContextPath();
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        if (attribute!=null){
            req.getSession().removeAttribute(Constants.USER_SESSION);
            //返回登录页面    使用重定向
            resp.sendRedirect(contextPath+"/login.jsp");
        }else {
            resp.sendRedirect(contextPath+"/login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
