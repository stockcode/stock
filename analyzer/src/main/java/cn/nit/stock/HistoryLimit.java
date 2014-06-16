package cn.nit.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;


/**
 * Hello world!
 *
 */
public class HistoryLimit 
{
    public static void main( String[] args ) throws Exception
    {            	    		
    	Connection conn1 = ConnUtils.getConn("stock");
    	Statement statement = conn1.createStatement();
    	
    	Connection conn = ConnUtils.getConn();
    	
    	
		Statement st = conn.createStatement();
		Statement st1 = conn.createStatement();
		
		Statement updateStatement = conn.createStatement();
		String sql = "select * from stock";
		ResultSet resultSet = st.executeQuery(sql);
		
		while (resultSet.next()) {		
			String stockcode = resultSet.getString("code");
			String name = resultSet.getString("name");
			System.err.println(stockcode);
			AddStock.createDatabase(stockcode);
			String type = "sznse";
			if (stockcode.startsWith("6"))
				type = "shase";
			
			PreparedStatement pstmt = conn.prepareStatement((new StringBuilder(
					"use A")).append(stockcode).toString());
			pstmt.execute();
			
    		
    		
    		sql = "select * from day where stockcode='" + stockcode + "' and tradedate >'2012-10-1' ";
            ResultSet rSet = st1.executeQuery(sql);
            
            while (rSet.next()) {
				Double yprice = rSet.getDouble("yesterday");
				if (yprice.intValue() == 0) continue;
				
				Double closePrice = rSet.getDouble("close");
				Double openPrice = rSet.getDouble("open");
				Double lowPrice = rSet.getDouble("low");
				Double highPrice =rSet.getDouble("high");
				
				Date tradedate = rSet.getDate("tradedate");
    		
    		
              if (yprice + yprice*0.1 - closePrice < 0.01) {            	  
            	  System.err.println("代码：" + stockcode + ",名称：" + name + ",涨停日期：" + tradedate);
            	  StringBuilder sb = new StringBuilder();
                  sb.append("insert into stocklimit(stockname, stockcode, yesterdayprice, openprice, lowprice, highprice, closeprice, limitdate) values('"+name);
                  sb.append("','" + stockcode);
                  sb.append("'," + yprice);
                  sb.append("," + openPrice);
                  sb.append("," + lowPrice);
                  sb.append("," + highPrice);
                  sb.append("," + closePrice);
                  sb.append(",'" + tradedate + "')");
                  
                  sql = sb.toString();
          	    System.err.println(sql);
          	    statement.execute(sql);   
              }
              
            }    		
    	}
    	st.close();
    	st1.close();
    	conn.close();
    	    	
    }
}
