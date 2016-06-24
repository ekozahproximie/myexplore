package com.trimble.agmantra.jobsync.jobuploader;

public interface JobUploadListener {
   
   public static final int IO_EXCEPTION = 0;
   public static final int TICKET_EXCEPTION = 1;
   
   
   

   public void onJobUploadStatus(int iStatus,long lJobID);
   
   public void onJobUploadSuccess(String stJobFilePath,long lJobID);

   public void onJobUploadFailure(String stJobFilePath,long lJobID,int iStatus);
   
   public void onAllJobUploaded();

}
