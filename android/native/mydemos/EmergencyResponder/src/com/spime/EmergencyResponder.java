package com.spime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

public class EmergencyResponder extends Activity {
	ReentrantLock lock;
	CheckBox locationCheckBox;
	ArrayList<String> requesters;
	ArrayAdapter<String> aa;
public static boolean isAlreadyInvoked=false;
boolean isFromReceiver=false;
	public static final String SENT_SMS = "com.spime.emergencyresponder.SMS_SENT";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		lock = new ReentrantLock();
		requesters = new ArrayList<String>();
		wireUpControls();

		Bundle bundle = getIntent().getBundleExtra("msg");
		if (bundle != null) {
			String stFromNumber = bundle.getString("phno");
			if (stFromNumber != null) {
				isFromReceiver=true;
				Log.i("SPIME", "phno received");
				requestReceived(stFromNumber);
			}
		}
	}

	public class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.okButton: {
				respond(true, locationCheckBox.isChecked());
				break;
			}
			case R.id.notOkButton: {
				respond(false, locationCheckBox.isChecked());
				break;
			}
			case R.id.autoResponder: {
				startAutoResponder();
				break;
			}
			}
		}

	}

	private void wireUpControls() {
		locationCheckBox = (CheckBox) findViewById(R.id.checkboxSendLocation);
		ListView myListView = (ListView) findViewById(R.id.myListView);
		int layoutID = android.R.layout.simple_list_item_1;
		aa = new ArrayAdapter<String>(this, layoutID, requesters);
		myListView.setAdapter(aa);
		ClickListener listener = new ClickListener();
		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(listener);
		Button notOkButton = (Button) findViewById(R.id.notOkButton);
		notOkButton.setOnClickListener(listener);
		Button autoResponderButton = (Button) findViewById(R.id.autoResponder);
		autoResponderButton.setOnClickListener(listener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("SPIME", "onResume");
		emergencyResponseRequestReceiver = new EmergencyRequestReceiver();
		IntentFilter filter = new IntentFilter(
		EmergencyRequestReceiver.SMS_RECEIVED);
		registerReceiver(emergencyResponseRequestReceiver, filter);
		IntentFilter attemptedDeliveryfilter = new IntentFilter(SENT_SMS);
		registerReceiver(attemptedDeliveryReceiver, attemptedDeliveryfilter);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		unregisterReceiver(attemptedDeliveryReceiver);
		unregisterReceiver(emergencyResponseRequestReceiver);
		super.onPause();
	}

	public void respond(boolean isOk, boolean isIncludeLocation) {
		String okString = getString(R.string.respondAllClearText);
		String notOkString = getString(R.string.respondMaydayText);
		String outString = isOk ? okString : notOkString;
		ArrayList<String> requestersCopy = (ArrayList<String>) requesters;
				
		for (String to : requestersCopy) {
			respond(to, outString, isIncludeLocation);
		}
		isAlreadyInvoked=false;
	}

	public void respond(String stTo, String stResponse,
			boolean isIncludeLocation) {
		// Remove the target from the list of people we need to respond to.
		lock.lock();
		requesters.remove(stTo);
		aa.notifyDataSetChanged();
		lock.unlock();
		SmsManager sms = SmsManager.getDefault();
		// Send the message
		Intent intent = new Intent(SENT_SMS);
		intent.putExtra("recipient", stTo);
		PendingIntent sentIntent = PendingIntent.getBroadcast(
				getApplicationContext(), 0, intent, 0);
		// Send the message
		sms.sendTextMessage(stTo, null, stResponse, sentIntent, null);

		StringBuilder sb = new StringBuilder();
		// Find the current location and send it as SMS messages if required.
		if (isIncludeLocation) {
			String ls = Context.LOCATION_SERVICE;
			LocationManager lm = (LocationManager) getSystemService(ls);
			Location myLastLocation = lm
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			sb.append("I’m @:\n");
			if (myLastLocation != null) {
				sb.append(myLastLocation.toString() + "\n");
				List<Address> addresses;

				try {
					Geocoder g = new Geocoder(getApplicationContext(),
							Locale.getDefault());
					addresses = g.getFromLocation(myLastLocation.getLatitude(),
							myLastLocation.getLongitude(), 1);
					if (addresses != null) {
						Address currentAddress = addresses.get(0);
						if (currentAddress.getMaxAddressLineIndex() > 0) {
							for (int i = 0; i < currentAddress
									.getMaxAddressLineIndex(); i++) {
								sb.append(currentAddress.getAddressLine(i));
								sb.append("\n");
							}
						} else {
							if (currentAddress.getPostalCode() != null)
								sb.append(currentAddress.getPostalCode());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				ArrayList<String> locationMsgs = sms.divideMessage(sb
						.toString());
				for (String locationMsg : locationMsgs) {
					sms.sendTextMessage(stTo, null, locationMsg, sentIntent,
							null);
				}
			} else {
				sb.append("Unknown location");
				sms.sendTextMessage(stTo, null, sb.toString(), sentIntent, null);
			}
		}
		Log.i("SPIME", "Message complete");
		isAlreadyInvoked=false;
	}

	private void startAutoResponder() {
		startActivityForResult(new Intent(EmergencyResponder.this,
				AutoResponder.class), 0);
	}

	public BroadcastReceiver attemptedDeliveryReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context _context, Intent _intent) {
			if (_intent.getAction().equals(SENT_SMS)) {
				if (getResultCode() != Activity.RESULT_OK) {
					String recipient = _intent.getStringExtra("recipient");
					requestReceived(recipient);
				}
			}
		}
	};
	private BroadcastReceiver emergencyResponseRequestReceiver = null;

	public void requestReceived(String _from) {
		if (!requesters.contains(_from)) {
			lock.lock();
			requesters.add(_from);
			aa.notifyDataSetChanged();
			lock.unlock();
			// Check for auto-responder
			String preferenceName = getString(R.string.user_preferences);
			SharedPreferences prefs = getSharedPreferences(preferenceName,
					MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
			String autoRespondPref = getString(R.string.autoRespondPref);
			boolean autoRespond = prefs.getBoolean(autoRespondPref, false);
			if (autoRespond) {
				String responseTextPref = getString(R.string.responseTextPref);
				String includeLocationPref = getString(R.string.includeLocationPref);
				String respondText = prefs.getString(responseTextPref, "");
				boolean includeLoc = prefs.getBoolean(includeLocationPref,
						false);
				respond(_from, respondText, includeLoc);
			}
		}
	}
}