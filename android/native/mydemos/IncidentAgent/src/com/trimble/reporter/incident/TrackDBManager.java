package com.trimble.reporter.incident;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class TrackDBManager {
	private SQLiteStatement insertThumbnail;
	private SQLiteDatabase db;
	private Context context;
	
	private static TrackDBManager dbManager = null;
	private static final String DATABASE_NAME = "tracktable.db";

	private static final int DATABASE_VERSION = 1;
	
	

	private TrackDBManager(Context context) {
		if(dbManager != null){
			throw new IllegalAccessError("Already instance created");
		}
		openDataBase(context);
		this.context = context;
	}
	 
	public static synchronized TrackDBManager getInstance(Context context){
		if(dbManager == null){
			dbManager = new TrackDBManager(context);
		}
		return dbManager;
	}
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		throw new  CloneNotSupportedException("not allowed");
	}
	
	private final void openDataBase(Context context) {
		TrackDataBaseHelper openHelper = new TrackDataBaseHelper(context,
				DATABASE_NAME, null, DATABASE_VERSION);
		db = openHelper.getWritableDatabase();
	}

	public final void insertOrUpdateThumbnail(byte[] data, double dLat,
			double dLon, String stComments,String stCatagory,String stIncidentID) {

		if (insertThumbnail == null) {
			String stQuery = "INSERT INTO " + TrackTable.TABLE_TRACK + " ( "
					+ TrackTable.COLUMN_ID + ","
					+ TrackTable.COLUMN_DATE_AND_TIME + ","
					+ TrackTable.COLUMN_COMMENTS + ","
					+ TrackTable.COLUMN_IMAGE + ","
					+ TrackTable.COLUMN_LATITUDE + ","
					+ TrackTable.COLUMN_LONGTITUDE+","
					+TrackTable.COLUMN_CATAGORY+","
					+TrackTable.COLUMN_INCIDENT_INTERNAL
					+ " ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			insertThumbnail = db.compileStatement(stQuery);
		}
		int index = 1;
		insertThumbnail.bindNull(index++); // ID - Primary key

		insertThumbnail.bindString(index++, new Date().toLocaleString());
		insertThumbnail.bindString(index++, stComments);
		if (data != null) {
			try {
				insertThumbnail.bindBlob(index++, data);
			} catch (Exception e) {
				insertThumbnail.bindNull(index++);
			}
		} else {
			insertThumbnail.bindNull(index++);
		}
		insertThumbnail.bindString(index++, String.valueOf(dLat));
		insertThumbnail.bindString(index++, String.valueOf(dLon));
		insertThumbnail.bindString(index++, stCatagory);
		insertThumbnail.bindString(index++, stIncidentID);
		// long id =
		insertThumbnail.executeInsert();
		insertThumbnail.clearBindings();
		// Debug.debugWrite("STM:: thumbnail" + id +
		// "is inserted for media" + c.getLong(0));

	}
	
	// get all results of incident report as a cursor
	   public final Cursor getIncidentResultsCursor() {
	      
	      return db.rawQuery("SELECT * FROM " + TrackTable.TABLE_TRACK, null);
	   }
	   
	// delete all results of incident report 
	   public final void clearAllIncidentReport() {
	      
	    db.delete(TrackTable.TABLE_TRACK, null,null);
	   }
	   
	// delete all results of incident report 
	   public final void deleteIncidentById(String stIncidentID) {
	      
	    db.delete(TrackTable.TABLE_TRACK, TrackTable.COLUMN_INCIDENT_INTERNAL+"='"+stIncidentID+"'",null);
	   }
}
