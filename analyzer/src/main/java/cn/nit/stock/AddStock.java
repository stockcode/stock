// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 2012/10/24 13:54:58
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AddStock.java

package cn.nit.stock;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

// Referenced classes of package cn.nit.stock:
//            ConnUtils

public class AddStock {

	public AddStock() {
	}

	public static void main(String args[]) throws Exception {
		Connection conn;
		Statement st;
		Statement insertsStatement;
		List codeList;
		HttpClient client;
		GetMethod method;
		conn = ConnUtils.getConn();
		st = conn.createStatement();
		insertsStatement = conn.createStatement();
		String sql = "select * from stock";
		codeList = new ArrayList();
		String stockcode;
		for (ResultSet resultSet = st.executeQuery(sql); resultSet.next(); codeList
				.add(stockcode))
			stockcode = resultSet.getString("code");

		client = new HttpClient();
		client.getParams().setParameter("http.protocol.content-charset", "GBK");
		method = new GetMethod("http://quote.eastmoney.com/stocklist.html");
		method.addRequestHeader("Content-type", "text/html; charset=utf-8");
		method.getParams().setParameter("http.method.retry-handler",
				new DefaultHttpMethodRetryHandler(3, false));
		InputStream ins = null;
		BufferedReader br = null;
		int statusCode = client.executeMethod(method);
		if (statusCode != 200)
			System.err.println((new StringBuilder("Method failed: ")).append(
					method.getStatusLine()).toString());
		ins = method.getResponseBodyAsStream();
		Boolean firstLine = Boolean.valueOf(true);
		br = new BufferedReader(new InputStreamReader(ins,
				method.getResponseCharSet()));
		String line = null;
		String totalCapital = "";
		String currentCapital = "";
		String profit = "";
		while ((line = br.readLine()) != null)
			if (line.contains("\u4E0A\u6D77\u80A1\u7968"))
				break;
		br.readLine();
		br.readLine();
		br.readLine();
		while ((line = br.readLine()) != null) {
			String words[] = line.split(">");
			if (words.length > 2) {
				words = words[2].substring(0, words[2].length() - 3).split(
						"\\(");
				String name = words[0];
				String code = words[1].substring(0, words[1].length() - 1);
				if (!name.toUpperCase().contains("ST")
						&& !codeList.contains(code) && !code.startsWith("2")
						&& !code.startsWith("5") && !code.startsWith("9")
						&& !code.startsWith("1")) {
					StringBuilder sb = new StringBuilder();
					sb.append("insert into stock(name,code) values('");
					sb.append(name);
					sb.append("','");
					sb.append(code);
					sb.append("')");
					System.err.println(sb.toString());
					insertsStatement.execute(sb.toString());
					//createDatabase(code);
				}
			}
			br.readLine();
		}
		method.releaseConnection();
		st.close();
		insertsStatement.close();
		conn.close();
		return;
	}

	public static void createDatabase(String code)
			throws ClassNotFoundException, SQLException {
		Connection conn = ConnUtils.getConn();
		String sql = (new StringBuilder("CREATE DATABASE IF NOT EXISTS A"))
				.append(code)
				.append(" default charset utf8 COLLATE utf8_general_ci")
				.toString();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.execute();
		pstmt = conn.prepareStatement((new StringBuilder("use A")).append(code)
				.toString());
		pstmt.execute();
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS mn5 (");
		sb.append("PKID int(11) NOT NULL AUTO_INCREMENT,");
		sb.append("STOCKCODE varchar(50) DEFAULT NULL,");
		sb.append("OPEN decimal(10,2) DEFAULT NULL,");
		sb.append("LOW decimal(10,2) DEFAULT NULL,");
		sb.append("HIGH decimal(10,2) DEFAULT NULL,");
		sb.append("CLOSE decimal(10,2) DEFAULT NULL,");
		sb.append("AMOUNT decimal(20,2) DEFAULT NULL,");
		sb.append("VOLUME decimal(20,2) DEFAULT NULL,");
		sb.append("TRADEDATE datetime DEFAULT NULL,");
		sb.append("PRIMARY KEY (PKID)");
		sb.append(") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		sql = sb.toString();
		pstmt = conn.prepareStatement(sql);
		pstmt.execute();
		try {
			sql = "create index IDX_CODE on MN5 (STOCKCODE);";
			pstmt = conn.prepareStatement(sql);
			pstmt.execute();
			sql = "create index IDX_TRADEDATE on MN5 (TRADEDATE);";
			pstmt = conn.prepareStatement(sql);
			pstmt.execute();
		} catch (Exception e) {
			System.err.println("mn5表已存在");
		}
		sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS day (");
		sb.append("PKID int(11) NOT NULL AUTO_INCREMENT,");
		sb.append("STOCKCODE varchar(50) DEFAULT NULL,");
		sb.append("YESTERDAY decimal(10,2) DEFAULT NULL,");
		sb.append("OPEN decimal(10,2) DEFAULT NULL,");
		sb.append("LOW decimal(10,2) DEFAULT NULL,");
		sb.append("HIGH decimal(10,2) DEFAULT NULL,");
		sb.append("CLOSE decimal(10,2) DEFAULT NULL,");
		sb.append("AMOUNT decimal(20,2) DEFAULT NULL,");
		sb.append("VOLUME decimal(20,2) DEFAULT NULL,");
		sb.append("TRADEDATE date DEFAULT NULL,");
		sb.append("PRIMARY KEY (PKID)");
		sb.append(") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;");
		sql = sb.toString();
		pstmt = conn.prepareStatement(sql);
		pstmt.execute();
		try {
			sql = "create index IDX_CODE on day (STOCKCODE);";
			pstmt = conn.prepareStatement(sql);
			pstmt.execute();
			sql = "create index IDX_TRADEDATE on day (TRADEDATE);";
			pstmt = conn.prepareStatement(sql);
			pstmt.execute();
		} catch (Exception e) {
			System.err.println((new StringBuilder(String.valueOf(code)))
					.append("day表已存在").toString());
		}
		conn.close();
	}
}
