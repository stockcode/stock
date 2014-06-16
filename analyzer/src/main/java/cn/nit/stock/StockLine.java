package cn.nit.stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;

/**
 * Hello world!
 * 
 */
public class StockLine {
	public static void main(String[] args) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
		// client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
		// "GBK");

		String url = "jdbc:jtds:sqlserver://218.28.139.40:4433/stock";
		String driver = "net.sourceforge.jtds.jdbc.Driver";
		java.sql.Connection conn;

		Class.forName(driver);
		conn = DriverManager.getConnection(url, "sa", "chrdw,hdhxt.");
		Statement stStock = conn.createStatement();
		Statement stBoard = conn.createStatement();
		Statement stDay = conn.createStatement();
		Statement stLine = conn.createStatement();
		Statement updateStatement = conn.createStatement();

		Map<String, Integer> stockMap = new HashMap<String, Integer>();
		List<String> boardList = new ArrayList<String>();

		String sql = "select * from stockboard";

		ResultSet resultSet = stBoard.executeQuery(sql);

		while (resultSet.next()) {
			boardList.add(resultSet.getString("boardcode"));
		}

		sql = "select * from stock";

		resultSet = stBoard.executeQuery(sql);

		while (resultSet.next()) {
			Integer stockID = resultSet.getInt("pkid");
			String stockCode = resultSet.getString("code");
			stockMap.put(stockCode, stockID);
		}

		String tradeSql = "select tradedate from day where stockcode='1a0001' and tradedate>'2010-01-01' and tradedate<='2010-07-23' order by tradedate desc";

		ResultSet rsDay = stDay.executeQuery(tradeSql);
		while (rsDay.next()) {

			String tradeDate = df.format(rsDay.getDate("tradedate"));
			System.err.println("交易日：" + tradeDate);

			for (String boardCode : boardList) {
				System.err.println("板块代码：" + boardCode);

				String boardURL = "http://vis.10jqka.com.cn/topwin/select/?do=block_stock&tradecode="
						+ boardCode
						+ "&account=geng_ke&passwd=790624&date="
						+ tradeDate;

				GetMethod method = new GetMethod(boardURL);
				method.addRequestHeader("Content-type",
						"text/html; charset=utf-8");
				// Provide custom retry handler is necessary
				method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
						new DefaultHttpMethodRetryHandler(3, false));

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
				String charset = method.getResponseCharSet();
				if (charset.toUpperCase().equals("ISO-8859-1")) {
					charset = "gbk";
				}
				// 按服务器编码字符集构建文件流，这里的CHARSET要根据实际情况设置
				br = new BufferedReader(new InputStreamReader(ins, method
						.getResponseCharSet()));
				StringBuffer sbf = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					sbf.append(line);
				}
				String result = new String(sbf.toString().getBytes(
						method.getResponseCharSet()), charset);
				// 输出内容

				result = result.substring(result.indexOf("[["));
				result = result.substring(0, result.indexOf("]]"));
				String[] boards = result.split("]");
				for (String board : boards) {
					board = StringUtils.removeStart(board, ",");
					board = StringUtils.remove(board, "[");
					String[] boardfields = board.split(",");

					String stockCode = StringUtils.remove(boardfields[0], '"');
					// System.err.println(bCode);

					String currentPrice = handleField(boardfields, 2);

					String ddje = handleField(boardfields, 3);

					String shsl = handleField(boardfields, 4);

					String mgyl = handleField(boardfields, 5);

					String syl = handleField(boardfields, 6);

					String ltag = handleField(boardfields, 7);

					String jgdlpm1 = handleField(boardfields, 8);
					// System.err.println(jgdlpm1);
					String jgdlpm3 = handleField(boardfields, 9);
					// System.err.println(jgdlpm3);
					String jgdlpm5 = handleField(boardfields, 10);
					// System.err.println(jgdlpm5);
					String jgdlpm10 = handleField(boardfields, 11);
					// System.err.println(jgdlpm10);

					String jgbyb1 = handleField(boardfields, 12);
					// System.err.println(jgbyb1);
					String jgbyb3 = handleField(boardfields, 13);
					// System.err.println(jgbyb3);
					String jgbyb5 = handleField(boardfields, 14);
					// System.err.println(jgbyb5);
					String jgbyb10 = handleField(boardfields, 15);
					// System.err.println(jgbyb10);

					String jgchl1 = handleField(boardfields, 16);
					// System.err.println(jjchl1);
					String jgchl3 = handleField(boardfields, 17);
					// System.err.println(jjchl3);
					String jgchl5 = handleField(boardfields, 18);
					// System.err.println(jjchl5);
					String jgchl10 = handleField(boardfields, 19);
					// System.err.println(jjchl10);

					String ddzpb1 = handleField(boardfields, 20);
					// System.err.println(ddzpb1);
					String ddzpb3 = handleField(boardfields, 21);
					// System.err.println(ddzpb3);
					String ddzpb5 = handleField(boardfields, 22);
					// System.err.println(ddzpb5);
					String ddzpb10 = handleField(boardfields, 23);
					// System.err.println(ddzpb10);

					String pmaqxs = handleField(boardfields, 24);
					// System.err.println(pmaqxs);

					String pmylxs = handleField(boardfields, 25);
					// System.err.println(pmylxs);

					String hs1 = handleField(boardfields, 26);
					// System.err.println(ddzpb1);
					String hs3 = handleField(boardfields, 27);
					// System.err.println(ddzpb3);
					String hs5 = handleField(boardfields, 28);
					// System.err.println(ddzpb5);
					String hs10 = handleField(boardfields, 29);
					// System.err.println(ddzpb10);

					String gjzf1 = handleField(boardfields, 30);
					// System.err.println(gjzf1);
					String gjzf3 = handleField(boardfields, 31);
					// System.err.println(gjzf3);
					String gjzf5 = handleField(boardfields, 32);
					// System.err.println(gjzf5);
					String gjzf10 = handleField(boardfields, 33);
					// System.err.println(gjzf10);

					Integer stockID = stockMap.get(stockCode);

					if (stockID == null) {
						String stockType = stockCode.startsWith("0") ? "sz"
								: "sh";
						sql = "insert into stock(code,type) values('"
								+ stockCode + "', '" + stockType + "')";
						System.err.println(sql);
						updateStatement.execute(sql);

						sql = "select * from stock where code='" + stockCode
								+ "'";

						resultSet = stStock.executeQuery(sql);
						while (resultSet.next()) {
							stockID = resultSet.getInt("pkid");
						}
						stockMap.put(stockCode, stockID);
					}

					sql = "select * from stockline where stockid=" + stockID
							+ " and tradedate='" + tradeDate + "'";

					ResultSet rsLine = stLine.executeQuery(sql);

					if (rsLine.next())
						continue;

					sql = String
							.format(
									"insert into stockline(stockid, boardcode, currentprice, ddje, shsl, mgyl, syl, ltag, jgdlpm1, jgdlpm3, jgdlpm5, jgdlpm10,jgbyb1, jgbyb3, jgbyb5, jgbyb10, jgchl1, jgchl3, jgchl5, jgchl10, ddzpb1, ddzpb3, ddzpb5, ddzpb10, pmaqxs, pmylxs, hs1, hs3, hs5, hs10, gjzf1, gjzf3, gjzf5, gjzf10, tradeDate) values(%d,'%s',%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,'%s')",
									stockID, boardCode, currentPrice, ddje,
									shsl, mgyl, syl, ltag, jgdlpm1, jgdlpm3,
									jgdlpm5, jgdlpm10, jgbyb1, jgbyb3, jgbyb5,
									jgbyb10, jgchl1, jgchl3, jgchl5, jgchl10,
									ddzpb1, ddzpb3, ddzpb5, ddzpb10, pmaqxs,
									pmylxs, hs1, hs3, hs5, hs10, gjzf1, gjzf3,
									gjzf5, gjzf10, tradeDate);

					System.err.println(sql);
					updateStatement.execute(sql);
				}
				Thread.sleep(35 * 1000);
			}
		}

		conn.close();

	}

	private static String handleField(String[] boardfields, int i) {
		String field = StringUtils.remove(boardfields[i], '"');
		if (StringUtils.isEmpty(field))
			field = "0";
		return field;
	}
}
