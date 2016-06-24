
package com.mapopolis.viewer.route;


import com.mapopolis.viewer.core.MapFeature;

class RouteStep 

{
	int distanceFromLastStep;
	int timeFromLastStep;

	int maneuverType;
	
	MapFeature streetFrom;
	MapFeature streetTo;
	
	String friendlyName;
}
