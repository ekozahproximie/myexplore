package com.trimble.agmantra.filecodec;

import com.trimble.agmantra.constant.Constants;

import android.os.Environment;
import com.trimble.agmantra.dbutil.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class AgTxtCodec {

   public static final int     TYPE_JOB_GPE   = 1;
   public static final int     TYPE_SHAPE_GPE = 2;
   public static final int     TYPE_LEGEND    = 3;

   // Shape GPE
   private static final String SHP_START      = "[CoordSys]";
   private static final String DATUM          = "Datum=";
   private static final String SYSTEM         = "System=";
   private static final String ZONE           = "Zone=";

   // JOB GPE
   private static final String JOB_START      = "[Options]";
   private static final String BOOM_OFFSET    = "BoomOffsets=";
   private static final String BOOM_TYPE      = "BoomType=";

   private String              stFilePath     = "";
   private String              stFileExten    = "";

   private int                 iType          = 0;
   private Object              txtObj         = null;

   public AgTxtCodec(String stFilePath, String stFileExten, int iType,
         Object obj) {

      this.stFilePath = stFilePath;
      this.stFileExten = stFileExten;
      this.iType = iType;
      switch (iType) {
         case TYPE_JOB_GPE:
            if (txtObj instanceof JobGpeAttribs) {
               throw new IllegalArgumentException("");
            }
            break;
         case TYPE_SHAPE_GPE:
            if (txtObj instanceof ShapeGpeAttribs) {
               throw new IllegalArgumentException("");
            }
            break;
         default:
            break;
      }
      this.txtObj = obj;
   }

   public boolean writeFile(String[] stStatus) {
      boolean isSuccess = false;

      File file = null;
      BufferedWriter bufWrite = null;
      FileWriter fileWriter = null;
      if (!(stFilePath.equals("") && stFileExten.equals(""))) {
         file = new File(stFilePath + stFileExten);
      }

      if (file == null) {
         return isSuccess;
      }

      try {
         fileWriter = new FileWriter(file);
         bufWrite = new BufferedWriter(fileWriter);

         switch (iType) {
            case TYPE_JOB_GPE:
               if (txtObj == null) {
                  return isSuccess;
               }
               JobGpeAttribs jobAttrib = (JobGpeAttribs) txtObj;
               bufWrite.write(JOB_START);
               bufWrite.write(Constants.ST_NEWLINE);
               bufWrite.write(BOOM_OFFSET + jobAttrib.iBoomOffsets);
               bufWrite.write(Constants.ST_NEWLINE);
               bufWrite.write(BOOM_TYPE + jobAttrib.iBoomType);

               break;

            case TYPE_SHAPE_GPE:
               if (txtObj == null) {
                  return isSuccess;
               }
               ShapeGpeAttribs shpAttrib = (ShapeGpeAttribs) txtObj;
               bufWrite.write(SHP_START);
               bufWrite.write(Constants.ST_NEWLINE);
               bufWrite.write(DATUM + shpAttrib.dDatum);
               bufWrite.write(Constants.ST_NEWLINE);
               bufWrite.write(SYSTEM + shpAttrib.dSystem);
               bufWrite.write(Constants.ST_NEWLINE);
               bufWrite.write(ZONE + shpAttrib.dZone);

               break;

            case TYPE_LEGEND:

               bufWrite.write(Constants.DEFAULT_VAL_LEGEND);

               break;

            default:
               break;
         }

         bufWrite.flush();
         bufWrite.close();
         isSuccess = true;

      } catch (FileNotFoundException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
         } else {

               stStatus[0] = Constants.SDCARD_NO_SPACE;

            e.printStackTrace();
         }
         return false;
      } catch (IOException e) {
         if (!Environment.getExternalStorageState().equals(
               Environment.MEDIA_MOUNTED)) {
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
         } else {
               stStatus[0] = Constants.SDCARD_NO_SPACE;
            e.printStackTrace();
         }
         return false;
      } finally {
         if (fileWriter != null) {
            try {
               fileWriter.close();
            } catch (Exception e2) {

            }

         }
         if (bufWrite != null) {
            try {
               bufWrite.close();
            } catch (Exception e2) {

            }

         }

      }
      return isSuccess;

   }

}
