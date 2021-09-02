package com.dxw.service.user;

import com.dxw.pojo.User;

import java.util.List;

public interface UserService {
    //登录业务(用户会输入用户名和密码)，得到返回值，具体的登录的对象
    User login(String userCode,String password);

    //修改密码业务
    boolean modifyPassword(int id,String password);

    //查询记录数量
    int getUserCount(String userName,int userRole);

    //查询用户列表
    List<User> getUserList(String userName,int userRole,int currentPageNo,int pageSize);

    //添加新用户
    boolean addNewUser(User user);

    //查询单个用户详细信息
    User queryUserInfo(int id);

    //删除单个用户信息
    boolean deleteUserInfo(int id);

    //修该用户信息
    boolean updateUserInfo(User user);

}
