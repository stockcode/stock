package cn.nit.stock;

import cn.nit.stock.dao.StockNameRepository;
import cn.nit.stock.model.StockName;
import cn.nit.stock.model.TradeDay;
import com.mongodb.MongoClient;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.io.FileUtils;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Hello world!
 */
public class History implements CommandLineRunner {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(History.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        Boolean firstLine = true;
        DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<String> tradeDates = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2013, 1, 1);

        Date startDate = calendar.getTime();

        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();
        // client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
        // "GBK");

        Datastore ds = ConnUtils.getDatastore();

        MongoClient mongoClient = ConnUtils.getMongo();

        MongoOperations mongoOps = new MongoTemplate(mongoClient, "stock");


        for (StockName stockName : ds.find(StockName.class).asList()) {

            String stockcode = stockName.getCode();


            if (mongoOps.getCollection(stockcode).count() > 0) continue;

            String stocktype = "sz";

            if (stockcode.startsWith("6"))
                stocktype = "ss";


            System.err.println(stockName);

            String url = "http://table.finance.yahoo.com/table.csv?s=" + stockcode + "." + stocktype;

            File tempFile = new File("d:\\future\\data\\" + stockcode + ".csv");

            try {
                FileUtils.copyURLToFile(new URL(url), tempFile, 30000, 30000);
            } catch (IOException e) {
                System.err.println(e);
                continue;
            }

            firstLine = true;

            for (String line : FileUtils.readLines(tempFile)) {

                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] results = line.split(",");

                String tradedate = results[0];

                Date tDate = dataFormat.parse(tradedate);

                if (tDate.before(startDate)) continue;

                //if (!tradeDates.contains(tradedate)) continue;

                Double openprice = Double.parseDouble(results[1]);
                Double highprice = Double.parseDouble(results[2]);
                Double lowprice = Double.parseDouble(results[3]);
                Double closeprice = Double.parseDouble(results[4]);
                //String volume = results[5];


                TradeDay tradeDay = new TradeDay(0d, openprice, closeprice, highprice, lowprice, stockcode, tradedate);

                mongoOps.insert(tradeDay, stockcode);

                //System.err.println(tradeDay);
            }


        }
    }
}
