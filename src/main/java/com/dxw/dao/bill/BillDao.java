package com.dxw.dao.bill;

import com.dxw.pojo.Bill;

import java.sql.Connection;
import java.util.List;

public interface BillDao {
    //查询全部的订单
    List<Bill> getBillList(Connection connection ,String productName,int providerId,int isPayment);

    //删除订单
    int deleteBill(Connection connection,int billId);

    //添加订单
    int addBill(Connection connection,Bill bill);

    //修改订单
    int modifyBill(Connection connection,Bill bill);
}
