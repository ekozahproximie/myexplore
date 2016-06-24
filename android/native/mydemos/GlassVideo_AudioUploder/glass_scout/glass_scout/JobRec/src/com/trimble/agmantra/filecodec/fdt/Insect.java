package com.trimble.agmantra.filecodec.fdt;

public class Insect {

   public String stFlagType = null;
   public String stFlagName = null;
   public String stPhoto    = null;
   public String stDetails  = null;
   public String stSeverity = null;
   public String stNotes    = null;

   /**
    * 
    * @param stFlagType
    * @param stFlagName
    * @param stPhoto
    * @param stDetails
    * @param stSeverity
    * @param stNotes
    */
   public Insect(final String stFlagType, final String stFlagName, final String stPhoto,
         final String stDetails, final String stSeverity, final String stNotes) {

      this.stFlagType = stFlagType;
      this.stFlagName = stFlagName;
      this.stPhoto = stPhoto;
      this.stDetails = stDetails;
      this.stSeverity = stSeverity;
      this.stNotes = stNotes;

   }
   
  
   public Insect() {

   }

   /**
    * 
    * @return
    */
   public Object[] getArrObj() {
      Object[] obj = new Object[] { stFlagType, stFlagName, stPhoto, stDetails,
            stSeverity, stNotes };
      return obj;
   }

   @Override
   public String toString() {
      return stFlagType + stFlagName + stPhoto + stDetails + stSeverity
            + stNotes;
   }

}
