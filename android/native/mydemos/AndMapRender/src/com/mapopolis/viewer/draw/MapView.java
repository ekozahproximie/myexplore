
package com.mapopolis.viewer.draw;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mapopolis.viewer.core.MapFeature;
import com.mapopolis.viewer.core.MapFile;
import com.mapopolis.viewer.engine.Engine;
import com.mapopolis.viewer.engine.MapopolisException;
import com.mapopolis.viewer.route.Route;
import com.mapopolis.viewer.utils.PixelCoordinates;
import com.mapopolis.viewer.utils.WorldCoordinates;


/**
 * Handles moving, drawing, zooming, rotation, and such of the map view
 */

public class MapView 

{
    public static final int maxDisplayWidth = 1600;
    public static final int maxDisplayHeight = 1200;

    // the center point (in absolute world coordinates) of the map view

    public WorldCoordinates mapCenterPoint;

    // the zoom level of the map from 0 - 15 where
    // 0 = 1 meter per pixel
    // 1 = 2 meters per pixel
    // 2 = 4 meter per pixel
    // etc

    public int zoom;

    // the maximum street type from 1 - 5 to be drawn
    // where the street types are
    // 1 = highway
    // 2 = state highway
    // 3 = other major road
    // 4 = major secondary
    // 5 = minor secondary

    public int maxTypeToDraw = 5;
    public int minTypeToDraw = 1;

    // the dimensions of the CURRENT display area
    // which is the CURRENT total area
    // where the engine may draw
    // including the map, nav pane, etc.
    // this may change throughout the program


    // the dimensions of the MAXIMUM display area
    // to be used throughout the program
    // should be divisible by 16
    

    // flags to tell the engine which panes to draw

    // boolean showNavPane;
    // boolean showGPSPane;

    // the current map rotation from 0 - 359 degrees
    // this is rounded to the nearest 32nd of a complete circle

    // int currentRotation = 0;
    
    public GraphicsBuffer gb = null;
    private Vector<MapFile> mapsAvailable = new Vector<MapFile>();
    
	public boolean stopRender = false;
	private Draw draw;

    public MapView(Vector<MapFile> v)

	{
        mapsAvailable = v;
		draw = new Draw();
		init();
    }

    public WorldCoordinates getViewCenterPoint()

    {
        return mapCenterPoint;
    }

    private void init()

    {
        if (mapsAvailable.size() > 2) 
        {
            MapFile m = (MapFile) mapsAvailable.elementAt(2);
			mapCenterPoint = new WorldCoordinates((m.X0 + m.X1) / 2, (m.Y0 + m.Y1) / 2);
            
			//initCenterPointToMapCenterPoint(m);

            //MapFile m = (MapFile) MapFile.mapsAvailable.elementAt(0);
            //mapCenterPoint = new WorldCoordinates((m.X0 + m.X1) / 2 - 18000,
            // (m.Y0 + m.Y1) / 2 - 30000);
        } 
        else 
        {
            mapCenterPoint = new WorldCoordinates(-6000000, 4000000);
        }

		Engine.out(mapCenterPoint.toString());		
		
        //mapCenterPoint = new WorldCoordinates(-8000000, 4000000);
    }
    
	public void render(Canvas g, int w, int h, PixelCoordinates pixelUpperLeft, Route route, Vector<MapFeature> selected)
						   
	{
    	draw.beginLabels();

		if (gb == null || w != gb.width || h != gb.height)
		{
			if (Engine.debug) Engine.out("New gb " + w + " " + h);
			gb = new GraphicsBuffer(w, h);
		}

        gb.paintBackground();
		
        stopRender = false;

        //mapCenterPoint = centerPoint;

		try

        {
            draw.draw(mapsAvailable, this, true, route, selected);
        }

        catch (Exception e)

        {
			Engine.out(e.toString()); e.printStackTrace();
        }

		g.drawBitmap(getImage(), pixelUpperLeft.x, pixelUpperLeft.y, null);
	}
	
    public void renderFull(Canvas g, int w, int h, PixelCoordinates pixelUpperLeft, Route route, Vector<MapFeature> selected)
    
    {
    	prepareForRender(g, w, h);
    	finishRender(g, pixelUpperLeft, route, selected);
    }

    private void prepareForRender(Canvas g, int w, int h) 
    
    {
    	draw.beginLabels();

		if (gb == null || w != gb.width || h != gb.height)
		{
			if (Engine.debug) Engine.out("New gb " + w + " " + h);
			gb = new GraphicsBuffer(w, h);
		}

        gb.paintBackground();
    }

	private void finishRender(Canvas g, PixelCoordinates pixelUpperLeft, Route route, Vector<MapFeature> selected) 
    
    {
        renderImpl(g, mapCenterPoint, true, pixelUpperLeft, route, selected);
    }
   
    private void renderImpl(Canvas g, WorldCoordinates centerPoint, boolean lastPass, PixelCoordinates pixelUpperLeft, Route route, Vector<MapFeature> selected)

    {
        stopRender = false;

        mapCenterPoint = centerPoint;

		try

        {
            draw.draw(mapsAvailable, this, lastPass, route, selected);
        }

        catch (Exception e)

        {
			Engine.out(e.toString()); e.printStackTrace();
        }

		g.drawBitmap(getImage(), pixelUpperLeft.x, pixelUpperLeft.y, null);
    }

	/**
     * Moves the map center point by the specified number of pixels in the x and
     * y direction
     */

    public void moveCenterPointPixels(int x, int y)

    {
        mapCenterPoint.x += (x << zoom);
        mapCenterPoint.y -= (y << zoom);
    }

    public void setCenterPointPixels(int x, int y)

    {

    }

    /**
     * Sets the map center point to the WorldCoordinates specified (returned by
     * Search functions)
     */

    public void setCenterPointWorld(WorldCoordinates wc)

    {
        mapCenterPoint = wc;
    }

    public void setCenterPointWorld(int x, int y)

    {
        mapCenterPoint = new WorldCoordinates(x, y);
    }

	/**
     * Zooms in
     */

    public void zoomIn()

    {
        zoom--;

        if (zoom < 0) zoom = 0;
    }

    /**
     * Zooms out
     */

    public void zoomOut()

    {
        zoom++;

        if (zoom > 15) zoom = 15;
    }

    /**
     * Sets the zoom (0 - 15)
     */

    public void setZoom(int z)

    {
        zoom = z;
        if (zoom < 0) zoom = 0;
        if (zoom > 15) zoom = 15;
    }

    public void setDetail(int n)
    
    {
    	maxTypeToDraw = n;
    	if (maxTypeToDraw > 5) maxTypeToDraw = 5;
    	if (maxTypeToDraw < 1) maxTypeToDraw = 1;
    }
    
    public void moreDetail()

    {
        ++maxTypeToDraw;
        if (maxTypeToDraw > 5) maxTypeToDraw = 5;
    }

    public void lessDetail()

    {
        --maxTypeToDraw;
        if (maxTypeToDraw < 1) maxTypeToDraw = 1;
    }

    public int getMaxTypeToDraw() 
    
    {
        return maxTypeToDraw;
    }

    /**
     * Sets the maximum street type to draw (1 - 5)
     */

    public void setMaxTypeToDraw(int max)

    {
        maxTypeToDraw = max;
        if (maxTypeToDraw < 1) maxTypeToDraw = 1;
        if (maxTypeToDraw > 5) maxTypeToDraw = 5;
    }

    public void setMinTypeToDraw(int min)

    {
        minTypeToDraw = min;
        if (minTypeToDraw < 1) minTypeToDraw = 1;
        if (minTypeToDraw > 5) minTypeToDraw = 5;
    }

    public GraphicsBuffer getGB()

    {
        return gb;
    }

    public int getWidth()

    {
        return gb.width;
    }

    public int getHeight()

    {
        return gb.height;
    }

    public Bitmap getImage()

    {
        return gb.getImage();
    }

    public void stopRender()

    {
        stopRender = true;
    }

    public Paint getBackgroundColor() 
    
    {
        return new Paint(Color.YELLOW);
    }

    public void saveImage(String s) throws MapopolisException
    
    {
    	try
		
		{
    		// "d:\\docume~1\\admini~1\\desktop\\Map\\map.gif"
    		
    		FileOutputStream out = new FileOutputStream(s);
    		//new GIFOutputStream(out).write(getImage());
    		out.close();
		}
    	
    	catch (IOException e)
		
		{
			Engine.out(e.toString()); e.printStackTrace();
		}
    }
	
    public PixelCoordinates convertWorldToPixels(WorldCoordinates wc) //, int height, int width2, int height2)

    {
        int xc = (wc.x - mapCenterPoint.x) >> zoom;
        int yc = (wc.y - mapCenterPoint.y) >> zoom;

        //rotate(&xc, &yc, data->screenOrientation);

        xc += (gb.width >> 1);
        yc += (gb.height >> 1);

        yc = (gb.height - 1) - yc;

        //if ( data->threeD )
        //	yc += (data->maxY * 2)/5;

        return new PixelCoordinates(xc, yc);
    }

    public WorldCoordinates convertPixelsToWorld(PixelCoordinates pix)

    {
        int xc = pix.x - getGB().width/2;
        int yc = pix.y - getGB().height/2;

        xc = mapCenterPoint.x + (xc << zoom);
        yc = mapCenterPoint.y - (yc << zoom);

        //System.out.println("center " + mv.mapCenterPoint.x + " " + mv.mapCenterPoint.y);
        //System.out.println("pix " + pix.x + " " + pix.y);// + " " + width2 + " " + height2);
        
        return new WorldCoordinates(xc, yc);
    }

	/**
     * Sets the rotation angle (0 - 359) to the closest 32nd of a circle
     * 
     * @param rotation
     *            the angle to rotate the map 0 = North up
     */

    //public void setRotation(int rotation)
    //
    //{
    //
    //}
    //public void setDrawMap(boolean t)
    //
    //{
    //	//drawMap = t;
    //}
    //public void setDrawStreetLabels(boolean t)
    //
    //{
    //	drawStreetLabels = t;
    //}
}