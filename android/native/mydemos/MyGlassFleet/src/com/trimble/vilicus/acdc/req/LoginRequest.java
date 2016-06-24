package com.trimble.vilicus.acdc.req;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class LoginRequest {

	public static final String TAG = "LOGIN REQUEST";

	private final static String USER_NAME = "username";

	private final static String USER_PASSWORD = "password";

	private final static String APPLICATION_NAME = "ApplicationName";

	public String username = null;

	public String password = null;

	public String appname = null;
	
	public String organization=null;

	public LoginRequest() {

	}
	public LoginRequest(String username,String password, String appname,String organizationID ) {
		this.username=username;
		this.password=password;
		this.appname=appname;
		this.organization=organizationID;
	}
	private String getJsonString() {

		StringBuffer buffer = new StringBuffer();

		JSONObject jsonObject = new JSONObject();

		try {
			if (username == null) {
				username = "";
			}

			jsonObject.put(USER_NAME, username);

			if (password == null) {
				password = "";
			}
			jsonObject.put(USER_PASSWORD, password);

			if (appname == null) {
				appname = "";
			}
			jsonObject.put(APPLICATION_NAME, appname);

			buffer.append(jsonObject.toString());

		} catch (JSONException e) {

			Log.i(TAG, e.getMessage());
		}
		return buffer.toString();
	}
	
	
	 private String getURLEncodedString() {

           StringBuffer buffer = new StringBuffer();
           
           buffer.append(USER_NAME.toLowerCase());
           buffer.append("=");
           String stUTFEncodeUserName=username;
           try {
              stUTFEncodeUserName = URLEncoder.encode(stUTFEncodeUserName,"UTF-8");
           } catch (UnsupportedEncodingException e) {
              Log.e(TAG, e.getMessage(),e);
              e.printStackTrace();
           }
           buffer.append(stUTFEncodeUserName);
          
           buffer.append("&");
           buffer.append(APPLICATION_NAME);
           buffer.append("=");
           buffer.append(appname);
           buffer.append("&");
           buffer.append(USER_PASSWORD.toLowerCase());
           buffer.append("=");
           buffer.append(password);
           String stData=buffer.toString();
         
           
           return stData ; 
   }
	 public String getRequestData(final boolean isJSON){
	    return isJSON ? getJsonString():getURLEncodedString();
	 }
}
