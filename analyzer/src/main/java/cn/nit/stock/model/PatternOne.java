package cn.nit.stock.model;

//模型1，特指第一天涨停回落，第二天封住涨停的形态
public class PatternOne {
	TradeDay first, second;

	public PatternOne(TradeDay first, TradeDay second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	
	public Boolean isMatch() {
		return first.isHighestLimt() && second.isHighLimit();
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PatternOne [first=");
		builder.append(first);
		builder.append(", second=");
		builder.append(second);
		builder.append("]");
		return builder.toString();
	}
	
	
}
