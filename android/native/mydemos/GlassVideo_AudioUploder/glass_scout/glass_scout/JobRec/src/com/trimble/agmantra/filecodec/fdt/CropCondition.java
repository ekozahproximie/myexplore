package com.trimble.agmantra.filecodec.fdt;

public class CropCondition {

   public String stFlagType    = null;
   public String stFlagName    = null;
   public String stPhoto       = null;
   public String stCrop        = null;
   public String stCondition   = null;
   public String stGrowthstage = null;
   public String stPlantCount  = null;
   public String stNotes       = null;

   /**
    * 
    * @param stFlagType
    * @param stFlagName
    * @param stPhoto
    * @param stCrop
    * @param stSeverity
    * @param stGrowthstage
    * @param iPlantCount
    * @param stNotes
    */
   
   public CropCondition(final String stFlagType, final String stFlagName,
         final String stPhoto, final String stCrop, final String stSeverity,
         final String stGrowthstage, final String stPlantCount,
         final String stNotes) {

      this.stFlagType = stFlagType;
      this.stFlagName = stFlagName;
      this.stPhoto = stPhoto;
      this.stCrop = stCrop;
      this.stCondition = stSeverity;
      this.stGrowthstage = stGrowthstage;
      this.stPlantCount = stPlantCount;
      this.stNotes = stNotes;

   }
   
   
   public CropCondition() {
   }

   /**
    * 
    * Get the arraylist of filed
    * 
    * @return
    */
   public Object[] getArrObj() {
      Object[] obj = new Object[] { stFlagType, stFlagName, stPhoto, stCrop,
            stGrowthstage, stPlantCount, stCondition, stNotes };
      return obj;
   }
   
   @Override
   public String toString() {
      return stFlagType + stFlagName + stPhoto + stCrop + stCondition
            + stGrowthstage + stPlantCount + stNotes;
   }

}
