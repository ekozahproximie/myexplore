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
 *      com.trimble.reporter.utils
 *
 * File name:
 *		Utils.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 14, 2012 11:50:52 PM
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



package com.trimble.reporter.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

/**
 * @author sprabhu
 *
 */

public class Utils {

    // File folder delete
    public static void deleteJobFileDir(File dir) {

       File[] fileList = dir.listFiles();
       if (fileList != null) { // some JVMs return null for empty dirs
          for (File file : fileList) {
             if (file.isDirectory()) {
                deleteJobFileDir(file);
             } else {
                file.delete();
             }
          }
       }
       dir.delete();
    }
    
    public static final boolean isInternetAvailable(Context ctx) {
        boolean lRetVal = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo nInfo = cm.getActiveNetworkInfo();
                if (null != nInfo) {
                    lRetVal = nInfo.isConnectedOrConnecting();
                }
            }
        } catch (Exception e) {
            return lRetVal;
        }

        return lRetVal;
    }
    
    public static boolean isAirplaneModeOn(Context ctx) {
        boolean isModeOn;
        isModeOn = Settings.System.getInt(ctx.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        return isModeOn;
    }
 // Displays an error if the app is unable to load content.
    public static void showError(Context context, String Mesg) {

        Toast.makeText(context, Mesg, Toast.LENGTH_LONG).show();
    }
    public static String getDeviceUUID(Context context){
        final String EMPTY = "";
        

        final TelephonyManager tm = (TelephonyManager)context
                .getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice = EMPTY + tm.getDeviceId();
        //tmSerial = EMPTY + tm.getSimSerialNumber();
        String androidId = EMPTY
                + android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) |tm.getPhoneType());

        
        String stDeviceId = deviceUuid.toString();
        Log.i("Utils", "Device ID generated = " + stDeviceId);
        return stDeviceId;
    }
    
 // Save Image from URL to Dest Location
    public static boolean saveUriImage(Uri uri, Context context,
          String stDesFile_path) throws FileNotFoundException, IOException {
       final int FILE_SIZE = 1024*150;
       boolean isSuccess = false;
       FileInputStream in = null;
       FileOutputStream out = null;
       if (uri == null) {
          return false;
       }

       try {
          String stFileName = uri.getPath();
          in = new FileInputStream(new File(stFileName));
          byte[] data = new byte[in.available()];
          in.read(data, 0, data.length);
          in.close();
          Bitmap bmp = makeBitmap(data, FILE_SIZE,null);
          if(bmp != null){
          out = new FileOutputStream(stDesFile_path);
          bmp.compress(Bitmap.CompressFormat.PNG, 75, out);
          out.flush();
          isSuccess = true;
          }
          /*try {
             if (context != null) {
                context.getContentResolver().delete(uri, null, null);
             }
          } catch (IllegalArgumentException e) {
             String stFilePath = uri.getPath();
             File file = new File(stFilePath);
             boolean isDeleted = file.delete();
             if (!isDeleted) {
                e.printStackTrace();
             }

          }*/
          
       } catch (FileNotFoundException e) {
          throw e;
       } finally {
          if (out != null) {
             out.close();
          }
          if (in != null) {
             in.close();
          }
       }
       return isSuccess;
    }

    public static Bitmap makeBitmap(byte[] jpegData, int maxNumOfPixels,Activity mActivity) {
       try {
    	   if(mActivity != null){
    	   DisplayMetrics metrics = new DisplayMetrics();
    	      mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    	      maxNumOfPixels= (int)(maxNumOfPixels * metrics.density);
   	   }
          BitmapFactory.Options options = new BitmapFactory.Options();
          options.inJustDecodeBounds = true;
          BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
          
         
          
          if (options.mCancel || options.outWidth == -1
                || options.outHeight == -1) {
             return null;
          }
          options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixels);
          options.inJustDecodeBounds = false;

          options.inDither = false;
          options.inPreferredConfig = Bitmap.Config.ARGB_8888;
          return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length,
                options);
       } catch (OutOfMemoryError ex) {
          Log.e("Test", "Got oom exception ", ex);
          return null;
       }
    }
    
    public static Bitmap makeBitmap(InputStream inputStream, int maxNumOfPixels, Activity mActivity) {
        try {
        	
        	 if(mActivity != null){
          	   DisplayMetrics metrics = new DisplayMetrics();
          	      mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
          	      maxNumOfPixels= (int)(maxNumOfPixels * metrics.density);
         	   }
           BitmapFactory.Options options = new BitmapFactory.Options();
           options.inJustDecodeBounds = true;
           BitmapFactory.decodeStream(inputStream,null, options);
           if (options.mCancel || options.outWidth == -1
                 || options.outHeight == -1) {
              return null;
           }
           options.inSampleSize = computeSampleSize(options, -1, maxNumOfPixels);
           options.inJustDecodeBounds = false;

           options.inDither = false;
           options.inPreferredConfig = Bitmap.Config.ARGB_8888;
           return BitmapFactory.decodeStream(inputStream,null, options);
        } catch (OutOfMemoryError ex) {
           Log.e("Test", "Got oom exception ", ex);
           return null;
        }
     }

    public static int computeSampleSize(BitmapFactory.Options options,
          int minSideLength, int maxNumOfPixels) {
       int initialSize = computeInitialSampleSize(options, minSideLength,
             maxNumOfPixels);

       int roundedSize;
       if (initialSize <= 8) {
          roundedSize = 1;
          while (roundedSize < initialSize) {
             roundedSize <<= 1;
          }
       } else {
          roundedSize = (initialSize + 7) / 8 * 8;
       }

       return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
          int minSideLength, int maxNumOfPixels) {
       double w = options.outWidth;
       double h = options.outHeight;

       int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w
             * h / maxNumOfPixels));
       int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
             Math.floor(w / minSideLength), Math.floor(h / minSideLength));

       if (upperBound < lowerBound) {
          // return the larger one when there is no overlapping zone.
          return lowerBound;
       }

       if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
          return 1;
       } else if (minSideLength == -1) {
          return lowerBound;
       } else {
          return upperBound;
       }
    }
    public static void sendEmail_attchament(Activity context,String[] staEmailTo,
            String stSubject, String stEmailText,  String[] filePaths,String stReportTitle,int code){
         ArrayList<Uri> listilePaths = new ArrayList<Uri>();
         for (String file : filePaths)
         {
             
             if(file.length() == 0){
                 continue;
             }
             File fileIn = new File(file);
             Uri uri = Uri.fromFile(fileIn);
             listilePaths.add(uri);
         }
         Log.i("SendMail", "No of. Attachemnt To be sent :: " +listilePaths.size());
         Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
         intent.putExtra(Intent.EXTRA_EMAIL,staEmailTo);
         intent.putExtra(Intent.EXTRA_SUBJECT, stSubject);
         intent.putExtra(Intent.EXTRA_TEXT, stEmailText);
         intent.setType("application/zip");
         intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listilePaths);
         context.startActivityForResult(Intent.createChooser(intent, stReportTitle),code);
      }
}
