package cn.nit.stock.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class StockLimit {

    @Id
    private ObjectId id;

	Double yesterdayPrice, openPrice, closePrice, highPrice, lowPrice;
	String stockCode, stockName;
	String limitDate;

    public Boolean isOpenLimit() {
        return highPrice - openPrice < 0.01;
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

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(String limitDate) {
        this.limitDate = limitDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StockLimit{");
        sb.append("id=").append(id);
        sb.append(", yesterdayPrice=").append(yesterdayPrice);
        sb.append(", openPrice=").append(openPrice);
        sb.append(", closePrice=").append(closePrice);
        sb.append(", highPrice=").append(highPrice);
        sb.append(", lowPrice=").append(lowPrice);
        sb.append(", stockCode='").append(stockCode).append('\'');
        sb.append(", stockName='").append(stockName).append('\'');
        sb.append(", limitDate='").append(limitDate).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
