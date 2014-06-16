package cn.nit.stock;


import java.sql.*;
import java.util.ResourceBundle;

public class ConnUtils
{

 public ConnUtils()
 {
 }

 public static Connection getConn()
     throws ClassNotFoundException, SQLException
 {
     ResourceBundle bundle = ResourceBundle.getBundle("db");
     String url = bundle.getString("url");
     String driver = bundle.getString("driver");
     Class.forName(driver);
     Connection conn = DriverManager.getConnection(url, bundle.getString("user"), bundle.getString("passwd"));
     return conn;
 }

 public static Connection getConn(String dbName)
     throws ClassNotFoundException, SQLException
 {
     Connection conn = getConn();
     PreparedStatement statement = conn.prepareStatement((new StringBuilder("use ")).append(dbName).toString());
     statement.execute();
     return conn;
 }
}
