package cn.nit.stock;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Hello world!
 * 
 */
public class SevenGold {
	public static void main(String[] args) throws Exception {

		List<String> tradeDates = new ArrayList<String>();
		tradeDates.add("2011-01-04");
		tradeDates.add("2011-01-05");
		tradeDates.add("2011-01-06");
		tradeDates.add("2011-01-07");

		String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		java.sql.Connection conn;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement st = conn.createStatement();
		Statement updateStatement = conn.createStatement();
		Statement insertStatement = conn.createStatement();

		for (String tradeDate : tradeDates) {

			String sql = "select * from stock where profit>0 and currentcapital<=100000";

			ResultSet resultSet = st.executeQuery(sql);
			Integer index = 1;
			while (resultSet.next()) {
				String pkid = resultSet.getString("pkid");
				String stockcode = resultSet.getString("code");
				String stockName = resultSet.getString("name");
				if (!StringUtils.isEmpty(stockName)) {
					if (stockName.toUpperCase().contains("ST"))
						continue;
				}

				sql = "select top 60 * from day where stockcode='" + stockcode
						+ "' and tradedate<='" + tradeDate
						+ "' order by tradedate desc";

				ResultSet rs = updateStatement.executeQuery(sql);

				List<String> tradeDateList = new ArrayList<String>();
				List<Double> lowPriceList = new ArrayList<Double>();
				List<Double> highPriceList = new ArrayList<Double>();

				Boolean first = true;
				Double closePrice = 0d;
				while (rs.next()) {
					if (first) {
						closePrice = rs.getDouble("closeprice");
						first = false;
					}
					String tradedate = rs.getString("tradedate");
					tradeDateList.add(tradedate);
					lowPriceList.add(rs.getDouble("lowprice"));
					highPriceList.add(rs.getDouble("highprice"));
				}

				if (tradeDateList.size() == 0)
					continue;

				Double maxPrice = 0d;
				for (Double price : highPriceList) {
					if (maxPrice < price)
						maxPrice = price;
				}

				Double highPrice = 0d;
				int highIndex = 0;

				for (int i = 0; i < highPriceList.size(); i++) {
					Double currentPrice = highPriceList.get(i);
					Boolean flag = true;
					try {
						for (int j = 1; j <= 5; j++) {
							if (currentPrice < highPriceList.get(i + j)) {
								flag = false;
								break;
							}
						}
					} catch (Exception e) {
						flag = false;
					}

					if (flag) {
						highIndex = i;
						highPrice = currentPrice;
						break;
					}
				}

				Double lowPrice = 0d;
				int lowIndex = 0;

				for (int i = highIndex; i < lowPriceList.size(); i++) {
					Double currentPrice = lowPriceList.get(i);
					Boolean flag = true;
					try {
						for (int j = 1; j <= 5; j++) {
							if (currentPrice > lowPriceList.get(i + j)) {
								flag = false;
								break;
							}
						}
					} catch (Exception e) {
						flag = false;
					}

					if (flag) {
						lowIndex = i;
						lowPrice = currentPrice;
						break;
					}
				}

				Double goldPrice = highPrice - (highPrice - lowPrice) * 0.712;

				Double percent = (highPrice - lowPrice) / lowPrice * 100;

				if (highPrice >= maxPrice && closePrice > goldPrice
						&& closePrice * 0.9 <= goldPrice
						&& goldPrice > lowPrice && percent > 10) {

					System.err.println("股票代码：" + stockcode + ",交易日："
							+ tradeDate);
					System.err.println("最高价：" + highPrice);
					System.err.println("最低价：" + lowPrice);
					System.err.println("收盘价：" + closePrice);
					System.err.println("上涨幅度： " + percent + ", 7寸价："
							+ goldPrice);
					index++;

					StringBuilder sb = new StringBuilder();
					sb
							.append("insert into seven(stockcode, stockname, highprice, lowprice, plpercent, sevenprice, matchpercent, tradedate) values('"
									+ stockcode);
					sb.append("','" + stockName);
					sb.append("'," + highPrice);
					sb.append("," + lowPrice);
					sb.append("," + percent);
					sb.append("," + goldPrice);
					sb
							.append("," + (closePrice - goldPrice) / closePrice
									* 100);
					sb.append(",'" + tradeDateList.get(0) + "')");

					sql = sb.toString();
					System.err.println(sql);
					insertStatement.execute(sql);
				}
			}

		}
		st.close();
		conn.close();

		//System.err.println("共" + index + "只股票符合七寸战法");
		// Properties props = System.getProperties();
		// props.put("mail.smtp.host", "smtp.139.com");
		//
		// Session session = Session.getDefaultInstance(props, null);
		// MimeMessage message = new MimeMessage(session);
		// message.setFrom(new InternetAddress("13613803575@139.com"));
		// message.addRecipient(Message.RecipientType.TO, new InternetAddress(
		// "13613803575@139.com"));
		//
		// DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
		// Date tradedate = new Date();
		// message.setSubject(df.format(tradedate) + " 跳空涨停提醒：");
		// String content = sb.toString();
		// if (StringUtils.isEmpty(content)) {
		// content = "今日没有跳空涨停股票。";
		// }
		// message.setText(content);

		// Transport transport = session.getTransport( "smtp" );//指定的协议
		// transport.connect("smtp.139.com", "13613803575", "gk790624");
		// transport.sendMessage(message,message.getAllRecipients());
		// transport.close();
	}
}
