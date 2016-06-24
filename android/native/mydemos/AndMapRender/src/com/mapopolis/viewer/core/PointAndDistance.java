package com.mapopolis.viewer.core;

import com.mapopolis.viewer.utils.*;

class PointAndDistance
{
	WorldCoordinates wc;
	int distance;

	PointAndDistance(int xx, int yy, int d)
	{
		wc = new WorldCoordinates(xx, yy);
		distance = d;
	}
}
