package com.dxw.dao.bill;

import com.dxw.dao.BaseDao;
import com.dxw.pojo.Bill;
import com.dxw.pojo.Provider;
import com.mysql.jdbc.StringUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BillDaoImpl implements BillDao{
    @Override
    public List<Bill> getBillList(Connection connection, String productName, int providerId, int isPayment) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Bill bill = null;
        List<Bill> billsList = new ArrayList<>();
        ArrayList<Object> paramsList = new ArrayList<>();
        StringBuffer sql = null;

        if (connection!=null){
            sql = new StringBuffer();
            sql.append("select b.*,p.proName from smbms_bill b,smbms_provider p where b.providerId = p.id");
            if (!StringUtils.isNullOrEmpty(productName)){
                sql.append(" and b.productName like ?");
                paramsList.add("%"+productName+"%");
            }
            if (providerId>0){
                sql.append(" and p.id = ?");
                paramsList.add(providerId);
            }
            if (isPayment>0){
                sql.append(" and b.isPayment = ?");
                paramsList.add(isPayment);
            }

            Object[] params = paramsList.toArray();

            try {
                resultSet = BaseDao.executeQuery(connection, sql.toString(), params, resultSet, preparedStatement);
                while (resultSet.next()){
                    bill = new Bill();
                    bill.setId(resultSet.getInt("id"));
                    bill.setBillCode(resultSet.getString("billCode"));
                    bill.setProductName(resultSet.getString("productName"));
                    bill.setProductDesc(resultSet.getString("productDesc"));
                    bill.setProductUnit(resultSet.getString("productUnit"));
                    bill.setProductCount(resultSet.getBigDecimal("productCount"));
                    bill.setTotalPrice(resultSet.getBigDecimal("totalPrice"));
                    bill.setIsPayment(resultSet.getInt("isPayment"));
                    bill.setCreatedBy(resultSet.getInt("createdBy"));
                    bill.setCreationDate(resultSet.getDate("creationDate"));
                    bill.setModifyBy(resultSet.getInt("modifyBy"));
                    bill.setModifyDate(resultSet.getDate("modifyDate"));
                    bill.setProviderId(resultSet.getInt("providerId"));

                    //连表查询得到的结果，用户显示供应商名称
                    bill.setProviderName(resultSet.getString("proName"));

                    billsList.add(bill);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseDao.freeResource(null,preparedStatement,resultSet);
            }
        }

        return billsList;
    }

    @Override
    public int deleteBill(Connection connection, int billId) {
        PreparedStatement preparedStatement = null;
        int count = 0;

        if (connection!=null){
            String sql = "delete from smbms_bill where id = ?";
            Object[] param = {billId};
            try {
                count = BaseDao.executeUpdate(connection, sql, param, preparedStatement);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseDao.freeResource(null,preparedStatement,null);
            }
        }

        return count;
    }

    @Override
    public int addBill(Connection connection, Bill bill) {
        PreparedStatement preparedStatement = null;
        ArrayList<Object> paramsList = new ArrayList<>();
        int count = 0;
        if (connection!=null){
            String sql = "insert smbms_bill (billCode,productName,productUnit,productCount," +
                    "totalPrice,providerId,isPayment) values (?,?,?,?,?,?,?)";
            paramsList.add(bill.getBillCode());
            paramsList.add(bill.getProductName());
            paramsList.add(bill.getProductUnit());
            paramsList.add(bill.getProductCount());
            paramsList.add(bill.getTotalPrice());
            paramsList.add(bill.getProviderId());
            paramsList.add(bill.getIsPayment());
            Object[] params = paramsList.toArray();
            try {
                count = BaseDao.executeUpdate(connection, sql, params, preparedStatement);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseDao.freeResource(null,preparedStatement,null);
            }
        }

        return count;
    }

    @Override
    public int modifyBill(Connection connection, Bill bill) {
        PreparedStatement preparedStatement = null;
        ArrayList<Object> paramsList = new ArrayList<>();
        int count = 0;
        if (connection!=null){
            String sql = "update smbms_bill set billCode = ?,productName = ?," +
                    "productUnit = ?,productCount = ?,totalPrice = ?,providerId = ?,isPayment = ? where id = ?";
            paramsList.add(bill.getBillCode());
            paramsList.add(bill.getProductName());
            paramsList.add(bill.getProductUnit());
            paramsList.add(bill.getProductCount());
            paramsList.add(bill.getTotalPrice());
            paramsList.add(bill.getProviderId());
            paramsList.add(bill.getIsPayment());
            paramsList.add(bill.getId());
            Object[] params = paramsList.toArray();
            try {
                count = BaseDao.executeUpdate(connection, sql, params, preparedStatement);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseDao.freeResource(null,preparedStatement,null);
            }
        }

        return count;
    }

    @Test
    public void Test(){
        List<Bill> providerList = getBillList(BaseDao.getConnection(), "", 0,0);
        for (Bill bill : providerList) {
            System.out.println(bill.getProductName());
        }
    }
}
