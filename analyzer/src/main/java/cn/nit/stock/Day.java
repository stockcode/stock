package cn.nit.stock;

import java.io.*;
import java.sql.*;
import java.util.*;

import cn.nit.stock.model.StockLimit;
import cn.nit.stock.model.StockName;
import cn.nit.stock.model.TradeDay;
import com.mongodb.MongoClient;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.mongodb.morphia.Datastore;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

public class Day
{

    private static Datastore ds;

    private static MongoClient mongoClient;

    private static MongoOperations mongoOps;

    public static void main( String[] args ) throws Exception {
        ds = ConnUtils.getDatastore();
        mongoClient = ConnUtils.getMongo();
        mongoOps = new MongoTemplate(mongoClient, "stock");

        List<String> limit = new ArrayList<String>();

        for (StockName stockName : ds.find(StockName.class).asList()) {
            System.err.println(stockName);
            addD1Bar(stockName);
        }

    }

    private static void addD1Bar(StockName stockName)
        throws ClassNotFoundException, SQLException, HttpException, IOException
    {
        String stockcode = stockName.getCode();

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
        String volume;
        String amount;
        String tradedate;

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

        Double openprice = Double.parseDouble(results[1]);
        Double yesterdayprice = Double.parseDouble(results[2]);
        Double closeprice = Double.parseDouble(results[3]);
        Double highprice = Double.parseDouble(results[4]);
        Double lowprice = Double.parseDouble(results[5]);

        TradeDay tradeDay = new TradeDay(yesterdayprice, openprice, closeprice, highprice, lowprice, stockcode, tradedate);

        System.err.println(tradeDay);

        mongoOps.insert(tradeDay, stockcode);

        if(yesterdayprice * 1.1 - closeprice < 0.01)
        {
            System.err.println((new StringBuilder("股票代码：")).append(stockcode).append(",名称：").append(name).append(",涨停日期：").append(tradedate).toString());

            StockLimit stockLimit = new StockLimit();

            stockLimit.setStockName(stockName.getName());
            stockLimit.setStockCode(stockcode);
            stockLimit.setYesterdayPrice(tradeDay.getYesterdayPrice());
            stockLimit.setClosePrice(tradeDay.getClosePrice());
            stockLimit.setOpenPrice(tradeDay.getOpenPrice());
            stockLimit.setLowPrice(tradeDay.getLowPrice());
            stockLimit.setHighPrice(tradeDay.getHighPrice());
            stockLimit.setLimitDate(tradeDay.getTradeDate());

            mongoOps.save(stockLimit);
        }
    }
}
