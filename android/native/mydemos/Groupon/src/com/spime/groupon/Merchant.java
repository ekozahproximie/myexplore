package com.spime.groupon;

public class Merchant {
public String stWebsiteUrl=null;
public String stName=null;
public Merchant(String stWebsiteUrl,String stName) {
	this.stWebsiteUrl=stWebsiteUrl;
	this.stName=stName;
	
}
@Override
	public String toString() {
		return stName+","+stWebsiteUrl;
	}
}
