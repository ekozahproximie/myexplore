package com.spime.groupon;

import java.util.HashMap;
import java.util.Vector;

public class Deals {

	
	public static int iMyTimeZoneOffset=0;
	public HashMap<String,Vector<Deal>> hMapDealByTag= null;
@Override
public String toString() {
	
	return hMapDealByTag.toString();
}
}
