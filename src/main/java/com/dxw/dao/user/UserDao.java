package com.dxw.dao.user;

import com.dxw.pojo.User;
import java.sql.Connection;
import java.util.List;

public interface UserDao {
    //得到登录的用户
    User getLoginUser(Connection connection,String userCode);
    //修改当前用户密码
    int updatePwd(Connection connection,int id ,String password);
    //根据用户名或者角色查询用户总数
    int getUserCount(Connection connection,String userName,int userRole);
    //获取用户列表
    List<User> getUserList(Connection connection,String userName,int userRole,int currentPageNo,int pageSize);
    //添加新用户
    int addUser(Connection connection,User user);
    //查询单个用户的详细信息
    User queryUserInfo(Connection connection,int id);
    //删除单个用户
    int deleteUser(Connection connection,int id);
    //修改用户信息
    int modifyUserInfo(Connection connection,User user);
}
