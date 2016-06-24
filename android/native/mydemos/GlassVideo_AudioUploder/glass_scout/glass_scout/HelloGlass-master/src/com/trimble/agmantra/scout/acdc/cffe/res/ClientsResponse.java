package com.trimble.agmantra.scout.acdc.cffe.res;

import android.content.Context;
import android.util.Log;

import com.trimble.agmantra.datacontroller.FarmWorksContentProvider;
import com.trimble.agmantra.entity.Client;
import com.trimble.agmantra.entity.User;
import com.trimble.agmantra.scout.acdc.ACDCResponse;
import com.trimble.agmantra.scout.acdc.MyJSONArray;
import com.trimble.agmantra.scout.acdc.MyJSONObject;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ClientsResponse extends ACDCResponse {

	private static final String TAG = "ACDC";

	private static final String C_CLIENTLIST = "clientList";
	private static final String C_ID = "iD";
	private static final String C_NAME = "name";
	
	public void readClientsResponse(String stLine, Context context) {

		if (stLine == null || stLine.length() == 0) {
			Log.i(TAG, "json string is null");
			return;
		}
		try {
			MyJSONObject clientsJSON = new MyJSONObject(stLine);
			MyJSONArray clientsJSONList = null;


			clientsJSONList = new MyJSONArray(clientsJSON.getString(C_CLIENTLIST));

			List<Client> listClients = new ArrayList<Client>(
					clientsJSONList.length());
			FarmWorksContentProvider contentProvider = FarmWorksContentProvider
					.getInstance(context);
			MyJSONObject jsonObject = null;
			long lClientID;
                        String stName;
                        Client client = null;
                        int iClientListSize = clientsJSONList.length();
                        
			for (int i = 0; i < iClientListSize; i++) {
			         jsonObject = clientsJSONList.getJSONObject(i);

				lClientID = jsonObject.getLong(C_ID);
				stName = jsonObject.getString(C_NAME);

				client = new Client(lClientID, stName, true, -1, -1, -1);

				listClients.add(client);
			}

			contentProvider.insertClientList(listClients);
			
		} catch (JSONException e) {

			Log.e(TAG, "paser error", e);
		}
	}

}
