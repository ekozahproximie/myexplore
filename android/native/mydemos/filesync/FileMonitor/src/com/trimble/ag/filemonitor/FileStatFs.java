/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.ag.filemonitor
 *
 * File name:
 *	    FileStatFs.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Sep 30, 201411:53:09 AM
 *
 * Abstract:
 *
 *
 * Environment:
 *  Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.trimble.ag.filemonitor;

import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;


/**
 * @author sprabhu
 *
 */
public class FileStatFs {

   private static final String LOG=FileStatFs.class.getSimpleName();
   /**
    * 
    */
   private FileStatFs() {
    
   }
   /*************************************************************************************************
   Returns size in bytes.

   If you need calculate external memory, change this: 
       StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
   to this: 
       StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());        
   **************************************************************************************************/
   
   private static boolean isFileExists(final String stFilePath) {
      return (stFilePath != null) && new File(stFilePath).exists();
   }

   public static long totalMemory(final String stFilePath) {
      if (!isFileExists(stFilePath)) {
         Log.e(LOG, "File not exist"+stFilePath);
         return -1;
      }
      final StatFs statFs = new StatFs(stFilePath);
      long total = ( (long)statFs.getBlockCount() *  (long)statFs.getAvailableBlocks());
      return total;
   }

   public static long freeMemory(final String stFilePath) {
      if (!isFileExists(stFilePath)) {
         Log.e(LOG, "File not exist"+stFilePath);
         return -1;
      }
      final StatFs statFs = new StatFs(stFilePath);
      long free = ( (long)statFs.getAvailableBlocks() *  (long)statFs.getBlockSize());
      return free;
   }

   public static long busyMemory(final String stFilePath) {
      if (!isFileExists(stFilePath)) {
         Log.e(LOG, "File not exist"+stFilePath);
         return -1;
      }
      final StatFs statFs = new StatFs(stFilePath);
      long total = (statFs.getBlockCount() * statFs.getBlockSize());
      long free = ( (long)statFs.getAvailableBlocks() *  (long)statFs.getBlockSize());
      long busy = total - free;
      return busy;
   }
   
   private static String floatForm (double d)
   {
      return new DecimalFormat("#.##").format(d);
   }


   public static String bytesToHuman (long size)
   {
       long Kb = 1  * 1024;
       long Mb = Kb * 1024;
       long Gb = Mb * 1024;
       long Tb = Gb * 1024;
       long Pb = Tb * 1024;
       long Eb = Pb * 1024;

       if (size <  Kb)                 return floatForm(        size     ) + " byte";
       if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " Kb";
       if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " Mb";
       if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " Gb";
       if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " Tb";
       if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " Pb";
       if (size >= Eb)                 return floatForm((double)size / Eb) + " Eb";

       return "???";
   }
}
