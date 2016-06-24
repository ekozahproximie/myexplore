package com.trimble.agmantra.login;

import android.util.Log;

import com.trimble.agmantra.entity.Organization;
import com.trimble.agmantra.scout.acdc.ACDCResponse;
import com.trimble.agmantra.scout.acdc.MyJSONObject;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi;

import org.json.JSONException;

import java.util.Iterator;
import java.util.Vector;

public class LoginResponse extends ACDCResponse {

	private static final String C_TICKET = "access_token";
	
	public static final String SUCCESS = "Success";

	private static final String C_ORG_ID = "organizationID";

	private static final String C_ORG_NAME = "organizationName";
	

	private static final String C_ORGANIZATIONS = "organizations";

	private transient Vector<Organization> vecOrganizations = null;

	// private static final String ERRORCODE = "ErrorCode";
	// private static final String MEANING = "Meaning";

	public boolean isSuccess = false;
	public String ticket = null;
	// public String stErrorCode = null;
	public String stMeaning = "";
	public boolean isAuthenticationFailed = false;
	public String stUserName = null;
	public String stOrganizationID = null;
	public String stOrganizationName = null;
	public String stPassword = null;

	private static final String TAG = LoginResponse.class.getSimpleName();

	public void readResponse(String stLine) {
		try {
			MyJSONObject regisObject = new MyJSONObject(stLine);

			isSuccess = regisObject.getString(ScoutACDCApi.C_RESULT_CODE).equals(
					SUCCESS);

			if (isSuccess) {

				ticket = regisObject.getString(C_TICKET);

				try {

					stOrganizationID = regisObject.getString(C_ORG_ID);

				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}

				try {

					stOrganizationName = regisObject.getString(C_ORG_NAME);

				} catch (JSONException e) {
					Log.e(TAG, e.getMessage());
				}

				try {
					String stOrgnization = null;
					if (!regisObject.isNull(C_ORGANIZATIONS)) {
						stOrgnization = regisObject.getString(C_ORGANIZATIONS);
					}
					if (stOrgnization != null) {
						MyJSONObject jsonObject = new MyJSONObject(
								stOrgnization);
						Iterator<String> myIter = jsonObject.keys();
						Organization organization = null;
						while (myIter.hasNext()) {
							// here you can get all keys
							String stOrgIDKey = myIter.next();
							Log.i(TAG, stOrgIDKey);
							String stOrgnizationName = jsonObject
									.getString(stOrgIDKey);
							organization = new Organization(stOrgIDKey,
									stOrgnizationName);
							if (vecOrganizations == null) {
								vecOrganizations = new Vector<Organization>(1);
							}
							vecOrganizations.add(organization);
						}
						if (vecOrganizations != null
								&& vecOrganizations.size() > 0) {
							stOrganizationID = vecOrganizations.get(0)
									.getOrgId();
						}
					}
				} catch (JSONException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}

			// if (!isSuccess) {
			// stErrorCode = regisObject.getString(ERRORCODE);
			// stMeaning = regisObject.getString(MEANING);
			// }

		} catch (JSONException e) {

			Log.e(TAG, e.getMessage(), e);
		}
	}

	public void readOrgChangeTicket(final String stLine) {
		try {
			MyJSONObject regisObject = new MyJSONObject(stLine);

			isSuccess = regisObject.getString(ScoutACDCApi.C_RESULT_CODE).equals(
					SUCCESS);

			if (isSuccess) {

				ticket = regisObject.getString(C_TICKET);

			}

		} catch (JSONException e) {

			Log.e(TAG, e.getMessage(), e);
		}
	}

	private static final String LOGIN_FAILED = "Generating token failed with reason: AuthenticationFailed";

	public boolean isAuthenticationFailed(String stLine) {

		try {
			MyJSONObject regisObject = new MyJSONObject(stLine);

			stMeaning = regisObject.getString(ScoutACDCApi.C_RESULT_CODE);

			isAuthenticationFailed = stMeaning.equals(LOGIN_FAILED);

		} catch (JSONException e) {

			Log.e(TAG, e.getMessage(), e);
		}

		return isAuthenticationFailed;
	}

	@Override
	public String toString() {

		return isSuccess + "," + ticket; // + "," + stErrorCode + "," +
											// stMeaning;
	}

	public void setUserName(String username) {
		this.stUserName = username;

	}

	public void setOrganizationID(String stOrganizationID) {
		this.stOrganizationID = stOrganizationID;
	}

	public void setPassword(String stPassword) {
		this.stPassword = stPassword;
	}
}
