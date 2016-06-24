package com.trimble.agmantra.filecodec.res;

public class ResAttribFarm extends ComResAttribsEx{
	/*
	 * 'area' attribute - total area of the fields in the farm
	 */
	private float fArea;
	
	/*
	 * 'unit' attribute - unit of measure for the area
	 */
	private String sAreaUnit;
	
	/*
	 * 'clientid' attribute - client that farm belongs to
	 */
	private String sClientID;
	
	
		
	public ResAttribFarm() {		
		super();		
		
		fArea = 0.0f;		
		sAreaUnit = null;
		sClientID = null;		
	}

	//getter and setter methods	
	public String getClientID(){
		return sClientID;
	}
	
	public void setClientID(String sInputClientID){
		sClientID = sInputClientID;
	}
	
	public String getAreaUnit(){
		return sAreaUnit;
	}	
	
	public void setAreaUnit(String sInputAreaUnit){
		sAreaUnit = sInputAreaUnit;
	}

	public float getArea(){
		return fArea;
	}	
	
	public void setArea(float fInputArea){
		fArea = fInputArea;
	}
}