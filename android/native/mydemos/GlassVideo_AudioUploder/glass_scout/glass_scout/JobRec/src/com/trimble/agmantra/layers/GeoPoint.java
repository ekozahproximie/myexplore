package com.trimble.agmantra.layers;

public class GeoPoint {

   /** The x. */
   public double iX;

   /** The y. */
   public double iY;

   /** The offset x. */
   public double iOffsetX;

   /** The offset y. */
   public double iOffsetY;

   public GeoPoint(double X, double Y, double offsetX, double offsetY) {
      this.iX = X;
      this.iY = Y;
      this.iOffsetX = offsetX;
      this.iOffsetY = offsetY;

   }

   public GeoPoint(double X, double Y) {
      this.iX = X;
      this.iY = Y;
      this.iOffsetX = 0;
      this.iOffsetY = 0;

   }

}
