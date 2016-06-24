package com.trimble.vilicus.acdc.asset.res;

import android.content.Context;
import android.util.Log;

import com.trimble.mobile.android.util.Utils;
import com.trimble.vilicus.acdc.ACDCApi;
import com.trimble.vilicus.acdc.ACDCResponse;
import com.trimble.vilicus.acdc.MyJSONArray;
import com.trimble.vilicus.acdc.MyJSONObject;
import com.trimble.vilicus.db.VilicusContentProvider;
import com.trimble.vilicus.entity.Asset;
import com.trimble.vilicus.entity.User;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AssetResponse extends ACDCResponse{
	private static final String C_ID = "iD";
	private static final String C_NAME = "name";
	private static final String C_MAKE = "make";
	private static final String C_TYPE = "type";
	private static final String C_ICONID = "iconID";

        public static final String C_ASSETLIST = "assetList";	
        public static final String C_UPDATEDUTC="updatedUTC";
	private static final String C_LATITUDE="latitude";
	private static final String C_LONGITUDE="longitude";
	private static final String C_SPEED="speed";
	private static final String C_HEADING="heading";
	private static final String C_STATUS="status";
	private static final String C_ENGINEHOURS="engineHours";
	private static final String C_APPLIEDOFFSET="appliedOffset";
	private static final String C_ALARM_COUNT="alarmCount";
	private static final String C_NEXTBOOKMARKUTC="nextBookmarkUTC";
	
	private static final String TAG = "ACDC";
	
	public  HashMap<Long,Asset> readAssetResponse(String stLine,Context context) {
	   HashMap<Long,Asset>  listAsset=null;
			if(stLine == null || stLine.length() == 0){
				Log.i(TAG, "json string is null");
				return null;
			}
		try {
			MyJSONObject assetJSON = new MyJSONObject(stLine);
			MyJSONArray assetJSONList =null;
			
                        assetJSONList = new MyJSONArray(assetJSON.getString(C_ASSETLIST));

			listAsset = new HashMap<Long,Asset> (assetJSONList.length());
			//VilicusContentProvider contentProvider = VilicusContentProvider.getInstance(context);
			
			final User user = ACDCApi.getInstance(context).getCurrentUser();
			long lAssetID;
                        String stName;
                        String stMake;
                        String stType;
                        int iIConID;
                        MyJSONObject jsonObject = null;
                        Asset asset = null;
                        
			for (int i = 0; i < assetJSONList.length(); i++) {
			   jsonObject = assetJSONList.getJSONObject(i);
				
				lAssetID = jsonObject.getLong(C_ID);
				stName = jsonObject.getString(C_NAME);
				stMake = jsonObject.getString(C_MAKE);
				stType = jsonObject.getString(C_TYPE);
				iIConID = jsonObject.getInt(C_ICONID);
				
//				//!!!! test patch
//				if(i > 0){
//				   stType="Combine";
//				}
				
	                          if(user == null){
	                             Log.e(TAG, "readAssetResponse of asset no user name found");
	                             continue;
	                          }
	                          
	                        asset = new Asset(lAssetID, stName, stType,
						stMake, null, iIConID, null, null, null, null, null,user.getUserid());
	                        if(listAsset.get(lAssetID) != null){
	                           Log.e(TAG, "readAssetResponse of asset id dublicate :"+lAssetID);
	                        }else{
	                           listAsset.put(lAssetID,asset);
	                        }
				
//				List<Device> deviceLists=getDeviceList(jsonObject,(long) iDeviceID);
//				contentProvider.insert_updateDeviceList(deviceLists);
			}
			//contentProvider.insert_updateAssetList(listAsset,true,false);
		} catch (JSONException e) {

			Log.e(TAG, "paser error", e);
		}
		
		return listAsset;
	}
	public void readAssetStatusResponse(String stLine,Context context,final HashMap<Long,Asset> assetMap) {
	
				if(stLine == null || stLine.length() == 0){
				Log.i(TAG, "json string is null");
				return;
			  }
		try {
			MyJSONObject assetJSON = new MyJSONObject(stLine);
			String stSuccess=null;

		   stSuccess= assetJSON.getString(ACDCApi.C_RESULT_CODE);
			
			boolean isSucess=stSuccess.equals("Success");
			if(!isSucess){
				Log.i(TAG, "Get Asset Status success return false");
				return;
			}
			VilicusContentProvider contentProvider = VilicusContentProvider.getInstance(context);
			List<Asset> assets=getAssetListStatus(assetJSON, assetMap);
			/*String nextUpdateUTC=assetJSON.getString(NEXTBOOKMARKUTC);
			AssetManager assetManager =AssetManager.getInstance(context);
			assetManager.storeNextUpdateTime(nextUpdateUTC);*/
                                
                       
			contentProvider.insert_updateAssetList(assets,true,false);
			//contentProvider.insert_updateAssetList(assets,false,true);
		} catch (JSONException e) {

			Log.e(TAG, "paser error", e);
		}
	}
	

	private  List<Asset> getAssetListStatus(MyJSONObject jsonObject,final HashMap<Long,Asset> assetMap){
		List<Asset> assets=null;
		try {
			if (jsonObject == null) {
				return assets;
			}
			MyJSONArray assetList = null;
			

			assetList = new MyJSONArray(jsonObject.getString(C_ASSETLIST));
			
			assets = new ArrayList<Asset>(assetList.length());
			
			MyJSONObject assetData = null;
			Asset asset = null;
			int iAssetListSize = assetList.length();
			for (int i = 0; i < iAssetListSize; i++) {
				
			        assetData = assetList.getJSONObject(i);
			        asset = getAssetStatus(assetData,assetMap);
				if(asset != null){
				   assets.add(asset);	
				}
			}
			if(assetMap.size() > 0){
			  Set<Long> assetNotwithLocation= assetMap.keySet();
			  for (Long lassetID : assetNotwithLocation) {
			     asset=assetMap.get(lassetID);
			     if(asset != null){
                                assets.add(asset);   
                             }
                           }
			}
			

		} catch (JSONException e) {
			Log.e(TAG, "paser error", e);
		}
		return assets;
		
	}
	
	private Asset getAssetStatus(MyJSONObject jsonObject,final HashMap<Long,Asset> assetMap){
		Asset asset=null;
		if(jsonObject == null || assetMap == null){
		   Log.e(TAG, "getAssetStatus return null :"+jsonObject +","+assetMap);
			return asset;
		}
		try{
			
			long lAssetID;
			String updateUTC;
			double latitude;
			double longitude;
			double speed;
			double heading;
			double engineHours;
			String status;
			double appliedOffset;
			double alarm;
			

				lAssetID = jsonObject.getLong(C_ID);
				updateUTC = jsonObject.getString(C_UPDATEDUTC);
				latitude = jsonObject.getDouble(C_LATITUDE);
	        	 longitude = jsonObject.getDouble(C_LONGITUDE);
	        	 speed = jsonObject.getDouble(C_SPEED);
	        	 heading = jsonObject.getDouble(C_HEADING);
	        	 engineHours = jsonObject.getDouble(C_ENGINEHOURS);
				status = jsonObject.getString(C_STATUS);
				appliedOffset = jsonObject.getDouble(C_APPLIEDOFFSET);
				alarm = jsonObject.getInt(C_ALARM_COUNT);

			asset=assetMap.get(lAssetID); 
			assetMap.remove(lAssetID);
			if(asset != null){
				asset.setUpdatedUTC(Utils.readServerDateInUTC(updateUTC));
				asset.setLatitude(latitude);
				asset.setLongitude(longitude);
				asset.setSpeed(speed);
				asset.setHeading(heading);
				asset.setStatus(status);
				
			}else{
				Log.e(TAG, "Get Asset By ID return null :"+lAssetID);
				
			}
			
		} catch (JSONException e) {
			Log.e(TAG, "paser error", e);
		}
		return asset;
	}
	

}
