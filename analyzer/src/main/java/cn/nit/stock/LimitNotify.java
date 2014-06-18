package cn.nit.stock;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimitNotify
{

    public LimitNotify()
    {
    }

    private static void buildStockList()
        throws ClassNotFoundException, SQLException
    {
        Connection conn = ConnUtils.getConn("stock");
        Statement st = conn.createStatement();
        String sql = "select * from stock";
        for(ResultSet rs = st.executeQuery(sql); rs.next(); stockList.add(rs.getString("code")));
        sql = "select distinct limitdate from stocklimit order by limitdate desc limit 0, 3";

        String limitdate = "";
        for(ResultSet rs = st.executeQuery(sql); rs.next(); limitdate = rs.getString("limitdate"));

        sql = "select stockcode, highprice from stocklimit where limitdate >= '" + limitdate + "' order by limitdate desc";

        for(ResultSet rs = st.executeQuery(sql); rs.next(); limitList.put(rs.getString("stockcode"), rs.getDouble("highprice")));
        st.close();
        conn.close();
    }

    public static void main(String args[])
        throws Exception
    {
        buildStockList();
        stockConn = ConnUtils.getConn("stock");
        
        for (String stockcode : stockList) {
			addD1Bar(stockcode);
        }


        FileUtils.writeStringToFile(new File("D:\\new_jyplug\\T0002\\blocknew\\ZTB.blk"), limit.toString());
        System.err.println(limit.toString());
    }

    private static void addD1Bar(String stockcode)
        throws ClassNotFoundException, SQLException, HttpException, IOException
    {
        Connection conn;
        HttpClient client;
        GetMethod method;
        //conn = ConnUtils.getConn((new StringBuilder("a")).append(stockcode).toString());
        client = new HttpClient();
        String stocktype = "sz";
        if(stockcode.startsWith("6"))
            stocktype = "sh";
        method = new GetMethod((new StringBuilder("http://hq.sinajs.cn/list=")).append(stocktype).append(stockcode).toString());
        method.addRequestHeader("Content-type", "text/html; charset=utf-8");
        method.getParams().setParameter("http.method.retry-handler", new DefaultHttpMethodRetryHandler(3, false));
        String results[];
        String name;
        InputStream ins = null;
        BufferedReader br = null;
        int statusCode = client.executeMethod(method);
        if(statusCode != 200)
            System.err.println((new StringBuilder("Method failed: ")).append(method.getStatusLine()).toString());
        ins = method.getResponseBodyAsStream();
        String charset = method.getResponseCharSet();
        if(charset.toUpperCase().equals("ISO-8859-1"))
            charset = "gbk";
        br = new BufferedReader(new InputStreamReader(ins, method.getResponseCharSet()));
        StringBuffer sbf = new StringBuffer();
        for(String line = null; (line = br.readLine()) != null;)
            sbf.append(line);

        String result = new String(sbf.toString().getBytes(method.getResponseCharSet()), charset);
        results = result.split("\"");
        results = results[1].split(",");
        System.out.println((new StringBuilder(String.valueOf(stockcode))).append("=").append(results[0]).toString());
        name = results[0].trim();
        if(name.equals(""))
        {
            method.releaseConnection();
            return;
        }
        String openprice;
        String yesterdayprice;
        String closeprice;
        String highprice;
        String lowprice;
        String volume;
        String amount;
        String tradedate;
        openprice = results[1];
        yesterdayprice = results[2];
        closeprice = results[3];
        highprice = results[4];
        lowprice = results[5];
        volume = results[8];
        amount = results[9];
        tradedate = results[30];
        if(volume.equals("0"))
        {
            method.releaseConnection();
            return;
        }

        Double yprice = Double.valueOf(Double.parseDouble(yesterdayprice));
        Double closePrice = Double.valueOf(Double.parseDouble(closeprice));
        Double highPrice = Double.valueOf(Double.parseDouble(highprice));
        Double lowPrice = Double.parseDouble(lowprice);

        if(yprice * 1.1 - highPrice < 0.01)
        {
            Statement limitSt = stockConn.createStatement();

            String sql = "select * from stocklimit where stockcode='" + stockcode + "' and limitdate='" + tradedate + "'";
            ResultSet rs = limitSt.executeQuery(sql);

            System.err.println((new StringBuilder("股票代码：")).append(stockcode).append(",名称：").append(name).append(",涨停日期：").append(tradedate).toString());
            if (rs.next()) {
                System.err.println("已存在");
            } else {

                StringBuilder sb = new StringBuilder();
                sb.append("insert into stocklimit ");


                sb.append((new StringBuilder("(stockname, stockcode, yesterdayprice, openprice, lowprice, highprice, closeprice, limitdate) values('")).append(name).toString());
                sb.append((new StringBuilder("','")).append(stockcode).toString());
                sb.append((new StringBuilder("',")).append(yprice).toString());
                sb.append((new StringBuilder(",")).append(openprice).toString());
                sb.append((new StringBuilder(",")).append(lowprice).toString());
                sb.append((new StringBuilder(",")).append(highprice).toString());
                sb.append((new StringBuilder(",")).append(closePrice).toString());
                sb.append((new StringBuilder(",'")).append(tradedate).append("')").toString());

                sql = sb.toString();
                limitSt.execute(sql);
            }
        }

        if (limitList.containsKey(stockcode)) {
            Double yHighPrice = limitList.get(stockcode);
            if (yHighPrice * 0.95 - lowPrice > 0.01) {
                if (stockcode.startsWith("6"))
                    limit.append("1" + stockcode + "\r\n");
                else
                    limit.append("0" + stockcode + "\r\n");
            }
        }
    }

    private static List<String> stockList = new ArrayList<String>();
    private static Map<String, Double> limitList = new HashMap<String, Double>();
    private static Connection stockConn;
    private static StringBuilder limit = new StringBuilder();
}
