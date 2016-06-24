package com.trimble.agmantra.filecodec.fdt;

public class Other {

   public String stFlagType = null;
   public String stFlagName = null;
   public String stPhoto    = null;
   public String stNotes    = null;
   
   /**
    * 
    * @param stFlagType
    * @param stFlagName
    * @param stPhoto
    * @param stNotes
    */

   public Other(final String stFlagType, final String stFlagName,
         final String stPhoto, final String stNotes) {

      this.stFlagType = stFlagType;
      this.stFlagName = stFlagName;
      this.stPhoto = stPhoto;
      this.stNotes = stNotes;

   }
   
   
   public Other() {
   }

   /**
    * 
    * @return
    */
   public Object[] getArrObj() {
      Object[] obj = new Object[] { stFlagType, stFlagName, stPhoto, stNotes };
      return obj;
   }

   @Override
   public String toString() {
      return stFlagType + stFlagName + stPhoto + stNotes;
   }
}
