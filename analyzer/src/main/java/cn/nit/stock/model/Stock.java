package cn.nit.stock.model;

public class Stock {
	public String stockCode, tradedate, status;
	public Double costPrice, costMoney;
	public Integer amount ,index ;
	
	@Override
	public String toString() {
		java.text.DecimalFormat   df=new   java.text.DecimalFormat( "#.## ");
		return "Stock [amount=" + amount + ", costMoney=" + df.format(costMoney)
				+ ", costPrice=" + df.format(costPrice) + ", stockCode=" + stockCode
				+ ", tradedate=" + tradedate + "]";
	}
	
	
}
