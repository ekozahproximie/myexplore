package com.spime;

import java.net.HttpURLConnection;

public class MyConnection implements Runnable{
	  String stURL=null;
	  String stMethod =null;
	  String stContent=null;
	  String stReferURL =null;
	  String stCookie=null;
	  boolean isLogout=false;
	  MyCallback callback=null;
	  public MyConnection( String stURL,String stMethod,String stContent
			  ,String stReferURL,String stCookie,boolean isLogout,MyCallback callback) {
		// TODO Auto-generated constructor stub
		  this.stURL=stURL;
		  this. stMethod=stMethod;
		  this. stContent=stContent;
		  this.stReferURL=stReferURL;
		  this.stCookie=stCookie;
		  this.isLogout=isLogout;
		  this.callback=callback;
	}
	  
	@Override
	public void run() {
		// TODO Auto-generated method stub
		HttpURLConnection uc = Utils.getURL(stURL, stMethod, stContent,
				stReferURL, stCookie, isLogout);
		if(uc != null){
			callback.resultOk(uc);
		}else{
			callback.resultCancel(null);
		}
	}
	  
}
