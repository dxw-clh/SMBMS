package com.dxw.servlet.bill;

import com.alibaba.fastjson.JSONArray;
import com.dxw.pojo.Bill;
import com.dxw.pojo.Provider;
import com.dxw.service.bill.BillServiceImpl;
import com.dxw.service.provider.ProviderServiceImpl;
import com.mysql.jdbc.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class BillServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取前端参数
        String method = req.getParameter("method");
        if (!StringUtils.isNullOrEmpty(method)){
            if (method.equals("query")){
                queryProviderList(req,resp);
            }else if (method.equals("view")){
                queryBillInfo(req,resp);
            }else if (method.equals("delbill")){
                deleteBill(req,resp);
            }else if (method.equals("modify")){
                checkModifyBill(req,resp);
            }else if (method.equals("getproviderlist")){
                getProviderList(req,resp);
            }else if (method.equals("add")){
                addNewBill(req,resp);
            }else if (method.equals("modifysave")){
                modifyBillInfo(req,resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void queryProviderList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        //查询的商品名称
        String name = req.getParameter("queryProductName");
        //供应商id
        String queryProviderId = req.getParameter("queryProviderId");
        //是否已经支付
        String queryIsPayment = req.getParameter("queryIsPayment");

        String proName = "";
        int providerId = 0;
        int isPayment = 0;

        if (!StringUtils.isNullOrEmpty(name)){
            proName = name;
        }
        if (!StringUtils.isNullOrEmpty(queryProviderId)){
            providerId = Integer.parseInt(queryProviderId);
        }
        if (!StringUtils.isNullOrEmpty(queryIsPayment)){
            isPayment = Integer.parseInt(queryIsPayment);
        }

        ProviderServiceImpl providerService = new ProviderServiceImpl();
        List<Provider> proList = providerService.getProList(null, null);

        BillServiceImpl billService = new BillServiceImpl();
        List<Bill> billList = billService.getBillList(proName, providerId, isPayment);

        //返回订单列表
        req.setAttribute("billList",billList);
        //返回查询条件:如果步返回查询条件，每一次查询就会清空输入的查询条件
        //返回查询的订单名称
        req.setAttribute("queryProductName",proName);
        //返回供应商列表
        req.setAttribute("providerList",proList);
        //返回供应商id
        req.setAttribute("queryProviderId",providerId);
        //返回是否支付的查询条件
        req.setAttribute("queryIsPayment",isPayment);

        req.getRequestDispatcher("billlist.jsp").forward(req,resp);
    }

    public void queryBillInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String billId = req.getParameter("billid");

        boolean flag = false;
        Bill billInfo = null;

        BillServiceImpl billService = new BillServiceImpl();
        if (!StringUtils.isNullOrEmpty(billId)){
            //得到全部的订单
            List<Bill> billList = billService.getBillList("",0,0);
            //遍历订单
            for (Bill bill : billList) {
                if (bill.getId()==Integer.parseInt(billId)){
                    flag = true;
                    billInfo = bill;
                }
            }
        }

        if (flag&&billInfo!=null){
            req.setAttribute("bill",billInfo);
            req.getRequestDispatcher("billview.jsp").forward(req,resp);
        }else {
            //重定向到错误页面
            resp.sendRedirect(req.getContextPath()+"/jsp/error.jsp");
        }
    }

    public void deleteBill(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String billId = req.getParameter("billid");
        HashMap<String, String> hashMap = new HashMap<>();

        if (!StringUtils.isNullOrEmpty(billId)){
            int id = Integer.parseInt(billId);
            BillServiceImpl billService = new BillServiceImpl();
            List<Bill> billList = billService.getBillList(null,0,0);
            for (Bill bill : billList) {
                if (bill.getId()==id){
                    //调用删除业务
                    boolean b = billService.deletSimpleBill(id);
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
        if (hashMap.isEmpty()){
            hashMap.put("delResult","notexist");
        }

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(hashMap));
        writer.flush();
        writer.close();
    }

    public void checkModifyBill(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String bId = req.getParameter("billid");

        Bill bill = null;
        if (!StringUtils.isNullOrEmpty(bId)){
            int providerId = Integer.parseInt(bId);
            BillServiceImpl billService = new BillServiceImpl();
            List<Bill> billList = billService.getBillList(null,0,0);
            for (Bill bill1 : billList) {
                if (bill1.getId()==providerId){
                    bill = bill1;
                    req.setAttribute("bill",bill);
                    //一定要加forward()函数，不然页面不会跳转！！！
                    req.getRequestDispatcher("billmodify.jsp").forward(req,resp);
                }
            }
            if (bill==null){
                resp.sendRedirect(req.getContextPath()+"/jsp/error.jsp");
            }
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/error.jsp");
        }
    }

    public void getProviderList(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        ProviderServiceImpl providerService = new ProviderServiceImpl();
        List<Provider> proList = providerService.getProList(null,null);

        resp.setContentType("application/json");
        PrintWriter writer = resp.getWriter();
        writer.write(JSONArray.toJSONString(proList));
        writer.flush();
        writer.close();
    }

    public void addNewBill(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String billCode = req.getParameter("billCode");
        String productName = req.getParameter("productName");
        String productUnit = req.getParameter("productUnit");
        String productCount = req.getParameter("productCount");
        String totalPrice = req.getParameter("totalPrice");
        String providerId = req.getParameter("providerId");
        String isPayment = req.getParameter("isPayment");

        Bill bill = new Bill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount));
        bill.setTotalPrice(new BigDecimal(totalPrice));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setIsPayment(Integer.parseInt(isPayment));

        boolean flag = false;
        //把数据传递个业务层
        BillServiceImpl billService = new BillServiceImpl();
        flag = billService.addNewBill(bill);

        if (flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/billadd.jsp");
        }

    }

    public void modifyBillInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String id = req.getParameter("id");
        String billCode = req.getParameter("billCode");
        String productName = req.getParameter("productName");
        String productUnit = req.getParameter("productUnit");
        String productCount = req.getParameter("productCount");
        String totalPrice = req.getParameter("totalPrice");
        String providerId = req.getParameter("providerId");
        String isPayment = req.getParameter("isPayment");

        Bill bill = new Bill();
        bill.setId(Integer.parseInt(id));
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount));
        bill.setTotalPrice(new BigDecimal(totalPrice));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setIsPayment(Integer.parseInt(isPayment));

        boolean flag = false;
        //把数据传递个业务层
        BillServiceImpl billService = new BillServiceImpl();
        flag = billService.updateBillInfo(bill);

        if (flag){
            resp.sendRedirect(req.getContextPath()+"/jsp/bill.do?method=query");
        }else {
            resp.sendRedirect(req.getContextPath()+"/jsp/billmodify.jsp");
        }
    }
}
