package com.dxw.service.bill;

import com.dxw.pojo.Bill;

import java.util.List;

public interface BillService {
    //获取订单列表
    List<Bill> getBillList(String productName,int providerId,int isPayment);

    //删除单个订单
    boolean deletSimpleBill(int billId);

    //添加订单
    boolean addNewBill(Bill bill);

    //修改订单
    boolean updateBillInfo(Bill bill);
}
