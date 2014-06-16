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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class Analyzer2 {
	static String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
	static String driver = "net.sourceforge.jtds.jdbc.Driver";

	public static void main(String[] args) throws Exception {
		analyseBuy();
		// analyseSell();

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

		String sql = "select * from stockaccount where tradedate='"
				+ chooseDate + "'";
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

			String ssql = "select top 99 * from day where stockcode='"
					+ stockCode + "' and tradedate<='" + chooseDate
					+ "' order by tradedate desc";
			ResultSet rs = lineStatement.executeQuery(ssql);
			while (rs.next()) {
				avg += rs.getDouble("closeprice");
			}
			avg = avg / 99;

			if (avg > currentPrice) {
				avg -= avg * 0.005;
				StringBuilder sb = new StringBuilder();
				sb
						.append("insert ROBOT(operation, Code, Price, Amount, priority, tradedate) values('SELL', ");
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
		Statement st = selectConn.createStatement();
		Statement lineStatement = selectConn.createStatement();
		Statement insertStatement = insertConn.createStatement();

		String sql = "select * from stock";

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		Date tradedate = new Date();

		String chooseDate = "2010-10-8"; // df.format(tradedate);

		ResultSet rsStock = selectStatement.executeQuery(sql);

		int count = 1;
		while (rsStock.next()) {
			System.err.println("分析第"+ count++ + "只股票");			

			String name = rsStock.getString("name");			
			if (name == null || name.toUpperCase().contains("ST"))
				continue;

			String pkid = rsStock.getString("pkid");
			String stockCode = rsStock.getString("code");
			String stockType = rsStock.getString("type");
			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd");
			

			sql = "select * from day where stockcode='" + stockCode
					+ "' and tradedate<='"+ chooseDate + "' order by tradedate";

			ResultSet resultSet = st.executeQuery(sql);

			List<String> tradeDateList = new ArrayList<String>();
			ArrayList<Double> priceList = new ArrayList<Double>();

			while (resultSet.next()) {
				String stockcode = resultSet.getString("stockcode");				
				Double closeprice = resultSet.getDouble("closeprice");
				priceList.add(closeprice);
				tradeDateList.add(resultSet.getString("tradedate"));
			}
			if (priceList.size() == 0)
				continue;

			Double[] avgList = new Double[priceList.size()];
			Double[] avg3List = new Double[priceList.size()];
			Double[] avg5List = new Double[priceList.size()];
			Double[] avg21List = new Double[priceList.size()];

			// System.err.println("首个交易日：" + tradeDateList.get(0));

			Double maxPL = 0d;

			Map<Integer, Double[]> avgMap = new HashMap<Integer, Double[]>();

			for (int i = 3; i <= 21; i++) {
				Double[] aList = new Double[priceList.size()];

				for (int j = i; j < priceList.size(); j++) {
					Double currentavg = 0d;
					for (int k = j - i + 1; k <= j; k++) {
						currentavg += priceList.get(k);
					}
					aList[j] = currentavg / i;
				}

				avgMap.put(i, aList);
			}

			avg3List = avgMap.get(3);
			avg5List = avgMap.get(5);
			avg21List = avgMap.get(21);
			
			int i = priceList.size() - 1;
			Double last3avg = avg3List[i - 1];
			Double last5avg = avg5List[i - 1];
			Double last21avg = avg21List[i - 1];

			Double current3avg = avg3List[i];
			Double current5avg = avg5List[i];
			Double current21avg = avg21List[i];
			
			// if (preavg > avg) continue; //均线向下忽略

			if ((last5avg <= last21avg) && (current5avg > (current21avg+current21avg*0.01)) && current5avg > last5avg) // 符合条件
			{
				System.err.println("stockcode=" + stockCode);
				 System.err.print(".昨日收盘价="+priceList.get(i-1) + ",今日收盘价="+priceList.get(i));
				 System.err.print("。昨日21日均线："+last21avg);
				 System.err.println("。今日21日均线："+current21avg);
				

				StringBuilder sb = new StringBuilder();
				sb
						.append("insert BAOHUA(ChooseType, StockCode, Price, Created) values(2,");
				sb.append("'" + stockCode + "', ");
				sb.append(priceList.get(i) + ",");
				sb.append("'" + chooseDate + "')");
				String ssql = sb.toString();
				System.err.println(ssql);
				insertStatement.execute(ssql);
			}		
		}		

		 sql = "select * from baohua where created = '" + chooseDate + "'";
		
		    	
		 ResultSet resultSet = selectStatement.executeQuery(sql);
		 while (resultSet.next()) {
		 Double price = resultSet.getDouble("price");
		 String stockCode = resultSet.getString("stockcode");
		    		
		 price += price * 0.005;
		 if (price > 25) continue;
		    		
		 int amount = 100; //1手
		 while (price * amount < 2500) {
		 amount += 100;
		 }
		    		
		 Calendar calendar = Calendar.getInstance();
		    		
		 if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
		 calendar.add(Calendar.DAY_OF_WEEK, 3);
		 } else {
		 calendar.add(Calendar.DAY_OF_WEEK, 1);
		 }
		    		
		 StringBuilder sb = new StringBuilder();
		 sb.append("insert ROBOT(operation, Code, Price, Amount, priority, tradedate) values('BUY', ");
		 sb.append("'" + stockCode + "', ");
		 sb.append(price + ",");
		 sb.append(amount + ",0,");
		 sb.append("'" + df.format(calendar.getTime()) + "')");
		 String ssql = sb.toString();
		 System.err.println(ssql);
		 insertStatement.execute(ssql);
		 }
		lineStatement.close();
		selectStatement.close();
		insertStatement.close();
		selectConn.close();
		insertConn.close();
	}
}
