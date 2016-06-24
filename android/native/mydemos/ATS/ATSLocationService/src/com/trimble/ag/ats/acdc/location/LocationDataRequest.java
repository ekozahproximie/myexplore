/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name: ATSLocationService
 *
 * Module Name: com.trimble.ag.ats.acdc.location
 *
 * File name: LocationDataRequest.java
 *
 * Author: sprabhu
 *
 * Created On: 06-Jan-2016 5:35:03 pm
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.ats.acdc.location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.trimble.ag.ats.entity.Location;

/**
 * @author sprabhu
 * 
 */
public class LocationDataRequest {

	private static final String OPERATOR_NAME = "operatorName";
	private static final String VEHICLE_ID = "vehicleId";
	private static final String DEVICE_ID = "deviceId";
	private static final String ORG_ID = "orgId";
	private static final String USER_SELECTED_SESSIONID = "userSelectedSessionId";
	private static final String USER_SELECTED_WORKING_STATE = "userSelectedWorkingState";
	private static final String PRODUCTIVITY_STATE = "productivityState";
	private static final String SOURCE = "source";
	private static final String LOCATION_RUNNING_MESSAGE_COUNT = "locationRunningMessageCount";
	private static final String GENERATED_LOCAL = "generatedLocal";
	private static final String ID = "iD";
	private static final String ASSET_ID = "assetID";
	private static final String GENERATED_UTC = "generatedUTC";
	private static final String INSERT_UTC = "insertUTC";
	private static final String TIME_ZONE_NAME = "timezoneName";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String ALTITUDE = "altitude";
	private static final String SPEED = "speed";
	private static final String HEADING = "heading";
	private static final String LOCATION_AGE = "locationAge";
	private static final String LOCATION_UNCERTAINTY_UNIT = "locationUncertaintyUnit";
	private static final String LOCATION_UNCERTAINTY = "locationUncertainty";
	private static final String ISEMP = "isEmp";
	private static final String SWATH_WIDTH = "swathWidth";
	private static final String APPLICATION_WIDTH = "applicationWidth";
	private static final String AUTO_GUIDANCE_ENABLED = "autoGuidanceEnabled";
	private static final String IMPLEMENT_ENGAGED = "implementEngaged";
	private static final String OFFLINE_DISTANCE = "offlineDistance";
	private static final String GPS_QUALITY = "gpsQuality";
	private static final String OPERATOR_ID = "operatorId";
	private static final String HIGHQUALITY = "highQuality";
	private static final String EXTERNAL_ID = "externalId";
	private static final String HIDDEN = "hidden";
	private static final String TARGET_FIELD_ID = "targetFieldId";

	private Location location = null;

	private List<com.trimble.ag.ats.entity.Location> locations = null;

	/**
    * 
    */
	public LocationDataRequest(final Location location) {
		this.location = location;
	}


	public LocationDataRequest(
			List<Location> toBeSyncedLocations) {
		this.locations = toBeSyncedLocations;
	}

	public String getJSONArrayString() {
		JSONArray outerJsonArray = new JSONArray();
		for (Location locationObject : locations) {
			this.location = locationObject;
			outerJsonArray.put(getJSONObject());
		}
		return outerJsonArray.toString();

	}

	public JSONObject getJSONObject() {
		final JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put(LATITUDE, location.getLatitude());
			jsonObject.put(LONGITUDE, location.getLongtitude());
			jsonObject.put(ALTITUDE, location.getAltitude());
			jsonObject.put(SPEED, location.getSpeed());
			jsonObject.put(HEADING, location.getHeading());
			
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));	
		    String date = sdf.format(new Date(location.getTime()));
			jsonObject.put(GENERATED_UTC, date);
			jsonObject.put(INSERT_UTC,date );
			jsonObject.put(ASSET_ID, 1234);

		} catch (JSONException e) {

			e.printStackTrace();
		}
		return jsonObject;
	}
	
	public String getJSONString() {
		return getJSONObject().toString();
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

}
