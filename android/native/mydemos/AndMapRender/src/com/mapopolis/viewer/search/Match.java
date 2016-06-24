
package com.mapopolis.viewer.search;

import com.mapopolis.viewer.utils.*;
import com.mapopolis.viewer.core.*;
import com.mapopolis.viewer.engine.*;

import java.util.*;

public class Match implements ISortable

{
    public int matchType;

    // for all search types

    public MapFeature mapFeature;
    
    // address match

    public int address;
    public StreetRelativeLocation streetRelativeLocation;

    // for POI match

    String category;

    // intersection
    
    public Match otherStreetMatch;
    public FeaturePoint intersectionPoint;
    
    // location, side of street, etc.

    public boolean equals(Match m)
    
    {
        if (matchType == MapSearch.SearchIntersectionType) 
        {
        	
        } 
        else if (matchType == MapSearch.SearchStreetType)
        {
        	
        }
        else if (matchType == MapSearch.SearchAddressType)
        {
        	return 	
			
			m.mapFeature.equals(mapFeature) &&
			m.address == address &&
			m.streetRelativeLocation.equals(streetRelativeLocation);        	
        }
        else if (matchType == MapSearch.SearchPointLocationType) 
        {
        	
        }
        else if (matchType == MapSearch.SearchCityType)
        {
        	
        }

        return m == this;
    }
    
    public Match copy()

    {
    	// update this 
    	
        Match m = new Match();

        m.matchType = matchType;
        m.address = address;
        m.mapFeature = mapFeature;
        m.category = category;
        m.otherStreetMatch = otherStreetMatch;

        return m;
    }

    public Match(MapFeatureRecord mfr, int offset, int type) throws MapopolisException

    {
        mapFeature = new MapFeature(mfr, offset);
        matchType = type;
    }

    public Match(MapFeature m, int type)

    {
        mapFeature = m;
        matchType = type;
    }

    private Match()

    {

    }

    
    public String getCityNames()

    {
        return "";
    }

    public String getPostalCodes()

    {
        return "not implemented";
    }

    public String getLatitudeLongitudeAsString()

    {
        return "not implemented";
    }

    public WorldCoordinates getWorldCoordinates() throws MapopolisException

    {
        if (matchType == MapSearch.SearchIntersectionType) 
        {
        	
        } 
        else if (matchType == MapSearch.SearchStreetType)
        {
        	
        }
        else if (matchType == MapSearch.SearchAddressType)
        {
        	return streetRelativeLocation.onStreetCoordinates();
        }
        else if (matchType == MapSearch.SearchPointLocationType) 
        {
        	
        }
        else if (matchType == MapSearch.SearchCityType)
        {
        	
        }
    	
        return mapFeature.coordinatesOfCenter();
    }
    
    
    public String friendlyName() throws MapopolisException

    {
        if (matchType == MapSearch.SearchIntersectionType) 
        {
            String r = mapFeature.getNames()[0];
            if (otherStreetMatch != null)
            	r += " & " + otherStreetMatch.mapFeature.getNames()[0];
            return r;
        } 
        else if (matchType == MapSearch.SearchStreetType)
        {
        	String[] names = mapFeature.getNames();
        	String s = "";
        	
        	for ( int i = 0; i < names.length; ++i )
        		if ( names[i] != null )
        			s += names[i] +"/";
        	
        	s = s.substring(0, s.length() - 1);
        		
        	return s;
        }
        else if (matchType == MapSearch.SearchAddressType) 
        	return address + " " + mapFeature.getNames()[0];
        else if (matchType == MapSearch.SearchPointLocationType) 
        {
        	String[] names = mapFeature.getNames();
        	String s = names[1] + "(" + names[0] + ")";
        
        	for ( int i = 2; i < names.length; ++i )
        		if ( names[i] != null )
        			s += " " + names[i];
        	
        	return s;
        }
        else if (matchType == MapSearch.SearchCityType) 
        	return mapFeature.getNames()[0];
        else 
        	return "Name Not Implemented";
    }
    
    public String toString()
    
    {
    	try
		
		{
    		return friendlyName() + " in " + mapFeature.mapFeatureRecord.mapFile.friendlyName();
		}
    	
    	catch (Exception e)
		
		{
			Engine.out(e.toString()); e.printStackTrace();
    		return "";
		}
    }

    /*
    
    WorldCoordinates coordinatesOfIntersectionPoint() throws MapopolisException

    {
        if (matchType != MapSearch.SearchIntersectionType)
                throw new MapopolisException("Invald match type");

        return null;
    }
    
    */

    Vector allPoints() throws MapopolisException

    {
        return mapFeature.allPoints(true, true);

    }

    StreetRelativeLocation streetRelativeLocationOfAddress(int a) throws MapopolisException

    {
        return mapFeature.streetRelativeLocationOfAddress(a);
    }
    
	public int compareTo(ISortable m)
	
	{
		try
		
		{
			return friendlyName().compareTo(((Match) m).friendlyName());
		}
		
		catch (Exception e)
		
		{
			Engine.out(e.toString()); e.printStackTrace();
			return 0;
		}
	}
}