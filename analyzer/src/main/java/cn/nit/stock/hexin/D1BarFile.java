package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;

public class D1BarFile
{

    public FileHeader header;
    public ColumnHeader columnList[];
    public D1BarRecord recordList[];
    public static int LENGTH = 4;

    
    public D1BarFile()
    {
        header = new FileHeader();
    }

    public static Boolean Read(D1BarFile d1BarFile, RandomAccessFile file)
    {
        if(file == null)
            return Boolean.valueOf(false);
        try
        {
            file.seek(0L);
            FileHeader.Read(d1BarFile.header, file);
            int FPosition = FileHeader.LENGTH;
            d1BarFile.columnList = new ColumnHeader[d1BarFile.header.getFieldCount()];
            for(int dwI = 0; dwI < d1BarFile.header.getFieldCount(); dwI++)
            {
                d1BarFile.columnList[dwI] = new ColumnHeader();
                file.seek(FPosition + ColumnHeader.LENGTH * dwI);
                ColumnHeader.Read(d1BarFile.columnList[dwI], file);
            }

            FPosition = d1BarFile.header.getHeaderLength();
            file.seek(FPosition);
            d1BarFile.recordList = new D1BarRecord[d1BarFile.header.getRecordCount()];
            for(int dwI = 0; dwI < d1BarFile.header.getRecordCount(); dwI++)
            {
                d1BarFile.recordList[dwI] = new D1BarRecord();
                file.seek(FPosition + dwI * d1BarFile.header.getRecordLength());
                D1BarRecord.Read(d1BarFile.recordList[dwI], file);
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return Boolean.valueOf(true);
    }

}
