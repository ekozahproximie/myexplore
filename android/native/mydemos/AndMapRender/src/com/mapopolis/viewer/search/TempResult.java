
package com.mapopolis.viewer.search;

import com.mapopolis.viewer.engine.*;
import com.mapopolis.viewer.core.*;

class TempResult

{
	public MapFeatureRecord mfr;
	public int index; 
	public int score;
	public Address address;
	public StreetRelativeLocation srl;
	
	public String toString()
		
	{
		return "SCORE=" + score + " MFR=" + mfr + " INDEX=" + index;
	}
}
