package cn.nit.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
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
public class Yesterday {
	public static void main(String[] args) throws Exception {
		Connection conn = ConnUtils.getConn();
		Statement st = conn.createStatement();
		Statement st1 = conn.createStatement();
		Statement statement = conn.createStatement();
		Statement updateStatement = conn.createStatement();
		String sql = "select * from stock";
		ResultSet resultSet = st.executeQuery(sql);
		
		while (resultSet.next()) {		
			String stockcode = resultSet.getString("code");
			System.err.println(stockcode);
			AddStock.createDatabase(stockcode);
			String type = "sznse";
			if (stockcode.startsWith("6"))
				type = "shase";
			
			PreparedStatement pstmt = conn.prepareStatement((new StringBuilder(
					"use A")).append(stockcode).toString());
			pstmt.execute();

			
			sql = "select * from day where YESTERDAY is null and tradedate>'2012-10-1' ";

			ResultSet rSet = st1.executeQuery(sql);

			while (rSet.next()) {
				String pkid = rSet.getString("pkid");				
				String tradedate = rSet.getString("tradedate");
				String ssql = "select * from day where stockcode='" + stockcode
						+ "' and tradedate<'" + tradedate
						+ "' order by tradedate desc limit 0,1";
				// System.err.println(ssql);
				ResultSet rs = statement.executeQuery(ssql);
				if (rs.next()) {
					sql = "update day set yesterday="
							+ rs.getString("close") + " where pkid="
							+ pkid;
					System.err.println(sql);
					int nresult = updateStatement.executeUpdate(sql);
					// System.err.println(nresult);
				}

			}

		}
		st.close();
		statement.close();
		updateStatement.close();
		conn.close();

	}
}
