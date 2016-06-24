/**
 * 
 */
package com.navteq.lpa;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing parameters that will be used for generating URLs.
 * 
 */
public class NavteqLPAQueryUtils  {
	
	private static boolean _mandatory = false;
	
	/** WebApp query parameter */
	private static Map<String, String> queryParams = new
		HashMap<String, String>();		

	/** Defined parameter names */
	static{																
		// Mandatory fields 
		queryParams.put("affiliatenametag", null);
		queryParams.put("deviceserialnum", null);
		queryParams.put("longitude", null);
		queryParams.put("latitude", null);
		// Optional fields
		queryParams.put("partnername", null);queryParams.put("securityseed", null);queryParams.put("securitytoken", null);
		// Ad relevant fields -->
		//queryParams.put("localepref", null);queryParams.put("campaignid", null);queryParams.put("keywords", null);
		//queryParams.put("d-int", null);queryParams.put("d-ag", null);queryParams.put("d-g", null);queryParams.put("d-ed", null);queryParams.put("d-inc", null);queryParams.put("d-hhs", null);queryParams.put("d-ch", null);queryParams.put("d-oc", null);queryParams.put("d-ms", null);
		//queryParams.put("spot", null);queryParams.put("distunit", null);queryParams.put("speed", null);queryParams.put("heading", null);queryParams.put("locacc", null);queryParams.put("travmod", null);
		// Operator fields
		//queryParams.put("smcc", null);queryParams.put("smnc", null);queryParams.put("nmcc", null);queryParams.put("nmnc", null);queryParams.put("user-agent", null);queryParams.put("user-ip", null);
	}	
	
	/**
	 * Adds a parameter to be used on the query URL. Example: addQueryParameter("campaignid","5")
	 * 
	 * @param paramName
	 * @param paramValue
	 */
	public static void addQueryParameter(String paramName, String paramValue)
	{
		queryParams.put(paramName, paramValue);
	}
	
	/**
	 * Remove a parameter from the query URL. Example: removeQueryParameter("campaignid")
	 * 
	 * @param paramName
	 */
	public static void removeQueryParamter(String paramName)
	{
		queryParams.remove(paramName);
	}
	
	/**
	 * Returns current map of teaser parameters
	 * Use this to add additional key-value pairs to parameter list 
	 * @return - current parameter map
	 */
	private static Map<String, String> getQueryParams() {
		return queryParams;
	}

	/**
	 * Generates a query URL string from the currently stored params.
	 */
    public static String buildQueryParamsString()
    {
    	Map<String, String> map = getQueryParams();
    	StringBuilder retVal = new StringBuilder();
    	for (Map.Entry<String, String> entry : map.entrySet()) {
    	    String key = entry.getKey();
    	    String value = entry.getValue();
    	    if(value!=null){
    	    	retVal.append(key);
    	    	retVal.append('=');
    	    	retVal.append(value);
    	    	retVal.append('&');
    	    }
    	}
    	//remove the final &
    	retVal.deleteCharAt(retVal.length()-1);
    	return retVal.toString();
    }
    
    /**
     * Returns true if the mandatory fields have been set on this class.
     * 
     * @return
     */
    public static boolean areMandatoryFieldsSet()
    {
    	return _mandatory;
    }

    /**
     * Set mandatory fields for the WepApp API to return an teaser ad
     * @param affiliatenametag - Designated by Navteq
     * @param deviceserialnum - Unique ID of a device
     * @param longitude - Current devices
     * @param latitude - Current devices
     * @param securityToken - The security token generated with NavteqLPASecurity
     */
	public static void setMandatoryFields(String affiliatenametag,
			String deviceserialnum, String longitude, String latitude, String securityToken) {					
		
		_mandatory = true;
		
		queryParams.put("affiliatenametag", affiliatenametag);
		queryParams.put("deviceserialnum",deviceserialnum );
		queryParams.put("longitude", longitude);
		queryParams.put("latitude", latitude);
		queryParams.put("securitytoken", securityToken);
		
	}

	/**
	 * Optional fields for affiliates to set
	 * @param partnername - Partner name for affiliate
	 * @param securityseed - security seed designated by Navteq
	 */
	public static void setOptionalFields(String partnername, String securityseed) {
		queryParams.put("partnername", partnername);
		queryParams.put("securityseed",securityseed );
		
	}   
	
}
