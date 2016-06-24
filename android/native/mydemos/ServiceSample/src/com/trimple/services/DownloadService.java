package com.trimple.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.trimble.receiver.DownloadReceiver;
import com.trimple.servicesample.R;

public class DownloadService extends IntentService {

	public static final String DOWNLOAD_URL = "downloadurl";

	public static final String STORE_F_NAME = "storelocation";

	private static final String TAG = "ServicesDemo";

	public DownloadService(String name) {
		super(name);

	}

	public DownloadService() {
		super("Download Intent Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		final String stDownloadURL = intent.getStringExtra(DOWNLOAD_URL);
		final String stFileName = intent.getStringExtra(STORE_F_NAME);
		startDownLoad(stDownloadURL, stFileName);

	}

	private boolean isNetworkAvaliable() {
		boolean isConnected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			isConnected = networkInfo.isConnectedOrConnecting();
		}
		return isConnected;
	}

	private void startDownLoad(String stDownloadURL, String stFileName) {
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		if (!isNetworkAvaliable()
				|| !Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
			Log.e(TAG, "download start.... ");
			return;
		}

		int timeoutConnection = 15000;
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		boolean isSuceess = false;
		File parentPath=null;
		File downloadFullPath=null;
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 15000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpGet httpGet = new HttpGet(stDownloadURL);
		FileOutputStream fileOutputStream = null;
		try {
			Log.i(TAG, "download start.... ");
			HttpResponse httpResponse = client.execute(httpGet);
			HttpEntity entity = httpResponse.getEntity();
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream inputStream = entity.getContent();

				String stFile = Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ File.separator
						+ Environment.DIRECTORY_DOWNLOADS;
				 parentPath = new File(stFile);
				if (!parentPath.exists() && !parentPath.mkdirs()) {
					return;
				}
				downloadFullPath=new File(parentPath.getAbsolutePath()
						+ File.separator + stFileName);
				fileOutputStream = new FileOutputStream(downloadFullPath.getAbsolutePath());
				
				int iChuck_size = 8 * 1024;
				byte bData[] = new byte[iChuck_size];
				while ((iChuck_size = inputStream.read(bData)) != -1) {
					fileOutputStream.write(bData, 0, iChuck_size);
					fileOutputStream.flush();
				}
				isSuceess = true;
			}
		} catch (ClientProtocolException e) {

			Log.e(TAG, "ClientProtocolException ", e);
		} catch (IOException e) {
			Log.e(TAG, "IOException ", e);
		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(isSuceess){
		   Log.i(TAG, "download complete");
			//sendBroadcast(downloadFullPath.getAbsolutePath());
			fireNotification(this, downloadFullPath.getAbsolutePath());
		}else{
		   Log.e(TAG, "download fail");
		}
		stopSelf();
	}
	   
	private void sendBroadcast(String stFileName){
		Intent intent = new Intent(DownloadReceiver.DOWNLOAD_COMPLETE);
		intent.putExtra(STORE_F_NAME,
				stFileName);
		sendBroadcast(intent);

	}

	@SuppressWarnings("deprecation")
	private void fireNotification(Context context,String stFileName) {
		String message;

		message = context.getString(R.string.notify_message);

		// Grab the notification manager to show the notification
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = message;

		// Grab the context to show the event for
		CharSequence contentTitle = context.getString(R.string.notify_title);
		CharSequence contentText = message;

		// Grab the intent to start the App
		PendingIntent contentIntent = getActivityPendingIntent(context, stFileName);

		// This is the notification we will present
		Notification notification = new Notification(icon, tickerText,
				System.currentTimeMillis());

		// Set info for this event
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		// Setoff a sound...
		// notification.defaults |= Notification.FLAG_AUTO_CANCEL|
		// Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		// notification.defaults |= Notification.DEFAULT_SOUND;

		long[] vibrate = { 0, 100, 200, 300 };
		notification.vibrate = vibrate;

		// Present the notification
		mNotificationManager.cancelAll();
		mNotificationManager.notify(Integer.MAX_VALUE, notification);
	}

	private PendingIntent getActivityPendingIntent(Context context,
			String stFileName) {
		// Seeting intent to fire when notifation tapped from the notification
		// tray.
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = new File(stFileName);
		String stFileExt = "png";
		int iExtIndex = stFileName.lastIndexOf(".");
		if (iExtIndex != -1) {
			stFileExt = stFileName.substring(iExtIndex + 1);
		}
		intent.setDataAndType(Uri.fromFile(file), "image/" + stFileExt);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);

		return contentIntent;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		Toast.makeText(this, "Download service stop", Toast.LENGTH_SHORT)
				.show();
	}
}
