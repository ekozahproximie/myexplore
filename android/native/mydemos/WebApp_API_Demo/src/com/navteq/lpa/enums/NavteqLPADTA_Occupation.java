package com.navteq.lpa.enums;

public enum NavteqLPADTA_Occupation
{
	NavteqLPADTA_OccupationProfessional(1), 
	NavteqLPADTA_OccupationManagerial(2), 
	NavteqLPADTA_OccupationClerical(3),
	NavteqLPADTA_OccupationSales(4);

	private int code;

 	private NavteqLPADTA_Occupation(int c) {
	 	code = c;
 	}

 	public int getValue() {
	 	return code;
 	}
}