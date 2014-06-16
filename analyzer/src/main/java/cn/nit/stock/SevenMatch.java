package cn.nit.stock;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 * 
 */
public class SevenMatch {
	public static void main(String[] args) throws Exception {

		List<String> tradeDates = new ArrayList<String>();
		//tradeDates.add("2011-02-09");

		tradeDates.add("2011-01-31");
		tradeDates.add("2011-02-01");

		String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		java.sql.Connection conn;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement st = conn.createStatement();
		Statement updateStatement = conn.createStatement();
		Statement insertStatement = conn.createStatement();

		for (String tradeDate : tradeDates) {

			String sql = "select * from seven where tradedate='" + tradeDate
					+ "'";

			ResultSet resultSet = st.executeQuery(sql);
			Integer index = 1;
			while (resultSet.next()) {
				String pkid = resultSet.getString("pkid");
				String stockcode = resultSet.getString("stockcode");
				String stockName = resultSet.getString("stockname");
				Double sevenPrice = resultSet.getDouble("sevenprice");

				sql = "select * from day where stockcode='" + stockcode
						+ "' and tradedate>'" + tradeDate
						+ "' order by tradedate";

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

				Double lowPrice = 0d;
				int lowIndex = 0;

				for (int i = 0; i < lowPriceList.size(); i++) {
					Double currentPrice = lowPriceList.get(i);
					if (sevenPrice >= currentPrice) {

						sql = "update seven set matchdate='"
								+ tradeDateList.get(i) + "' where pkid=" + pkid;
						System.err.println(sql);
						insertStatement.execute(sql);
						break;
					}
				}

			}
		}
		st.close();
		conn.close();

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
