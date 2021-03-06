package com.trimble.vilicus.db;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;

import com.trimble.vilicus.dao.DaoMaster;

public class CustomDevOpenHelper extends DaoMaster.DevOpenHelper {

	public static boolean IS_STORE_ON_SD = false;

	public final static String DB_NAME = "vilicusDataStore.db";

	private final static String DB_FOLDERNAME = "Vilicus";

	// public static final String DATABASE_FILE_PATH =
	// Environment.getExternalStorageDirectory();

	public static final String DB_FULL_PATH = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator
			+ DB_FOLDERNAME + File.separator + DB_NAME;

	
	public CustomDevOpenHelper(Context context) {
        super(context, ((Build.VERSION.SDK_INT > 7 && IS_STORE_ON_SD) ? (DB_FULL_PATH) : DB_NAME),
                null);
        // super(context, DB_NAME, null, DB_VERSION); //Per Bob, we move the DB
        // file back to the internal memory.
    }

	public String getDatabasePath() {
		return DB_FULL_PATH;
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

}
