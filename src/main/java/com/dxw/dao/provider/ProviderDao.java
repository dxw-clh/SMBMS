package com.dxw.dao.provider;

import com.dxw.pojo.Provider;

import java.sql.Connection;
import java.util.List;

public interface ProviderDao {
    //查询供应商列表
    List<Provider> getProviderList(Connection connection,String proCode,String proName);
    //添加供应商
    int addProvider(Connection connection,Provider provider);
    //查询供应商是否有订单，有几个订单
    int queryProviderBill(Connection connection,int id);
    //删除供应商
    int deleteProvider(Connection connection,int id);
    //修改供应商信息
    int modifyProviderInfo(Connection connection,Provider provider);
}
