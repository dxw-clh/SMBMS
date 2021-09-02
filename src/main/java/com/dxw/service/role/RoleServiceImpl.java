package com.dxw.service.role;

import com.dxw.dao.BaseDao;
import com.dxw.dao.role.RoleDao;
import com.dxw.dao.role.RoleDaoImpl;
import com.dxw.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class RoleServiceImpl implements RoleService{
    private RoleDao roleDao;

    public RoleServiceImpl(){
        this.roleDao = new RoleDaoImpl();
    }

    @Override
    public List<Role> getRoleList() {
        Connection connection = BaseDao.getConnection();
        List<Role> roleList = roleDao.getRoleList(connection);
        BaseDao.freeResource(connection,null,null);

        return roleList;
    }

    @Test
    public void Test(){
        List<Role> roleList = getRoleList();
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }
}

