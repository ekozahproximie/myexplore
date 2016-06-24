package com.trimble.agmantra.filecodec.res;

public class ResAttribCarrier  extends ComResAttribs{
	/*
	 * 'unit'attribute - unit of measure ID for this carrier
	 */
	private String sUnit;
			
	public ResAttribCarrier() {		
		super();		
		
		sUnit = null;
	}

	//getter and setter methods	
	public String getUnit(){
		return sUnit;
	}
	
	public void setUnit(String sInputUnit){
		sUnit = sInputUnit;
	}
}

