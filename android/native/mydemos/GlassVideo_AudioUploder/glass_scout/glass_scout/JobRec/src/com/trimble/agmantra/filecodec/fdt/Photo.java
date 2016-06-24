package com.trimble.agmantra.filecodec.fdt;

public class Photo {

   public String stFlagType = null;
   public String stImgName  = null;

   public Photo() {

   }

   /**
    * 
    * @return
    */
   public Object[] getArrObj() {
      Object[] obj = new Object[] { stFlagType, stImgName };
      return obj;
   }

   @Override
   public String toString() {
      return stImgName;
   }

}
