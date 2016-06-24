package com.trimble.agmantra.filecodec.res;

public class ResAttribUnit extends ComResAttribs{
	/*
	 * 'short'attribute - abbreviated name
	 */
	private String sShort;
	
	/*
	 * 'singular' attribute - singular version of text description
	 */
	private String sSingular;
	
	/*
	 * 'type' attribute - domain of this unit (mass, length, volume, etc)
	 */
	private String sType;
	
	/*'metric' attribute - unit's metric system
	 *  0  - English
	 *  1  - Metric
	 *  -1 - Both
	*/ 
	private int nMetric;
	
	/*
	 * 'unitconv' attribute - conversion factor to standard unit for this domain
	 */
	private float fUnitConv;
		
	public ResAttribUnit() {		
		super();		
		
		sShort = null;
		sSingular = null;
		sType = null;
		nMetric = 0;
		fUnitConv = 0.0f;
	}

	//getter and setter methods	
	public String getShort(){
		return sShort;
	}
	
	public void setShort(String sInputShort){
		sShort = sInputShort;
	}
	
	public String getSingular(){
		return sSingular;
	}
	
	public void setSingular(String sInputSingular){
		sSingular = sInputSingular;
	}
	
	public String getType(){
		return sType;
	}
	
	public void setType(String sInputType){
		sType = sInputType;
	}
	
	public int getMetricSystem(){
		return nMetric;
	}
	
	public void setMetricSystem(int nInpMetric){
		nMetric = nInpMetric;
	}

	public float getUnitConv(){
		return fUnitConv;
	}
	
	public void setUnitConv(float fInpUnitConv){
		fUnitConv = fInpUnitConv;
	}
}
