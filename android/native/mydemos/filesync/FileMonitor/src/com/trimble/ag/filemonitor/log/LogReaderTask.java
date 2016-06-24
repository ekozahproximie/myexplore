package com.trimble.ag.filemonitor.log;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

import com.trimble.ag.filemonitor.utils.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

public final class LogReaderTask {

   public static final String  TAG              = LogReaderTask.class.getSimpleName();

   private final String[]       LOGCAT_CMD       = new String[] { "logcat",
         "-v", "time"                           };

   private final String[]       LOGCAT_CMD_CLEAR = new String[] { "logcat",
         "-c"                                   };

   private final int            BUFFER_SIZE      = 1024;

   private final int            FILE_SIZE        = 3 * 1024 * 1024;     // 3 mb

   private Process              logprocess       = null;

   private boolean              isRunning        = false;

//    public final static String   stLogFilePath             = Environment
//                                                              .getExternalStorageDirectory()
//                                                              + File.separator
//                                                              + "Vilicus"
//                                                              + File.separator
//                                                              + "log"
//                                                              + File.separator;

   private transient String   stLogFilePath = null; 
   
   private final static String   LOG_FILENAME             = "log.txt";

   public final static String   SEND_LOG                = "LogFile.txt";

   private static LogReaderTask logReaderTask    = null;

   private transient Context    context          = null;

   private Thread               thread           = null;
   
   private transient boolean isSdcardReceiverRegistered=false;
   
   private transient SdcardReceiver receiver = null;
   
   private static final String LOGGER_THREAD="connected farm logger thread";

   public static LogReaderTask getInstance(final Context appContext) {
      if (logReaderTask == null) {
         logReaderTask = new LogReaderTask(appContext);
      }
      return logReaderTask;
   }

   private LogReaderTask(final Context appContext) {
      if(logReaderTask != null){
         throw new IllegalAccessError("use getInstance to obtain the object !!!");
      }
      this.context = appContext;
      File file = appContext.getApplicationContext().getExternalCacheDir();
      if( file == null){
         file=Environment.getExternalStorageDirectory(); 
      }
      stLogFilePath = file.getAbsolutePath() + File.separator;
      receiver= new SdcardReceiver(this);
      if(! isSdcardReceiverRegistered){
         try{
         final IntentFilter intentFilter =SdcardReceiver.getSdcardIntentFilter();
         appContext.registerReceiver(receiver, intentFilter);
         android.util.Log.d(TAG, " logReaderTask register sdcard Receiver ");
         isSdcardReceiverRegistered=true;
         }catch(IllegalArgumentException e){
            Log.e(TAG, e.getMessage(),e);
         }
         
      }
      startLogProcess();
   }

   private void clearAllLog() {
      Process logClearProcess = null;
      try {
         logClearProcess = Runtime.getRuntime().exec(LOGCAT_CMD_CLEAR);
      } catch (IOException e) {
         android.util.Log.e(TAG, e.getMessage(), e);
      } finally {
         if (logClearProcess != null) {
            logClearProcess.destroy();
         }
      }
   }

   private void initLogProcess() {

      if (!Utils.isSDCardMount()) {
         android.util.Log.e(TAG, "sdcard not mounted,log file not created ");
         return;
      }

      final File logDir = new File(stLogFilePath);
      
      final File logFile = new File(stLogFilePath + LOG_FILENAME);
      BufferedWriter bufferedWriter = null;
      FileWriter fileWriter = null;
      PrintWriter logFilePrintWriter = null;
      try {

         if (!logDir.exists()) {
            logDir.mkdirs();
            logFile.createNewFile();
         }
         boolean isExist = checkFileSize(logFile);
         if (!isExist) {
            android.util.Log.e(TAG, "error in log creation");
            return;
         }
         fileWriter = new FileWriter(logFile, true);
         bufferedWriter = new BufferedWriter(fileWriter);
         logFilePrintWriter = new PrintWriter(bufferedWriter, true);

         writeLogtoFile(logFile, logFilePrintWriter);

      } catch (FileNotFoundException e) {
         android.util.Log.e(TAG, e.getMessage(), e);
      } catch (IOException e) {
         android.util.Log.e(TAG, e.getMessage(), e);
      }catch (Throwable e) {
         android.util.Log.e(TAG, e.getMessage(), e);
      } finally {
         android.util.Log.e(TAG, " logReaderTask enter closeWriterStream");  
         closeWriterStream(fileWriter);
         closeWriterStream(bufferedWriter);
         closeWriterStream(logFilePrintWriter);
         android.util.Log.e(TAG, " logReaderTask  exit closeWriterStream");

      }
   }

   private void writeLogtoFile(final File logFile,
         PrintWriter logFilePrintWriter) throws IOException {

      InputStreamReader readerStreamReader = null;
      logprocess = Runtime.getRuntime().exec(LOGCAT_CMD);
      if (logprocess == null) {
         android.util.Log.e(TAG, "logprocess is null ");
         return;
      }
      InputStream inputStream = logprocess.getInputStream();
      if (inputStream == null) {
         android.util.Log.e(TAG, "logprocess not obtained inputstream");
         return;
      }
      readerStreamReader = new InputStreamReader(inputStream);
      BufferedReader logProcessDataReader = new BufferedReader(
            readerStreamReader, BUFFER_SIZE);
     // String stLogLine = null;
      try {
         if(isRunning){
         android.util.Log.d(TAG, " logReaderTask sucessfully start log process :"+Environment.getExternalStorageDirectory());
         }else{
            android.util.Log.d(TAG, " logReaderTask not-start log process "+Environment.getExternalStorageState());
         }
         final char data[] =new char[BUFFER_SIZE];
         while (isRunning) {
            if (Thread.interrupted()) {
               android.util.Log.e(TAG, "logger thread interrupted");
               break;
            }
            if (logProcessDataReader == null || (!logProcessDataReader.ready())) {
               continue;
            }

             final int iReadData= logProcessDataReader.read(data);
              //stLogLine= new String(data,0,iReadData);
              boolean isExist = checkFileSize(logFile);
            if (!isExist) {
               android.util.Log.e(TAG, "error in log file truncation");
               break;
            }
            if (logFilePrintWriter != null && iReadData != 0) {
               //logFilePrintWriter.println(stLogLine);
               logFilePrintWriter.write(data, 0, iReadData);
            }
         }
         android.util.Log.d(TAG, " logReaderTask exit the log write black");
      } finally {
         android.util.Log.e(TAG, " logReaderTask  enter close reader stream");
         closeReaderStream(readerStreamReader);
         closeReaderStream(logProcessDataReader);
         android.util.Log.e(TAG, " logReaderTask  exit close reader stream");
      }

   }

   private void closeWriterStream(Writer writer) {
      if (writer != null) {
         try {
            writer.close();
         } catch (IOException e) {
            android.util.Log.e(TAG, e.getMessage(), e);
         }catch (IllegalStateException e) {
            android.util.Log.e(TAG, e.getMessage(), e);
         }
      }
   }

   private void closeReaderStream(Reader reader) {
      if (reader != null) {
         try {
            reader.close();
         } catch (IOException e) {
            android.util.Log.e(TAG, e.getMessage(), e);
         }catch (IllegalStateException e) {
            android.util.Log.e(TAG, e.getMessage(), e);
         }
      }
   }

   private boolean checkFileSize(final File logFile) throws IOException {
      boolean isExist = false;

      // new File(context.getFileStreamPath(LOG_FILENAME).getAbsolutePath());
      if (logFile.exists()) {
         if (logFile.length() > FILE_SIZE) {
            android.util.Log.d(TAG, " log file reached max size");
            boolean isMaxSizeReached = logFile.delete();
            android.util.Log.d(TAG, " file deleted :" + isMaxSizeReached);
            boolean isNewFile = logFile.createNewFile();
            android.util.Log.d(TAG, " after file delete new file create :"
                  + isNewFile);

         }
      } else{
         boolean isNewFile = logFile.createNewFile();
         android.util.Log.d(TAG, "File does not exist. New file created "
               + isNewFile);
      }
      isExist = logFile.exists();

      return isExist;
   }

   private Runnable runnable = new Runnable() {

                                @Override
                                public void run() {
                                   initLogProcess();
                                }
                             };

   protected void startLogProcess() {
      if (!Utils.isSDCardMount()) {
         android.util.Log.e(TAG, "sdcard not mounted log process not started ");
         return;
      }
      isRunning=true;
      thread = new Thread(runnable,LOGGER_THREAD );
     if(! Utils.isThreadRunning(LOGGER_THREAD)){
           thread.start();
     }else{
        android.util.Log.e(TAG, LOGGER_THREAD +" already running...");
     }
   }
   protected void stopLogProcess() {
      isRunning = false;
      android.util.Log.d(TAG, " stopLogProcess ");
      if (thread != null) {
         thread.interrupt();
      }
      if (logprocess != null) {
         logprocess.destroy();
      }

   }
   public void clear() {
      android.util.Log.d(TAG, " logReaderTask clear");
      if(isSdcardReceiverRegistered){
         try{
         context.unregisterReceiver(receiver);
         android.util.Log.d(TAG, " logReaderTask unregister sdcard Receiver ");
         isSdcardReceiverRegistered=false;
         }catch(IllegalArgumentException e){
          Log.e(TAG, e.getMessage(),e);
         }
      }
      stopLogProcess();
      logprocess = null;
      logReaderTask = null;
      context = null;
   }

  

   public boolean copyFile(String stDesFOlderPath, String logFileName,
         String stSource) {

      boolean isSucess = false;
      if (!Utils.isSDCardMount()) {
         return isSucess;
      }

      File logDir = new File(stDesFOlderPath);
      File sendLogFile = new File(stDesFOlderPath + logFileName);
      if (!logDir.exists()) {
         boolean isDirCreated = logDir.mkdirs();
         if (!isDirCreated) {
            android.util.Log.e(TAG, " log directory not created");
            return isSucess;
         }

      }
      if (!sendLogFile.exists()) {
         boolean isLogFileCreated = false;
         try {
            isLogFileCreated = sendLogFile.createNewFile();
         } catch (IOException e) {
            android.util.Log.e(TAG, e.getMessage(), e);
         }
         if (!isLogFileCreated) {
            android.util.Log.e(TAG, " log file not created");
            return isSucess;
         }
      }

      // final File clogFile = new
// File(context.getFileStreamPath(LOG_FILENAME).getAbsolutePath());
      final File currentlogFile = new File(stSource);
      long clogFileLength = currentlogFile.length();
      FileOutputStream fileos = null;
      FileInputStream fileInputStream = null;
      try {
         fileos = new FileOutputStream(sendLogFile);
         fileInputStream = new FileInputStream(currentlogFile);
         // fileInputStream =context.openFileInput(LOG_FILENAME);
         byte bData[] = new byte[4 * 1024];
         int iReadData = 0;

         while (clogFileLength > 0) {
            iReadData = fileInputStream.read(bData);
            fileos.write(bData);
            clogFileLength = clogFileLength - iReadData;
         }
         isSucess = true;
      } catch (IOException e) {

         android.util.Log.e(TAG, e.getMessage(), e);
      } finally {

         if (fileos != null) {
            try {
               fileos.close();
            } catch (IOException e) {
               android.util.Log.e(TAG, e.getMessage(), e);
            }
         }

         if (fileInputStream != null) {
            try {
               fileInputStream.close();
            } catch (IOException e) {
               android.util.Log.e(TAG, e.getMessage(), e);
            }
         }
      }
      return isSucess;

   }

   public File getMailLogFilePath() {

      File logFile = new File(stLogFilePath + SEND_LOG);
      return logFile;
   }
   
   public File getLogFilePath() {

      File logFile = new File(stLogFilePath + LOG_FILENAME);
      return logFile;
   }
   
}
