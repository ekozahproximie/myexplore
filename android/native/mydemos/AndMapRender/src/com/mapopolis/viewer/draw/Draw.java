
package com.mapopolis.viewer.draw;

import java.util.Vector;

import com.mapopolis.viewer.core.FeaturePoint;
import com.mapopolis.viewer.core.IO;
import com.mapopolis.viewer.core.MapFeature;
import com.mapopolis.viewer.core.MapFeatureRecord;
import com.mapopolis.viewer.core.MapFile;
import com.mapopolis.viewer.engine.MapopolisException;
import com.mapopolis.viewer.engine.PersistentSettings;
import com.mapopolis.viewer.route.Route;
import com.mapopolis.viewer.route.RouteElement;
import com.mapopolis.viewer.utils.Box;
import com.mapopolis.viewer.utils.PixelCoordinates;
import com.mapopolis.viewer.utils.WorldCoordinates;





public class Draw
	
{
    public static final int LargeStreetWidth0 = 10;
    public static final int LargeStreetWidth1 = 8;
    public static final int LargeStreetWidth2 = 6;
		
	LabelDraw labelDraw;
    private int features;
	
	Draw()
		
	{
		labelDraw = new LabelDraw();	
	}
	
	public void beginLabels()
	
	{
		LabelDraw.beginLabels();
	}
	
	public void draw(Vector <MapFile>maps, MapView mv, boolean drawLabels, Route route, Vector<MapFeature> selected) throws MapopolisException
		
	{
		//Engine.out("" + mv.getViewCenterPoint() + " " + mv.getWidth() + " " + mv.getHeight());
		
        int g = (mv.getWidth() >> 1);

        if ((mv.getHeight() >> 1) > g) 
        	g = (mv.getHeight() >> 1);

        g = (g << mv.zoom);

        int minx = mv.getViewCenterPoint().x - g;
        int miny = mv.getViewCenterPoint().y - g;
        int maxx = mv.getViewCenterPoint().x + g;
        int maxy = mv.getViewCenterPoint().y + g;

        Box viewBox = new Box(minx, miny, maxx,maxy);
		
        for (int i = 0; i < maps.size(); ++i) 
        {
            MapFile m = (MapFile) maps.elementAt(i);

			//Engine.out("View: " + viewBox);
			//Engine.out("Map: " + m.boundingBox());
			
			
			if ( Box.overlap(m.boundingBox(), viewBox) )
				drawMap(m, mv, drawLabels, route, selected, viewBox,mv.zoom);
            
            if (mv.stopRender) return;
        }
	}
	
    private void drawMap(MapFile map, MapView mv, boolean drawLabels, Route route, Vector selected, Box viewBox,int iZoomLevel) throws MapopolisException

    {
        //features = loopCount = lineCount = 0;

        // mLightType = getTypeToShow(detailLevel);

		int x = mv.getViewCenterPoint().x;
		int y = mv.getViewCenterPoint().y;

		for (int i = 0; i < map.dataRecords; ++i) 
        {
            MapFeatureRecord r = map.getMapFeatureRecords()[i];
           
	if (r.getChunkType() <= mv.maxTypeToDraw && r.getChunkType() >= mv.minTypeToDraw && r.isFeatureRecord) {
                if (Box.overlap(viewBox, r.boundingBox)) {
                    if (iZoomLevel > 14 && r.getChunkType() > 1) {
                        continue;
                    }
                    if (iZoomLevel > 12 && r.getChunkType() > 2) {
                        continue;
                    }
                    if (iZoomLevel > 9 && r.getChunkType() > 3) {
                        continue;
                    }
                    if (iZoomLevel > 6 && r.getChunkType() > 4) {
                        continue;
                    }
                    if (iZoomLevel > 3 && r.getChunkType() > 5) {
                        continue;
                    }
                    drawFeatureRecord(map, mv, r, viewBox, drawLabels, x, y);
                }
            }

			if ( mv.stopRender )
            	break;
        }
		
		// now go through each special feature (route elements, selected, etc)
        
        if ( route != null )
        {
        	Vector<RouteElement> re = route.routeElements();
        	
        	for ( int i = 0; i < re.size(); ++i )
        	{
        		RouteElement r = (RouteElement) re.elementAt(i);
				//Engine.out(r.street.friendlyName() + " " + r.startPoint.idx + " " + r.endPoint.idx);
        		drawMapFeature(r.street, mv, true, r.startPoint, r.endPoint, x, y, true);
        	}
        }
        
        if ( selected != null )
        {
			for ( int i = 0; i < selected.size(); ++i )
			{
				MapFeature mf = (MapFeature) selected.elementAt(i);
                                drawMapFeature(mf, mv, true, null, null, x, y, true);
			}
        }
    }
	
    private void drawFeatureRecord(MapFile map, MapView mv, MapFeatureRecord rec, Box viewBox, boolean drawLabels, int x, int y) throws MapopolisException

    {
		int[] featureIndices = rec.getFeatureIndices();
		int n = featureIndices[0];

		for ( int j = 1; j <= n; ++j )
		{
			MapFeature feature = new MapFeature(rec, featureIndices[j]);
			
			int t = feature.getStreetLevel();
			
            if ( t <= mv.maxTypeToDraw && t >= mv.minTypeToDraw )
				if ( Box.overlap(viewBox, feature.boundingBox()) ){
					drawMapFeature(feature, mv, drawLabels, null, null, x, y, false);
                                        //drawFeaturexxx(map, mv, feature, feature.getByte(), 0, t, t, t, drawLabels);
                                }

			if ( mv.stopRender )
            	break;
        }
    }

	private void drawMapFeature(MapFeature feature, MapView mv, boolean drawLabels, FeaturePoint p0, FeaturePoint p1, int x, int y, boolean r) throws MapopolisException

    {
        boolean doFilledPolygons = false;
		
		int width = mv.getWidth();
		int height = mv.getHeight();
		
        int width2 = (width >> 1);
        int height2 = (height >> 1);

        String name = feature.getName();

        Vector<PixelCoordinates> pts = new Vector<PixelCoordinates>(1000, 1000);
        Vector<FeaturePoint>  vecAllPoint = feature.allPoints(false, false);

        boolean start = ( p0 == null || p1 == null );
        FeaturePoint lastFeaturePoint = null;

		boolean isStreet = feature.isStreet();
                
		int l = feature.getStreetLevel();

		for ( int i = 0; i < vecAllPoint.size(); ++i )
        {
        	FeaturePoint fp = (FeaturePoint) vecAllPoint.elementAt(i);

        	if ( !start )
        		if ( (p0 != null && fp.equals(p0)) || (p1 != null && fp.equals(p1)) )
        		{
        			if ( fp.equals(p0) )
        				p0 = null;
        			else
        				p1 = null;

        			start = true;
        		}

        	if ( !start )
        		continue;

			if ( lastFeaturePoint != null && isStreet )
        	{

				int lastx = (((lastFeaturePoint.getX()) - x) >> mv.zoom) + width2;
				int lasty = (height - 1) - ((((lastFeaturePoint.getY()) - y) >> mv.zoom) + height2);

				int currx = (((fp.getX()) - x) >> mv.zoom) + width2;
				int curry = (height - 1) - ((((fp.getY()) - y) >> mv.zoom) + height2);

				drawLine(mv, lastx, lasty, currx, curry, l, false, false, false, r);
        		

				// TODO should this have EVERY point ???
				
				pts.addElement(new PixelCoordinates(currx, curry));
        	}
        
            //rotate(&xc, &yc, data->screenOrientation);

            if ( fp.isLast ) 
            	break;

            if ( start )
            	if ( (p0 != null && fp.equals(p0)) || (p1 != null && fp.equals(p1)) )
        			break;

            lastFeaturePoint = fp;
        }

		if ( isStreet && drawLabels )
            LabelDraw.labelStreet(mv, name, pts, PersistentSettings.Type1StreetColor);
    }

	/*
	private void drawLinex(MapView mv, int x0, int y0, int x1, int y1, int n, Color color)
    
    {
    	mv.getGB().setColor(color);
    	
        if (n == 1) 
        {
            mv.getGB().drawLine(x0, y0, x1, y1);
            return;
        }

        int t0 = -(n / 2);
        int t1 = (n + 1) / 2;

        for (int x = t0; x < t1; ++x)
            for (int y = t0; y < t1; ++y) 
                if (x == t0 || x == t1 || y == t0 || y == t1) 
                    mv.getGB().drawLine(x0 + x, y0 + y, x1 + x, y1 + y);
    }
    */
	
    private void drawLine(MapView mv, int x0, int y0, int x1, int y1, int type, boolean w, boolean g, boolean f, boolean r)

    {
        //lineCount++;

        int n = getPix(mv, type, (r?1:0));

		if (r)
			mv.getGB().setColor(PersistentSettings.RouteStreetColor);
        else if (w) 
        	mv.getGB().setColor(PersistentSettings.WaterColor);
        else if (g) 
        	mv.getGB().setColor(PersistentSettings.GreenColor);
        else if (f) 
        	mv.getGB().setColor(PersistentSettings.FacilityColor);
        else if (type == 1) 
        	mv.getGB().setColor(PersistentSettings.Type1StreetColor);
        else if (type == 2) 
        	mv.getGB().setColor(PersistentSettings.Type2StreetColor);
        else if (type == 3) 
        	mv.getGB().setColor(PersistentSettings.Type3StreetColor);
        else if (type == 4) 
        	mv.getGB().setColor(PersistentSettings.Type4StreetColor);
        else if (type == 5)
        	mv.getGB().setColor(PersistentSettings.Type5StreetColor);

        if (n == 1) 
        {
            mv.getGB().drawLine(x0, y0, x1, y1);
            return;
        }

        int t0 = -(n / 2);
        int t1 = (n + 1) / 2;

        for (int x = t0; x < t1; ++x)
            for (int y = t0; y < t1; ++y) 
                if (x == t0 || x == t1 || y == t0 || y == t1) 
                    mv.getGB().drawLine(x0 + x, y0 + y, x1 + x, y1 + y);
    }

    private int getPix(MapView mv, int type, int selected)

    {
        int pix;

        if (type == 1 || type == 2) pix = 2; else pix = 1;

        int p;

        if (mv.zoom < 3) 
		{
            if (mv.zoom == 0) 
			{
                p = LargeStreetWidth0;
                if (pix == 2) pix = p;
                else pix = p - 2;
            } 
			else if (mv.zoom == 1) 
			{
                p = LargeStreetWidth1;
                if (pix == 2) pix = p;
                else pix = p - 2;
            } 
			else if (mv.zoom == 2) 
			{
                p = LargeStreetWidth2;
                if (pix == 2) pix = p;
                else pix = p - 2;
            }

            if (selected != 0) pix *= 2;
        } 
		else 
		{
            if (MapView.maxDisplayWidth >= 320) 
			{
                if (selected == 2) pix = 10;
                else if (selected != 0) pix = 8;
            } 
			else 
			{
                if (selected == 2) pix = 5;
                else if (selected != 0) pix = 4;
            }
        }
		
        return pix;
    }

    private void drawFeaturexxx(MapFile map, MapView mv,  MapFeature mapFeature,
            byte[] buffer, int index, int height, int width2, int height2, boolean drawLabels)
            throws MapopolisException

    {
        //System.out.println(mv.zoom);

        int comp, start, slen, len, lastlen, nextIndex, xc, yc;
        boolean isLast;
        boolean doFilledPolygons = false;

        features++;
        int width = mv.getWidth();
		 height = mv.getHeight();
		
         width2 = (width >> 1);
         height2 = (height >> 1);

        //System.out.println(" Draw feature " + MapUtilities.getStreetName(buffer, index));
        
        //msg(" [" + x0 + " " + y0 + " " + x1 + " " + y1 + "][" +
        //				   ax0 + " " + ay0 + " " + ax1 + " " + ay1 + "]");

        int n = 0, lastxUnits = 0, lastyUnits = 0, currxUnits = 0, curryUnits = 0;
        int lastx = 0, lasty = 0, currx = 0, curry = 0;
        buffer=mapFeature.getByte();
        String name = mapFeature.getName();
        int type  = mapFeature.getFeatureType();
        
        boolean water = type == MapFeature.Water;
        boolean green = type == MapFeature.Green;
        boolean facility = type == MapFeature.Facility;

        boolean polygon = water || green || facility;
        
        boolean landmark = type == MapFeature.Landmark;
        boolean street = type == MapFeature.Street;

        index += 16;
            int length=IO.get2(buffer, index );
        Vector<PixelCoordinates> pts = new Vector<PixelCoordinates>(1000, 1000);
        WorldCoordinates wc = new WorldCoordinates(0, 0);

        while (true) 
        {
            if ((buffer[index + 0] & 0x80) != 0) 
            {
                 len =IO.get2( buffer,index - 2);

                comp = (buffer[index + 0] & 3);
                start = 3;

                isLast = ((buffer[index + 0] & 32) != 0);
            } 
            else 
            {
                len = 3;

                comp = 1;
                start = 1;

                isLast = false;
            }

            if (comp == 1) {
                wc.x = IO.get1(buffer, index + start) - 128;
                wc.y = IO.get1(buffer, index + start + 1) - 128;
            } else if (comp == 2) {
                wc.x = IO.get2(buffer, index + start) - 32768;
                wc.y = IO.get2(buffer, index + start + 2) - 32768;
            } else if (comp == 3) {
                wc.x = IO.get3(buffer, index + start) - 8388608;
                wc.y = IO.get3(buffer, index + start + 3) - 8388608;
            } else {
                wc.x = 0;
                wc.y = 0;
            }

            currxUnits = lastxUnits + wc.x;
            curryUnits = lastyUnits + wc.y;

            //System.out.println(mv.mapCenterPoint.x);

            xc = (((currxUnits + map.X0) - mv.mapCenterPoint.x) >> mv.zoom) + width2;
            yc = (((curryUnits + map.Y0) - mv.mapCenterPoint.y) >> mv.zoom) + height2;

            //rotate(&xc, &yc, data->screenOrientation);

            yc = (height - 1) - yc;

            currx = xc;
            curry = yc;

            pts.addElement(new PixelCoordinates(xc, yc));

            if (polygon && doFilledPolygons) 
            {} 
            else if (landmark) 
            {} 
            else 
            {
                // don't process until have second point

                if (n > 0) 
                {
                    drawLine(mv, lastx, lasty, currx, curry, type, water, green, facility,true);
                }
            }

            if (isLast) break;

            index += len;

            lastx = currx;
            lasty = curry;

            lastxUnits = currxUnits;
            lastyUnits = curryUnits;

            n++;
        }

        if (polygon && doFilledPolygons) 
        {
            if (water) mv.getGB().setColor(PersistentSettings.WaterColor);
            else if (green) mv.getGB().setColor(PersistentSettings.GreenColor);
            else if (facility)
                    mv.getGB().setColor(PersistentSettings.FacilityColor);

            mv.getGB().fillPolygon(pts);
        } 
        else if (street && drawLabels)
        {
            LabelDraw.labelStreet(mv, name, pts, PersistentSettings.Type1StreetColor);
        }
    }
	

}

	// ******************* changing
	
	
	
 