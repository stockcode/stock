package cn.nit.stock.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Document
public class TradeDay {


    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    @Id
    private ObjectId id;

	Double yesterdayPrice, openPrice, closePrice, highPrice, lowPrice;
	String stockCode;
	String tradeDate;

    public Boolean isInvalid() {
        Double delta = Math.abs(yesterdayPrice - highPrice) + Math.abs(yesterdayPrice - lowPrice);
        return delta < 0.01;
    }
	
	public Double getYesterdayPrice() {
		return yesterdayPrice;
	}



	public void setYesterdayPrice(Double yesterdayPrice) {
		this.yesterdayPrice = yesterdayPrice;
	}



	public Double getOpenPrice() {
		return openPrice;
	}



	public void setOpenPrice(Double openPrice) {
		this.openPrice = openPrice;
	}



	public Double getClosePrice() {
		return closePrice;
	}



	public void setClosePrice(Double closePrice) {
		this.closePrice = closePrice;
	}



	public Double getHighPrice() {
		return highPrice;
	}



	public void setHighPrice(Double highPrice) {
		this.highPrice = highPrice;
	}



	public Double getLowPrice() {
		return lowPrice;
	}



	public void setLowPrice(Double lowPrice) {
		this.lowPrice = lowPrice;
	}


	

	public String getStockCode() {
		return stockCode;
	}



	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}



	public String getTradeDate() {
		return tradeDate;
	}



	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}



	public Boolean isHighLimit() {
		return yesterdayPrice*1.1 - closePrice < 0.01;
	}
	
	public Boolean isHighestLimt() {
		return (yesterdayPrice*1.1 - highPrice < 0.01) && !isHighLimit();
	}

    public Boolean isOpenLimit() {
        return isHighLimit() && (highPrice - openPrice < 0.01);
    }

	public TradeDay(Double yesterdayPrice, Double openPrice, Double closePrice,
			Double highPrice, Double lowPrice, String stockCode, String tradeDate) {
		super();
		this.yesterdayPrice = yesterdayPrice;
		this.openPrice = openPrice;
		this.closePrice = closePrice;
		this.highPrice = highPrice;
		this.lowPrice = lowPrice;
		this.stockCode = stockCode;
		this.tradeDate = tradeDate;
	}



	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradeDay [yesterdayPrice=");
		builder.append(yesterdayPrice);
		builder.append(", openPrice=");
		builder.append(openPrice);
		builder.append(", closePrice=");
		builder.append(closePrice);
		builder.append(", highPrice=");
		builder.append(highPrice);
		builder.append(", lowPrice=");
		builder.append(lowPrice);
		builder.append(", stockCode=");
		builder.append(stockCode);
		builder.append(", tradeDate=");
		builder.append(tradeDate);
		builder.append("]");
		return builder.toString();
	}


    public boolean isTodayLimit() {
        Date today = new Date();
        return df.format(today).equals(tradeDate) && isOpenLimit();
    }
}
