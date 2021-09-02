package com.dxw.dao.user;

import com.dxw.dao.BaseDao;
import com.dxw.pojo.User;
import com.mysql.jdbc.StringUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserDaoImpl implements UserDao{
    //用户登录
    @Override
    public User getLoginUser(Connection connection, String userCode) {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        //sql语句
        String sql = "select * from smbms_user where userCode=?";
        Object[] params = {userCode};
        User user = null;
        if(connection!=null){
            try {
                //在Dao层创建返回集对象
                resultSet = BaseDao.executeQuery(connection, sql, params, resultSet, preparedStatement);
                while (resultSet.next()){
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUserCode(resultSet.getString("userCode"));
                    user.setUserName(resultSet.getString("userName"));
                    user.setUserPassword(resultSet.getString("userPassword"));
                    user.setGender(resultSet.getInt("gender"));
                    user.setBirthday(resultSet.getDate("birthday"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setAddress(resultSet.getString("address"));
                    user.setUserRole(resultSet.getInt("userRole"));
                    user.setCreatedBy(resultSet.getInt("createdBy"));
                    user.setCreationDate(resultSet.getTimestamp("creationDate"));
                    user.setModifyBy(resultSet.getInt("modifyBy"));
                    user.setModifyDate(resultSet.getTimestamp("modifyDate"));
                }
                //释放资源
                BaseDao.freeResource(null,preparedStatement,resultSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    //更新密码
    @Override
    public int updatePwd(Connection connection, int id, String password) {
        PreparedStatement preparedStatement = null;
        String sql = "update smbms_user set userPassword = ? where id = ?";
        Object[] params = {password,id};
        int i = 0;
        if (connection!=null){
            try {
                i = BaseDao.executeUpdate(connection, sql, params, preparedStatement);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //释放资源
                BaseDao.freeResource(null,preparedStatement,null);
            }
        }

        return i;
    }

    //根据用户名或者角色查询用户总数
    @Override
    public int getUserCount(Connection connection, String userName, int userRole) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = 0;

        //确保建立的连接
        if (connection!=null){
            //威力拼接sql语句，使用StringBuffer
            StringBuffer sql = new StringBuffer();
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole = r.id");

            //创建一个集合存放参数(参数类型有很多，使用Object)
            ArrayList<Object> paramsList = new ArrayList<>();

            //这里需要拼接aql语句
            if (!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName LIKE ?");
                paramsList.add("%"+userName+"%");//模糊查询需要%
            }
            if (userRole>0){
                sql.append(" and r.id = ?");
                paramsList.add(userRole);
            }

            //把集合转变为数组
            Object[] params = paramsList.toArray();

            try {
                //获得结果集
                resultSet = BaseDao.executeQuery(connection, sql.toString(), params, resultSet, preparedStatement);
                if (resultSet.next()){
                    //获得结果集中的数量
                    count = resultSet.getInt("count");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //关闭资源
                BaseDao.freeResource(null,preparedStatement,resultSet);
            }
        }

        return count;
    }

    //获取用户列表
    @Override
    public List<User> getUserList(Connection connection, String userName, int userRole, int currentPageNo, int pageSize) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        //接收用户列表
        ArrayList<User> users = new ArrayList<>();
        if (connection!=null){
            StringBuffer sql = new StringBuffer();
            //查询所有用户
            sql.append("select u.*,r.roleName from smbms_user u,smbms_role r where u.userRole = r.id");
            //存放需要传递的参数
            ArrayList<Object> paramList = new ArrayList<>();
            //拼接sql语句，存入参数
            if (!StringUtils.isNullOrEmpty(userName)){
                sql.append(" and u.userName LIKE ?");
                paramList.add("%"+userName+"%");
            }
            if (userRole>0){
                sql.append(" and r.id = ?");
                //sql.append(" and u.userRole = ?");
                paramList.add(userRole);
            }

            //在数据库中分页使用limit,它的两个参数为    startIndex pageSize：开始的脚标和页面大小
            //当前端传递过来的currentPageNo为1时，需要0到4五个数据，为2时，需要5到9五个数据，以此类推
            //得数据库需要传递的参数：开始的脚标startIndex = (currentPageNo-1) * pageSize
            //这个查询语句是从开始的脚标查询数量为页面大小    假设页面大小为5
            //页面数   数据库查询需要查询出的数据范围     开始的脚标
            //  1               0——4                    0
            //  2               5——9                    5
            //  3              10——                     10
            //开始脚标
            int startIndex = 0;

            //如果页面大小为0，当前页面为0，M那么不进行分页，查询所有的数据
            if (currentPageNo>0 && pageSize>0) {
                sql.append(" order by creationDate DESC limit ?,?");
                startIndex = (currentPageNo - 1) * pageSize;
                paramList.add(startIndex);
                paramList.add(pageSize);
            }

            //把集合转换为数组
            Object[] params = paramList.toArray();
            //获得结果集
            try {
                resultSet = BaseDao.executeQuery(connection, sql.toString(), params, resultSet, preparedStatement);
                while (resultSet.next()){
                    //获得结果集中的数量
                    user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setUserCode(resultSet.getString("userCode"));
                    user.setUserName(resultSet.getString("userName"));
                    user.setGender(resultSet.getInt("gender"));
                    user.setBirthday(resultSet.getDate("birthday"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setUserRole(resultSet.getInt("userRole"));
                    user.setUserRoleName(resultSet.getString("roleName"));
                    //把查到的用户存在集合中
                    users.add(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                BaseDao.freeResource(null,preparedStatement,resultSet);
            }

        }

        //返回用户
        return users;
    }

    @Override
    public int addUser(Connection connection, User user) {
        PreparedStatement preparedStatement = null;
        int count = 0;
        ArrayList<Object> paramsList = new ArrayList<>();
        if (connection!=null){
            String sql = "INSERT INTO smbms_user (" +
                    "userCode,userName,userPassword,gender,birthday,phone,address,userRole) " +
                    "VALUES (?,?,?,?,?,?,?,?)";
            paramsList.add(user.getUserCode());
            paramsList.add(user.getUserName());
            paramsList.add(user.getUserPassword());
            paramsList.add(user.getGender());
            paramsList.add(user.getBirthday());
            paramsList.add(user.getPhone());
            paramsList.add(user.getAddress());
            paramsList.add(user.getUserRole());

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

    //查询单个用户详细信息
    @Override
    public User queryUserInfo(Connection connection, int id) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;

        if (connection!=null){
            if (id>0){
                String sql = "select u.*,r.roleName from smbms_user u,smbms_role r where u.id = ? and u.userRole = r.id";
                Object[] params = {id};
                try {
                    resultSet = BaseDao.executeQuery(connection, sql, params, resultSet, preparedStatement);
                    if (resultSet.next()){
                        user = new User();
                        user.setId(resultSet.getInt("id"));
                        user.setUserCode(resultSet.getString("userCode"));
                        user.setUserName(resultSet.getString("userName"));
                        user.setUserPassword(resultSet.getString("userPassword"));
                        user.setGender(resultSet.getInt("gender"));
                        user.setBirthday(resultSet.getDate("birthday"));
                        user.setPhone(resultSet.getString("phone"));
                        user.setAddress(resultSet.getString("address"));
                        user.setUserRoleName(resultSet.getString("roleName"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    BaseDao.freeResource(null,preparedStatement,resultSet);
                }
            }
        }

        return user;
    }

    @Override
    public int deleteUser(Connection connection, int id) {
        PreparedStatement preparedStatement = null;
        int count = 0;

        if (connection!=null){
            String sql = "delete from smbms_user where id = ?";
            Object[] params = {id};
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

    //修改用户信息
    @Override
    public int modifyUserInfo(Connection connection, User user) {
        PreparedStatement preparedStatement = null;
        ArrayList<Object> paramsList = new ArrayList<>();
        int count = 0;

        if (connection!=null){
            String sql = "update smbms_user set userName = ? ,gender = ?,birthday = ?," +
                    "phone = ?,address = ?,userRole = ? where id = ?";
            //update table_name set column1=value1,column2=value2,... where some_column=some_value;
            paramsList.add(user.getUserName());
            paramsList.add(user.getGender());
            paramsList.add(user.getBirthday());
            paramsList.add(user.getPhone());
            paramsList.add(user.getAddress());
            paramsList.add(user.getUserRole());
            paramsList.add(user.getId());

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
        int count = deleteUser(BaseDao.getConnection(), 19);
        System.out.println(count);
    }
}
