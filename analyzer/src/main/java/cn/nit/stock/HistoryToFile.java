package cn.nit.stock;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 * 
 */
public class HistoryToFile {

    public static void main(String[] args) throws Exception {

        Connection conn = ConnUtils.getConn("stock");

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
		// client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
		// "GBK");

		Statement st = conn.createStatement();
		Statement updateStatement = conn.createStatement();

		String sql = "select * from stock";

		ResultSet resultSet = st.executeQuery(sql);
		while (resultSet.next()) {
			String pkid = resultSet.getString("pkid");
			String stockcode = resultSet.getString("code");
            String stocktype = "sz";
			if (stockcode.startsWith("6")) {
				stocktype = "ss";
			}


			System.err.println("stockcode=" + stockcode);

            URL url = new URL("http://table.finance.yahoo.com/table.csv?s=" + stockcode + "." + stocktype);

            File stockFile = new File("c:\\stockdata\\" + stockcode + ".csv");

            if (stockFile.exists()) continue;

            try {
                FileUtils.copyURLToFile(url, new File("c:\\stockdata\\" + stockcode + ".csv"));
            } catch (Exception e) {
                System.err.println(e.toString());
            }
            Thread.sleep(1000);
		}
		st.close();
		conn.close();

	}
}
