package com.dxw.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

//操作数据库的公共类
public class BaseDao {
    //创建属性，和db.properties文件的值一一对应
    private static String driver;
    private static String url;
    private static String username;
    private static String password;

    //给属性赋初始值
    static {
//        //获得流的方法1：
//        FileInputStream inputStream = null;
//        //获取流，拿到db.properties文件
//        try {
//            inputStream = new FileInputStream("D:\\java\\SMBMS\\src\\main\\resources\\db.properties");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        //获得流的方法2：
        InputStream inputStream = BaseDao.class.getClassLoader().getResourceAsStream("db.properties");

        //创建Properties集合
        Properties properties = new Properties();
        //利用Properties集合加载流中的对象
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取Properties对象里面的值，并且赋值给本类的属性进行初始化
        driver = properties.getProperty("driver");
        url = properties.getProperty("url");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }


    //获取数据库连接对象（可以说是数据库对象）
    public static Connection getConnection(){
        Connection connection = null;
        try {
            //1.加载驱动
            Class.forName(driver);
            //2.建立连接
            try {
                connection = DriverManager.getConnection(url, username, password);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //编写查询公共类
/*    public static ResultSet executeQuery(String sql){
        Connection connection = getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        return resultSet;
    }
}*/
    public static ResultSet executeQuery(Connection connection,String sql,Object[] params,
                             ResultSet resultSet,PreparedStatement preparedStatement)throws Exception{
        //预编译已经传递了sql语句，后面的实际查询或者更新方法就不需要传递sql参数了
        //操作数据库的对象
        preparedStatement =  connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    //编写增删改的公共方法
    /*public static int executeUpdate(String sql){
        Connection connection = getConnection();
        Statement statement = null;
        int i = 0;
        try {
            statement = connection.createStatement();
            i = statement.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return i;
    }*/
    public static int executeUpdate(Connection connection,String sql,Object[] params,
                                         PreparedStatement preparedStatement)throws Exception{
        preparedStatement =  connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i+1,params[i]);
        }
        int i = preparedStatement.executeUpdate();

        return i;
    }
    //关闭资源
    public static boolean freeResource
                (Connection connection,PreparedStatement preparedStatement,ResultSet resultSet){
        boolean flag = true;

        if (resultSet!=null){
            try {
                resultSet.close();
                //GC回收
                resultSet = null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }
        if (preparedStatement!=null){
            try {
                preparedStatement.close();
                //GC回收
                preparedStatement = null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }
        if (connection!=null){
            try {
                connection.close();
                //GC回收
                connection = null;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                flag = false;
            }
        }

        return flag;
    }
}