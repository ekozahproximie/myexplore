
package com.mapopolis.viewer.search;

import java.util.*;

import com.mapopolis.viewer.utils.*;
import com.mapopolis.viewer.core.*;
import com.mapopolis.viewer.engine.*;

public class StreetRelativeLocation

{
    public int sideOfStreet;
	
    public int address;
    public int percentAlongSegment;
    
    public MapFeature mapFeature;
    
    // the consecutive points surrounding the location
    // temporarily used for other stuff in MapFeature
    // these are points not nodes
    
    public FeaturePoint beforePoint, afterPoint;
    
    public boolean equals(StreetRelativeLocation s)
    
    {
    	return  s.sideOfStreet == sideOfStreet && 
				s.address == address &&
				s.percentAlongSegment == percentAlongSegment &&
				s.mapFeature.equals(mapFeature) &&
				s.beforePoint.equals(beforePoint) &&
				s.afterPoint.equals(afterPoint);
    }
	
	public String featureName() throws MapopolisException
		
	{
		if ( mapFeature != null ) 
			return mapFeature.getName();
		else
			return "NoNameFound";
	}
    
    public String toString()
    
    {
    	try
		{
			String a = "Null";
			String b = "Null";
			
			if ( beforePoint != null ) b = beforePoint.toString();
			if ( afterPoint != null ) a = afterPoint.toString();
			
			String name = "Name";
			
			if ( mapFeature != null ) name = mapFeature.getName();
			
    		return address + " " + name;// + " from " + b + " to " + a + " " + percentAlongSegment + " " + (sideOfStreet == MapopolisProgramSettings.SideLeft ? "left" : "right");
			
			//WorldCoordinates wc = onStreetCoordinates();
			//return "[" + address + " " + mapFeature.getName() + "] " + wc.x + " " + wc.y;
		}
    	
    	catch (Exception e)
		
		{
			Engine.out(e.toString()); e.printStackTrace();
    		return e.toString();
		}
    }
    
    public WorldCoordinates onStreetCoordinates() throws MapopolisException
    
    {
		int wcbx = beforePoint.getX();
		int wcby = beforePoint.getY();

		int wcax = afterPoint.getX();
		int wcay = afterPoint.getY();

		//WorldCoordinates wca = afterPoint.getWorldCoordinates();
    	
		//Engine.out("" + wcb + " " + wca + " " + percentAlongSegment + " " + mapFeature.friendlyName());
		//Engine.out("" + beforePoint + " " + afterPoint);

		//Vector v = new Vector();
		//this.mapFeature.inCity(0, v);
		//for ( int i = 0; i < v.size(); ++i )
		//{
		//	SearchArea a = (SearchArea) v.elementAt(i);
		//	Engine.out(a.name);
		//}

		//Vector v = this.mapFeature.allPoints(true, true);
		//
		//for ( int i = 0; i < v.size(); ++i )
		//{
		//	FeaturePoint p = (FeaturePoint) v.elementAt(i);
		//	Engine.out("Point " + i + ":" + p.toString());
		//}
		
		
    	int x, y;
    	
    	if ( wcbx > wcax )
    		x = wcax + ((wcbx - wcax) * percentAlongSegment)/100;
    	else
    		x = wcbx + ((wcax - wcbx) * percentAlongSegment)/100;
    	
    	if ( wcby > wcay )
    		y = wcay + ((wcby - wcay) * percentAlongSegment)/100;
    	else
    		y = wcby + ((wcay - wcby) * percentAlongSegment)/100;

    	return new WorldCoordinates(x, y);
    }
    
    public int distanceToNode(boolean fwd) throws MapopolisException
    
    {
    	Vector p = mapFeature.allPoints(false, false);
    	
    	boolean start = false;
    	int total = 0;
    	
    	if ( fwd )
    	{
        	for ( int i = 0; i < p.size(); ++i )
        	{
        		FeaturePoint fp = (FeaturePoint) p.elementAt(i);
        		
    			if ( fp.equals(beforePoint) )
    				start = true;

    			if ( start )
    			{
    				if ( !fp.equals(beforePoint) && fp.isNode )
    					break;

    				total += fp.distance;
    			}
        	}
        	
        	return total - WorldCoordinates.distanceBetweenCoordinates(onStreetCoordinates(), beforePoint.getWorldCoordinates());
    	}
    	else
    	{
        	for ( int i = p.size() - 1; i >= 0; --i )
        	{
        		FeaturePoint fp = (FeaturePoint) p.elementAt(i);

        		if ( start )
        		{
        			total += fp.distance;
        			
    				if ( !fp.equals(afterPoint) && fp.isNode )
    					break;
        		}
        		
    			if ( fp.equals(afterPoint) )
    				start = true;
        	}
        	
        	return total - WorldCoordinates.distanceBetweenCoordinates(onStreetCoordinates(), afterPoint.getWorldCoordinates());
    	}
    }
}