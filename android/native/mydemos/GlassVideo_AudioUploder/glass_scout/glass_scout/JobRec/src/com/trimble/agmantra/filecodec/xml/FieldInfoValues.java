package com.trimble.agmantra.filecodec.xml;

public class FieldInfoValues {

   /*
    * contains the project id - 64 bit hex value
    */
   public String sPrjID             = "";

   /*
    * contains the Client ID to which the farm belongs
    */
   public String sClientID          = "";

   /*
    * contains the Client description to which the farm belongs
    */
   public String sClientDesc        = "";

   /*
    * contains the Farm ID to which the field belongs
    */
   public String sFarmID            = "";

   /*
    * contains the Farm description to which the field belongs
    */
   public String sFarmDesc          = "'";

   /*
    * contains the Farm area to which the field belongs
    */
   public float  fFarmArea          = 0;

   /*
    * contains the Farm area's unit
    */
   public String sFarmAreaUnitID    = "";

   /*
    * contains the Farm area's unit description
    */
   public String sFarmAreaUnitDesc  = "";

   /*
    * contains the field ID to which the boundary belongs
    */
   public String sFieldID           = "";

   /*
    * contains the field description to which the boundary belongs
    */
   public String sFieldDesc         = "";

   /*
    * contains the field area to which the boundary belongs
    */
   public float  fFieldArea         = 0;

   /*
    * contains the field area's unit
    */
   public String sFieldAreaUnitID   = "";

   /*
    * contains the field area's unit description
    */
   public String sFieldAreaUnitDesc = "";

   /*
    * contains the field boundary's modified version
    */
   public int    nFieldBdryModified = 1;

   /*
    * contains the field boundary's revision version
    */
   public int    nFieldBdryRevision = 1;

   public FieldInfoValues() {
      /*
       * sPrjID = ""; // client sClientID = ""; sClientDesc = ""; // farm
       * sFarmID = ""; sFarmDesc = ""; fFarmArea = 0.0f; sFarmAreaUnitID = "";
       * sFarmAreaUnitDesc = ""; // field sFieldID = ""; sFieldDesc = "";
       * fFieldArea = 0.0f; sFieldAreaUnitID = ""; sFieldAreaUnitDesc = ""; //
       * boundary versions nFieldBdryModified = 0; nFieldBdryRevision = 0;
       */
   }

   /*
    * set client attributes and proj id
    */
   public void setClientAttribs(String ProjID, String ClientID,
         String ClientDesc) {
      sPrjID = ProjID;
      sClientID = ClientID;
      sClientDesc = ClientDesc;
   }

   /*
    * set farm ID and its attributes
    */
   public void setFarmAttribs(String FarmID, String FarmDesc,
         String FarmAreaUnitID, String FarmAreaUnitDesc, float fArea) {
      sFarmID = FarmID;
      sFarmDesc = FarmDesc;
      sFarmAreaUnitID = FarmAreaUnitID;
      sFarmAreaUnitDesc = FarmAreaUnitDesc;
      fFarmArea = fArea;
   }

   /*
    * set field ID and its attributes
    */
   public void setFieldAttribs(String FieldID, String FieldDesc,
         String FieldAreaUnitID, String FieldAreaUnitDesc, float fArea) {
      sFieldID = FieldID;
      sFieldDesc = FieldDesc;
      sFieldAreaUnitID = FieldAreaUnitID;
      sFieldAreaUnitDesc = FieldAreaUnitDesc;
      fFieldArea = fArea;
   }

   /*
    * set field boundary versions
    */
   public void setFieldBdryVersions(int nBdryModified, int nBdryRevision) {
      nFieldBdryModified = nBdryModified;
      nFieldBdryRevision = nBdryRevision;
   }

}
