package com.spime.groupon;

public class TimeDiff{
public int iDay=0;
public int iHours;
public int iMins;
public int iSeconds;
public long lMyTime;
public TimeDiff() {
	
}
@Override
	public String toString() {
		
		return iDay+":"+iHours+":"+iMins;
	}
}
