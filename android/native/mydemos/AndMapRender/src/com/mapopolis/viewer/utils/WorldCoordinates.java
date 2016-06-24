
package com.mapopolis.viewer.utils;

public class WorldCoordinates

{
    public int x, y;

    public WorldCoordinates(int xx, int yy)

    {
        x = xx;
        y = yy;
    }

    public String toString()
    
    {
    	return "World Coordinates: " + x + ", " + y;
    }

	public static int distanceBetweenCoordinates(WorldCoordinates wc1, WorldCoordinates wc2)
	{
		int dx = Math.abs(wc1.x - wc2.x);
		int dy = Math.abs(wc1.y - wc2.y);

		return Utilities.hypot(dx, dy);
	}

	public static int distanceBetweenCoordinates(int x0, int y0, int x1, int y1)
    
    {
    	int dx = Math.abs(x0 - x1);
    	int dy = Math.abs(y0 - y1);
    	
    	return Utilities.hypot(dx, dy);
    }

	public static WorldCoordinates random()

	{
		int x = (int)(-7000000 - (1000000.0 * Math.random()));
		int y = (int)(3500000 + (1000000.0 * Math.random()));

		return new WorldCoordinates(x, y);
	}
}