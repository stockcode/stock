package cn.nit.stock;

import cn.nit.stock.hexin.D1BarFile;
import cn.nit.stock.hexin.D1BarRecord;
import cn.nit.stock.repository.D1BarRepository;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.UnknownHostException;
import java.sql.*;

// Referenced classes of package cn.nit.stock:
//            ConnUtils, AddStock

public class AddD1Future
{


    public AddD1Future()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        String[] ins = new String[]{
                "IF1407"};

        for(String code:ins) {
        getData(code);
        }
    }

    private static void getData(String stockcode)
            throws ClassNotFoundException, SQLException, FileNotFoundException, UnknownHostException {

            System.err.println(stockcode);


        MongoClient mongoClient = new MongoClient( "115.28.160.121" );
        DB db = mongoClient.getDB("future");
        DBCollection coll = db.getCollection(stockcode);

        MongoOperations mongoOps = new MongoTemplate(mongoClient, "future");



        //log.info(mongoOps.findOne(new Query(where("name").is("Joe")), Person.class));
                RandomAccessFile file = new RandomAccessFile(new File((new StringBuilder("C:\\同花顺软件\\同花顺\\history\\cffex\\day\\").append(stockcode).append(".day")).toString()), "r");
                D1BarFile d1barFile = new D1BarFile();
                D1BarFile.Read(d1barFile, file);
                D1BarRecord ad1barrecord[];
                int j = (ad1barrecord = d1barFile.recordList).length;
                for(int i = 0; i < j; i++)
                {
                    D1BarRecord record = ad1barrecord[i];
                    if(record.getDate() != null && record.getOpen() > 10)
                    {
                        System.err.println(record);

                        Query searchUserQuery = new Query(Criteria.where("date").is(record.getDate()));
                        if (!mongoOps.exists(searchUserQuery, D1BarRecord.class)) {
                            mongoOps.insert(record, stockcode);
                        }
                    }
                }

        }
    }

