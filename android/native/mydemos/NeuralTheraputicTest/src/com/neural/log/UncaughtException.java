package com.neural.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;

import com.neural.constant.Constants;

public class UncaughtException implements UncaughtExceptionHandler {

	private java.lang.Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;
	   private Context context;
	   
	   public UncaughtException(java.lang.Thread.UncaughtExceptionHandler pDefaultExceptionHandler)
	   {
	      mUncaughtExceptionHandler = pDefaultExceptionHandler;
	   }

	   public void setContext(Context context) {
	      this.context = context;
	   }

	   @Override
	   public void uncaughtException(Thread t, Throwable e) {
	      final Writer result = new StringWriter();
	      final PrintWriter printWriter = new PrintWriter(result);
	      e.printStackTrace(printWriter);
	      System.out.println(result.toString());
	      
	     
	      if (true) {
	         if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
	            String root = Environment.getExternalStorageDirectory() + "/" + Constants.ROOT;
	            if (root.charAt(root.length() - 1) == '/')
	               root = root.substring(0, root.length() - 1);
	            String fileName =  Constants.APP_NAME + "_" + new Date() + ".hprof";
	            try {
	               File rootDir = new File(root);
	               boolean trimbleRootDirCreated = rootDir.exists();
	               if (!trimbleRootDirCreated) {
	                  trimbleRootDirCreated = rootDir.mkdirs();
	               }
	               if (trimbleRootDirCreated) {
	            	   File crashfile = new File(root, Constants.CRASH_LOG);
	            	   FileOutputStream fileOutputStream = new FileOutputStream(crashfile);
	            	   fileOutputStream.write(result.toString().getBytes());
	            	   fileOutputStream.flush();
	            	   fileOutputStream.close();
	            	   
	            	   
	                  File file = new File(root, fileName);
	                  file.createNewFile();
	                  System.gc();
	                  android.os.Debug.dumpHprofData(root + "/" + fileName);
	                  System.out.println("Heap dump file " + fileName + " has been saved to sdcard.");
	               }
	            } catch (IOException ioe) {
	               ioe.printStackTrace();
	            } catch (UnsupportedOperationException uoe) {}
	         }
	      }

	      
	      if (e instanceof OutOfMemoryError && context != null) {
	         showOutOfMemoryDialog();
	      } else {
	         // call original handler
	         mUncaughtExceptionHandler.uncaughtException(t, e);
	      }
	   }

	   private void showOutOfMemoryDialog() {
//	      final Dialog alert =
//	            new AlertDialog.Builder(context).setMessage(ResourceManager.getString("error_outOfMemory_fatal")).create();

	      new Thread() {
	         @Override
	         public void run() {
	            Looper.prepare();
	            //alert.show();
	            Looper.loop();
	         }
	      }.start();

	      try {
	         Thread.sleep(6000);
	      } catch (InterruptedException e) {
	      }
	      System.exit(10);
	   }

}
