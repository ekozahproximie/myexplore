package com.neural.log;

import android.content.Context;

public class ExceptionHandler {
	private static ExceptionHandler mInstance;
	 
	   long mDeleteLogsBefore=-1;
	   private Context mContext =null;
	   private ExceptionHandler(){
	      //singleton class
	     
	   }
	   
	   public static ExceptionHandler getInstance(){
	      if(mInstance==null){
	         mInstance = new ExceptionHandler();
	      }
	      return mInstance;
	   }
	   /**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
	    mContext = context;
	}
	   public void register() {
	      java.lang.Thread.UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
	      // don't register again if already registered
	      if (!(currentHandler instanceof UncaughtException)) {
	         // Register default exceptions handler
	    	  UncaughtException   uceh=new UncaughtException(currentHandler);
	          uceh.setContext(mContext);
	         Thread.setDefaultUncaughtExceptionHandler(uceh);
	      }

	   }
	   
	   /**
	    * Submits cached logs to the server and clears them from the device.
	    * 
	    * @param force
	    *           If set to true, will always submit logs regardless of content. Default behavior (false) is to only submit logs if
	    *           the logs contain an exception.
	    */
	   public void submitAllLogs(final Context context,final boolean force,final boolean isDebuggle) {
	      if (true) {
	         new Thread() {
	            public void run() {
	            }
	                 
	         }.start();
	      }
	   }
	   
	  
	   
	    public void clearLog() {
	        if (mDeleteLogsBefore == -1) {
	            mDeleteLogsBefore = System.currentTimeMillis();
	        }
	        
	    }
	   
}
