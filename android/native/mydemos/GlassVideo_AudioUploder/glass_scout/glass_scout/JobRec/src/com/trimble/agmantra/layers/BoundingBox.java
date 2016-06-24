package com.trimble.agmantra.layers;


/*
 * Mercator meter value box
 * 
 */
public class BoundingBox {

  public  int left, top, right, bottom;
  
    public BoundingBox() {
       right = Integer.MIN_VALUE;
        bottom = Integer.MIN_VALUE;

        left = Integer.MAX_VALUE;
        top = Integer.MAX_VALUE;
    }

    public BoundingBox(int x00, int y00, int x11, int y11) {
        left = x00;
        top = y00;

        right = x11;
        bottom = y11;
    }

    @Override
    public String toString() {
        return "(" + top/10E5 + "," + left/10E5 + "," + bottom/10E5 + "," + right/10E5 +
                ", x extent = " + xExtent() +
                ", y extent = " + yExtent() +
                ")";
    }

    public int stretch(int x, int y) {
        int c = 0;

        if (x < left) {
            c = Math.abs(x - left);
             left = x;
        }
        if (x > right) {
            c = Math.abs(x - right);
            right = x;
        }

        if (y < top) {
            c = Math.abs(y - top);
            top = y;
        }
        if (y > bottom) {
            c = Math.abs(y - bottom);
            bottom = y;
        }

        return c;
    }

    public void stretch(BoundingBox b) {
        stretch(b.left, b.top);
        stretch(b.right, b.bottom);
    }

    public  boolean overlap(BoundingBox outerBBox) {

       //First bounding box, top left corner, bottom right corner
      int  ATLx = left;
       int ATLy= top;
       int ABRx = right;
       int ABRy = bottom;

       //Second bounding box, top left corner, bottom right corner
       int BTLx = outerBBox.left;
       int BTLy = outerBBox.top;
       int BBRx = outerBBox.right;
       int BBRy = outerBBox.bottom;

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
        if (x < left) {
            return false;
        }
        if (x > right) {
            return false;
        }
        if (y > top) {
            return false;
        }
        if (y < bottom) {
            return false;
        }
        return true;
    }
   
    double convertMinX() {
        return  Double.valueOf(left) / 10E5 ;
    }

    double convertMaxX() {
        return Double.valueOf(right) / 10E5 ;
    }

    double convertMinY() {
        return Double.valueOf(top)/ 10E5 ;
    }

    double convertMaxY() {
        return Double.valueOf(bottom) / 10E5;
    }

    int xExtent() {
        return right - left;
    }

    int yExtent() {
        return bottom - top;
    }

    int centerX() {
        return (left + right) / 2;
    }

    int centerY() {
        return (top + bottom) / 2;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if( getClass() != obj.getClass()){
            return false;
        }
        final BoundingBox otherBox=(BoundingBox)obj;
        if( left != otherBox.left || right != otherBox.right || top != otherBox.top || bottom != otherBox.bottom ){
            return  false;
        }
    return  true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.left;
        hash = 83 * hash + this.top;
        hash = 83 * hash + this.right;
        hash = 83 * hash + this.bottom;
        return hash;
    }
    public boolean dOverLap(BoundingBox compareBox){
        if (convertMinX() > compareBox.convertMaxX()) {
            return false;
        }
        // compareBox Start X is lessthen base box end right
        if (compareBox.convertMinX() > convertMaxX()) {
            return false;
        }
         // start Y is lessthen  compareBox end bottom  
        if (convertMinY() > compareBox.convertMaxY()) {
            return false;
        }
        // compareBox Start Y is lessthen base box end bottom
        if (compareBox.convertMinY() > convertMaxY()) {
            return false;
        }
        return true;
    }
   
     public String latlonBox(){
         return convertMinX()+","+convertMinY()+" "+convertMaxX()+","+convertMaxY();
     }
     
     public static BoundingBox getStretchBoxByRadius(int iLatE5,int iLonE5,int iRadiusInMeter){
    	 long latY = VilicusMercator.latToY( (iLatE5/10E5));
 		long lonX = VilicusMercator.lonToX((iLonE5/10E5));
 		int topY = (int) (Double.valueOf(VilicusMercator.yToLat(latY + iRadiusInMeter)) * 1E6);

 		int topX = (int) (Double.valueOf(VilicusMercator.xToLon(lonX - iRadiusInMeter)) * 1E6);

 		int bottomY = (int) (Double.valueOf(VilicusMercator.yToLat(latY - iRadiusInMeter)) * 1E6);

 		int bottomX = (int) (Double.valueOf(VilicusMercator.xToLon(lonX + iRadiusInMeter)) * 1E6);
 		BoundingBox box = new BoundingBox( topX,topY,  bottomX, bottomY);
 		
 		return box;
     }
     
     
}
