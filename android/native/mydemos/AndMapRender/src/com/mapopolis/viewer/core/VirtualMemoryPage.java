
package com.mapopolis.viewer.core;

import java.io.*;
import java.net.*;
import java.util.*;

import com.mapopolis.viewer.engine.*;

class VirtualMemoryPage

{
	// this is the only class that extensively uses static variables
	// in a global sense, it seems justified that only one cache is present
	
    private static final int ArraySize = 32 * 1024;
    private static VirtualMemoryPage oldestMemoryPage = null;
    private static VirtualMemoryPage youngestMemoryPage = null;
    private static int currentPagesIn = 0;
    private static boolean File = true;
    private static Vector<byte[]> storage;
	
    static
	
	{
    	storage = new Vector<byte[]>(Engine.MaxCachePages);
    	
    	for ( int i = 0; i < Engine.MaxCachePages; ++i )
    		storage.addElement(new byte[ArraySize]);
    }

    // instance

    private MapFeatureRecord mapFeatureRecord;
    private int byteArrayIndex = -1;
    private VirtualMemoryPage nextYoungerPage = null;

    static VirtualMemoryPage registerRecord(MapFeatureRecord mfr)

    {
        VirtualMemoryPage m = new VirtualMemoryPage();
        m.mapFeatureRecord = mfr;
        return m;
    }

    byte[] getAsBytes() throws MapopolisException

    {
		if ( byteArrayIndex >= 0 )
			return (byte[])storage.elementAt(byteArrayIndex);
        
		// its not in memory

		if (currentPagesIn < Engine.MaxCachePages)
		{
			// use a new page

			byteArrayIndex = currentPagesIn;
			currentPagesIn++;
		}
		else
		{
			byteArrayIndex = oldestMemoryPage.byteArrayIndex;

			// remove oldest and set byteArrayIndex to indicate that it is no longer available

			oldestMemoryPage.byteArrayIndex = -1;
			oldestMemoryPage = oldestMemoryPage.nextYoungerPage;
		}

		// must have byteArrayIndex set before this call

		read();

		// add to list

		addToHeadOfList();
		
		//Engine.out("read: " + byteArrayIndex + " " + mapFeatureRecord);

		return (byte[])storage.elementAt(byteArrayIndex);
    }

	private void read() throws MapopolisException

	{
		try
		{
			byte[] b = (byte[])storage.elementAt(byteArrayIndex);

			int k;

			if (File)
			{
				RandomAccessFile in = mapFeatureRecord.mapFile.getInputStream();
				in.seek(mapFeatureRecord.offset);
				k = in.read(b, 0, mapFeatureRecord.length);
			}
			else
			{
				k = getPage("", b, mapFeatureRecord.length);
			}

			if (k != mapFeatureRecord.length)
				throw new MapopolisException("Map file read error");
		}

		catch (IOException e)
		{
			Engine.out(e.toString()); e.printStackTrace();
			throw new MapopolisException(e.toString());
		}
	}

	private void addToHeadOfList() throws MapopolisException

    {
		if (oldestMemoryPage == null)
		{
			// initialize

			oldestMemoryPage = this;
			youngestMemoryPage = this;
			nextYoungerPage = null;

			return;
		}

        youngestMemoryPage.nextYoungerPage = this;
        nextYoungerPage = null;
        youngestMemoryPage = this;
    }
    
	private int getPage(String url, byte[] buffer, int len) throws MapopolisException
	
	{
		URL u1 = null;
		int k = 0;

		try
	
		{
			u1 = new URL(url); 
			InputStream input = u1.openStream();
			k = input.read(buffer, 0, len);
		}
	
		catch (Exception e) 
	
		{
			Engine.out(e.toString()); e.printStackTrace();
			throw new MapopolisException(e.toString());
		}
		
		return k;
	}
}