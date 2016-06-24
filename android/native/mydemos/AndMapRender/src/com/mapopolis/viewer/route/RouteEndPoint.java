
package com.mapopolis.viewer.route;



import java.util.Vector;

import com.mapopolis.viewer.core.FeaturePoint;
import com.mapopolis.viewer.core.MapFeature;
import com.mapopolis.viewer.engine.Engine;
import com.mapopolis.viewer.engine.MapopolisException;
import com.mapopolis.viewer.search.MapSearch;
import com.mapopolis.viewer.search.Match;
import com.mapopolis.viewer.search.StreetRelativeLocation;
import com.mapopolis.viewer.utils.WorldCoordinates;

public class RouteEndPoint

{
    Link[] links;
    MapFeature feature;

    //WorldCoordinates exactCoordinates;

	private int x;
	private int y;

    StreetRelativeLocation streetRelativeLocation;
    
    private String friendlyName;
    private FeaturePoint beforeNode;
    
	public String toString()
		
	{
		try
		
		{
			return feature.friendlyName() + " " + feature.myCities() + " " + beforeNode;
		}
		
		catch (MapopolisException e)
			
		{
			Engine.out(e.toString()); e.printStackTrace();
			return e.toString();	
		}
	}
	
    private RouteEndPoint()

    {
    	links  = new Link[2];
    }

    public static RouteEndPoint createRouteEndPoint(WorldCoordinates wc, int heading, int speed, Vector v) throws MapopolisException

    {
        RouteEndPoint r = new RouteEndPoint();

		MapSearch searcher = new MapSearch(v);
        r.feature = searcher.closestStreetFeatureToCoordinates(wc).mapFeature;
        
        r.streetRelativeLocation = r.feature.streetRelativeLocationOfCoordinates(wc);            
        
        if ( r.streetRelativeLocation.beforePoint.isNode && !r.streetRelativeLocation.beforePoint.isLast )
        	r.beforeNode = r.streetRelativeLocation.beforePoint;
        else
        	r.beforeNode = r.streetRelativeLocation.beforePoint.getNextNodePoint(false);
        
        //if ( Engine.debug ) Engine.out("create route endpoint " + r.feature.friendlyName() + " " + r.streetRelativeLocation);
        
        r.beforeNode.setSeqNumber();
        r.makeLinks(r.beforeNode);

        r.setExactCoordinates();

        return r;
    }
    
    public static RouteEndPoint createRouteEndPoint(WorldCoordinates wc, Vector v) throws MapopolisException

    {
        return createRouteEndPoint(wc, 0, 0, v);
    }

    // making route endpoint

    public static RouteEndPoint createRouteEndPoint(Match match, Vector v) throws MapopolisException

    {
        RouteEndPoint r = new RouteEndPoint();
        r.feature = match.mapFeature;
        r.friendlyName = match.friendlyName();
		
		MapSearch searcher = new MapSearch(v);

        if (match.matchType == MapSearch.SearchAddressType)
        {
            r.streetRelativeLocation = r.feature.streetRelativeLocationOfAddress(match.address); 

            if ( match.streetRelativeLocation.beforePoint.isNode )
            	r.beforeNode = match.streetRelativeLocation.beforePoint;
            else
            	r.beforeNode = match.streetRelativeLocation.beforePoint.getNextNodePoint(false);
        } 
        else if (match.matchType == MapSearch.SearchStreetType)
        {
        	Vector a = r.feature.allPoints(true, true);
        	
        	FeaturePoint f = (FeaturePoint) a.elementAt(a.size()/2);

            if ( f.isNode && !f.isLast )
            	r.beforeNode = f;
            else
            	r.beforeNode = f.getNextNodePoint(false);

            r.streetRelativeLocation = r.feature.streetRelativeLocationOfCoordinates(new WorldCoordinates(f.getX(), f.getY()));
        } 
        else if (match.matchType == MapSearch.SearchCityType)
        {
            WorldCoordinates ec = r.feature.coordinatesOfCenter();
            r.feature = searcher.closestStreetFeatureToCoordinates(ec).mapFeature;
            
            r.streetRelativeLocation = r.feature.streetRelativeLocationOfCoordinates(ec);            

            if ( r.streetRelativeLocation.beforePoint.isNode && !r.streetRelativeLocation.beforePoint.isLast )
            	r.beforeNode = r.streetRelativeLocation.beforePoint;
            else
            	r.beforeNode = r.streetRelativeLocation.beforePoint.getNextNodePoint(false);
        } 
        else if (match.matchType == MapSearch.SearchIntersectionType)
        {
        	FeaturePoint f = match.intersectionPoint;
        	
            if ( f.isNode && !f.isLast )
            	r.beforeNode = f;
            else
            	r.beforeNode = f.getNextNodePoint(false);

            r.streetRelativeLocation = r.feature.streetRelativeLocationOfCoordinates(new WorldCoordinates(f.getX(), f.getY()));
        } 
        else if (match.matchType == MapSearch.SearchPointLocationType)
        {
        	//WorldCoordinates wc = r.feature.firstPoint().getWorldCoordinates();

			int x = r.feature.firstPoint().getX();
			int y = r.feature.firstPoint().getY();

        	// System.out.println(r.feature.getName() + " " + wc.x + " " + wc.y + " " + r.feature.firstPoint());
        	
            r.feature = searcher.closestStreetFeatureToCoordinates(new WorldCoordinates(x, y)).mapFeature;
            
            // System.out.println(r.feature.getName());

			r.streetRelativeLocation = r.feature.streetRelativeLocationOfCoordinates(new WorldCoordinates(x, y));            
            
            if ( r.streetRelativeLocation.beforePoint.isNode && !r.streetRelativeLocation.beforePoint.isLast )
            	r.beforeNode = r.streetRelativeLocation.beforePoint;
            else
            	r.beforeNode = r.streetRelativeLocation.beforePoint.getNextNodePoint(false);
        } 
        else 
        {
            throw new MapopolisException("Invalid match type - Route End Point");
        }

        // System.out.println(r.streetRelativeLocation);
        
        r.beforeNode.setSeqNumber();
        r.makeLinks(r.beforeNode);

        r.setExactCoordinates();

		return r;
    }

    private void makeLinks(FeaturePoint f) throws MapopolisException
	
	{
    	if ( !f.isNode )
    		throw new MapopolisException("Not node - Route end point");
    	
    	links[0] = links[1] = null;

    	if ( (f.descriptor & FeaturePoint.TravelDecreasing) != 0 && !f.isLast )
    	{
    		links[1] = new Link(-1);
    		links[1].endPoint = f;
    		links[1].directionIncreasing = false;
    	}
    	
    	f = f.getNextNodePoint(true);    
    	f.setSeqNumber();
    	
    	if ( (f.descriptor & FeaturePoint.TravelIncreasing) != 0 && !f.isFirst )
    	{
    		links[0] = new Link(-1);
    		links[0].endPoint = f;
    		links[0].directionIncreasing = true;
    	}

    	//if ( Engine.debug ) Engine.out("endpoint link " + links[0]);
    	//if ( Engine.debug ) Engine.out("endpoint link " + links[1]);
    }
    
    private void setExactCoordinates() throws MapopolisException

    {
    	// TODO Implement correctly
    	
    	//////////////////////////////////////////////////////
        // returns the exact coordinates of the route endpoint
        // a point about 15 meters to the right or left of
        // the onstreet coordinates
    	//////////////////////////////////////////////////////
    	
		
		
    	WorldCoordinates wc = feature.coordinatesOfCenter();

		setX(wc.x);
		setY(wc.y);
    }
    
    WorldCoordinates onStreetCoordinates() throws MapopolisException
	
	{
    	return streetRelativeLocation.onStreetCoordinates();
	}
    
    public String friendlyName()
    
    {
    	if ( friendlyName == null )
    		return "Not Implemented";
    	else
    		return friendlyName;
    }

	public int getX()
	{
		return x;
	}

	public int getY()
	{
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
	
	public MapFeature getFeature()
		
	{
		return feature;
	}
}