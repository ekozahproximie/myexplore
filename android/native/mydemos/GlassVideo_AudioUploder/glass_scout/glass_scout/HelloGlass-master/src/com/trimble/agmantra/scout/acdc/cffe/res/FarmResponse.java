package com.trimble.agmantra.scout.acdc.cffe.res;

import android.content.Context;
import android.util.Log;

import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.entity.Client;
import com.trimble.agmantra.entity.Farm;
import com.trimble.agmantra.entity.User;
import com.trimble.agmantra.scout.acdc.ACDCResponse;
import com.trimble.agmantra.scout.acdc.MyJSONArray;
import com.trimble.agmantra.scout.acdc.MyJSONObject;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FarmResponse extends ACDCResponse {

	private static final String TAG = "ACDC";

	private static final String C_FARMLIST = "farmList";
	private static final String C_ID = "iD";
	private static final String C_NAME = "name";
	private static final String C_CLIENTID = "clientID";
	
	public void readFarmsResponse(String stLine, Context context) {

		if (stLine == null || stLine.length() == 0) {
			Log.i(TAG, "json string is null");
			return;
		}
		try {
			MyJSONObject farmsJSON = new MyJSONObject(stLine);
			MyJSONArray farmsJSONList = null;

				farmsJSONList = new MyJSONArray(
						farmsJSON.getString(C_FARMLIST));
			List<Farm> listFarms = new ArrayList<Farm>(
					farmsJSONList.length());
			FarmWorksContentProvider contentProvider = FarmWorksContentProvider
					.getInstance(context);
			long lFarmID;
                        String stName;
                        long lClientID;
                        int iFarmListSize = farmsJSONList.length();
                        MyJSONObject jsonObject = null;
                        Farm farm = null;
                        
			for (int i = 0; i < iFarmListSize; i++) {
			   
			   jsonObject = farmsJSONList.getJSONObject(i);

					lFarmID = jsonObject.getLong(C_ID);
					stName = jsonObject.getString(C_NAME);
					lClientID = jsonObject.getLong(C_CLIENTID);
					if(lClientID == -1){
					   lClientID = FarmWorksContentProvider.D_UNKNOWN_CFFE_ID;
					}else{
                                           Client client = contentProvider.getClientByClientId(lClientID);
                                           if(client == null){
                                              lClientID = FarmWorksContentProvider.D_UNKNOWN_CFFE_ID;
                                           }
                                        }

					farm = new Farm(lFarmID, stName, -1, -1, true, -1, lClientID);

				listFarms.add(farm);

			}

			contentProvider.insertFarmList(listFarms);
			
		} catch (JSONException e) {

			Log.e(TAG, "paser error", e);
		}
	}
}
