package cn.nit.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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
public class StockBoard {
	public static void main(String[] args) throws Exception {
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
		Statement updateStatement = conn.createStatement();

		String boardcode="881262";
		String[] stockCodes = new String[] {
				"600051",	
				"600079",	
				"600119",	
				"600200",	
				"600209",	
				"600256",	
				"600555",	
				"600608",	
				"600620",	
				"600622",	
				"600643",	
				"600647",	
				"600661",	
				"600683",	
				"600687",	
				"600708",	
				"600711",	
				"600730",	
				"600743",	
				"600759",	
				"600770",	
				"600784",	
				"600790",	
				"600805",	
				"600811",	
				"600832",	
				"600846",	
				"600858",	
				"600868",	
				"600872",	
				"600881",	
				"600883",	
				"000005",	
				"000009",	
				"000025",	
				"000034",	
				"000040",	
				"000301",	
				"000503",	
				"000507",	
				"000532",	
				"000551",	
				"000571",	
				"000626",	
				"000632",	
				"000633",	
				"000671",	
				"000803",	
				"000881",	
				"000915",	
				"002077",	
				"002344",	
				"300012",	


		};
		
		for (String stockCode : stockCodes) {
			String sql = "select * from stock where code='" + stockCode + "'";

			ResultSet resultSet = stStock.executeQuery(sql);
			Integer stockID = 0;
			while (resultSet.next()) {
				stockID = resultSet.getInt("pkid");
			}
			
			if (stockID == 0) {
				String stockType = stockCode.startsWith("0") ? "sz":"sh";				
				sql = "insert into stock(code,type) values('" + stockCode + "', '" + stockType + "')";
				System.err.println(sql);
				updateStatement.execute(sql);
				
				sql = "select * from stock where code='" + stockCode + "'";

				resultSet = stStock.executeQuery(sql);				
				while (resultSet.next()) {
					stockID = resultSet.getInt("pkid");
				}
			}
			
			sql = "select * from stockboard where boardcode='" + boardcode + "'";
			resultSet = stStock.executeQuery(sql);
			Integer boardID = 0;
			while (resultSet.next()) {
				boardID = resultSet.getInt("pkid");
				System.err.println("正在处理" + resultSet.getString("boardname")+"板块");
			}

			sql = "insert into stockinboard(stockid,boardid) values(" + stockID + ", " + boardID + ")";
			
			System.err.println(sql);
			updateStatement.execute(sql);
			

		}
		
		conn.close();

	}
}
