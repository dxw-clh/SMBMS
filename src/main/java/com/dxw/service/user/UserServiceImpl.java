package com.dxw.service.user;

import com.dxw.dao.BaseDao;
import com.dxw.dao.user.UserDao;
import com.dxw.dao.user.UserDaoImpl;
import com.dxw.pojo.User;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImpl implements UserService{
    //业务层调用Dao层
    //一般换做私有对象
    private UserDao userDao;

    //无参构造初始化生成这个对象，这也是为了减少代码量
    public UserServiceImpl( ){
        this.userDao = new UserDaoImpl();
    }

    @Override
    public User login(String userCode, String password) {
        //登录业务调用Dao层，Dao层操作数据库
        //获得连接对象（数据库对象）
        //在业务层创建连接
        Connection connection = BaseDao.getConnection();
        User loginUser = userDao.getLoginUser(connection, userCode);

        //释放资源
        BaseDao.freeResource(connection,null,null);

        return loginUser;
    }

    @Override
    public boolean modifyPassword(int id, String password) {
        Connection connection = BaseDao.getConnection();
        boolean flag = false;

        //调用Dao层
        int i = userDao.updatePwd(connection,id,password);

        if (i>0){
            flag = true;
        }

        //释放资源
        BaseDao.freeResource(connection,null,null);

        return flag;
    }

    @Override
    public int getUserCount(String userName, int userRole) {
        Connection connection = BaseDao.getConnection();
        //调用Dao获得记录数
        int userCount = userDao.getUserCount(connection, userName, userRole);

        //关闭资源
        BaseDao.freeResource(connection,null,null);

        return userCount;
    }

    @Override
    public List<User> getUserList(String userName,int userRole,int currentPageNo,int pageSize) {
        Connection connection = BaseDao.getConnection();
        //调用Dao层
        List<User> userList = userDao.getUserList(connection, userName, userRole, currentPageNo, pageSize);
        //关闭资源
        BaseDao.freeResource(connection,null,null);

        return userList;
    }

    @Override
    public boolean addNewUser(User user) {
        boolean flag = false;
        Connection connection = BaseDao.getConnection();
        //开启事务
        try {
            connection.setAutoCommit(false);
            int i = userDao.addUser(connection, user);
            if (i>0){
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            //如果发生了异常，事务回滚
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }finally {
            //提交事务
            try {
                connection.commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //释放资源
            BaseDao.freeResource(connection,null,null);
        }

        return flag;
    }

    @Override
    public User queryUserInfo(int id) {
        Connection connection = BaseDao.getConnection();
        User user = userDao.queryUserInfo(connection, id);
        BaseDao.freeResource(connection,null,null);

        return user;
    }

    //事务，需要保证原子性，要么成功要么失败
    @Override
    public boolean deleteUserInfo(int id) {
        boolean flag = false;
        Connection connection = BaseDao.getConnection();
        try {
            //开启事务
            connection.setAutoCommit(false);
            int i = userDao.deleteUser(connection,id);
            if (i>0){
                flag = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                //抛出异常，事务失败，事务回滚
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                //提交事务
                connection.commit();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return flag;
    }

    @Override
    public boolean updateUserInfo(User user) {
        boolean flag = false;
        Connection connection = BaseDao.getConnection();
        try {
            connection.setAutoCommit(false);
            int i = userDao.modifyUserInfo(connection, user);
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
        List<User> admin = getUserList("", 0,0,0);
        for (User user : admin) {
            System.out.println(user.getUserName());
        }
    }
}
