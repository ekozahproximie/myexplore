package com.trimble.agmantra.filecodec.res;

public class ResAttribCommodity extends ComResAttribs{
	/*
	 * 'unit'attribute - unit of measure ID for this commodity
	 */
	private String sUnit;
	
	/*
	 * 'unitconv' attribute - conversion factor to kg
	 */
	private float fUnitConv;
		
	public ResAttribCommodity() {		
		super();		
		
		sUnit = null;
		fUnitConv = 0.0f;
	}

	//getter and setter methods	
	public String getUnit(){
		return sUnit;
	}
	
	public void setUnit(String sInputUnit){
		sUnit = sInputUnit;
	}

	public float getUnitConv(){
		return fUnitConv;
	}
	
	public void setUnitConv(float fInpUnitConv){
		fUnitConv = fInpUnitConv;
	}
}
