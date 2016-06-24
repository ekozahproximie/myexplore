package com.trimble.agmantra.filecodec.res;

public class ComResAttribsEx extends ComResAttribs{

	/*'deleted' attribute - flag to indicate this item has been deleted by the user
	 *  0  - item is not deleted
	 *  1  - item is deleted
	 *  -1 - attribute unknown
	*/ 
	private int nDeleted;
	
	public ComResAttribsEx() {
		super();
		// TODO Auto-generated constructor stub		
		nDeleted = -1;
	}

	//getter and setter methods		
	public int getDeleted(){
		return nDeleted;
	}
	
	public void setDeleted(int nInpDeleted){
		nDeleted = nInpDeleted;
	}
}


