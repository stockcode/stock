package cn.nit.stock;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

import cn.nit.stock.model.StockLimit;
import cn.nit.stock.model.StockName;
import cn.nit.stock.model.TradeDay;
import com.mongodb.MongoClient;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.mongodb.morphia.Datastore;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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

        SuccessiveLimit successiveLimit = new SuccessiveLimit();

        successiveLimit.main(args);

    }

    private static void addD1Bar(StockName stockName)
        throws ClassNotFoundException, SQLException, HttpException, IOException
    {
        String stockcode = stockName.getCode();

        String stocktype = "sz";
        if(stockcode.startsWith("6"))
            stocktype = "sh";


        URL url = new URL(new StringBuilder("http://hq.sinajs.cn/list=").append(stocktype).append(stockcode).toString());

        String result = IOUtils.toString(url,"GBK");
        String results[] = result.split("\"");
        results = results[1].split(",");
        System.out.println((new StringBuilder(String.valueOf(stockcode))).append("=").append(results[0]).toString());
        String name = results[0].trim();
        if(name.equals(""))
        {
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

        if (!mongoOps.exists((new Query(Criteria.where("tradeDate").is(tradedate))), TradeDay.class, stockcode)) {
            mongoOps.insert(tradeDay, stockcode);
        } else {
            System.err.println("已存在");
        }

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

            if (!mongoOps.exists((new Query(Criteria.where("limitDate").is(tradedate))), StockLimit.class)) {
                mongoOps.save(stockLimit);
            }
        }
    }
}
