package com.trimble.agmantra.layers;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.utils.IO;

import android.os.Environment;
import com.trimble.agmantra.dbutil.Log;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * FGPPoint.java
 * 
 * @author ssamina Structure for the FGPPoint
 */
public class FGPPoint {

   public int    iPointID      = 0;
   public int    iPassID       = 0;
   public int    iAttrID       = 0;
   public int    iRegionID     = 0;

   public int    iObjectType   = GSObjectType.GSO_NONE;
   public int    iTime         = 0;
   public int    iTime_ms      = 0;
   public byte[] unused1         = null;

   public int    iX            = 0;
   public int    iY            = 0;
   public int    iAlt          = 0;
   public int    iOffset       = 0;

   public int    iOffsetX      = 0;
   public int    iOffsetY      = 0;
   public int    iOffsetAlt    = 0;
   public int    iDistTraveled = 0; // (Distance travelled since last point?)

   public int    iSpeed        = 0;
   public int    iHeading      = 0;
   public int    iQuality      = 0;
   public int    iHDOP         = 0;

   public int    iNo_Sat       = 0;
   public int    iMarkers      = 0;
   public int    iBooms        = 0;
   public byte[] unused4         = null;
   public byte   checksum      = 0;

   public FGPPoint() {
      unused1 = new byte[4];
      unused4 = new byte[3];

   }
   
   public CPoint getoffsetCoordinates()
   {
	   if((iOffsetX == 0) && (iOffsetY == 0))
	   {
		   return new CPoint(iX, iY);
	   }
	   else
	   {
		   return new CPoint(iOffsetX, iOffsetY);		   
	   }
   }

   /**
    * All the raw data for this structure
    * 
    * @return
    */
   public byte[] getRawData() {
      // int iSize=22*(Integer.SIZE/Byte.SIZE) +(8*(Byte.SIZE/Byte.SIZE));
      int iSize = getSize();
      int iOffsetVal = 0;
      byte bData[] = new byte[iSize];

      iOffsetVal = IO.put4(bData, iOffsetVal, iPointID);
      iOffsetVal = IO.put4(bData, iOffsetVal, iPassID);
      iOffsetVal = IO.put4(bData, iOffsetVal, iAttrID);
      iOffsetVal = IO.put4(bData, iOffsetVal, iRegionID);

      iOffsetVal = IO.put4(bData, iOffsetVal, iObjectType);
      iOffsetVal = IO.put4(bData, iOffsetVal, iTime);
      iOffsetVal = IO.put4(bData, iOffsetVal, iTime_ms);

      if (unused1 != null) {
         System.arraycopy(unused1, 0, bData, iOffsetVal, unused1.length);
         iOffsetVal += unused1.length;
      }

      iOffsetVal = IO.put4(bData, iOffsetVal, iX);
      iOffsetVal = IO.put4(bData, iOffsetVal, iY);
      iOffsetVal = IO.put4(bData, iOffsetVal, iAlt);
      iOffsetVal = IO.put4(bData, iOffsetVal, iOffset);

      iOffsetVal = IO.put4(bData, iOffsetVal, iOffsetX);
      iOffsetVal = IO.put4(bData, iOffsetVal, iOffsetY);
      iOffsetVal = IO.put4(bData, iOffsetVal, iOffsetAlt);
      iOffsetVal = IO.put4(bData, iOffsetVal, iDistTraveled);

      iOffsetVal = IO.put4(bData, iOffsetVal, iSpeed);
      iOffsetVal = IO.put4(bData, iOffsetVal, iHeading);
      iOffsetVal = IO.put4(bData, iOffsetVal, iQuality);
      iOffsetVal = IO.put4(bData, iOffsetVal, iHDOP);

      iOffsetVal = IO.put4(bData, iOffsetVal, iNo_Sat);
      iOffsetVal = IO.put4(bData, iOffsetVal, iMarkers);
      iOffsetVal = IO.put4(bData, iOffsetVal, iBooms);

      if (unused4 != null) {
         System.arraycopy(unused4, 0, bData, iOffsetVal, unused4.length);
         iOffsetVal += unused4.length;
      }
      checksum = (byte) calculateCheckSUM(bData);
      iOffsetVal = IO.put1(bData, iOffsetVal, checksum);

      return bData;
   }

   /**
    * 
    * @param bData
    * @return
    */
   public int calculateCheckSUM(byte[] bData) {
      int iCheckSum = 0x55;

      if (bData != null) {
         for (int i = 0; i < bData.length - 1; i++) {
            iCheckSum = iCheckSum ^ bData[i];
         }
      }
      return iCheckSum;
   }

   /**
    * 
    * @param outputStream
    * @return
    */
   public boolean writeData(DataOutputStream outputStream,String[] stStatus) {
      boolean isWrite = false;
      if (outputStream != null) {
         try {
            outputStream.write(getRawData());
            outputStream.flush();
            isWrite = true;
         } catch (FileNotFoundException e) {
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
            }else{             
                  stStatus[0] = Constants.SDCARD_NO_SPACE;               
               e.printStackTrace();
            }          
            return false;
         } catch (IOException e) {
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
               Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
            }else{
               stStatus[0] = Constants.SDCARD_NO_SPACE;
               e.printStackTrace();
            }          
            return false;
         }
      }
      return isWrite;
   }

   /**
    * Get size of the class
    * 
    * @return
    */
   public static int getSize() {
      int iSize = 22 * (Integer.SIZE / Byte.SIZE)
            + (8 * (Byte.SIZE / Byte.SIZE));
      return iSize;
   }
   
   @Override
   public String toString() {

      return "FGP Attribs -->\n" + iPointID + "," + iPassID + "," + iAttrID
            + "," + iRegionID + "," +

            iObjectType + "," + iTime + "," + iTime_ms + "," + unused1
            + "," +

            iX + "," + iY + "," + iAlt + "," + iOffset + "," +

            iOffsetX + "," + iOffsetY + "," + iOffsetAlt + ","
            + iDistTraveled + "," +

            iSpeed + "," + iHeading + "," + iQuality + "," + iHDOP
            + "," +

            iNo_Sat + "," + iMarkers + "," + iBooms + "," + unused4 + ","
            + (int)checksum + ",";
   }
}
