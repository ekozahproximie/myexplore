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
 *		com.trimble.ag.ats.db       		
 *
 * File name:
 *		DbContentProvider.java
 *
 * Author:
 *		sprabhu
 *
 * Created On:
 * 		30-Oct-2015 12:18:23 pm
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
package com.trimble.ag.ats.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.trimble.ag.ats.utils.SelectionBuilder;


/**
 * @author sprabhu
 *
 */
public class DbContentProvider extends ContentProvider {
   
   
   private ATSDB_OpenHelper mDatabaseHelper;

   /**
    * Content authority for this provider.
    */
   public static final String AUTHORITY = LocationContent.CONTENT_AUTHORITY;

   // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
   // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these
   // IDs.
   //
   // When a incoming URI is run through sUriMatcher, it will be tested against the defined
   // URI patterns, and the corresponding route ID will be returned.
   /**
    * URI ID for route: /locations
    */
   public static final int LOCATION_ENTRIES = 1;

   /**
    * URI ID for route: /locations/{ID}
    */
   public static final int LOCATION_ENTRIES_ID = 2;

   /**
    * UriMatcher, used to decode incoming URIs.
    */
   private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
   static {
       sUriMatcher.addURI(AUTHORITY, "locations", LOCATION_ENTRIES);
       sUriMatcher.addURI(AUTHORITY, "locations/*", LOCATION_ENTRIES_ID);
   }

   @Override
   public boolean onCreate() {
      final ATSContentProvdier contentProvdier = ATSContentProvdier.getInstance(getContext().getApplicationContext());
       mDatabaseHelper = contentProvdier.getDevDbOpenHelper();
       return true;
   }

   /**
    * Determine the mime type for entries returned by a given URI.
    */
   @Override
   public String getType(Uri uri) {
       final int match = sUriMatcher.match(uri);
       switch (match) {
           case LOCATION_ENTRIES:
               return LocationContent.LocationEntry.CONTENT_TYPE;
           case LOCATION_ENTRIES_ID:
               return LocationContent.LocationEntry.CONTENT_ITEM_TYPE;
           default:
               throw new UnsupportedOperationException("Unknown uri: " + uri);
       }
   }

   /**
    * Perform a database query by URI.
    *
    * <p>Currently supports returning all entries (/entries) and individual entries by ID
    * (/entries/{ID}).
    */
   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                       String sortOrder) {
       SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
       SelectionBuilder builder = new SelectionBuilder();
       int uriMatch = sUriMatcher.match(uri);
       switch (uriMatch) {
           case LOCATION_ENTRIES_ID:
               // Return a single entry, by ID.
               String id = uri.getLastPathSegment();
               builder.where(LocationContent.LocationEntry._ID + "=?", id);
           case LOCATION_ENTRIES:
               // Return all known entries.
               builder.table(LocationContent.LocationEntry.TABLE_NAME)
                      .where(selection, selectionArgs);
               Cursor c = builder.query(db, projection, sortOrder);
               // Note: Notification URI must be manually set here for loaders to correctly
               // register ContentObservers.
               Context ctx = getContext();
               assert ctx != null;
               c.setNotificationUri(ctx.getContentResolver(), uri);
               return c;
           default:
               throw new UnsupportedOperationException("Unknown uri: " + uri);
       }
   }

   /**
    * Insert a new entry into the database.
    */
   @Override
   public Uri insert(Uri uri, ContentValues values) {
       final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
       assert db != null;
       final int match = sUriMatcher.match(uri);
       Uri result;
       switch (match) {
           case LOCATION_ENTRIES:
               long id = db.insertOrThrow(LocationContent.LocationEntry.TABLE_NAME, null, values);
               result = Uri.parse(LocationContent.LocationEntry.CONTENT_URI + "/" + id);
               break;
           case LOCATION_ENTRIES_ID:
               throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
           default:
               throw new UnsupportedOperationException("Unknown uri: " + uri);
       }
       // Send broadcast to registered ContentObservers, to refresh UI.
       Context ctx = getContext();
       assert ctx != null;
       ctx.getContentResolver().notifyChange(uri, null, false);
       return result;
   }

   /**
    * Delete an entry by database by URI.
    */
   @Override
   public int delete(Uri uri, String selection, String[] selectionArgs) {
       SelectionBuilder builder = new SelectionBuilder();
       final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
       final int match = sUriMatcher.match(uri);
       int count;
       switch (match) {
           case LOCATION_ENTRIES:
               count = builder.table(LocationContent.LocationEntry.TABLE_NAME)
                       .where(selection, selectionArgs)
                       .delete(db);
               break;
           case LOCATION_ENTRIES_ID:
               String id = uri.getLastPathSegment();
               count = builder.table(LocationContent.LocationEntry.TABLE_NAME)
                      .where(LocationContent.LocationEntry._ID + "=?", id)
                      .where(selection, selectionArgs)
                      .delete(db);
               break;
           default:
               throw new UnsupportedOperationException("Unknown uri: " + uri);
       }
       // Send broadcast to registered ContentObservers, to refresh UI.
       Context ctx = getContext();
       assert ctx != null;
       ctx.getContentResolver().notifyChange(uri, null, false);
       return count;
   }

   /**
    * Update an etry in the database by URI.
    */
   @Override
   public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
       SelectionBuilder builder = new SelectionBuilder();
       final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
       final int match = sUriMatcher.match(uri);
       int count;
       switch (match) {
           case LOCATION_ENTRIES:
               count = builder.table(LocationContent.LocationEntry.TABLE_NAME)
                       .where(selection, selectionArgs)
                       .update(db, values);
               break;
           case LOCATION_ENTRIES_ID:
               String id = uri.getLastPathSegment();
               count = builder.table(LocationContent.LocationEntry.TABLE_NAME)
                       .where(LocationContent.LocationEntry._ID + "=?", id)
                       .where(selection, selectionArgs)
                       .update(db, values);
               break;
           default:
               throw new UnsupportedOperationException("Unknown uri: " + uri);
       }
       Context ctx = getContext();
       assert ctx != null;
       ctx.getContentResolver().notifyChange(uri, null, false);
       return count;
   }

  
}
