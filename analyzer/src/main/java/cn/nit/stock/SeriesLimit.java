package cn.nit.stock;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Hello world!
 * 
 */
public class SeriesLimit {

    public static void main(String[] args) throws Exception {
		
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		List<String> tradeDates = new ArrayList<String>();
				
		tradeDates.add("2011-04-01");


        Iterator<File> iter = FileUtils.iterateFiles(new File("c:\\stockdata"), new String[]{"csv"}, true);

		while (iter.hasNext()) {
            File stockFile = iter.next();

			List<String> lines = FileUtils.readLines(stockFile);
					

            for(int i = lines.size() - 1 ; i > 0; i --) {
					String[] results = lines.get(i).split(",");

					String tradedate = results[0];

					//if (!tradeDates.contains(tradedate)) continue;
					
					String openprice = results[1];
					String highprice = results[2];
					String lowprice = results[3];
					String closeprice = results[4];
					String volume = results[5];

                Double closePrice = Double.parseDouble(closeprice);


            }



		}


	}
}
