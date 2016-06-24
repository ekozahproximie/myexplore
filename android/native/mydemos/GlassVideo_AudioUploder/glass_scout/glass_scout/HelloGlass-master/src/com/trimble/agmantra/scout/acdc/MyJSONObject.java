package com.trimble.agmantra.scout.acdc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;
/**
 * 
 * @author kmuruga
 *
 */
public class MyJSONObject extends JSONObject {

	private static final String NULL_VALUE = "null";
	
	public MyJSONObject() {
		
	}

	public MyJSONObject(Map copyFrom) {
		super(copyFrom);
		
	}

	public MyJSONObject(JSONTokener readFrom) throws JSONException {
		super(readFrom);
		
	}

	public MyJSONObject(String json) throws JSONException {
		super(json);
		
	}

	public MyJSONObject(JSONObject copyFrom, String[] names)
			throws JSONException {
		super(copyFrom, names);
		
	}

	@Override
	public long getLong(String name) throws JSONException {
		String stValue = getString(name);

		if (stValue == null || stValue.length() == 0 || stValue.equals(NULL_VALUE)) {
			return -1;
		}
		return Long.parseLong(stValue);
	}

         @Override
         public boolean getBoolean(String name) throws JSONException {
            String stValue = getString(name);

            if (stValue == null || stValue.length() == 0 || stValue.equals(NULL_VALUE)) {
                    return false;
            }
         return Boolean.parseBoolean(stValue);
         }
	@Override
	public int getInt(String name) throws JSONException {
		String stValue = getString(name);

		if (stValue == null || stValue.length() == 0 || stValue.equals(NULL_VALUE)) {
			return -1;
		}
		return Integer.parseInt(stValue);
	}

	@Override
	public double getDouble(String name) throws JSONException {
		String stValue = getString(name);

		if (stValue == null || stValue.length() == 0 || stValue.equals(NULL_VALUE)) {
			return -1;
		}
		return Double.parseDouble(stValue);
	}
	
	@Override
	public String getString(String name) throws JSONException {
		String stValue=null;
		try {
			 stValue=super.getString(name);	
		} catch (JSONException e) {
			//ignore this exception for key not found 
		        name=getNon_CamelCaseString(name);
			stValue=super.getString(name);
		}
		
		
		return stValue;
	}
	@Override
	public MyJSONArray getJSONArray(String name) throws JSONException {
		JSONArray  jsonArray =null;
		try {
			jsonArray=super.getJSONArray(name);	
		} catch (JSONException e) {
			//ignore this exception for key not found 
		         name=getNon_CamelCaseString(name);
			jsonArray=super.getJSONArray(name);
		}
		
		return new MyJSONArray(jsonArray.toString());
	}
	/* (non-Javadoc)
	* @see org.json.JSONObject#getJSONObject(java.lang.String)
	*/
	@Override
	public MyJSONObject getJSONObject(String name) throws JSONException {
	MyJSONObject jsonObject =null;
	   try{
	      
	      JSONObject jsonObject2 =super.getJSONObject(name);
	      jsonObject= new MyJSONObject(jsonObject2.toString());
	   }catch(JSONException exception){
	    //ignore this exception for key not found 
	      name=getNon_CamelCaseString(name);
	      JSONObject jsonObject2 =super.getJSONObject(name);
              jsonObject= new MyJSONObject(jsonObject2.toString());
	   }
	return jsonObject;
	}
	
	private String getNon_CamelCaseString(String name){
	   if(name == null || name.length() == 0){
	      return name;
	   }
	   final StringBuffer buffer =new StringBuffer(name);
           buffer.replace(0, 1, String.valueOf(buffer.charAt(0)).toUpperCase());
           name=buffer.toString();
           return name;
	}
	
}
