package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DividendFile
{

    public DividendFile()
    {
        header = new FileHeader();
        block = new ComplexIndexBlock();
    }

    public static Boolean Read(DividendFile dividend, RandomAccessFile file) throws IOException
    {
        if(file == null)
            return Boolean.valueOf(false);
        file.seek(0L);
        if(!FileHeader.Read(dividend.header, file).booleanValue())
            return Boolean.valueOf(false);
        int FPosition = FileHeader.LENGTH;
        dividend.columnList = new ColumnHeader[dividend.header.getFieldCount()];
        for(int dwI = 0; dwI < dividend.header.getFieldCount(); dwI++)
        {
            dividend.columnList[dwI] = new ColumnHeader();
            file.seek(FPosition + ColumnHeader.LENGTH * dwI);
            ColumnHeader.Read(dividend.columnList[dwI], file);
        }

        FPosition = FileHeader.LENGTH + ColumnHeader.LENGTH * dividend.header.getFieldCount();
        file.seek(FPosition);
        byte b[] = new byte[dividend.header.getFieldCount() * 2];
        file.read(b);
        dividend.w1 = b;
        FPosition += dividend.header.getFieldCount() * 2;
        file.seek(FPosition);
        if(!ComplexIndexBlock.Read(dividend.block, file).booleanValue())
            return Boolean.valueOf(false);
        try
        {
            FPosition = dividend.header.getHeaderLength();
            file.seek(FPosition);
            dividend.recordList = new DividendRecord[dividend.header.getRecordCount()];
            for(int dwI = 0; dwI < dividend.header.getRecordCount(); dwI++)
            {
                dividend.recordList[dwI] = new DividendRecord();
                file.seek(FPosition + dwI * dividend.header.getRecordLength());
                DividendRecord.Read(dividend.recordList[dwI], file);
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return Boolean.valueOf(true);
    }

    public FileHeader getHeader()
    {
        return header;
    }

    public void setHeader(FileHeader header)
    {
        this.header = header;
    }

    public ColumnHeader[] getColumnList()
    {
        return columnList;
    }

    public void setColumnList(ColumnHeader columnList[])
    {
        this.columnList = columnList;
    }

    public byte[] getW1()
    {
        return w1;
    }

    public void setW1(byte w1[])
    {
        this.w1 = w1;
    }

    public ComplexIndexBlock getBlock()
    {
        return block;
    }

    public void setBlock(ComplexIndexBlock block)
    {
        this.block = block;
    }

    public DividendRecord[] getRecordList()
    {
        return recordList;
    }

    public void setRecordList(DividendRecord recordList[])
    {
        this.recordList = recordList;
    }

    private FileHeader header;
    private ColumnHeader columnList[];
    private byte w1[];
    private ComplexIndexBlock block;
    private DividendRecord recordList[];
}
