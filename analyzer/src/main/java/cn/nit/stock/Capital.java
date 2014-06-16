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

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;

/**
 * Hello world!
 * 
 */
public class Capital {
	public static void main(String[] args) throws Exception {
		
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
		 client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"GBK");

		String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		java.sql.Connection conn;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement st = conn.createStatement();
		Statement updateStatement = conn.createStatement();

		String sql = "select * from stock where totalcapital is null";

		ResultSet resultSet = st.executeQuery(sql);
		while (resultSet.next()) {
			String pkid = resultSet.getString("pkid");
			String stockcode = resultSet.getString("code");
			String stocktype = resultSet.getString("type").toLowerCase();			

			System.err.println("stockcode=" + stockcode);
			// Create a method instance.
			GetMethod method = new GetMethod(
					"http://finance.sina.com.cn/realstock/company/" + stocktype + stockcode + "/nc.shtml");
			
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
				String totalCapital = "", currentCapital = "", profit = "";
				while ((line = br.readLine()) != null) {
					
					if (line.contains("var totalcapital")) {
						totalCapital = line.substring(line.indexOf("=")+2, line.indexOf(";"));
					}
					
					if (line.contains("var currcapital")) {
						currentCapital = line.substring(line.indexOf("=")+2, line.indexOf(";"));
					}
					
					if (line.contains("var curracapital") && currentCapital.equals("0")) {
						currentCapital = line.substring(line.indexOf("=")+2, line.indexOf(";"));
					}
					
					if (line.contains("每股收益</td>")) {
						line = br.readLine();
						profit = line.substring(line.indexOf("<td>")+4, line.indexOf("元"));
						if (StringUtils.isEmpty(profit)) {
							line = line.substring(line.indexOf("</td>"), line.length());
							profit = line.substring(line.indexOf("<td>")+4, line.indexOf("元"));
						}
					}
				}

				StringBuilder sb = new StringBuilder();
				
				sb.append("update stock set ");
				sb.append("totalcapital=" + totalCapital);
				sb.append(", currentcapital=" + currentCapital);
				sb.append(", profit=" + profit);
				sb.append(" where code='" + stockcode + "'");
				sql = sb.toString();
				System.err.println(sql);
				updateStatement.execute(sql);
				
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
		conn.close();

	}
}
