package com.navteq.lpa.enums;

public enum NavteqLPADTA_Interests 
{
	NavteqLPADTA_InterestsMusic(1), 
	NavteqLPADTA_InterestsEntertainment(2), 
	NavteqLPADTA_InterestsGamesLeisure(3), 
	NavteqLPADTA_InterestsNews(4), 
	NavteqLPADTA_InterestsSportsRecreation(5),
	NavteqLPADTA_InterestsBusinessFinance(6), 
	NavteqLPADTA_InterestsProductivityUtility(7), 
	NavteqLPADTA_InterestsTravel(8), 
	NavteqLPADTA_InterestsMedicalHealth(9), 
	NavteqLPADTA_InterestsFitness(10),
	NavteqLPADTA_InterestsEducationReference(11),
	NavteqLPADTA_InterestsBooksReading(12);

	private int code;

 	private NavteqLPADTA_Interests(int c) {
	 	code = c;
 	}

 	public int getValue() {
	 	return code;
 	}
}