package com.trimble.ag.ats.acdc.res;

import android.util.Log;

import com.trimble.ag.ats.acdc.ACDCApi;
import com.trimble.ag.ats.acdc.MyJSONObject;
import com.trimble.ag.ats.entity.Organization;

import org.json.JSONException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

public class LoginResponse extends com.trimble.ag.ats.acdc.ACDCResponse {

	private static final String C_TICKET = "access_token";
	
	public static final String SUCCESS = "Success";

	private static final String C_ORG_ID = "organizationID";

	private static final String C_ORG_NAME = "organizationName";
	

	private static final String C_ORGANIZATIONS = "organizations";
	
	private static final String C_PRIMARYORGID ="primaryOrgID";

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
	private transient long lPrimaryOrgId=-1;

	private static final String TAG = LoginResponse.class.getSimpleName();

	public void readResponse(String stLine) {
		try {
			MyJSONObject regisObject = new MyJSONObject(stLine);

			isSuccess = regisObject.getString(ACDCApi.C_RESULT_CODE).equals(
					SUCCESS);

			if (isSuccess) {

				ticket = regisObject.getString(C_TICKET);

				
					stOrganizationID = regisObject.getString(C_ORG_ID);

					stOrganizationName = regisObject.getString(C_ORG_NAME);


				
					String stOrgnization = null;
					if (!regisObject.isNull(C_ORGANIZATIONS)) {
						stOrgnization = regisObject.getString(C_ORGANIZATIONS);
					}
					if (stOrgnization != null) {
					   lPrimaryOrgId=regisObject.getLong(C_PRIMARYORGID);
						MyJSONObject jsonObject = new MyJSONObject(
								stOrgnization);
						Iterator<String> myIter = jsonObject.keys();
						Organization organization = null;
						int iPrimaryOrgIndex=0;
						while (myIter.hasNext()) {
							// here you can get all keys
							String stOrgIDKey = myIter.next();
							Log.i(TAG, stOrgIDKey);
							String stOrgnizationName = jsonObject
									.getString(stOrgIDKey);
							final boolean isPrimaryOrg=lPrimaryOrgId == Long.parseLong(stOrgIDKey);
							organization = new Organization(stOrgIDKey,
									stOrgnizationName,isPrimaryOrg);
							if (vecOrganizations == null) {
								vecOrganizations = new Vector<Organization>(1);
							}
							if(isPrimaryOrg){
							   iPrimaryOrgIndex =vecOrganizations.size();
							}
							vecOrganizations.add(organization);
						}
						if (lPrimaryOrgId == -1 && vecOrganizations != null
								&& vecOrganizations.size() > 0) {
							Comparator<Organization> comparator = new Comparator<Organization>() {

								@Override
								public int compare(Organization lhs,
										Organization rhs) {
									if (lhs.getName() == null
											&& rhs.getName() != null) {
										return -1;
									} else if (rhs.getName() == null
											&& lhs.getName() != null) {
										return 1;
									} else if (lhs.getName() == null
											&& rhs.getName() == null) {
										return 0;
									}
									return lhs.getName().compareToIgnoreCase(
											rhs.getName());
								}

							};
							
							Collections.sort(vecOrganizations, comparator);
							setOrgName_Id(0);
						}else{
						   setOrgName_Id(iPrimaryOrgIndex);
						}
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

	private void setOrgName_Id(final int index ){
	   if(vecOrganizations == null || vecOrganizations.size() <= index){
	      return;
	   }
	     stOrganizationID = vecOrganizations.get(index)
                 .getOrgId();
	     stOrganizationName = vecOrganizations.get(index).getName();
	}
	public void readOrgChangeTicket(final String stLine) {
		try {
			MyJSONObject regisObject = new MyJSONObject(stLine);
			
			final String stResultCode= regisObject.getString(ACDCApi.C_RESULT_CODE);
			if(stResultCode != null){
			isSuccess =stResultCode.equals(
					SUCCESS);
			}

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

			stMeaning = regisObject.getString(ACDCApi.C_RESULT_CODE);
			if(stMeaning == null || stMeaning.trim().length() == 0){
			   stMeaning  = regisObject.getString(ACDCApi.C_MESSAGE);
			}
			if(stMeaning != null){
			   isAuthenticationFailed = stMeaning.equals(LOGIN_FAILED);
			}

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
	
	public Vector<Organization> getVecOrganizations() {
		return vecOrganizations;
	}
	
      /**
       * @return the lPrimaryOrgId
       */
      public long getPrimaryOrgId() {
         return lPrimaryOrgId;
      }
}
