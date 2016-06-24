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

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.spime.wifi.R;

public class AutoWifiOnActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView textView = (TextView) findViewById(R.id.info);
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

			System.out.print("\nDate:" + date);
			System.out.println("\nTime:" + time);
			Log.d("AUTO", "\nDate:" + date);
			Log.d("AUTO", "\nTime:" + time);
			date = date + " " + time;
			SimpleDateFormat sdf;

			sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			netDate = sdf.parse(date);

			// dt.setHours(dt.getHours()+ 5 );
			// dt.setMinutes(dt.getMinutes()+31);

			Date now = new Date();
			textView.setText("Date from server ");
			textView.append("\n UTC:" + netDate.toString());
			textView.append("\nCurrent System time: \n" + now.toString());

		} catch (IOException e) {
			e.printStackTrace();
			Log.e("AUTO", "Error", e);
			System.out.print("\nError:" + e);

		} catch (ParseException pe) {
			pe.printStackTrace();
			Log.e("AUTO", "Error", pe);
			System.out.print("\nError:" + pe);

		} catch (NoSuchElementException pe) {
			pe.printStackTrace();
			Log.e("AUTO", "Error", pe);
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
			/*int year = netDate.getYear();
			int month = 0;
			int day = 0;
			int hourOfDay = 0;
			int minute = 0;
			 c.set(Calendar.YEAR, year); c.set(Calendar.MONTH, month);
			  c.set(Calendar.DAY_OF_MONTH, day); c.set(Calendar.HOUR_OF_DAY,
			  hourOfDay); c.set(Calendar.MINUTE, minute);
			*/
			Calendar c = Calendar.getInstance();
			c.setTime(netDate);
			
			  

			long when = c.getTimeInMillis();

			if (when / 1000 < Integer.MAX_VALUE) {
				SystemClock.setCurrentTimeMillis(when);
			}
		}
	
		System.out.println("Date "
				+ new Date(android.os.SystemClock.currentThreadTimeMillis()));
	}
}