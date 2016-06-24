package com.trimble.agmantra.jobencoder;


public interface JobEncoderProcessListener {

   public static final int IO_EXCEPTION     = 0;
   public static final int SD_CARD_REMOVED  = 1;
   public static final int SD_CARD_NO_SPACE = 2;


   public void jobEncodeComplete(long lJobID);

   public void jobEncodeFailer(long lJobid, int iStatus,String stStatus);
}
