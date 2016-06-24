
package com.mapopolis.viewer.utils;

public class Box 

{
	public int x0, y0, x1, y1;
	
	private Box()
	
	{
		
	}
	
	public Box(int xx0, int yy0, int xx1, int yy1)
	
	{
		x0 = xx0;
		y0 = yy0;
		
		x1 = xx1;
		y1 = yy1;
	}
	
	public Box(int xx0, int yy0, int xx1, int yy1, boolean t)
		
	{
		// make x1 > x0 and y1 > y0
		
		x0 = xx0;
		y0 = yy0;
		
		x1 = xx1;
		y1 = yy1;

		if ( x0 > x1 )
		{
			x1 = xx0;
			x0 = xx1;
		}
		
		if ( y0 > y1 )
		{
			y1 = yy0;
			y0 = yy1;
		}
	}
	
    public static boolean overlap(Box b0, Box b1) //int x0, int y0, int x1, int y1)

    {
    	//System.out.println("R box " + X0 + " " + X1 + " " + Y0 + " " + Y1);
    	//System.out.println("V box " + x0 + " " + x1 + " " + y0 + " " + y1);
    	
        if (b0.x0 > b1.x1) return false;
        if (b1.x0 > b0.x1) return false;
        
        if (b0.y0 > b1.y1) return false;
        if (b1.y0 > b0.y1) return false;
        
        return true;
    }
	
    public static boolean overlapWithMargin(Box b0, Box b1, int m)

    {
    	//System.out.println("R box " + X0 + " " + X1 + " " + Y0 + " " + Y1);
    	//System.out.println("V box " + x0 + " " + x1 + " " + y0 + " " + y1);
    	
        if (b0.x0 - b1.x1 > m) return false;
        if (b1.x0 - b0.x1 > m) return false;
        
        if (b0.y0 - b1.y1 > m) return false;
        if (b1.y0 - b0.y1 > m) return false;
        
        return true;
    }
	
	public void stretch(int x, int y)
		
	{
		if ( x > x1 ) x1 = x;
		if ( x < x0 ) x0 = x;
		
		if ( y > y1 ) y1 = y;
		if ( y < y0 ) y0 = y;
	}
	
	public String toString()
		
	{
		return "[" + x0 + " " + y0 + ", " + x1 + " " + y1 + "]";
	}
}