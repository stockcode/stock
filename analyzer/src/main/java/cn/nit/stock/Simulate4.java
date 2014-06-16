package cn.nit.stock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cn.nit.stock.model.Stock;

/**
 * Hello world!
 * 
 */
public class Simulate4 {
	static String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
	static String driver = "net.sourceforge.jtds.jdbc.Driver";

	public static void main(String[] args) throws Exception {

		String[] tradeList = new String[] { "2010-08-16"};

		for (int i = 0; i < tradeList.length; i++) {
			String chooseDate = tradeList[i];
			
			String nextDate="";
			if (i==tradeList.length-1) {
				nextDate = "2010-10-08";
			} else {
				nextDate = tradeList[i + 1];
			}
			System.err.println("交易日：" + chooseDate);

			//Sell(chooseDate);
			//Buy(chooseDate);

			//analyseSell(chooseDate, nextDate);
			analyseBuy(chooseDate, nextDate);

			//statics(chooseDate, nextDate);			
		}

	}

	private static void Buy(String chooseDate) throws ClassNotFoundException,
			SQLException {
		Connection selectConn, insertConn;

		Class.forName(driver);
		selectConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		insertConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement selectStatement = selectConn.createStatement();
		Statement lineStatement = selectConn.createStatement();
		Statement insertStatement = insertConn.createStatement();

		String sql = "select * from sim_account where tradedate='" + chooseDate
				+ "'";
		ResultSet resultSet = selectStatement.executeQuery(sql);

		Double usableMoney = 0d;
		while (resultSet.next()) {
			usableMoney = resultSet.getDouble("usable");
		}

		sql = "select * from sim_robot where  result is null and operation='BUY' and tradedate='"
				+ chooseDate + "'";

		resultSet = selectStatement.executeQuery(sql);

		while (resultSet.next()) {
			Integer pkid = resultSet.getInt("pkid");
			String stockCode = resultSet.getString("code");
			Integer stockAmount = resultSet.getInt("amount");
			String ssql = "select top 1 * from day where stockcode='"
					+ stockCode + "' and tradedate<='" + chooseDate
					+ "' order by tradedate desc";

			ResultSet rs = lineStatement.executeQuery(ssql);
			Double openPrice = 0d, closePrice = 0d;
			while (rs.next()) {
				openPrice = rs.getDouble("openprice");
				closePrice = rs.getDouble("closePrice");
			}

			

			Double costMoney = openPrice * stockAmount;
			if (usableMoney >= costMoney) {
				StringBuilder sb = new StringBuilder();
				sb
						.append("insert sim_stockaccount(stockcode, stockremain, costPrice, currentprice, currentmoney, tradedate) values(");
				sb.append("'" + stockCode + "', ");
				sb.append(stockAmount + ",");
				sb.append(openPrice + ",");
				sb.append(closePrice + ",");
				sb.append(closePrice * stockAmount + ",");
				sb.append("'" + chooseDate + "')");
				ssql = sb.toString();
				System.err.println(ssql);
				insertStatement.execute(ssql);
				usableMoney = usableMoney - costMoney;
				
				ssql = "update sim_robot set result='委托已成功' where pkid=" + pkid;
				System.err.println(ssql);
				insertStatement.execute(ssql);
			}
		}

		String ssql = "update sim_account set usable=" + usableMoney
				+ " where tradedate='" + chooseDate + "'";
		System.err.println(ssql);
		insertStatement.execute(ssql);
	}

	private static void Sell(String chooseDate) throws ClassNotFoundException,
			SQLException {
		Connection selectConn, insertConn;

		Class.forName(driver);
		selectConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		insertConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement selectStatement = selectConn.createStatement();
		Statement lineStatement = selectConn.createStatement();
		Statement insertStatement = insertConn.createStatement();

		String sql = "select * from sim_robot where  result is null and operation='SELL' and tradedate='"
				+ chooseDate + "'";

		ResultSet resultSet = selectStatement.executeQuery(sql);

		while (resultSet.next()) {
			Integer pkid = resultSet.getInt("pkid");
			String stockCode = resultSet.getString("code");
			Integer stockAmount = resultSet.getInt("amount");
			String ssql = "select top 1 * from day where stockcode='"
					+ stockCode + "' and tradedate<='" + chooseDate
					+ "' order by tradedate desc";

			ResultSet rs = lineStatement.executeQuery(ssql);
			Double openPrice = 0d, closePrice = 0d;
			while (rs.next()) {
				openPrice = rs.getDouble("openprice");
			}

			ssql = "update sim_robot set result='委托已成功' where pkid=" + pkid;
			System.err.println(ssql);
			insertStatement.execute(ssql);

			ssql = "delete from sim_stockaccount where stockcode='" + stockCode
					+ "' and tradedate='" + chooseDate + "'";
			System.err.println(ssql);
			insertStatement.execute(ssql);

			Double costMoney = openPrice * stockAmount;
			ssql = "update sim_account set usable=usable+" + costMoney
					+ " where tradedate='" + chooseDate + "'";
			System.err.println(ssql);
			insertStatement.execute(ssql);

		}
	}

	private static void analyseSell(String chooseDate, String nextDate)
			throws ClassNotFoundException, SQLException {
		Connection selectConn, insertConn;

		Class.forName(driver);
		selectConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		insertConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement selectStatement = selectConn.createStatement();
		Statement lineStatement = selectConn.createStatement();
		Statement insertStatement = insertConn.createStatement();

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date tradedate = new Date();

		String sql = "select * from sim_stockaccount where tradedate='"
				+ chooseDate + "'";
		ResultSet resultSet = selectStatement.executeQuery(sql);

		while (resultSet.next()) {
			String stockCode = resultSet.getString("stockcode");
			Double costPrice = resultSet.getDouble("costPrice");
			Integer stockUsable = resultSet.getInt("stockremain");

			String ssql = "select top 1 * from day where stockcode='"
					+ stockCode + "' and tradedate<='" + chooseDate
					+ "' order by tradedate desc";
			ResultSet rs = lineStatement.executeQuery(ssql);
			Double currentPrice = 0d;
			while (rs.next()) {
				currentPrice = rs.getDouble("closeprice");
			}

			StringBuilder sb = new StringBuilder();
			sb
					.append("insert SIM_ROBOT(operation, Code, Price, Amount, priority, tradedate) values('SELL', ");
			sb.append("'" + stockCode + "', ");
			sb.append(currentPrice + ",");
			sb.append(stockUsable + ",0,");
			sb.append("'" + nextDate + "')");
			ssql = sb.toString();
			System.err.println(ssql);
			insertStatement.execute(ssql);

		}

		selectStatement.close();
	}

	private static void analyseBuy(String chooseDate, String nextDate)
			throws SQLException, ParseException, ClassNotFoundException {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.## ");
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
		String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		java.sql.Connection conn;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Connection conn1 = DriverManager.getConnection(url, "sa",
				"chrdw,hdhxt.");
		Connection conn2 = DriverManager.getConnection(url, "sa",
				"chrdw,hdhxt.");
		Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		;
		Statement stStock = conn.createStatement();
		Statement stBoard = conn.createStatement();
		Statement insertStatement = conn2.createStatement();
		Statement selectStatement = conn1.createStatement();

		Map<Integer, Stock> stockMap = new HashMap<Integer, Stock>();
		
		String sql = "select * from stock";

		ResultSet resultSet = stBoard.executeQuery(sql);

		while (resultSet.next()) {
			Integer stockID = resultSet.getInt("pkid");
			String stockCode = resultSet.getString("code");
			String status = resultSet.getString("status");
			Stock stock = new Stock();
			stock.stockCode = stockCode;
			stock.status = status;
			stockMap.put(stockID, stock);
		}
		
		
		String stockSql = "select boardcode,jjchl1 from boardday,stockboard where boardid=stockboard.pkid and tradedate = '"
				+ chooseDate + "' order by jjchl1 desc";

		ResultSet rsBoard = stBoard.executeQuery(stockSql);

		Integer top = 3;
		while (rsBoard.next()) {
			String boardCode = rsBoard.getString("boardcode");
			Double jjchl1 = rsBoard.getDouble("jjchl1");
			if (jjchl1 < 2000) break;
			
			top--;
			
			if (top < 0) break;
			
			System.err.println("板块代码=" + boardCode + "机构吃货量：" + jjchl1 + "万股");			

			stockSql = "select * from stockline where boardcode='"
					+ boardCode + "' and tradedate='" + chooseDate + "' order by jgchl1 desc";

			ResultSet rsStock = stStock.executeQuery(stockSql);

			int count = 1;

			while (rsStock.next()) {

				Integer stockID = rsStock.getInt("stockid");

				Stock stock = stockMap.get(stockID);
				String stockCode = stock.stockCode;
				String status = stock.status;
				
				Double jgchl1 = rsStock.getDouble("jgchl1");
				if (stockCode.startsWith("3")) continue;
				if (!StringUtils.isEmpty(status) &&status.equals("大盘股")) continue;
				if (jgchl1 < 1000) break;
				
				// stockCode="002181";
				
				
				System.err.println("分析第" + count++ + "只股票,股票代码：" + stockCode + ",机构吃货量：" + jgchl1);
				
				if (count > 3) break;
				
//
//				String sql = "select top 30 * from day where stockcode='"
//						+ stockCode + "' and tradedate<='" + chooseDate
//						+ "' order by tradedate desc";
//
//				ResultSet resultSet = st.executeQuery(sql);
//
//				ArrayList<Double> priceList = new ArrayList<Double>();
//				List<String> tradeDateList = new ArrayList<String>();
//				List<Double> lowPriceList = new ArrayList<Double>();
//				List<Double> highPriceList = new ArrayList<Double>();
//				List<Double> openPriceList = new ArrayList<Double>();
//
//				String lastTrade = "";
//
//				resultSet.last();
//				while (resultSet.previous()) {
//					String stockcode = resultSet.getString("stockcode");
//					String tradedate = resultSet.getString("tradedate");
//					Double closeprice = resultSet.getDouble("closeprice");
//					priceList.add(closeprice);
//					tradeDateList.add(tradedate);
//					lastTrade = tradedate;
//					lowPriceList.add(resultSet.getDouble("lowprice"));
//					highPriceList.add(resultSet.getDouble("highprice"));
//					openPriceList.add(resultSet.getDouble("openprice"));
//				}
//				if (!lastTrade.startsWith(chooseDate))
//					continue;
//				if (priceList.size() == 0 || priceList.size() < 25)
//					continue;
//
//				Double[] avgList = new Double[priceList.size()];
//				Double[] avg3List = new Double[priceList.size()];
//				Double[] avg5List = new Double[priceList.size()];
//				Double[] avg21List = new Double[priceList.size()];
//				// System.err.println("首个交易日：" + tradeDateList.get(0));
//
//				Double maxPL = 0d;
//
//				Map<Integer, Double[]> avgMap = new HashMap<Integer, Double[]>();
//
//				for (int m = 3; m <= 21; m++) {
//					Double[] aList = new Double[priceList.size()];
//
//					for (int j = m; j < priceList.size(); j++) {
//						Double currentavg = 0d;
//						for (int k = j - m + 1; k <= j; k++) {
//							currentavg += priceList.get(k);
//						}
//						aList[j] = currentavg / m;
//					}
//
//					avgMap.put(m, aList);
//				}
//
//				avg3List = avgMap.get(3);
//				avg5List = avgMap.get(5);
//				avg21List = avgMap.get(21);
//
//				int currentIndex = priceList.size() - 1;
//				Double last3avg = avg3List[currentIndex - 1];
//				Double last5avg = avg5List[currentIndex - 1];
//				Double last21avg = avg21List[currentIndex - 1];
//
//				Double current3avg = avg3List[currentIndex];
//				Double current5avg = avg5List[currentIndex];
//				Double current21avg = avg21List[currentIndex];
//
//				if ((last5avg <= last21avg)
//						&& (current5avg > (current21avg + current21avg * 0.01))
//						&& current5avg > last5avg) // 符合条件
//				{
//					System.err.println("stockcode=" + stockCode);
//					System.err.print(".昨日收盘价="
//							+ priceList.get(currentIndex - 1) + ",今日收盘价="
//							+ priceList.get(currentIndex));
//					System.err.print("。昨日21日均线：" + last21avg);
//					System.err.println("。今日21日均线：" + current21avg);
//
//					StringBuilder sb = new StringBuilder();
//					sb
//							.append("insert SIM_BAOHUA(ChooseType, StockCode, Price, Created) values(2,");
//					sb.append("'" + stockCode + "', ");
//					sb.append(priceList.get(currentIndex) + ",");
//					sb.append("'" + chooseDate + "')");
//					String ssql = sb.toString();
//					System.err.println(ssql);
//					insertStatement.execute(ssql);
//				}
			}
		}

//		String sql = "select * from sim_baohua where created = '" + chooseDate
//				+ "'";
//
//		ResultSet resultSet = selectStatement.executeQuery(sql);
//		while (resultSet.next()) {
//			Double price = resultSet.getDouble("price");
//			String stockCode = resultSet.getString("stockcode");
//
//			price += price * 0.005;
//			if (price > 25)
//				continue;
//
//			int amount = 100; // 1手
//			while (price * amount < 2500) {
//				amount += 100;
//			}
//
//			Calendar calendar = Calendar.getInstance();
//
//			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
//				calendar.add(Calendar.DAY_OF_WEEK, 3);
//			} else {
//				calendar.add(Calendar.DAY_OF_WEEK, 1);
//			}
//
//			StringBuilder sb = new StringBuilder();
//			sb
//					.append("insert sim_ROBOT(operation, Code, Price, Amount, priority, tradedate) values('BUY', ");
//			sb.append("'" + stockCode + "', ");
//			sb.append(price + ",");
//			sb.append(amount + ",0,");
//			sb.append("'" + nextDate + "')");
//			String ssql = sb.toString();
//			System.err.println(ssql);
//			insertStatement.execute(ssql);
//		}

		st.close();
		stStock.close();
		selectStatement.close();
		conn.close();
		conn1.close();
		conn2.close();
	}

	private static void statics(String chooseDate, String nextDate)
			throws ClassNotFoundException, SQLException {
		Connection selectConn, insertConn;

		Class.forName(driver);
		selectConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		insertConn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement selectStatement = selectConn.createStatement();
		Statement lineStatement = selectConn.createStatement();
		Statement insertStatement = insertConn.createStatement();

		String sql = "select * from sim_stockaccount where  tradedate='"
				+ chooseDate + "'";

		ResultSet resultSet = selectStatement.executeQuery(sql);

		Double stockMoney = 0d;

		while (resultSet.next()) {
			Integer pkid = resultSet.getInt("pkid");
			String stockCode = resultSet.getString("stockcode");
			Integer stockAmount = resultSet.getInt("stockremain");
			String ssql = "select top 1 * from day where stockcode='"
					+ stockCode + "' and tradedate<='" + chooseDate
					+ "' order by tradedate desc";

			ResultSet rs = lineStatement.executeQuery(ssql);
			Double closePrice = 0d;
			while (rs.next()) {
				closePrice = rs.getDouble("closeprice");
			}

			Double costMoney = closePrice * stockAmount;
			stockMoney += costMoney;
			ssql = "update sim_stockaccount set currentprice=" + closePrice
					+ " ,currentmoney=" + costMoney + " where pkid=" + pkid;
			System.err.println(ssql);
			insertStatement.execute(ssql);

		}

		sql = "insert into sim_stockaccount(stockcode,stockremain, costprice, currentprice,currentmoney,tradedate) select stockcode,stockremain, costprice, currentprice,currentmoney, '"
				+ nextDate
				+ "' from sim_stockaccount where tradedate='"
				+ chooseDate + "'";
		System.err.println(sql);
		insertStatement.execute(sql);

		sql = "update sim_account set stock=" + stockMoney
				+ " where tradedate='" + chooseDate + "'";
		System.err.println(sql);
		insertStatement.execute(sql);

		sql = "update sim_account set total=stock+usable where tradedate='"
				+ chooseDate + "'";
		System.err.println(sql);
		insertStatement.execute(sql);

		sql = "insert into sim_account(usable, stock,total,tradedate) select usable,stock, total, '"
				+ nextDate
				+ "' from sim_account where tradedate='"
				+ chooseDate + "'";
		System.err.println(sql);
		insertStatement.execute(sql);
	}
}
