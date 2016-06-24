package com.trimble.ag.ats.acdc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Collection;

public class MyJSONArray extends JSONArray {

	public MyJSONArray() {
		
	}

	public MyJSONArray(Collection copyFrom) {
		super(copyFrom);
		
	}

	public MyJSONArray(JSONTokener readFrom) throws JSONException {
		super(readFrom);
		
	}

	public MyJSONArray(String json) throws JSONException {
		super(json);
		
	}

	@Override
	public MyJSONObject getJSONObject(int index) throws JSONException {
		final JSONObject jsonObject = super.getJSONObject(index);
		
		return  new MyJSONObject(jsonObject.toString()) ;
	}
}
