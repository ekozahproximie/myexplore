package com.trimble.agmantra.filecodec.res;

public class ComResAttribs {
	
	/*'id' attribute - 64 bit id number*/
	private String sID;
	
	/*'desc' attribute - text description*/
	private String sDescription;
	
	/*'locked' attribute - flag to indicate this item may not be edited
	 *  0  - item can be edited
	 *  1  - item cannot be edited
	 *  -1 - attribute unknown
	*/ 
	private int nLocked;
	
	//ctor
	public ComResAttribs(){
		sID = null;
		sDescription = null;
		nLocked = -1;
	}
	
	//getter and setter methods	
	public String getID(){
		return sID;
	}
	
	public void setID(String sInputID){
		sID = sInputID;
	}
	
	public String getDescription(){
		return sDescription;
	}
	
	public void setDescription(String sInputDesc){
		sDescription = sInputDesc;
	}
	
	public int getLocked(){
		return nLocked;
	}
	
	public void setLocked(int nInpLocked){
		nLocked = nInpLocked;
	}
}
