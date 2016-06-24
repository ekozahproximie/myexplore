package com.trimple.servicesample;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.trimble.receiver.DownloadReceiver;
import com.trimple.services.DownloadService;

public class DownloadActivity extends Activity {

	private EditText edFilename = null;

	private EditText edDownloadURL = null;
	
	private DownloadReceiver downloadReceiver =null;
	
	private IntentFilter filter =null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_layout);
		edFilename = (EditText) findViewById(R.id.edfilename);
		edDownloadURL = (EditText) findViewById(R.id.edURL);
		downloadReceiver= new DownloadReceiver();
		filter=new IntentFilter(DownloadReceiver.DOWNLOAD_COMPLETE);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(downloadReceiver, filter);
	}
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(downloadReceiver);
	}
	public void onClick(View view) {
		String stFileName = edFilename.getText().toString().trim();
		String stURL = edDownloadURL.getText().toString().trim();
		if (stFileName.length() != 0 && stURL.length() != 0) {
			final Intent intent = new Intent(this, DownloadService.class);
			intent.putExtra(DownloadService.DOWNLOAD_URL, stURL);
			intent.putExtra(DownloadService.STORE_F_NAME, stFileName);
			startService(intent);
		}
	}
	
}
