package cn.nit.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public class NSHistory {
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
		Statement st = conn.createStatement();
		Statement stQuery = conn.createStatement();
		Statement updateStatement = conn.createStatement();

		String sql = "select * from stock";

		ResultSet resultSet = st.executeQuery(sql);
		while (resultSet.next()) {
			String pkid = resultSet.getString("pkid");
			String stockcode = resultSet.getString("code");
			String stocktype = resultSet.getString("type").toLowerCase();
			if (stocktype.equals("sh")) {
				stocktype = "ss";
			}
					
			System.err.println("stockcode=" + stockcode);
			
			
			sql = "select top 1 * from day where STOCKCODE='" + stockcode + "'";
			
			ResultSet rs = stQuery.executeQuery(sql);
			
			if (rs.next()) {
				continue;
			}
			
			// Create a method instance.
			GetMethod method = new GetMethod(
					"http://table.finance.yahoo.com/table.csv?s=" + stockcode
							+ "." + stocktype);
			method.addRequestHeader("Content-type", "text/html; charset=utf-8");
			// Provide custom retry handler is necessary
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
					new DefaultHttpMethodRetryHandler(3, false));

			try {
				// 定义一个输入流
				InputStream ins = null;
				// 定义文件流
				BufferedReader br = null;

				// Execute the method.
				int statusCode = client.executeMethod(method);

				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Method failed: "
							+ method.getStatusLine());
				}

				ins = method.getResponseBodyAsStream();

				// 按服务器编码字符集构建文件流，这里的CHARSET要根据实际情况设置
				Boolean firstLine = true;
				br = new BufferedReader(new InputStreamReader(ins, method
						.getResponseCharSet()));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (line.contains("Not Found")) break;
					
					if (firstLine) {
						firstLine = false;
						continue;
					}
					
					
					String[] results = line.split(",");

					String tradedate = results[0];

					
					String openprice = results[1];
					String highprice = results[2];
					String lowprice = results[3];
					String closeprice = results[4];
					String volume = results[5];

					StringBuilder sb = new StringBuilder();
					sb
							.append("insert into day(stockcode, openprice, highprice, lowprice, closeprice, volume, tradedate) values('"
									+ stockcode);
					sb.append("'," + openprice);
					sb.append("," + highprice);
					sb.append("," + lowprice);
					sb.append("," + closeprice);
					sb.append("," + volume);
					sb.append(",'" + tradedate + "')");

					sql = sb.toString();
					System.err.println(sql);
					updateStatement.execute(sql);
				}

			} catch (HttpException e) {
				System.err.println("Fatal protocol violation: "
						+ e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Fatal transport error: " + e.getMessage());
				e.printStackTrace();
			} finally {
				// Release the connection.
				method.releaseConnection();
			}

		}
		st.close();
		stQuery.close();
		conn.close();

	}
}
