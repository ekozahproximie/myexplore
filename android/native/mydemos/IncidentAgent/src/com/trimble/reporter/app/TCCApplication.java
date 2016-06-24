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
 *		TCCApplication.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 14, 2012 4:26:43 PM
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

package com.trimble.reporter.app;

import com.trimble.reporter.SplashActivity;
import com.trimble.reporter.service.AgentLocationService;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

/**
 * @author sprabhu
 */

public class TCCApplication extends Application {

    /**
     * @return
     */
    
    private double dLat=0.0;
    private double dLon=0.0;
    
    private int iDeviceID=101;
    private Context mContext =null;
    /**
     * 
     */
    public TCCApplication() {
        super();
        mContext=this;
    }
    /**
     * @param dLat the dLat to set
     */
    public void setdLat(double dLat,double dLon) {
        this.dLat = dLat;
        this.dLon=dLon;
    }
    /**
     * @return the dLat
     */
    public double getLat() {
        return dLat;
    }
    /**
     * @return the dLon
     */
    public double getLon() {
        return dLon;
    }
    public Class<?> getSplashActivityClass() {

        return SplashActivity.class;
    }

    public void takePhoto(Activity activity, int activityRequestCode, Uri uri) {
        // create Intent to take a picture and return control to the calling
        // application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            // Log.i("TBA", "uri = " + uri);
            if (uri != null)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // set the image
                                                               // file name
            else
                Log.v("TBA", "Uri = null -> Default location will be used.");

            // start the image capture Intent
            activity.startActivityForResult(intent, activityRequestCode);
        } catch (Exception e) {
            Log.w("TBA", "Error taking photo: " + e);
            // activity.showToast(R.string.errorUnexpected);
        }
    }

    public int getSizeFromUri(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            return is.available();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void cancelPhotoCapture(Uri uri) {

        try {
            if (uri != null)
                getContentResolver().delete(uri, null, null);

        } catch (IllegalArgumentException e) {
            String stFilePath = uri.getPath();
            File file = new File(stFilePath);
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                e.printStackTrace();
            }

        }
    }
    /**
     * @return the iDeviceID
     */
    public int getDeviceID() {
        return iDeviceID;
    }
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            String stID=(String)msg.obj;
            AgentLocationService.fireNotification(mContext,stID);
        }
    };
    
    public void postMeg(String id){
        Message message=Message.obtain();
        message.obj=id;
        mHandler.sendMessage(message);
    }
}
