
package com.mapopolis.viewer.route;

import com.mapopolis.viewer.engine.*;
import com.mapopolis.viewer.core.*;

public class CostArray

{
	private int[] costArray;
	
    // these few functions should be the ONLY place where the data in 
    // the cost is accessed or written so the format can be easily changed
    
	private int maxSeqId;
	
	public CostArray(int m)
			  
	{
		maxSeqId = m;	
	}

	int getCurrentCost(int linkID)
    
    {
    	if ( costArray == null )
    		return Link.BigLong;
    	
    	int data = costArray[linkID];
    	
    	data = (data>>14) & 0x3ffff; 	
    	
    	if ( data == 0x3ffff ) 
    		data = Link.BigLong;
    	
    	return data;
    }
    
    int getPreviousMapIndex(int linkID)
    
    {
    	if ( costArray == null )
    		return Link.BigLong;
    	
    	int data = costArray[linkID];
    	
    	data = (data>>5) & 0x1ff;

    	if ( data == 0x1ff ) 
    		data = Link.BigLong;
    	
    	return data;
    }
	
    int getOverlap(int linkID)
    
    {
    	if ( costArray == null )
    		return Link.BigLong;
    	
    	int data = costArray[linkID];
    	
    	data = (data>>1) & 0xf;	
    	
    	if ( data == 0xf ) 
    		data = Link.BigLong;
    	
    	return data;
    }
    
    boolean getPreviousDirection(int linkID)
    
    {
    	if ( costArray == null )
    		return false;
    	
    	int data = costArray[linkID];
    	
    	data = data & 0x1; 	
    	
    	return (data == 1);
    }

    void setCurrentCost(int linkID, int newTotalCost, MapFile map, int olap, boolean previousDirection) throws MapopolisException
    
    {
    	if ( costArray == null )
    		initializeCostArray();
    	
    	newTotalCost = (newTotalCost & 0x3ffff);
    	int mapIndex = (map.myIndex & 0x1ff);
    	olap = (olap & 0xf);
    	int dir = (previousDirection ? 1 : 0);

    	costArray[linkID] = (newTotalCost<<14) | (mapIndex<<5) | (olap<<1) | dir;
    }
    
    void setCurrentCost(int linkID, int newTotalCost, int mIndex, int olap, boolean previousDirection) throws MapopolisException
    
    {
    	if ( costArray == null )
    		initializeCostArray();
    	
    	newTotalCost = (newTotalCost & 0x3ffff);
    	int mapIndex = (mIndex & 0x1ff);
    	olap = (olap & 0xf);
    	int dir = (previousDirection ? 1 : 0);

    	costArray[linkID] = (newTotalCost<<14) | (mapIndex<<5) | (olap<<1) | dir;
    }
    
    private void initializeCostArray() throws MapopolisException
	
	{
    	int n = maxSeqId * 2 + 1;
    	costArray = new int[n];
    	
    	for ( int i = 0; i < n; ++i )
    		costArray[i] = -1;
	}
    
    void cleanUpCostArray()
    
    {
    	costArray = null;
    }
}
