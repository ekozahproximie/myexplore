package com.spime;



import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PhotoProvider extends ContentProvider {

	private static final String TAG = "PhotoProvider";
	private static final String DATABASE_NAME = "photo.db";
	private static final int DATABASE_VERSION = 1;
	private static final String PHOTO_TABLE = "photo";
	private static final int PHOTO = 1;
	private static final int PHOTO_ID = 2;
	private static final UriMatcher uriMatcher;
	public static final Uri CONTENT_URI = Uri
	.parse("content://com.spime.provider.photoProvider/photoProvider");;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.spime.provider.PhotoProvider", "photoProvider",
				PHOTO);
		uriMatcher.addURI("com.spime.provider.PhotoProvider", "photoProvider/#",
				PHOTO_ID);
		
	}
	
	// The underlying database
	private SQLiteDatabase photoDB;
	// Column Names
	public static final String KEY_ID = "_id";
	public static final String MY_IMAGE="image";
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
		case PHOTO:
			count = photoDB.delete(PHOTO_TABLE, where, whereArgs);
			break;
		case PHOTO_ID:
			String segment = uri.getPathSegments().get(1);
			count = photoDB.delete(PHOTO_TABLE,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case PHOTO:
			return "vnd.android.cursor.dir/vnd.spime.photoProvider";
		case PHOTO_ID:
			return "vnd.android.cursor.item/vnd.spime.photoProvider";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

	}

	@Override
	public Uri insert(Uri _uri, ContentValues values) {
		// Insert the new row, will return the row number if
		// successful.
		long rowID = photoDB.insert(PHOTO_TABLE, "photo",
				values);
		// Return a URI to the newly inserted row on success.
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + _uri);
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();
		PhotoDatabaseHelper dbHelper;
		dbHelper = new PhotoDatabaseHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
		photoDB = dbHelper.getWritableDatabase();
		return (photoDB == null) ? false : true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(PHOTO_TABLE);
		// If this is a row query, limit the result set to the passed in row.
		switch (uriMatcher.match(uri)) {
		case PHOTO_ID:
			qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			break;
		}
		// If no sort order is specified sort by date / time
		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = KEY_ID;
		} else {
			orderBy = sort;
		}
		// Apply the query to the underlying database.
		Cursor c = qb.query(photoDB, projection, selection, selectionArgs,
				null, null, orderBy);
		// Register the contexts ContentResolver to be notified if
		// the cursor result set changes.
		c.setNotificationUri(getContext().getContentResolver(), uri);
		// Return a cursor to the query result.
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
		case PHOTO:
			count = photoDB.update(PHOTO_TABLE, values, where,
					whereArgs);
			break;
		case PHOTO_ID:
			String segment = uri.getPathSegments().get(1);
			count = photoDB.update(PHOTO_TABLE, values,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	private static class PhotoDatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ PHOTO_TABLE + " (" + KEY_ID
				+ " integer primary key autoincrement, "+
				MY_IMAGE +" BLOB);";

		public PhotoDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + PHOTO_TABLE);
			onCreate(db);
		}

	}

}
