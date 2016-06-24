package com.trimble.agmantra.filecodec.res;

public class ResAttribPeople extends ComResAttribsEx{
	/*
	 * 'epa_num' attribute - chemical applicator license number
	 */
	private String sEPANumber;
		
	public ResAttribPeople() {		
		super();		
		
		sEPANumber = null;
	}

	//getter and setter methods	
	public String getEPANumber(){
		return sEPANumber;
	}
	
	public void setEPANumber(String sInputEPANumber){
		sEPANumber = sInputEPANumber;
	}

}