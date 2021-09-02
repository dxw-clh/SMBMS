package com.dxw.filter;

import com.dxw.pojo.User;
import com.dxw.util.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //登录拦截功能
        //没登录的用户不允许访问除登录页面之外的页面
        //获取Session
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取项目路径
        String contextPath = request.getContextPath();
        //获取指定Session信息
        User attribute = (User) request.getSession().getAttribute(Constants.USER_SESSION);
        //如果attribute为空，说明没有登录，跳转到错误页面，如果不为空，不跳转，继续监听拦截
        if (attribute==null){
            //Session对象的值为空，说明退出登录了，要拦截其他页面的访问
            response.sendRedirect(contextPath+"/login.jsp");
        }else {
            //不要让程序中断
            //使用方法提供的对象，不要用自己转型获取的！
            filterChain.doFilter(servletRequest,servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
