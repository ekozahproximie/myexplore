package com.trimble.agmantra.jobsync.jobdownloader;

public interface JobDownloadListener {

   public static int IO_EXCEPTION          = 0;
   public static int TICKET_EXCEPTION      = 1;
   public static int UNKOWN_HOST_EXCEPTION = 2;

   public void onJobDownloadSuccess(String stJobFilePath);

   public void onJobDownloadFailure(String stJobFilePath, int iStatus);

}
