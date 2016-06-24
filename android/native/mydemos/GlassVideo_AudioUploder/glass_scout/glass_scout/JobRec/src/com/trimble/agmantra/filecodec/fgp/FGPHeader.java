package com.trimble.agmantra.filecodec.fgp;

import com.trimble.agmantra.constant.Constants;
import com.trimble.agmantra.utils.IO;

import android.os.Environment;
import com.trimble.agmantra.dbutil.Log;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FGPHeader {

   public byte[] bufFileType = null;
   public int    iVersion    = 0;
   public int    iFieldID    = 0;
   public int    iDate       = 0;

   /**
    * 
    * @param mFiletype
    * @param iVersion
    * @param iId
    * @param iDate
    */
   public FGPHeader(int iVersion, long iId, int iDate) {
      bufFileType = new byte[] { 'F', 'G', 'P', '\0' };
      // m_FileType = mFiletype;
      this.iVersion = iVersion;
      this.iFieldID = (int) iId;
      this.iDate = iDate;
   }

   /**
    * 
    * @param outputStream
    * @return
    */
   public boolean writeData(DataOutputStream outputStream) {
      boolean isWrite = false;
      try {
         if (outputStream == null) {
            return isWrite;
         }
         // int iSize = (4 * Integer.SIZE / Byte.SIZE);
         int iSize = getSize();

         int iOffset = 0;
         byte bData[] = new byte[iSize];
         if (bufFileType != null) {
            System.arraycopy(bufFileType, 0, bData, iOffset, bufFileType.length);
            iOffset += bufFileType.length;
         }
         iOffset = IO.put4(bData, iOffset, iVersion);
         iOffset = IO.put4(bData, iOffset, iFieldID);
         iOffset = IO.put4(bData, iOffset, iDate);

         outputStream.write(bData);
         outputStream.flush();
         isWrite = true;
      } catch (FileNotFoundException e) {
         if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
         }else{
            e.printStackTrace();
         }          
         return false;
      } catch (IOException e) {
         if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.i(Constants.TAG_JOB_ENCODER, "SDcard Unmounted");
         }else{
            e.printStackTrace();
         }          
         return false;
      }
      return isWrite;

   }

   /**
    * Get the size of this class
    * 
    * @return
    */
   public static int getSize() {
      int iSize = (4 * Integer.SIZE / Byte.SIZE);
      return iSize;
   }
}
