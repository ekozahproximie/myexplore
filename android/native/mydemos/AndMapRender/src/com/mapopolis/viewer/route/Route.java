
package com.mapopolis.viewer.route;

import com.mapopolis.viewer.engine.*;
import com.mapopolis.viewer.utils.*;
import com.mapopolis.viewer.core.*;
import com.mapopolis.viewer.search.*;

import java.util.*;

public class Route

{
	private static final int SequenceIDChunkCode = 115;

	private static final int NoManeuver = 0;
	private static final int ContinueRight = 1;
	private static final int SlightRight = 2;
	private static final int Right = 3;
	private static final int ContinueLeft = 4;
	private static final int SlightLeft = 5;
	private static final int Left = 6;
	private static final int Continue = 7;
	private static final int ExitRight = 8;
	private static final int ExitLeft = 9;
	private static final int DestinationRight = 10;
	private static final int DestinationLeft = 11;
	private static final int RoundAboutR = 20;
	private static final int RoundAboutL = 21;
	private static final int TurnLeftOnto = 22;
	private static final int TurnRightOnto = 23;
	private static final int TurnLeftTo = 24;
	private static final int TurnRightTo = 25;
	
	public static int loops;
	public static long totalTime;

	///////////////////////////////////////////////////////////////////////////
	
	private RouteEndPoint origin, destination;
	private Link originLink, destinationLink;
	private LinkPool linkPool;
	private int[] destinationLinks = new int[2];
	private MapFile destinationMap;
	private Vector<MapFile> vecMaps;
	private Link previousLink;
	private int bestResultSoFar;	
	private int radiusBase;
	private int distDest, distOrig;
	private boolean favorHighways;
	private boolean avoidHighways;
	private int turnCost;
	private int type5NodeCost;
	private int originContinueLocal;
	private int originContinueNonLongHaul;
	private int originUseLocal;
	private int originUseNonLongHaul;
	private int destinationContinueLocal;
	private int destinationContinueNonLongHaul;
	private int destinationUseLocal;
	private int destinationUseNonLongHaul;
	
	private FeaturePoint startPoint;
	private FeaturePoint nextPoint;
	
	private boolean directionIncreasing;

	Vector vecRouteFeatures;
	public int straightLineDistance;
	
	private Vector<RouteElement> vecRouteElements;
	private Vector<RouteStep> vecRouteSteps;
	
	public Route(Vector<MapFile> ms, RouteEndPoint org, RouteEndPoint dst, boolean favorHighwaysOption, boolean avoidHighwaysOption) throws MapopolisException
		
	{
		vecMaps = ms;
				
    	origin = org;
    	destination = dst;

		favorHighways = favorHighwaysOption;
    	avoidHighways = avoidHighwaysOption;
	}
	
	public Route(Vector<MapFile> vecMs, Match originMatch, Match destinationMatch, boolean favorHighwaysOption, boolean avoidHighwaysOption) throws MapopolisException
		
	{
		vecMaps = vecMs;
				
    	origin = RouteEndPoint.createRouteEndPoint(originMatch, vecMaps);
    	destination = RouteEndPoint.createRouteEndPoint(destinationMatch, vecMaps);

		favorHighways = favorHighwaysOption;
    	avoidHighways = avoidHighwaysOption;
	}

	private void postRoute() throws MapopolisException

    {
    	int min = Link.BigLong, c;
    	Link tLink, dLink = null;
    	Vector vecLVectorinksInRoute;

    	for ( int i = 0; i < 2; ++i )
    		if ( destination.links[i] != null )
    		{
    			int id = destination.links[i].myLinkID();
    			c = destination.links[i].endPoint.myMapFeatureRecord.mapFile.getCostArray().getCurrentCost(id);
    			
    			if ( c < min )
    			{
    				min = c;
    				dLink = destination.links[i];
    			}
    		}
    		
    	if ( dLink == null )
    		throw new MapopolisException("Invalid dLink - Route");
    	
    	Vector vecLinks0 = new Vector();

    	tLink = destinationLink = dLink;
    	
    	int nextMapIndex, currMapIndex, olap;
    	boolean nextDir;
    	
    	nextMapIndex = tLink.endPoint.myMapFeatureRecord.mapFile.myIndex;
    	
    	while ( true )
    		
    	{
    		//if ( Engine.debug ) msg("Next route link " + tLink.toString());
    		
    		vecLinks0.addElement(tLink);

    		currMapIndex = nextMapIndex;

    		nextMapIndex = tLink.endPoint.myMapFeatureRecord.mapFile.getCostArray().getPreviousMapIndex(tLink.myLinkID());
    		olap = tLink.endPoint.myMapFeatureRecord.mapFile.getCostArray().getOverlap(tLink.myLinkID());
    		nextDir = tLink.endPoint.myMapFeatureRecord.mapFile.getCostArray().getPreviousDirection(tLink.myLinkID());
    		
    		//if ( Engine.debug ) msg(nextMapIndex + " " + currMapIndex + " " + olap + " " + nextDir + " " + tLink);
    		
    		// this is how the start links are marked
    		
    		if ( nextMapIndex < 0 || nextMapIndex == Link.BigLong )
    			break;
    		
    		MapFile newMap = (MapFile) vecMaps.elementAt(nextMapIndex);
    		
    		if ( nextMapIndex == currMapIndex )
    		{
    			if ( olap == 0 )
    			{
    				// same street - go opposite direction to get first point of link
    				
    				FeaturePoint p = tLink.endPoint.getNextNodePoint(!nextDir);
    				p.setSeqNumber();
    				
    				tLink = new Link(-1);
    				
    				tLink.endPoint = p;
    				tLink.directionIncreasing = nextDir;
    			}
    			else
    			{
    				// get the start point of this link
    				
    				FeaturePoint p = tLink.endPoint.getNextNodePoint(!tLink.directionIncreasing);
    				p.setSeqNumber();

    				tLink = new Link(-1);

					tLink.endPoint = FeaturePoint.getFeaturePoint(p.overlaps.overlapNodes[olap - 1], newMap, true, false, true);
        			tLink.endPoint.setSeqNumber();
        			tLink.directionIncreasing = nextDir;
    			}
    		}
    		else
    		{
	 			// if the map is different we must go through the "map-jumping" logic
				// to get the previous node

				// get the start point of this link
				
				FeaturePoint p = tLink.endPoint.getNextNodePoint(!tLink.directionIncreasing);
				p.setSeqNumber();

				tLink = new Link(-1);

				byte[] uid = p.getUID();
				int k = newMap.getAddressForUID(uid);
				
				if ( k == 0 )
					throw new MapopolisException("Invalid address - generate route - Route");

				FeaturePoint ePoint = FeaturePoint.getFeaturePoint(k, newMap, true, false, true);

				if ( olap > 0 )
					ePoint = FeaturePoint.getFeaturePoint(ePoint.overlaps.overlapNodes[olap - 1], newMap, true, false, true);
				
    			tLink.endPoint = ePoint;
    			tLink.endPoint.setSeqNumber();
    			tLink.directionIncreasing = nextDir;
    		}
    	}
    	
    	vecLVectorinksInRoute = new Vector<Link>();
    	
    	for ( int i = vecLinks0.size() - 1; i >= 0; --i )
    		vecLVectorinksInRoute.addElement(vecLinks0.elementAt(i));
    	
    	originLink = (Link) vecLVectorinksInRoute.elementAt(0);
    	
    	if ( Engine.debug )
    		for ( int i = 0; i < vecLVectorinksInRoute.size(); ++i )
    			Engine.out("route link " + i + " " + ((Link) vecLVectorinksInRoute.elementAt(i)).endPoint.getStreetName());

    	buildRouteElements(vecLVectorinksInRoute, vecMaps);
    	
    	/*
    	
    	for ( int i = 0; i < routeElements.size(); ++i )
    	{
    		RouteElement re = (RouteElement) routeElements.elementAt(i);
    		
    		if ( re.maneuver != NoManeuver )
    			System.out.println(re);
    	}
    	
    	*/
    	
    	
    	/////////////////////////////////
    	// now modify for roundabouts etc
    	/////////////////////////////////
    	
    	
    	/*
    	 
    	// roundabouts
	
		int offRounds = 0;
		
		for ( int i = 0; i < pathElements; ++i )
		{
			VirtualAddress nxtStreet = pathElementArray[i].street;
			
			if ( !round(nxtStreet, 0) )
				offRounds = 1;
			
			if ( offRounds ) // pathElementArray[i].maneuver != NoManeuver && offRounds )
			{
				if ( round(nxtStreet, 0) && i > 0 && !round(pathElementArray[i - 1].street, 0) )
				{
					// find the next non-roundabout maneuver
					
					// count number of turns
					
					// turn off all maneuvers up to and including turn off roundabout
					
					int j, c = 1;
					
					for ( j = i + 1; j < pathElements; ++j )
					{
						// still on roundabout
						
						if ( round(pathElementArray[j].street, 0) )
						{
							pathElementArray[j].maneuver = NoManeuver;
							
							// count intersections
							
							if ( numberOfNextLinksFrom(j - 1) > 1 )
								c++;
						}
						else
							break;
					}
	
					if ( j < pathElements )
					{
						// this sets the direction incorrectly if there is no maneuver here at i
						// however it doesnt matter because they are not announced differently
	
						if ( pathElementArray[i].maneuver == Left || pathElementArray[i].maneuver == SlightLeft || pathElementArray[i].maneuver == ContinueLeft || pathElementArray[i].maneuver == ExitLeft )
							pathElementArray[i].maneuver = RoundAboutL;
						else
							pathElementArray[i].maneuver = RoundAboutR;
	
						pathElementArray[i].roundaboutExitNumber = c;
						
						// j is the next street after the roundabout
						
						pathElementArray[j].maneuver = NoManeuver;
					}
				}
			}
		}
	
		// remove short un-named steps
	
		for ( int i = 1; i < pathElements; ++i )
		{
			if ( pathElementArray[i].maneuver != NoManeuver )
				if ( pathElementArray[i - 1].maneuver == Continue )
				{
					char name[NameStringLength];
					realGetStreetNameBuffered(realAddress(pathElementArray[i - 1].street), name, NameStringLength);
					
					if ( sEqual(name, "Z") || startsWith(name, "RAMP") ||  startsWith(name, "TO ") )
						if ( pathElementArray[i - 1].distance < 35 )
							pathElementArray[i - 1].maneuver = NoManeuver;
				}
		}	

    	*/
    	
    	// now create RouteSteps
    	
    	vecRouteSteps = new Vector<RouteStep>();
    	
    	// placeholder for first step
    	
    	vecRouteSteps.addElement(new RouteStep());
    	
    	int stepDistance = 0, stepTime = 0;
    	
    	for ( int i = 0; i < vecRouteElements.size(); ++i )
    	{
    		RouteElement re = (RouteElement) vecRouteElements.elementAt(i);
    		RouteElement prev = null;

    		if ( i > 0 )
    			prev = (RouteElement) vecRouteElements.elementAt(i - 1);
    		
    		if ( re.maneuver != NoManeuver )
    		{
    			RouteStep rs = new RouteStep();
    			
    			//////////////////////////////////////////////
    			// doesnt include first (origin) link distance
    			//////////////////////////////////////////////
    			
    			rs.distanceFromLastStep = stepDistance;
    			rs.timeFromLastStep = stepTime;
    			//rs.distanceFromOrigin = totalDistance;
    			rs.maneuverType = re.maneuver;
    			
    			if ( prev == null )
    				rs.streetFrom = null;
    			else
    				rs.streetFrom = prev.street;
    			
    			rs.streetTo = re.street;
    			rs.friendlyName = rs.streetTo.friendlyName();
    			
    			vecRouteSteps.addElement(rs);
    			
    			stepDistance = re.distance;
    			stepTime = re.time();
    		}
    		else
    		{
    			stepDistance += re.distance;
    			stepTime += re.time();
    			//totalDistance += re.distance;
    		}
    	}
    	
    	// last step
    	
    	RouteStep re = new RouteStep();
    	re.streetFrom = ((RouteStep) vecRouteSteps.elementAt(vecRouteSteps.size() - 1)).streetTo;
    	vecRouteSteps.addElement(re);
    	
    	// modify step 0
    	
    	RouteStep rs = (RouteStep) vecRouteSteps.elementAt(0);   	

		if 
		(
		(originLink.directionIncreasing ==
		(origin.streetRelativeLocation.sideOfStreet == MapFeature.SideLeft))
		)
		{
			rs.maneuverType = TurnLeftOnto;
		}
		else
		{
			rs.maneuverType = TurnRightOnto;
		}

		rs.friendlyName = origin.feature.friendlyName();


		// it is possible that there will be just a start 
		// and an end step with no maneuvers
		// in that case this is the last step of two


    	// modify step 1
    	
    	rs = (RouteStep) vecRouteSteps.elementAt(1);   	
    	rs.distanceFromLastStep = origin.streetRelativeLocation.distanceToNode(originLink.directionIncreasing);   			

		if (vecRouteSteps.size() > 2)
    		rs.timeFromLastStep = rs.distanceFromLastStep/rs.streetFrom.firstPoint().speed();
    	
    	// modify last step
    	
    	rs = (RouteStep) vecRouteSteps.elementAt(vecRouteSteps.size() - 1);   	
    	rs.distanceFromLastStep = destination.streetRelativeLocation.distanceToNode(!destinationLink.directionIncreasing);

		if (vecRouteSteps.size() > 2)
    		rs.timeFromLastStep = rs.distanceFromLastStep/rs.streetFrom.firstPoint().speed();
    	
		if 
		(
		(destinationLink.directionIncreasing ==
		(destination.streetRelativeLocation.sideOfStreet == MapFeature.SideLeft))
		)
		{
			rs.maneuverType = TurnLeftTo;
		}
		else
		{
			rs.maneuverType = TurnRightTo;
		}
		
		rs.friendlyName = destination.friendlyName();
    }
    
    private void buildRouteElements(Vector<Link> vecLinksInRoute, Vector<MapFile> vecMaps) throws MapopolisException
    
    {
    	vecRouteElements = new Vector<RouteElement>();
    	vecRouteFeatures = new Vector<RouteStep>();
    	
    	RouteElement previousRouteElement = null;
    	Link previousLink = null;
    	
    	// set to names that wont be matched
    	
    	String[] previousStreetNames = { "ZYX" };
    	String[] previousManeuverStreetNames = { "XYZ" };
    	
    	for ( int i = 0; i < vecLinksInRoute.size(); ++i )
    	{
    		Link l = (Link) vecLinksInRoute.elementAt(i);
    		
    		RouteElement r = new RouteElement();
    		vecRouteElements.addElement(r);
    		
    		r.endPoint = l.endPoint;
    		r.startPoint = r.endPoint.getNextNodePoint(!l.directionIncreasing);
    		r.startPoint.setSeqNumber();
    		
    		r.street = r.endPoint.getContainingFeature();
    		
    		if ( !contains(vecRouteFeatures, r.street) )
    			vecRouteFeatures.addElement(r.street);
    		
    		if ( !l.directionIncreasing )
    			r.distance = r.endPoint.distance;
    		else
    			r.distance = r.startPoint.distance;
    		    		
    		Vector vecOut = r.startPoint.allOutgoingStreets(vecMaps);

    		//for ( int j = 0; j < out.size(); ++j )
    		//	System.out.println(out.elementAt(j));

    		// the first real maneuver is at index = 1 and the last is at size - 2
    		// maneuver are associated with the start of the link

			int leavingAngle = -1, approachAngle = -1;
			
    		for ( int j = 0; j < vecOut.size(); ++j )
    		{
    			StreetSegment seg = (StreetSegment) vecOut.elementAt(j);
    			
    			if ( seg.street.equals(r.startPoint.getContainingFeature()) )
    				if ( seg.directionIncreasing == l.directionIncreasing )
    				{
    					leavingAngle = seg.outgoingAngle;
    					seg.leaving = true;
    				}
    			
    			if ( i > 0 )
    			{
    				if ( seg.street.equals(previousRouteElement.startPoint.getContainingFeature()) )
    					if ( seg.directionIncreasing != previousLink.directionIncreasing )
    					{
    						approachAngle = seg.outgoingAngle;
							approachAngle += 180;

							if ( approachAngle >= 360 )
								approachAngle -= 360;
							
							seg.approach = true;
    					}
    			}
    		}

			//System.out.println("");
			//System.out.println("At " + r.startPoint);
			//System.out.println(" turn " + angleDifference(leavingAngle, approachAngle) + 
			//					" number next " + numberOfNextLinksFrom(out) +
			//					" min other " + absMinimumOtherAllowedAngle(out, approachAngle));

    		if ( i > 0 )
    			r.maneuver = maneuver(r, previousRouteElement, previousStreetNames, previousManeuverStreetNames, vecOut, approachAngle, leavingAngle);
    		else
    			r.maneuver = NoManeuver;

    		///////////
    		
    		previousStreetNames = r.startPoint.getContainingFeature().getNames();
    		
    		if ( r.maneuver != NoManeuver )
    			previousManeuverStreetNames = previousStreetNames; 

    		previousLink = l;
    		previousRouteElement = r;
    	}
    }
    
    int maneuver(RouteElement re, RouteElement preRE, String[] lastNames, String[] lastManeuverNames, Vector vecOutgoing, int approach, int leave) throws MapopolisException
    
    {
    	int turnAngle = angleDifference(leave, approach);

    	int absTurnAngle = Math.abs(turnAngle);
    	int absMinOtherAngle = absMinimumOtherAllowedAngle(vecOutgoing, approach);
    	
    	re.turnAngle = turnAngle;

    	int nOutgoing = numberOfNextLinksFrom(vecOutgoing);
    	
    	// if same name and easily distinguished then no maneuver
    	
    	if ( sameNames(lastManeuverNames, re.street.getNames()) )
    	{
    		if ( nOutgoing <= 1 )
    			return NoManeuver;

    		if ( absTurnAngle < 45 && absMinOtherAngle > 60 )
    			return NoManeuver;

    		if ( absMinOtherAngle > 2 * absTurnAngle )
    			return NoManeuver;
    			
    		// this fixes the problem that occurs
    		// in Denver/Adams CO where E 56th St
    		// is in those two adjacent maps
    			
    		if ( absMinOtherAngle == absTurnAngle )
    			return NoManeuver;
    	}

    	// if first name the same eliminate but a little more strictly

    	if ( lastManeuverNames[0].equals(re.street.getNames()[0]) ||
    		 sameNames(lastNames, re.street.getNames())
    	   )
    	{
    		if ( nOutgoing <= 1 )
    			return NoManeuver;

    		if ( absTurnAngle < 20 && absMinOtherAngle > 70 )
    			return NoManeuver;

    		if ( absMinOtherAngle > 2 * absTurnAngle )
    			return NoManeuver;
    		
    		// if passing a freeway exit then it's not a maneuver
    		// can also use street types to determine
    		
    		if ( nOutgoing == 2 )
    			if ( re.startPoint.isOneWay() )
    				if ( alternateRoad(vecOutgoing).isExit() )
    					return NoManeuver;
    	}

    	if ( nOutgoing <= 1 && absTurnAngle < 40 )
    		return NoManeuver;

		// test whether this is an exit (an entrance ramp has only one next link)
		
    	boolean exit = false;
    	
    	if 	( re.street.isExit() )
    		exit = true;

    	if ( nOutgoing == 2 && re.startPoint.isOneWay() )
			if ( preRE.street.firstPoint().isOneWay() )
				if ( absTurnAngle < 25 )
				{
					int k = preRE.street.getStreetLevel();
						
					if ( k < 3 && k < re.street.getStreetLevel() )
						exit = true;
				}
				
		if ( exit )
		{
			if ( pathIsRightMost(vecOutgoing, approach) )
				return ExitRight;
			else
				return ExitLeft;
		}			
	
		// if the path is the rightmost (leftmost) street then classify
		// as right (left) turn even if all streets go left (right)
	
		if ( nOutgoing >= 2 && turnAngle < -7 && pathIsRightMost(vecOutgoing, approach) )
			return ContinueRight;
		
		if ( nOutgoing >= 2 && turnAngle > 7 && pathIsLeftMost(vecOutgoing, approach) )
			return ContinueLeft;
		
		// bifurcation
		
		if ( nOutgoing == 2 )
			if ( absMinOtherAngle < 45 && absTurnAngle < 45 )
			{
				if ( pathIsRightMost(vecOutgoing, approach) )
					return ContinueRight;
				else
					return ContinueLeft;
			}
	
		int climit;
		
		if ( absMinOtherAngle < absTurnAngle )
			climit = 0;
		else
			climit = 15;
		
		// the path is neither rightmost or leftmost
		
		if 		( turnAngle > 55 )		return Right;
		else if ( turnAngle > 25 )		return SlightRight;
		else if ( turnAngle > climit )	return ContinueRight;
		else if ( turnAngle > -climit )	return Continue;
		else if ( turnAngle > -25 )		return ContinueLeft;
		else if ( turnAngle > -55 )		return SlightLeft;
		else							return Left;
    }

    public void addRouteProgressListener(RouteProgressListener listener)

    {

    }

	/*
	public Route generateRoute(Vector ms, Match originMatch, Match destinationMatch, boolean favorHighwaysOption, boolean avoidHighwaysOption) throws MapopolisException
																																							   
	{
    	RouteEndPoint org = RouteEndPoint.createRouteEndPoint(originMatch);
    	RouteEndPoint dst = RouteEndPoint.createRouteEndPoint(destinationMatch);
		
		return generateRoute(maps, org, dst, favorHighwaysOption, avoidHighwaysOption);
	}
	*/
	
    public Route generateRoute() throws MapopolisException
    
	{
		straightLineDistance = Utilities.hypot(	Math.abs(origin.getX() - destination.getX()),
												Math.abs(origin.getY() - destination.getY()));

		if ( Engine.debug ) Engine.out("distance " + straightLineDistance);
		
		for ( int i = 1; i <= 3; ++i )
		{
			int poolSize = 300;
			
			// the street limitations should be set
			
			// may be avoid highways makes it search more intensively
			// and favor highways makes it more agressively highway only		
			
			if ( straightLineDistance > 1000000L )
			{
				if ( i == 1 ) 		
				{
					//minSpeed = 25;
					poolSize = 200;
					radiusBase = 7000;
				}
				else if ( i == 2 ) 	
				{
					//minSpeed = 15;
					poolSize = 300;
					radiusBase = 10000;
				}
				else if ( i == 3 ) 	
				{
					//minSpeed = 5;
					poolSize = 400;
					radiusBase = 30000;
				}
			}
			else if ( straightLineDistance > 100000L )
			{
				if ( i == 1 ) 		
				{
					//minSpeed = 20;
					poolSize = 500;
					radiusBase = 7000;
				}
				else if ( i == 2 ) 	
				{
					//minSpeed = 12;
					poolSize = 650;
					radiusBase = 10000;
				}
				else if ( i == 3 ) 	
				{
					//minSpeed = 3;
					poolSize = 800;
					radiusBase = 30000;
				}
			}
			else
			{
				if ( i == 1 ) 		
				{
					//minSpeed = 15;
					poolSize = 700;
					radiusBase = 7000;
				}
				else if ( i == 2 ) 	
				{
					//minSpeed = 7;
					poolSize = 1500;
					radiusBase = 10000;
				}
				else if ( i == 3 ) 	
				{
					//minSpeed = 0;
					poolSize = 2500;
					radiusBase = 30000;
				}
			}

			originContinueLocal = radiusBase;
			destinationContinueLocal = 3 * originContinueLocal;
	
			originUseLocal = 2 * originContinueLocal;
			destinationUseLocal = 3 * originUseLocal;

			originContinueNonLongHaul = 7 * originContinueLocal;
			destinationContinueNonLongHaul = 3 * originContinueNonLongHaul;
	
			originUseNonLongHaul = 2 * originContinueNonLongHaul;
			destinationUseNonLongHaul = 3 * originUseNonLongHaul;

    		// type 5 cost lower in Europe
    	
    		//if ( ((MapFile) maps.elementAt(0)).inEurope() )
    		//	type5NodeCost = 5;
    		//else
		
    		type5NodeCost = 10;
    			
    		// increase turn cost if favor highway option is on

    		if ( favorHighways )
    			turnCost = 20;
    		else
    			turnCost = 10;

			bestResultSoFar = Link.BigLong;
			
			// this may change bestResultSoFar:
			initializeDestinationLinks();
			
			
			//poolSize = 2000;
			
			
			linkPool = new LinkPool();
			linkPool.initialize(origin, destination, poolSize);
		
			Route r = generateRouteInternal();
			
			// if acceptable route then return
			
			if ( r != null )
				return r;
		}
		
		return null;
	}
		
	private Route generateRouteInternal() throws MapopolisException
						 
	{
    	while ( true )
    	{
			loops++;

			//if ((loops % 1000) == 0)
			//	Engine.out("loops:" + loops);
			
    		Link link = linkPool.getMinimumCostLink();
    		
    		if ( link == null )
    		{
    			// there are no more links to process
    			// see if we have a bestResultSoFar
    			// if not then no route could be found
    			
    			break;
    		}

    		if ( link.costToEnd >= bestResultSoFar )
    			break;

			//if ( Engine.debug ) Engine.out("Bdry " + ((link.endPoint.descriptor & FeaturePoint.Bdry) != 0));
    		
			// how far from each endpoint are we?

			distDest = Utilities.hypot(	Math.abs(link.endPoint.getX() - destination.getX()),
										Math.abs(link.endPoint.getY() - destination.getY()));

			distOrig = Utilities.hypot(	Math.abs(link.endPoint.getX() - origin.getX()),
										Math.abs(link.endPoint.getY() - origin.getY()));

			if ( Engine.debug ) Engine.out("Process link " + link);
			
			//String sl = link.toString();
			//Engine.out("Loop " + loops + " Extend cost " + link.costToEnd + " free links " + link.numberOfFreeLinks() + " best " + bestResultSoFar + " distances " + distOrig + " " + distDest + " " + (( (link.endPoint.descriptor & FeaturePoint.Bdry) != 0 )?" boundary " :""));
			
			// if this is a boundary node then step across maps
    		
    		if ( link.endPoint.isBoundaryNode() )
    		{
				//if ( Engine.debug ) Engine.out("**************Bdry Node");
				
    			MapFile map = link.endPoint.myMapFeatureRecord.mapFile;
    			
    			byte[] uid = link.endPoint.getUID();
    			int k = map.getAddressForUID(uid);

				FeaturePoint headPoint = FeaturePoint.getFeaturePoint(k, map, true, false, true);

				int o;
    			
    			// when we get to headpoint, we use olap to get to the correct link 
    			
    			if ( headPoint.equals(link.endPoint) )
    				o = 0;
    			else
    				o = getOlap(headPoint, link.endPoint, map);

				for ( int i = 0; i < vecMaps.size(); ++i )
    			{
    				MapFile m = (MapFile) vecMaps.elementAt(i);
    				
					//if ( Engine.debug ) Engine.out("Test " + m);
					
    				if ( map == m )
    					continue;
    				
    				if ( link.endPoint.overlapsMap(m) )
    				{
						//if ( Engine.debug ) Engine.out("Overlaps " + m);
						
    					k = m.getAddressForUID(uid);
    					
    					if ( k == 0 )
    						continue;

						FeaturePoint otherMapPoint = FeaturePoint.getFeaturePoint(k, m, true, false, true);
						
						otherMapPoint.getX();
						otherMapPoint.getY();

    					if ( (otherMapPoint.descriptor & FeaturePoint.TravelIncreasing) != 0 && !otherMapPoint.isLast )
    						processLink(otherMapPoint, true, o, link, link.directionIncreasing);
    											
    					if ( (otherMapPoint.descriptor & FeaturePoint.TravelDecreasing) != 0 && !otherMapPoint.isFirst )
    						processLink(otherMapPoint, false, o, link, link.directionIncreasing);

    					for ( int j = 0; j < otherMapPoint.overlaps.overlapCount; ++j )
    					{
    						k = otherMapPoint.overlaps.overlapNodes[j];

							FeaturePoint overlapPoint = FeaturePoint.getFeaturePoint(k, m, true, false, true);

							overlapPoint.setX(otherMapPoint.getX());
							overlapPoint.setY(otherMapPoint.getY());

    						if ( (overlapPoint.descriptor & FeaturePoint.TravelIncreasing) != 0 && !overlapPoint.isLast )
    							processLink(overlapPoint, true, o, link, link.directionIncreasing);
    								
    						if ( (overlapPoint.descriptor & FeaturePoint.TravelDecreasing) != 0 && !overlapPoint.isFirst )
    							processLink(overlapPoint, false, o, link, link.directionIncreasing);
    					}
    				}
    			}
    		}
    		
    		if 	( 	
    				(link.directionIncreasing && 
    				(link.endPoint.descriptor & FeaturePoint.TravelIncreasing) != 0 && 
					!link.endPoint.isLast) ||
					(!link.directionIncreasing && 
	    			(link.endPoint.descriptor & FeaturePoint.TravelDecreasing) != 0 && 
					!link.endPoint.isFirst)
    			)
    		{
    			processLink(link.endPoint, link.directionIncreasing, 0, link, link.directionIncreasing);
    		}

    		// this assumes that the endpoint of the link has overlaps built
    		
    		if ( !link.endPoint.isNode )
    			throw new MapopolisException("Not a node - Route");
    		
    		for ( int i = 0; i < link.endPoint.overlaps.overlapCount; ++i )
    		{
    			int k = link.endPoint.overlaps.overlapNodes[i];

				FeaturePoint overlapPoint = FeaturePoint.getFeaturePoint(k, link.endPoint.myMapFeatureRecord.mapFile, true, false, true);
    			
    			if ( !overlapPoint.isNode ) 
				{
					//Engine.out("endpoint " + link.endPoint);
					//Engine.out("overlap point " + overlapPoint);
    				throw new MapopolisException("Overlap not a node - Route");
				}
				
    			int olap = getOlap(overlapPoint, link.endPoint, link.endPoint.myMapFeatureRecord.mapFile);

				overlapPoint.setX(link.endPoint.getX());
				overlapPoint.setY(link.endPoint.getY());

    			if ( link.directionIncreasing )
    			{
    				if ( (k & 0x80000000) != 0 )		// ff
    					processLink(overlapPoint, true, olap, link, link.directionIncreasing);
    				
    				if ( (k & 0x40000000) != 0 ) 		// fb
    					processLink(overlapPoint, false, olap, link, link.directionIncreasing);
    			}
    			else
    			{
    				if ( (k & 0x20000000) != 0 )		// bf
    					processLink(overlapPoint, true, olap, link, link.directionIncreasing);
    				
    				if ( (k & 0x10000000) != 0 ) 		// bb
    					processLink(overlapPoint, false, olap, link, link.directionIncreasing);
    			}
    		}

    		linkPool.delete(link);
    	}

		if (bestResultSoFar < Link.BigLong)
		{
			//Engine.out("Create route");
			
			postRoute();
			cleanUp();
			return this;
		}
		else
		{
			Engine.out("No route found");
			cleanUp();
			return null;
		}
    }

	private void processLink(FeaturePoint sp, boolean dir, int olap, Link pLink, boolean previousDirection) throws MapopolisException
	
	{
		startPoint = sp;
		directionIncreasing = dir;
		previousLink = pLink;

		nextPoint = startPoint.getNextNodePoint(directionIncreasing);
		
		//if ( Engine.debug ) Engine.out("Consider " + nextPoint);

		// TODO implement fully as in Palm code
		
		if (linkLevelNotUsable())
		{
			return;
		}

		MapFile map = startPoint.myMapFeatureRecord.mapFile;
		
		setSeqNumbers();
		
		int linkCost = timeCostToDriveLink();		

		// get new total route cost with additional costs
    	
    	int newTotalCost = previousLink.costToEnd + linkCost;

    	// turn cost
    	
    	if ( olap != 0 ) 
    		newTotalCost += turnCost;
    	
    	// type 5 street cost
    	
    	if ( startPoint.localStreet ) 
    		newTotalCost += type5NodeCost;
    	
    	// cost for going through possible intersection
    	
    	newTotalCost += 1;
		
		// test that estimated speed is greater than min (see Palm Code)

    	int linkID = Link.makeLinkID(nextPoint.seqNumber, directionIncreasing);
    	int currentCost = map.getCostArray().getCurrentCost(linkID);

		if (newTotalCost >= currentCost)
		{
			return;
		}

		map.getCostArray().setCurrentCost(linkID, newTotalCost, previousLink.endPoint.myMapFeatureRecord.mapFile, olap, previousDirection);

    	// check whether destination has been reached

    	if ( (linkID == destinationLinks[0] || linkID == destinationLinks[1]) && (map == destinationMap) )
    	{
    		if ( newTotalCost < bestResultSoFar ) 
    			bestResultSoFar = newTotalCost;

			return;
    	}

    	// add the new link on top of the highest cost link
    	
    	Link link = linkPool.getMaximumEstimatedCostLink(previousLink.myIndex);

    	int index = link.myIndex;

		linkPool.removeFromQueueC(index);
		linkPool.removeFromLookupC(index);

		linkPool.removeFromQueueE(index);
		linkPool.removeFromLookupE(index);

    	link.endPoint = nextPoint;    	
    	link.costToEnd = newTotalCost;
    	
    	// if the divisior (3 in this case) is high then 
    	// the algorithm will favor keeping paths farther away

    	link.estimatedCostToDestination = newTotalCost + (distanceToEnd()>>3);
    	link.directionIncreasing = directionIncreasing;

    	linkPool.addToQueueC(index);
    	linkPool.addToLookupC(index);

    	linkPool.addToQueueE(index);
    	linkPool.addToLookupE(index);
		
		//if ( Engine.debug ) Engine.out("Add link " + link);
	}

	private void initializeDestinationLinks() throws MapopolisException
		
	{
		if (Engine.debug)
		{
			for ( int i = 0; i < 2; ++i )
			{
				if ( origin.links[i] != null )
					Engine.out("Origin link id " + origin.links[i].myLinkID());
			}
		}
		
    	for ( int i = 0; i < 2; ++i )
    	{
    		if ( destination.links[i] != null )
    		{
    			destinationLinks[i] = destination.links[i].myLinkID();
    			destinationMap = destination.links[i].endPoint.myMapFeatureRecord.mapFile;
    		}
    		else
    			destinationLinks[i] = -1;

    		if (Engine.debug) Engine.out("Destination link id " + destinationLinks[i]);
    	}
		
    	// if already at destination then set best result so far
    			
    	for ( int j = 0; j < 2; ++j )
			for ( int k = 0; k < 2; ++k )
			{
    			if ( origin.links[j] != null && destinationLinks[k] >= 0 )
    				if ( origin.links[j].myLinkID() == destinationLinks[k] )
    					bestResultSoFar = 0;
			}
		
		//if (Engine.debug) msg("best:" + bestResultSoFar);
	}
	
	private void setSeqNumbers() throws MapopolisException
		
	{
    	//if ( !nextPoint.isNode )
    	//	throw new MapopolisException("Invalid isnode - Route");

    	if ( startPoint.seqNumber > 0 )
    	{
    		if ( directionIncreasing )
    			nextPoint.seqNumber = startPoint.seqNumber + 1;
    		else
    			nextPoint.seqNumber = startPoint.seqNumber - 1;
    	}
    	else
    	{
    		if ( directionIncreasing )
    			startPoint.seqNumber = nextPoint.seqNumber - 1;
    		else
    			startPoint.seqNumber = nextPoint.seqNumber + 1;
    	}

    	// if ( debug ) System.out.println("Process link " + startPoint.seqNumber + " " + directionIncreasing + " on " + startPoint.getContainingFeature().getName());

    	//if ( startPoint.seqNumber <= 0 || nextPoint.seqNumber <= 0 )
    	//	throw new MapopolisException("Invalid seq number - Route");
	}

	private int timeCostToDriveLink() throws MapopolisException
		
	{
    	// calculate the link cost

    	int len;

    	if ( !directionIncreasing )
    		len = nextPoint.distance;
    	else
    		len = startPoint.distance;

    	//if ( Engine.debug ) 
		//	if ( len == 0 )
		//		Engine.out("From " + startPoint + " to " + nextPoint + " is length of " + len);
    	
		int sp = (((7 - startPoint.speedClass)  *  5 ) + 7);
		
    	int linkCost = (len + (sp>>1))/sp;

    	if ( linkCost < 0 )    		
			throw new MapopolisException("Invalid linkcost - Route");

		// use favorHighways and avoidHighways
    	
    	if ( avoidHighways )
    	{
    		if ( startPoint.speedClass == 1 )		linkCost = (23 * linkCost)/7;
			else if ( startPoint.speedClass == 2 )	linkCost = (19 * linkCost)/7;
			else if ( startPoint.speedClass == 3 )	linkCost = (15 * linkCost)/7;
			else if ( startPoint.speedClass == 4 )	linkCost = (11 * linkCost)/7;
    	}
    	else if ( favorHighways )
    	{
    		// favor high speed roads - decrease cost
    				
			if ( startPoint.speedClass == 1 )		linkCost = (3 * linkCost)/7;
    		else if ( startPoint.speedClass == 2 )	linkCost = (4 * linkCost)/7;
    		else if ( startPoint.speedClass == 3 )	linkCost = (5 * linkCost)/7;
    	}

    	// implement avoid link flag

    	if ( (startPoint.descriptor & FeaturePoint.Avoid) != 0 )
    	{
    		linkCost *= 8;

    		if ( linkCost < 1000 )
    			linkCost = 1000;
    	}

		//if ( Engine.debug )
		//	if ( linkCost == 0 )
		//		Engine.out("Cost " + linkCost + " from " + startPoint + " to " + nextPoint + " is length of " + len);
		
		return linkCost;
	}
	
	private int distanceToEnd() throws MapopolisException
		
	{
		return Utilities.hypot(	Math.abs(nextPoint.getX() - destination.getX()),
								Math.abs(nextPoint.getY() - destination.getY()));
	}

	private int distanceFromStart() throws MapopolisException
		
	{
		return Utilities.hypot(	Math.abs(nextPoint.getX() - origin.getX()),
								Math.abs(nextPoint.getY() - origin.getY()));
	}
	
	private boolean linkLevelNotUsable()
		
	{
		if ( true ) return false;
		
		int BigDistance = 10000;
		int VeryBigDistance = 100000;
		
		if ( nextPoint.localStreet )
			if ( distOrig > BigDistance && distDest > BigDistance ) 
				return true;

		if ( !nextPoint.longHaul )
			if ( distOrig > VeryBigDistance && distDest > VeryBigDistance ) 
				return true;
		
		return false;		
		
		//if ( true ) return false;
		
		// decide whether to skip this link if distant from both endpoints
		
		//previousLink.endPoint.localStreet
		
		/*
		boolean newLinkIsLocal;
		boolean prevLinkIsLocal;
		boolean newLinkIsLongHaul;
		boolean prevLinkIsLongHaul;
		
		if ( newLinkIsLocal )
			if ( prevLinkIsLocal )
			{
				// going from local to local
				if ( distOrig > originContinueLocal && distDest > destinationContinueLocal ) return true;
			}
			else
			{
				// non-local to local
				if ( distOrig > originUseLocal && distDest > destinationUseLocal );
			}
		
		if ( !newLinkIsLongHaul )
			if ( prevLinkIsLongHaul )
			{
				// long-haul to non-long-haul
			}
			else
			{
				// non-long-haul to non-long-haul
			}
		*/
	}
	
    private int getOlap(FeaturePoint p, FeaturePoint find, MapFile map) throws MapopolisException
    
    {
    	int olap;
    	
		if ( p.overlaps.overlapCount == 1 )
			olap = 1;
		else
		{
			olap = -1;

			int indexFind = find.myMapFeatureRecord.myIndex;

			for ( int j = 0; j < p.overlaps.overlapCount; ++j )
			{
				if ( (p.overlaps.overlapNodes[j] & 0x0000ffff) == find.idx )
					
					//if ( map.getMapFeatureRecords()[((p.overlaps.overlapNodes[j] >> 16) & 0x00000fff)].equals(find.myMapFeatureRecord) )
					
					if ( ((p.overlaps.overlapNodes[j] >> 16) & 0x00000fff) == indexFind )
					{
						olap = j;
						break;
					}
			}
			
			if ( olap < 0 )
				throw new MapopolisException("Invalid olap - getOlap Route");
			
			olap += 1;
		}

    	return olap;
    }

	private boolean contains(Vector<MapFeature> v, MapFeature m)
    
    {
    	for ( int i = 0; i < v.size(); ++i )
    		if ( m.equals((MapFeature) v.elementAt(i)) )
    			return true;

    	return false;
    }
    
    private int angleDifference(int leave, int approach)
    
    {
    	int a = leave - approach;

    	if ( a < -180 )
    		a += 360;
    		
    	if ( a > 180 )
    		a -= 360;		

    	return a;
    }
    
    private int absMinimumOtherAllowedAngle(Vector<StreetSegment> v, int approach)
    
    {
    	int min = 999;
    	
    	for ( int i = 0; i < v.size(); ++i )
    	{
    		StreetSegment seg = (StreetSegment) v.elementAt(i);

    		if ( !seg.approach && !seg.leaving && seg.allowed )
    		{
    			int a = Math.abs(angleDifference(seg.outgoingAngle, approach));

    			if ( a < min )
    				min = a;
    		}
    	}
    	
    	return min;    	
    }
    
    private boolean sameNames(String[] n, String[] m)
    
    {
    	if ( n.length != m.length )
    		return false;
    	
    	for ( int i = 0; i < n.length; ++i )
    		if ( !n[i].equals(m[i]) )
    			return false;
    		
    	return true;    	
    }
    
    private int numberOfNextLinksFrom(Vector<StreetSegment> v)
    
    {
    	int c = 0;
    	
    	for ( int i = 0; i < v.size(); ++i )
    	{
    		StreetSegment seg = (StreetSegment) v.elementAt(i);
    		
    		if ( !seg.approach && seg.allowed )
    			c++;    			
    	}
    	
    	return c;    	
    }
    
    private MapFeature alternateRoad(Vector<StreetSegment> v) throws MapopolisException
    
    {
    	for ( int i = 0; i < v.size(); ++i )
    	{
    		StreetSegment seg = (StreetSegment) v.elementAt(i);

    		if ( !seg.approach && !seg.leaving && seg.allowed )
    			return seg.street;
    	}
    	
    	throw new MapopolisException("Invalid outgoing roads - Route");
    }
    
    private boolean pathIsRightMost(Vector<StreetSegment> out, int approach)
    
    {
    	int max = -999;
    	int p = 0;
    	
    	for ( int i = 0; i < out.size(); ++i )
    	{
    		StreetSegment seg = (StreetSegment) out.elementAt(i);
    		
    		if ( !seg.allowed )
    			continue;
    		
    		if ( seg.approach )
    			continue;
    		
    		if ( seg.leaving )
    		{
    			p = angleDifference(seg.outgoingAngle, approach);
    			continue;
    		}

    		int a = angleDifference(seg.outgoingAngle, approach);

    		if ( a > max )
    			max = a;
    	}
    	
    	return (p > max);
    }

    private boolean pathIsLeftMost(Vector<StreetSegment> out, int approach)
    
    {
    	int min = 999;
    	int p = 0;
    	
    	for ( int i = 0; i < out.size(); ++i )
    	{
    		StreetSegment seg = (StreetSegment) out.elementAt(i);
    		
    		if ( !seg.allowed )
    			continue;
    		
    		if ( seg.approach )
    			continue;
    		
    		if ( seg.leaving )
    		{
    			p = angleDifference(seg.outgoingAngle, approach);
    			continue;
    		}

    		int a = angleDifference(seg.outgoingAngle, approach);

    		if ( a < min )
    			min = a;
    	}
    	
    	return (p < min);
    }
    
   public int totalTripTime() throws MapopolisException

   {
        return 0;
   }

   public int totalTripDistance() throws MapopolisException

   {
    	int t = 0;
		
		if ( vecRouteElements == null )
			return 0;
    	
    	for ( int i = 0; i < vecRouteElements.size(); ++i )
    	{
    		RouteElement r = (RouteElement) vecRouteElements.elementAt(i);
    		t += r.distance;
    	}

    	return t;
   }

   public int numberOfSteps()
   
   {
   	
   		return vecRouteSteps.size();
   		
   		//if ( routeSteps == null )
   		//	return 0;
   		//
   		// include turn-onto and turn-off steps
   		//
   		//return routeSteps.size() + 2;
   }
   
   public String routeStepText(int step) throws MapopolisException
   
   {
   		if ( step < 0 || step >= numberOfSteps() )
   			throw new MapopolisException("Invalid Step Number");
   		
   		RouteStep rs = (RouteStep) vecRouteSteps.elementAt(step);
   		
   		return rs.distanceFromLastStep + " (" + rs.timeFromLastStep + ") " + turnPhrase(rs) + " " + rs.friendlyName;
   }   

   /*
   
   public String routeStepText(int step) throws MapopolisException
   
   {
   		int distance;
   		
   		if ( step < 0 || step >= numberOfSteps() )
   			throw new MapopolisException("Invalid Step Number");

   		if ( step == 0 )
   		{
   			if 
			(
   			(originLink.directionIncreasing ==
   			(origin.streetRelativeLocation.sideOfStreet == MapopolisProgramSettings.SideLeft))
			)
   				return "Turn Left onto " + originLink.friendlyName();
   			else
   				return "Turn Right onto " + originLink.friendlyName();
   		} 
   		else if ( step == numberOfSteps() - 1 )
   		{
			distance = destination.streetRelativeLocation.distanceToNode(!destinationLink.directionIncreasing);
			String s;
			
   			if 
			(
   			(destinationLink.directionIncreasing ==
   			(destination.streetRelativeLocation.sideOfStreet == MapopolisProgramSettings.SideLeft))
			)
   				s = "Left";
   			else
   				s = "Right";
   			
   			return distance + " meters then " + "Turn " + s + " to " + destination.friendlyName();
   		}
   		else
   		{
   			RouteStep rs = (RouteStep) routeSteps.elementAt(step - 1);

   			if ( step == 1 )
				distance = origin.streetRelativeLocation.distanceToNode(originLink.directionIncreasing);
   			else
   				distance = rs.distanceFromLastStep;
   			
   			return distance + " meters then " + turnPhrase(rs) + " onto " + rs.streetTo.friendlyName(); // + " from " + rs.streetFrom.friendlyName();
   		}
   }
   
   */

   private String turnPhrase(RouteStep rs)
   
   {
   		String s = "";
   		
   		if ( rs.maneuverType == ContinueRight ) s = "Continue Right";
   		else if ( rs.maneuverType == SlightRight ) s = "Turn Slight Right";
   		else if ( rs.maneuverType == Right )  s = "Turn Right";
   		else if ( rs.maneuverType == ContinueLeft ) s = "Continue Left";
   		else if ( rs.maneuverType == SlightLeft ) s = "Turn Slight Left";
   		else if ( rs.maneuverType == Left ) s = "Turn Left";
   		else if ( rs.maneuverType == Continue ) s = "Continue" ;
   		else if ( rs.maneuverType == ExitRight ) s = "Exit Right";
   		else if ( rs.maneuverType == ExitLeft ) s = "Exit Left";
   		else if ( rs.maneuverType == DestinationRight ) s= "Destination Right";
   		else if ( rs.maneuverType == DestinationLeft ) s = "Destination Left";
   		else if ( rs.maneuverType == RoundAboutR ) s = "Take Roundabout Exit";
   		else if ( rs.maneuverType == RoundAboutL ) s = "Take Roundabout Exit";
   		else if ( rs.maneuverType == TurnLeftOnto ) s = "Turn Left Onto";
   		else if ( rs.maneuverType == TurnRightOnto ) s = "Turn Right Onto";
   		else if ( rs.maneuverType == TurnLeftTo ) s = "Turn Left To";
   		else if ( rs.maneuverType == TurnRightTo ) s = "Turn Right To";
   	
   		return Utilities.translate(s);
   }
   
   public Vector<RouteElement> routeElements()
   
   {
   		return vecRouteElements;
   }
   
   /*
   static String printBytes(byte[] b)
					 
   {
		String s = "[ ";
		for ( int i = 0; i < b.length; ++i ) s += b[i] + " ";
		return s + "]";
   }
   */
   
   private void cleanUp()
   
   {
		for ( int i = 0; i < vecMaps.size(); ++i )
		{
			MapFile m = (MapFile) vecMaps.elementAt(i);
			m.getCostArray().cleanUpCostArray();
		}
   }
   
    /*
    
    public int distanceFromLastStep(int step)
    
    {
    	
    }
    
    public int distanceToNextStep(int step)
    
    {
    	
    }
    		

    		
        	if ( link.endPoint.isBoundaryNode() )
        	{
        		Vector r = link.endPoint.allOverlappingPoints(maps);
        		
        		//if ( r.size() == 1 )
        		{
        		System.out.println("");
        		for ( int i = 0; i < r.size(); ++i )
        			System.out.println((FeaturePoint) r.elementAt(i));
        		}
        	}
        	

   
    
    */
}