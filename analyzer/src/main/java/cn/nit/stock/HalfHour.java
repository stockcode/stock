package cn.nit.stock;

import cn.nit.stock.hexin.MN30File;
import cn.nit.stock.hexin.MN30Record;
import cn.nit.stock.indicator.Boll;
import java.io.*;
import java.sql.*;

public class HalfHour {
	private static String loginid = "sunsulian";

	public HalfHour() {
	}

	public static void main(String args[]) throws Exception {
		getData();
		addBoll();
	}

	private static void getData() throws ClassNotFoundException, SQLException,
			FileNotFoundException {
		Connection conn = ConnUtils.getConn();
		Statement st = conn.createStatement();
		Statement statement = conn.createStatement();
		Statement updateStatement = conn.createStatement();
		String sql = "select tradedate from stockaccount where loginid='"
				+ loginid + "' order by tradedate desc limit 0,1";
		ResultSet resultSet = st.executeQuery(sql);
		resultSet.next();
		String chooseDate = resultSet.getString("tradedate");
		resultSet.close();
		sql = "select * from stockaccount where loginid='" + loginid
				+ "' and tradedate='" + chooseDate + "'";

		for (resultSet = st.executeQuery(sql); resultSet.next();) {
			String stockcode = resultSet.getString("stockcode");
			String type = "sznse";
            if(stockcode.startsWith("6"))
                type = "shase";
			RandomAccessFile file = new RandomAccessFile(
					new File((new StringBuilder(
							"C:\\同花顺软件\\同花顺\\history\\"+ type + "\\min5\\"))
							.append(stockcode).append(".mn5").toString()), "r");
			MN30File mn5File = new MN30File();
			MN30File.Read(mn5File, file);
			MN30Record amn30record[];
			int j = (amn30record = mn5File.recordList).length;
			for (int i = 0; i < j; i++) {
				MN30Record record = amn30record[i];
				if (record.getDate() != null) {
					sql = (new StringBuilder(
							"select * from MN5 where stockcode='"))
							.append(stockcode).append("' and tradedate='")
							.append(record.getDate()).append("'").toString();
					ResultSet rs = statement.executeQuery(sql);
					if (!rs.next()) {
						StringBuilder sb = new StringBuilder();
						sb.append("insert into MN5");
						sb.append("(stockcode, open, low, high, close, amount, volume, tradedate) values(");
						sb.append((new StringBuilder("'")).append(stockcode)
								.toString());
						sb.append((new StringBuilder("',")).append(
								record.getOpen()).toString());
						sb.append((new StringBuilder(",")).append(
								record.getLow()).toString());
						sb.append((new StringBuilder(",")).append(
								record.getHigh()).toString());
						sb.append((new StringBuilder(",")).append(
								record.getClose()).toString());
						sb.append((new StringBuilder(",")).append(
								record.getAmount()).toString());
						sb.append((new StringBuilder(",")).append(
								record.getVolume()).toString());
						sb.append((new StringBuilder(",'"))
								.append(record.getDate()).append("')")
								.toString());
						sql = sb.toString();
						System.err.println(sql);
						updateStatement.execute(sql);
					}
				}
			}

		}

		st.close();
		conn.close();
	}

	private static void addBoll() throws ClassNotFoundException, SQLException {
		Connection selectConn = ConnUtils.getConn();
		Statement selectStatement = selectConn.createStatement();
		Statement lineStatement = selectConn.createStatement();
		Statement insertStatement = selectConn.createStatement();
		String sql = "select * from mn5 where boll is null order by tradedate desc";
		for (ResultSet resultSet = selectStatement.executeQuery(sql); resultSet
				.next();) {
			String stockCode = resultSet.getString("stockcode");
			Double currentPrice = Double.valueOf(resultSet.getDouble("close"));
			String tradedate = resultSet.getString("tradedate");
			Double cls[] = new Double[20];
			String ssql = (new StringBuilder(
					"select close from mn5 where stockcode='"))
					.append(stockCode).append("' and tradedate<='")
					.append(tradedate)
					.append("' order by tradedate desc limit 20").toString();
			ResultSet rs = lineStatement.executeQuery(ssql);
			int i;
			for (i = 0; rs.next(); i++)
				cls[i] = Double.valueOf(rs.getDouble("close"));

			if (i >= 20) {
				Boll boll = new Boll(cls);
				System.err
						.println((new StringBuilder(String.valueOf(tradedate)))
								.append("=").append(boll).toString());
				StringBuilder sb = new StringBuilder();
				sb.append("update mn5 set ");
				sb.append((new StringBuilder(" boll=")).append(boll.getBoll())
						.toString());
				sb.append((new StringBuilder(", ub=")).append(boll.getUb())
						.toString());
				sb.append((new StringBuilder(", lb=")).append(boll.getLb())
						.toString());
				sb.append(" where stockcode='").append(stockCode).append("'");
				sb.append(" and tradedate='").append(tradedate).append("'");
				sql = sb.toString();
				insertStatement.execute(sql);
			}
		}

	}
}
