package cn.nit.stock.model;

//模型1，特指第一天涨停回落，第二天封住涨停的形态
public class PatternTwo {
	TradeDay first, second, third;

	public PatternTwo(TradeDay first, TradeDay second, TradeDay third) {
		super();
		this.first = first;
		this.second = second;
		this.third = third;
	}
	
	
	public Boolean isMatch() {
		return first.isHighLimit() && third.isHighLimit();
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PatternTwo [first=");
		builder.append(first);
		builder.append(", second=");
		builder.append(second);
		builder.append(", third=");
		builder.append(third);
		builder.append("]");
		return builder.toString();
	}


	
	
	
}
