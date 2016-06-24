package com.trimble.agmantra.filecodec.res;

public class ResAttribEquipment extends ComResAttribsEx{
	/*
	 * 'charge_units' attribute - unit of measure
	 */
	private String sChargeUnits;
	
	/*
	 * 'icon_group' attribute - equipment grouping ID
	 */
	private String sIconGroup;
	
	/*
	 * 'width' attribute - working width of implement in cm
	 */
	private float fWidth;
		
	public ResAttribEquipment() {		
		super();		
		
		sChargeUnits = null;
		sIconGroup = null;
		fWidth = 0.0f;
	}

	//getter and setter methods	
	public String getChargeUnits(){
		return sChargeUnits;
	}
	
	public void setChargeUnits(String sInputChargeUnits){
		sChargeUnits = sInputChargeUnits;
	}
	
	public String getIconGroup(){
		return sIconGroup;
	}	
	
	public void setIconGroup(String sInputIconGroup){
		sIconGroup = sInputIconGroup;
	}

	public float getWidth(){
		return fWidth;
	}	
	
	public void setWidth(float fInputWidth){
		fWidth = fInputWidth;
	}
}