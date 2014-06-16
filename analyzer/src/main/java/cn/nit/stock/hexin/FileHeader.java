package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;


public class FileHeader
{
	
    private int sign;
    private int w1;
    private int recordCount;
    private int headerLength;
    private int recordLength;
    private int fieldCount;
    public static int LENGTH = 16;


    public FileHeader()
    {
    }

    public static Boolean Read(FileHeader header, RandomAccessFile file)
    {
        if(file == null)
            return Boolean.valueOf(false);
        try
        {
            header.sign = IntUtils.readInt32(file);
            header.w1 = IntUtils.readInt16(file);
            header.recordCount = IntUtils.readInt32(file) & 0xffffff;
            header.headerLength = IntUtils.readInt16(file);
            header.recordLength = IntUtils.readInt16(file);
            header.fieldCount = IntUtils.readInt16(file);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return Boolean.valueOf(true);
    }

    

    @Override
	public String toString() {
		return "FileHeader [sign=" + sign + ", w1=" + w1 + ", recordCount="
				+ recordCount + ", headerLength=" + headerLength
				+ ", recordLength=" + recordLength + ", fieldCount="
				+ fieldCount + "]";
	}

	public int getSign()
    {
        return sign;
    }

    public void setSign(int sign)
    {
        this.sign = sign;
    }

    public int getW1()
    {
        return w1;
    }

    public void setW1(int w1)
    {
        this.w1 = w1;
    }

    public int getRecordCount()
    {
        return recordCount;
    }

    public void setRecordCount(int recordCount)
    {
        this.recordCount = recordCount;
    }

    public int getHeaderLength()
    {
        return headerLength;
    }

    public void setHeaderLength(int headerLength)
    {
        this.headerLength = headerLength;
    }

    public int getRecordLength()
    {
        return recordLength;
    }

    public void setRecordLength(int recordLength)
    {
        this.recordLength = recordLength;
    }

    public int getFieldCount()
    {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount)
    {
        this.fieldCount = fieldCount;
    }
}
