package cn.nit.stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
public class BoardLine {
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
		Statement updateStatement = conn.createStatement();

		String tradeSql = "select tradedate from day where stockcode='1a0001' and tradedate>'2010-01-01' order by tradedate desc";

		ResultSet rsDay = stDay.executeQuery(tradeSql);
		while (rsDay.next()) {

			String tradeDate = df.format(rsDay.getDate("tradedate"));
			System.err.println("交易日：" + tradeDate);
			String boardURL = "http://vis.10jqka.com.cn/topwin/select/?account=geng_ke&passwd=790624&date="
					+ tradeDate;

			Map<String, Integer> boardMap = new HashMap<String, Integer>();
			String sql = "select * from stockboard";

			ResultSet resultSet = stBoard.executeQuery(sql);

			while (resultSet.next()) {
				Integer boardID = resultSet.getInt("pkid");
				String boardName = resultSet.getString("boardname");
				String boardCode = resultSet.getString("boardcode");
				boardMap.put(boardCode, boardID);
			}

			GetMethod method = new GetMethod(boardURL);
			method.addRequestHeader("Content-type", "text/html; charset=utf-8");
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
				System.err.println("Method failed: " + method.getStatusLine());
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

			result = result.substring(result.indexOf("[{"));
			result = result.substring(0, result.indexOf("}]"));
			String[] boards = result.split("}");
			for (String board : boards) {
				board = StringUtils.removeStart(board, ",");
				String[] boardfields = board.split(",");

				String bCode = StringUtils.remove(boardfields[0].split(":")[1],
						'"');
				// System.err.println(bCode);

				String zjlx = StringUtils.remove(boardfields[2].split(":")[1],
						'"');
				// System.err.println(zjlx);

				String jgdlpm1 = StringUtils.remove(
						boardfields[3].split(":")[1], '"');
				// System.err.println(jgdlpm1);
				String jgdlpm3 = StringUtils.remove(
						boardfields[4].split(":")[1], '"');
				// System.err.println(jgdlpm3);
				String jgdlpm5 = StringUtils.remove(
						boardfields[5].split(":")[1], '"');
				// System.err.println(jgdlpm5);
				String jgdlpm10 = StringUtils.remove(
						boardfields[6].split(":")[1], '"');
				// System.err.println(jgdlpm10);

				String jgbyb1 = StringUtils.remove(
						boardfields[7].split(":")[1], '"');
				// System.err.println(jgbyb1);
				String jgbyb3 = StringUtils.remove(
						boardfields[8].split(":")[1], '"');
				// System.err.println(jgbyb3);
				String jgbyb5 = StringUtils.remove(
						boardfields[9].split(":")[1], '"');
				// System.err.println(jgbyb5);
				String jgbyb10 = StringUtils.remove(
						boardfields[10].split(":")[1], '"');
				// System.err.println(jgbyb10);

				String jjchl1 = StringUtils.remove(
						boardfields[11].split(":")[1], '"');
				// System.err.println(jjchl1);
				String jjchl3 = StringUtils.remove(
						boardfields[12].split(":")[1], '"');
				// System.err.println(jjchl3);
				String jjchl5 = StringUtils.remove(
						boardfields[13].split(":")[1], '"');
				// System.err.println(jjchl5);
				String jjchl10 = StringUtils.remove(
						boardfields[14].split(":")[1], '"');
				// System.err.println(jjchl10);

				String ddzpb1 = StringUtils.remove(
						boardfields[15].split(":")[1], '"');
				// System.err.println(ddzpb1);
				String ddzpb3 = StringUtils.remove(
						boardfields[16].split(":")[1], '"');
				// System.err.println(ddzpb3);
				String ddzpb5 = StringUtils.remove(
						boardfields[17].split(":")[1], '"');
				// System.err.println(ddzpb5);
				String ddzpb10 = StringUtils.remove(
						boardfields[18].split(":")[1], '"');
				// System.err.println(ddzpb10);

				String pmaqxs = StringUtils.remove(
						boardfields[19].split(":")[1], '"');
				// System.err.println(pmaqxs);

				String pmylxs = StringUtils.remove(
						boardfields[20].split(":")[1], '"');
				// System.err.println(pmylxs);

				String gjzf1 = StringUtils.remove(
						boardfields[21].split(":")[1], '"');
				// System.err.println(gjzf1);
				String gjzf3 = StringUtils.remove(
						boardfields[22].split(":")[1], '"');
				// System.err.println(gjzf3);
				String gjzf5 = StringUtils.remove(
						boardfields[23].split(":")[1], '"');
				// System.err.println(gjzf5);
				String gjzf10 = StringUtils.remove(
						boardfields[24].split(":")[1], '"');
				// System.err.println(gjzf10);

				Integer boardID = boardMap.get(bCode);

				sql = String
						.format(
								"insert into boardday(boardid, zjlx, jgdlpm1, jgdlpm3, jgdlpm5, jgdlpm10,jgbyb1, jgbyb3, jgbyb5, jgbyb10, jjchl1, jjchl3, jjchl5, jjchl10, ddzpb1, ddzpb3, ddzpb5, ddzpb10, pmaqxs, pmylxs, gjzf1, gjzf3, gjzf5, gjzf10, tradeDate) values(%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,'%s')",
								boardID, zjlx, jgdlpm1, jgdlpm3, jgdlpm5,
								jgdlpm10, jgbyb1, jgbyb3, jgbyb5, jgbyb10,
								jjchl1, jjchl3, jjchl5, jjchl10, ddzpb1,
								ddzpb3, ddzpb5, ddzpb10, pmaqxs, pmylxs, gjzf1,
								gjzf3, gjzf5, gjzf10, tradeDate);

				System.err.println(sql);
				updateStatement.execute(sql);
			}

			// sql = "insert into boardday(boardid,plpercent,tradedate) values("
			// +
			// boardID + ", " + pl + ",'" + tradeDate+ "')";
			// System.err.println(sql);
			// updateStatement.execute(sql);

			// calendar.add(Calendar.DAY_OF_WEEK, 1);

			Thread.sleep(35 * 1000);
		}

		conn.close();

	}
}
