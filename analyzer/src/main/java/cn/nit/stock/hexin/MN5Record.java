package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;

public class MN5Record
{

    public MN5Record()
    {
    }

    public static Boolean Read(MN5Record record, RandomAccessFile file) throws IOException
    {
        if(file == null)
            return Boolean.valueOf(false);
        long FValue;
        FValue = IntUtils.readInt32(file);
        if(FValue == 0L)
            return Boolean.valueOf(false);
        try
        {
            String value = toBinary(FValue, 2);
            int year = Integer.parseInt(value.substring(0, 7), 2) + 1900;
            int month = Integer.parseInt(value.substring(7, 11), 2);
            int day = Integer.parseInt(value.substring(11, 16), 2);
            int hour = Integer.parseInt(value.substring(16, 21), 2);
            int minute = Integer.parseInt(value.substring(21, 27), 2);
            record.date = (new StringBuilder(String.valueOf(year))).append("-").append(month).append("-").append(day).append(" ").append(hour).append(":").append(minute).toString();
            record.open = GetValue(IntUtils.readInt32(file));
            record.high = GetValue(IntUtils.readInt32(file));
            record.low = GetValue(IntUtils.readInt32(file));
            record.close = GetValue(IntUtils.readInt32(file));
            record.amount = GetValue(IntUtils.readInt32(file));
            record.volume = GetValue(IntUtils.readInt32(file));
            return Boolean.valueOf(true);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return Boolean.valueOf(true);
    }

    private static String toBinary(long n, int target)
    {
        String s = "";
        for(; n != 0L; n /= target)
            s = (new StringBuilder(String.valueOf(n % (long)target))).append(s).toString();

        return s;
    }

    public static double GetValue(long value)
    {
        double FValue = value & 0xfffffffL;
        byte FSign = (byte)(int)(value >> 28);
        if((FSign & 7) != 0)
        {
            double FFactor = Math.pow(10D, FSign & 7);
            if((FSign & 8) != 0)
                FValue /= FFactor;
            else
                FValue *= FFactor;
        }
        return FValue;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public double getOpen()
    {
        return open;
    }

    public void setOpen(double open)
    {
        this.open = open;
    }

    public double getHigh()
    {
        return high;
    }

    public void setHigh(double high)
    {
        this.high = high;
    }

    public double getLow()
    {
        return low;
    }

    public void setLow(double low)
    {
        this.low = low;
    }

    public double getClose()
    {
        return close;
    }

    public void setClose(double close)
    {
        this.close = close;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public double getVolume()
    {
        return volume;
    }

    public void setVolume(double volume)
    {
        this.volume = volume;
    }

    private String date;
    private double open;
    private double high;
    private double low;
    private double close;
    private double amount;
    private double volume;
    public static int LENGTH = 16;

}
