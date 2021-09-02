package com.dxw.dao.role;

import com.dxw.dao.BaseDao;
import com.dxw.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RoleDaoImpl implements RoleDao{
    @Override
    public List<Role> getRoleList(Connection connection) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = null;
        Role role = null;
        Object[] params = {};
        //存放角色数据
        ArrayList<Role> roles = new ArrayList<>();

        if (connection!=null){
            sql = "select * from smbms_role" ;
            try {
                resultSet = BaseDao.executeQuery(connection, sql, params, resultSet, preparedStatement);
                while (resultSet.next()){
                    role = new Role();
                    role.setId(resultSet.getInt("id"));
                    role.setRoleCode(resultSet.getString("roleCode"));
                    role.setRoleName(resultSet.getString("roleName"));
                    roles.add(role);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseDao.freeResource(null,preparedStatement,resultSet);
            }
        }

        return roles;
    }

    @Test
    public void Test(){
        List<Role> roleList = getRoleList(BaseDao.getConnection());
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }
}
