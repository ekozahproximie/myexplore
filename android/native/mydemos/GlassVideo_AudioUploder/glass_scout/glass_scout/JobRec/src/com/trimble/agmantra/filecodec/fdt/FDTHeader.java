package com.trimble.agmantra.filecodec.fdt;

import java.util.Vector;

public class FDTHeader {

   public int                    iTemplateType = 0;
   public Vector<AttributesInfo> vecHeaderVal  = null;
   
   /**
    * 
    * @param vecHeaderVal
    * @param iTemplateType
    */
   public FDTHeader(final Vector<AttributesInfo> vecHeaderVal, final int iTemplateType) {
      this.vecHeaderVal = vecHeaderVal;
      this.iTemplateType = iTemplateType;
   }

   @Override
   public String toString() {
      return iTemplateType+"Result";
   }

}