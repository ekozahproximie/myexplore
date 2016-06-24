package com.spime.groupon;

import android.os.Handler;

public class GrouponManager {
	public static final String JSON=".json";
	public static final String FORCE_HTTP_SUCCESS="&force_http_success=true";
	public static final String ST_TAG="Groupon";
	private static final String API_KEY="2b804fa0b6f4d4cccac963669f7969fbbebb22ae";
	 public static final String CLIENT_ID="client_id="+API_KEY;
	public static final String  STATUS_URL="https://api.groupon.com/status"+JSON+"?"+CLIENT_ID;
	public static final String SHOW_ALL="&show=all";
	public static final String SHOW_DEFAULT="&show=default";
	public static final String  DIVISIONS_URL=
		"http://api.groupon.com/v2/divisions"+JSON+"?"+CLIENT_ID+SHOW_ALL+FORCE_HTTP_SUCCESS;
	
	
	public static final String DEALS_URL="https://api.groupon.com/v2/deals";
	public static final String DIVISION_ID="&division_id=";
	public static final String LAT="&lat=";
	public static final String LNG="&lng=";
	public static final String DEFAULT_RADIUS ="10";
	public static final String RADIUS ="&radius="+DEFAULT_RADIUS;
	private  static GrouponManager MANAGER=null;
	public static final int DIVISIONS=1;
	public static final int DEALS=2;
	
	private GrouponManager(){
		
	}
	public static GrouponManager getInstance(){
		if(MANAGER == null){
			MANAGER=new GrouponManager();
		}
		return MANAGER;
	}
	public void sendRequest(String stURL,Handler handler,int iServiceCode){
		GrouponParser parser = new GrouponParser(stURL,handler,iServiceCode);
		parser.doJob();
		parser=null;
	}
}
