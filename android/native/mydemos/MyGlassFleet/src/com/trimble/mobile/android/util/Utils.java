package com.trimble.mobile.android.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

public class Utils {

   private static final String EMAILTYPE               = "plain/text";
   public static final String  ST_NEWLINE              = "\n";
   public static final String  COLON                   = ": ";
   public static final String  COMMA                   = ",";
   public static final String  SERVER_DATE_FORMAT      = "yyyy-MM-dd'T'HH:mm:ss'Z'";
   public static final String  SERVER_DATE_FORMAT_MILI = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

   public static void sendEmail_attchament(Context context,
         String[] staEmailTo,

         String stSubject, String stEmailText, String[] filePaths,
         String stReportTitle) {

      ArrayList<Uri> listilePaths = new ArrayList<Uri>();

      if (filePaths != null) {
         for (String file : filePaths)

         {

            File fileIn = new File(Environment.getExternalStorageDirectory()
                  .getPath() + File.separator + file);

            Uri uri = Uri.fromFile(fileIn);
            listilePaths.add(uri);

         }

      }

      // Log.i("SendMail",
      // "No of. Attachemnt To be sent :: " + listilePaths.size());

      Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);

      intent.putExtra(Intent.EXTRA_EMAIL, staEmailTo);

      intent.putExtra(Intent.EXTRA_SUBJECT, stSubject);

      intent.putExtra(Intent.EXTRA_TEXT, stEmailText);

      intent.setType(EMAILTYPE);

      if (listilePaths.size() > 0) {
         intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listilePaths);

      }

      context.startActivity(Intent.createChooser(intent, stReportTitle));

   }

   public static boolean isInternetConnection(final Context context) {
      boolean isOnline = false;

      ConnectivityManager connMgnr = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo nwInfo = connMgnr.getActiveNetworkInfo();
      if (nwInfo != null && nwInfo.isConnectedOrConnecting()) {
         isOnline = true;
      }
      return isOnline;
   }

   /**
    * This method converts dp unit to equivalent pixels, depending on device
    * density.
    * 
    * @param dp
    *           A value in dp (density independent pixels) unit. Which we need
    *           to convert into pixels
    * @param context
    *           Context to get resources and device specific display metrics
    * @return A float value to represent px equivalent to dp depending on device
    *         density
    */
   public static float convertDpToPixel(float dp, Context context) {
      Resources resources = context.getResources();
      DisplayMetrics metrics = resources.getDisplayMetrics();
      float px = dp * (metrics.densityDpi / 160f);
      return px;
   }

   /**
    * This method converts device specific pixels to density independent pixels.
    * 
    * @param px
    *           A value in px (pixels) unit. Which we need to convert into db
    * @param context
    *           Context to get resources and device specific display metrics
    * @return A float value to represent dp equivalent to px value
    */
   public static float convertPixelsToDp(float px, Context context) {
      Resources resources = context.getResources();
      DisplayMetrics metrics = resources.getDisplayMetrics();
      float dp = px / (metrics.densityDpi / 160f);
      return dp;
   }

   public static boolean isSDCardMount() {
      return Environment.MEDIA_MOUNTED.equals(Environment
            .getExternalStorageState());
   }

   public static final String UTC_FORMAT      = "yyyy-MM-dd'T'HH:mm:ss'Z'";

   public static final String UTC_FORMAT_MILI = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

   public static Date utcToLocalTime(String stUTCTime, String stUTCFormat) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(stUTCFormat);
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date myDate = null;
      try {
         myDate = simpleDateFormat.parse(stUTCTime);
      } catch (ParseException e) {

         e.printStackTrace();
      }
      return myDate;
   }

   private static final String TAG             = Utils.class.getName();
   private static DateFormat   inputFormat     = null;
   private static DateFormat   inputFormatMili = null;

   public static Date readServerDateInUTC(String updateUTC) {
      if (updateUTC == null) {
         return null;
      }
      Date date = null;
      if (inputFormat == null) {
         inputFormat = new SimpleDateFormat(SERVER_DATE_FORMAT);
         inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      }

      try {
         date = inputFormat.parse(updateUTC);
      } catch (ParseException e) {
         Log.e(TAG, e.getMessage(), e);

      }
      return date;
   }

   public static Date readServerDateInUTC_Mili(String updateUTC) {
      if (updateUTC == null) {
         return null;
      }
      Date date = null;
      if (inputFormatMili == null) {
         inputFormatMili = new SimpleDateFormat(SERVER_DATE_FORMAT_MILI,
               Locale.getDefault());
         inputFormatMili.setTimeZone(TimeZone.getTimeZone("UTC"));
      }

      try {
         date = inputFormatMili.parse(updateUTC);
      } catch (ParseException e) {
         // Log.e(TAG, e.getMessage(), e);
         date = readServerDateInUTC(updateUTC);

      }
      return date;
   }

   public static String getFormattedDate_LocaleTimeZ(Date date, String stForamt) {
      if (stForamt == null || date == null) {
         return null;
      }

      final SimpleDateFormat formatter = new SimpleDateFormat(stForamt);
      formatter.setTimeZone(TimeZone.getDefault());
      return formatter.format(date);
   }

   public static String getFormattedDate_LocaleTimeZ(Date date,
         final Context appContext) {
      if (date == null || appContext == null) {
         return null;
      }
      final DateFormat dateFormat = android.text.format.DateFormat
            .getDateFormat(appContext);
      dateFormat.setTimeZone(TimeZone.getDefault());
      final DateFormat timeFormat = android.text.format.DateFormat
            .getTimeFormat(appContext);
      timeFormat.setTimeZone(TimeZone.getDefault());
      // final DateFormat dateFormat =
// DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM);
      return dateFormat.format(date) + " " + timeFormat.format(date);
   }

   public static String getFormattedDateOnly_LocaleTimeZ(Date date,
         final Context appContext) {
      if (date == null || appContext == null) {
         return null;
      }
      final DateFormat dateFormat = android.text.format.DateFormat
            .getDateFormat(appContext);
      dateFormat.setTimeZone(TimeZone.getDefault());

      return dateFormat.format(date);
   }

   public static String getFormattedTimeOnly_LocaleTimeZ(Date date,
         final Context appContext) {
      if (date == null || appContext == null) {
         return null;
      }

      final DateFormat timeFormat = android.text.format.DateFormat
            .getTimeFormat(appContext);
      timeFormat.setTimeZone(TimeZone.getDefault());

      return timeFormat.format(date);
   }

   public static String getSysFormattedDate(final Date date,
         final Context appContext) {
      if (date == null || appContext == null) {
         return null;
      }

      final DateFormat dateFormat = android.text.format.DateFormat
            .getDateFormat(appContext);
      dateFormat.setTimeZone(TimeZone.getDefault());

      return dateFormat.format(date);
   }

   public static String getUTCDatetimeAsString(final Date date) {
      final SimpleDateFormat sdf = new SimpleDateFormat(UTC_FORMAT);
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      final String utcTime = sdf.format(date);

      return utcTime;
   }

   public static void hideSoftKeyboard(Context context, EditText searchBox) {

      InputMethodManager imm = (InputMethodManager) context
            .getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

   }

  

   public static final boolean isThreadRunning(final String stThreadName) {
      boolean isThreadRuning = false;
      if (stThreadName == null || stThreadName.length() == 0) {
         return isThreadRuning;
      }
      if (Thread.getAllStackTraces() != null) {
         Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
         if (threadSet != null) {
            Thread[] runningThreadArray = threadSet
                  .toArray(new Thread[threadSet.size()]);
            for (int i = 0; i < runningThreadArray.length; i++) {
               Thread runningThread = runningThreadArray[i];
               if (runningThread != null) {
                  final String stRunningThreadName = runningThread.getName();
                  isThreadRuning = stThreadName.equals(stRunningThreadName);
                  if (isThreadRuning) {
                     break;
                  }
               }

            }
         }
      }

      return isThreadRuning;

   }
   public static Thread getThreadByName(String threadName)
   {
       Thread __tmp = null;
       Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
       Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
       for (int i = 0; i < threadArray.length; i++)
           if (threadArray[i].getName().equals(threadName))
               __tmp =  threadArray[i];
       return __tmp;
   }
   public static String getHours(long time) {

      final long timeInMillis = time;

      final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));

      int remdr = (int) (timeInMillis % (24L * 60 * 60 * 1000));

      final int hours = remdr / (60 * 60 * 1000);

      remdr %= 60 * 60 * 1000;

      final int minutes = remdr / (60 * 1000);

      remdr %= 60 * 1000;

      final int seconds = remdr / 1000;

      final int ms = remdr % 1000;

      final String stTime = hours + ":" + minutes + ":" + seconds;
      return stTime;

   }

   private static final String SPACE=" ";
   private static final long SECONDS_OF_MINUTE = 60L * 1000;
   private static final long MINUTES_OF_HOUR = 60 * SECONDS_OF_MINUTE;
   private static final long MILLSECONDS_OF_DAY = 24 * MINUTES_OF_HOUR;
   private static final long DAYS_OF_YEAR = 365 * MILLSECONDS_OF_DAY;
   private static final long DAYS_OF_MONTH = 30 * MILLSECONDS_OF_DAY;
   private static final long DAYS_OF_WEEK = 7 * MILLSECONDS_OF_DAY;
   
   public static String getWorkingHours(final long time, final Context context,
         final String stHours, final String stMinutes, final String stSeconds, 
         final String stDays, final String stMonths, final String stYears, final String stWeeks) {

      final StringBuilder stTime = new StringBuilder();
      
      if(time > 0){
         double remdr = time;

         double fCalculatedTime = remdr / DAYS_OF_YEAR;

         if (fCalculatedTime >= 1) {
            stTime.append(Utils.getDecimalFormat(2, fCalculatedTime));
            stTime.append(SPACE);
            stTime.append(stYears);
         }else if((fCalculatedTime = (remdr / DAYS_OF_MONTH)) >= 1){
            stTime.append(Utils.getDecimalFormat(2, fCalculatedTime));
            stTime.append(SPACE);
            stTime.append(stMonths);
         }else if((fCalculatedTime = (remdr / DAYS_OF_WEEK)) >= 1){
            stTime.append(Utils.getDecimalFormat(2, fCalculatedTime));
            stTime.append(SPACE);
            stTime.append(stWeeks);
         }else if((fCalculatedTime = (remdr / MILLSECONDS_OF_DAY)) >= 1){
            stTime.append(Utils.getDecimalFormat(2, fCalculatedTime));
            stTime.append(SPACE);
            stTime.append(stDays);
         }else if((fCalculatedTime = ((remdr % MILLSECONDS_OF_DAY) / MINUTES_OF_HOUR)) >= 1){
            stTime.append(Utils.getDecimalFormat(2, fCalculatedTime));
            stTime.append(SPACE);
            stTime.append(stHours);
         }else if((fCalculatedTime = ((remdr % MINUTES_OF_HOUR) / SECONDS_OF_MINUTE)) >= 1){
            stTime.append(Utils.getDecimalFormat(2, fCalculatedTime));
            stTime.append(SPACE);
            stTime.append(stMinutes);
         }else {
            remdr %= SECONDS_OF_MINUTE;
            fCalculatedTime = remdr / 1000;
            stTime.append(Utils.getDecimalFormat(2, fCalculatedTime));
            stTime.append(SPACE);
            stTime.append(stSeconds);
         }
      } else {
         stTime.append(Utils.getDecimalFormat(2, time));
         stTime.append(SPACE);
         stTime.append(stHours); 
      }
      
      return stTime.toString();

   }
   
   public static final String getDecimalFormat(final int iPrecision,
         double dData) {
      final StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < iPrecision; i++) {
         buffer.append("#");
      }
      DecimalFormat df = new DecimalFormat("#." + buffer.toString());
      return df.format(dData);
   }

   private static final String dnt_application_name          = "Application Name";
   private static final String dnt_application_version       = "Application Version";

   private static final String dnt_device_id                 = "Device ID";
   private static final String dnt_device_model              = "Device Model";
   private static final String dnt_device_name               = "Device Name";
   private static final String dnt_device_type               = "Android";
   private static final String dnt_application_build_version = "Application Build Version";

   private static final String dnt_device_uuid               = "Device UUID";
   private static final String dnt_android_version           = "Android Version";
   private static final String dnt_build_type                = "Build Type";

   public static String getDeviceInfoDetails(Context context,
         final String dnt_full_app_name, final String stBuildType) {

      StringBuffer buffer = new StringBuffer();
      final String PACKAGENAME = context.getApplicationContext()
            .getPackageName();

      // final String stDeviceType = "Farm Works Mate"; //
// android.os.Build.PRODUCT;
      final String stDeviceType = dnt_device_type; // TBD - to retrive phone
// name
      final String EMPTY = "";
      final String UNKNOWN = "(unknown)";

      final TelephonyManager tm = (TelephonyManager) context
            .getSystemService(Context.TELEPHONY_SERVICE);

      String tmDevice = EMPTY + tm.getDeviceId();

      String androidId = EMPTY
            + android.provider.Settings.Secure.getString(
                  context.getContentResolver(),
                  android.provider.Settings.Secure.ANDROID_ID);
      UUID deviceUuid = new UUID(androidId.hashCode(),
            ((long) tmDevice.hashCode() << 32) | tm.getPhoneType());

      String stDeviceId = deviceUuid.toString();

      String stDeviceName = android.os.Build.MANUFACTURER + " "
            + android.os.Build.MODEL;
      String stAndroidVersion = android.os.Build.VERSION.CODENAME + "_"
            + android.os.Build.VERSION.RELEASE;

      final PackageManager pm = context.getPackageManager();
      if (pm != null) {
         ApplicationInfo ai = null;
         PackageInfo pi = null;

         try {

            ai = pm.getApplicationInfo(PACKAGENAME, 0);
            pi = pm.getPackageInfo(PACKAGENAME, 0);
         } catch (NameNotFoundException e) {
            ai = null;
         }

         String stProductName = dnt_full_app_name;

         int stSoftBuildNo = (int) (pi != null ? pi.versionCode : 0);
         String stSoftVersion = (String) (pi != null ? pi.versionName : UNKNOWN);

         // construct device details here
         buffer.append(
               "-------------------- Device Details ---------------------")
               .append(ST_NEWLINE);
         buffer.append(dnt_application_name).append(COLON)
               .append(stProductName).append(ST_NEWLINE);
         buffer.append(dnt_application_version).append(COLON)
               .append(stSoftVersion).append(ST_NEWLINE);
         buffer.append(dnt_application_build_version).append(COLON)
               .append(stSoftBuildNo).append(ST_NEWLINE);
         buffer.append(dnt_device_id).append(COLON).append(tmDevice)
               .append(ST_NEWLINE);
         buffer.append(dnt_device_uuid).append(COLON).append(stDeviceId)
               .append(ST_NEWLINE);
         buffer.append(dnt_device_name).append(COLON).append(stDeviceName)
               .append(ST_NEWLINE);
         buffer.append(dnt_device_model).append(COLON).append(stDeviceType)
               .append(ST_NEWLINE);
         buffer.append(dnt_android_version).append(COLON)
               .append(stAndroidVersion).append(ST_NEWLINE);

// buffer.append("Serial Number: ").append(tm.getSimSerialNumber())
// .append(ST_NEWLINE);
// buffer.append("Device Type: ").append(stDeviceType)
// .append(ST_NEWLINE);
// buffer.append("Product Type: ").append(stProductType)
// .append(ST_NEWLINE);

         buffer.append(dnt_build_type).append(COLON).append(stBuildType)
               .append(ST_NEWLINE);

         buffer.append(
               "--------------------------------------------------------------")
               .append(ST_NEWLINE);
      }
      return buffer.toString();
   }

   public static final long WEEK = 60L *1000 *60 * 24 * 7; 
   
   public static boolean isLastReportedWithinAWeek(final Date lastReportedDate) {
      boolean isReportedWithinWeek = false;

      if(lastReportedDate != null){
         long lLastReportedTime = lastReportedDate.getTime();
         
         if(System.currentTimeMillis() - lLastReportedTime < WEEK){
            isReportedWithinWeek = true;
         }
      }
         
      return isReportedWithinWeek;
   }
   
 
   
}
