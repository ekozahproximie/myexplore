package com.trimble.reporter;

import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.trimble.agent.R;
import com.trimble.reporter.app.TCCApplication;
import com.trimble.reporter.incident.IncidentTrackListActivity;
import com.trimble.reporter.incident.TrackDBManager;
import com.trimble.reporter.looper.LooperThread;
import com.trimble.reporter.utils.Utils;

public class MainActivity extends BaseActivity implements
		OnItemSelectedListener {

	private ArrayAdapter<CharSequence> adapter = null;
	private Spinner spin = null;
	private Cursor mCursor = null;

	private TrackDBManager dbManager = null;

	boolean isInit = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbManager = TrackDBManager.getInstance(this);

		setUpView();
		LooperThread looperThread = LooperThread.getInstance();
		looperThread.setTCCApplication((TCCApplication) getApplication());
	}

	private void setUpView() {
		setContentView(R.layout.activity_main);

		spin = (Spinner) findViewById(R.id.agentids);
		adapter = ArrayAdapter.createFromResource(this, R.array.agentid,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);

		spin.setOnItemSelectedListener(this);

		// saveLatLon();
		checkDeviceGPS();

	}

	void setSpinner() {
		int id = AgentLocActivity.getAgentID(this);
		int pos = adapter.getPosition(String.valueOf(id));
		spin.setSelection(pos);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mCursor != null) {
			mCursor.close();
		}
		mCursor = dbManager.getIncidentResultsCursor();
		if (mCursor.moveToFirst()) {
			Button tv = (Button) findViewById(R.id.agenttask);
			tv.setVisibility(View.VISIBLE);
		}else{
			Button tv = (Button) findViewById(R.id.agenttask);
			tv.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		if (isInit == false) {
			setSpinner();
			isInit = true;
			return;
		}
		if (parent.getId() == R.id.agentids) {

			Object stData = adapter.getItem(position);
			System.out.println(stData);
			if (stData != null) {
				int iID = Integer.parseInt((String) stData);
				AgentLocActivity.storeAgentID(this, iID);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	public void onClick(View view) {
		switch (view.getId()) {

		case R.id.agent: {

			openActivity(AgentLocActivity.class, 2);
			break;
		}
		case R.id.agenttask:
			openActivity(IncidentTrackListActivity.class, 12);
			break;

		default:
			break;
		}
	}

	private void openActivity(Class<?> activityToOpen, int iTitle) {
		Intent intent = new Intent(this, activityToOpen);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			int exit_message_id = R.string.exit_message;

			showAlertDialog(ALERT_APP_EXIT, R.string.exit_title,
					exit_message_id, R.string.yes, R.string.no);
			return true;
		}
		return false;
	}

	private void closeApp() {

		Log.i("map", "close app");
		Utils.deleteJobFileDir(new File(PhotoActivity.getFlagStoreDir()));

		// show the splash in every launch
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefs.edit().putBoolean(SHOW_SPLASH, true).commit();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ALERT_APP_EXIT:
			if (resultCode == ALERT_RESULT_POS) {
				closeApp();
			}
			break;
		case SPLASH:
			if (resultCode == RESULT_CANCELED) {
				closeApp();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (mCursor != null) {
			mCursor.close();
		}
	}
}
