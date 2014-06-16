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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import cn.nit.stock.model.Stock;

/**
 * Hello world!
 * 
 */
public class Simulate2 {
	public static void main(String[] args) throws Exception {
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
		Statement st = conn.createStatement();
		Statement stStock = conn.createStatement();
		Statement updateStatement = conn2.createStatement();
		Statement selectStatement = conn1.createStatement();

		String stockSql = "select * from stock";

		ResultSet rsStock = stStock.executeQuery(stockSql);
		Date startTrade = dataFormat.parse("2010-01-01");

		while (rsStock.next()) {
			String stockCode = rsStock.getString("code");
			stockCode = "600086";
			System.err.print("分析" + stockCode + ",");
			String sql = "select * from day where stockcode='" + stockCode
					+ "' order by tradedate";

			ResultSet resultSet = st.executeQuery(sql);

			ArrayList<Double> priceList = new ArrayList<Double>();
			List<String> tradeDateList = new ArrayList<String>();
			List<Double> lowPriceList = new ArrayList<Double>();
			List<Double> highPriceList = new ArrayList<Double>();
			List<Double> openPriceList = new ArrayList<Double>();

			while (resultSet.next()) {
				String stockcode = resultSet.getString("stockcode");
				String tradedate = resultSet.getString("tradedate");
				Double closeprice = resultSet.getDouble("closeprice");
				priceList.add(closeprice);
				tradeDateList.add(tradedate);
				lowPriceList.add(resultSet.getDouble("lowprice"));
				highPriceList.add(resultSet.getDouble("highprice"));
				openPriceList.add(resultSet.getDouble("openprice"));
			}
			if (priceList.size() == 0)
				continue;

			Double[] avgList = new Double[priceList.size()];
			Double[] avg3List = new Double[priceList.size()];
			Double[] avg5List = new Double[priceList.size()];
			Double[] avg21List = new Double[priceList.size()];
			// System.err.println("首个交易日：" + tradeDateList.get(0));

			Double maxPL = 0d;

			Map<Integer, Double[]> avgMap = new HashMap<Integer, Double[]>();

			for (int i = 2; i <= 150; i++) {
				Double[] aList = new Double[priceList.size()];

				for (int j = i; j < priceList.size(); j++) {
					Double currentavg = 0d;
					for (int k = j - i + 1; k <= j; k++) {
						currentavg += priceList.get(k);
					}
					aList[j] = currentavg / i;
				}

				avgMap.put(i, aList);
			}

			avg3List = avgMap.get(3);
			avg5List = avgMap.get(5);
			avg21List = avgMap.get(21);

			Double initMoney = 100000d, totalMoney = 0d;
			Integer index = 0;
			List<Stock> buyList = new ArrayList<Stock>();
			List<Stock> sellList = new ArrayList<Stock>();

			Double lastPrice = 0d, closePrice = 0d;

			Date sellDate = dataFormat.parse(tradeDateList.get(0));
			for (int i = 21; i < priceList.size() - 1; i++) {
				Date tDate = dataFormat.parse(tradeDateList.get(i));
				if (tDate.before(startTrade))
					continue;

				// if (tDate.before(sellDate))
				// continue;

				lastPrice = priceList.get(i - 1);
				closePrice = priceList.get(i);

				Double last3avg = avg3List[i - 1];
				Double last5avg = avg5List[i - 1];
				Double last21avg = avg21List[i - 1];

				Double current3avg = avg3List[i];
				Double current5avg = avg5List[i];
				Double current21avg = avg21List[i];

				if ((last5avg <= last21avg) && (current5avg > (current21avg+current21avg*0.01)) && current5avg > last5avg) // 符合条件
				{
					// System.err.print("符合条件,交易日:" +
					// tradeDateList.get(i) +
					// ".昨日收盘价="+lastPrice + ",今日收盘价="+closePrice);
					// System.err.print("。昨日21日均线："+last21avg);
					// System.err.println("。今日21日均线："+current21avg);
					Stock stock = new Stock();
					stock.stockCode = stockCode;
					stock.costPrice = openPriceList.get(i+1);
					stock.costMoney = initMoney;
					stock.amount = (int) (initMoney / stock.costPrice);
					stock.tradedate = tradeDateList.get(i + 1);
					stock.index = i + 1;
					buyList.add(stock);
					System.err.println(stock);
					index++;

					for (int k = stock.index + 1; k < priceList.size(); k++) {
						// if (priceList.get(k) * stock.amount >=
						// 11000d) {
						// totalMoney += stock.amount *
						// priceList.get(i);
						// System.err.println("卖出："+ stock.amount *
						// priceList.get(i));
						// break;
						// }
						
						last3avg = avg3List[k];
						last5avg = avg5List[k];
						last21avg = avg21List[k];

						if ((k+1) == priceList.size()) {
							Stock sellStock = new Stock();
							sellStock.stockCode = stockCode;							
							sellStock.costPrice = openPriceList.get(k);
							sellStock.amount = stock.amount;
							sellStock.costMoney = stock.amount * sellStock.costPrice;
							sellStock.tradedate = tradeDateList.get(k);
							totalMoney = sellStock.costMoney;

							initMoney = stock.amount * sellStock.costPrice;
							Double plpercent = (sellStock.costMoney - stock.costMoney)
									/ stock.costMoney;
							 System.err.print("卖出：" + sellStock);
							 System.err.println(".收益率："
							 + df.format(plpercent));
							sellDate = dataFormat.parse(sellStock.tradedate);
							sellList.add(sellStock);
							break;
						}
						
						current3avg = avg3List[k+1];
						current5avg = avg5List[k+1];
						current21avg = avg21List[k+1];

						if ((last5avg > last21avg) && (current5avg < current21avg)) {
							// 均线卖出
							Stock sellStock = new Stock();
							sellStock.stockCode = stockCode;							
							sellStock.costPrice = openPriceList.get(k+2);
							sellStock.amount = stock.amount;
							sellStock.costMoney = stock.amount * sellStock.costPrice;
							sellStock.tradedate = tradeDateList.get(k+2);
							totalMoney = sellStock.costMoney;

							initMoney = stock.amount * sellStock.costPrice;
							Double plpercent = (sellStock.costMoney - stock.costMoney)
									/ stock.costMoney;
							 System.err.print("卖出：" + sellStock);
							 System.err.println(".收益率："
							 + df.format(plpercent));
							sellDate = dataFormat.parse(sellStock.tradedate);
							sellList.add(sellStock);
							break;
						}
//
//						// if (priceList.get(k) <= avg5List[k - 1])
//						// {
//						// //5日均线卖点
//						// Stock sellStock = new Stock();
//						// sellStock.stockCode = stockCode;
//						// sellStock.costPrice = avg5List[k - 1];
//						// sellStock.amount = stock.amount;
//						// sellStock.costMoney = stock.amount
//						// * avg5List[k - 1];
//						// sellStock.tradedate =
//						// tradeDateList.get(k);
//						// totalMoney = sellStock.costMoney;
//						//
//						// initMoney = stock.amount * avgList[k];
//						// Double plpercent = (sellStock.costMoney -
//						// stock.costMoney)
//						// / stock.costMoney;
//						// System.err.print("卖出："+ sellStock);
//						// System.err.println(".收益率：" +
//						// df.format(plpercent));
//						// sellDate = dataFormat
//						// .parse(sellStock.tradedate);
//						// break;
//						// }
					}

				}

				// if (((lastPrice - closePrice)/closePrice) > 0.12) {
				// System.err.println("除权日:" + tradeDateList.get(i) +
				// ".昨日收盘价="+lastPrice + ",今日收盘价="+closePrice);
				// }
			}

			// for (int i = 0; i < avgList.length; i++) {
			// System.err.println("交易日：" + tradeDateList.get(i) +
			// "，21日均线："+
			// avgList[i]);
			// }

			 System.err.println("共进行了" + index + "次买入交易");

			 Double pl = (totalMoney - 100000d) / 100000d;
			 System.err.println("总成本:"
			 + 100000d + ", 总收益：" + df.format(totalMoney) + "元,"
			 + "收益率：" + df.format(pl) + "共进行：" + buyList.size() + "次交易");
			
			// StringBuilder sb = new StringBuilder();
			// sb
			// .append("insert into simluate(stockcode, buyline, sellline, initmoney, totalmoney, plpercent, buycount, sellcount) values('"
			// + stockCode);
			// sb.append("'," + buyline);
			// sb.append("," + sellline);
			// sb.append("," + 100000);
			// sb.append("," + totalMoney);
			// sb.append("," + pl);
			// sb.append("," + buyList.size());
			// sb.append("," + sellList.size() + ")");
			//
			// sql = sb.toString();
			// updateStatement.execute(sql);

			break;
		}

		st.close();
		stStock.close();
		selectStatement.close();
		conn.close();
		conn1.close();
		conn2.close();

	}
}
