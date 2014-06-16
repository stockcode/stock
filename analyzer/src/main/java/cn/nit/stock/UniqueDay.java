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
import java.util.Map.Entry;
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
public class UniqueDay {
	public static void main(String[] args) throws Exception {
		List<String> tradeDates = new ArrayList<String>();
		tradeDates.add("2011-04-29");

		String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		java.sql.Connection conn;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Connection conn1 = DriverManager.getConnection(url, "sa",
				"chrdw,hdhxt.");
		Connection conn2 = DriverManager.getConnection(url, "sa",
				"chrdw,hdhxt.");
		Statement st = conn.createStatement();
		Statement updateStatement = conn2.createStatement();
		Statement selectStatement = conn1.createStatement();

		Map<String, String> map = new HashMap<String, String>();
		
		for (String tradeDate : tradeDates) {

			String sql = "select * from day where tradedate='" + tradeDate + "'";

			ResultSet resultSet = st.executeQuery(sql);

			while (resultSet.next()) {
				String pkid = resultSet.getString("pkid");
				String stockcode = resultSet.getString("stockcode");
				
				if (!map.containsKey(stockcode)) {
					map.put(stockcode, pkid);
				}

			}
			
			for (Entry<String, String> entry : map.entrySet()) {
				sql = "delete day where pkid=" + entry.getValue();
				System.err.println(sql);
				int nresult = updateStatement.executeUpdate(sql);
			}
		}
		
		st.close();
		selectStatement.close();
		conn.close();
		conn1.close();
		conn2.close();

	}
}
