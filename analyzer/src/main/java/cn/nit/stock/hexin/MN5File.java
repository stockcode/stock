package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MN5File
{

    public MN5File()
    {
        header = new FileHeader();
    }

    public static Boolean Read(MN5File mn5File, RandomAccessFile file)
    {
        if(file == null)
            return Boolean.valueOf(false);
        try
        {
            file.seek(0L);
            FileHeader.Read(mn5File.header, file);
            int FPosition = FileHeader.LENGTH;
            mn5File.columnList = new ColumnHeader[mn5File.header.getFieldCount()];
            for(int dwI = 0; dwI < mn5File.header.getFieldCount(); dwI++)
            {
                mn5File.columnList[dwI] = new ColumnHeader();
                file.seek(FPosition + ColumnHeader.LENGTH * dwI);
                ColumnHeader.Read(mn5File.columnList[dwI], file);
            }

            FPosition = mn5File.header.getHeaderLength();
            file.seek(FPosition);
            mn5File.recordList = new MN5Record[mn5File.header.getRecordCount()];
            for(int dwI = 0; dwI < mn5File.header.getRecordCount(); dwI++)
            {
                mn5File.recordList[dwI] = new MN5Record();
                file.seek(FPosition + dwI * mn5File.header.getRecordLength());
                MN5Record.Read(mn5File.recordList[dwI], file);
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return Boolean.valueOf(true);
    }

    public FileHeader header;
    public ColumnHeader columnList[];
    public MN5Record recordList[];
    public static int LENGTH = 4;

}
