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
 *		LocationContent.java
 *
 * Author:
 *		sprabhu
 *
 * Created On:
 * 		30-Oct-2015 12:20:22 pm
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

import com.trimble.ag.ats.dao.LocationDao;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * @author sprabhu
 *
 */
public class LocationContent {

   /**
    * 
    */
   private LocationContent() {

   }

   /**
    * Content provider authority.
    */
   public static final String CONTENT_AUTHORITY = "com.trimble.ag.ats";

   /**
    * Base URI. (content://com.trimble.ag.ats)
    */
   public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

   /**
    * Path component for "Location"-type resources..
    */
   private static final String PATH_ENTRIES = "locations";

   /**
    * Columns supported by "LocationEntries" records.
    */
   public static class LocationEntry implements BaseColumns {
       /**
        * MIME type for lists of entries.
        */
       public static final String CONTENT_TYPE =
               ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.locationsyncadapter.locations";
       /**
        * MIME type for individual entries.
        */
       public static final String CONTENT_ITEM_TYPE =
               ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.locationsyncadapter.location";

       /**
        * Fully qualified URI for "location" resources.
        */
       public static final Uri CONTENT_URI =
               BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

       /**
        * Table name where records are stored for "entry" resources.
        */
       public static final String TABLE_NAME = LocationDao.TABLENAME;
       /**
        * Atom ID. (Note: Not to be confused with the database primary key, which is _ID.
        */
       public static final String COLUMN_NAME_LOCATION_ID = LocationDao.Properties.LocationId.columnName;
       
      public static final String COLUMN_NAME_LATITUDE    = LocationDao.Properties.Latitude.columnName;

      public static final String COLUMN_NAME_LONGTITUDE  = LocationDao.Properties.Longtitude.columnName;

      public static final String COLUMN_NAME_SPEED       = LocationDao.Properties.Speed.columnName;

      public static final String COLUMN_NAME_TIME        = LocationDao.Properties.Time.columnName;

      public static final String COLUMN_NAME_IS_SYNCED   = LocationDao.Properties.IsSynced.columnName;

      public static final String COLUMN_NAME_HEADING     = LocationDao.Properties.Heading.columnName;
      
      public static final String COLUMN_NAME_ORGANIZATION_ID = LocationDao.Properties.OrgId.columnName;
      
      public static final String COLUMN_NAME_ALTITUDE = LocationDao.Properties.Altitude.columnName;
       
       
   }
}
