
package com.mapopolis.viewer.core;

import java.util.*;
import com.mapopolis.viewer.utils.*;
import com.mapopolis.viewer.engine.*;

public class MapFeatureRecord

{
    public MapFile mapFile;
    public int myIndex;
    public int offset;
    public Box boundingBox;
	int length;
	public boolean isFeatureRecord = false;
	private VirtualMemoryPage virtualMemoryPage;
	private int type = -1;

	public boolean equals(MapFeatureRecord mfr)
    
    {
    	return mfr.mapFile == mapFile && mfr.offset == offset;
    }

    MapFeatureRecord(MapFile m, int off)

    {
        offset = off;
        mapFile = m;
        virtualMemoryPage = VirtualMemoryPage.registerRecord(this);
    }

    void initializeAsFeatureRecord(int t, Box bbox)

    {
		boundingBox = bbox;
		type = t;
        isFeatureRecord = true;
    }

	/*
	void setFeatureCache(MapFeature m)
	{
		if (Engine.LargeDevice)
		{
			// make a place to hold MapFeature stuff that has been calculated
			// must limit total number of these for memory

			if (mapFeatureCache == null)
				mapFeatureCache = new Object[32*1024];
		}

		mapFeatureCache[m.offsetInRecord] = m;

		//Engine.out("Cached features: " + ++numberOfCachedFeatures);
	}

	void setFeatureCache(FeaturePoint p)
		
	{
		if ( Engine.LargeDevice )
		{
			// make a place to hold FeaturePoint stuff that has been calculated
			// must limit total number of these for memory
			
			if ( mapFeatureCache == null )
				mapFeatureCache = new Object[32 * 1024];
		}
		
		mapFeatureCache[p.idx] = p;
		
		//Engine.out("Cached features: " + ++numberOfCachedFeatures);
	}

	MapFeature getFeatureCache(MapFeature m)
	{
		if (mapFeatureCache == null)
			return null;

		return (MapFeature) mapFeatureCache[m.offsetInRecord];
	}

	FeaturePoint getPointFeatureCache(int offset)
	
	{
		if ( mapFeatureCache == null )
			return null;
		
		return (FeaturePoint) mapFeatureCache[offset];
	}
	*/
			
	public byte[] getAsBytes() throws MapopolisException

    {
        return virtualMemoryPage.getAsBytes();
    }

	public int getChunkType() throws MapopolisException
    
    {
    	if ( type < 0 )
		{
    		type = IO.get1(getAsBytes(), 0);
			//Engine.out("type is " + type);
		}
		
    	return type;
    }
	
    @Override
	public String toString()
		
	{
		try
		{
			return "(MapFeatureRecord " + myIndex + " in " + mapFile.friendlyName() + " of type " + getChunkType() + ")";	
		}
		
		catch (Exception e)
			
		{
			Engine.out(e.toString()); e.printStackTrace();			
			return e.toString();
		}
	}
	
    public int getFeatureTypex(int offsetInRecord) throws MapopolisException
	
    {
		// can we change to just using type ???
		
    	byte[] buffer = getAsBytes();
       return (IO.get1(buffer, offsetInRecord + 11));
    }
	
	public int[] getFeatureIndices() throws MapopolisException
		
	{
		// maybe buffer this too
		
		byte[] buffer = getAsBytes();
		int[] indices = new int[5000];
		
		int index = 0;
		int n = 1;
		
		while (true) 
		{
		    int rlen = IO.get2(buffer, index);
			indices[n++] = index;
		
		    if (rlen != 0) 
		    	index += rlen;
		    else 
		    	break;
		}
		
		//
		// new (n-1)
		//
		
		indices[0] = n - 1;
		return indices;
	}

	
	public void test() throws MapopolisException
	{
		Engine.out("test " + getChunkType() + " " + myIndex + " " + offset + " " + length);
				   
		int[] ind = getFeatureIndices();

		for ( int i = 1; i <= ind[0]; ++i )
		{
			MapFeature mm = new MapFeature(this, ind[i]);
			Engine.out("---------------" + mm.getName());
			mm.allPoints(true, true);
			// + " " + mm.boundingBox());
		}
	}
	
	public void dump() throws MapopolisException
		
	{
		Engine.out("test " + getChunkType() + " " + myIndex + " " + offset + " " + length);
				   
		int[] ind = getFeatureIndices();

		for ( int i = 1; i <= ind[0]; ++i )
		{
			MapFeature m = new MapFeature(this, ind[i]);
			
			Vector<FeaturePoint> v = m.allPoints(true, true);
			
			for ( int j = 0; j < v.size(); ++j )
			{
				FeaturePoint fp = (FeaturePoint) v.elementAt(j);
				fp.testAllOverlaps();
			}
		}
	}
	
	public boolean stringsCompressed()
		
	{
		return mapFile.stringsCompressed();	
	}
}