package com.navteq.lpa.enums;

public enum NavteqLPADTA_Education
{
	NavteqLPADTA_EducationLessThanSecondary(1), 
	NavteqLPADTA_EducationSecondary(2), 
	NavteqLPADTA_EducationSomeUniversity(3), 
	NavteqLPADTA_EducationUniversityDegree(4), 
	NavteqLPADTA_EducationAdvancedDegree(5),
	NavteqLPADTA_EducationUnknown(6);

	private int code;

 	private NavteqLPADTA_Education(int c) {
	 	code = c;
 	}

 	public int getValue() {
	 	return code;
 	}
}