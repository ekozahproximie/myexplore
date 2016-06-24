
package com.mapopolis.viewer.search;

import com.mapopolis.viewer.utils.*;
import com.mapopolis.viewer.core.*;
import com.mapopolis.viewer.draw.*;
import com.mapopolis.viewer.engine.*;

import java.util.Vector;

/**
 * MapSearch provides all of the search functionality for the viewer. Each
 * function returns a Vector of Match objects. Each Match has an accessible
 * friendlyName to display to the user. When a choice is made by the user, the
 * Match object's coordinate location can be used to re-position the map, or the
 * Match can be used to create a route endpoint.
 *  
 */

public class MapSearch

{
    public static final int SearchStreetType = 1;
	public static final int SearchAddressType = 2;
    public static final int SearchPointLocationType = 3;
    public static final int SearchCityType = 4;
    public static final int SearchIntersectionType = 5;
    public static final int SearchFavoriteType = 6;
    public static final int SearchAnyType = 7;

	////////////////////////////////
	
	private boolean isStopped;
	private Vector<MapFile> vecMapsAvailable;
	private int leastScore;
	    
    public MapSearch(Vector v)
    
    {
    	vecMapsAvailable = v;
    }
	
	public boolean inSearchArea(MapFeature mf, SearchArea sa) throws MapopolisException
    
    {
		Vector rs = mapFeatureRecordsOverlappingSearchArea(sa);
		
		boolean in = false;
		
		for ( int i = 0; i < rs.size(); ++i )
		{
			MapFeatureRecord mfr = (MapFeatureRecord) rs.elementAt(i);
			
			if ( mfr.equals(mf.mapFeatureRecord) )
			{
				in = true;
				break;
			}
		}
		
		if ( !in )
			return false;
		
    	Vector v = mf.allPoints(false, false);
    	
    	for ( int i = 0; i < v.size(); ++i )
    	{
    		FeaturePoint f = (FeaturePoint) v.elementAt(i);
    		
			if ( f.containsCity(sa.name, null) )
				return true;
    	}
    	
    	return false;
    }

	static Vector names;
	static Vector cboxes;
	
	private Vector featureRecordsOverlappingCounty(String cs)
		
	{
		if ( cboxes == null )
		{
			Vector testdata = Utilities.getData("d:\\docume~1\\admini~1\\desktop\\servicepower\\maps.csv");
		
			cboxes = new Vector();
			names = new Vector();
			
			for ( int i = 0; i < testdata.size(); ++i )
			{
				String[] f = (String[]) testdata.elementAt(i);
				
				int x0 = Utilities.getZip(f[1]);
				int y0 = Utilities.getZip(f[2]);
				int x1 = Utilities.getZip(f[3]);
				int y1 = Utilities.getZip(f[4]);
				
				Box b = new Box(x0, y0, x1, y1);
				
				
				names.addElement(f[0]);
				cboxes.addElement(b);
				
				//Engine.out(f[0] + " " + b);
			}
		}

		Vector records = new Vector();
		
		String use = Address.removeCharacters(cs, " ").toUpperCase();
		
		
		//Engine.out("countyState=" + use);
		
		Vector boxes = new Vector();
		for ( int i = 0; i < names.size(); ++i )
		{
			if ( ((String) names.elementAt(i)).equals(use) )
				boxes.addElement(cboxes.elementAt(i));
		}
		
		if ( boxes.size() != 1 )
			Engine.out("for " + use + " found " + boxes.size() + " results");
		
		if ( boxes.size() == 0 )
		{
			return records;
		}
		
		for ( int i = 0; i < vecMapsAvailable.size(); ++i )
		{
			MapFile map = (MapFile) vecMapsAvailable.elementAt(i);
			
			MapFeatureRecord[] mfrs = map.getMapFeatureRecords();
			
			for ( int j = 0; j < mfrs.length; j++ )
			{
				MapFeatureRecord mfr = mfrs[j];
				
				if ( !mfr.isFeatureRecord ) 
            		continue;

				for ( int k = 0; k < boxes.size(); ++k )
				{
					Box zb = (Box) boxes.elementAt(k);
					if (Box.overlapWithMargin(zb, mfr.boundingBox, Engine.BoxMargin))
					{
						records.addElement(mfr);
					}
				}
			}
		}		
		
		//Engine.out("found " + records.size() + " records");
		return records;
	}
	
	
	private Vector featureRecordsOverlappingZip(int zip)
		
	{
		Vector zipboxes = new Vector();
		Vector records = new Vector();
		
		for ( int i = 0; i < vecMapsAvailable.size(); ++i )
		{
			MapFile map = (MapFile) vecMapsAvailable.elementAt(i);
			
			for ( int j = 0; j < map.cities.size(); ++j )
			{
				SearchArea sa = (SearchArea) map.cities.elementAt(j);
				
				if ( sa.zip == zip )
				{
					zipboxes.addElement(sa.box);
					//Engine.out("FOUND " + sa.box + " in " + map.friendlyName());
				}
			}
		}

		if ( zipboxes.size() == 0 )
		{
			//Engine.out("Zip not found " + zip);
			return records;
		}
		
		for ( int i = 0; i < vecMapsAvailable.size(); ++i )
		{
			MapFile map = (MapFile) vecMapsAvailable.elementAt(i);
			
			MapFeatureRecord[] mfrs = map.getMapFeatureRecords();
			
			for ( int j = 0; j < mfrs.length; j++ )
			{
				MapFeatureRecord mfr = mfrs[j];
				
				if ( !mfr.isFeatureRecord ) 
            		continue;

				for ( int k = 0; k < zipboxes.size(); ++k )
				{
					Box zb = (Box) zipboxes.elementAt(k);
					
					//if ( Engine.BoxMargin )
					//{

					if (Box.overlapWithMargin(zb, mfr.boundingBox, Engine.BoxMargin))
					{
						records.addElement(mfr);
					}

					//}
					//else
					//{
					//	if ( Box.overlap(zb, mfr.boundingBox) )
					//	{
					//		records.addElement(mfr);
					//		//Engine.out(mfr.toString());
					///	}
					//
					//}
				}
			}
		}		
		
		return records;
	}

	private Vector featureRecordsOverlappingCity(String city)
		
	{
		city = city.toUpperCase();
		
		Vector cityboxes = new Vector();
		Vector records = new Vector();
		
		Engine.out("look for city " + city);
		
		for ( int i = 0; i < vecMapsAvailable.size(); ++i )
		{
			MapFile map = (MapFile) vecMapsAvailable.elementAt(i);
			
			for ( int j = 0; j < map.cities.size(); ++j )
			{
				SearchArea sa = (SearchArea) map.cities.elementAt(j);
				
				if ( sa.zip < 0 )
					if ( sa.name.equals(city) )
					{
						cityboxes.addElement(sa.box);
						//Engine.out("FOUND " + sa.box + " in " + map.friendlyName());
					}
			}
		}

		
		Engine.out("found " + cityboxes.size() + " results");
		
		
		if ( cityboxes.size() == 0 )
		{
			//Engine.out("Zip not found " + zip);
			return records;
		}
		
		for ( int i = 0; i < vecMapsAvailable.size(); ++i )
		{
			MapFile map = (MapFile) vecMapsAvailable.elementAt(i);
			
			MapFeatureRecord[] mfrs = map.getMapFeatureRecords();
			
			for ( int j = 0; j < mfrs.length; j++ )
			{
				MapFeatureRecord mfr = mfrs[j];
				
				if ( !mfr.isFeatureRecord ) 
            		continue;

				for ( int k = 0; k < cityboxes.size(); ++k )
				{
					Box cb = (Box) cityboxes.elementAt(k);

					if (Box.overlapWithMargin(cb, mfr.boundingBox, Engine.BoxMargin))
					{
						records.addElement(mfr);
					}
				}
			}
		}		
		
		return records;
	}
	
	private Vector mapFeatureRecordsOverlappingSearchArea(SearchArea a)
	
	{
		if ( a.zip > 0 )
			return featureRecordsOverlappingZip(a.zip);
		else
			return featureRecordsOverlappingCity(a.name);
	}

	/**
     * Searches the map set for a street address
     * 
     * @param target
     *            the string to be matched in the street name in the form "123
     *            main"
     * 
     * @param filter
     *            SearchFilter object that limits the search
     * 
     * @return a Vector of Match objects
     *  
     */

    public Vector searchAddress(String target, SearchArea filter) throws MapopolisException

    {
        isStopped = false;
        Vector v = new Vector();

        int address = getAddress(target);
        target = getStreet(target);
        
        if ( address < 0 )
        	return v;
        
        if ( target.equals("") )
        	return v;
        
        Vector maps = vecMapsAvailable;//Vector maps = getMapsFromFilter(filter);
        
        for (int i = 0; i < maps.size(); ++i)
        {
            MapFile map = (MapFile) maps.elementAt(i);

            Vector m = search(map, target, SearchAddressType, filter);
            
            for ( int j = 0; j < m.size(); ++j )
            {
            	Match match = (Match) m.elementAt(j);
            	
            	StreetRelativeLocation al = match.mapFeature.streetRelativeLocationOfAddress(address);
            	
            	if ( al != null )
            	{
            		v.addElement(match);
            		match.address = address;
            		match.streetRelativeLocation = al;
            	}
            }
        }

        v = QSortAlgorithm.sort(v);
        
        return v;
    }

    /**
     * Searches the map set for a street name
     * 
     * @param target
     *            the string (partial or full) to be matched in the street name
     *            as "lamber"
     * 
     * @param filter
     *            SearchFilter object that limits the search
     * 
     * @return a Vector of Match objects
     *  
     */

    public Vector searchStreet(String target, SearchArea filter) throws MapopolisException

    {
        isStopped = false;
        Vector v = new Vector();

        Vector maps = vecMapsAvailable;//Vector maps = getMapsFromFilter(filter);
        
        for (int i = 0; i < maps.size(); ++i) 
        {
            MapFile map = (MapFile) maps.elementAt(i);
            Utilities.appendVector(v, search(map, target, SearchStreetType, filter));
        }
        
        v = QSortAlgorithm.sort(v);

        return v;
    }

    /**
     * Searches the map set for a city name
     * 
     * @param target
     *            the string to be matched in the street name
     * 
     * @param filter
     *            SearchArea object that limits the search
     * 
     * @return a Vector of Match objects
     *  
     */

    public Vector searchCity(String target, SearchArea filter) throws MapopolisException

    {
        isStopped = false;
        Vector v = new Vector();

        Vector maps = vecMapsAvailable;//Vector maps = getMapsFromFilter(filter);
        
        for (int i = 0; i < maps.size(); ++i) 
        {
            MapFile map = (MapFile) maps.elementAt(i);
            Utilities.appendVector(v, search(map, target, SearchCityType, filter));
        }

        v = QSortAlgorithm.sort(v);
        
        return v;
    }

    /**
     * Searches the map set for a street intersection
     * 
     * @param target1
     *            the string to be matched in the first street name
     * 
     * @param target2
     *            the string to be matched in the second street name
     * 
     * @param filter
     *            SearchArea object that limits the search
     * 
     * @return a Vector of Match objects
     *  
     */

    public Vector searchIntersection(String target1, String target2, SearchArea filter) throws MapopolisException

    {
        isStopped = false;
        Vector v = new Vector();

        Vector maps = vecMapsAvailable;//Vector maps = getMapsFromFilter(filter);
        
        for (int i = 0; i < maps.size(); ++i) 
        {
            MapFile map = (MapFile) maps.elementAt(i);
            Utilities.appendVector(v, search(map, target1, SearchIntersectionType, filter));
        }

        // now check each match for intersection with target2

        target2 = target2.toUpperCase();

        Vector r = new Vector();

        for (int i = 0; i < v.size(); ++i) 
        {
            Match match = (Match) v.elementAt(i);

            Vector ap = match.mapFeature.allPoints(true, true);
            
            for (int j = 0; j < ap.size(); ++j) 
            {
            	FeaturePoint f = (FeaturePoint) ap.elementAt(j);
            	
            	if ( !f.isNode )
            		continue;
            	
            	Vector ops = f.overlappingPoints();
            	
            	for ( int k = 0; k < ops.size(); ++k )
            	{
            		FeaturePoint ofp = (FeaturePoint) ops.elementAt(k);
            		MapFeature cf = ofp.getContainingFeature();
            		
                    String[] names = cf.getNames();

                    for (int n = 0; n < names.length; ++n)
                        if (names[n] != null)
                                if (names[n].indexOf(target2) >= 0) 
                                {
                                	Match newMatch = match.copy();
                                	
                                    newMatch.otherStreetMatch = new Match(cf, SearchIntersectionType);
                                    newMatch.intersectionPoint = f;
                                    
                                    r.addElement(newMatch);
                                }
            	}
            }
        }
        
        r = QSortAlgorithm.sort(r);
        
        return r;
    }

    /**
     * Searches the map set for a poi
     * 
     * @param category
     *            the string to be matched in the category name
     * 
     * @param target
     *            the string to be matched in the poi name
     * 
     * @param filter
     *            SearchArea object that limits the search
     * 
     * @return a Vector of Match objects
     *  
     */

    public Vector searchPOI(String category, String target, SearchArea filter) throws MapopolisException

    {
        isStopped = false;
        
        Vector v = new Vector();
        Vector r = new Vector();
        
        target = target.toUpperCase();
        
        if ( category != null )
        	category = category.toUpperCase();
        
        Vector maps = vecMapsAvailable;//Vector maps = getMapsFromFilter(filter);
        
        for (int i = 0; i < maps.size(); ++i) 
        {
            MapFile map = (MapFile) maps.elementAt(i);
            Utilities.appendVector(v, search(map, target, SearchPointLocationType, filter));
        }

        // now check that the category matches and the name (not only the
        // category) matches target

    	for ( int i = 0; i < v.size(); ++i )
    	{
    		Match m = (Match) v.elementAt(i);
    		
    		// System.out.println("Match " + m.friendlyName());
    		
    		String[] names = m.mapFeature.getNames();

    		if ( category != null )
    			if ( !category.equals(names[0]) )
    				continue;
    			
    		boolean ok = false;
    		
    		for ( int j = 1; j < names.length; ++j )
    		{
    			if ( names[j] != null )
	    			if ( names[j].indexOf(target) >= 0 )
	    			{
	    				ok = true;
	    				break;
	    			}
    		}
    		
    		if ( ok )
    			r.addElement(m);
    	}

    	r = QSortAlgorithm.sort(r);
    	
        return r;
    }

    /**
     * Searches the current set of favorites
     * 
     * @param target
     *            the string to be matched in the favorite name
     * 
     * @param filter
     *            SearchArea object that limits the search
     * 
     * @return a Vector of Match objects
     *  
     */

    public Vector searchFavorite(String target, SearchArea filter) throws MapopolisException
    
	{
    	// this will search a settings database
    	
    	isStopped = false;
    	return new Vector();
    }

    /**
     * Searches the map set for all POI categories
     * 
     * @return a Vector of Strings
     *  
     */

    public Vector getAllPOICategories(SearchArea filter) throws MapopolisException

    {
        isStopped = false;
        Vector results = new Vector();
        
        Vector maps = vecMapsAvailable;//Vector maps = getMapsFromFilter(filter);

        for (int m = 0; m < maps.size(); ++m) 
        {
            MapFile map = (MapFile) maps.elementAt(m);

            for (int i = 0; i < map.dataRecords; ++i) 
            {
                MapFeatureRecord rec = (map.getMapFeatureRecords())[i];

                if (!rec.isFeatureRecord) continue;

                // test that this feature record is in geographic search area
                // if not then continue
				
				int[] featureIndices = rec.getFeatureIndices();
				int n = featureIndices[0];
				
				for ( int j = 1; j <= n; ++j )
				{
					MapFeature mf = new MapFeature(rec, featureIndices[j]);
					
                    if (mf.isFeatureTypeSearchingFor(SearchPointLocationType)) 
                    {
                        String category = mf.getNames()[0]; //MapUtilities.getNames(rec, featureIndices[j])[0];
                        if (!inVector(results, category))
                                results.addElement(category);
                    }
				}
            }
        }
        
        results = Utilities.sortStrings(results);

        return results;
    }

	/*
    public Match getClosestFeaturex(WorldCoordinates wc)
    
    {
    	return null;
    }

    public Match getClosestFeaturex(PixelCoordinates wc)
    
    {
    	return null;
    }
	*/

	/*
    public Vector getSearchAreasxxx(String match, boolean sort) throws MapopolisException
    
    {
    	if ( match != null )
    		match = match.toUpperCase();
    	
        Vector results = new Vector();

        for (int m = 0; m < mapsAvailable.size(); ++m) 
        {
            MapFile map = (MapFile) mapsAvailable.elementAt(m);
            Utilities.appendVector(results, map.getCities());
            
            // System.out.println(map.friendlyName() + " " + results.size());
        }
        
        if ( match != null )
        {
        	Vector r = new Vector();
        	
        	for ( int i = 0; i < results.size(); ++i )
        	{
        		SearchArea a = (SearchArea) results.elementAt(i);
        		
        		if ( a.name.toUpperCase().indexOf(match) >= 0 )
        			r.addElement(a);
        	}
        	
        	results = r;
        }
        
        // sort results
        
        if ( sort )
        	results = QSortAlgorithm.sort(results);
        
        return results;
    }
	*/
    
	/*
    public Vector findx(String name, String city, String state, Vector sa) throws MapopolisException
    
    {
    	city = city.toUpperCase();
    	state = state.toUpperCase();
    	
    	Vector r = new Vector();
    	
    	// 1 = address
    	// 2 = street
    	// 3 = intersection
    	
    	int type = 2;
    	String name1 = "", name2 = "";
    	
    	if ( getAddress(name) > 0 )
    		type = 1;
    	
    	int k = name.toUpperCase().indexOf(" AND ");
    	
    	if ( k > 0 )
    	{
    		name1 = name.substring(0, k);
    		name2 = name.substring(k + 5, name.length());
    		type = 3;
    	}

    	k = name.toUpperCase().indexOf(" & ");
    	
    	if ( k > 0 )
    	{
    		name1 = name.substring(0, k);
    		name2 = name.substring(k + 3, name.length());
    		type = 3;
    	}

    	for ( int i = 0; i < sa.size(); ++i )
    	{
    		SearchArea s = (SearchArea) sa.elementAt(i);
    		
    		if ( !s.sameName(city, state) )
    			continue;
    		
    		Vector v = null;
    		
    		if ( type == 1 )
    			v = MapSearch.searchAddress(name, SearchArea.searchAreaSearchFilter(s));
    		else if ( type == 2 )
    			v = MapSearch.searchStreet(name, SearchArea.searchAreaSearchFilter(s));
    		else
    			v = MapSearch.searchIntersection(name1, name2, SearchArea.searchAreaSearchFilter(s));
    		
    		Utilities.appendVector(r, v);
    	}
    	
    	Vector n = new Vector();
    	
    	for ( int i = 0; i < r.size(); ++i )
    	{
    		Match m = (Match) r.elementAt(i);
    		boolean found = false;
    		
    		for ( int j = 0; j < n.size(); ++j )
    			if ( m.equals((Match) n.elementAt(j)) )
    			{
    				found = true;
    				break;
    			}
    			
    		if ( !found )
    			n.addElement(m);
    	}
    	
    	return n;
    }
	*/

    public Match closestStreetFeatureToCoordinates(WorldCoordinates wc) throws MapopolisException

    {
    	return closestFeatureToCoordinates(wc, true, vecMapsAvailable);
    }
    	
    public Match closestFeatureToCoordinates(WorldCoordinates wc) throws MapopolisException
    
    {
    	return closestFeatureToCoordinates(wc, false, vecMapsAvailable);
    }

    public Match closestStreetFeatureToPixels(MapView mv, PixelCoordinates pix, GraphicsBuffer gb) throws MapopolisException

    {
    	return closestStreetFeatureToCoordinates(mv.convertPixelsToWorld(pix));
    }
	
    private static Match closestFeatureToCoordinates(WorldCoordinates wc, boolean streetOnly, Vector maps) throws MapopolisException
    
    {
    	int min = 999999999;
    	MapFeature closest = null;
		
		Box w = new Box(wc.x, wc.y, wc.x, wc.y);
    	
        for (int i = 0; i < maps.size(); ++i) 
        {
            MapFile map = (MapFile) maps.elementAt(i);

            for (int j = 0; j < map.dataRecords; ++j) 
            {
                MapFeatureRecord r = map.getMapFeatureRecords()[j];

				if ( !r.isFeatureRecord )
					continue;
				
				if ( !Box.overlapWithMargin(w, r.boundingBox, 100) )
					continue;

				int[] featureIndices = r.getFeatureIndices();
				int n = featureIndices[0];
				
				for ( int k = 1; k <= n; ++k )
				{
					MapFeature mf = new MapFeature(r, featureIndices[k]);						
						
                    int t = mf.getFeatureType();

                    if ( !streetOnly || (t == MapFeature.Street) ) 
                    {
                        int distance = mf.distanceToCoordinates(wc);

                        if (distance < min) 
                        {
                            min = distance;
                            closest = mf;
                        }
                    }
				}				
            }
        }

		if (closest == null)
			throw new MapopolisException("no feature found within 100 meters of point");

        if ( closest.isStreet() )
        	return new Match(closest, MapSearch.SearchStreetType);
        else if ( closest.isLandmark() )
        	return new Match(closest, MapSearch.SearchPointLocationType);
        else
        	
        	// allow more types later
        	
        	throw new MapopolisException("Invalid feature type - Map Search");
    }

	/**
     * Searches the map set for the closest feature to a given pixel location
     * 
     * @param x
     *            x pixel coordinate
     * @param y
     *            y pixel coordinate
     * @param streetsOrAddressesOnly
     *            if true returns only streets or addresses and not landmarks,
     *            city boundaries, etc.
     */

    Match closestFeatureToPixels(MapView mv, PixelCoordinates pix, GraphicsBuffer gb) throws MapopolisException

    {
        return closestFeatureToCoordinates(mv.convertPixelsToWorld(pix));
    }

    private boolean objectInVector(Vector v, Object s)

    {
        for (int i = 0; i < v.size(); ++i) 
        {
            Object t = v.elementAt(i);
            if ( s == t ) return true;
        }

        return false;
    }
    
    private boolean inVector(Vector v, String s)

    {
        for (int i = 0; i < v.size(); ++i) 
        {
            String t = (String) v.elementAt(i);
            if (s.equals(t)) return true;
        }

        return false;
    }

    private Vector search(MapFile map, String target, int searchType, SearchArea sa) throws MapopolisException

    {
        Vector results = new Vector();
        
		//Box searchBox = null;
        //SearchArea searchArea = null;
        
        //if ( filterx != null )
        //{
       	//	//searchBox = filterx.box;
       	//	searchArea = filterx.searchArea;
        //}
        
        // is all the data in uppercase???
        // what about fuzzy searching???

        target = target.toUpperCase();

        // will have parameters to define the search

        for (int i = 0; i < map.dataRecords; ++i) 
        {
            MapFeatureRecord rec = (map.getMapFeatureRecords())[i];

            if ( !rec.isFeatureRecord ) 
            	continue;

            //if ( searchBox != null )
			
            if ( !Box.overlap(sa.box, rec.boundingBox) )
            	continue;

 //System.out.println("get bytes");
  //          byte[] buffer = rec.getAsBytes();
// System.out.println("ok get bytes");
 
            //int index = 0;
//
            //while (true) 
           // {
            //    int rlen = IO.get2(buffer, index);

			int[] featureIndices = rec.getFeatureIndices();
			int n = featureIndices[0];
				
			for ( int k = 1; k <= n; ++k )
			{
				MapFeature mf = new MapFeature(rec, featureIndices[k]);
				
                if (mf.isFeatureTypeSearchingFor(searchType)) 
                {
                    // test the names

					String[] names = mf.getNames();

                    for (int j = 0; j < names.length; ++j)
                        if (names[j] != null)
                        	if (names[j].indexOf(target) >= 0) 
                            {
                                Match m = new Match(rec, featureIndices[k], searchType);
                                
                                //if ( searchBox != null )
                                //	if ( !m.mapFeature.inSearchBox(searchBox) )
                                //		continue;

                                if ( sa != null )
                                	if ( !inSearchArea(m.mapFeature, sa) )
                                		continue;
                                	
                                results.addElement(m);
                                
                                break;
                            }
                }

                if (isStopped) 
                	break;

               // if (rlen != 0) 
                //	index += rlen;
                //else 
                //	break;
            }

            if (isStopped) 
            	break;
        }

        return results;
    }

    public Vector fuzzySearch(Address a, int zip, boolean requireAddress, boolean compress, String countyState, boolean isCity, int m) throws MapopolisException

    {
		Vector fr;
		
		//Engine.out("fuzzy " + a + " " + countyState);
		
		if ( isCity )
			fr = featureRecordsOverlappingCounty(countyState);
			//fr = featureRecordsOverlappingCity(city);
		else
			fr = featureRecordsOverlappingZip(zip);
		
		if ( fr.size() == 0 )
			return null;
		
		Vector results = new Vector();

		leastScore = -1;
		
		for ( int i = 0; i < fr.size(); ++i )
		{
			MapFeatureRecord mfr = (MapFeatureRecord) fr.elementAt(i);

			int[] featureIndices = mfr.getFeatureIndices();
			int n = featureIndices[0];
				
			for ( int j = 1; j <= n; ++j )
			{
				evaluateFeature(mfr, featureIndices[j], a, zip, results, requireAddress, compress, m);
			}
		}

		//for ( int i = 0; i < results.size(); ++i )
		//	Engine.out("" + results.elementAt(i));
		
		return results;
    }

	private void evaluateFeature(MapFeatureRecord mfr, int index, Address a, int zip, Vector results, boolean requireAddress, boolean compress, int mm) throws MapopolisException
		
	{
		//String target = "" + (zip + 100000);
		//target = "# " + target.substring(1);

		MapFeature mapFeature = new MapFeature(mfr, index);
		String[] names = mapFeature.getNames();

		//if ( !mapFeature.inCity(target, null) )
		//	return;

		//Engine.out("evaluate " + mapFeature);
		
		for (int j = 0; j < names.length; ++j)
			if (names[j] != null)
			{
				Address n = new Address(names[j], false, 0);
				
				int m = Address.streetMatch(a, n, compress);

				
				
				
				//if ( a.getClean().indexOf("FRONT") >= 0 ) Engine.out("Test " + j + " " + names[j] + " " + m + " " + leastScore + " " + a.number);
				
				if ( m > leastScore )
					if ( a.getNumber() < 0 || !requireAddress )
					{
						//if ( names[j].indexOf("MASON ") >= 0 )
						//	Engine.out("add temp no addr " + names[j] + " " + a + " " + m);
						
						addTempResults(mfr, index, n, null, m, results);
					}
					else
					{
						StreetRelativeLocation al = mapFeature.streetRelativeLocationOfAddress(a.getNumber());
						if ( al != null ) 
						{
							//if ( names[j].indexOf("MASON ") >= 0 )
							//	Engine.out("add temp w addr " + names[j] + " " + a + " " + m);
							
							addTempResults(mfr, index, n, al, m, results);
						}
					}
			}
	}

	private void addTempResults(MapFeatureRecord mfr, int index, Address a, StreetRelativeLocation srl, int score, Vector r)
		
	{
		//Engine.out("add " + mfr + " " + index);
								
		// dont add if in already
		
		for ( int i = 0; i < r.size(); ++i )
		{
			TempResult t = (TempResult) r.elementAt(i);
			
			if ( t.mfr == mfr && t.index == index && t.score == score )
				return;
		}
		
		// keep top x results
		
		if ( r.size() < 10 )
		{
			TempResult t = new TempResult();
			
			t.mfr = mfr;
			t.index = index;
			t.score = score;
			t.address = a;
			t.srl = srl;
			
			r.addElement(t);
		}
		else
		{
			for ( int i = 0; i < r.size(); ++i )
			{
				TempResult t = (TempResult) r.elementAt(i);
				if ( t.score == leastScore )
				{
					t.mfr = mfr;
					t.index = index;
					t.score = score;
					t.address = a;
					t.srl = srl;
					
					break;
				}
			}
		}
		
		int m = 99999;

		for ( int i = 0; i < r.size(); ++i )
		{
			TempResult t = (TempResult) r.elementAt(i);
			if ( t.score < m )
				m = t.score;
		}
		
		leastScore = m;
	}

	//private Vector getMapsFromFilterxxx(SearchFilter filter)
    
    //{
     //   Vector maps = mapsAvailable;
     //   return maps;

		/*
        if ( filter != null )
        	if ( filter.searchArea != null )
	        {
	        	maps = new Vector();
	        	
	        	for ( int i = 0; i < filter.searchArea.mapAndIndices.size(); ++i )
	        	{
	        		MapFile m = ((MapAndIndex) filter.searchArea.mapAndIndices.elementAt(i)).map;
	        		
	        		if ( !objectInVector(maps, m) )
	        			maps.addElement(m);
	        	}
	        }
        	
        return maps;
		*/
    //}
    
    private int getAddress(String target)
    
    {
        int k = target.indexOf(" ");

        if (k < 1) 
        	return -1;

        String a = target.substring(0, k).trim();
        int address = -1;

        try 
		{
            address = new Integer(a).intValue();
        }

        catch (Exception e) 
		
		{
			Engine.out(e.toString()); e.printStackTrace();
        }

        if (address <= 0) 
        	return -1;
        else
        	return address;
    }
    
    private String getStreet(String target)
    
    {
        int k = target.indexOf(" ");

        if ( k < 1 || target.length() < k + 2 ) 
        	return "";
        else
        	return target.substring(k + 1).trim();
    }

    /*
    
    private Vector scanAllPointsForStreetNamex(MapFile map,
            MapFeatureRecord mapFeatureRecord, int index,
            String target, int searchType) throws MapopolisException

    {
        Vector results = new Vector();

        //System.out.println("look for " + target);

        index += 16;

        while (true) 
        {
            //System.out.println("get feature point " + index);

            FeaturePoint fp = new FeaturePoint(mapFeatureRecord, index, true, false, true);

            if (fp.isNode) 
            {
                for (int i = 0; i < fp.overlaps.overlapCount; ++i) 
                {
                    MapFeature ofeature = new MapFeature(map, fp.overlaps.overlapNodes[i]);

                    //byte[] buf = ofeature.mapFeatureRecord.getAsBytes();

                    if (isFeatureTypeSearchingFor(SearchIntersectionType, ofeature.mapFeatureRecord, ofeature.offsetInRecord)) 
                    {
                        String[] names = MapUtilities.getNames(ofeature.mapFeatureRecord, ofeature.offsetInRecord);

                        for (int j = 0; j < names.length; ++j)
                            if (names[j] != null)
                                    if (names[j].indexOf(target) >= 0) 
                                    {
                                        Match m = new Match(ofeature.mapFeatureRecord,
                                                ofeature.offsetInRecord,
                                                names[j], searchType);
                                        results.addElement(m);
                                    }
                    }
                }
            }

            if (fp.isLast) break;

            index = fp.nextIndex;
        }

        return results;
    }

    */
	
    public void stop()

    {
        isStopped = true;
    }
}