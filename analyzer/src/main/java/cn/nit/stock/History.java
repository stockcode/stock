package cn.nit.stock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Exchanger;

import android.provider.ContactsContract;
import cn.nit.stock.model.StockName;
import cn.nit.stock.model.TradeDay;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.mongodb.morphia.Datastore;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Hello world!
 * 
 */
public class History {

    public static void main(String[] args) throws Exception {
		
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		List<String> tradeDates = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2013,1,1);

        Date startDate = calendar.getTime();
		
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
		// client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
		// "GBK");

        Datastore ds = ConnUtils.getDatastore();

        MongoClient mongoClient = ConnUtils.getMongo();

        MongoOperations mongoOps = new MongoTemplate(mongoClient, "stock");



        for(StockName stockName : ds.find(StockName.class).asList()) {

                String stockcode = stockName.getCode();
                String stockname = stockName.getName();


            if (mongoOps.getCollection(stockcode).count() > 0) continue;

                String stocktype = "sz";

                if(stockcode.startsWith("6"))
                    stocktype = "ss";


            System.err.println("stockcode=" + stockcode);
            // Create a method instance.
            GetMethod method = new GetMethod(
                    "http://table.finance.yahoo.com/table.csv?s=" + stockcode
                            + "." + stocktype);
            method.addRequestHeader("Content-type", "text/html; charset=utf-8");
            // Provide custom retry handler is necessary
            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));

            try {
                // 定义一个输入流
                InputStream ins = null;
                // 定义文件流
                BufferedReader br = null;

                // Execute the method.
                int statusCode = client.executeMethod(method);

                if (statusCode != HttpStatus.SC_OK) {
                    System.err.println("Method failed: "
                            + method.getStatusLine());
                }

                ins = method.getResponseBodyAsStream();

                // 按服务器编码字符集构建文件流，这里的CHARSET要根据实际情况设置
                Boolean firstLine = true;
                br = new BufferedReader(new InputStreamReader(ins, method
                        .getResponseCharSet()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (line.contains("Not Found")) break;

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

                    System.err.println(tradeDay);
                }

            } catch (HttpException e) {
                System.err.println("Fatal protocol violation: "
                        + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Fatal transport error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Release the connection.
                method.releaseConnection();
            }
            }
	}
}
