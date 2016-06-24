
package com.mapopolis.viewer.route;


import com.mapopolis.viewer.core.FeaturePoint;
import com.mapopolis.viewer.core.MapFeature;
import com.mapopolis.viewer.engine.Engine;
import com.mapopolis.viewer.engine.MapopolisException;

public class RouteElement

{
	//
	// start descriptor and destination descriptor have:
	//
	//  1) info on partial link at each end
	//	2) info on segment from location on street to location of address
	//

	public FeaturePoint startPoint;
	public FeaturePoint endPoint;
	
	public MapFeature street;
		
	int distance;

	int maneuver;
	int roundaboutExitNumber;

	int turnAngle;

	public String toString()
	
	{
		try
		{
			return "Turn " + turnAngle + " of type " + maneuver + " onto " + startPoint.getStreetName() + " at [" + startPoint + "]";
		}
		
		catch (Exception e)
		
		{
			Engine.out(e.toString()); e.printStackTrace();
			return "";
		}
	}
	
	int getX0() throws MapopolisException
	
	{
		return startPoint.getX();
	}
	
	int getY0() throws MapopolisException
	
	{
		return startPoint.getY();
	}
	
	int getX1() throws MapopolisException
	
	{
		return endPoint.getX();
	}
	
	int getY1() throws MapopolisException
	
	{
		return endPoint.getY();
	}
	
	int time()
	
	{
		return distance/endPoint.speed();
	}
}