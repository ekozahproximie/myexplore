/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trimble.ag.ats.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.trimble.ag.ats.acdc.ACDCApi;
import com.trimble.ag.ats.acdc.location.LocationDataRequest;
import com.trimble.ag.ats.db.ATSContentProvdier;
import com.trimble.ag.ats.db.LocationContent;
import com.trimble.ag.ats.entity.Location;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.util.List;

import org.json.JSONException;

/**
 * Define a sync adapter for the app.
 * 
 * <p>
 * This class is instantiated in {@link SyncService}, which also binds
 * SyncAdapter to the system. SyncAdapter should only be initialized in
 * SyncService, never anywhere else.
 * 
 * <p>
 * The system calls onPerformSync() via an RPC call through the IBinder object
 * supplied by SyncService.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
	public static final String TAG = "SyncAdapter";

	/**
	 * Content resolver, for performing database operations.
	 */
	private final ContentResolver mContentResolver;

	private final ATSContentProvdier mContentProvider;

	private final ACDCApi mACDCApi;

	/**
	 * Constructor. Obtains handle to content resolver for later use.
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mContentResolver = context.getContentResolver();
		mContentProvider = ATSContentProvdier.getInstance(context
				.getApplicationContext());
		mACDCApi = ACDCApi.getInstance(context.getApplicationContext());
	}

	/**
	 * Constructor. Obtains handle to content resolver for later use.
	 */
	public SyncAdapter(Context context, boolean autoInitialize,
			boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
		mContentResolver = context.getContentResolver();
		mContentProvider = ATSContentProvdier.getInstance(context
				.getApplicationContext());
		mACDCApi = ACDCApi.getInstance(context.getApplicationContext());
	}

	/**
	 * Called by the Android system in response to a request to run the sync
	 * adapter. The work required to read data from the network, parse it, and
	 * store it in the content provider is done here. Extending
	 * AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
	 * run on a background thread. For this reason, blocking I/O and other
	 * long-running tasks can be run <em>in situ</em>, and you don't have to set
	 * up a separate thread for them. .
	 * 
	 * <p>
	 * This is where we actually perform any work required to perform a sync.
	 * {@link AbstractThreadedSyncAdapter} guarantees that this will be called
	 * on a non-UI thread, so it is safe to peform blocking I/O here.
	 * 
	 * <p>
	 * The syncResult argument allows you to pass information back to the method
	 * that triggered the sync.
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.i(TAG, "Beginning network synchronization :" + account);

		try {

			Thread.sleep(10000);
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
			sendLocationDataToServer();

			mContentResolver.notifyChange(
					LocationContent.LocationEntry.CONTENT_URI, // URI where
																// data was
																// modified
					null, // No local observer
					false); // IMPORTANT: Do not sync to network
			// This sample doesn't support uploads, but if *your* code does,
			// make sure you set
			// syncToNetwork=false in the line above to prevent duplicate
			// syncs.
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		Log.i(TAG, "Network synchronization complete");
	}

	private void sendLocationDataToServer() {
		try {
			String orgId = mACDCApi.getOrganizationID();
			List<Location> toBeSyncedLocations = mContentProvider
					.getNonSyncedLocations(orgId);
			if (null != toBeSyncedLocations && toBeSyncedLocations.size() > 0) {
				LocationDataRequest dataRequest = new LocationDataRequest(
						toBeSyncedLocations);
				String stJsonString = dataRequest.getJSONArrayString();
				LocationAPI locationAPI = new LocationAPI(getContext());

				boolean isDataSent;

				isDataSent = locationAPI.sendLocationsData(stJsonString,
						mACDCApi);

				if (isDataSent) {
					for (Location locationObject : toBeSyncedLocations) {
						locationObject.setIsSynced(true);
					}
					mContentProvider.updateLocations(toBeSyncedLocations);
					mContentProvider.deleteSyncedLocations();
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
