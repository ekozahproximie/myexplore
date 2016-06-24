package com.trimble.ag.acdc;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
/**
 * 
 * @author kmuruga
 *
 */
public class MyJSONObject extends JSONObject {

         public static final String NULL_VALUE = "null";
	
	private HashMap<String, String> myKeyMap=null;
	
	private static final String LOG=MyJSONObject.class.getSimpleName();
	
	public MyJSONObject() {
		
	}

	public MyJSONObject(Map copyFrom) {
		super(copyFrom);
		initKeyMap();
	}

	public MyJSONObject(JSONTokener readFrom) throws JSONException {
		super(readFrom);
		initKeyMap();
	}

	public MyJSONObject(String json) throws JSONException {
		super(json);
		initKeyMap();
	}

	public MyJSONObject(JSONObject copyFrom, String[] names)
			throws JSONException {
		super(copyFrom, names);
		initKeyMap();
	}
	private void initKeyMap(){
	   final Iterator<String> keysIterator= keys();
	    myKeyMap = new HashMap<String, String>(length());
	   if(keysIterator != null){
	      while(keysIterator.hasNext()) {
	         final String myOriginalKey = (String)keysIterator.next();
	         if(myOriginalKey != null){
	            final String stLowerCaseKey= getLowerCaseString(myOriginalKey);
	            
	            if( myKeyMap.get(stLowerCaseKey) == null){
	                myKeyMap.put(stLowerCaseKey, myOriginalKey);   
	            }else{
	               Log.e(LOG, "Lowercase key already persent in the map:"+stLowerCaseKey+","+myOriginalKey);
	            }
	            
	         }
	      }
	   }
	}
	@Override
	public long getLong(String name) throws JSONException {
		String stValue = getString(name);

		if ( ! isValueValid(stValue)) {
			return -1;
		}
		return Long.parseLong(stValue);
	}

         @Override
         public boolean getBoolean(String name) throws JSONException {
            String stValue = getString(name);

            if (! isValueValid(stValue)) {
                    return false;
            }
         return Boolean.parseBoolean(stValue);
         }
	@Override
	public int getInt(String name) throws JSONException {
		String stValue = getString(name);

		if (! isValueValid(stValue)) {
			return -1;
		}
		return Integer.parseInt(stValue);
	}

	@Override
	public double getDouble(String name) throws JSONException {
		String stValue = getString(name);

		if (! isValueValid(stValue)) {
			return -1;
		}
		return Double.parseDouble(stValue);
	}
	  public String getString_Optional(String name) throws JSONException {
             String stValue=null;
             try {
                      stValue=getString(name); 
             } catch (JSONException e) {
                     //ignore this exception for key not found 
                   final String stOriginal=name;
                     name=getNon_CamelCaseString(name);
                     try{
                       stValue=super.getString(name);
                     } catch (JSONException e1) {
                        //ignore
                        Log.e(LOG, "no value for key:"+stOriginal);
                     }
             }
             
             return stValue;
     }
     public double getDouble_Optional(String name) throws JSONException {
        String stValue=null;
        double dData=-1;
        try {
                 stValue=getString(name); 
        } catch (JSONException e) {
                //ignore this exception for key not found 
              final String stOriginal=name;
                name=getNon_CamelCaseString(name);
                try{
                  stValue=super.getString(name);
                } catch (JSONException e1) {
                   //ignore
                   Log.e(LOG, "no value for key:"+stOriginal);
                }
        }
        
        if( isValueValid(stValue)){
           dData=Double.parseDouble(stValue);
        }
        return dData;
}
     
     public int getInt_Optional(String name) throws JSONException {
        String stValue=null;
        int dData=-1;
        try {
                 stValue=getString(name); 
        } catch (JSONException e) {
                //ignore this exception for key not found 
              final String stOriginal=name;
                name=getNon_CamelCaseString(name);
                try{
                  stValue=super.getString(name);
                } catch (JSONException e1) {
                   //ignore
                   Log.e(LOG, "no value for key:"+stOriginal);
                }
        }
        
        if( isValueValid(stValue)){
           dData=Integer.parseInt(stValue);
        }
        return dData;
}
     
     public long getLong_Optional(String name) throws JSONException {
        String stValue=null;
        long dData=-1;
        try {
                 stValue=getString(name); 
        } catch (JSONException e) {
                //ignore this exception for key not found 
              final String stOriginal=name;
                name=getNon_CamelCaseString(name);
                try{
                  stValue=super.getString(name);
                } catch (JSONException e1) {
                   //ignore
                   Log.e(LOG, "no value for key:"+stOriginal);
                }
        }
        
        if( isValueValid(stValue)){
           dData=Long.parseLong(stValue);
        }
        return dData;
}
	private String getLowerCaseString(final String name){
	   if(name == null){
	      return null;
	   }
           return name.toLowerCase(Locale.US);
        }
	private String getMappingOriginalKey(final String stKey){
	   if(stKey == null){
	      Log.e(LOG, "Json key  is null");
              return null;
	   }
	   final String stLowerCaseKey=getLowerCaseString(stKey);
           
           final String stOriginalKeyInJson=myKeyMap.get(stLowerCaseKey);
           
           if(stOriginalKeyInJson == null){
             // Log.e(LOG, "Json key :"+stKey +", not persent in the original key list:"+stLowerCaseKey);
              return null;
           }
           return stOriginalKeyInJson;
	}
	
	public boolean isExist(final String stKey){
	   boolean isValueNull=isNull(stKey);
	   if(isValueNull){
	      final String stOriginalKeyInJson=getMappingOriginalKey(stKey);
	      isValueNull=isNull(stOriginalKeyInJson);
	   }
	   return ( isValueNull == false );
	}
	private String getIngoreCaseKeyValue(final String stKey) throws JSONException{
	   
	   final String stOriginalKeyInJson=getMappingOriginalKey(stKey);
	   if(stOriginalKeyInJson == null){
	      return null;
	   }
	   // additional check not required here .because we are mirroring "nameValuePairs"
	   /*if(isNull(stOriginalKeyInJson) ){
	      Log.e(LOG, "object has no mapping for name or mapping whose value is NULL :"+stOriginalKeyInJson);
	      return null;
	   }*/
	   
	   final String stValue=super.getString(stOriginalKeyInJson);
	   
	   return stValue;
	}
	@Override
	public String getString(String stOriginalKey) throws JSONException {
		String stValue=null;
		try {
		         if(isNull(stOriginalKey)){
			    stValue=getIngoreCaseKeyValue(stOriginalKey);
		         }else{
		            stValue=super.getString(stOriginalKey);
		         }
		} catch (JSONException e) {
			//ignore this exception for key not found 
		        stOriginalKey=getNon_CamelCaseString(stOriginalKey);
			stValue=super.getString(stOriginalKey);
		}
		
		return stValue;
	}
	@Override
	public MyJSONArray getJSONArray(String name) throws JSONException {
		
	   MyJSONArray  myJsonArray =null;
		
		try {
		   if(isNull(name)){
		        name =getMappingOriginalKey(name);
		   }
		        if(name == null){
		           Log.e(LOG, "getJSONArray key is null");
		           return myJsonArray;
		        }
		        final JSONArray jsonArray=super.getJSONArray(name);
		        myJsonArray=new  MyJSONArray(jsonArray.toString());
		} catch (JSONException e) {
			//ignore this exception for key not found 
		         name=getNon_CamelCaseString(name);
			final JSONArray jsonArray=super.getJSONArray(name);
			myJsonArray=new  MyJSONArray(jsonArray.toString());
		}
		
		return myJsonArray ;
	}
        
        public MyJSONArray getJSONArray_Optional(String name) throws JSONException {
                JSONArray  jsonArray =null;
                MyJSONArray myJSONArray=null;
                try {
                      if(isNull(name)){
                            name =getMappingOriginalKey(name);
                         }
                      if(name == null){
                         Log.e(LOG, "getJSONArray key is null");
                         return myJSONArray;
                      }
                        jsonArray=super.getJSONArray(name);     
                } catch (JSONException e) {
                   final String stOriginal=name;
                        //ignore this exception for key not found 
                         name=getNon_CamelCaseString(name);
                         try{
                            jsonArray=super.getJSONArray(name);
                         } catch (JSONException e1) {
                            //ignore
                            Log.e(LOG, "no value for key:"+stOriginal);
                         }
                }
                if(jsonArray != null){
                   myJSONArray= new MyJSONArray(jsonArray.toString());
                }
                return myJSONArray;
        }
	@Override
	public MyJSONObject getJSONObject(String name) throws JSONException {
	MyJSONObject jsonObject =null;
	   try{
	      if(isNull(name)){
	        name =getMappingOriginalKey(name);
	      }
              if(name == null){
                 Log.e(LOG, "getJSONObject key is null");
                 return jsonObject;
              }
	     final JSONObject jsonObject2 =super.getJSONObject(name);
	      jsonObject= new MyJSONObject(jsonObject2.toString());
	   }catch(JSONException exception){
	    //ignore this exception for key not found 
	      name=getNon_CamelCaseString(name);
	     final JSONObject jsonObject2 =super.getJSONObject(name);
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
	
	private void updateNewKey(final String name,final Object value){
	   if (value == null) {
	      myKeyMap.remove(name);
	      return;
	   }
	   myKeyMap.put(getLowerCaseString(name), name);
	}
	private void updateNewKey(final String name){
           myKeyMap.put(getLowerCaseString(name), name);
        }
	@Override
	public JSONObject put(String name, boolean value) throws JSONException {
	   updateNewKey(name);
	return super.put(name, value);
	}
	
	@Override
	public JSONObject put(String name, double value) throws JSONException {
	   updateNewKey(name);
	return super.put(name, value);
	}
	
	@Override
	public JSONObject put(String name, int value) throws JSONException {
	   updateNewKey(name);
	return super.put(name, value);
	}
	
	@Override
	public JSONObject put(String name, long value) throws JSONException {
	   updateNewKey(name);
	return super.put(name, value);
	}
	
	@Override
	public JSONObject put(String name, Object value) throws JSONException {
	   updateNewKey(name, value);
	return super.put(name, value);
	}
	
	@Override
	public JSONObject putOpt(String name, Object value) throws JSONException {
	   if(name == null || value == null){
	      return this;
	   }
	   updateNewKey(name, value);
	return super.putOpt(name, value);
	}
	@Override
	public Object remove(String name) {
	 myKeyMap.remove(name);
	return super.remove(name);
	}
	
	private boolean isValueValid(final String stValue){
	   
	   boolean isValid = true;
	   if(stValue == null || stValue.length() == 0 || stValue.equalsIgnoreCase(NULL_VALUE)){
	      isValid = false;
	   }
	   
	   return isValid;
	}
}
