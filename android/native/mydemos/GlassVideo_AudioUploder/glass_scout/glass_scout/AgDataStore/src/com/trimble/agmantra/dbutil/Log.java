// Log.java

package com.trimble.agmantra.dbutil;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
 * Log Message Geneartion and write Into File
 */

public class Log {

    // Logger Enable or disable
    public static final boolean IS_LOG_ENABLE = true;

    // dump of log msg Enable or disable
    public static final boolean IS_FILE_LOG_ENABLE = false;

    // Tag Name
    public final static String TAG = "CONNECTED_FARM_LOG";

    private static final String ST_SPACE = "   ";

    private static final String ST_DOUBLE_UNDERSCORE = "--";

    // LOGGER MSG DETAILS
    private static final String DEBUG_MSG = "--DEBUG-- ";

    private static final String VERBOSE_MSG = "--VERBOSE--";

    private static final String INFO_MSG = "--INFO--";

    private static final String ERROR_MSG = "--ERROR--";

    private static final String WARNING_MSG = "--WARNING--";

    private final static String LOG_DIR_NAME = "AgLog";

    private final static String LOG_FILE_NAME = "AgLog.txt";

    private final static String AG_FOLDERNAME = "AgMantra";
    
    private final static int FILE_SIZE = 5 * 1024 * 1024;

    // File name and path MainMenu.java construct its name
    private static File logFile = null;

    private static File logDir = null;

    public static String getStoreRoot() {

        File SDCARD_PATH = Environment.getExternalStorageDirectory();

        String AG_FLAG_STORAGE = SDCARD_PATH.getAbsolutePath() + File.separator + AG_FOLDERNAME;

        return AG_FLAG_STORAGE;
    }

    // Check the directory for logfile
    private static void checkLogDirectory() {        try {

            logDir = new File(getStoreRoot() + File.separator + LOG_DIR_NAME);

            if (!logDir.exists()) {
                logDir.mkdirs();
                logFile = new File(logDir.getAbsolutePath());
                logFile.createNewFile();

            } else {
                logFile = new File(logDir.getAbsolutePath() + File.separator + LOG_FILE_NAME);
               
                if(logFile.length()>FILE_SIZE){
                   logFile.delete();
                   logFile.createNewFile();
                }
                
                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get current time for Log format
    private static String getDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat formatter = (SimpleDateFormat)DateFormat.getDateTimeInstance(
                DateFormat.LONG, DateFormat.LONG, Locale.US);
        formatter.applyPattern("yyyy-MM-dd   h:mm:ss a z");
        String dateFormat = formatter.format(date);
        return dateFormat;
    }

    private static String getMsgFormat(String tag, String msg, String stLevel) {
        StringBuffer logmsg = new StringBuffer();
        logmsg.append(getDate()).append(ST_SPACE).append(stLevel).append(tag)
                .append(ST_DOUBLE_UNDERSCORE).append(msg);
        return logmsg.toString();
    }

    private static String getMsgFormat(String tag, String msg, String stLevel, Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        StringBuffer logmsg = new StringBuffer();
        logmsg.append(getDate()).append(ST_SPACE).append(stLevel).append(tag)
                .append(ST_DOUBLE_UNDERSCORE).append(msg).append(result.toString());
        return logmsg.toString();
    }

    // Write the log into file
    private static void fileWriter( String tag, String msg, String stLevel) {
        if (IS_FILE_LOG_ENABLE) {
            if (isSDCardMount()) {
                String stLogMsg = getMsgFormat(tag, msg, stLevel);
                checkLogDirectory();
                FileWriter logwriter = null;
                BufferedWriter osw = null;
                try {
                    logwriter = new FileWriter(logFile.getAbsolutePath(), true);
                    osw = new BufferedWriter(logwriter);
                    osw.write(stLogMsg);
                    osw.write("\r\n"); // new Line
                    osw.flush();

                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (osw != null) {
                        try {
                            osw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (logwriter != null) {
                        try {
                            logwriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    // Write the log into file
    private static void fileWriter( String tag, String msg, String stLevel,
            Throwable throwable) {
        if (IS_FILE_LOG_ENABLE) {
            if (isSDCardMount()) {
                String stLogMsg = getMsgFormat(tag, msg, stLevel, throwable);
                checkLogDirectory();
                
                FileWriter logwriter = null;
                BufferedWriter osw = null;
                try {
                    logwriter = new FileWriter(logFile.getAbsolutePath(), true);
                    osw = new BufferedWriter(logwriter);
                    osw.write(stLogMsg);
                    osw.write("\r\n"); // new Line
                    osw.flush();

                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (osw != null) {
                        try {
                            osw.close();
                            osw = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (logwriter != null) {
                        try {
                            logwriter.close();
                            logwriter = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    public static void d(String tag, String msg) {
        if(IS_LOG_ENABLE)
        android.util.Log.d(tag, msg);
        fileWriter(tag, msg, DEBUG_MSG);

    }

    public static void i(String tag, String msg) {
        if(IS_LOG_ENABLE)
        android.util.Log.i(tag, msg);
        fileWriter(tag, msg, INFO_MSG);
    }

    public static void e(String tag, String msg, Error e) {
        if(IS_LOG_ENABLE)
        android.util.Log.e(tag, msg,e);
        fileWriter(tag, msg, ERROR_MSG);
    }

    public static void e(String tag, String msg, Exception e) {
        if(IS_LOG_ENABLE)
        android.util.Log.e(tag, msg,e);
        fileWriter(tag, msg, ERROR_MSG, e);

    }

    public static void e(String tag, String msg) {
        if(IS_LOG_ENABLE)
        android.util.Log.e(tag, msg);
        fileWriter(tag, msg, ERROR_MSG);
    }

    public static void v(String tag, String msg) {
        if(IS_LOG_ENABLE)
        android.util.Log.v(tag, msg);
        fileWriter(tag, msg, VERBOSE_MSG);
    }

    public static void w(String tag, String msg) {
        if(IS_LOG_ENABLE)
        android.util.Log.w(tag, msg);
        fileWriter(tag, msg, WARNING_MSG);
    }

    public static void w(String tag, String msg, Exception e) {
        if(IS_LOG_ENABLE)
        android.util.Log.w(tag, msg,e);
        fileWriter(tag, msg, WARNING_MSG, e);
    }

    public static boolean isSDCardMount() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
