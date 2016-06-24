package com.trimble.agmantra.scout.acdc.cffe.res;

import android.content.Context;
import android.util.Log;

import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.dbutil.AgDataStoreResources;
import com.trimble.agmantra.entity.Farm;
import com.trimble.agmantra.entity.Field;
import com.trimble.agmantra.entity.User;
import com.trimble.agmantra.scout.acdc.ACDCResponse;
import com.trimble.agmantra.scout.acdc.MyJSONArray;
import com.trimble.agmantra.scout.acdc.MyJSONObject;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FieldResponse extends ACDCResponse {

	private static final String TAG ="ACDC";

	private static final String C_FIELDLIST = "fieldList";
	private static final String C_ID = "iD";
	private static final String C_NAME = "name";
	private static final String C_CLIENTID = "clientID";
	private static final String C_FARMID = "farmID";
	private static final String C_BOUNDARYUTC = "boundaryUTC";
	
	public void readFieldsResponse(String stLine, Context context) {

		if (stLine == null || stLine.length() == 0) {
			Log.i(TAG, "json string is null");
			return;
		}
		try {
			MyJSONObject fieldsJSON = new MyJSONObject(stLine);
			MyJSONArray fieldsJSONList = null;

 
			fieldsJSONList = new MyJSONArray(fieldsJSON.getString(C_FIELDLIST));

			List<Field> listFields = new ArrayList<Field>(
					fieldsJSONList.length());
			FarmWorksContentProvider contentProvider = FarmWorksContentProvider
					.getInstance(context);
			long lFieldID;
                        long lFarmID = 0;
                        String stName;
                        long lClientID = 0;
                        String stBoundaryUTC;
                        MyJSONObject jsonObject = null;
                        Field field = null;
                        int iFieldListSize = fieldsJSONList.length();
                        
			for (int i = 0; i < iFieldListSize; i++) {
			   jsonObject = fieldsJSONList.getJSONObject(i);

					lFieldID = jsonObject.getLong(C_ID);
					lFarmID = jsonObject.getLong(C_FARMID);
					if(lFarmID == -1){
					   lFarmID = FarmWorksContentProvider.D_UNKNOWN_CFFE_ID;
					}else{
					   Farm farm = contentProvider.getFarmByFarmId(lFarmID);
					   if(farm == null){
					      lFarmID = FarmWorksContentProvider.D_UNKNOWN_CFFE_ID;
					   }
					}
					stName = jsonObject.getString(C_NAME);
					lClientID = jsonObject.getLong(C_CLIENTID);
					stBoundaryUTC = jsonObject.getString(C_BOUNDARYUTC);
				
					field = new Field(lFieldID, stName, true, null, -1 , -1, -1, -1, -1, -1, -1, -1, -1, lFarmID, -1L);
					listFields.add(field);

			}
			contentProvider.insertFieldList(listFields);
			
		} catch (JSONException e) {

			Log.e(TAG, "paser error", e);
		}
	}
}
