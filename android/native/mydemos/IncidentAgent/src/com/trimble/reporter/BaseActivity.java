/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.reporter
 *
 * File name:
 *		BaseActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 14, 2012 4:28:25 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */

package com.trimble.reporter;

import com.trimble.agent.R;
import com.trimble.reporter.app.TCCApplication;
import com.trimble.reporter.dialog.AlertDialogActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Toast;

/**
 * @author sprabhu
 */

public class BaseActivity extends Activity {
    public static final String SHOW_SPLASH = "splash";

    public static final String DIALOG_TITLE = "title";

    public static final String ALERT_MESSAGE = "message";

    public static final String ALERT_MESSAGE_TEXT = "messageText";

    public static final String ALERT_ICON = "icon";

    public static final String ALERT_POS = "pos";

    public static final String ALERT_NEG = "neg";

    public static final String ALERT_NEUTRAL = "neutral";

    public static final int SPLASH = 1;

    public static final int DIALOG_SHOW_GPS_SETTINGS = 2;
    
    public static final int ALERT_APP_EXIT = 3;
    
    public static final int CATEGORY_PICK = 4;
    
    public static final int ACTIVITY_REQUEST_CODE_PHOTO = 5;
    
    public static final int PHOTO_TAKE=6;
    
    public static final int LAT_LON_TAKE=7;
    
    public static final int FUNNY=8;

    private static final int RESULT_FIRST_USER = 0x1;// from: int
                                                     // android.app.Activity.RESULT_FIRST_USER
                                                     // = 1 [0x1]

    public static final int ALERT_RESULT_POS = RESULT_FIRST_USER + 1;

    public static final int ALERT_RESULT_NEG = ALERT_RESULT_POS + 1;

    public static final int ALERT_RESULT_NEUTRAL = ALERT_RESULT_NEG + 1;

    public Dialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        checkSplashScreen();
    }

    public void checkSplashScreen() {
        // show the splash in every launch
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (prefs.getBoolean(SHOW_SPLASH, true)) {
            // Start showing the splash screen right away, and then continue
            // with
            // initialization
            startActivityForResult(
                    (new Intent(this, ((TCCApplication)getApplication()).getSplashActivityClass())),
                    SPLASH);
            overridePendingTransition(0, 0);
            prefs.edit().putBoolean(SHOW_SPLASH, false).commit();
        }
     
    }
    public void checkDeviceGPS() {
        final android.location.LocationManager manager = (android.location.LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( android.location.LocationManager.GPS_PROVIDER ) ) {
      
                showDialog(DIALOG_SHOW_GPS_SETTINGS);
            
        }

    }
    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {

            case DIALOG_SHOW_GPS_SETTINGS:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.gps_title)
                        .setMessage(
                                String.format(getString(R.string.gps_disabled),
                                        getString(R.string.app_name)))
                        .setCancelable(true)
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.settings,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        // show location settings
                                        Intent intent = new Intent(
                                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivity(intent);
                                    }
                                });
                return builder.create();
            default:
                dialog = super.onCreateDialog(id);
                break;
        }
        return dialog;

    }

    public void showAlertDialog(int dialogId, int titleId, String message, int posId, int negId) {
        showAlertDialog(null, dialogId, titleId, message, posId, negId);
    }

    public void showAlertDialog(Activity fragment, int dialogId, int titleId, String message,
            int posId, int negId) {
        Intent intent = new Intent(this, AlertDialogActivity.class);
        intent.putExtra(DIALOG_TITLE, titleId);
        intent.putExtra(ALERT_MESSAGE_TEXT, message);
        intent.putExtra(ALERT_POS, posId);
        intent.putExtra(ALERT_NEG, negId);
        if (fragment == null) {
            startActivityForResult(intent, dialogId);
        } else {
            // this.startActivityFromFragment(fragment, intent, dialogId);
        }
    }

    public void showAlertDialog(int dialogId, int titleId, int messageId, int posId, int negId) {
        showAlertDialog(null, dialogId, titleId, getString(messageId), posId, negId);
    }
    private Toast mToast =null;
    public void showToast(String stMsg){
        if(mToast != null){
            mToast.cancel();
        }
        mToast=Toast.makeText(this, stMsg, Toast.LENGTH_LONG);
        mToast.show();
    }

}
