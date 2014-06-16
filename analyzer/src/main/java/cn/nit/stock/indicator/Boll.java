package cn.nit.stock.indicator;


public class Boll
{

    public Boll(Double data[])
    {
        boll = 0.0D;
        ub = 0.0D;
        lb = 0.0D;
        Double adouble[];
        int k = (adouble = data).length;
        for(int j = 0; j < k; j++)
        {
            Double d = adouble[j];
            boll += d.doubleValue();
        }

        boll /= data.length;
        Double sqrt = Double.valueOf(0.0D);
        for(int i = 0; i < data.length; i++)
            sqrt = Double.valueOf(sqrt.doubleValue() + Math.pow(Math.abs(data[i].doubleValue() - boll), 2D));

        sqrt = Double.valueOf(sqrt.doubleValue() / (double)(data.length - 1));
        sqrt = Double.valueOf(Math.pow(sqrt.doubleValue(), 0.5D));
        ub = boll + sqrt.doubleValue() * 2D;
        lb = boll - sqrt.doubleValue() * 2D;
    }

    public double getBoll()
    {
        return boll;
    }

    public void setBoll(double boll)
    {
        this.boll = boll;
    }

    public double getUb()
    {
        return ub;
    }

    public void setUb(double ub)
    {
        this.ub = ub;
    }

    public double getLb()
    {
        return lb;
    }

    public void setLb(double lb)
    {
        this.lb = lb;
    }

    public String toString()
    {
        return (new StringBuilder("Boll [boll=")).append(boll).append(", ub=").append(ub).append(", lb=").append(lb).append("]").toString();
    }

    private double boll;
    private double ub;
    private double lb;
}
