package com.dxw.service.provider;

import com.dxw.pojo.Provider;

import java.util.List;

public interface ProviderService {

    //查询全部的供应商
    List<Provider> getProList(String proCode,String proName);
    //添加新的供应商
    boolean addNewProvider(Provider provider);
    //根据条件实现删除供应商的业务
    int deleteProvider(int id);
    //修改供应商信息
    boolean updateProviderInfo(Provider provider);

}
