package dz.lab.finance.provider.data;

import lombok.ToString;

@ToString
public class ForexPair {
	
	public String fxpair;
	public int year;
	public int month;
	
	public ForexPair(String fxpair, int year, int month) {
		this.fxpair = fxpair;
		this.year = year;
		this.month = month;
	}	
	
}
