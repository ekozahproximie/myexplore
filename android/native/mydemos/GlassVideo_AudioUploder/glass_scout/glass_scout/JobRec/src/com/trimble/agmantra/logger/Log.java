package com.trimble.agmantra.logger;

public class Log {

   public static Log       logger           = null;

   public static final int LOG_TYPE_DEBUG   = 0;
   public static final int LOG_TYPE_INFO    = 1;
   public static final int LOG_TYPE_ERROR   = 2;
   public static final int LOG_TYPE_VERBOSE = 3;
   public static final int LOG_TYPE_WARNING = 4;


   public static void printLog(boolean isLogger, int iType, String stTag,
         String stMsg) {

      if (isLogger) {

         switch (iType) {
            case LOG_TYPE_DEBUG:
               android.util.Log.d(stTag, stMsg);
               break;
            case LOG_TYPE_INFO:
               android.util.Log.i(stTag, stMsg);
               break;
            case LOG_TYPE_ERROR:
               android.util.Log.e(stTag, stMsg);
               break;
            case LOG_TYPE_VERBOSE:
               android.util.Log.v(stTag, stMsg);
               break;
            case LOG_TYPE_WARNING:
               android.util.Log.w(stTag, stMsg);
               break;

            default:
               break;
         }
      }

   }

}
