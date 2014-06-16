package cn.nit.stock.hexin;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DividendRecord
{

    public DividendRecord()
    {
    }

    public static Boolean Read(DividendRecord record, RandomAccessFile file) throws IOException
    {
        if(file == null)
            return Boolean.valueOf(false);
        int FValue;
        FValue = IntUtils.readInt32(file);
        if(FValue == 0)
            return Boolean.valueOf(false);
        try
        {
            record.date = (new StringBuilder(String.valueOf(FValue))).toString();
            record.m_W1 = IntUtils.readInt32(file);
            FValue = IntUtils.readInt32(file);
            if(FValue < 0)
                record.exdividendDate = "19010101";
            else
                record.exdividendDate = (new StringBuilder(String.valueOf(FValue))).toString();
            record.cash = Double.valueOf(IntUtils.readDouble(file));
            record.split = Double.valueOf(IntUtils.readDouble(file));
            record.bonus = Double.valueOf(IntUtils.readDouble(file));
            record.dispatch = Double.valueOf(IntUtils.readDouble(file));
            record.price = Double.valueOf(IntUtils.readDouble(file));
            FValue = IntUtils.readInt32(file);
            if(FValue < 0)
                record.registerDate = "19010101";
            else
                record.registerDate = (new StringBuilder(String.valueOf(FValue))).toString();
            FValue = IntUtils.readInt32(file);
            if(FValue < 0)
                record.listingDate = "19010101";
            else
                record.listingDate = (new StringBuilder(String.valueOf(FValue))).toString();
            byte FBuffer[] = new byte[242];
            file.read(FBuffer);
            int FIndex;
            for(FIndex = 0; FIndex < FBuffer.length; FIndex++)
                if(FBuffer[FIndex] == 0)
                    break;

            record.description = new String(FBuffer, 0, FIndex, "GB2312");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return Boolean.valueOf(true);
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

    public int getM_W1()
    {
        return m_W1;
    }

    public void setM_W1(int m_W1)
    {
        this.m_W1 = m_W1;
    }

    public Double getCash()
    {
        return cash;
    }

    public void setCash(Double cash)
    {
        this.cash = cash;
    }

    public Double getSplit()
    {
        return split;
    }

    public void setSplit(Double split)
    {
        this.split = split;
    }

    public Double getBonus()
    {
        return bonus;
    }

    public void setBonus(Double bonus)
    {
        this.bonus = bonus;
    }

    public Double getDispatch()
    {
        return dispatch;
    }

    public void setDispatch(Double dispatch)
    {
        this.dispatch = dispatch;
    }

    public Double getPrice()
    {
        return price;
    }

    public void setPrice(Double price)
    {
        this.price = price;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getExdividendDate()
    {
        return exdividendDate;
    }

    public void setExdividendDate(String exdividendDate)
    {
        this.exdividendDate = exdividendDate;
    }

    public String getRegisterDate()
    {
        return registerDate;
    }

    public void setRegisterDate(String registerDate)
    {
        this.registerDate = registerDate;
    }

    public String getListingDate()
    {
        return listingDate;
    }

    public void setListingDate(String listingDate)
    {
        this.listingDate = listingDate;
    }

    public String toString()
    {
        return (new StringBuilder("DividendRecord [date=")).append(date).append(", m_W1=").append(m_W1).append(", exdividendDate=").append(exdividendDate).append(", cash=").append(cash).append(", split=").append(split).append(", bonus=").append(bonus).append(", dispatch=").append(dispatch).append(", price=").append(price).append(", registerDate=").append(registerDate).append(", listingDate=").append(listingDate).append(", description=").append(description).append("]").toString();
    }

    private String date;
    private int m_W1;
    private String exdividendDate;
    private Double cash;
    private Double split;
    private Double bonus;
    private Double dispatch;
    private Double price;
    private String registerDate;
    private String listingDate;
    private String description;
}
