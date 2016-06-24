
package com.mapopolis.viewer.core;



import java.util.Vector;

import com.mapopolis.viewer.engine.Engine;
import com.mapopolis.viewer.engine.MapopolisException;
import com.mapopolis.viewer.route.Overlaps;
import com.mapopolis.viewer.route.StreetSegment;
import com.mapopolis.viewer.utils.Box;
import com.mapopolis.viewer.utils.SearchArea;
import com.mapopolis.viewer.utils.Utilities;
import com.mapopolis.viewer.utils.WorldCoordinates;

public class FeaturePoint

{
    public static final int IncreasingIn = 1;
    public static final int DecreasingIn = 2;
    public static final int IncreasingOut = 3;
    public static final int DecreasingOut = 4;

    public static final int MaxOverlappingNodes = 16;
    public static final int Bdry = 0x80;
    public static final int Round = 0x40;
    public static final int TravelIncreasing = 0x20;
    public static final int TravelDecreasing = 0x10;
    public static final int Avoid = 0x08;
    public static final int Toll = 0x04;
	public static final int BoundaryNodeAssociationCode = 117;
	
    int dx, dy;

	private int x = 0;
	private int y = 0;

    //public WorldCoordinates wc;

    public boolean isFirst;
    public boolean isLast;
    public boolean isNode;

	boolean cityChange;
	int cityOffset;
	
    public int descriptor;
    public int speedClass;
	
	public boolean tollRoad;
	public boolean localStreet;
	public boolean longHaul;

    public int seqNumber;
    public int distance;

    public Overlaps overlaps;

    public MapFeatureRecord myMapFeatureRecord;
    private MapFeature containingFeature = null;

    public int idx;
	public int nextIndex;

    private int forwardIndex, backwardsIndex;

	public FeaturePoint nextNode;
	public FeaturePoint lastNode;

    AddressSet addressSet;
	Vector<SearchArea> myCities;
	
    public boolean equals(FeaturePoint fp)

    {
        return (myMapFeatureRecord == fp.myMapFeatureRecord) && (idx == fp.idx);
    }
    
	/*
    public FeaturePoint(int o, MapFile map, boolean fwd, boolean buildAddressInfo, boolean buildOverlapInfo) throws MapopolisException
	
	{
        this(map.getMapFeatureRecords()[((o >> 16) & 0x00000fff)], (o & 0x0000ffff), fwd, buildAddressInfo, buildOverlapInfo);


		if (fp != null)
		{
			Engine.out("found fp " + fp.idx + " " + this.idx);
		}
	}
	*/

	/*
	public static FeaturePoint getFeaturePoint(MapFeatureRecord mfr, int index, boolean fwd, boolean buildAddressInfo, boolean buildOverlapInfo, int xyz) throws MapopolisException
	{
		// this can only be called when building the points initially
		// after that only MapFeature.allPoints or the second getFeaturePoint() should be used

		return new FeaturePoint(mfr, index, fwd, buildAddressInfo, buildOverlapInfo);

		//return getTheFeaturePoint(mfr, index, fwd, buildAddressInfo, buildOverlapInfo);
	}
	*/

	public static FeaturePoint getFeaturePoint(int o, MapFile map, boolean fwd, boolean buildAddressInfo, boolean buildOverlapInfo) throws MapopolisException
	{
		MapFeatureRecord mfr = map.getMapFeatureRecords()[((o >> 16) & 0x00000fff)];
		int index = (o & 0x0000ffff);
		return new FeaturePoint(mfr, index, fwd, buildAddressInfo, buildOverlapInfo);
	}

	/*
	private static FeaturePoint getTheFeaturePoint(MapFeatureRecord mfr, int index, boolean fwd, boolean buildAddressInfo, boolean buildOverlapInfo) throws MapopolisException
	{
		
		
		
		if (!Engine.LargeDevice)
		{
			return new FeaturePoint(mfr, index, fwd, buildAddressInfo, buildOverlapInfo);
		}
		else
		{
			FeaturePoint fp = mfr.getPointFeatureCache(index);

			if (fp != null)
				return fp;
			else
			{
				// this is a newly accessed point so lets initialize its mapfeature

				getContainingFeature(mfr, index);

				// try again

				fp = mfr.getPointFeatureCache(index);

				if (fp != null)
					return fp;
				else
				{
					//if (MapViewFrame.loop > 1) Engine.out("did not find " + index);
					//return new FeaturePoint(mfr, index, fwd, buildAddressInfo, buildOverlapInfo);

					Engine.out("Could not find feature point cached"); 
					int a = 0; int y = 2 / a;
					return null;
				}
			}
		}
		
	}
	*/
	
	public FeaturePoint(MapFeatureRecord mfr, int indexIntoFeatureRecord, boolean fwd, boolean buildAddressInfo, boolean buildOverlapInfo) throws MapopolisException

    {
		byte[] buffer = mfr.getAsBytes();
        myMapFeatureRecord = mfr;

        int index = idx = indexIntoFeatureRecord;
		int addressLen = 0;
		int addressBlockIndex = -1;
		boolean simple;
        boolean hasSeq = false;
		int noverlaps = 0;
		int cityLen = 0;
		
		int comp;
		int start;
		int slen;
		int len;
		int lastlen;
		int offset;
				
        if ((buffer[index + 0] & 0x80) != 0) 
        {
			simple = false;
			
			comp = (buffer[index + 0] & 3);
			
			if ( comp == 0 )
			{
				comp = 3;
				isFirst = true;
				lastlen = 0;
			}
			else
			{
				isFirst = false;
				
				byte b = buffer[index - 1];
				
				if ( (b & 128) != 0 )
					lastlen = b & 127;
				else
					lastlen = 2;
			}

			start = 1;

			isLast = ((buffer[index + 0] & 32) != 0);
            isNode = ((buffer[index + 0] & 0x08) != 0);
            hasSeq = ((buffer[index + 0] & 0x04) != 0);

			// calculate number of overlap nodes
			
			if ( isNode ) noverlaps = (buffer[index + comp * 2 + 2] >> 4);
			if ( noverlaps < 0 ) noverlaps += 16;

			// calculate length
		
			//int linkStart = 1 + comp * 2;

			// calculate offset to address block (if exists)
			
			if ( isNode )
				offset = 1 + comp * 2 + 4 + (hasSeq ? 3 : 0) + noverlaps * 4;
			else
				offset = 1 + comp * 2;			
			
			if ((buffer[index + 0] & 0x10) != 0) 
            {
				addressLen = (buffer[index + offset] & 31) + 1;
				addressBlockIndex = index + offset;
				//if ( isNode )
				//	addressBlockIndex = index + offset;//1 + comp * 2 + 4 + (hasSeq ? 3 : 0) + noverlaps * 4;
				//else
				//	addressBlockIndex = index + offset;//1 + comp * 2;
            }
			
			offset += addressLen;
			
			cityChange = ((buffer[index + 0] & 0x40) != 0);
						
			if ( cityChange )
			{
				cityOffset = offset;
				cityLen = ((IO.get2(buffer,index + offset)>>13) & 7) * 2;
			}
			else
				cityOffset = -1;
			
			len = offset + cityLen + 1;

			//if ((buffer[index + 0] & 0x10) != 0)
			//{
			//	if ( isNode )
			//	{
			//		//int p = (buffer[index + 2 + comp * 2] >> 4);
			//		addressBlockIndex = index + 1 + comp * 2 + 4 + (hasSeq ? 3 : 0) + noverlaps * 4;
			//	}
			//	else
			//	{
			//		addressBlockIndex = index + 1 + comp * 2;
			//	}
			//}
			
			
			//if ( addressBlockIndex > 0 )
			//Engine.out(addressLen + " " + (addressBlockIndex - index) + " " + ((buffer[index + offset] & 0x000000ff) >> 6));
        } 
        else 
        {
			simple = true;
            
			len = 2;
			
			byte b = buffer[index - 1];
				
			if ( (b & 128) != 0 )
				lastlen = b & 127;
			else
				lastlen = 2;
			
			isFirst = false;
			
            //lastlen = buffer[index + 0] & 0x7f;

            comp = 1;
            start = 1;
            
            isLast = false;
            isNode = false;
            hasSeq = false;
        }

        if (comp == 1) 
        {
			if ( simple )
			{
				dx = (IO.get1(buffer, index + 0))<<1;
				dy = (IO.get1(buffer, index + 1))<<1;
			
				if ( (dx & 0x80) != 0 ) dx |= 0xffffff00;
				if ( (dy & 0x80) != 0 ) dy |= 0xffffff00;			
			}
			else
			{
				dx = IO.get1(buffer, index + start) - 128;
				dy = IO.get1(buffer, index + start + 1) - 128;
			}
        } 
        else if (comp == 2) 
        {
            dx = IO.get2(buffer, index + start) - 32768;
            dy = IO.get2(buffer, index + start + 2) - 32768;
        } 
        else if (comp == 3) 
        {
            dx = IO.get3(buffer, index + start) - 8388608;
            dy = IO.get3(buffer, index + start + 3) - 8388608;
        } 
        else 
        {
            dx = 0;
            dy = 0;
        }
				
		//Engine.out("Delta: " + dx + " " + dy + " simple=" + simple + " comp=" + comp);
		
        seqNumber = 0;

        if (isNode) 
        {
            int linkStart = 1 + comp * 2;

            if ((buffer[index + 0] & 4) != 0) slen = 3;
            else slen = 0;

			descriptor = buffer[index + linkStart];
            speedClass = (buffer[index + linkStart + 1] & 7);
			
        		boolean t5 = ((buffer[index + linkStart + 1] & 8) != 0);
			boolean tl = (descriptor & Toll) != 0;

			if ( tl )
			{
				if ( t5 )
				{
					tollRoad = false;
					longHaul = true;
					localStreet = false;
				}
				else
				{
					tollRoad = true;
					longHaul = true;
					localStreet = false;
				}
			}
			else
			{
				if ( t5 )
				{
					tollRoad = false;
					longHaul = false;
					localStreet = true;
				}
				else
				{
					tollRoad = false;
					longHaul = false;
					localStreet = false;
				}
			}

			distance = IO.get2(buffer, index + linkStart + 2 + slen);

            if ( (distance & 0x8000) != 0 )
    			distance = ((distance & 0x7fff) << 5);

            if ( distance < 0 ) 
            	throw new MapopolisException("Invalid distance in FeaturePoint");
			else if ( distance == 0 )
				if ( !isLast )
				{
					distance = 10;
					
					//Engine.out("Invalid zero distance in FeaturePoint ");
					//String s = "index=" + index + " x=" + x + " y=" + y;
					//s += " " + getStreetName() + " ";
    				//s += " distance=" + distance + " speed=" + speedClass + " seqNumber=" + seqNumber;
					//Engine.out(s);
					//throw new MapopolisException("Invalid zero distance in FeaturePoint");
				}
			
			if ( buildOverlapInfo )
            {
            	overlaps = new Overlaps();
            	
            	overlaps.overlapCount = (buffer[index + linkStart + 1] >> 4);
				
				if ( overlaps.overlapCount < 0 ) 
					overlaps.overlapCount += 16;

				if (overlaps.overlapCount > MaxOverlappingNodes || overlaps.overlapCount < 0) 
	            {
	                //Engine.out("throwing exception");
					
	                throw new MapopolisException("Invalid overlap count "
	                        + overlaps.overlapCount + " " + buffer[index + linkStart + 1]
	                        + " " + dx + " " + dy + " " + distance);
	            }
	            
	            overlaps.overlapNodes = new int[overlaps.overlapCount];
	
	            for (int i = 0; i < overlaps.overlapCount; ++i) 
	            	overlaps.overlapNodes[i] = IO.get4(buffer, index + linkStart + 4 + slen + 4 * i);
				
				//Engine.out("overlaps=" + overlaps.overlapCount + " " + (buffer[index + linkStart + 1] >> 4));
            }
            
            if (hasSeq) 
            {
                seqNumber = IO.get3(buffer, index + 2 * comp + 3);
            }
        }

		/*
        Engine.out(
				   (isFirst?"1st":"") + " " + 
				   (isLast?"last":"") + " " + 
				   (isNode?"node":"") + " " +
				   "dx=" + dx + " " + 
				   "dy=" + dy + " " + 
				   "addrLen=" + addressLen + " " +
				   "cityLen=" + cityLen + " " +
				   "len=" + len + " " +
				   "dist=" + distance + " " +
				   "overlaps=" + noverlaps + " " +
				   "seq=" + seqNumber);
		*/

        if ( isLast )
        	forwardIndex = -1;
        else
        	forwardIndex = index + len;
        
        if ( isFirst )
        	backwardsIndex = -1;
        else
        	backwardsIndex = index - lastlen;
        
        if ( fwd )
        	nextIndex = forwardIndex;
        else
        	nextIndex = backwardsIndex;
        
        /*
        
        if (fwd) 
        {
            if (isLast) nextIndex = index + 0;
            else nextIndex = index + len;
        } 
        else 
        {
            if (isFirst) nextIndex = index + 0;
            else nextIndex = index - lastlen;
        } 

        */
        
		if ( addressBlockIndex >= 0 )
			if ( buildAddressInfo )
			{
			    addressSet = new AddressSet();

			    for (int i = 0; i < 10; ++i) 
			    {
			        addressSet.fromLeft[i] = address(buffer, addressBlockIndex, true, true, i);
			        addressSet.fromRight[i] = address(buffer, addressBlockIndex, true, false, i);
			        addressSet.toLeft[i] = address(buffer, addressBlockIndex, false, true, i);
			        addressSet.toRight[i] = address(buffer, addressBlockIndex, false, false, i);
					
					//Engine.out(addressSet.fromLeft[i] + " " + addressSet.toLeft[i] + " --- " + addressSet.fromRight[i] + " " + addressSet.toRight[i]);
			    }
			}
		
		//Engine.out("Indices " + forwardIndex + " " + backwardsIndex);
    }
	
	public boolean containsCity(int city, Vector<SearchArea> r) throws MapopolisException
    
    {
		if ( myCities == null )
			myCities = myMapFeatureRecord.mapFile.cities;
		
		//int comp, start, off;

        byte[] buffer = myMapFeatureRecord.getAsBytes();
        int index = idx;

        //if ( (buffer[index + 0] & 128) != 0 && (buffer[index + 0] & 64) != 0 )
		
		if ( cityChange )
		{
			/*
			comp = (buffer[index + 0] & 3);
			off = (3 + comp * 2);
			
			// is it a node?
			
			if ( (buffer[index + 0] & 0x08) != 0 )
			{
				off += (4 + ((buffer[index + 0] & 0x04) != 0 ? 3 : 0));
				off += ((buffer[index + 4 + comp * 2] >> 4) * 4);
			}
			
			// address block?
			
			if ( (buffer[index + 0] & 0x10) != 0 )
			{
				int a = buffer[index + off];
				off += ((a & 31) + 1);
			}
			*/
			// cities - up to 7
			
			int offset = cityOffset;
			
			int n = (IO.get2(buffer, index + offset) >> 13);
			
			// System.out.println(this + " number of cities " + n + " " + IO.get2(buffer, index + off) + " " + seqNumber);
			
			//Engine.out("N CITIES=" + n);
			
			for ( int i = 0; i < n; ++i )
			{
				int c = IO.get2(buffer, index + offset) & 0x1fff;
				
				//if ( c > 0 && c < myMapFeatureRecord.mapFile.cities.size() )
				//{
				//	String name = ((SearchArea) myMapFeatureRecord.mapFile.cities.elementAt(c)).name;
				//	//Engine.out("City=" + c + " " + name);
				//}
				
				if ( c == city )
					return true;
				
				if ( r != null )
					r.addElement(myCities.elementAt(c));
				
				//if ( r != null ) Engine.out(c + " " + myCities.size());

				offset += 2;
			}
		}

		return false;
    }

	public boolean containsCity(String city, Vector <SearchArea>r) throws MapopolisException
    
    {
		if ( myCities == null )
			myCities = myMapFeatureRecord.mapFile.cities;

        byte[] buffer = myMapFeatureRecord.getAsBytes();
        int index = idx;

        //if ( (buffer[index + 0] & 128) != 0 && (buffer[index + 0] & 64) != 0 )
		
		if ( cityChange )
		{
			/*
			comp = (buffer[index + 0] & 3);
			off = (3 + comp * 2);
			
			// is it a node?
			
			if ( (buffer[index + 0] & 0x08) != 0 )
			{
				off += (4 + ((buffer[index + 0] & 0x04) != 0 ? 3 : 0));
				off += ((buffer[index + 4 + comp * 2] >> 4) * 4);
			}
			
			// address block?
			
			if ( (buffer[index + 0] & 0x10) != 0 )
			{
				int a = buffer[index + off];
				off += ((a & 31) + 1);
			}
			*/
			// cities - up to 7
			
			int offset = cityOffset;
			
			int n = (IO.get2(buffer, index + offset) >> 13);
			
			// System.out.println(this + " number of cities " + n + " " + IO.get2(buffer, index + off) + " " + seqNumber);
			
			//Engine.out("N CITIES=" + n);
			
			for ( int i = 0; i < n; ++i )
			{
				int c = IO.get2(buffer, index + offset) & 0x1fff;
				
				//if ( c > 0 && c < myMapFeatureRecord.mapFile.cities.size() )
				//{
				//	String name = ((SearchArea) myMapFeatureRecord.mapFile.cities.elementAt(c)).name;
				//	//Engine.out("City=" + c + " " + name);
				//}
				
				if ( c >= 0 && c < myCities.size() )
				{
					//Engine.out("===" + city + "===" + ((SearchArea) myCities.elementAt(c)).name + "===");
					
					if ( ((SearchArea) myCities.elementAt(c)).name.equals(city) )
						return true;
					
					if ( r != null )
						r.addElement(myCities.elementAt(c));
					
				}
				
				offset += 2;
			}
		}

		return false;
    }
    
    public FeaturePoint getNextNodePoint(boolean fwd) throws MapopolisException

    {
		/*
		if (fwd)
		{
			if (nextNode != null)
				return nextNode;
		}
		else
		{
			if (lastNode != null)
				return lastNode;
		}
		
		//if (MapViewFrame.loop > 1)
		//	Engine.out("node pointers null");
		
		// this should never happen on a large device

		if (Engine.LargeDevice)
		{
			Engine.out("Could not find next node"); 
			int a = 0; int y = 2 / a;
		}
		*/
		
		if ( x == 0 && y == 0 )
			setWorldCoordinates();

		int tx = 0;
    	int ty = 0;

		if ( !fwd )
		{
			tx -= dx;
			ty -= dy;
		}

    	int n;

    	FeaturePoint fp = this;
    	
    	while ( true )
    	{
        	if ( fwd )
        		n = fp.forwardIndex;
        	else
        		n = fp.backwardsIndex;
    		
    		fp = new FeaturePoint(myMapFeatureRecord, n, true, false, true);

    		if ( fwd )
			{
				tx += fp.dx;
				ty += fp.dy;
			}
    		else
			{
				if ( !fp.isNode )
				{
					tx -= fp.dx;
					ty -= fp.dy;
				}
			}
    			
    		if ( fp.isNode )
    		{
				fp.x = x + tx;
				fp.y = y + ty;

				return fp;
    		}
    	}
    }
    
    public void setSeqNumber() throws MapopolisException
    
    {
    	if ( seqNumber <= 0 )
    	{
	    	FeaturePoint fp;
	    	
	    	if ( isLast )
	    	{
	    		fp = getNextNodePoint(false);
	    		seqNumber = fp.seqNumber + 1;
	    	}
	    	else
	    	{
	    		fp = getNextNodePoint(true);
	    		seqNumber = fp.seqNumber - 1;
	    	}
    	}
    }
    
    //private void setWorldCoordinates() throws MapopolisException
	    
    //{
    //	if ( wc != null )
    //		return;
    	
	//	Vector v = getContainingFeature().allPoints(false, false);
		
		/*
		for ( int i = 0; i < v.size(); ++i )
		{
			FeaturePoint z = (FeaturePoint) v.elementAt(i);
			
			if ( z.equals(this) )
			{
				wc = new WorldCoordinates(z.x, z.y);

				// System.out.println(wc.x + " " + wc.y);
				
				return;
			}
		}
		*/
		
		//throw new MapopolisException("node not found - setWorldCoordinates - FeaturePoint");
    //}

	public int getX() throws MapopolisException
	{
		if (x == 0 && y == 0)
			setWorldCoordinates();
		return x;
	}

	public int getY() throws MapopolisException
	
	{
		if (x == 0 && y == 0)
			setWorldCoordinates();
		return y;
	}

	public void setX(int n)
	{
		x = n;
	}

	public void setY(int n)
	{
		y = n;
	}
	
	private void setWorldCoordinates() throws MapopolisException
	
	{
		if ( x == 0 && y == 0 )
		{
			MapFeature f = getContainingFeature();
			FeaturePoint fp = f.getSamePoint(this);
			
			x = fp.x;
			y = fp.y;
		}		
	}

	public WorldCoordinates getWorldCoordinates() throws MapopolisException
	{
		setWorldCoordinates();
		return new WorldCoordinates(x, y);
	}

    public void testCoordinates() throws MapopolisException
    
    {
		Vector<FeaturePoint> v = getContainingFeature().allPoints(false, false);
		
		boolean ok = false;
		
		for ( int i = 0; i < v.size(); ++i )
		{
			FeaturePoint z = (FeaturePoint) v.elementAt(i);
			if ( z.equals(this) )
			{
				if ( z.x == x && z.y == y )
				{
					ok = true;
					break;
				}
			}
		}
    	
		if ( !ok )
			throw new MapopolisException("invalid coordinates - FeaturePoint");
    }
        
    public String getStreetName() throws MapopolisException
    
    {
    	return getContainingFeature().getName();    	
    }
    
    /*
     * 
     * static void getCoordinates(MapFeatureRecord mfr, byte[] buffer, int
     * indexIntoFeatureRecord, boolean fwd, boolean buildAddressInfo) throws
     * MapopolisException
     *  { int comp, start, slen, len, lastlen;
     * 
     * //int index = idx = indexIntoFeatureRecord;
     * 
     * if ((buffer[index + 0] & 0x80) != 0) { len = buffer[index + 1]; lastlen =
     * buffer[index + 2];
     * 
     * comp = (buffer[index + 0] & 3); start = 3;
     * 
     * isLast = ((buffer[index + 0] & 32) != 0); } else { len = 3; lastlen =
     * buffer[index + 0] & 0x7f;
     * 
     * comp = 1; start = 1;
     * 
     * isLast = false; }
     * 
     * if (comp == 1) { dx = IO.get1(buffer, index + start) - 128; dy =
     * IO.get1(buffer, index + start + 1) - 128; } else if (comp == 2) { dx =
     * IO.get2(buffer, index + start) - 32768; dy = IO.get2(buffer, index +
     * start + 2) - 32768; } else if (comp == 3) { dx = IO.get3(buffer, index +
     * start) - 8388608; dy = IO.get3(buffer, index + start + 3) - 8388608; }
     * else { dx = 0; dy = 0; }
     * 
     * //System.out.println(isFirst + " " + isLast + " " + isNode + " " + dx + // " " +
     * dy + " " + seqNumber);
     * 
     * if (fwd) { if (isLast) nextIndex = index + 0; else nextIndex = index +
     * len; } }
     *  
     */

    public boolean hasAddressSet()

    {
        return (addressSet != null);
    }

	private static MapFeature getContainingFeature(MapFeatureRecord mfr, int index) throws MapopolisException

	{
		FeaturePoint fp;

		while (true)
		{
			fp = new FeaturePoint(mfr, index, false, false, false);

			if (fp.isFirst)
				break;

			index = fp.nextIndex;
		}

		return new MapFeature(mfr, fp.idx - MapFile.FeatureStartToPointsStart);
	}

    public MapFeature getContainingFeature() throws MapopolisException
	
	{
    	if ( containingFeature == null )
    	{
			/*
			FeaturePoint fp;
			int index = idx;

			while (true)
			{
				fp = new FeaturePoint(myMapFeatureRecord, index, false, false, false);

				if (fp.isFirst)
					break;

				index = fp.nextIndex;
			}

    		//FeaturePoint f = getFirstPoint(myMapFeatureRecord, idx); //MapUtilities.getFirstPoint(this);
			*/

			containingFeature = getContainingFeature(myMapFeatureRecord, idx); // new MapFeature(fp.myMapFeatureRecord, fp.idx - MapFeature.FeatureStartToPointsStart);
    	}
    	
    	return containingFeature;
	}
	
	/*
    private static FeaturePoint getFirstPoint(MapFeatureRecord mapFeatureRecord, int index) throws MapopolisException

    {
        FeaturePoint fp;

        while (true) 
        {
            fp = new FeaturePoint(mapFeatureRecord, index, false, false, false);

            if (fp.isFirst) return fp;

            index = fp.nextIndex;
        }
    }
	*/

	private int address(byte[] buffer, int idx, boolean from, boolean left, int n) throws MapopolisException

    {
        int f = ((int) buffer[idx]) & 0x000000ff;

        int k = (f >> 6);

        //if ( (f & 128) != 0 ) k += 2;
        //if ( (f & 64) != 0 ) k += 1;

        //if (f < 0) System.out.println(f + " " + k);

        boolean leftEven = (f & 0x20) != 0;
        int i, j;
        int r;

        if (k == 0) 
        {
            if (n != 0) return -1;

            r = IO.get2(buffer, idx + 1);

            if (r == 0) { throw new MapopolisException("Invalid address block"); }

            if ((leftEven && left) || (!leftEven && !left)) 
            {
                r = r & 0xfffffffe;
                if (r == 0) r = 2;
            } 
            else 
            	r = r | 1;

            return r;
        } 
        else if (k == 1) 
        {
            //System.out.print(" 1");

            if (n != 0) return -1;

            if (left) i = 1;
            else i = 3;

            r = IO.get2(buffer, idx + i); //(&(getPointerFromRealAddress(block)[i]));

            if (r == 0) { throw new MapopolisException("Invalid address block"); }

            if ((leftEven && left) || (!leftEven && !left)) {
                r = r & 0xfffffffe;
                if (r == 0) r = 2;
            } else r = r | 1;

            return r;
        } 
        else if (k == 2) 
        {
        	///////////////////////////////////////////////
            // TODO there may be a bug here
            // for instance on Euclid Ave this is generated
            //
            // -1 19500 -1 -1
            // -1 -1 -1 19598
        	///////////////////////////////////////////////
        	
            int x1 = ((int) buffer[idx + 1]) & 0x000000ff;

            int nfrom = (x1 >> 4);
            int nto = (x1 & 15);

            //System.out.print(" 2 ");//+ x + " " + nfrom + " " + nto + " ===

            boolean lbit;

            if (from) 
            {
                for (i = 0, j = 0; i < nfrom; ++i) 
                {
                    r = IO.get3(buffer, idx + 2 + i * 3);//get3Byte(&(getPointerFromRealAddress(block)[2
                                                         // + i * 3]));
                    lbit = (r >> 23) != 0;

                    if (r == 0) 
                    	throw new MapopolisException("Invalid address block"); 

                    if ((lbit && left) || (!lbit && !left)) 
                    {
                        if (j == n) return r & 0x007fffff;
                        else j++;
                    }
                }
            } 
            else 
            {
                for (i = 0, j = 0; i < nto; ++i) 
                {
                    r = IO.get3(buffer, idx + 2 + nfrom * 3 + i * 3);//get3Byte(&(getPointerFromRealAddress(block)[2
                                                                     // + nfrom
                                                                     // * 3 + i
                                                                     // * 3]));
                    lbit = (r >> 23) != 0;

                    if (r == 0) 
                    { 
                    	throw new MapopolisException(
                            "Invalid address block"); 
                    }

                    if ((lbit && left) || (!lbit && !left)) 
                    {
                        if (j == n) return r & 0x007fffff;
                        else j++;
                    }
                }
            }

            return -1;
        } 
        else if (k == 3) 
        {
            // check the bit that tells whether we have an address block in this
            // aux block

            if ((f & 32) != 0) return address(buffer, idx + 1, from, left, n);
            else return -1;
        } 
        else 
        {
            throw new MapopolisException("Invalid address block");
        }
    }
    
    public Vector<FeaturePoint> overlappingPoints() throws MapopolisException
    
    {
    	Vector<FeaturePoint> v = new Vector<FeaturePoint>();
    	
    	if ( overlaps == null )
    		throw new MapopolisException("Overlap null - FeaturePoint");
    	
    	for ( int i = 0; i < overlaps.overlapCount; ++i )
    	{
    		FeaturePoint fp = FeaturePoint.getFeaturePoint(overlaps.overlapNodes[i], myMapFeatureRecord.mapFile, false, true, true);
    		v.addElement(fp);
    	}
    	
    	return v;
    }
    
    public String toString()
    
    {
    	String s = "Point ";
    	
    	try
		
		{
			//setWorldCoordinates();
			
			s += " x=" + x + " y=" + y + " ";
			
			//s += " dx=" + dx + " dy=" + dy;
			//s += " index=" + idx;

			
			//byte[] buffer = myMapFeatureRecord.getAsBytes();

			//Engine.out("");
			//Engine.out("Index=" + index);
		
			//if ((buffer[idx + 0] & 0x80) != 0) 
			//{
			//	int comp = (buffer[idx + 0] & 3);
			//	s += " comp=" + comp;
			//}	
			
			
			
	    	if ( overlaps != null )
			{
	    		for ( int i = 0; i < overlaps.overlapCount; ++i )
	    		{
					FeaturePoint fp = FeaturePoint.getFeaturePoint(overlaps.overlapNodes[i], myMapFeatureRecord.mapFile, false, false, false);
	    			s += fp.getStreetName();
					
					if ( i < overlaps.overlapCount - 1 )
						s += " & ";
	    		}
			}
	    	
	    	//s += getStreetName();
			
    		s += " distance=" + distance + " speed=" + speedClass + " seqNumber=" + seqNumber;// + " local=" + localStreet + " longhaul=" + longHaul + " toll=" + tollRoad;
			
			//if ( overlaps.overlapCount == 0 ) s += " ********************";
			
			s += "[";
			
			if ( (descriptor & Avoid) != 0 )
				s += " AVOID";
			
			if ( isFirst )
				s += " FIRST";
			
			if ( isLast )
				s += " LAST";
			
			if ( isBoundaryNode() )
				s += " BOUNDARY";
			
			if ( isNode )
				s += " NODE";
			
			s += "]";
		}
    	
    	catch (Exception e)
		
		{
			Engine.out(e.toString()); e.printStackTrace();
    		return "NULL";
		}
    	
    	return s;
    }

	int streetDirectionAtPoint(int approach) throws MapopolisException
    
    {
    	Vector<FeaturePoint> p = getContainingFeature().allPoints(false, false);
    	
    	for ( int i = 0; i < p.size(); ++i )
    	{
    		FeaturePoint fprev = null, fnext = null;
    		FeaturePoint fp = (FeaturePoint) p.elementAt(i);
    		
    		if ( i > 0 ) fprev = (FeaturePoint) p.elementAt(i - 1);
    		if ( i < p.size() - 1 ) fnext = (FeaturePoint) p.elementAt(i + 1);
    		
    		if ( fp.equals(this) )
    		{
    			if ( approach == IncreasingOut )
    			{
    				if ( fnext != null )
    					return Utilities.atan(fnext.x - fp.x, fnext.y - fp.y);
    			}
    			else if ( approach == IncreasingIn )
    			{
    				if ( fprev != null )
    					return Utilities.atan(fp.x - fprev.x, fp.y - fprev.y);
    			}
    			else if ( approach == DecreasingOut )
    			{
    				if ( fprev != null )
    					return Utilities.atan(fprev.x - fp.x, fprev.y - fp.y);
    			}
    			else if ( approach == DecreasingIn )
    			{
    				if ( fnext != null )
    					return Utilities.atan(fp.x - fnext.x, fp.y - fnext.y);
    			}
    		}
    	}
    	
    	throw new MapopolisException("Invalid parameters street direction - FeaturePoint");
    }
    
    public byte[] getUID() throws MapopolisException
    
    {
    	int find = (myMapFeatureRecord.myIndex<<16) | idx;
    	
    	// System.out.println(find);
    	
        for ( int i = myMapFeatureRecord.mapFile.dataRecords - 1; i >= 0; --i ) 
        {
            MapFeatureRecord rec = (myMapFeatureRecord.mapFile.getMapFeatureRecords())[i];
            
            if ( rec.isFeatureRecord )
            	break;
            
            if ( rec.getChunkType() == BoundaryNodeAssociationCode )
            {
            	byte[] buf = rec.getAsBytes();
            	int off = 1;
            	
            	int n = IO.get2(buf, off);
            	off += 2;

            	for ( int j = 0; j < n; ++j )
            	{
            		int k = IO.get4(buf, off);
            		
            		if ( k == find )
            		{
            			off += 4;
            			byte[] b = new byte[8];
            			
            			for ( int r = 0; r < 8; ++r )
            				b[r] = buf[off + r];
            			
            			return b;
            		}
            		
            		off += 12;
            	}
            }
        }
        
        throw new MapopolisException("Cannot find uid - Route " + this);
    }

    Vector<FeaturePoint> allOverlappingPoints(Vector<MapFile> maps) throws MapopolisException
    
    {
    	Vector<FeaturePoint> v = new Vector<FeaturePoint>();

    	// in this map
    	
    	v.addElement(this);
    	Utilities.appendVector(v, overlappingPoints());
    	
    	// in other maps
    	
    	if ( (descriptor & FeaturePoint.Bdry) != 0 )
		{
			MapFile map = myMapFeatureRecord.mapFile;
			
			byte[] uid = getUID();

			for ( int i = 0; i < maps.size(); ++i )
			{
				MapFile otherMap = (MapFile) maps.elementAt(i);
				
				if ( map == otherMap )
					continue;
				
				if ( overlapsMap(otherMap) )
				{
					int k = otherMap.getAddressForUID(uid);
					
					if ( k == 0 )
						continue;

					FeaturePoint otherMapPoint = FeaturePoint.getFeaturePoint(k, otherMap, true, false, true);
					
			    	v.addElement(otherMapPoint);
			    	Utilities.appendVector(v, otherMapPoint.overlappingPoints());
				}
			}
		}
    	
    	// set coordinates for each point

		for (int i = 0; i < v.size(); ++i)
		{
			FeaturePoint fp = ((FeaturePoint)v.elementAt(i));
			fp.setWorldCoordinates();//.getWorldCoordinates();
		}

    	return v;
    }

    public Vector<StreetSegment> allOutgoingStreets(Vector<MapFile> maps) throws MapopolisException
    
    {
    	Vector<StreetSegment>  r= new Vector<StreetSegment> ();
    	
    	Vector<FeaturePoint> p = allOverlappingPoints(maps);
    	
    	for ( int i = 0; i < p.size(); ++i )
    	{
    		FeaturePoint fp = (FeaturePoint) p.elementAt(i);
    		MapFeature mf = fp.getContainingFeature();
    		
    		if ( !fp.isLast )
    		{
    			StreetSegment seg = new StreetSegment();
    			r.addElement(seg);
    			seg.street = mf;
    			seg.outgoingAngle = fp.streetDirectionAtPoint(IncreasingOut);
    			seg.directionIncreasing = true;
    			seg.allowed = (fp.descriptor & TravelIncreasing) != 0;
    		}
    		
    		if ( !fp.isFirst )
    		{
    			StreetSegment seg = new StreetSegment();
    			r.addElement(seg);
    			seg.street = mf;
    			seg.outgoingAngle = fp.streetDirectionAtPoint(DecreasingOut);
    			seg.directionIncreasing = false;
    			seg.allowed = (fp.descriptor & TravelDecreasing) != 0;
    		}
    	}

    	return r;
    }
    
    public boolean isOneWay()
    
    {
    	return ((descriptor & TravelIncreasing) == 0) || ((descriptor & TravelDecreasing) == 0);
    }
    
    public boolean isBoundaryNode()
    
    {
    	return ((descriptor & FeaturePoint.Bdry) != 0);
    }
    
    public boolean overlapsMap(MapFile map) throws MapopolisException
    
    {
		setWorldCoordinates();
		
		Box box = map.boundingBox();
    	
		boolean r = false;
		
		if ( x > box.x0 - 300 )
    		if ( x < box.x1 + 300 )
    			if ( y > box.y0 - 300 )
    				if ( y < box.y1 + 300 )
    					r = true;
    	
		if ( Engine.debug ) Engine.out("Overlap test " + x + " " + y + " " + box + " " + r);
		
    	return r;
    }
    
    public int speed()
    
    {
    	// in meters per second
    	
		return (((7 - speedClass)  *  5 ) + 7);
    }
	
	public void testAllOverlaps() throws MapopolisException
		
	{
		if ( !isNode )
			return;
		
    	for ( int i = 0; i < overlaps.overlapCount; ++i )
    	{
    		int k = overlaps.overlapNodes[i];

			FeaturePoint overlapPoint = FeaturePoint.getFeaturePoint(k, myMapFeatureRecord.mapFile, true, false, true);
    			
    		if ( !overlapPoint.isNode ) 
			{
    			throw new MapopolisException("Test failed, overlap point is not a node " + this);
			}
		}		
	}

    /*
     * 
     * unsigned char * getNextPt(ExchangeData * data, unsigned char * ptr, int
     * lx, int ly, int * x, int * y, int * last, unsigned char ** addressBlock,
     * int * isNode, int * seqNumber) { PointDescriptor p;
     * 
     * unsigned char * nextPtr = getPointData(data, ptr, 1, &p, 0, 0);
     * 
     * if ( x ) * x = lx + p.dx; if ( y ) * y = ly + p.dy; if ( last ) * last =
     * p.isLast; if ( addressBlock ) * addressBlock = p.addressBlock; if (
     * isNode ) * isNode = p.isNode; if ( seqNumber ) * seqNumber = p.seqNumber;
     * 
     * return nextPtr; }
     *  
     */
    
    
}