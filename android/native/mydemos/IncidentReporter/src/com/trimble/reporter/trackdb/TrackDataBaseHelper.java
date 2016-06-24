package com.trimble.reporter.trackdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;



public class TrackDataBaseHelper extends SQLiteOpenHelper{

	 
	    
	    private Context myContext;

	    public TrackDataBaseHelper(
	 			Context context, 
	 			String name,
	 			CursorFactory factory, 
	 			int version) {
	 		super(context, name, factory, version);
	 		myContext = context;
	 	}
	    

	    @Override
	    public void onCreate(SQLiteDatabase database) {
	    	TrackTable.onCreate(database);
	    }

	    @Override
	    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
	    	TrackTable.onUpgrade(database, oldVersion, newVersion);
	    }
}
