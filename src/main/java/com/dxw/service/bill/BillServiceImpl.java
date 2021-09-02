package com.dxw.service.bill;

import com.dxw.dao.BaseDao;
import com.dxw.dao.bill.BillDao;
import com.dxw.dao.bill.BillDaoImpl;
import com.dxw.pojo.Bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BillServiceImpl implements BillService{
    private BillDao billDao;

    public BillServiceImpl(){
        billDao = new BillDaoImpl();
    }

    @Override
    public List<Bill> getBillList(String productName, int providerId, int isPayment) {
        Connection connection = BaseDao.getConnection();

        List<Bill> billList = billDao.getBillList(connection, productName, providerId, isPayment);

        BaseDao.freeResource(connection,null,null);

        return billList;
    }

    @Override
    public boolean deletSimpleBill(int billId) {
        boolean flag = false;
        Connection connection = BaseDao.getConnection();
        try {
            connection.setAutoCommit(false);
            int i = billDao.deleteBill(connection, billId);
            if (i>0){
                flag = true;
            }
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        } finally {
            try {
                connection.commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return flag;
    }

    @Override
    public boolean addNewBill(Bill bill) {
        boolean flag = false;
        Connection connection = BaseDao.getConnection();
        try {
            connection.setAutoCommit(false);
            int i = billDao.addBill(connection, bill);
            if (i>0){
                flag = true;
            }
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        } finally {
            try {
                connection.commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            BaseDao.freeResource(connection,null,null);
        }

        return flag;
    }

    @Override
    public boolean updateBillInfo(Bill bill) {
        boolean flag = false;
        Connection connection = BaseDao.getConnection();
        try {
            connection.setAutoCommit(false);
            int i = billDao.modifyBill(connection,bill);
            if (i>0){
                flag = true;
            }
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        } finally {
            try {
                connection.commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            BaseDao.freeResource(connection,null,null);
        }

        return flag;
    }
}
