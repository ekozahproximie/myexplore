package com.navteq.lpa.enums;

public enum NavteqLPADTA 
{
	NavteqLPADTA_Age(0), NavteqLPADTA_Gender(1), NavteqLPADTA_Interests(2), NavteqLPADTA_Education(3), NavteqLPADTA_Income(4),
	NavteqLPADTA_MaritalStatus(5), NavteqLPADTA_HouseholdSize(6), NavteqLPADTA_ChildrenInHouseHold(7), NavteqLPADTA_Occupation(8);

	private int code;

 	private NavteqLPADTA(int c) {
	 	code = c;
 	}

 	public int getValue() {
	 	return code;
 	}
}