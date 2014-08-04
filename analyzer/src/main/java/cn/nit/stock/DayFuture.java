package cn.nit.stock;

import cn.nit.stock.hexin.D1BarRecord;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DayFuture
{

    public DayFuture()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        mongoClient = new MongoClient( "115.28.160.121" );
        mongoOps = new MongoTemplate(mongoClient, "future");

        DBCollection collection = mongoOps.getCollection("Instrument");

        DBCursor cursor = collection.find();
        try {
            while(cursor.hasNext()) {
                DBObject object = cursor.next();
                D1BarRecord record = null;

                String instrumentID = object.get("InstrumentID").toString();

                String code = instrumentID;

                if (!StringUtils.isNumeric(instrumentID.substring(instrumentID.length()-4)))
                {
                    code = instrumentID.substring(0, instrumentID.length() - 3) + "1" + instrumentID.substring(instrumentID.length() - 3);
                }

                if (instrumentID.startsWith("IF")) {
                    code = "CFF_RE_" + instrumentID;
                    record = addIFD1Bar(code.toUpperCase());
                }
                else {
                    //record = addD1Bar(code.toUpperCase());
                    continue;
                }

                object.put("Volume", record.getVolume());

                collection.save(object);

                Query searchUserQuery = new Query(Criteria.where("date").is(record.getDate()));
                if (!mongoOps.exists(searchUserQuery, D1BarRecord.class, instrumentID)) {
                    mongoOps.insert(record, instrumentID);
                } else {
                    System.err.println("已存在");
                }


            }
        } finally {
            cursor.close();
        }
    }

    private static D1BarRecord addIFD1Bar(String code)
        throws ClassNotFoundException, HttpException, IOException
    {
        Connection conn;
        HttpClient client;
        GetMethod method;
        //conn = ConnUtils.getConn((new StringBuilder("a")).append(stockcode).toString());
        client = new HttpClient();
        String stocktype = "sz";
        method = new GetMethod((new StringBuilder("http://hq.sinajs.cn/list=")).append(code).toString());

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
        System.out.println((new StringBuilder(String.valueOf(code))).append("=").append(results[0]).toString());
        name = results[0].trim();


        D1BarRecord record = new D1BarRecord();

        record.setOpen(Double.parseDouble(results[0]));
        record.setHigh(Double.parseDouble(results[1]));
        record.setLow(Double.parseDouble(results[2]));
        record.setClose(Double.parseDouble(results[3]));
        record.setVolume(Integer.parseInt(results[4]));
        record.setAmount(Double.parseDouble(results[5]));
        record.setDate(results[36].replaceAll("-", ""));

        System.err.println(code + ":" +record);

        return record;
    }

    private static D1BarRecord addD1Bar(String code)
            throws ClassNotFoundException, HttpException, IOException
    {


        Connection conn;
        HttpClient client;
        GetMethod method;
        //conn = ConnUtils.getConn((new StringBuilder("a")).append(stockcode).toString());
        client = new HttpClient();




        method = new GetMethod((new StringBuilder("http://hq.sinajs.cn/list=")).append(code).toString());

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
        System.out.println((new StringBuilder(String.valueOf(code))).append("=").append(results[0]).toString());
        name = results[0].trim();
        if(name.equals(""))
        {
            System.err.println(code + "不存在");
            method.releaseConnection();
            return null;
        }

        D1BarRecord record = new D1BarRecord();

        record.setOpen(Double.parseDouble(results[2]));
        record.setHigh(Double.parseDouble(results[3]));
        record.setLow(Double.parseDouble(results[4]));
        record.setClose(Double.parseDouble(results[8]));
        record.setAmount(Double.parseDouble(results[13]));
        record.setVolume(Integer.parseInt(results[14]));
        record.setDate(results[17].replaceAll("-", ""));

        System.err.println(code + ":" +record);


        return record;
    }

    private static MongoClient mongoClient ;
    private static MongoOperations mongoOps;

}
