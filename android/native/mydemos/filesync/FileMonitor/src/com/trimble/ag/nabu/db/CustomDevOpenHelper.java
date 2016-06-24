package com.trimble.ag.nabu.db;

import com.trimble.ag.filemonitor.dao.FileSyncDaoMaster;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import java.io.File;

public class CustomDevOpenHelper extends FileSyncDaoMaster.DevOpenHelper {

	public static boolean IS_STORE_ON_SD = true;
	//adb pull /sdcard/Android/data/com.trimble.ag.filemonitor/cache/fileDataStore.db .
	public final static String DB_NAME = "fileDataStore.db";

	// public static final String DATABASE_FILE_PATH =
	// Environment.getExternalStorageDirectory();

//	public static final String DB_FULL_PATH = Environment
//			.getExternalStorageDirectory().getAbsolutePath()
//			+ File.separator
//			+ DB_FOLDERNAME + File.separator + DB_NAME;


	public static String DB_FULL_PATH = null;
	
	public CustomDevOpenHelper(Context context) {
        super(context, ((Build.VERSION.SDK_INT > 7 && IS_STORE_ON_SD) ? getDatabasePath(context) : DB_NAME),
                null);
        // super(context, DB_NAME, null, DB_VERSION); //Per Bob, we move the DB
        // file back to the internal memory.
    }

	public static String getDatabasePath(final Context context) {
		return DB_FULL_PATH = context.getApplicationContext().getExternalCacheDir().getAbsolutePath()
		            + File.separator + CustomDevOpenHelper.DB_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.trimble.agmantra.dao.DaoMaster.OpenHelper#onCreate(android.database
	 * .sqlite .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		super.onCreate(db);

	}

	public void deleteDatabase(Context context) {
		context.deleteDatabase((IS_STORE_ON_SD) ? (DB_FULL_PATH) : DB_NAME);
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
