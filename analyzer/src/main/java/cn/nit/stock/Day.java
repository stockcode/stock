package cn.nit.stock;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class Day
{

    public Day()
    {
    }

    private static void buildStockList()
        throws ClassNotFoundException, SQLException
    {
        Connection conn = ConnUtils.getConn("stock");
        Statement st = conn.createStatement();
        String sql = "select * from stock";
        for(ResultSet rs = st.executeQuery(sql); rs.next(); stockList.add(rs.getString("code")));
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
        if(name.equals("上证指数"))
            stockcode = "1A0001";
        StringBuilder sb = new StringBuilder();
        sb.append((new StringBuilder("insert into day(stockcode, open, high, low, close, volume, amount, tradedate) values('")).append(stockcode).toString());
        sb.append((new StringBuilder("',")).append(openprice).toString());
        sb.append((new StringBuilder(",")).append(highprice).toString());
        sb.append((new StringBuilder(",")).append(lowprice).toString());
        sb.append((new StringBuilder(",")).append(closeprice).toString());
        sb.append((new StringBuilder(",")).append(volume).toString());
        sb.append((new StringBuilder(",")).append(amount).toString());
        sb.append((new StringBuilder(",'")).append(tradedate).append("')").toString());
        String sql = sb.toString();
        //System.err.println(sql);
        //Statement st = conn.createStatement();
        //st.execute(sql);
        Double yprice = Double.valueOf(Double.parseDouble(yesterdayprice));
        Double closePrice = Double.valueOf(Double.parseDouble(closeprice));
        Double highPrice = Double.valueOf(Double.parseDouble(highprice));
        if(yprice * 1.1 - highPrice < 0.01)
        {
            System.err.println((new StringBuilder("股票代码：")).append(stockcode).append(",名称：").append(name).append(",涨停日期：").append(tradedate).toString());
            sb = new StringBuilder();
            sb.append("insert into ");
            if(highPrice - closePrice > 0.01)
                sb.append("stocknotlimit");
            else
                sb.append("stocklimit");
            sb.append((new StringBuilder("(stockname, stockcode, yesterdayprice, openprice, lowprice, highprice, closeprice, limitdate) values('")).append(name).toString());
            sb.append((new StringBuilder("','")).append(stockcode).toString());
            sb.append((new StringBuilder("',")).append(yprice).toString());
            sb.append((new StringBuilder(",")).append(openprice).toString());
            sb.append((new StringBuilder(",")).append(lowprice).toString());
            sb.append((new StringBuilder(",")).append(highprice).toString());
            sb.append((new StringBuilder(",")).append(closePrice).toString());
            sb.append((new StringBuilder(",'")).append(tradedate).append("')").toString());
            Statement limitSt = stockConn.createStatement();
            sql = sb.toString();
            limitSt.execute(sql);
        }
    }

    private static List<String> stockList = new ArrayList<String>();
    private static Connection stockConn;

}
