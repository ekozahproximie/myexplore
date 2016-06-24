package com.spime.groupon;

public class Division{
	
	public boolean isNowCustomerEnabled=false;
	public boolean isNowMerchantEnabled=false;
	public String stID=null;
	public String stTimezone=null;
	public int iTimezoneOffsetInSeconds=0;
	public String stAreas=null;
	public String stName=null;
	public String stCountry=null;
	public double stlng=0;
	public double stlat=0;
	public Division(boolean isNowCustomerEnabled,boolean isNowMerchantEnabled,
			String stID,String stTimezone,String stName,String stCountry,
			double stlng,double stlat) {
		this.stName=stName;
		this.stlat=stlat;
		this.stlng=stlng;
		this.stCountry=stCountry;
		this.stID=stID;
		this.stTimezone=stTimezone;
		this.isNowMerchantEnabled=isNowMerchantEnabled;
		this.isNowCustomerEnabled=isNowCustomerEnabled;
	}
	@Override
	public String toString() {
		
		return stName+","+stlat+","+stlng+","+stID+","+stCountry;
	}
}
