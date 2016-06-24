package com.trimble.agmantra.filecodec.res;

public class ResAttribCrop extends ComResAttribs{
	/*
	 * 'commodity' attribute - commodity ID for this crop
	 */
	private String sCommodity;
	
	/*
	 * 'year' attribute - year crop to be harvested
	 */
	private String sYear;
		
	public ResAttribCrop() {		
		super();		
		
		sCommodity = null;
		sYear = null;
	}

	//getter and setter methods	
	public String getCommodity(){
		return sCommodity;
	}
	
	public void setCommodity(String sInputCommodity){
		sCommodity = sInputCommodity;
	}

	public String getYear(){
		return sYear;
	}
	
	public void setYear(String sInputYear){
		sYear = sInputYear;
	}
}
