package com.trimble.agmantra.filecodec;

public class ShapeGpeAttribs {

   public double dDatum  = 0;
   public double dSystem = 0;
   public double dZone   = 0;
   
   /**
    * 
    * @param dDatum
    * @param dSystem
    * @param dZone
    */
   public ShapeGpeAttribs(double dDatum, double dSystem, double dZone) {
      this.dDatum = dDatum;
      this.dSystem = dSystem;
      this.dZone = dZone;
   }

}
