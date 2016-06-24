/**
 * Copyright Trimble Inc., 2015 - 2016 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *		atsLocationService
 *
 * Module Name:
 *		com.trimble.ag.ats.dao       		
 *
 * File name:
 *		atsDaoMaster.java
 *
 * Author:
 *		sprabhu
 *
 * Created On:
 * 		27-Oct-2015 11:30:26 pm
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  	Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.ats.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * @author sprabhu
 *
 */
public class ATSDaoMaster extends DaoMaster {

   /**
    * @param db
    */
   public ATSDaoMaster(SQLiteDatabase db) {
      super(db);
      
   }
   public static final int VERSION_1_1    = 1;
   

   public static final int SCHEMA_VERSION = VERSION_1_1;


   public static void createAllTables(SQLiteDatabase db, boolean ifNotExists,
         int schema_version) {
      switch (schema_version) {
         case VERSION_1_1:
            createInitVersionTables(db, ifNotExists);
            break;
         
         default:
            break;
      }

   }

   private static void createInitVersionTables(SQLiteDatabase db,
         boolean ifNotExists) {
   DaoMaster.createAllTables(db, ifNotExists);
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

    
      private Context             context          = null;

    
      public DevOpenHelper(Context context, String name, CursorFactory factory) {
         super(context, name, factory);
         this.context = context;
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       
      }
    
   }

}
