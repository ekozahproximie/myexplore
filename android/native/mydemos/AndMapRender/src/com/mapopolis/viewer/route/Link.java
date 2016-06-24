
package com.mapopolis.viewer.route;

import com.mapopolis.viewer.engine.*;
import com.mapopolis.viewer.core.*;

class Link

{
    public static final int Invalid = -1;
    public static final int BigLong = 999999999;

	// link description
	
    FeaturePoint endPoint;
    boolean directionIncreasing;
    int myIndex = -1;
    
    // costs
    
    int costToEnd;
    int estimatedCostToDestination;

    // indices
    
    int costfindex;
    int costbindex;
    
    int estfindex;
    int estbindex;
    
    Link(int n)

    {
    	myIndex = n;
    }
    
    public boolean equals(Link l)
    
    {
    	return endPoint.equals(l.endPoint) && directionIncreasing == l.directionIncreasing;
    }
    
    boolean free()
    
    {
    	return endPoint == null && costToEnd == Invalid && estimatedCostToDestination == BigLong;
    }
    
    int myLinkID()
    
    {
    	if ( endPoint == null )
    		return -1;
    	else
    		return makeLinkID(endPoint.seqNumber, directionIncreasing);
    }
    
    static int makeLinkID(int id, boolean dir)
    
    {
    	return (id<<1) + (dir ? 0 : 1);
    }
	
	public String toString()
	
	{
		String s = "NO END POINT";
		
		try
		
		{
			if ( endPoint != null )
			{
				s = endPoint.toString();// + " " + endPoint.getStreetName();
			}
		}
		
		catch (Exception e)
		
		{
			Engine.out(e.toString()); e.printStackTrace();
		}
		
		s += " costToHere=" + costToEnd;
		s += " estCost=" + estimatedCostToDestination;
		s += " id=" + myLinkID();
		
    	return s;
	}
}