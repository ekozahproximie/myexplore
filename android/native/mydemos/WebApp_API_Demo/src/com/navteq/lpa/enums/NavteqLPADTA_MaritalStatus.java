package com.navteq.lpa.enums;

public enum NavteqLPADTA_MaritalStatus
{
	NavteqLPADTA_MaritalStatusMarried(1), 
	NavteqLPADTA_MaritalStatusSingle(2), 
	NavteqLPADTA_MaritalStatusDivorced(3);

	private int code;

 	private NavteqLPADTA_MaritalStatus(int c) {
	 	code = c;
 	}

 	public int getValue() {
	 	return code;
 	}
}