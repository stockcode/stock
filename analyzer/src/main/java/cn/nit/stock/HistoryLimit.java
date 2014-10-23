package cn.nit.stock;

import cn.nit.stock.model.StockLimit;
import cn.nit.stock.model.StockName;
import cn.nit.stock.model.TradeDay;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;


/**
 * Hello world!
 *
 */
public class HistoryLimit 
{
    private static Datastore ds;

    private static MongoClient mongoClient;

    private static MongoOperations mongoOps;

    public static void main( String[] args ) throws Exception
    {
        ds = ConnUtils.getDatastore();
        mongoClient = ConnUtils.getMongo();
        mongoOps = new MongoTemplate(mongoClient, "stock");

        for (StockName stockName : ds.find(StockName.class).asList()) {


            String stockcode = stockName.getCode();
            System.err.println(stockName);



            List<TradeDay> list = mongoOps.findAll(TradeDay.class, stockcode);
    		
    		for(TradeDay tradeDay : list) {
    		
    		
              if (tradeDay.getYesterdayPrice() * 1.1 - tradeDay.getClosePrice() < 0.01) {

            	  System.err.println("代码：" + stockcode + ",名称：" + stockName.getName() + ",涨停日期：" + tradeDay.getTradeDate());

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
    }
}
