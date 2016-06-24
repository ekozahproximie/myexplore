package com.trimble.agmantra.filecodec.res;

public class ResAttribField extends ComResAttribsEx{
	/*
	 * 'area' attribute - area of the field
	 */
	private float fArea;
	
	/*
	 * 'unit' attribute - unit of measure for the area
	 */
	private String sAreaUnit;
	
	/*
	 * 'farmid' attribute - farm the field belongs in
	 */
	private String sFarmID;
	
	/*
	 * 'boundary-revision' attribute - field boundary's revision version
	 */
	private int nBdryRevision;
	
	/*
	 * 'boundary-modified' attribute - field boundary's modified version
	 */
	private int nBdryModified;
		
	public ResAttribField() {		
		super();		
		
		fArea = 0.0f;		
		sAreaUnit = null;
		sFarmID = null;		
	}

	//getter and setter methods	
	public String getFarmID(){
		return sFarmID;
	}
	
	public void setFarmID(String sInputFarmID){
		sFarmID = sInputFarmID;
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

	/**
	 * @return the nBdryRevision
	 */
	public int getBdryRevision() {
		return nBdryRevision;
	}

	/**
	 * @param nInpBdryRevision the nBdryRevision to set
	 */
	public void setBdryRevision(int nInpBdryRevision) {
		this.nBdryRevision = nInpBdryRevision;
	}

	/**
	 * @return the nBdryModified
	 */
	public int getBdryModified() {
		return nBdryModified;
	}

	/**
	 * @param nInpBdryModified the nBdryModified to set
	 */
	public void setBdryModified(int nInpBdryModified) {
		this.nBdryModified = nInpBdryModified;
	}
}