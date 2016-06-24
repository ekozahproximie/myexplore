package com.trimble.agmantra.filecodec.fdt;

public class NDVIRef {

   public String stFlagType = null;
   public String stFlagName = null;
   public String stFlatNRef    = null;

   
   /**
    * 
    * @param stFlagType
    * @param stFlagName
    * @param stPhoto
    * @param stNotes
    */

   public NDVIRef(final String stFlagType, final String stFlagName, final String stFlatNRef) {

      this.stFlagType = stFlagType;
      this.stFlagName = stFlagName;
      this.stFlatNRef = stFlatNRef;
   }
   
   
   public NDVIRef() {
   }

   /**
    * 
    * @return
    */
   public Object[] getArrObj() {
      Object[] obj = new Object[] { stFlagType, stFlagName, stFlatNRef};
      return obj;
   }

   @Override
   public String toString() {
      return stFlagType + stFlagName + stFlatNRef;
   }
}
