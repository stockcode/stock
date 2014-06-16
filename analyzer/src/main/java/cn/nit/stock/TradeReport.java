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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TradeReport {

	public static void main(String[] args) throws Exception {

		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				"GBK");

		List<String> tradedates = new ArrayList<String>();

		Connection conn = ConnUtils.getConn();

		Statement st = conn.createStatement();
		String dateSql = "select tradedate from day where stockcode='1A0001' and (tradedate between '2012-1-1' and '2012-1-31')";
		ResultSet resultSet = st.executeQuery(dateSql);
		while (resultSet.next()) {
			tradedates.add(dataFormat.format(resultSet.getDate("tradedate")));
		}
		resultSet.close();
		conn.close();

		tradedates.add("2013-01-18");

		for (String tradedate : tradedates) {

			System.err.println("分析日期：" + tradedate);
			handleShangHai(client, tradedate);

			//handleShenZhen(client, tradedate);
		}
	}

	private static void handleShenZhen(HttpClient client, String tradedate) throws SQLException, ClassNotFoundException, IOException {
		String url = "http://www.szse.cn/szseWeb/FrontController.szse?ACTIONID=8&CATALOGID=1842_xxpl&TABKEY=tab1&ENCODE=1&txtEnd="+tradedate + "&txtStart="
				+ tradedate;

			String reason = "", stockCode = "", stockName = "", tradeamount = "", trademoney = "";
			Document doc = Jsoup.connect(url).ignoreContentType(true).timeout(30000).get();
			Elements tds = doc.select("td");
			Iterator<Element> iter = tds.iterator();
			while(iter.hasNext()) {				
				Element td = iter.next();
				
				if (td.hasAttr("style")) {
					tradedate = td.previousElementSibling().text();
					
					stockCode = td.text();
					
					td = iter.next();
					stockName = td.text();
					
					td = iter.next();
					trademoney = td.text().replace(",", "");
					
					td = iter.next();
					tradeamount = td.text().replace(",", "");
					
					td = iter.next();
					reason = td.text();
					
					HandleSZContent(stockCode, stockName,trademoney, tradeamount, reason, tradedate);					
				}
			}
	}

	private static void HandleSZContent(String stockCode, String stockName,
			String trademoney, String tradeamount, String reason,
			String tradedate) throws ClassNotFoundException, SQLException, IOException {
		
		String code ="0901";
		
		if (reason.startsWith("异常期间价格涨幅偏离值累计达到")) code = "1001";
		else if (reason.startsWith("日换手率达到")) code = "0903";
		else if (reason.startsWith("日价格涨幅偏离值达到")) code = "0901";
		else if (reason.startsWith("日价格振幅达到")) code = "0902";
		
		Connection conn = ConnUtils.getConn();

		Statement st = conn.createStatement();
		
		StringBuilder sb = new StringBuilder();
		sb.append("insert into tradereport(stockcode, stockname, reason, code, tradeamount, trademoney, tradedate) values(");
		sb.append("'" + stockCode);
		sb.append("','" + stockName);
		sb.append("','" + reason);
		sb.append("','" + code);
		sb.append("'," + tradeamount);		
		sb.append("," + trademoney);
		sb.append(",'" + tradedate + "')");

		String sql = sb.toString();
		System.err.println(sql);
		
		st.execute(sql);
		
		ResultSet rs = st.executeQuery("SELECT LAST_INSERT_ID() AS pkid");
		rs.next();
		int reportid = rs.getInt("pkid");
		
		String url = String.format("http://www.szse.cn/szseWeb/FrontController.szse?ACTIONID=7&CATALOGID=1842_detal&TABKEY=tab2&DQRQ=%s&ZQDM=%s&ZBDM=%s", tradedate, stockCode, code);
		System.err.println(url);
		
		Document doc = Jsoup.connect(url).timeout(30000).get();
		Elements tds = doc.select("td");
		Iterator<Element> iter = tds.iterator();
		while(iter.hasNext()) {
			Element td = iter.next();
			
			if (td.hasClass("cls-data-td")) {
				
				String orderNo = td.text();
				String deptName = iter.next().text();
				String buyMoney = iter.next().text().replace(",", "");
				String sellMoney = iter.next().text().replace(",", "");
				
				sb = new StringBuilder();
				sb.append("insert into tradereportdept(reportid, orderno, deptname, buymoney, sellmoney) values("
						+ reportid);
				sb.append(",'" + orderNo);
				sb.append("','" + deptName);
				sb.append("'," + buyMoney);
				sb.append("," + sellMoney + ")");
				System.err.println(sb);
				st.execute(sb.toString());
			}
		}
		st.close();
		conn.close();
	}

	private static void handleShangHai(HttpClient client, String tradedate)
			throws SQLException, ClassNotFoundException, IOException {
		
		String url = "http://www.sse.com.cn/sseportal/webapp/datapresent/SSENewTradeInfoPublishAct?DATE="
				+ tradedate;
		Document doc = Jsoup.connect(url).ignoreContentType(true).timeout(30000).get();
		
		Elements tds = doc.select("td");
		Iterator<Element> iter = tds.iterator();
		while(iter.hasNext()) {
			Element td = iter.next();
			if (td.hasClass("content_gkjy")) {
				String content = StringUtils.deleteWhitespace(td.text());
				
				String[] lines = content.split("一、有价格涨跌幅限制的日收盘价格涨幅偏离值达到7%的前三只证券:");
				String line = lines[0];
				content = lines[1];
				
				content = HandleSHContent(tradedate, content, "二、有价格涨跌幅限制的日收盘价格跌幅偏离值达到7%的前三只证券:", "highpercent");
				
				content = HandleSHContent(tradedate, content, "三、有价格涨跌幅限制的日价格振幅达到15%的前三只证券:", "lowpercent");
								
				content = HandleSHContent(tradedate, content, "四、有价格涨跌幅限制的日换手率达到20%的前三只证券:", "maxpercent");
				
				content = HandleSHContent(tradedate, content, "五、无价格涨跌幅限制的证券:", "changepercent");
				
				content = HandleSHContent(tradedate, content, "六、非ST和\\*ST证券连续三个交易日内收盘价格涨幅偏离值累计达到20%的证券:", "nouse");
				
				content = HandleSHContent(tradedate, content, "七、非ST和\\*ST证券连续三个交易日内收盘价格跌幅偏离值累计达到20%的证券:", "highpercent, period");
				
				content = HandleSHContent(tradedate, content, "八、ST和\\*ST证券连续三个交易日内收盘价格涨幅偏离值累计达到15%的证券:", "lowpercent, period");
				
				content = HandleSHContent(tradedate, content, "九、ST和\\*ST证券连续三个交易日内收盘价格跌幅偏离值累计达到15%的证券:", "highpercent, period");
				
				System.err.println(content);
				//content = HandleSHContent(tradedate, content, "十、连续三个交易日内的日均换手率与前五个交易日日均换手率的比值到达30倍,并且该股票封闭式基金连续三个交易日内累计换手率达到20%", "wait");
			}

		}		
	}

	private static String HandleSHContent(String tradedate, String content, String separator,
			String fieldName) throws SQLException, ClassNotFoundException,
			IOException {

		String [] lines = content.split(separator);
		String line = lines[0];
		content = lines[1];
		
		Connection conn = ConnUtils.getConn();

		Statement st = conn.createStatement();
		Statement updateStatement = conn.createStatement();

		System.err.println(line);
		
		lines = line.split("(1)");
		
		if (lines.length == 2) {
			
		}
//
//
//		String line = "";
//		Integer count = 0;
//		while (!(line = br.readLine()).equals("<br>")) {
//			if (line.startsWith(" 2、B股"))
//				break; // 为空
//
//			count++;
//			line = line.replace("&nbsp;", "").replace("<br>", "")
//					.replace("兴 业", "兴业");
//			// System.err.println(line);
//			String[] strs = line.split(" ");
//			int k = 1;
//			String orderNo = strs[k++].substring(1, 2);
//			String stockCode = strs[k++];
//			String stockName = strs[k++];
//			String maxpercent = "''";
//			if (!fieldName.startsWith("nouse")) {
//				maxpercent = strs[k++];
//				maxpercent = maxpercent.substring(0, maxpercent.length() - 1);
//			}
//			String tradeamount = strs[k++];
//			String trademoney = strs[k++];
//			String period = "";
//			if (fieldName.endsWith("period")) {
//				period = strs[k++];
//			}
//
//			StringBuilder sb = new StringBuilder();
//			sb.append("insert into tradereport(orderno, stockcode, stockname, "
//					+ fieldName
//					+ ", tradeamount, trademoney, tradedate) values(" + orderNo);
//			sb.append(",'" + stockCode);
//			sb.append("','" + stockName);
//			sb.append("'," + maxpercent);
//			if (fieldName.endsWith("period")) {
//				sb.append(",'" + period);
//				sb.append("'," + tradeamount);
//			} else {
//				sb.append("," + tradeamount);
//			}
//			sb.append("," + Double.parseDouble(trademoney) * 10000);
//			sb.append(",'" + tradedate + "')");
//
//			String sql = sb.toString();
//			// System.err.println(sql);
//			updateStatement.execute(sql);
//			br.readLine();
//		}
//
//		// System.err.println("count=" + count);
//		for (int j = 0; j < count; j++) {
//
//			int ct = j == 0 ? 1 : 2;
//			for (int i = 0; i < ct; i++) {
//				br.readLine();
//			}
//
//			line = br.readLine().replace("&nbsp;", "").replace("<br>", "");
//			// System.err.println(line);
//
//			String stockCode = line.substring(6, 13).trim();
//			String stockName = line.substring(20, line.length());
//			// System.err.println("代码：" + stockCode + ",名称：" + stockName);
//
//			for (int i = 0; i < 5; i++) {
//				br.readLine();
//			}
//
//			String[] fields = fieldName.split(",");
//
//			StringBuilder sb = new StringBuilder();
//			sb.append(" and 1=1 ");
//			for (String field : fields) {
//				sb.append(" and " + field + " is not null");
//			}
//
//			String sql = "select pkid from tradereport where stockcode='"
//					+ stockCode + "' and tradedate='" + tradedate + "'"
//					+ sb.toString();
//			// System.err.println(sql);
//			ResultSet resultSet = st.executeQuery(sql);
//			resultSet.next();
//			String reportid = resultSet.getString("pkid");
//			resultSet.close();
//
//			Boolean lessFive = false;
//			for (int i = 0; i < 5; i++) {
//
//				line = br.readLine().replace("&nbsp;", "").replace("<br>", "");
//
//				if (StringUtils.isEmpty(line)) {
//					lessFive = true;
//					break;
//				}
//
//				String[] strs = line.split(" ");
//
//				String orderNo = strs[1].substring(1, 2);
//				String deptName = strs[2];
//				String deptMoney = strs[3];
//
//				sb = new StringBuilder();
//				sb.append("insert into tradereportdept(reportid, orderno, deptname, deptmoney, direction) values("
//						+ reportid);
//				sb.append("," + orderNo);
//				sb.append(",'" + deptName);
//				sb.append("'," + deptMoney);
//				sb.append(",0)");
//
//				sql = sb.toString();
//				// System.err.println(sql);
//				updateStatement.execute(sql);
//				br.readLine();
//			}
//
//			int five = lessFive ? 3 : 4;
//			for (int i = 0; i < five; i++) {
//				br.readLine();
//			}
//
//			for (int i = 0; i < 5; i++) {
//
//				line = br.readLine().replace("&nbsp;", "").replace("<br>", "");
//				String[] strs = line.split(" ");
//
//				String orderNo = strs[1].substring(1, 2);
//				String deptName = strs[2];
//				String deptMoney = strs[3];
//
//				sb = new StringBuilder();
//				sb.append("insert into tradereportdept(reportid, orderno, deptname, deptmoney, direction) values("
//						+ reportid);
//				sb.append("," + orderNo);
//				sb.append(",'" + deptName);
//				sb.append("'," + deptMoney);
//				sb.append(",1)");
//
//				sql = sb.toString();
//				// System.err.println(sql);
//				updateStatement.execute(sql);
//				br.readLine();
//			}
//		}
//
//		conn.close();
		return content;
	}
}
