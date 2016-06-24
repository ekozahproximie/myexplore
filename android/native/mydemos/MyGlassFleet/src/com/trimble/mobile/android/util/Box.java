package com.trimble.mobile.android.util;


/*
 * Mercator meter value box
 * 
 */
public class Box {

  public  int x0, y0, x1, y1;
  
    public Box() {
        x0 = Integer.MAX_VALUE;
        y0 = Integer.MAX_VALUE;

        x1 = Integer.MIN_VALUE;
        y1 = Integer.MIN_VALUE;
    }

    public Box(int x00, int y00, int x11, int y11) {
        x0 = x00;
        y0 = y00;

        x1 = x11;
        y1 = y11;
    }

    @Override
    public String toString() {
        return "(" + y0/10E5 + "," + x0/10E5 + "," + y1/10E5 + "," + x1/10E5 +
                ", x extent = " + xExtent() +
                ", y extent = " + yExtent() +
                ")";
    }

    public int stretch(int x, int y) {
        int c = 0;

        if (x < x0) {
            c = Math.abs(x - x0);
             x0 = x;
        }
        if (x > x1) {
            c = Math.abs(x - x1);
            x1 = x;
        }

        if (y < y0) {
            c = Math.abs(y - y0);
            y0 = y;
        }
        if (y > y1) {
            c = Math.abs(y - y1);
            y1 = y;
        }

        return c;
    }

    public void stretch(Box b) {
        stretch(b.x0, b.y0);
        stretch(b.x1, b.y1);
    }

    public  boolean overlap(Box outerBBox) {

       //First bounding box, top left corner, bottom right corner
      int  ATLx = x0;
       int ATLy= y0;
       int ABRx = x1;
       int ABRy = y1;

       //Second bounding box, top left corner, bottom right corner
       int BTLx = outerBBox.x0;
       int BTLy = outerBBox.y0;
       int BBRx = outerBBox.x1;
       int BBRy = outerBBox.y1;

       int rabx = Math.abs(ATLx + ABRx - BTLx - BBRx);
       int raby = Math.abs(ATLy + ABRy - BTLy - BBRy);

       //rAx + rBx
       int raxPrbx = ABRx - ATLx + BBRx - BTLx;

       //rAy + rBy
       int rayPrby = ATLy - ABRy + BTLy - BBRy;

       if(rabx <= raxPrbx && raby <= rayPrby)
       {
                       return true;
       }
       return false;
    }

    public boolean overlap(int x, int y) {
        if (x < x0) {
            return false;
        }
        if (x > x1) {
            return false;
        }
        if (y > y0) {
            return false;
        }
        if (y < y1) {
            return false;
        }
        return true;
    }
   
    double convertMinX() {
        return  Double.valueOf(x0) / 10E5 ;
    }

    double convertMaxX() {
        return Double.valueOf(x1) / 10E5 ;
    }

    double convertMinY() {
        return Double.valueOf(y0)/ 10E5 ;
    }

    double convertMaxY() {
        return Double.valueOf(y1) / 10E5;
    }

    int xExtent() {
        return x1 - x0;
    }

    int yExtent() {
        return y1 - y0;
    }

    int centerX() {
        return (x0 + x1) / 2;
    }

    int centerY() {
        return (y0 + y1) / 2;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if( getClass() != obj.getClass()){
            return false;
        }
        final Box otherBox=(Box)obj;
        if( x0 != otherBox.x0 || x1 != otherBox.x1 || y0 != otherBox.y0 || y1 != otherBox.y1 ){
            return  false;
        }
    return  true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.x0;
        hash = 83 * hash + this.y0;
        hash = 83 * hash + this.x1;
        hash = 83 * hash + this.y1;
        return hash;
    }
    public boolean dOverLap(Box compareBox){
        if (convertMinX() > compareBox.convertMaxX()) {
            return false;
        }
        // compareBox Start X is lessthen base box end X1
        if (compareBox.convertMinX() > convertMaxX()) {
            return false;
        }
         // start Y is lessthen  compareBox end Y1  
        if (convertMinY() > compareBox.convertMaxY()) {
            return false;
        }
        // compareBox Start Y is lessthen base box end Y1
        if (compareBox.convertMinY() > convertMaxY()) {
            return false;
        }
        return true;
    }
   
     public String latlonBox(){
         return convertMinX()+","+convertMinY()+" "+convertMaxX()+","+convertMaxY();
     }
     
     
     
     
}
