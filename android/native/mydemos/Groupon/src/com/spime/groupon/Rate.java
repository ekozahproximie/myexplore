package com.spime.groupon;

public class Rate {
public int iAmount=0;
public String stCurrencyCode=null;
public String stFormattedAmount=null;
public Rate(Rate rate) {
	 iAmount=rate.iAmount;
	 stCurrencyCode=rate.stCurrencyCode;
	 stFormattedAmount=rate.stFormattedAmount;
}
public Rate() {
	
}
@Override
	public String toString() {
	
		return iAmount+","+stCurrencyCode+","+stFormattedAmount;
	}
}
