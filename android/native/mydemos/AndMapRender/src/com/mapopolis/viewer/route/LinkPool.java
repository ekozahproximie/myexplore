
package com.mapopolis.viewer.route;

import com.mapopolis.viewer.engine.MapopolisException;



class LinkPool

{
    private static final int MaxLookupSlots = 8000;
	private static final boolean fast = true;

	private int LinkPoolSize;
	
    private Link[] linkPool;

    private int[] lookupTableC;
    private int[] lookupTableE;

    private int poolHeadC;
    private int poolHeadE;
    
    LinkPool()

    {

    }

    void initialize(RouteEndPoint origin, RouteEndPoint destinationxxx, int lps) throws MapopolisException

    {
		LinkPoolSize = lps;
		
		//Engine.out("Pool size is " + lps);
		
        // there are two lookup tables
        // lookupTableC makes it fast to look up by cost so far
        // lookupTableE makes it fast to look up by estimated total cost
        
        if ( lookupTableC == null ) lookupTableC = new int[MaxLookupSlots];
		if ( lookupTableE == null)  lookupTableE = new int[MaxLookupSlots];

        for (int j = 0; j < MaxLookupSlots; ++j)
        {
            lookupTableC[j] = -1;
            lookupTableE[j] = -1;
        }

		boolean newpool = (linkPool == null || linkPool.length != LinkPoolSize);
		
		if ( newpool ) linkPool = new Link[LinkPoolSize];        
        
        for (int i = 0; i < LinkPoolSize; ++i) 
        {
        	if ( newpool ) linkPool[i] = new Link(i);
        	
            linkPool[i].costToEnd = Link.Invalid;
            linkPool[i].estimatedCostToDestination = Link.BigLong;
			linkPool[i].endPoint = null;

			if (i == 0) 
            {
                linkPool[i].estfindex = i + 1;
                linkPool[i].estbindex = -1;
            } 
            else if (i == LinkPoolSize - 1) 
            {
                linkPool[i].estfindex = -1;
                linkPool[i].estbindex = i - 1;
            } 
			else 
            {
                linkPool[i].estfindex = i + 1;
                linkPool[i].estbindex = i - 1;
            }
        }

        int c = 0;

        for (int i = 0; i < 2; ++i) 
            if (origin.links[i] != null) c++;

		/*
		for (int i = 0; i < 2; ++i) 
        {
            //Link link = origin.links[i];

			if (origin.links[i] != null) 
            {
				Link link;

                if (i == 1 && c == 1)
                {
					link = linkPool[0];
                	//link.myIndex = 0;
                	//linkPool[0] = link;

                }
                else
                {
					link = linkPool[i];
                	//link.myIndex = i;
                	//linkPool[i] = link;
                }

				link.costToEnd = 0;
				link.estimatedCostToDestination = 0;

				
				//boolean directionIncreasing;
				///int myIndex = -1;



				//Engine.out("" + link.endPoint);
				//Engine.out("" + link.endPoint.myMapFeatureRecord);
				//Engine.out("" + link.endPoint.myMapFeatureRecord.mapFile);
				
                link.endPoint.myMapFeatureRecord.mapFile.getCostArray().setCurrentCost(link.myLinkID(), 0, -1, -1, false);
                
                // System.out.println("Start link " + link);
            }
        }
		*/

        // set up the forward and backward links in the tables

        if (c == 2) 
        {
            linkPool[0].costfindex = 1;
            linkPool[0].costbindex = -1;
            linkPool[1].costfindex = -1;
            linkPool[1].costbindex = 0;
            poolHeadC = 0;

            linkPool[0].estfindex = 1;
            linkPool[0].estbindex = LinkPoolSize - 1;
            linkPool[1].estfindex = -1;
            linkPool[1].estbindex = 0;
            linkPool[2].estfindex = 3;
            linkPool[2].estbindex = -1;
            linkPool[LinkPoolSize - 1].estfindex = 0;
            linkPool[LinkPoolSize - 1].estbindex = LinkPoolSize - 2;
            poolHeadE = 2;

			linkPool[0].endPoint = origin.links[0].endPoint;
			linkPool[0].directionIncreasing = origin.links[0].directionIncreasing;
			linkPool[0].costToEnd = 0;
			linkPool[0].estimatedCostToDestination = 0;
			linkPool[0].endPoint.myMapFeatureRecord.mapFile.getCostArray().setCurrentCost(linkPool[0].myLinkID(), 0, -1, -1, false);

			linkPool[1].endPoint = origin.links[1].endPoint;
			linkPool[1].directionIncreasing = origin.links[1].directionIncreasing;
			linkPool[1].costToEnd = 0;
			linkPool[1].estimatedCostToDestination = 0;
			linkPool[1].endPoint.myMapFeatureRecord.mapFile.getCostArray().setCurrentCost(linkPool[1].myLinkID(), 0, -1, -1, false);
        } 
        else 
        {
            linkPool[0].costfindex = -1;
            linkPool[0].costbindex = -1;
            poolHeadC = 0;

            linkPool[0].estfindex = -1;
            linkPool[0].estbindex = LinkPoolSize - 1;
            linkPool[1].estfindex = 2;
            linkPool[1].estbindex = -1;
            linkPool[LinkPoolSize - 1].estfindex = 0;
            linkPool[LinkPoolSize - 1].estbindex = LinkPoolSize - 2;
            poolHeadE = 1;

			int i;

			if (origin.links[0] != null)
				i = 0;
			else
				i = 1;

			linkPool[0].endPoint = origin.links[i].endPoint;
			linkPool[0].directionIncreasing = origin.links[i].directionIncreasing;
			linkPool[0].costToEnd = 0;
			linkPool[0].estimatedCostToDestination = 0;
			
			// think this should be linkPool[0].myLinkID() 1/25/2007
			linkPool[0].endPoint.myMapFeatureRecord.mapFile.getCostArray().setCurrentCost(linkPool[0].myLinkID(), 0, -1, -1, false);
        }
    }
	
    void delete(Link link)
    
    {
		int myIndex = link.myIndex;
		
		removeFromQueueC(myIndex);
		removeFromLookupC(myIndex);

		removeFromQueueE(myIndex);
		removeFromLookupE(myIndex);

    	linkPool[myIndex].costToEnd = Link.Invalid;
    	linkPool[myIndex].estimatedCostToDestination = Link.BigLong;
    	linkPool[myIndex].endPoint = null;
    	
		addToQueueE(myIndex);
		addToLookupE(myIndex);
    }

	Link getMinimumCostLink() throws MapopolisException
    
    {
    	if ( fast )
    	{
	    	if ( poolHeadC < 0 )
	    		return null;
	    	else
	    	{
	    		return linkPool[poolHeadC];
	    	}
    	}
    	else
    	{
	    	int min = Link.BigLong;
	    	int index = -1;
	    	
	    	for ( int i = 0; i < LinkPoolSize; ++i )
	    	{
	    		Link l = linkPool[i];
	    		
	    		if ( l.costToEnd >= 0 )
		    		if ( l.costToEnd < min )
		    		{
		    			min = l.costToEnd;
		    			index = i;
		    		}
	    	}
	
	    	if ( index >= 0 )
	    	{
	    		linkPool[index].myIndex = index;
	    		return linkPool[index];
	    	}
	    	else
	    		return null;
    	}
    }
    
    Link getMaximumEstimatedCostLink(int doNotUse) throws MapopolisException
    
    {
    	if ( fast )
    	{
			int index = poolHeadE;
	
			if ( index == doNotUse )
				index = linkPool[index].estfindex;
			
			return linkPool[index];
    	}
    	else
    	{
	    	int max = 0;
	    	int index = -1;
	    	
	    	for ( int i = 0; i < LinkPoolSize; ++i )
	    	{
	    		if ( i == doNotUse )
	    			continue;
	    		
	    		Link l = linkPool[i];
	    		
	    		if ( l.estimatedCostToDestination > max )
	    		{
	    			max = l.estimatedCostToDestination;
	    			index = i;
	    		}
	    	}
	
	    	if ( index >= 0 )
	    	{
	    		linkPool[index].myIndex = index;
	    		return linkPool[index];
	    	}
	    	else
	    		throw new MapopolisException("Invalid - Link");
    	}
    }
    
    int numberOfFreeLinks()
    
    {
    	int c = 0;
    	
    	for ( int i = 0; i < LinkPoolSize; ++i )
    		if ( linkPool[i].free() )
    			c++;
    		
    	return c;    
    }

	void addToQueueC(int index)
	
	{
		Link link = linkPool[index];
		int last, current;
		Link l;
		int cost = link.costToEnd;
		int limit, i;
	
		current = poolHeadC;
	
		if ( cost >= 0 && cost < MaxLookupSlots )
		{
			if ( cost < 25 )
				limit = 0;
			else
				limit = cost - 25;
	
			for ( i = cost; i >= limit; --i )
				if ( lookupTableC[i] != -1 )
				{
					current = lookupTableC[i];
					break;
				}
		}
	
		if ( current >= 0 )
			last = linkPool[current].costbindex;
		else
			last = -1;
	
		// loop for moving forward from wherever we start
	
		while ( true )
		{
			if ( current < 0 )
			{
				if ( last < 0 )
				{
					link.costfindex = -1;
					link.costbindex = -1;
					poolHeadC = index;
					return;
				}
				else
				{
					// place at end
	
					linkPool[last].costfindex = index;
					link.costfindex = -1;
					link.costbindex = last;
					return;
				}
			}
	
			l = linkPool[current];
	
			if ( cost <= l.costToEnd )
			{
				if ( last < 0 )
				{
					link.costfindex = current;
					link.costbindex = -1;
					l.costbindex = index;
					poolHeadC = index;
					return;
				}
				else
				{
					link.costfindex = current;
					link.costbindex = last;
					l.costbindex = index;
					linkPool[last].costfindex = index;
					return;
				}
			}
	
			last = current;
			current = l.costfindex;
		}
	}

	void removeFromQueueC(int index)
	
	{
		Link link = linkPool[index];
	
		if ( link.costToEnd == Link.Invalid )
			return;
		
		if ( poolHeadC == index ) poolHeadC = link.costfindex;
		if ( link.costbindex >= 0 ) linkPool[link.costbindex].costfindex = link.costfindex;
		if ( link.costfindex >= 0 ) linkPool[link.costfindex].costbindex = link.costbindex;
	}

	void addToQueueE(int index)
	
	{
		Link link = linkPool[index];
		int last, current;
		Link l;
		int cost = link.estimatedCostToDestination;
		int limit, i;
	
		current = poolHeadE;
	
		if ( cost >= 0 && cost < MaxLookupSlots )
		{
			if ( cost > MaxLookupSlots - 25 - 1 )
				limit = MaxLookupSlots - 1;
			else
				limit = cost + 25;
	
			for ( i = cost; i <= limit; ++i )
				if ( lookupTableE[i] != -1 )
				{
					current = lookupTableE[i];
					break;
				}
		}
	
		if ( current >= 0 )
			last = linkPool[current].estbindex;
		else
			last = -1;
	
		// loop for moving forward from wherever we start
	
		while ( true )
		{
			if ( current < 0 )
			{
				if ( last < 0 )
				{
					link.estfindex = -1;
					link.estbindex = -1;
					poolHeadE = index;
					return;
				}
				else
				{
					// place at end
	
					linkPool[last].estfindex = index;
					link.estfindex = -1;
					link.estbindex = last;
					return;
				}
			}
	
			l = linkPool[current];
	
			if ( cost >= l.estimatedCostToDestination )
			{
				if ( last < 0 )
				{
					link.estfindex = current;
					link.estbindex = -1;
					l.estbindex = index;
					poolHeadE = index;
					return;
				}
				else
				{
					link.estfindex = current;
					link.estbindex = last;
					l.estbindex = index;
					linkPool[last].estfindex = index;
					return;
				}
			}
	
			last = current;
			current = l.estfindex;
		}
	}

	void removeFromQueueE(int index)
	
	{
		Link link = linkPool[index];
	
		if ( poolHeadE == index ) poolHeadE = link.estfindex;
		if ( link.estbindex >= 0 ) linkPool[link.estbindex].estfindex = link.estfindex;
		if ( link.estfindex >= 0 ) linkPool[link.estfindex].estbindex = link.estbindex;
	}

	void addToLookupC(int index)
	
	{
		Link link;
		int cost;

		link = linkPool[index];
		cost = link.costToEnd;
	
		if ( cost >= 0 && cost < MaxLookupSlots )
			if ( lookupTableC[cost] == -1 )
				lookupTableC[cost] = index;
	}

	void removeFromLookupC(int index)
	
	{
		int cost;

		cost = linkPool[index].costToEnd;
	
		if ( cost >= 0 && cost < MaxLookupSlots )
			if ( lookupTableC[cost] == index )
				lookupTableC[cost] = -1;
	}
							
	void addToLookupE(int index)
	
	{
		Link link;
		int cost;

		link = linkPool[index];
		cost = link.estimatedCostToDestination;
	
		if ( cost >= 0 && cost < MaxLookupSlots )
			if ( lookupTableE[cost] == -1 )
				lookupTableE[cost] = index;
	}

	void removeFromLookupE(int index)
	
	{
		int cost;

		cost = linkPool[index].estimatedCostToDestination;
	
		if ( cost >= 0 && cost < MaxLookupSlots )
			if ( lookupTableE[cost] == index )
				lookupTableE[cost] = -1;
	}
}