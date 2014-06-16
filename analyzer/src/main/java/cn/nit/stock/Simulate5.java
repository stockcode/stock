package cn.nit.stock;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.httpclient.HttpClient;

/**
 * Hello world!
 * 
 */
public class Simulate5 {
	public static void main(String[] args) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
		// client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
		// "GBK");

		String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		java.sql.Connection conn;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement stStock = conn.createStatement();
		Statement stBoard = conn.createStatement();
		Statement stDay = conn.createStatement();
		Statement updateStatement = conn.createStatement();

		String tradeSql = "select tradedate from day where stockcode='1a0001' and tradedate>'2010-01-01' order by tradedate desc";

		ResultSet rsDay = stDay.executeQuery(tradeSql);
		while (rsDay.next()) {

			String tradeDate = df.format(rsDay.getDate("tradedate"));
			System.err.println("交易日：" + tradeDate);
			
			String ssql = "select * from day where tradedate='" + tradeDate + "'";
			
			ResultSet rsStock = stBoard.executeQuery(ssql);
			while (rsStock.next()) {
				String stockCode = rsStock.getString("stockcode");
				Double yesterdayPrice = rsStock.getDouble("yesterdayprice");
				Double openPriceD = rsStock.getDouble("openprice");
				String openPrice = rsStock.getString("openprice");
				String lowPrice = rsStock.getString("lowPrice");
				Double closePrice = rsStock.getDouble("closeprice");
				Double highPrice = rsStock.getDouble("highprice");
				
				if (
						openPriceD > yesterdayPrice //跳空高开						
						&& openPrice.equals(lowPrice)  //光脚
						&& (closePrice-yesterdayPrice)/yesterdayPrice > 0.095 //大阳
						) { 
					
					System.err.println(stockCode);
				}
			}
			
		}

		conn.close();

	}
}
