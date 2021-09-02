package com.dxw.service.provider;

import com.dxw.dao.BaseDao;
import com.dxw.dao.provider.ProviderDao;
import com.dxw.dao.provider.ProviderDaoImpl;
import com.dxw.pojo.Provider;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProviderServiceImpl implements ProviderService{
    private ProviderDao providerDao;

    public ProviderServiceImpl(){
        providerDao = new ProviderDaoImpl();
    }

    @Override
    public List<Provider> getProList(String proCode, String proName) {
        Connection connection = BaseDao.getConnection();

        List<Provider> providerList = providerDao.getProviderList(connection, proCode, proName);

        BaseDao.freeResource(connection,null ,null);

        return providerList;
    }

    @Override
    public boolean addNewProvider(Provider provider) {
        boolean flag = false;
        Connection connection = BaseDao.getConnection();
        try {
            connection.setAutoCommit(false);
            int i = providerDao.addProvider(connection, provider);
            if (i>0){
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
    public int deleteProvider(int id) {
        int count = 0;
        //   count          query             0              -1
        //    标记      有订单,不能删除  没有订单，删除失败  没有订单，删除成功
        Connection connection = BaseDao.getConnection();
        try {
            connection.setAutoCommit(false);
            int query = providerDao.queryProviderBill(connection, id);
            if (query<1){//没有订单
                //进行删除操作
                int delete = providerDao.deleteProvider(connection,id);
                if (delete>0){
                    count = -1;//删除成功
                }else {
                    count = 0;//删除失败
                }
            }else {//有订单
                count = query;//不能删除,返回订单数量
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                connection.commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return count;
    }

    @Override
    public boolean updateProviderInfo(Provider provider) {
        boolean flag = false;
        Connection connection = BaseDao.getConnection();
        try {
            connection.setAutoCommit(false);
            int i = providerDao.modifyProviderInfo(connection, provider);
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

    @Test
    public void Test(){
        List<Provider> providerList = getProList("", "");
        for (Provider provider : providerList) {
            System.out.println(provider.getProName());
        }
    }
}
