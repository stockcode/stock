package cn.nit.stock;

import cn.nit.stock.hexin.D1BarFile;
import cn.nit.stock.hexin.D1BarRecord;
import java.io.*;
import java.sql.*;

// Referenced classes of package cn.nit.stock:
//            ConnUtils, AddStock

public class AddD1Bar
{

    public AddD1Bar()
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
                RandomAccessFile file = new RandomAccessFile(new File((new StringBuilder("C:\\同花顺软件\\同花顺\\history\\")).append(type).append("\\day\\").append(stockcode).append(".day").toString()), "r");
                D1BarFile d1barFile = new D1BarFile();
                D1BarFile.Read(d1barFile, file);
                D1BarRecord ad1barrecord[];
                int j = (ad1barrecord = d1barFile.recordList).length;
                for(int i = 0; i < j; i++)
                {
                    D1BarRecord record = ad1barrecord[i];
                    if(record.getDate() != null)
                    {
                        sql = (new StringBuilder("select * from day where stockcode='")).append(stockcode).append("' and tradedate='").append(record.getDate()).append("'").toString();
                        ResultSet rs = statement.executeQuery(sql);
                        if(!rs.next())
                        {
                            StringBuilder sb = new StringBuilder();
                            sb.append("insert into day");
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
                            System.err.println(sql);
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
