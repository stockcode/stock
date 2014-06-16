package cn.nit.stock;

import cn.nit.stock.hexin.MN5File;
import cn.nit.stock.hexin.MN5Record;
import java.io.*;
import java.sql.*;

public class AddFiveMinute
{

    public AddFiveMinute()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        getData();
    }

    private static void getData()
        throws ClassNotFoundException, SQLException
    {
        Connection conn = ConnUtils.getConn();
        Statement st = conn.createStatement();
        Statement statement = conn.createStatement();
        Statement updateStatement = conn.createStatement();
        String sql = "select * from stock";
        for(ResultSet resultSet = st.executeQuery(sql); resultSet.next();)
        {
            String stockcode = resultSet.getString("code");
            System.err.println(stockcode);
            AddStock.createDatabase(stockcode);
            String type = "sznse";
            if(stockcode.startsWith("6"))
                type = "shase";
            PreparedStatement pstmt = conn.prepareStatement((new StringBuilder("use A")).append(stockcode).toString());
            pstmt.execute();
            try
            {
                RandomAccessFile file = new RandomAccessFile(new File((new StringBuilder("C:\\同花顺软件\\同花顺\\history\\")).append(type).append("\\min5\\").append(stockcode).append(".mn5").toString()), "r");
                MN5File mn5File = new MN5File();
                MN5File.Read(mn5File, file);
                MN5Record amn5record[];
                int j = (amn5record = mn5File.recordList).length;
                for(int i = 0; i < j; i++)
                {
                    MN5Record record = amn5record[i];
                    if(record.getDate() != null)
                    {
                        sql = (new StringBuilder("select * from MN5 where stockcode='")).append(stockcode).append("' and tradedate='").append(record.getDate()).append("'").toString();
                        ResultSet rs = statement.executeQuery(sql);
                        if(!rs.next())
                        {
                            StringBuilder sb = new StringBuilder();
                            sb.append("insert into MN5");
                            sb.append("(stockcode, open, low, high, close, amount, volume, tradedate) values(");
                            sb.append((new StringBuilder("'")).append(stockcode).toString());
                            sb.append((new StringBuilder("',")).append(record.getOpen()).toString());
                            sb.append((new StringBuilder(",")).append(record.getLow()).toString());
                            sb.append((new StringBuilder(",")).append(record.getHigh()).toString());
                            sb.append((new StringBuilder(",")).append(record.getClose()).toString());
                            sb.append((new StringBuilder(",")).append(record.getAmount()).toString());
                            sb.append((new StringBuilder(",")).append(record.getVolume()).toString());
                            sb.append((new StringBuilder(",'")).append(record.getDate()).append("')").toString());
                            sql = sb.toString();
                            updateStatement.execute(sql);
                        }
                    }
                }

            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        st.close();
        conn.close();
    }
}
