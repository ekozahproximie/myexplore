package com.spime.groupon;

public class RedemptionLocation{
	public String stState=null;
	public String stCity=null;
	public String stStreet=null;
	public String stZip=null;
	public String stPhoneNumber=null;
	public double dLat=0;
	public double dLng=0;
	public String stName=null;
	@Override
	public String toString() {
		
		return stName+","+dLat+","+dLng+","+stStreet+","+stCity+","+stZip+","+stState+","+stPhoneNumber;
	}
}
