
package com.mapopolis.viewer.utils;

import com.mapopolis.viewer.core.*;
import com.mapopolis.viewer.engine.*;

import java.util.*;

public class SearchArea implements ISortable

{
	public String name;
	public int zip = -1;
	public Box box;
	
	public MapFile map;

	//public Vector mapAndIndices = new Vector(1, 1);
	
	public SearchArea(String n, Box b)
		
	{
		name = n;
		box = b;
		
		if ( name.startsWith("# ") )
		{
			int z = -1;
			
			try
			
			{
				z = new Integer(name.substring(2)).intValue();	
			}
			
			catch (Exception e)
				
			{
				//Engine.out(e.toString()); e.printStackTrace();
			}
			
			if ( z < 0 )
			{
				//Engine.out("Cannot make zip from " + name);
			}
			else
				zip = z;
			
			//Engine.out("Zip " + zip);
		}
	}
		
	public boolean overlaps(Box b)
		
	{
		return Box.overlap(box, b);	
	}
	
	public int compareTo(ISortable sa)
	
	{
		return name.compareTo(((SearchArea) sa).name);
	}
}
