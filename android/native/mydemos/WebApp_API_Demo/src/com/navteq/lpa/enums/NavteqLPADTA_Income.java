package com.navteq.lpa.enums;

public enum NavteqLPADTA_Income
{
	NavteqLPADTA_IncomeLessThan15K(1), 
	NavteqLPADTA_Income15K(2), 
	NavteqLPADTA_Income25K(3), 
	NavteqLPADTA_Income40K(4), 
	NavteqLPADTA_Income60K(5),
	NavteqLPADTA_Income75K(6),
	NavteqLPADTA_Income100Kplus(7);

	private int code;

 	private NavteqLPADTA_Income(int c) {
	 	code = c;
 	}

 	public int getValue() {
	 	return code;
 	}
}