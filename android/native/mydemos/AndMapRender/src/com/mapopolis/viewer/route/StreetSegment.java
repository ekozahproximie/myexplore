
package com.mapopolis.viewer.route;

import com.mapopolis.viewer.engine.*;
import com.mapopolis.viewer.core.*; 

public class StreetSegment

{
	public MapFeature street;
	public int outgoingAngle;
	
	public boolean allowed;
	public boolean directionIncreasing;
	
	public boolean leaving = false;
	public boolean approach = false;
	
    @Override
	public String toString()
	
	{
		try
		{
			return street.friendlyName() + " " + outgoingAngle + " " + allowed;
		}
		
		catch (Exception e)
		
		{
			Engine.out(e.toString()); e.printStackTrace();
			return "";
		}
	}
}