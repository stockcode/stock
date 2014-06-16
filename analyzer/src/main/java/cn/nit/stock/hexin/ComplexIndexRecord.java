package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ComplexIndexRecord
{

    public ComplexIndexRecord()
    {
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public int getFreeNumber()
    {
        return freeNumber;
    }

    public void setFreeNumber(int freeNumber)
    {
        this.freeNumber = freeNumber;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public int getRecordNumber()
    {
        return recordNumber;
    }

    public void setRecordNumber(int recordNumber)
    {
        this.recordNumber = recordNumber;
    }

    public int getMarket()
    {
        return market;
    }

    public void setMarket(int market)
    {
        this.market = market;
    }

    public static Boolean Read(ComplexIndexRecord record, RandomAccessFile file) throws IOException
    {
        if(file == null)
            return Boolean.valueOf(false);
        byte FValue;
        FValue = file.readByte();
        if(FValue == 0)
            return Boolean.valueOf(false);
        try
        {
            record.market = FValue;
            byte FBuffer[] = new byte[9];
            file.read(FBuffer);
            int FIndex;
            for(FIndex = 0; FIndex < FBuffer.length; FIndex++)
                if(FBuffer[FIndex] == 0)
                    break;

            record.symbol = new String(FBuffer, 0, FIndex, "GB2312");
            record.freeNumber = IntUtils.readInt16(file);
            record.position = IntUtils.readInt32(file);
            record.recordNumber = IntUtils.readInt16(file);
            return Boolean.valueOf(true);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return Boolean.valueOf(true);
    }
    
    @Override
	public String toString() {
		return "ComplexIndexRecord [market=" + market + ", symbol=" + symbol
				+ ", freeNumber=" + freeNumber + ", position=" + position
				+ ", recordNumber=" + recordNumber + "]";
	}




	private int market;
    private String symbol;
    private int freeNumber;
    private int position;
    private int recordNumber;
}