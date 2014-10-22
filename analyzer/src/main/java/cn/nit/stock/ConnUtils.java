package cn.nit.stock;


import cn.nit.stock.model.StockName;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.net.UnknownHostException;
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

    public static Datastore getDatastore() throws UnknownHostException {
        ResourceBundle bundle = ResourceBundle.getBundle("db");
        String url = bundle.getString("mongodbIP");
        MongoClient mongoClient = new MongoClient(url);

        Morphia morphia = new Morphia();
        morphia.map(StockName.class);
        return morphia.createDatastore(mongoClient, "stock");
    }

    public static MongoClient getMongo() throws UnknownHostException {
        ResourceBundle bundle = ResourceBundle.getBundle("db");
        String url = bundle.getString("mongodbIP");
        return new MongoClient(url);
    }
}
