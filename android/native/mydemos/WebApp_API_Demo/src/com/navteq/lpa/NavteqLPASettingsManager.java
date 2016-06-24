package com.navteq.lpa;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//import com.navteq.lpa.webapp.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * NavteqLPASettingsManager.java
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 *
 */
public class NavteqLPASettingsManager
{
	public static final String DOMAIN_KEY = "Dom. Override";
	private static final String AFFILIATEID_KEY = "Affiliate ID";
	private static final String PARTNERNAME_KEY = "Partner Name";
	private static final String SECURITY_KEY = "Security Key";
	private static final String LAT_KEY = "Lat";
	private static final String LON_KEY = "Lon";
	public static final String ENVIRONMENT_KEY = "Domain";
	
	private static final String DEFAULT_AFFILIATEID = "TEST";		//"b4324574001";
	private static final String DEFAULT_PARTNERNAME = "webkit";		//"Nokia";
	private static final String DEFAULT_SECURITY = "12345";
	private static final String DEFAULT_LAT = "37.774048";
	private static final String DEFAULT_LON = "-122.437692";
	private static final String DEFAULT_SERVER = "";
	
	public static final String WALLET_PATH = "getAdWallet/?";	
	public static final String FINDER_PATH = "dealfinder/getPage.php/?";
	
	private Map<String, String> settingsMap = new LinkedHashMap<String, String>();
	private Map<String, String> environmentMap = new HashMap<String, String>();
	private Map<String, String> mapBackgroundEnvrionmentMap = new HashMap<String, String>();
	
	public static final String ENV_STAGE_KEY = "Stage";
	private static final String ENV_STAGE_VAL = "http://stage.lpaweb.net/";
	
	public static final String ENV_QA_KEY = "QA";
	private static final String ENV_QA_VAL = "http://test.lpaweb.net/";
	
	public static final String ENV_INT_KEY = "Integration";
	private static final String ENV_INT_VAL = "http://int.mcg.lpad.mobi/";
	
	private Context context;
	
	/**
	 * This class manages various settings used by the Reference Application.
	 * 
	 * @param context
	 */
    private NavteqLPASettingsManager(Context context)
    {
    	this.context = context;
    	
    	environmentMap.put(ENV_STAGE_KEY, ENV_STAGE_VAL);
    	environmentMap.put(ENV_QA_KEY, ENV_QA_VAL);
    	environmentMap.put(ENV_INT_KEY, ENV_INT_VAL);    	
    	
    	mapBackgroundEnvrionmentMap.put(ENV_STAGE_KEY, "http://silap.ilap-na.net/");
    	mapBackgroundEnvrionmentMap.put(ENV_QA_KEY, "http://silap.ilap-na.net/");
    	mapBackgroundEnvrionmentMap.put(ENV_INT_KEY, "http://silap.ilap-na.net/");
    	
		// initialize default settings
    	initializeSettingsMap();
    	
    	// lood saved settings from disk
    	readSettingsFromDisk();
    	
		// setup manditory data
		setAffiliateSuppliedData();
    }

    private void initializeSettingsMap()
    {
    	settingsMap.clear();
    	
    	// generate defaults
    	settingsMap.put(ENVIRONMENT_KEY, ENV_STAGE_KEY);
	    settingsMap.put(DOMAIN_KEY, DEFAULT_SERVER);
	    settingsMap.put(AFFILIATEID_KEY, DEFAULT_AFFILIATEID);
	    settingsMap.put(PARTNERNAME_KEY, DEFAULT_PARTNERNAME);
	    settingsMap.put(SECURITY_KEY, DEFAULT_SECURITY);
	    settingsMap.put(LAT_KEY, DEFAULT_LAT);
	    settingsMap.put(LON_KEY, DEFAULT_LON);
    }
    
    /**
     * Returns an instance of the singleton SettingsManager. Will
     * be created if instance is null.
     */
    public static NavteqLPASettingsManager getInstance(Context context)
    {
      if (instance == null)
    	  instance = new NavteqLPASettingsManager(context);
      
      return instance;
    }
    
    /**
     * Resets the settings to default values
     */
    public void resetSettings()
    {
    	settingsMap.clear();
    	
    	// generate defaults
    	settingsMap.put(ENVIRONMENT_KEY, ENV_STAGE_KEY);
	    settingsMap.put(DOMAIN_KEY, DEFAULT_SERVER);
	    settingsMap.put(AFFILIATEID_KEY, DEFAULT_AFFILIATEID);
	    settingsMap.put(PARTNERNAME_KEY, DEFAULT_PARTNERNAME);
	    settingsMap.put(SECURITY_KEY, DEFAULT_SECURITY);
	    settingsMap.put(LAT_KEY, DEFAULT_LAT);
	    settingsMap.put(LON_KEY, DEFAULT_LON);
	    
	    saveSettingsToDisk();
    }
    
    /**
     * Returns a map containing the settings key/value pairs.
     * @return
     */
    public Map<String, String> getSettingsMap()
    {
    	return settingsMap;
    }
    
    /**
     * Returns a String[] of keys that represents all of the stored
     * settings in NavteqLPASettingsManager.
     * 
     * @return
     */
    public String[] getSettingKeysAsArray()
    {
    	String [] keys = new String[settingsMap.size()];
    	
    	int index = 0;
    	for(String key : settingsMap.keySet())
    	{
    		keys[index] = key;
    		index++;
    	}
    	
    	return keys;
    }
    
    
    /**
     * Saves the specified key/value in the SettingsManager. Will
     * persist the data to disk and load it back on each launch.
     * 
     * @param key
     * @param value
     */
    public void setSettingValue(String key, String value)
    {
    	settingsMap.put(key, value);
    	
    	saveSettingsToDisk();
    	
    	setAffiliateSuppliedData();
    }
    
    /**
     * Returns the saved value for the specified key.
     * 
     * @param key
     * @return
     */
    public String getSettingValue(String key)
    {
    	return settingsMap.get(key);
    }
    
    /**
     * Returns the URL to use. If Domain setting overrides environment,
     * that will be used. 
     * 
     * @return
     */
    public String getDomainValue()
    {
    	if(!getSettingValue(DOMAIN_KEY).equals(""))
    	{
    		String override = getSettingValue(DOMAIN_KEY).toLowerCase();
    		
    		if(!override.startsWith("http"))
    			override = "http://" + override;
    		
    		if(!override.endsWith("/"))
    			override = override + "/";
    		
    		return  override;
    	}
    	
    	else
   			return environmentMap.get(getSettingValue(ENVIRONMENT_KEY));
    }
    
    /**
     * Generates a URL to retrieve a static map background relative to the
     * current location.
     * 
     * @return
     */
    public String getMapBackgroundURL()
    {
    	WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE); 
    	
    	DisplayMetrics displaymetrics = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(displaymetrics);
        int width = (int)(displaymetrics.widthPixels / displaymetrics.scaledDensity) ;
        int height = (int)(displaymetrics.heightPixels / displaymetrics.scaledDensity);
        
        String seed = NavteqLPASecurity.generateSecurityKey();
        String token = NavteqLPASecurity.generateSecurityToken(getSettingValue(PARTNERNAME_KEY), seed, getSettingValue(AFFILIATEID_KEY)); 
        double[] coords = NavteqLPASettingsManager.getInstance(context).getLocationCoordinates();
    	
        String campaignID = "420"; 
        
    	String url = mapBackgroundEnvrionmentMap.get(getSettingValue(ENVIRONMENT_KEY)) 
    		+ "/lpa//api1.aspx?a=getmap"
    		+ "&affiliatenametag=" + "TEST" 
	    	+ "&deviceserialnum=" + getHashedUID()
	    	+ "&partnername=" + "webkit"
	    	+ "&securityseed=" + seed
	    	+ "&securitytoken=" + token
	    	+ "&cid=" + campaignID
	    	+ "&sflonlist=" + coords[1]
	    	+ "&sflatlist=" + coords[0]
	    	+ "&imgwidth="+width
	    	+ "&imgheight="+height
	    	+ "&ver=2.2";
    	
    	Log.i("", url);
    	
    	return url;
    }
    
    private String getHashedUID()
    {
    	TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	
    	String uid = tManager.getDeviceId();
		if(uid == null)
			uid = "000000000000000";
		else 
			uid = NavteqLPASecurity.getHashedString(uid);
		
		return uid;
    }
    
    private void readSettingsFromDisk()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	
    	for(String key : settingsMap.keySet())
    	{
    		String value = prefs.getString(key, settingsMap.get(key));
    		settingsMap.put(key, value);
    	}
    }
    
    private void saveSettingsToDisk()
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	SharedPreferences.Editor editor = prefs.edit();
    	
    	for(String key : settingsMap.keySet())
    	{
    		editor.putString(key, settingsMap.get(key));
    	}
    	
    	editor.commit();
    }
    
    /**
     * Sets the affiliate supplied data in the NavteqLPASettingsManager
     * 
     * @param partnerName
     * @param affiliateNameTag
     */
    public void setAffiliateData(String partnerName, String affiliateNameTag)
    {
		setSettingValue(PARTNERNAME_KEY, partnerName);
		setSettingValue(AFFILIATEID_KEY, affiliateNameTag);
		
		setAffiliateSuppliedData();
    }
    
	/** 
	 * Applies the settings to the NavteqLPAQueryUtils so that they can be used to 
	 * construct URLs.  
	 */
	private void setAffiliateSuppliedData() {				
		
		String deviceserialnum = getHashedUID();
		
		double[] locationCoords = getLocationCoordinates();
		String longitude = new BigDecimal(locationCoords[1]).toPlainString();
		String latitude = new BigDecimal(locationCoords[0]).toPlainString();
		
		// generate security seed
		String seed = NavteqLPASecurity.generateSecurityKey();
		
		// generate and hash the security token
		String hashedToken = NavteqLPASecurity.generateSecurityToken(getSettingValue(SECURITY_KEY), seed, getSettingValue(AFFILIATEID_KEY));
		
		//These are mandatory fields to set for an ad to be returned
		NavteqLPAQueryUtils.setMandatoryFields(getSettingValue(AFFILIATEID_KEY), deviceserialnum, longitude, latitude, hashedToken);

		//These are optional fields to set (Security seed is only optional during testing
		NavteqLPAQueryUtils.setOptionalFields(getSettingValue(PARTNERNAME_KEY), seed);
	}

	/**
	 * Returns location coordinates as a double[2], with latitude in index 0, longitude
	 * in index 1. If location values are stored in settings, those will override GPS. 
	 * If GPS isn't available, default location points will be used.
	 * 
	 * @return double[2]
	 */
	public double[] getLocationCoordinates()
	{
		double[] locationCoords = new double[2];
		
		double dLon = -75.510664;
		double dLat = 40.055065;
		
		if(getSettingValue(LAT_KEY).equals("") 
				&& getSettingValue(LON_KEY).equals(""))
		{
			//External class for collecting user location, many examples exists
			Location location = NavteqLPALocationUtils.getLocation();
			
			if (location == null) 
			{
				// Lets just populate one..longitude=-75.510664&latitude=40.055065
				boolean isEmulator = "000000000000000".equals(getHashedUID()); 
				if(isEmulator)
				{
					dLon = -75.510664;
					dLat = 40.055065;
					
					Log.e(getClass().getName(), "Running in emulator - setting location to " + dLat + ", " + dLon);
				}
				else
					Log.d(getClass().getName(), "No location could be obtained from hardware at this time.");
			} 
			else 
			{
				dLon = location.getLongitude();
				dLat = location.getLatitude();
			}
		}
		// use values stored in the settings instead
		else 
		{
			String lon = getSettingValue(LON_KEY);
			String lat = getSettingValue(LAT_KEY);
			
			dLon = Double.valueOf((lon.equals("")?"0":lon)).doubleValue();
			dLat = Double.valueOf((lat.equals("")?"0":lat)).doubleValue();
		}
		
		locationCoords[0] = dLat;
		locationCoords[1] = dLon;
		
		return locationCoords;
	}
	
    private static NavteqLPASettingsManager instance;
}
