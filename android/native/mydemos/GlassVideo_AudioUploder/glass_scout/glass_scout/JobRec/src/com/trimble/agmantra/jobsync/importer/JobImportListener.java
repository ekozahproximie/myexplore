package com.trimble.agmantra.jobsync.importer;

public interface JobImportListener {

   public static int IO_EXCEPTION          = 0;

   public void onJobImportSuccess(String stJobFilePath);

   public void onJobImportFailure(String stJobFilePath, int iStatus);

}
