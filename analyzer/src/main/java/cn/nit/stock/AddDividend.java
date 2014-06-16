package cn.nit.stock;

import cn.nit.stock.hexin.ComplexIndexBlock;
import cn.nit.stock.hexin.ComplexIndexRecord;
import cn.nit.stock.hexin.DividendFile;
import cn.nit.stock.hexin.DividendRecord;
import java.io.*;
import java.sql.*;
import org.apache.commons.lang3.StringUtils;

public class AddDividend
{

    public AddDividend()
    {
    }

    public static void main(String args[])
        throws Exception
    {
        Connection conn = ConnUtils.getConn("stock");
        Statement st = conn.createStatement();
        Statement statement = conn.createStatement();
        Statement updateStatement = conn.createStatement();
        RandomAccessFile file = new RandomAccessFile(new File("C:\\同花顺软件\\同花顺\\finance\\权息资料.财经"), "r");
        DividendFile dividendFile = new DividendFile();
        DividendFile.Read(dividendFile, file);
        ComplexIndexRecord acomplexindexrecord[];
        int k = (acomplexindexrecord = dividendFile.getBlock().RecordList).length;
        for(int j = 0; j < k; j++)
        {
            ComplexIndexRecord record = acomplexindexrecord[j];
            int recordNumber = -1;
            String stockcode = record.getSymbol();
            String sql = (new StringBuilder("select * from dividendindex where symbol='")).append(stockcode).append("'").toString();
            ResultSet rs = st.executeQuery(sql);
            if(rs.next())
                recordNumber = rs.getInt("recordNumber");
            if(recordNumber != record.getRecordNumber())
            {
                sql = (new StringBuilder("delete from dividendindex where symbol='")).append(stockcode).append("'").toString();
                st.execute(sql);
                StringBuilder sb = new StringBuilder();
                sb.append("insert into dividendindex");
                sb.append("(market, symbol, freeNumber, position, recordNumber) values(");
                sb.append(record.getMarket());
                sb.append((new StringBuilder(",'")).append(record.getSymbol()).toString());
                sb.append((new StringBuilder("',")).append(record.getFreeNumber()).toString());
                sb.append((new StringBuilder(",")).append(record.getPosition()).toString());
                sb.append((new StringBuilder(",")).append(record.getRecordNumber()).append(")").toString());
                sql = sb.toString();
                System.err.println(sql);
                st.execute(sql);
                for(int i = 0; i < record.getRecordNumber(); i++)
                {
                    DividendRecord dividendRecord = dividendFile.getRecordList()[record.getPosition() + i];
                    if(!StringUtils.isEmpty(dividendRecord.getDate()))
                    {
                        sb = new StringBuilder();
                        sb.append("insert into dividend");
                        sb.append("(stockcode, date, w1, exdividendDate, cash, split, bonus, dispatch, price, registerDate, listingDate, description) values('");
                        sb.append(stockcode);
                        sb.append((new StringBuilder("','")).append(dividendRecord.getDate()).toString());
                        sb.append((new StringBuilder("',")).append(dividendRecord.getM_W1()).toString());
                        sb.append((new StringBuilder(",'")).append(dividendRecord.getExdividendDate()).toString());
                        sb.append((new StringBuilder("',")).append(dividendRecord.getCash()).toString());
                        sb.append((new StringBuilder(",")).append(dividendRecord.getSplit()).toString());
                        sb.append((new StringBuilder(",")).append(dividendRecord.getBonus()).toString());
                        sb.append((new StringBuilder(",")).append(dividendRecord.getDispatch()).toString());
                        sb.append((new StringBuilder(",")).append(dividendRecord.getPrice()).toString());
                        sb.append((new StringBuilder(",'")).append(dividendRecord.getRegisterDate()).toString());
                        sb.append((new StringBuilder("','")).append(dividendRecord.getListingDate()).toString());
                        sb.append((new StringBuilder("','")).append(dividendRecord.getDescription()).append("')").toString());
                        sql = sb.toString();
                        System.err.println(sql);
                        st.execute(sql);
                    }
                }

            }
        }

    }
}
