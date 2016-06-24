/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 * 
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 * 
 * Product Name: com.trimble.ag.nabu.dao
 * 
 * Module Name: com.trimble.ag.nabu.dao
 * 
 * File name: FileSyncDaoMaster.java
 * 
 * Author: karthiga
 * 
 * Created On: Jun 4, 20143:41:40 PM
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
package com.trimble.ag.filemonitor.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author kmuruga
 * 
 */
public class FileSyncDaoMaster extends DaoMaster {

   public FileSyncDaoMaster(SQLiteDatabase db) {
      super(db);
   }

   public static final int INIT_VERSION   = 1;

   public static final int SCHEMA_VERSION = INIT_VERSION;

   /** Creates underlying database table using DAOs. */
   public static void createAllTables(SQLiteDatabase db, boolean ifNotExists,
         int schema_version) {
      switch (schema_version) {
         case INIT_VERSION:
            DaoMaster.createAllTables(db, ifNotExists);
            break;
         default:
            break;
      }

   }

  

   public static abstract class OpenHelper extends SQLiteOpenHelper {

      public OpenHelper(Context context, String name, CursorFactory factory) {
         super(context, name, factory, SCHEMA_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         Log.i("greenDAO", "Creating tables for schema version "
               + SCHEMA_VERSION);
         createAllTables(db, false, SCHEMA_VERSION);
      }
   }

   /** WARNING: Drops all table on Upgrade! Use only during development. */
   public static class DevOpenHelper extends OpenHelper {

      private transient boolean isDBUpdated = false;

      public DevOpenHelper(Context context, String name, CursorFactory factory) {
         super(context, name, factory);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

      }

      /**
       * @return the isDBUpdated
       */
      public boolean isDBUpdated() {
         return isDBUpdated;
      }
   }
}
