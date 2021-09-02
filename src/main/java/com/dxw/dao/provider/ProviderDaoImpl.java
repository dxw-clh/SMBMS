package com.dxw.dao.provider;

import com.dxw.dao.BaseDao;
import com.dxw.pojo.Provider;
import com.mysql.jdbc.StringUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProviderDaoImpl implements ProviderDao{
    @Override
    public List<Provider> getProviderList(Connection connection, String proCode, String proName) {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        ArrayList<Object> paramsList = null;
        ArrayList<Provider> providersList = new ArrayList<>();
        Object[] params = null;
        Provider provider = null;

        if (connection!=null){
            StringBuffer sql = new StringBuffer();
            sql.append("select * from smbms_provider WHERE 1=1");
            paramsList =new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(proCode)){
                sql.append(" and proCode like ?");
                paramsList.add("%"+proCode+"%");
            }
            if (!StringUtils.isNullOrEmpty(proName)){
                sql.append(" and proName like ?");
                paramsList.add("%"+proName+"%");
            }
            params = paramsList.toArray();

            try {
                resultSet = BaseDao.executeQuery(connection, sql.toString(), params, resultSet, preparedStatement);
                while (resultSet.next()){
                    provider = new Provider();
                    provider.setId(resultSet.getInt("id"));
                    provider.setProCode(resultSet.getString("proCode"));
                    provider.setProName(resultSet.getString("proName"));
                    provider.setProDesc(resultSet.getString("proDesc"));
                    provider.setProContact(resultSet.getString("proContact"));
                    provider.setProPhone(resultSet.getString("proPhone"));
                    provider.setProAddress(resultSet.getString("proAddress"));
                    provider.setProFax(resultSet.getString("proFax"));
                    provider.setCreatedBy(resultSet.getInt("createdBy"));
                    provider.setCreationDate(resultSet.getDate("creationDate"));
                    provider.setModifyBy(resultSet.getInt("modifyBy"));
                    provider.setModifyDate(resultSet.getDate("modifyDate"));

                    provider.setProviderName(resultSet.getString("proName"));

                    providersList.add(provider);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseDao.freeResource(null,preparedStatement,resultSet);
            }
        }

        return providersList;
    }

    @Override
    public int addProvider(Connection connection,Provider provider) {
        PreparedStatement preparedStatement = null;
        ArrayList<Object> paramsList = new ArrayList<>();
        int count = 0;

        if (connection!=null){
            String sql = "insert smbms_provider " +
                    "(proCode,ProName,proDesc,proContact,proPhone,proAddress,proFax) " +
                    "values(?,?,?,?,?,?,?)";
            //保存需要的参数
            paramsList.add(provider.getProCode());
            paramsList.add(provider.getProName());
            paramsList.add(provider.getProDesc());
            paramsList.add(provider.getProContact());
            paramsList.add(provider.getProPhone());
            paramsList.add(provider.getProAddress());
            paramsList.add(provider.getProFax());

            Object[] params = paramsList.toArray();

            try {
                count = BaseDao.executeUpdate(connection, sql, params, preparedStatement);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                BaseDao.freeResource(null,preparedStatement,null);
            }
        }

        return count;
    }

    //查询供应商是否存在订单
    @Override
    public int queryProviderBill(Connection connection, int id) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = 0;
        if (connection!=null){
            String sql = "select count(1) as count from smbms_provider p,smbms_bill b where p.id = ? and p.id = b.providerId";
            Object[] param = {id};
            try {
                resultSet = BaseDao.executeQuery(connection,sql,param,resultSet,preparedStatement);
                if (resultSet.next()){
                    count = resultSet.getInt("count");//如果上面没有用as count别名，那么就要用count(1)
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseDao.freeResource(null,preparedStatement,resultSet);
            }
        }

        return count;
    }

    //根据id删除供应商
    @Override
    public int deleteProvider(Connection connection, int id) {
        PreparedStatement preparedStatement = null;
        int count = 0;
        if (connection!=null){
            String sql = "delete from smbms_provider where id = ?";
            Object[] param = {id};
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
    public int modifyProviderInfo(Connection connection, Provider provider) {
        PreparedStatement preparedStatement = null;
        ArrayList<Object> paramsList = new ArrayList<>();
        int count = 0;

        if (connection!=null){
            String sql = "update smbms_provider set proCode = ? ,proName = ?,proContact = ?," +
                    "proPhone = ?,proAddress = ?,proFax = ?,proDesc = ? where id = ?";
            //update table_name set column1=value1,column2=value2,... where some_column=some_value;
            paramsList.add(provider.getProCode());
            paramsList.add(provider.getProName());
            paramsList.add(provider.getProContact());
            paramsList.add(provider.getProPhone());
            paramsList.add(provider.getProAddress());
            paramsList.add(provider.getProFax());
            paramsList.add(provider.getProDesc());
            paramsList.add(provider.getId());

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
        int count = queryProviderBill(BaseDao.getConnection(), 14);
        System.out.println(count);
    }
}
