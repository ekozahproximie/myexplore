package com.navteq.lpa.enums;

public enum NavteqLPADTA_Age 
{
	NavteqLPADTA_Age12to14(12), 
	NavteqLPADTA_Age15to17(15), 
	NavteqLPADTA_Age18to20(18), 
	NavteqLPADTA_Age21to24(21), 
	NavteqLPADTA_Age25to34(25),
	NavteqLPADTA_Age35to44(35), 
	NavteqLPADTA_Age45to49(45), 
	NavteqLPADTA_Age50to54(50), 
	NavteqLPADTA_Age55to64(55), 
	NavteqLPADTA_Age65Plus(65);

	private int code;

 	private NavteqLPADTA_Age(int c) {
	 	code = c;
 	}

 	public int getValue() {
	 	return code;
 	}
}