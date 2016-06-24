package com.trimble.agmantra.jobencoder;

import android.content.Context;



public class JobEncoderProcess implements Runnable {

   private Thread                    thread                 = null;

   private long                      lJobID                 = -1;

   private boolean                   isAsynMode             = false;

   private JobEncoderProcessListener encoderProcessListener = null;

   private static final String       THREAD_NAME            = "JEProcee ";

   private Context                   context                = null;

   public JobEncoderProcess(long lJobID, boolean isAsyn,
         JobEncoderProcessListener encoderProcessListener, Context context) {
      this.lJobID = lJobID;
      this.isAsynMode = isAsyn;
      this.encoderProcessListener = encoderProcessListener;
      this.context = context;
   }

   public void startEncode() {
      if (isAsynMode) {
         thread = new Thread(this);
         thread.setName(THREAD_NAME + lJobID);
         thread.start();
      } else {
         doEncode();
      }
   }

   public long getlJobID() {
      return lJobID;
   }
   @Override
   public void run() {
      if (!Thread.interrupted()) {
         doEncode();
      }

   }

   private void doEncode() {

      JobEncoder encoder = JobEncoder.getInstance(context);
      
      String[] stReturn = {""};
      
      boolean isWriteSucess = encoder.generateJobFiles(lJobID,stReturn);
      if (isWriteSucess) {
         if (encoderProcessListener != null) {
            encoderProcessListener.jobEncodeComplete(lJobID);
         }
      } else {
         if (encoderProcessListener != null) {
            encoderProcessListener.jobEncodeFailer(lJobID,
                  JobEncoderProcessListener.IO_EXCEPTION,stReturn[0]);
         }
      }

   }

   public void stopEncode() {
      if (thread != null) {
      thread.interrupt();
      }
   }
}
