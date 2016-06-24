package com.trimble.reporter.trackdb;


import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.trimble.reporter.R;
import com.trimble.reporter.looper.DataSend;
import com.trimble.reporter.looper.Incidents;
import com.trimble.reporter.looper.LooperThread;
import com.trimble.reporter.looper.NetworkResponseListener;
import com.trimble.reporter.looper.TrackIncidents;
import com.trimble.reporter.utils.Utils;

public class IncidentTrackListActivity extends ListActivity {

	private IncidentListAdapter incidentListAdapter =null;
	
	private Cursor mCursor = null;
	
	private TrackDBManager dbManager =null;
	
	private HashMap<String,String> hm=null;
	
	 private ProgressDialog progress = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incidentlist);
		 dbManager = TrackDBManager.getInstance(getApplicationContext());
		mCursor= dbManager.getIncidentResultsCursor();
		hm = new HashMap<String, String>(1);
		String[] from = new String[] {
            TrackTable.COLUMN_DATE_AND_TIME, TrackTable.COLUMN_COMMENTS,TrackTable.COLUMN_CATAGORY
         };
        int[] to = new int[] {
                R.id.date, R.id.comments,R.id.catagory
        };
        progress = new ProgressDialog(this, R.style.DefaultProgressDialogTheme);
        progress.setMessage(getString(R.string.update));
        progress.setCancelable(false);
       
        
        incidentListAdapter = new IncidentListAdapter(this, R.layout.incidentitem, mCursor, from,
                to,this,hm);
        incidentListAdapter.notifyDataSetChanged();
        setListAdapter(incidentListAdapter);
        if(mCursor.moveToFirst()){
        	TextView tv =(TextView)findViewById(R.id.no_incident);
        	tv.setVisibility(View.GONE);
        	refreshList();
        }
	}
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			 progress.cancel();
			if(incidentListAdapter != null){
				incidentListAdapter.setHm(hm);
				}
		}
	};
	protected void onPause() {
		super.onPause();
		if(progress != null)
		    progress.cancel();
	};
	private void refreshList() {
		 progress.show();
		final DataSend dataSend = new DataSend();
		dataSend.stURL = String.format(LooperThread.TRACK_INCIDENTS,
				Utils.getDeviceUUID(this));
		dataSend.isPost = false;
		dataSend.listener =  new NetworkResponseListener() {
			
			@Override
			public void onConnectionSucess(Object objData, int iDataType) {
				
				try {
					TrackIncidents trackIncidents=TrackIncidents.parseFromJSON(new JSONObject(((String)objData)));
					if(trackIncidents != null && trackIncidents.vecIncidents != null){
						hm.clear();
						for (Incidents iterable_element : trackIncidents.vecIncidents) {
							if(iterable_element != null){
								hm.put( iterable_element.incidentIdInternal, iterable_element.status);
								
							}
						}
						
						handler.sendEmptyMessage(1);
					}
				} catch (JSONException e) {
				
					e.printStackTrace();
				}
			}
			
			@Override
			public void onConnectionFail(Object objData, int iDataType) {
				handler.sendEmptyMessage(1);
				
			}
		};
		Thread thread = new Thread() {

			@Override
			public void run() {

				LooperThread.getInstance().addData(dataSend);
			}
		};
		thread.start();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.incident_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
            	dbManager.clearAllIncidentReport();
            	mCursor=dbManager.getIncidentResultsCursor();
            	incidentListAdapter.changeCursor(mCursor);
                break;
                
            case R.id.menu_refresh:
            	refreshList();
            	break;
        }
        return true;
    }
    @Override
    protected void onDestroy() {
    	
    	super.onDestroy();
    	mCursor.close();
    }
}
