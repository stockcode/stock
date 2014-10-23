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
import org.springframework.data.mongodb.core.query.Update;

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

        //RemoveInvalidDay();
	}

    private static void RemoveInvalidDay() {
        for (StockName stockName : ds.find(StockName.class).asList()) {

            System.err.println(stockName);

            for(TradeDay tradeDay : mongoOps.findAll(TradeDay.class, stockName.getCode())) {
                if (tradeDay.isInvalid()) {
                    mongoOps.remove(tradeDay, stockName.getCode());
                }
            }
        }
    }

    private static void ResetYesterdayPrice() {
        for (StockName stockName : ds.find(StockName.class).asList()) {

            mongoOps.updateMulti(new Query(Criteria.where("yesterdayPrice").gt(0)), Update.update("yesterdayPrice", 0), TradeDay.class, stockName.getCode());
        }
    }

    private static void FillYesterdayPrice() throws UnknownHostException {



        for (StockName stockName : ds.find(StockName.class).asList()) {


			String stockcode = stockName.getCode();
			System.err.println(stockName);

            List<TradeDay> list = mongoOps.find(new Query(Criteria.where("yesterdayPrice").lt(0.1)).with(new Sort(Sort.Direction.DESC, "tradeDate")), TradeDay.class, stockcode);

			for(int i = 0; i < list.size()-1; i ++) {

                TradeDay today = list.get(i);
                TradeDay yesterday = list.get(i+1);

                today.setYesterdayPrice(yesterday.getClosePrice());
				System.err.println(today +"|||" + yesterday);
                mongoOps.save(today, stockcode);
			}


		}
    }
}
