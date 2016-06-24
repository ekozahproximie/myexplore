package com.android.wifi.auto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * BroadcastReceiver used to find out the system boot up.
 * 
 * @author senthil
 * 
 */
public class BootUpReceiver extends BroadcastReceiver {
	

	private String LOG_TAG = "AUTO";
	private static boolean isTimeSet=false;
	@Override
	public void onReceive(Context context, Intent intent) {
	
		String action = intent.getAction();

		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if (noConnectivity) {

			} else {
				if(isTimeSet == false){
				setTime();
				}
				
			}
			Log.i(LOG_TAG, "Network connection " + !noConnectivity);
			return;
		}
		if (action != null) {
			if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
					|| intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(true);
				Toast.makeText(context,
						"Wifi enable " + wifiManager.getWifiState(),
						Toast.LENGTH_SHORT).show();

			}
		}

	}

	

	private void setTime() {
		Socket sock = null;
		InputStream in = null;
		BufferedReader br = null;
		Date netDate = null;
		try {
			sock = new Socket("192.43.244.18", 13);
			in = sock.getInputStream();
			br = new BufferedReader(new InputStreamReader(in));
			String s = null, s1 = "", date = "", time = "";
			while ((s = br.readLine()) != null) {
				System.out.print("\n" + s);
				s1 = s;
			}
			StringTokenizer st = new StringTokenizer(s1);
			st.nextToken();
			date = st.nextToken();
			time = st.nextToken();

			Log.i(LOG_TAG, "Date:" + date);
			Log.i(LOG_TAG, "Time:" + time);
			date = date + " " + time;
			SimpleDateFormat sdf;

			sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			netDate = sdf.parse(date);

			// dt.setHours(dt.getHours()+ 5 );
			// dt.setMinutes(dt.getMinutes()+31);

			Date now = new Date();
			Log.i(LOG_TAG, "Date from server ");
			Log.i(LOG_TAG, "UTC:" + netDate.toString());
			Log.i(LOG_TAG, "Current System time: " + now.toString());
			isTimeSet=true;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.print("\nError:" + e);

		} catch (ParseException pe) {
			pe.printStackTrace();
			System.out.print("\nError:" + pe);

		} catch (NoSuchElementException pe) {
			pe.printStackTrace();
			System.out.print("\nError:" + pe);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (sock != null)
				try {
					sock.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
		}
		if (netDate != null) {
			
			Calendar c = Calendar.getInstance();
			c.setTime(netDate);
			long when = c.getTimeInMillis();

			if (when / 1000 < Integer.MAX_VALUE) {
				SystemClock.setCurrentTimeMillis(when);
			}
		}
	}
}
