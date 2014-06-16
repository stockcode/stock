package cn.nit.stock;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;


/**
 * Hello world!
 *
 */
public class HistoryNotLimit 
{
    public static void main( String[] args ) throws Exception
    {            	
    	 // Create an instance of HttpClient.
        HttpClient client = new HttpClient();
        //client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
  
        
    	String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";  
    	String   driver= "net.sourceforge.jtds.jdbc.Driver";  
    	java.sql.Connection conn;
    	
		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement st = conn.createStatement();
		Statement st1 = conn.createStatement();
		Statement updateStatement = conn.createStatement();
		
    	String sql = "select * from stock";

    	Integer index = 1;
    	ResultSet resultSet = st.executeQuery(sql);
    	while (resultSet.next()) {
    		String pkid = resultSet.getString("pkid");
    		String stockcode = resultSet.getString("code");
    		String name = resultSet.getString("name");
    		
    		sql = "select * from day where stockcode='" + stockcode + "' order by tradedate desc";
            ResultSet rSet = st1.executeQuery(sql);
            
            while (rSet.next()) {
				Double yprice = rSet.getDouble("yesterdayprice");
				if (yprice.intValue() == 0) continue;
				
				Double closePrice = rSet.getDouble("closeprice");
				Double openPrice = rSet.getDouble("openprice");
				Double lowPrice = rSet.getDouble("lowprice");
				Double highPrice =rSet.getDouble("highprice");
				
				Date tradedate = rSet.getDate("tradedate");
    		
    		
              if ((yprice + yprice*0.1 - highPrice < 0.01) && (highPrice -closePrice > 0.01)) {            	  
            	  System.err.println("代码：" + stockcode + ",名称：" + name + ",涨停日期：" + tradedate);
            	  StringBuilder sb = new StringBuilder();
                  sb.append("insert into stocknotlimit(stockname, stockcode, yesterdayprice, openprice, lowprice, highprice, closeprice, limitdate) values('"+name);
                  sb.append("','" + stockcode);
                  sb.append("'," + yprice);
                  sb.append("," + openPrice);
                  sb.append("," + lowPrice);
                  sb.append("," + highPrice);
                  sb.append("," + closePrice);
                  sb.append(",'" + tradedate + "')");
                  
                  sql = sb.toString();
          	    System.err.println(sql);
          	    updateStatement.execute(sql);   
              }
              
            }    		
    	}
    	st.close();
    	st1.close();
    	conn.close();
    	    	
    }
}
