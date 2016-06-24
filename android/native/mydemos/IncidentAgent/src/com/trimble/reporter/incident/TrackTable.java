package com.trimble.reporter.incident;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class TrackTable {
	 // Database table
    public static final String TABLE_TRACK = "tracktable";

    public static SQLiteDatabase databaseStore;

    public static final String COLUMN_ID = "_id";

    public static final String COLUMN_DATE_AND_TIME = "datetime";

    public static final String COLUMN_LATITUDE = "latitude";

    public static final String COLUMN_LONGTITUDE = "longtitude";

    public static final String COLUMN_COMMENTS = "comments";

    public static final String COLUMN_IMAGE = "image";
    
    public static final String COLUMN_CATAGORY = "catagory";

    public static final String COLUMN_INCIDENT_INTERNAL = "incidentinternal";
    

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table " + TABLE_TRACK + "("
            + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_DATE_AND_TIME
            + " text not null, " + COLUMN_COMMENTS + " text not null," + COLUMN_IMAGE
            + " BLOB," + COLUMN_LATITUDE + " text not null," + COLUMN_LONGTITUDE
            + " text not null, "+ COLUMN_CATAGORY
            + " text not null,"+ COLUMN_INCIDENT_INTERNAL
            + " text not null"+  ");";

    public static void onCreate(SQLiteDatabase database) {
        databaseStore = database;
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TrackTable.class.getName(), "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);
        onCreate(database);
    }
}
