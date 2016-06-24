
package com.mapopolis.viewer.core;

import com.mapopolis.viewer.utils.*;
import com.mapopolis.viewer.engine.*;
import com.mapopolis.viewer.search.*;

import java.util.Vector;

public class MapFeature

{
    
	
	
	
	
	public static final int Railroad = 1;
    public static final int Landmark = 2;
    public static final int Water = 3;
    public static final int Facility = 4;
    public static final int Green = 5;
    public static final int City = 6;
    public static final int County = 7;
    public static final int Street = 8;
	public static final int MaxNameParts = 15;
	public static final int SideLeft = 1;
    public static final int SideRight = 2;
    public static final int SideUnknown = 3;
	
	
    public MapFeatureRecord mapFeatureRecord;
    int offsetInRecord;
	
	// this will be stored when calculated if LargeDevice
    
	private Box boundingBox = null;
	//private Vector allPoints = null;
	private String name = null;
	private int featureType;
	private int streetLevel;
	
	//private MapFeature myCachedCopy = null;
    
    public boolean equals(MapFeature m)
    
    {
    	return m.offsetInRecord == offsetInRecord && m.mapFeatureRecord.equals(mapFeatureRecord);
    }

    public MapFeature(MapFeatureRecord mfr, int offset) throws MapopolisException

    {
        mapFeatureRecord = mfr;
        offsetInRecord = offset;
		
		//Engine.out(this + " " + ( mapFeatureRecord.getFeatureCache(this) != null ));
		
		//if ( Engine.LargeDevice )
		//	prepareCachedRecord();
    }

	/*
    public MapFeature(MapFile map, int o) throws MapopolisException

    {
        mapFeatureRecord = map.getMapFeatureRecords()[((o >> 16) & 0x00000fff)];
        offsetInRecord = (o & 0x0000ffff);

        // this is the address of a point

        // search for first

        FeaturePoint fp;

        while (true) 
        {
            fp = new FeaturePoint(mapFeatureRecord, offsetInRecord, false, false, false);

            if (fp.isFirst) break;

            offsetInRecord = fp.nextIndex;
        }

        offsetInRecord -= FeatureStartToPointsStart;
		
		// should we do caching here?
    }
	*/

	/*
	private void prepareCachedRecord() throws MapopolisException

	{
		if ( mapFeatureRecord.getFeatureCache(this) != null )
		{
			myCachedCopy = mapFeatureRecord.getFeatureCache(this);
		}
		else
		{
			// make everything and store in cache
				
			allPoints = allPoints(true, true);
			name = getName();
			boundingBox = boundingBox();
			featureType = getFeatureType();
			streetLevel = getStreetLevel();
			
			mapFeatureRecord.setFeatureCache(this);

			for (int i = 0; i < allPoints.size(); ++i)
			{
				FeaturePoint pt = (FeaturePoint)allPoints.elementAt(i);

				mapFeatureRecord.setFeatureCache(pt);

				pt.nextNode = getNextNode(pt);
				pt.lastNode = getLastNode(pt);
			}

			myCachedCopy = this;
		}
	}
	*/

	/*
	private FeaturePoint getNextNode(FeaturePoint fp)
	{
		Vector v = allPoints;
		boolean passed = false;

		for (int i = 0; i < v.size(); ++i)
		{
			FeaturePoint pt = (FeaturePoint)v.elementAt(i);

			if (pt.equals(fp))
			{
				passed = true;
				continue;
			}

			if (passed)
				if (pt.isNode)
					return pt;
		}

		return null;
	}

	private FeaturePoint getLastNode(FeaturePoint fp)
	{
		Vector v = allPoints;
		boolean passed = false;

		for (int i = v.size() - 1; i >= 0; --i )
		{
			FeaturePoint pt = (FeaturePoint)v.elementAt(i);

			if (pt.equals(fp))
			{
				passed = true;
				continue;
			}

			if (passed)
				if (pt.isNode)
					return pt;
		}

		return null;
	}
	*/

	public FeaturePoint firstPoint() throws MapopolisException

    {
        return (FeaturePoint) allPoints(false, false).elementAt(0);
    }

    public String[] getNames() throws MapopolisException

    {
        return getNames(getName());
    }

	public String getName() throws MapopolisException

    {
		//if ( myCachedCopy != null )
		//	return myCachedCopy.name;
		//else
		
		return getName(mapFeatureRecord, offsetInRecord);
    }
	
	// new navcard format 
	//
	//	feature record format
	//
	//		2 bytes = length of the whole feature record
	//		1 byte = descriptor byte
	//		1 byte = extended type byte
	//		2 bytes = length of points block
	//		point array
	//		compressed name string
	
	public byte[] getByte()throws MapopolisException{
             byte[] buffer = mapFeatureRecord.getAsBytes();
             return  buffer;
        }
        private static String getName(MapFeatureRecord mfr, int index) throws MapopolisException

    {
                byte[] buffer = mfr.getAsBytes();

		index += IO.get2(buffer, index + MapFile.HeaderLen) + MapFile.HeaderLen;	
                //old way: IO.get2(buffer, index + 4) + 4;

		SN sn = Utilities.expandString(buffer, index, mfr.stringsCompressed());
		
		//Engine.out("=====" + sn.name + "=====");
		
		return sn.name;
	}

    private static String[] getNames(String s) throws MapopolisException

    {
    	int n = 0, start = 0, end = 0;
    	
        String[] strings = new String[MaxNameParts];
        
		//String s = getName(mfr, index);

		while (true) 
        {
            end = s.indexOf('|', start);

            if (end < 0) 
            {
                end = s.length();
                strings[n] = s.substring(start, end);
                break;
            } 
            else 
            {
				if ( n >= MaxNameParts )
					Engine.out(s);
				
                strings[n++] = s.substring(start, end);
                start = end + 1;
            }
        }
        
        String[] r = new String[n + 1];
        
        for ( int i = 0; i < n + 1; ++i )
        	r[i] = strings[i];
        
        return r;
    }

	public String friendlyName() throws MapopolisException

    {
        String[] n = getNames();
        String s = "";

        for (int i = 0; i < n.length; ++i)
            if (n[i] != null) 
            {
                if (i > 0) s += "/";
                s += n[i];
            }

        return s;
    }

    public StreetRelativeLocation streetRelativeLocationOfAddress(int address) throws MapopolisException

    {
		Vector<FeaturePoint> p = allPoints(true, true);

		StreetRelativeLocation a = boundingAddressPointsOnStreet(address, p);

		//Engine.out("SRL for " + address + " on " + this + " is " + a);
		
		if (a == null) 
        	return null;

        StreetRelativeLocation srl = new StreetRelativeLocation();
		
        srl.mapFeature = this;
        srl.address = address;
        
        // calculate total distance

        FeaturePoint fp;
		
		//Engine.out("SRL " + a.beforePoint + " " + a.afterPoint);

		int totalDistance = 0;
		boolean start = false;
		
		for ( int i = 0; i < p.size(); ++i )
        {
            fp = (FeaturePoint) p.elementAt(i);// FeaturePoint(mapFeatureRecord, index, true, false, false);

			if ( fp.equals(a.afterPoint) ) 
				break;

			if ( fp.equals(a.beforePoint) )
				start = true;
			
			if ( start )
				totalDistance += fp.distance;
            
			//index = fp.nextIndex;
        }

        int find = (totalDistance * a.percentAlongSegment) / 100;

		if ( find < 0 )
			find = 0;
		
		//Engine.out("total=" + totalDistance + " find=" + find);		
		
		//fp = a.beforePoint;
        //index = fp.nextIndex;
		
        totalDistance = 0;
        FeaturePoint lastfp = null;
		start = false;

		for ( int i = 0; i < p.size(); ++i )
        {
            fp = (FeaturePoint) p.elementAt(i);

			if ( fp.equals(a.beforePoint) )
				start = true;

			if ( !start )
				continue;
			
			int ld = 0;
			
			if ( lastfp != null )
			{
				ld = WorldCoordinates.distanceBetweenCoordinates(lastfp.getX(), lastfp.getY(), fp.getX(), fp.getY());
				totalDistance += ld;
			}

			//Engine.out("distances=" + totalDistance + " " + find);

			if ( (totalDistance > find || fp.equals(a.afterPoint)) && lastfp != null ) 
            {
                // it is in the segment between lastfp and fp

                find = find - (totalDistance - ld);

				//Engine.out(ld + "==" + find);
				
                int div;
                
                if ( ld == 0 )
					div = 1;
				else
                	div = ld;
                
                srl.percentAlongSegment = (find * 100) / div;
                
                if (srl.percentAlongSegment < 2) srl.percentAlongSegment = 2;
                if (srl.percentAlongSegment > 98) srl.percentAlongSegment = 98;

                srl.sideOfStreet = a.sideOfStreet;
				
                srl.beforePoint = lastfp;
                srl.afterPoint = fp;

				return srl;
            }
			
			lastfp = fp;
        }

        return null;
    }
    
	//// check ...
	
    public StreetRelativeLocation streetRelativeLocationOfCoordinates(WorldCoordinates wc) throws MapopolisException
	
	{
        int min = 999999999;
        int index  = -1;
        PointAndDistance mpd = null;
        
        Vector<FeaturePoint> p = allPoints(false, true);
                
        for (int i = 0; i < p.size() - 1; ++i)
        {
            FeaturePoint pf = (FeaturePoint) p.elementAt(i);
            FeaturePoint pt = (FeaturePoint) p.elementAt(i + 1);

            PointAndDistance pd = distanceToSegment(pf, pt, wc.x , wc.y);

            if (pd.distance < min)
            {
                min = pd.distance;
                index = i;
                mpd = pd;
            }
        }

        StreetRelativeLocation srl = new StreetRelativeLocation();
        
        srl.mapFeature = this;
        srl.beforePoint = (FeaturePoint) p.elementAt(index);
        srl.afterPoint = (FeaturePoint) p.elementAt(index + 1);
        
        // get address at the location
        // get side of street at the location

		int sd = WorldCoordinates.distanceBetweenCoordinates(srl.beforePoint.getX(), srl.beforePoint.getY(), srl.afterPoint.getX(), srl.afterPoint.getY());
		int md = WorldCoordinates.distanceBetweenCoordinates(mpd.wc.x, mpd.wc.y, srl.beforePoint.getX(), srl.beforePoint.getY());
		
		if ( sd > 0 )
			srl.percentAlongSegment = (100 * md) /sd;
		else
			srl.percentAlongSegment = 50;

		if ( srl.percentAlongSegment < 2) srl.percentAlongSegment = 2;
        if ( srl.percentAlongSegment > 98) srl.percentAlongSegment = 98;

    	return srl;
	}

    public int numberOfPoints() throws MapopolisException
    
    {
    	// there may be a faster way to do this
    	
        return allPoints(false, false).size();
    }

    public WorldCoordinates coordinatesOfCenter() throws MapopolisException

    {
        Box b = boundingBox();
        return new WorldCoordinates((b.x0 + b.x1) / 2, (b.y0 + b.y1) / 2);
    }

    //int sideOfStreetSegmentOfCoordinates(WorldCoordinates wc, int pointIndex)
    //
    //{
    //    return 0;
    //}

    //int closestAddressToCoordinates(WorldCoordinates wc)
    //
    //{
    //    return 0;
    //}

    public Vector<FeaturePoint> allPoints(boolean buildAddressInfo, boolean buildOverlap) throws MapopolisException 

    {
		//if ( myCachedCopy != null )
		//	return myCachedCopy.allPoints;
		
		Vector<FeaturePoint> results = new Vector<FeaturePoint>();

        int index = offsetInRecord + MapFile.FeatureStartToPointsStart;

        int x = 0, y = 0, c = 0, seq = -1;

        int x0 = mapFeatureRecord.mapFile.X0;
        int y0 = mapFeatureRecord.mapFile.Y0;

        while (true) 
        {
            FeaturePoint fp = new FeaturePoint(mapFeatureRecord, index, true, buildAddressInfo, buildOverlap);

			//Engine.out("" + fp);
			// set the world coordinates


//Engine.out(this.mapFeatureRecord.mapFile.friendlyName() + " " + (new MapFeature(this.mapFeatureRecord, this.offsetInRecord)).getName()
//+ " " + fp + " " + c + " " + seq + " " + fp.seqNumber);


            if ( c == 0 )
            	seq = fp.seqNumber;
            else if ( fp.isNode )
            {
            	seq++;
            	
            	if ( fp.seqNumber != 0 )
            		if ( seq != fp.seqNumber )
            		{
            			//new Exception().printStackTrace();
            			//throw new MapopolisException("Invalid Sequence Number " + seq + " " + fp.seqNumber 
            			//	+ " " + c + " " + fp
            			//	+ " " + (new MapFeature(this.mapFeatureRecord, this.offsetInRecord)).getName() + 
            			//		" " + this.mapFeatureRecord.mapFile.friendlyName());
            		}
            		
            	fp.seqNumber = seq;
            }
            
            x += fp.dx;
            y += fp.dy;

			fp.setX(x0 + x);
			fp.setY(y0 + y);

            results.addElement(fp);

            if (fp.isLast) 
				break;

            index = fp.nextIndex;
            c++;
        }

		return results;
    }
    
    public Vector<FeaturePoint> allNodes(boolean buildAddressInfo, boolean buildOverlap) throws MapopolisException
	
	{
    	Vector<FeaturePoint> v = allPoints(buildAddressInfo, buildOverlap);
    	Vector<FeaturePoint> r = new Vector<FeaturePoint>();
    	
    	for ( int i = 0; i < v.size(); ++i )
    	{
    		FeaturePoint fp = (FeaturePoint) v.elementAt(i);
    		
    		if ( fp.isNode )
    			r.addElement(fp);
    	}
    	
    	return r;
	}
	
	FeaturePoint getSamePoint(FeaturePoint fp) throws MapopolisException
		
	{
		Vector<FeaturePoint> p = allPoints(false, false);
		 
        for (int i = 0; i < p.size(); ++i)
        {
            FeaturePoint pf = (FeaturePoint) p.elementAt(i);
			
			//if ( Engine.debug ) Engine.out(fp.idx + " " + pf.idx + "  " + fp.myMapFeatureRecord + " " + pf.myMapFeatureRecord);
			
			if ( pf.equals(fp) )
				return pf;
		}
		
		throw new MapopolisException("cannot get same point");
	}

    public int distanceToCoordinates(WorldCoordinates wc) throws MapopolisException

    {
        int min = 999999999;
        Vector<FeaturePoint> p = allPoints(false, false);

        for (int i = 0; i < p.size() - 1; ++i)
        {
            FeaturePoint pf = (FeaturePoint) p.elementAt(i);
            FeaturePoint pt = (FeaturePoint) p.elementAt(i + 1);

            int distance = distanceToSegment(pf, pt, wc.x , wc.y).distance;

            if (distance < min) 
                min = distance;
        }

        return min;
    }

	public String myCities() throws MapopolisException
	{
		Vector<SearchArea> c = new Vector<SearchArea>();

		inCity(0, c);

		String str = " in: ";

		for (int i = 0; i < c.size(); ++i)
		{
			SearchArea s = (SearchArea)c.elementAt(i);
			str += s.name + ", ";
		}

		return str;
	}
	
	public boolean inCity(int city, Vector<SearchArea> r) throws MapopolisException

	{
    	Vector<FeaturePoint> v = allPoints(false, false);
    	
    	for ( int i = 0; i < v.size(); ++i )
    	{
    		FeaturePoint f = (FeaturePoint) v.elementAt(i);
    		
			if ( f.containsCity(city, r) )
				return true;
    	}
    	
    	return false;
	}

	public boolean inCity(String city, Vector<SearchArea> r) throws MapopolisException

	{
		//Engine.out(city);
		
    	Vector<FeaturePoint> v = allPoints(false, false);
    	
    	for ( int i = 0; i < v.size(); ++i )
    	{
    		FeaturePoint f = (FeaturePoint) v.elementAt(i);
    		
			if ( f.containsCity(city, r) )
				return true;
    	}
    	
    	return false;
	}
    
	private StreetRelativeLocation boundingAddressPointsOnStreet(int address, Vector<FeaturePoint> p) throws MapopolisException

    {
    	// this just returns the before and after points
    	// and percent is set to percent between the two 
    	// points (possibly separated by multiple points)

		//if ( MapSearch.debug ) Engine.out("Points=" + p.size());
		
        for (int i = 0; i < p.size(); ++i)
        {
            FeaturePoint pf = (FeaturePoint) p.elementAt(i);

			//if ( MapSearch.debug ) Engine.out("from point " + i + " " + pf.hasAddressSet());
			
            if (!pf.hasAddressSet()) 
				continue;

            for (int k = i + 1; k < p.size(); ++k) 
            {
                FeaturePoint pt = (FeaturePoint) p.elementAt(k);

				//if ( MapSearch.debug ) Engine.out("to point " + k + " " + pt.hasAddressSet());
				
                // go till we find next point with addresses
                
                if (pt.hasAddressSet()) 
                {
                    Vector<AddressRange> ar = allAddressRangesOnSegment(pf, pt);

					//if ( MapSearch.debug ) Engine.out("Addresses=" + ar.size());
					
                    for (int j = 0; j < ar.size(); ++j) 
                    {
                        AddressRange a = (AddressRange) ar.elementAt(j);
						
						
						
						//if ( MapSearch.debug ) Engine.out(a + " target=" + address + " result=" + (a.containsAddress(address)));
						
						
						
                        if (a.containsAddress(address)) 
                        {
                            StreetRelativeLocation w = new StreetRelativeLocation();
                            
                            w.beforePoint = pf;
                            w.afterPoint = pt;
                            
                            if (a.left) 
                            	w.sideOfStreet = SideLeft;
                            else 
                            	w.sideOfStreet = SideRight;
                            
                            int min, max;
                            
                            if (a.from > a.to) 
                            {
                                max = a.from;
                                min = a.to;
                            } 
                            else 
                            {
                                max = a.to;
                                min = a.from;
                            }
                            
							int s = max - min;
							
							if ( s < 1 )
								s = 1;
							
                            if (a.from > a.to)
                            	w.percentAlongSegment = (100 * (a.from - address)) / s;
                            else
                            	w.percentAlongSegment = (100 * (address - a.from)) / s;
                            
                            if (w.percentAlongSegment < 2) w.percentAlongSegment = 2;
                            if (w.percentAlongSegment > 98) w.percentAlongSegment = 98;

                            return w;
                        }
                    }

                    break;
                }
            }
        }

        return null;
    }

    private Vector<AddressRange> allAddressRangesOnSegment(FeaturePoint from, FeaturePoint to)

    {
        Vector<AddressRange> ar = new Vector<AddressRange>();

        int j = 0;

        for (int i = 0; i < 10; ++i) 
        {
            int fl = from.addressSet.fromLeft[i];
            int fr = from.addressSet.fromRight[i];
            
            int tl = to.addressSet.toLeft[i];
            int tr = to.addressSet.toRight[i];

			
			//if ( MapSearch.debug ) 
			//	if ( fl >= 0 || tl >= 0 || fr >= 0 || tr >= 0 ) 
			//		Engine.out("Addresses " + i + " (" + fl + " " + tl + ") (" + fr + " " + tr + ")");
			
			
            if (fl > 0 && tl > 0) 
            {
                if (even(fl) == even(tl))
                	ar.addElement(new AddressRange(fl, tl, even(fl), true));
				else
				{
					//Engine.out("Non-coherent address " + fl + " " + tl);
					ar.addElement(new AddressRange(fl, tl, even(fl), true));
				}
            }

            if (fr > 0 && tr > 0) 
            {
                if (even(fr) == even(tr))
                	ar.addElement(new AddressRange(fr, tr, even(fr), false));
				else
				{
					ar.addElement(new AddressRange(fr, tr, even(fr), false));
					//Engine.out("Non-coherent address " + fr + " " + tr);
				}
            }
        }

        return ar;
    }
    
    /*

    private FeaturePoint closetPointToCoordinates(WorldCoordinates wc) throws MapopolisException

    {
        FeaturePoint best = null;
        int min = 999999999;

        Vector p = allPoints(false, false);

        for (int i = 0; i < p.size() - 1; ++i)

        {
            FeaturePoint pf = (FeaturePoint) p.elementAt(i);
            FeaturePoint pt = (FeaturePoint) p.elementAt(i + 1);

            int distance = distanceToSegment(pf, pt, wc).distance;

            if (distance < min)
                best = pf;
        }

        return best;
    }
    
    */

    private PointAndDistance distanceToSegment(FeaturePoint fp0, FeaturePoint fp1, int wx, int wy) throws MapopolisException

    {
        int Z = 1000, Far = 10000;
        int p, r, xi, yi;
        int scx, scy, dx, dy, adx, ady;

        //WorldCoordinates wc0 = fp0.getWorldCoordinates();

		int x0 = fp0.getX();
		int y0 = fp0.getY();

        //WorldCoordinates wc1 = fp1.getWorldCoordinates();

		int x1 = fp1.getX();
		int y1 = fp1.getY();

        int cx = wx;
        int cy = wy;

        //System.out.println(x0 + " " + y0 + " " + x1 + " " + y1 + " " + cx + " " + cy);
                
        int intx, inty;

        if ((cx < x0 - Far && cx < x1 - Far)
                || (cx > x0 + Far && cx > x1 + Far)
                || (cy < y0 - Far && cy < y1 - Far)
                || (cy > y0 + Far && cy > y1 + Far)) {
            intx = (x0 + x1) / 2;
            inty = (y0 + y1) / 2;

            return new PointAndDistance(intx, inty, Far);
        }

        scx = cx;
        scy = cy;

        cx -= scx;
        x0 -= scx;
        x1 -= scx;

        cy -= scy;
        y0 -= scy;
        y1 -= scy;

        dx = x1 - x0;
        dy = y1 - y0;

        adx = Math.abs(dx);
        ady = Math.abs(dy);

        if (dx == 0 && dy == 0) 
        {
            // r = absi(x0 - cx) + absi(y0 - cy);

            xi = x0;
            yi = y0;
        } 
        else if (adx * Z < ady) 
        {
            // r = absi(x0 - cx);

            xi = x0;
            yi = cy;
        } 
        else if (ady * Z < adx) 
        {
            // r = absi(y0 - cy);

            xi = cx;
            yi = y0;
        } 
        else 
        {
            int m0 = (Z * dy) / dx;
            int m1 = (-Z * dx) / dy;

            p = x0;
            p = p * m0;

            if (Math.abs(p) > 1000000000L) 
            {
                //printLong(cx, 10, 10); printLong(cy, 100, 10);
                //printLong(x0, 10, 30); printLong(y0, 100, 30);
                //printLong(x1, 10, 50); printLong(y1, 100, 50);

                //WDraw("point overflow", 20, 20);

                intx = (x0 + x1) / 2;
                inty = (y0 + y1) / 2;

                return new PointAndDistance(intx, inty, Far);
            }

            p = p / Z;

            int b0 = y0 - p;

            p = cx;
            p = p * m1;

            if (Math.abs(p) > 1000000000L) 
            {
                //WDraw("point overflow", 20, 20);

                intx = (x0 + x1) / 2;
                inty = (y0 + y1) / 2;

                return new PointAndDistance(intx, inty, Far);
            }

            p = p / Z;

            int b1 = cy - p;

            if (adx > ady) {
                xi = ((b1 - b0) * Z + ((m0 - m1) / 2)) / (m0 - m1);
                yi = (m0 * xi) / Z + b0;
            } else {
                yi = (m1 * b0 - m0 * b1 + ((m1 - m0) / 2)) / (m1 - m0);
                xi = ((yi - b0) * Z + (m0 / 2)) / m0;
            }
        }

        //c:;

        int out = 0;

        // the calculated intersection is not on the segment

        if ((xi < x0 && xi < x1) || (xi > x0 && xi > x1)
                || (yi < y0 && yi < y1) || (yi > y0 && yi > y1)) {
            int p0 = Math.abs(x0 - cx) + Math.abs(y0 - cy);
            int p1 = Math.abs(x1 - cx) + Math.abs(y1 - cy);

            if (p0 < p1) {
                xi = x0;
                yi = y0;
            } else {
                xi = x1;
                yi = y1;
            }

            out = 1;
        }

        r = Utilities.hypot(Math.abs(xi - cx), Math.abs(yi - cy));

        if (r > 30000) r = 30000;

        intx = xi + scx;
        inty = yi + scy;

        return new PointAndDistance(intx, inty, r);
    }

	public Box boundingBox() throws MapopolisException

    {
		if ( boundingBox != null )
			return boundingBox;
		
		boundingBox = new Box(999999999, 999999999, -999999999, -999999999);
		
		Vector<FeaturePoint> points = allPoints(false, false);
		
		for ( int i = 0; i < points.size(); ++i )
		{
			FeaturePoint fp = (FeaturePoint) points.elementAt(i);
			boundingBox.stretch(fp.getX(), fp.getY());
		}
		
		return boundingBox;
		
		/*
        int index = offsetInRecord + FeatureStartToPointsStart;

        while (true) 
        {
            FeaturePoint fp = new FeaturePoint(mapFeatureRecord, index, true, false, false);

			
			
			
			
            if (fp.isLast)
				break;

            index = fp.nextIndex;
        }
		*/
		
		
		
		
		/*
    	if ( boundingBox == null )
    	{
	        byte[] buffer = mapFeatureRecord.getAsBytes();
	        int index = offsetInRecord;
	        
	        boundingBox = new Box();
	
	        boundingBox.x0 = (IO.get2(buffer, index + 2) << 8) + mapFeatureRecord.mapFile.X0;
	        boundingBox.y0 = (IO.get2(buffer, index + 4) << 8) + mapFeatureRecord.mapFile.Y0;
	        boundingBox.x1 = (IO.get2(buffer, index + 6) << 8) + mapFeatureRecord.mapFile.X0;
	        boundingBox.y1 = (IO.get2(buffer, index + 8) << 8) + mapFeatureRecord.mapFile.Y0;
    	}

    	return boundingBox;
		*/
    }

	/*
	private static void scanAllPointsx(MapFeatureRecord mapFeatureRecord, int index) throws MapopolisException

    {
        index += FeatureStartToPointsStart;

        while (true) 
        {
            FeaturePoint fp = new FeaturePoint(mapFeatureRecord, index, true, false, true);

            if (fp.isLast) break;

            index = fp.nextIndex;
        }
    }
	*/

	public boolean isRailroad() throws MapopolisException
    {
    	return getFeatureType() == 1;
    }

    public boolean isLandmark() throws MapopolisException
    {
    	return getFeatureType() == 2;
    }

    public boolean isWater() throws MapopolisException
    {
    	return getFeatureType() == 3;
    }

    public boolean isFacility() throws MapopolisException
    {
    	return getFeatureType() == 4;
    }

    public boolean isGreen() throws MapopolisException
    {
    	return getFeatureType() == 5;
    }

    public boolean isCity() throws MapopolisException 
    {
    	return getFeatureType() == 6;
    }

    public boolean isCounty() throws MapopolisException
    {
    	return getFeatureType() == 7;
    }

    public boolean isStreet() throws MapopolisException
    {
    	return getFeatureType() == 8;
    }
	
    public boolean isExit() throws MapopolisException
    
    {
    	if ( !firstPoint().isOneWay() )
    		return false;
    	else
    	{
    		String s = getName();
    		return ( s.startsWith("EXIT") || s.startsWith("TO ") );
    	}
    }
	
	public boolean isFeatureTypeSearchingFor(int searchType) throws MapopolisException

    {
    	int type = getFeatureType();
    	
        if (searchType == MapSearch.SearchAddressType) 
        {
            if ( type != MapFeature.Street ) return false;
        } 
        else if (searchType == MapSearch.SearchStreetType) 
        {
            if ( type != MapFeature.Street ) return false;
        } 
        else if (searchType == MapSearch.SearchIntersectionType) 
        {
            if ( type != MapFeature.Street ) return false;
        } 
        else if (searchType == MapSearch.SearchCityType) 
        {
            if ( type != MapFeature.City ) return false;
        } 
        else if (searchType == MapSearch.SearchPointLocationType) 
        {
            if ( type != MapFeature.Landmark ) return false;
        }

        return true;
    }

	public int getFeatureType() throws MapopolisException

    {
		//if ( myCachedCopy != null )
		//	return myCachedCopy.featureType;
		
    	byte[] buffer = mapFeatureRecord.getAsBytes();
        return (IO.get1(buffer, offsetInRecord + MapFile.FeatureTypeOffset) & 0x3f);
    }
	
    public int getStreetLevel() throws MapopolisException

    {
		//if ( myCachedCopy != null )
		//	return myCachedCopy.streetLevel;
		
    	byte[] buffer = mapFeatureRecord.getAsBytes();
        return (IO.get1(buffer, offsetInRecord + MapFile.LevelOffset) & 7);
    }

	private static boolean even(int n)
	{
		return (n & 1) == 0;
	}

    @Override
	public String toString()
		
	{
		try
		
		{
			return friendlyName() + " type: " + getStreetLevel() + " feature:" + getFeatureType();
		}
		
		catch (MapopolisException e)
			
		{
			Engine.out(e.toString()); e.printStackTrace();
			return "Name";	
		}
	}
}