package com.trimble.ag.ats;

import java.util.Date;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SyncStatusObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.trimble.ag.ats.db.DbContentProvider;
import com.trimble.ag.ats.db.LocationContent;
import com.trimble.ag.ats.location.GPSTrackService;
import com.trimble.ag.ats.location.GPSTrackService.MyGPSBinder;
import com.trimble.ag.ats.sync.account.ACDCAuthenticator;
import com.trimble.ag.ats.sync.account.ACDCAuthenticatorService;

public class MainActivity extends AppCompatActivity implements LocationListener,
      OnClickListener {

   /**
    * 
    */
   private static final String TAG = MainActivity.class.getSimpleName();

   /**
    * 
    */
	private static final int START_SETTINGS = 1000;

	private static final int LOCATION_REQUEST = 1;

   private transient GPSTrackService gpsTrackService = null;

   private boolean                   isServiceBound  = false;
   /**
    * Cursor adapter for controlling ListView results.
    */
   private SimpleCursorAdapter mAdapter;

   /**
    * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
    * that the sync is complete.
    *
    * <p>This allows us to delete our SyncObserver once the application is no longer in the
    * foreground.
    */
   private Object mSyncObserverHandle;

   /**
    * Options menu used to populate ActionBar.
    */
   private Menu mOptionsMenu;
   
   private AccountManager mAccountManager;
   
   private Account mConnectedAccount;
   
   private String authToken = null;
   
   private int targetSdkVersion;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      mAccountManager = AccountManager.get(this);
   // Create account, if needed
     // SyncUtils.CreateSyncAccount(this);
      
      getSupportActionBar().setTitle(R.string.location);
      updateStartStopButton();
      
      try {
          final PackageInfo info = this.getPackageManager().getPackageInfo(
        		  this.getPackageName(), 0);
          targetSdkVersion = info.applicationInfo.targetSdkVersion;
      } catch (PackageManager.NameNotFoundException e) {
          e.printStackTrace();
      }
      
		if ( hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
			
		} else {
			// Show rationale and request permission.

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {

				// Show an expanation to the user *asynchronously* -- don't
				// block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

			} else {

				// No explanation needed, we can request the permission.

				ActivityCompat
						.requestPermissions(
								this,
								new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
								LOCATION_REQUEST);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}
     
      
      
   }
   
   public boolean hasPermission(String permission) {
       // For Android < Android M, self permissions are always granted.
       boolean result = true;

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

           if (targetSdkVersion >= Build.VERSION_CODES.M) {
               // targetSdkVersion >= Android M, we can
               // use Context#checkSelfPermission
               result = this.checkSelfPermission(permission)
                       == PackageManager.PERMISSION_GRANTED;
           } else {
               // targetSdkVersion < Android M, we have to use PermissionChecker
               result = PermissionChecker.checkSelfPermission(this, permission)
                       == PermissionChecker.PERMISSION_GRANTED;
           }
       }

       return result;
   }
   private boolean canAccessLocation() {
	    return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
	  }
   @Override
   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	    switch(requestCode) {
	      case LOCATION_REQUEST:
//	        if (canAccessLocation()) {
//	          
//	        }else{
//	        	
//	        }
	        
	        updateStartStopButton();
	        break;
	    }
    
   }
// Sync interval constants
   public static final long SECONDS_PER_MINUTE = 60L;
   public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
   public static final long SYNC_INTERVAL =
           SYNC_INTERVAL_IN_MINUTES *
           SECONDS_PER_MINUTE;
   private void initButtonsAfterConnect() {
     final String authority = LocationContent.CONTENT_AUTHORITY;

      // Get the syncadapter settings and init the checkboxes accordingly
      int isSyncable = ContentResolver.getIsSyncable(mConnectedAccount, authority);
      boolean autSync = ContentResolver.getSyncAutomatically(mConnectedAccount, authority);

      ((CheckBox)findViewById(R.id.cbIsSyncable)).setChecked(isSyncable > 0);
      ((CheckBox)findViewById(R.id.cbAutoSync)).setChecked(autSync);

      findViewById(R.id.cbIsSyncable).setEnabled(true);
      findViewById(R.id.cbAutoSync).setEnabled(true);
      
      findViewById(R.id.btnSync).setEnabled(true);
      findViewById(R.id.btn_login).setEnabled(false);
      findViewById(R.id.btnAccountList).setEnabled(true);

      ContentResolver.setSyncAutomatically(mConnectedAccount, authority, true);
      ContentResolver.addPeriodicSync(mConnectedAccount, authority,
            Bundle.EMPTY, 60 );
      
      refreshSyncStatus();
  }
   
   private void showGPSenableDialog(){
	   LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	      boolean gps_enabled = false;
	      boolean network_enabled = false;

	      try {
	          gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	      } catch(Exception ex) {}

	      try {
	          network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	      } catch(Exception ex) {}

	      if(!gps_enabled && !network_enabled) {
	          // notify user
	          AlertDialog.Builder dialog = new AlertDialog.Builder(this);
	          dialog.setMessage(this.getResources().getString(R.string.gps_network_not_enabled));
	          dialog.setPositiveButton(this.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
	                  @Override
	                  public void onClick(DialogInterface paramDialogInterface, int paramInt) {
	                      // TODO Auto-generated method stub
	                      Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                      startActivity(myIntent);
	                      //get gps
	                  }
	              });
	          dialog.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

	                  @Override
	                  public void onClick(DialogInterface paramDialogInterface, int paramInt) {
	                      // TODO Auto-generated method stub
	                	  finish();
	                  }
	              });
	          dialog.show();      
	      }
   }
   private void refreshSyncStatus() {
      if(mConnectedAccount == null){
         return;
      }
      String status;

      if (ContentResolver.isSyncActive(mConnectedAccount, LocationContent.CONTENT_AUTHORITY))
          status = "Status: Syncing..";
      else if (ContentResolver.isSyncPending(mConnectedAccount, LocationContent.CONTENT_AUTHORITY))
          status = "Status: Pending..";
      else
          status = "Status: Idle";

      ((TextView) findViewById(R.id.status)).setText(status);
      Log.d(TAG, "refreshSyncStatus> " + status);
  }

   /**
    * Get an auth token for the account.
    * If not exist - add it and then return its auth token.
    * If one exist - return its auth token.
    * If more than one exists - show a picker and return the select account's auth token.
    * @param accountType
    * @param authTokenType
    */
   private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
       final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
               new AccountManagerCallback<Bundle>() {
                   @Override
                   public void run(AccountManagerFuture<Bundle> future) {
                       Bundle bnd = null;
                       try {
                           bnd = future.getResult();
                           authToken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                           if (authToken != null) {
                               String accountName = bnd.getString(AccountManager.KEY_ACCOUNT_NAME);
                               mConnectedAccount = new Account(accountName, ACDCAuthenticatorService.ACCOUNT_TYPE);
                               initButtonsAfterConnect();
                           }
                           showMessage(((authToken != null) ? "SUCCESS!\ntoken: " + authToken : "FAIL"));
                           Log.d(TAG, "GetTokenForAccount Bundle is " + bnd);

                       } catch (Exception e) {
                           e.printStackTrace();
                           showMessage(e.getMessage());
                       }
                   }
               }
       , null);
   }

   private void updateStartStopButton(){
      boolean isServiceAlreadyRunning= isMyServiceRunning(GPSTrackService.class);
      final Button buttonStart = (Button) findViewById(R.id.btn_start);
      buttonStart.setOnClickListener(this);
      final Button buttonStop = (Button) findViewById(R.id.btn_stop);
      buttonStop.setOnClickListener(this);
      final Button btnAccountList  = (Button) findViewById(R.id.btnAccountList );
      btnAccountList.setOnClickListener(this);
      //btnAccountList.setVisibility(View.GONE);
      final Button btn_login  = (Button) findViewById(R.id.btn_login );
      btn_login.setOnClickListener(this);
      final Button btnSync  = (Button) findViewById(R.id.btnSync );
      btnSync.setOnClickListener(this);
      
      buttonStart.setEnabled( isServiceAlreadyRunning == false);
      buttonStop.setEnabled( isServiceAlreadyRunning == true);
      if(! canAccessLocation()){
    	  buttonStart.setEnabled(false);
    	  buttonStop.setEnabled(false);
    	  stopLocationService();
      }
      showTextInfo(R.id.txt_status, getString(isServiceAlreadyRunning ?R.string.running :R.string.idle));
   }

   private void showLocationInfo(final Location location) {
      if(location == null){
         return;
      }
      showTextInfo(R.id.txt_latitude, String.valueOf(location.getLatitude()));
      showTextInfo(R.id.txt_longitude, String.valueOf(location.getLongitude()));
      showTextInfo(R.id.txt_accuracy, String.valueOf(location.getAccuracy()));
      showTextInfo(R.id.txt_time, String.valueOf(new Date(location.getTime())));
      showTextInfo(R.id.txt_status, getString(R.string.running));

   }
   private void showMessage(final String msg) {
      if (msg == null || msg.trim().equals(""))
          return;

      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
          }
      });
  }
   private void showTextInfo(final int id, final String stData) {
      final TextView txt = (TextView) findViewById(id);
      if (txt != null) {
         txt.setText(stData);
      }
   }

   @Override
   protected void onDestroy() {
      unBind();
      
      super.onDestroy();
   }

   private void startLocationService() {

      
       Intent intent = new Intent(this, GPSTrackService.class);
      startService(intent);
      
    
   }

   private void stopLocationService() {

      
      final Intent intent = new Intent(this, GPSTrackService.class);
      stopService(intent);

   }

   
  
   private void checkGPSEnable(){
      if(gpsTrackService != null && ! gpsTrackService.isLocationRecordStarted()){
         showSettingsAlert() ;
      }
   }
   private void bindService(){
      if ( isServiceBound == false) {
         showTextInfo(R.id.txt_status, getString(R.string.idle));
         Intent  intent = new Intent(this, GPSTrackService.class);
          bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
      }
   }
   private void unBind(){
      if (isServiceBound) {
         unbindService(myConnection);
         unbindEvent();         
      }
   }
   private void unbindEvent(){
      if(gpsTrackService != null){
         gpsTrackService.setLocationListener(null);
      }
      isServiceBound = false;
   }
   
   private void startLocationRec(){
	   gpsTrackService.setLocationListener(MainActivity.this);
       checkGPSEnable();
       updateStartStopButton();
   }
   private ServiceConnection myConnection = new ServiceConnection() {

                                             public void onServiceConnected(
                                                   ComponentName className,
                                                   IBinder service) {
                                                final MyGPSBinder binder = (MyGPSBinder) service;
                                                gpsTrackService = binder
                                                      .getParentServiceObject();
                                                
                                                final Location location =gpsTrackService.getLocation();
                                                showLocationInfo(location);
                                                
                                                isServiceBound = true;
                                                startLocationRec();
                                                
                                             }

                                             public void onServiceDisconnected(
                                                   ComponentName arg0) {
                                                unbindEvent();
                                                updateStartStopButton();
                                             }

                                          };

                                          
   @Override
   public void onLocationChanged(Location location) {

      showLocationInfo(location);
   }

   @Override
   public void onStatusChanged(String provider, int status, Bundle extras) {

   }

   @Override
   public void onProviderEnabled(String provider) {

   }

   @Override
   public void onProviderDisabled(String provider) {

   }

   @Override
   public void onClick(View v) {

      switch (v.getId()) {
         case R.id.btn_login:
            getTokenForAccountCreateIfNeeded(ACDCAuthenticatorService.ACCOUNT_TYPE, ACDCAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS);
            break;
 case R.id.btn_start:
    // startLocationService();
    bindService();
            break;
 case R.id.btn_stop:
 // stopLocationService();
    unBind();
    break;
 case R.id.btnAccountList:
    showAccountPicker(ACDCAuthenticator.AUTHTOKEN_TYPE_FULL_ACCESS, true);
    break;
 case R.id.btnSync:
    doSync();
    break;
         default:
            break;
      }
    
      updateStartStopButton();
   }

   private void doSync(){
      if (mConnectedAccount == null) {
         Toast.makeText(MainActivity.this, "Please connect first", Toast.LENGTH_SHORT).show();
         return;
     }
     final Bundle bundle = new Bundle();
     bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true); // Performing a sync no matter if it's off
     bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true); // Performing a sync no matter if it's off
     ContentResolver.requestSync(mConnectedAccount, DbContentProvider.AUTHORITY, bundle);
   }
   /**
    * Function to show settings alert dialog On pressing Settings button will
    * lauch Settings Options
    * */
   public void showSettingsAlert() {
      AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

      // Setting Dialog Title
      alertDialog.setTitle(R.string.gps_settings);

      // Setting Dialog Message
      alertDialog.setMessage(R.string.gps_not_enable);

      // On pressing Settings button
      alertDialog.setPositiveButton(R.string.settings,
            new DialogInterface.OnClickListener() {

               public void onClick(DialogInterface dialog, int which) {
                  Intent intent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                  startActivityForResult(intent, START_SETTINGS);
               }
            });

      // on pressing cancel button
      alertDialog.setNegativeButton(R.string.cancel,
            new DialogInterface.OnClickListener() {

               public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
               }
            });

      // Showing Alert Message
      alertDialog.show();
   }
   private boolean isMyServiceRunning(Class<?> serviceClass) {
      ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
      for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
          if (serviceClass.getName().equals(service.service.getClassName())) {
              return true;
          }
      }
      return false;
  }
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {

      if (requestCode == START_SETTINGS) {
         
      } else {
         super.onActivityResult(requestCode, resultCode, data);
      }

   }
   
   /**
    * Create the ActionBar.
    */
 
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      mOptionsMenu = menu;
      final MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.main, menu);
      return true; 
      
   }
 

   /**
    * Respond to user gestures on the ActionBar.
    */
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
           // If the user clicks the "Refresh" button.
           case R.id.menu_refresh:
                 doSync();
               return true;
       }
       return super.onOptionsItemSelected(item);
   }
   @Override
   public void onResume() {
       super.onResume();
       mSyncStatusObserver.onStatusChanged(0);

       // Watch for sync state changes
       final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
               ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
       mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
   }

   @Override
   public void onPause() {
       super.onPause();
       if (mSyncObserverHandle != null) {
           ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
           mSyncObserverHandle = null;
       }
   }
   
   /**
    * Set the state of the Refresh button. If a sync is active, turn on the ProgressBar widget.
    * Otherwise, turn it off.
    *
    * @param refreshing True if an active sync is occuring, false otherwise
    */
   public void setRefreshActionButtonState(boolean refreshing) {
       if (mOptionsMenu == null) {
           return;
       }
       refreshSyncStatus();
       final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
       if (refreshItem != null) {
           if (refreshing) {
        	   findViewById(R.id.btnSync).setEnabled(false);
               refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
           } else {
        	   if(!findViewById(R.id.btn_login).isEnabled())
        	   findViewById(R.id.btnSync).setEnabled(true);
               refreshItem.setActionView(null);
           }
       }
   }

   /**
    * Crfate a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
    * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
    * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
    * ProgressBar; otherwise, the button itself is displayed.
    */
   private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
       /** Callback invoked with the sync adapter status changes. */
       @Override
       public void onStatusChanged(int which) {
           runOnUiThread(new Runnable() {
               /**
                * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                * runs on the UI thread.
                */
               @Override
               public void run() {
                   // Create a handle to the account that was created by
                   // SyncService.CreateSyncAccount(). This will be used to query the system to
                   // see how the sync status has changed.
                   
                   
                   if (mConnectedAccount == null) {
                       // GetAccount() returned an invalid value. This shouldn't happen, but
                       // we'll set the status to "not refreshing".
                       setRefreshActionButtonState(false);
                       return;
                   }
                   

                   // Test the ContentResolver to see if the sync adapter is active or pending.
                   // Set the state of the refresh button accordingly.
                   boolean syncActive = ContentResolver.isSyncActive(
                         mConnectedAccount, LocationContent.CONTENT_AUTHORITY);
                   boolean syncPending = ContentResolver.isSyncPending(
                         mConnectedAccount, LocationContent.CONTENT_AUTHORITY);
                   setRefreshActionButtonState(syncActive || syncPending);
               }
           });
       }
   };
   /**
    * Show all the accounts registered on the account manager. Request an auth token upon user select.
    * @param authTokenType
    */
   private void showAccountPicker(final String authTokenType, final boolean invalidate) {

       final Account availableAccounts[] = mAccountManager.getAccountsByType(ACDCAuthenticatorService.ACCOUNT_TYPE);

       if (availableAccounts.length == 0) {
           Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
       } else {
           String name[] = new String[availableAccounts.length];
           for (int i = 0; i < availableAccounts.length; i++) {
               name[i] = availableAccounts[i].name;
           }

           // Account picker
           new AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   if(invalidate)
                       invalidateAuthToken(availableAccounts[which], authTokenType);
                   else
                       getExistingAccountAuthToken(availableAccounts[which], authTokenType);
               }
           }).show();
       }
   }

   /**
    * Get the auth token for an existing account on the AccountManager
    * @param account
    * @param authTokenType
    */
   private void getExistingAccountAuthToken(Account account, String authTokenType) {
       final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   Bundle bnd = future.getResult();

                   for (String key : bnd.keySet()) {
                       Log.d(TAG, "Bundle[" + key + "] = " + bnd.get(key));
                   }

                   final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                   showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL");
                   Log.d(TAG, "GetToken Bundle is " + bnd);
               } catch (Exception e) {
                   e.printStackTrace();
                   showMessage(e.getMessage());
               }
           }
       }).start();
   }

   /**
    * Invalidates the auth token for the account
    * @param account
    * @param authTokenType
    */
   private void invalidateAuthToken(final Account account, String authTokenType) {
       final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null,null);

       new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   Bundle bnd = future.getResult();

                   final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                   mAccountManager.invalidateAuthToken(account.type, authtoken);
                   showMessage(account.name + " invalidated");
               } catch (Exception e) {
                   e.printStackTrace();
                   showMessage(e.getMessage());
               }
           }
       }).start();
   }
}
