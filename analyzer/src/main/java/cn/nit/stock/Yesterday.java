package cn.nit.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.text.SimpleDateFormat;

import cn.nit.stock.model.StockName;
import cn.nit.stock.model.TradeDay;
import com.mongodb.MongoClient;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.mongodb.morphia.Datastore;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Hello world!
 * 
 */
public class Yesterday {

    private static Datastore ds;

    private static MongoClient mongoClient;

    private static MongoOperations mongoOps;

	public static void main(String[] args) throws Exception {

        ds = ConnUtils.getDatastore();
        mongoClient = ConnUtils.getMongo();
        mongoOps = new MongoTemplate(mongoClient, "stock");

        //ResetYesterdayPrice();

        FillYesterdayPrice();
	}

    private static void ResetYesterdayPrice() {
        for (StockName stockName : ds.find(StockName.class).asList()) {

            for(TradeDay tradeDay : mongoOps.findAll(TradeDay.class, stockName.getCode())) {
                tradeDay.setYesterdayPrice(0d);
                mongoOps.save(tradeDay, stockName.getCode());
            }

            break;
        }
    }

    private static void FillYesterdayPrice() throws UnknownHostException {



        for (StockName stockName : ds.find(StockName.class).asList()) {


			String stockcode = stockName.getCode();
			System.err.println(stockName);

            List<TradeDay> list = mongoOps.find(new Query(Criteria.where("yesterdayPrice").lt(0.1)).with(new Sort(Sort.Direction.ASC, "tradedate")), TradeDay.class, stockcode);

			for(int i = 1; i < list.size(); i ++) {
                if (i == list.size() -1) break;

                TradeDay today = list.get(i+1);
                TradeDay yesterday = list.get(i);

                today.setYesterdayPrice(yesterday.getClosePrice());
				//System.err.println(today +"|||" + yesterday);
                mongoOps.save(today, stockcode);
			}
		}
    }
}
