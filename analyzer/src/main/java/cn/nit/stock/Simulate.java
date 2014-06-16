package cn.nit.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.mail.SendFailedException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import cn.nit.stock.model.PatternOne;
import cn.nit.stock.model.PatternTwo;
import cn.nit.stock.model.TradeDay;

/**
 * Hello world!
 * 
 */
public class Simulate {
	public static void main(String[] args) throws Exception {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.## ");
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");

		String url = "jdbc:mysql://127.0.0.1:3306/stock?useUnicode=true&characterEncoding=UTF-8&mysqlEncoding=UTF-8";
		String driver = "com.mysql.jdbc.Driver";

		java.sql.Connection conn;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, "root", "aoxun");
		Connection conn1 = DriverManager.getConnection(url, "root", "aoxun");
		Connection conn2 = DriverManager.getConnection(url, "root", "aoxun");
		Statement st = conn.createStatement();
		Statement stStock = conn.createStatement();
		Statement updateStatement = conn2.createStatement();
		Statement selectStatement = conn1.createStatement();

		String stockSql = "select * from stock";

		ResultSet rsStock = stStock.executeQuery(stockSql);
		Date startTrade = dataFormat.parse("2010-01-01");

		while (rsStock.next()) {
			String stockCode = rsStock.getString("code");
			// stockCode = "002192";
			System.err.println("分析" + stockCode + ",");
			String sql = "select * from day where stockcode='" + stockCode
					+ "' and tradedate>'2011-1-1' order by tradedate";

			ResultSet resultSet = st.executeQuery(sql);

			List<TradeDay> tradeList = new ArrayList<TradeDay>();

			while (resultSet.next()) {
				TradeDay tradeDay = new TradeDay(
						resultSet.getDouble("yesterdayprice"),
						resultSet.getDouble("openprice"),
						resultSet.getDouble("closeprice"),
						resultSet.getDouble("highprice"),
						resultSet.getDouble("lowprice"),
						resultSet.getString("stockcode"),
						resultSet.getString("tradedate"));
				tradeList.add(tradeDay);
			}
			for (int i = 0; i < tradeList.size();) {
				TradeDay first = tradeList.get(i++);
				if (i == tradeList.size()) break;
				TradeDay second = tradeList.get(i++);
				
				if (i == tradeList.size()) break;
				TradeDay third = tradeList.get(i++);
				
				PatternTwo patternTwo = new PatternTwo(first, second, third); 
				
				if (patternTwo.isMatch()) {
					System.err.println(patternTwo);
				}
			}
		}

		st.close();
		stStock.close();
		selectStatement.close();
		conn.close();
		conn1.close();
		conn2.close();

	}
}
