
package com.spime.gps;

import android.app.Activity;
import android.content.ContentQueryMap;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class GPSActivity extends Activity {
    /** Called when the activity is first created. */
    private Button btStart = null;
    private Button btStop = null;
    private TextView tvRemainCount = null;
    private EditText edInterval = null;
    private EditText edTestCount = null;
    private EditText edNextInterVal = null;
    private TimerTask task = null;
    private Timer timer = null;
    private int iOnOffInter = 0;
    private int iTestCount = 0;
    private static final String LOG="GPSTest";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btStop = (Button) findViewById(R.id.stop);
        btStop.setEnabled(false);
        btStart = (Button) findViewById(R.id.start);
        tvRemainCount = (TextView) findViewById(R.id.remainingcount);
        edInterval = (EditText) findViewById(R.id.interval);
        edTestCount = (EditText) findViewById(R.id.testcount);
        edNextInterVal = (EditText) findViewById(R.id.nextinterval);
        // Intent callGPSSettingIntent = new Intent(
        // android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        // startActivity(callGPSSettingIntent);
        Handler m_Handler = new Handler();
        ContentResolver resolver = getContentResolver();
        Cursor settingsCursor = resolver.query(Settings.Secure.CONTENT_URI, null,
                "(" + Settings.System.NAME + "=?)",
                new String[] {
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED
                },
                null);
        ContentQueryMap mSettings = new ContentQueryMap(settingsCursor, Settings.System.NAME, true,
                m_Handler);
        SettingsObserver settingsObserver = new SettingsObserver();
        mSettings.addObserver(settingsObserver);

    }

    private final class SettingsObserver implements Observer {

        @Override
        public void update(java.util.Observable observable, Object data) {
            ContentResolver resolver = getContentResolver();
            if(isLocationProviderEnabled(resolver, LocationManager.GPS_PROVIDER)){
                Log.i(LOG, "use "+LocationManager.GPS_PROVIDER+" is setting enable");
            }else{
                Log.i(LOG, "use "+LocationManager.GPS_PROVIDER+" is setting disabled");
            }
            
        }
    }
    /**
     * Helper method for determining if a location provider is enabled.
     * @param cr the content resolver to use
     * @param provider the location provider to query
     * @return true if the provider is enabled
     */
    private  boolean isLocationProviderEnabled(ContentResolver cr, String provider) {
        String allowedProviders = Settings.Secure.getString(cr, Secure.LOCATION_PROVIDERS_ALLOWED);
        return delimitedStringContains(allowedProviders, ',', provider);
    }
    
    private static boolean isEmpty(CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
    private static boolean delimitedStringContains(
            String delimitedString, char delimiter, String item) {
        if (isEmpty(delimitedString) || isEmpty(item)) {
            return false;
        }
        int pos = -1;
        int length = delimitedString.length();
        while ((pos = delimitedString.indexOf(item, pos + 1)) != -1) {
            if (pos > 0 && delimitedString.charAt(pos - 1) != delimiter) {
                continue;
            }
            int expectedDelimiterPos = pos + item.length();
            if (expectedDelimiterPos == length) {
                // Match at end of string.
                return true;
            }
            if (delimitedString.charAt(expectedDelimiterPos) == delimiter) {
                return true;
            }
        }
        return false;
    }
    private String getText(EditText editText) {
        String stData = editText.getText().toString().trim();
        if (stData.equals("")) {
            stData = "0";
        }
        return stData;
    }

    private long delay = 0;

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                delay = Long.parseLong(getText(edNextInterVal)) * 1000;
                if (delay == 0) {
                    Toast.makeText(this, "Next Inteval couldn't be zero", Toast.LENGTH_SHORT)
                            .show();
                    break;
                }
                iOnOffInter = Integer.parseInt(getText(edInterval));
                if (iOnOffInter == 0) {
                    Toast.makeText(this, "On/Off Inteval couldn't be zero", Toast.LENGTH_SHORT)
                            .show();
                    break;
                }
                iTestCount = Integer.parseInt(getText(edTestCount));
                if (iTestCount == 0) {
                    Toast.makeText(this, "Test Count couldn't be zero", Toast.LENGTH_SHORT).show();
                    break;
                }
                timer = new Timer();
                task = new TimerTask() {

                    @Override
                    public void run() {
                        while (iTestCount > 0) {
                            Log.i(LOG,"run testCount:" + iTestCount);
                            Settings.Secure.setLocationProviderEnabled(getContentResolver(),
                                    LocationManager.GPS_PROVIDER, true);
                            // turnGPSOn();
                            Log.i(LOG,"GPS is turn on by code");
                            try {
                                Thread.sleep(iOnOffInter);
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }
                            String provider = null;
                            do {
                                provider = Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                            }
                            while (!provider.contains("gps"));
                            Log.i(LOG,"GPS is turn on after tight loop");
                            Settings.Secure.setLocationProviderEnabled(getContentResolver(),
                                    LocationManager.GPS_PROVIDER, false);
                            // turnGPSOff();
                            Log.i(LOG,"GPS is turn off by code");
                            do {
                                provider = Settings.Secure.getString(getContentResolver(),
                                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                            }
                            while (provider.contains("gps"));
                            try {
                                Log.i(LOG,"GPS is turn off after thread sleep");
                                Thread.sleep(delay);
                            } catch (InterruptedException e) {

                                e.printStackTrace();
                            }

                            handler.sendEmptyMessage(1);
                            Log.i(LOG,"iTestCount:" + iTestCount);
                            iTestCount--;
                        }
                        Log.i(LOG,"stop:" + iTestCount);
                        handler.sendEmptyMessage(2);
                    }
                };
                timer.schedule(task, 0);
                btStart.setEnabled(false);
                btStop.setEnabled(true);
                handler.sendEmptyMessage(1);
                break;
            case R.id.stop:

                stop();
                break;

            default:
                break;
        }

    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    tvRemainCount.setText(String.valueOf(iTestCount));
                    break;
                case 2:
                    stop();
                    break;
                default:
                    break;
            }

        }
    };

    private void stop() {
        if (task != null) {
            task.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
        btStart.setEnabled(true);
        btStop.setEnabled(false);
    }

    GPSEnableReceiver receiver = new GPSEnableReceiver();

    @Override
    protected void onResume() {
        // IntentFilter filter = new IntentFilter();
        // filter.addAction("android.location.GPS_ENABLED_CHANGE");
        // filter.addAction("android.location.GPS_FIX_CHANGE");
        // registerReceiver(receiver, filter);
        Toast.makeText(this, "GPS register", Toast.LENGTH_SHORT).show();
        super.onResume();

    }

    private void turnGPSOn() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) {
            // if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void turnGPSOff() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps")) {
            // if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onDestroy() {
        // unregisterReceiver(receiver);
        Toast.makeText(this, "GPS deregister", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
