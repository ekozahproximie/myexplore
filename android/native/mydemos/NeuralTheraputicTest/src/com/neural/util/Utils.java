package com.neural.util;

import java.io.File;
import java.util.Set;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.StatFs;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utils {
	public static final long LOW_STORAGE_THRESHOLD = 512L * 1024L;
	private static final String TAG = "Utils";

	/**
	 * Returns
	 * 
	 * @return number of bytes available, or an ERROR code.
	 */
	public static long getAvailableStorage() {
		try {
			if (!hasStorage()) {
				return NO_STORAGE_ERROR;
			} else {
				String storageDirectory = Environment
						.getExternalStorageDirectory().toString();
				StatFs stat = new StatFs(storageDirectory);
				return (long) stat.getAvailableBlocks()
						* (long) stat.getBlockSize();
			}
		} catch (Exception ex) {
			// if we can't stat the filesystem then we don't know how many
			// free bytes exist. It might be zero but just leave it
			// blank since we really don't know.
			Log.e(TAG, "Fail to access sdcard", ex);
			return CANNOT_STAT_ERROR;
		}
	}

	private static boolean checkFsWritable() {
		// Create a temporary file to see whether a volume is really writeable.
		// It's important not to put it in the root directory which may have a
		// limit on the number of files.
		String directoryName = Environment.getExternalStorageDirectory()
				.toString() + "/DCIM";
		File directory = new File(directoryName);
		if (!directory.isDirectory()) {
			if (!directory.mkdirs()) {
				return false;
			}
		}
		return directory.canWrite();
	}

	public static boolean hasStorage() {
		return hasStorage(true);
	}

	public static int getStorageStatus(boolean mayHaveSd) {
		long remaining = mayHaveSd ? getAvailableStorage() : NO_STORAGE_ERROR;
		if (remaining == NO_STORAGE_ERROR) {
			return STORAGE_STATUS_NONE;
		} else if (remaining == CANNOT_STAT_ERROR) {
			return STORAGE_STATUS_FAIL;
		}
		return remaining < LOW_STORAGE_THRESHOLD ? STORAGE_STATUS_LOW
				: STORAGE_STATUS_OK;
	}

	private static final long NO_STORAGE_ERROR = -1L;
	private static final long CANNOT_STAT_ERROR = -2L;
	public static final int STORAGE_STATUS_OK = 0;
	public static final int STORAGE_STATUS_LOW = 1;
	public static final int STORAGE_STATUS_NONE = 2;
	public static final int STORAGE_STATUS_FAIL = 3;

	public static boolean hasStorage(boolean requireWriteAccess) {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			if (requireWriteAccess) {
				boolean writable = checkFsWritable();
				return writable;
			} else {
				return true;
			}
		} else if (!requireWriteAccess
				&& Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
	
	public static boolean isSDCardMount() {
	      return Environment.MEDIA_MOUNTED.equals(Environment
	            .getExternalStorageState());
	   }
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 * 
	 * @param dp
	 *            A value in dp (density independent pixels) unit. Which we need
	 *            to convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on
	 *         device density
	 */
	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to density independent
	 * pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent dp equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;
	}
	
	public static final boolean isThreadRunning(final String stThreadName) {
	      boolean isThreadRuning = false;
	      if (stThreadName == null || stThreadName.length() == 0) {
	         return isThreadRuning;
	      }
	      if (Thread.getAllStackTraces() != null) {
	         Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
	         if (threadSet != null) {
	            Thread[] runningThreadArray = threadSet
	                  .toArray(new Thread[threadSet.size()]);
	            for (int i = 0; i < runningThreadArray.length; i++) {
	               Thread runningThread = runningThreadArray[i];
	               if (runningThread != null) {
	                  final String stRunningThreadName = runningThread.getName();
	                  isThreadRuning = stThreadName.equals(stRunningThreadName);
	                  if (isThreadRuning) {
	                     break;
	                  }
	               }

	            }
	         }
	      }

	      return isThreadRuning;

	   }
}
