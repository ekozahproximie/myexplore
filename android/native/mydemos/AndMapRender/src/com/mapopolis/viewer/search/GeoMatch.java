
package com.mapopolis.viewer.search;

import java.util.*;
import com.mapopolis.viewer.engine.*;

public class GeoMatch

{
	// single exact address match
	static final int ADDRESSMATCH = 1;
	
	// single street segment match
	static final int STREETMATCH = 2;
	
	public static final int Google6 = 10;
	public static final int Google7 = 11;
	public static final int Google8 = 12;
	
	public static final int ZIP2MATCH = 7;
	public static final int ZIP4MATCH = 15;
	
	// more than one address matches
	// may present to user or this
	// may be an anomaly in the data
	static final int MULTIPLEADDRESSMATCH = 3;

	// multiple street match
	// they may all be connected and
	// thus represent a single 'street'
	static final int MULTIPLESTREETMATCH = 4;
	
	// No match found
	public static final int NOMATCH = 0;
	
	public static double cy;
	public static double cx;
	
	// street and address if single match
	public String streetName;
	public int addressNumber;
	public String fullAddress;
	
	// if multiple matches
	// the set of GeoMatch 
	// objects
	Vector<GeoMatch> mulipleMatches;
	
	int score;
	
	static
	
	{
		cy = 40000000.0/360.0;
		double rads = 40.0 *(Math.PI/180.0);
		cx = Math.cos(rads) * cy;	
	}

	public int mapX;
	public int mapY;
	
	public double lat;
	public double lng;
	
	public int status;
	
	public GeoMatch()
		
	{
		
	}
	
	public GeoMatch(int n)
		
	{
		status = n;	
	}
	
	public GeoMatch(int s, String x, String y, String a, int k)
		
	{
		this(s, x, y, k);
		fullAddress = a;
	}
	
	public GeoMatch(int s, int x, int y, int a, String n, int k)
		
	{
		status = s;
		mapX = x;
		mapY = y;
		
		score = k;
		addressNumber = a;
		streetName = n;
	}
	
	public GeoMatch(int s, String x, String y, int a, String n, int k)
		
	{
		this(s, x, y, k);
		addressNumber = a;
		streetName = n;
	}
	
	private GeoMatch(int s, String x, String y, int k)
		
	{
		status = s;
		score = k;

		mapX = 0;
		mapY = 0;
		
		try
		
		{
			lng = new Double(x).doubleValue();
			lat = new Double(y).doubleValue();
			
			mapX = (int) (lng * cx);
			mapY = (int) (lat * cy);
		}
		
		catch (Exception e)
			
		{
			Engine.out(e.toString()); e.printStackTrace();
		}
	}
	
	public GeoMatch best()
		
	{
		if ( status == GeoMatch.MULTIPLEADDRESSMATCH || status == GeoMatch.MULTIPLESTREETMATCH )
		{
			GeoMatch best = null;
			int bestscore = 0;
			
			for ( int i = 0; i < this.mulipleMatches.size(); ++i )
			{
				GeoMatch g = (GeoMatch) this.mulipleMatches.elementAt(i);
				if ( g.score > bestscore )
				{
					best = g;
					bestscore = g.score;
				}
			}
			
			if ( best != null )
				return best;
			else
				return null;
		}
		else return this;
	}
	
	public String toString()
		
	{
		if ( status == this.MULTIPLEADDRESSMATCH || status == this.MULTIPLESTREETMATCH )
		{
			String s = "";
			
			for ( int i = 0; i < this.mulipleMatches.size(); ++i )
			{
				GeoMatch g = (GeoMatch) this.mulipleMatches.elementAt(i);
				s += "Multiple Match, " + g.toString();
				if ( i < this.mulipleMatches.size() - 1)
					s += "\015\012";
			}
			
			return s;
		}
		else
		{
			String s = "Unknown, , ";
			
			if ( status == this.ADDRESSMATCH ) 
				s = "Address Match, " + addressNumber + ", " + streetName;
			else if ( status == this.STREETMATCH ) 
				s = "Street Match, , " + streetName;
			else if ( status == this.NOMATCH ) 
				s = "No Match, , ";
			else if ( status == this.ZIP2MATCH )
				s = "Melissa2, " + fullAddress;
			else if ( status == this.ZIP4MATCH )
				s = "Melissa4, " + fullAddress;
			else if ( status == Google6 )
				s = "Google6, " + fullAddress;
			else if ( status == Google7 )
				s = "Google7, " + fullAddress;
			else if ( status == Google8 )
				s = "Google8, " + fullAddress;
			
			return s + ", " + mapX + ", " + mapY + ", " + lat + ", " + lng + ", " + score;	
		}
	}
}
