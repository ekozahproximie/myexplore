package com.trimble.agmantra.filecodec.fdt;

public class AttributesInfo {

   public static final int ATT_TYPE_UNKNOWN     = 0;
   public static final int ATT_TYPE_NUMBER      = 1;
   public static final int ATT_TYPE_FLOAT       = 2;
   public static final int ATT_TYPE_DATA_STRING = 3;
   public static final int ATT_TYPE_DATE        = 4;

   // public char stName[] = new char[11];
   public String           stName               = "";
   public int              iAttFieldType        = ATT_TYPE_UNKNOWN;
   public String           stDefaultValue       = "";
   public int              iDecimalCount        = 1;
   public int              iFieldLength         = 0;
   public String           stFlagType           = "";

   public AttributesInfo(final String stName, final int iAttFieldType,
         final String stDefaultValue, final int iDecimalCount,
         String stFlagType, int iLength) {
      this.stName = stName;
      this.iAttFieldType = iAttFieldType;
      this.stDefaultValue = stDefaultValue;
      this.iDecimalCount = iDecimalCount;
      this.iFieldLength = iLength;
      this.stFlagType = stFlagType;

   }

   @Override
   public String toString() {
      return "("+stName +","+ iAttFieldType +","+stDefaultValue +","+iDecimalCount+","+stFlagType+","+iFieldLength+")";
   }
}
