package com.trimble.reporter.incident;

import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.trimble.agent.R;
import com.trimble.reporter.IncidentResolveActivity;

public class IncidentTrackListActivity extends ListActivity implements
		OnItemClickListener {

	private IncidentListAdapter incidentListAdapter = null;

	private Cursor mCursor = null;

	private TrackDBManager dbManager = null;

	private HashMap<String, String> hm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incidentlist);
		dbManager = TrackDBManager.getInstance(getApplicationContext());
		mCursor = dbManager.getIncidentResultsCursor();
		hm = new HashMap<String, String>(1);
		String[] from = new String[] { TrackTable.COLUMN_DATE_AND_TIME,
				TrackTable.COLUMN_COMMENTS, TrackTable.COLUMN_CATAGORY };
		int[] to = new int[] { R.id.date, R.id.comments, R.id.catagory };

		if (mCursor.moveToFirst()) {
			TextView tv = (TextView) findViewById(R.id.no_incident);
			tv.setVisibility(View.GONE);
		}
		incidentListAdapter = new IncidentListAdapter(this,
				R.layout.incidentitem, mCursor, from, to, this, hm);
		incidentListAdapter.notifyDataSetChanged();
		setListAdapter(incidentListAdapter);
		registerForContextMenu(getListView());
		getListView().setClickable(true);
		getListView().setOnItemClickListener(this);
	}
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();    
		inflater.inflate(R.menu.resolvemenu, menu);    
	}
	
	/** when press-hold option selected */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
	
		//poiDetails = poiList.get(info.position);

		switch (item.getItemId()) {
		
		default:
			return super.onContextItemSelected(item);
		}

	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			mCursor.moveToPosition(position);
			final String incidentInternal = mCursor.getString(mCursor
					.getColumnIndex(TrackTable.COLUMN_INCIDENT_INTERNAL));
			System.out.println(incidentInternal);
			Intent intent = new Intent(this, IncidentResolveActivity.class);
			intent.putExtra(IncidentResolveActivity.INCIDENT_ID, incidentInternal);
			startActivityForResult(intent,
					IncidentResolveActivity.RESLOVE_INCIDENT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case IncidentResolveActivity.RESLOVE_INCIDENT:
			
			
			if(resultCode == RESULT_OK){
				
				if(data != null && data.getExtras() != null){
					String stIncidentID=data.getStringExtra(IncidentResolveActivity.INCIDENT_ID);
					
					if(stIncidentID != null){
						dbManager.deleteIncidentById(stIncidentID);
					}
					mCursor=dbManager.getIncidentResultsCursor();
					incidentListAdapter.changeCursor(mCursor);
					TextView tv = (TextView) findViewById(R.id.no_incident);
					if (mCursor.moveToFirst()) {
						
						tv.setVisibility(View.GONE);
					}else{
						tv.setVisibility(View.VISIBLE);
					}
				}
			}else{
				
			}
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
			break;
		}
		
	}
	@Override
	protected void onDestroy() {

		super.onDestroy();
		mCursor.close();
	}
}
