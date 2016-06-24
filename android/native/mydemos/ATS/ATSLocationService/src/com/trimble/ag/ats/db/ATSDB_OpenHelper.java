/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name: atsLocationService
 *
 * Module Name: com.trimble.ag.ats.db
 *
 * File name: CustomDevHelper.java
 *
 * Author: sprabhu
 *
 * Created On: 27-Oct-2015 11:28:41 pm
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.ats.db;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Build;

import com.trimble.ag.ats.dao.ATSDaoMaster;

import java.io.File;

/**
 * @author sprabhu
 *
 */
public class ATSDB_OpenHelper extends ATSDaoMaster.DevOpenHelper {

   public static boolean      IS_STORE_ON_SD = false;
   // adb pull /sdcard/Android/data/com.trimble.ag.ats/cache/locationStore.db .

   public final static String DB_NAME        = "locationStore.db";

   /**
    * @param context
    * @param name
    * @param factory
    */
   public ATSDB_OpenHelper(Context context, String name, CursorFactory factory) {
      super(context, name, factory);

   }

   // public static final String DATABASE_FILE_PATH =
   // Environment.getExternalStorageDirectory();

   public static String DB_FULL_PATH = null;

   public ATSDB_OpenHelper(Context context) {
      super(
            context,
            ((Build.VERSION.SDK_INT > 7 && (IS_STORE_ON_SD = isAppDebugable(context))) ? getDatabasePath(context)
                  : DB_NAME), null);
      // super(context, DB_NAME, null, DB_VERSION);
      // Per Bob, we move the DB file back to the internal memory.
   }

   private static boolean isAppDebugable(final Context context) {
      return (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
   }

   public static String getDatabasePath(final Context context) {
      File file = context.getApplicationContext().getExternalCacheDir();
      if (file == null) {
         file = context.getCacheDir();
      }

      return DB_FULL_PATH = file.getAbsolutePath() + File.separator + DB_NAME;
   }

   @Override
   public void onCreate(SQLiteDatabase db) {
      super.onCreate(db);

   }

   public void deleteDatabase(Context context) {
      context.deleteDatabase((IS_STORE_ON_SD) ? (DB_FULL_PATH) : DB_NAME);
   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      super.onUpgrade(db, oldVersion, newVersion);
   }
}