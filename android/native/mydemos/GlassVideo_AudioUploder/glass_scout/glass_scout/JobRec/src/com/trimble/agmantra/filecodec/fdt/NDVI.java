package com.trimble.agmantra.filecodec.fdt;

public class NDVI {

   public String stFlagType = null;
   public String stFlagName = null;
   public String stCrop = null;
   public String stCropNper = null;
   public String stConvFact = null;
   public String stNDVIRef = null;
   public String stNDVIFp = null;
   public String stNDVISoil = null;
   public String stNUEPer = null;
   public String stMaxYield = null;
   public String stResponseIndex = null;
   public String stNRate = null;
   public String stNRateUnit = null;
   public String stYieldUnit = null;
   
   public String stPhoto    = null;
   
   
   /**
    * 
    * @param stFlagType
    * @param stFlagName
    * @param stPhoto
    * @param stNotes
    */

   public NDVI(final String stFlagType, final String stFlagName,final String stCrop,final String stCropNper,
         final String stConvFact,final String stNDVIRef,final String stNDVIFp,final String stNDVISoil,
         final String stNUEPer,final String stMaxYield,final String stResponseIndex,final String stNRate,
         final String stNRateUnit,final String stYieldUnit,final String stPhoto) {

      this.stFlagType = stFlagType;
      this.stFlagName = stFlagName;
      this.stCrop = stCrop;
      this.stCropNper = stCropNper;
      this.stConvFact = stConvFact;
      this.stNDVIRef = stNDVIRef;
      this.stNDVIFp = stNDVIFp;
      this.stNDVISoil = stNDVISoil;
      this.stNUEPer = stNUEPer;
      this.stMaxYield = stMaxYield;      
      this.stResponseIndex = stResponseIndex;
      this.stNRate = stNRate;      
      this.stNRateUnit = stNRateUnit;     
      this.stYieldUnit = stYieldUnit;
      
      this.stPhoto = stPhoto;      
   }
   
   
   public NDVI() {
   }

   /**
    * 
    * @return
    */
   public Object[] getArrObj() {
      Object[] obj = new Object[] { stFlagType, stFlagName, stCrop,stNDVIRef,
            stNDVIFp,stNDVISoil,stNUEPer,stMaxYield,stResponseIndex,stNRate,stCropNper,stConvFact,stNRateUnit,stYieldUnit,stPhoto };
      return obj;
   }

   @Override
   public String toString() {
      return stFlagType + stFlagName + stCrop+ stNDVIRef + stNDVIFp + stNDVISoil + stNUEPer + stMaxYield + stResponseIndex + stNRate
              + stCropNper + stConvFact   + stNRateUnit + stYieldUnit +  stPhoto;
   }
}
