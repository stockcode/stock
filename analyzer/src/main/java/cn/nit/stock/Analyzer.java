package cn.nit.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


/**
 * Hello world!
 *
 */
public class Analyzer
{
	static String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
	static String driver= "net.sourceforge.jtds.jdbc.Driver";
	
    public static void main( String[] args ) throws Exception
    {
    	analyseBuy();    	
    	//analyseSell();
    	
    }

	private static void analyseSell() throws ClassNotFoundException,
	SQLException {
		Connection selectConn, insertConn;

		Class.forName(driver);
		selectConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		insertConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");		
		Statement selectStatement = selectConn.createStatement();
		Statement lineStatement = selectConn.createStatement();
		Statement insertStatement = insertConn.createStatement();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	Date tradedate = new Date();    	
    	String chooseDate = df.format(tradedate);
    	
    	String sql = "select * from stockaccount where tradedate='" + chooseDate + "'";
    	ResultSet resultSet = selectStatement.executeQuery(sql);
    	
    	Calendar calendar = Calendar.getInstance();    		
		
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
			calendar.add(Calendar.DAY_OF_WEEK, 3);
		} else {
			calendar.add(Calendar.DAY_OF_WEEK, 1);	
		}
		
    	while (resultSet.next()) {
    		Double avg = 0d;
    		String stockCode = resultSet.getString("stockcode");
    		Double currentPrice = resultSet.getDouble("currentPrice");
    		Double plPercent = resultSet.getDouble("plpercent");
    		Integer stockUsable = resultSet.getInt("stockremain");
    		
    		
    		String ssql = "select top 99 * from day where stockcode='" + stockCode + "' and tradedate<='" + chooseDate + "' order by tradedate desc";
    		ResultSet rs = lineStatement.executeQuery(ssql);
            while (rs.next()) {
            	avg += rs.getDouble("closeprice");
            }
            avg = avg / 99;
            
            if (avg > currentPrice) {
            	avg -= avg * 0.005;
            	StringBuilder sb = new StringBuilder();
                sb.append("insert ROBOT(operation, Code, Price, Amount, priority, tradedate) values('SELL', ");
                sb.append("'" + stockCode + "', ");
                sb.append(avg + ",");
                sb.append(stockUsable + ",0,");
                sb.append("'" + df.format(calendar.getTime()) + "')");            
                ssql = sb.toString();
                System.err.println(ssql);
                insertStatement.execute(ssql);
            }
    	}
	}

	private static void analyseBuy() throws ClassNotFoundException,
			SQLException {
		
    	Connection selectConn, insertConn;

		Class.forName(driver);
		selectConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		insertConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");		
		Statement selectStatement = selectConn.createStatement();
		Statement lineStatement = selectConn.createStatement();
		Statement insertStatement = insertConn.createStatement();

    	String sql = "select * from stock";

    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	
    	Date tradedate = new Date();
    	
    	String chooseDate = "2010-09-01"; //df.format(tradedate);
    	
    	ResultSet resultSet = selectStatement.executeQuery(sql);
    	
    	while (resultSet.next()) {
    		Double close=0d,pre=0d,preavg=0d,avg=0d, preavg5 = 0d, avg5 = 0d;
    		int j = 0;
    		
    		String name = resultSet.getString("name");
    		if (name == null || name.toUpperCase().contains("ST")) continue;
    		
    		String pkid = resultSet.getString("pkid");
    		String stockCode = resultSet.getString("code");
            String stockType = resultSet.getString("type");
            SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
            String ssql = "select top 4 * from day where stockcode='" + stockCode + "' and tradedate<='" + chooseDate + "' order by tradedate desc";            
            //System.err.println(ssql);
            ResultSet rs = lineStatement.executeQuery(ssql);
            if (rs.next()) {
            	close = rs.getDouble("closeprice");
            	pre = rs.getDouble("yesterdayprice");
            	String tradeDate = rs.getString("tradedate");
            	if (!tradeDate.startsWith(chooseDate)) continue;
            	//System.err.println("分析"+ stockCode);
            	Double lastPrice = 0d;
            	
            	while (rs.next()) {
            		lastPrice = rs.getDouble("closeprice");
            		
//            		if (j < 5) {
//            			preavg5 += lastPrice;
//            		}
//            		
//            		if (j < 4) {
//            			avg5 += lastPrice;
//            		}
//            		
            		preavg += lastPrice;
            		j++;
                }
            	
            	avg5 += close;
            	//if (preavg5 > avg5) continue; //5日均线向下忽略
            	
            	avg = close + preavg - lastPrice;            	
            	avg = avg / 3;
            	preavg = preavg / 3;
            }
                       
            
            //if (preavg > avg) continue; //均线向下忽略
            
            if ((pre <= preavg) && (close > avg)) //符合条件
            {
            	StringBuilder sb = new StringBuilder();
            	sb.append("收盘价="+close);
            	sb.append(",昨日收盘价=" + pre);
            	sb.append(",平均价=" + avg);
            	sb.append(",昨日平均价=" + preavg);
            	System.err.println(sb.toString());
                System.err.println("stockcode=" + stockCode);

                sb = new StringBuilder();
                sb.append("insert BAOHUA(ChooseType, StockCode, Price, Created) values(2,");
                sb.append("'" + stockCode + "', ");
                sb.append(avg + ",");
                sb.append("'" + chooseDate + "')");
                ssql = sb.toString();
                System.err.println(ssql);
                //insertStatement.execute(ssql);
            }            
    	}
    	
//    	sql = "select * from baohua where created = '" + chooseDate + "'";
//
//    	
//    	resultSet = selectStatement.executeQuery(sql);
//    	while (resultSet.next()) {
//    		Double price = resultSet.getDouble("price");
//    		String stockCode = resultSet.getString("stockcode");
//    		
//    		price += price * 0.005;
//    		if (price > 25) continue;
//    		
//    		int amount = 100; //1手
//    		while (price * amount < 2500) {
//    			amount += 100;
//    		}
//    		
//    		Calendar calendar = Calendar.getInstance();    		
//    		
//    		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
//    			calendar.add(Calendar.DAY_OF_WEEK, 3);
//    		} else {
//    			calendar.add(Calendar.DAY_OF_WEEK, 1);	
//    		}
//    		
//    		StringBuilder sb = new StringBuilder();
//            sb.append("insert ROBOT(operation, Code, Price, Amount, priority, tradedate) values('BUY', ");
//            sb.append("'" + stockCode + "', ");
//            sb.append(price + ",");
//            sb.append(amount + ",0,");
//            sb.append("'" + df.format(calendar.getTime()) + "')");            
//            String ssql = sb.toString();
//            System.err.println(ssql);
//            insertStatement.execute(ssql);
//    	}
    	lineStatement.close();
    	selectStatement.close();
    	insertStatement.close();
    	selectConn.close();
    	insertConn.close();
	}
}
