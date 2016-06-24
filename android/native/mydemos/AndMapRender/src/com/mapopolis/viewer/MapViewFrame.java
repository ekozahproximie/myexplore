package com.mapopolis.viewer;

import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mapopolis.viewer.core.MapFeature;
import com.mapopolis.viewer.core.MapFile;
import com.mapopolis.viewer.draw.MapView;
import com.mapopolis.viewer.engine.Engine;
import com.mapopolis.viewer.engine.MapopolisException;
import com.mapopolis.viewer.route.Route;
import com.mapopolis.viewer.route.RouteEndPoint;
import com.mapopolis.viewer.utils.ISortable;
import com.mapopolis.viewer.utils.PixelCoordinates;
import com.mapopolis.viewer.utils.QSortAlgorithm;
import com.mapopolis.viewer.utils.SearchArea;
import com.mapopolis.viewer.utils.WorldCoordinates;

public class MapViewFrame extends View

{
	MapView mv;
	RouteEndPoint origin, destination;
	Engine engine;
	public static int loop;
	Route paintRoute;
	Vector<MapFeature> selected;
	Canvas canvas = null;
	public static String OutputFileName;
	public static String MapDirectory;
	Context context=null;
	public MapViewFrame(Context context, AttributeSet ats, int ds) {
		super(context, ats, ds);
		init(context);
	}

	public MapViewFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MapViewFrame(Context context)

	{
		super(context);

		init(context);

	}

	public void init(Context context) {
		this.context=context;
		OutputFileName = "MapopolisOutput.txt";

		MapDirectory = "/sdcard/Maps/";
		// if (MapDirectory.charAt(MapDirectory.length() - 1) != '\\')
		// MapDirectory += "\\";
		try {
			start();
		} catch (Throwable e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	int nmaps = 8;

	class Holder {
		int zip3;
		String state;
		MapFile[] maps = new MapFile[nmaps];
		int n = 0;
	}

	void start() throws MapopolisException

	{
		// addMouseListener(this);
		// addWindowListener(new WA());

		engine = new Engine(MapDirectory, OutputFileName);
		engine.start();

		Engine.out("MapDirectory=" + MapDirectory);
		Engine.out("OutputFile=" + OutputFileName);

		mv = new MapView(engine.getMapSet());

		// tests the map files
		/*
		 * if (false) { for (int i = 0; i < engine.getMapSet().size(); ++i) {
		 * MapFile m = (MapFile) engine.getMapSet().elementAt(i); //
		 * Engine.out("Testing " + m); // m.test(); } }
		 */
		// set up the center of the view, zoom, and detail level
		mv.setZoom(8);
		mv.setDetail(5);
		// mv.setCenterPointWorld(-7460482, 4606752);

		/*
		 * GeoMatch gmd = null, dst = null;
		 * 
		 * // find an address if (false) { gmd = (GeoMatch) ((new
		 * WebServices()).mapopolisResult( "2469 Canterbury", "44118",
		 * engine.getMapSet())) .elementAt(0); mv.setCenterPointWorld(gmd.mapX,
		 * gmd.mapY); }
		 * 
		 * // find another address if (false) { dst = (GeoMatch) ((new
		 * WebServices()).mapopolisResult( "134 w currant", "16137",
		 * engine.getMapSet())).elementAt(0); mv.setCenterPointWorld(dst.mapX,
		 * dst.mapY); }
		 */
		setMinimumHeight(250);
		setMinimumWidth(250);

		// calculate a route
		/*
		 * if (false) { Route r = createRoute(gmd.mapX, gmd.mapY, dst.mapX,
		 * dst.mapY); Engine.out("" + r); }
		 */

		// mv.saveImage("D:\\kavi.gif");

		/*
		 * Vector t = new Vector();
		 * 
		 * for ( int i = 0; i < engine.getMapSet().size(); ++i ) { MapFile m =
		 * (MapFile) engine.getMapSet().elementAt(i); Vector c = m.getCities();
		 * for ( int j = 0; j < c.size(); ++j ) { SearchArea a =
		 * (SearchArea)c.elementAt(j); a.map = m;
		 * 
		 * if ( a.zip < 0 ) { t.add(a); } } }
		 * 
		 * t = QSortAlgorithm.sort(t);
		 */

		/*
		 * for ( int i = 0; i < t.size(); ++i ) { SearchArea a =
		 * (SearchArea)t.elementAt(i); int cx = (a.box.x0 + a.box.x1)/2; int cy
		 * = (a.box.y0 + a.box.y1)/2; int w = (a.box.x1 - a.box.x0)/2; int h =
		 * (a.box.y1 - a.box.y0)/2; Engine.out(exp(a.name, 50) + exp(cx, 10) +
		 * exp(cy, 10) + exp(w, 10) + exp(h, 10)); }
		 */

		// produceStatesInRegions(t);

		// Engine.out("done");

		/*
		 * GeoMatch gmd = WebServices.mapopolisResult("2469 Canterbury",
		 * "44118");
		 * 
		 * int cx = gmd.mapX; int cy = gmd.mapY - 5000;
		 * 
		 * mv.setCenterPointWorld(gmd.mapX, gmd.mapY);
		 * 
		 * setSize(500, 500); setVisible(true);
		 * 
		 * 
		 * paintRoute = createRoute(cx, cy);
		 * 
		 * repaint();
		 */

		/*
		 * if ( true ) { createRoute(-7249452, 3891380, -7249452, 3891380);
		 * createRoute(-7162841, 3994724, -7157591, 3991796);
		 * createRoute(-7546730, 3881294, -7547727, 3888191);
		 * createRoute(-7505209, 3880151, -7505456, 3888902);
		 * createRoute(-7260668, 4307211, -7255469, 4312735);
		 * createRoute(-7553764, 3891818, -7545085, 3888106);
		 * createRoute(-6999797, 4075989, -7001731, 4065712);
		 * createRoute(-7151688, 4410137, -7151097, 4395081);
		 * createRoute(-7671243, 3888344, -7669340, 3887951);
		 * createRoute(-8235264, 3623473, -7855311, 3690716);
		 * 
		 * 
		 * }
		 */

		// Engine.out("total time: " + (System.currentTimeMillis() - time) +
		// ", total time in subsection:" + Route.totalTime + ", loops:" +
		// Route.loops);

		/*
		 * //GeoMatch gmo = WebServices.mapopolisResult("2490 lee blvd",
		 * "44118");
		 * 
		 * if (true) { //GeoMatch gmd =
		 * WebServices.mapopolisResult("14103 Lakewood Hts Blvd", "44107");
		 * 
		 * //GeoMatch gmd = WebServices.mapopolisResult("240 Caldecott LN",
		 * "94618"); //GeoMatch gmd =
		 * WebServices.mapopolisResult("3423 cornell", "45220"); //GeoMatch gmd
		 * = WebServices.mapopolisResult("2469 Canterbury", "44118");
		 * 
		 * origin = RouteEndPoint.createRouteEndPoint(new
		 * WorldCoordinates(gmo.mapX, gmo.mapY)); destination =
		 * RouteEndPoint.createRouteEndPoint(new WorldCoordinates(gmd.mapX,
		 * gmd.mapY));
		 * 
		 * Engine.out(""); Engine.out("Origin " + origin);
		 * Engine.out("Destination " + destination);
		 * 
		 * Route r = Route.generateRoute(engine.getMapSet(), origin,
		 * destination, false, false);
		 * 
		 * Route.loops = 0; Route.totalTime = 0;
		 * 
		 * long time = System.currentTimeMillis(); for (loop = 0; loop < 10;
		 * ++loop) { r = Route.generateRoute(engine.getMapSet(), origin,
		 * destination, false, false); } Engine.out("total time: " +
		 * (System.currentTimeMillis() - time) + ", total time in subsection:" +
		 * Route.totalTime + ", loops:" + Route.loops);
		 * 
		 * //Engine.out("Match:" + gm + "==>" + gm.mapX + " " + gm.mapY); }
		 * 
		 * mv.setCenterPointWorld(gmo.mapX, gmo.mapY);
		 */

	}
    public void zoomin(){
    	mv.zoomIn();
    	invalidate();
    }
    public void zoomout(){
    	mv.zoomOut();
    	invalidate();
    }
	void produceStatesInRegions(Vector<SearchArea> t)

	{
		Vector<Holder> holders = new Vector<Holder>();
		for (int i = 0; i < t.size(); ++i) {
			SearchArea a = (SearchArea) t.elementAt(i);

			String state = a.name.substring(a.name.length() - 2, a.name
					.length());

			if (a.name.charAt(a.name.length() - 3) != ' ') {
				Engine.out("bad state " + a.name);
				continue;
			}

			// Engine.out(a.name + " " + state);

			Holder h = null;
			boolean zfound = false;
			for (int j = 0; j < holders.size(); ++j) {
				h = (Holder) holders.elementAt(j);

				if (h.state.equals(state)) {
					zfound = true;
					boolean found = false;
					for (int k = 0; k < h.n; ++k)
						if (h.maps[k] == a.map) {
							found = true;
							break;
						}
					if (!found) {
						if (h.n >= nmaps)
							Engine.out("overflow " + a.zip + " " + a.map);
						else {
							h.maps[h.n] = a.map;
							h.n++;
						}
					}
					break;
				}
			}

			// add new

			if (!zfound) {
				h = new Holder();
				holders.add(h);
				h.state = state;
				h.maps[h.n] = a.map;
				h.n++;
			}
		}

		// for ( int z = 0; z < 1000; ++z )
		// {
		Holder h = null;
		// boolean found = false;

		for (int i = 0; i < holders.size(); ++i) {
			h = (Holder) holders.elementAt(i);

			String s = "" + h.state + ",";

			// Engine.out(s);

			for (int p = 0; p < nmaps; ++p) {
				if (h.maps[p] != null) {
					String name = h.maps[p].friendlyName();
					s += name.substring(name.length() - 2, name.length() - 0)
							+ ",";
				} else
					s += "00,";
			}

			Engine.out(s);
		}
	}

	void produceZipsInRegions(Vector<SearchArea> t)

	{
		Vector<Holder> holders = new Vector<Holder>();
		for (int i = 0; i < t.size(); ++i) {
			SearchArea a = (SearchArea) t.elementAt(i);

			Holder h = null;
			boolean zfound = false;
			for (int j = 0; j < holders.size(); ++j) {
				h = (Holder) holders.elementAt(j);
				if (h.zip3 == a.zip / 100) {
					zfound = true;
					boolean found = false;
					for (int k = 0; k < h.n; ++k)
						if (h.maps[k] == a.map) {
							found = true;
							break;
						}
					if (!found) {
						if (h.n >= nmaps)
							Engine.out("overflow " + a.zip + " " + a.map);
						else {
							h.maps[h.n] = a.map;
							h.n++;
						}
					}
					break;
				}
			}

			// add new

			if (!zfound) {
				h = new Holder();
				holders.add(h);
				h.zip3 = a.zip / 100;
				h.maps[h.n] = a.map;
				h.n++;
			}
		}

		for (int z = 0; z < 1000; ++z) {
			Holder h = null;
			boolean found = false;
			for (int i = 0; i < holders.size(); ++i) {
				h = (Holder) holders.elementAt(i);
				if (h.zip3 == z) {
					found = true;
					break;
				}

			}

			String s = "" + z + ",";

			for (int p = 0; p < nmaps; ++p) {
				if (found) {
					if (h.maps[p] != null) {
						String name = h.maps[p].friendlyName();
						s += name.substring(name.length() - 2,
								name.length() - 0)
								+ ",";
					} else
						s += "00,";
				} else {
					s += "00,";
				}
			}

			Engine.out(s);
		}
	}

	String exp(String s, int k) throws MapopolisException {
		if (s.length() > k - 1) {
			throw new MapopolisException("exp");
		} else {
			int len = s.length();
			for (int i = 0; i < k - len; ++i)
				s = s + " ";
			return s;
		}
	}

	String exp(int n, int k) throws MapopolisException {
		return exp("" + n, k);
	}

	private Route createRoute(int x0, int y0, int x1, int y1)
			throws MapopolisException

	{
		mv.setCenterPointWorld((x0 + x1) / 2, (y0 + y1) / 2);
		// repaint();

		try {
			origin = RouteEndPoint.createRouteEndPoint(new WorldCoordinates(x0,
					y0), engine.getMapSet());
			destination = RouteEndPoint.createRouteEndPoint(
					new WorldCoordinates(x1, y1), engine.getMapSet());
		}

		catch (MapopolisException e) {
			Engine.out(e.toString());
		}

		if (origin == null || destination == null)
			return null;

		Engine.out("Origin " + origin + " " + origin.getX() + " "
				+ origin.getY());
		Engine.out("Destination " + destination + " " + destination.getX()
				+ " " + destination.getY());

		selected = new Vector<MapFeature>();
		selected.addElement(origin.getFeature());
		selected.addElement(destination.getFeature());

		return route(origin, destination, 0);
	}

	private Route createRoute(int cx, int cy, int n) throws MapopolisException {
		WorldCoordinates wc1, wc2;

		origin = destination = null;

		while (true) {
			wc1 = new WorldCoordinates(
					(int) (((Math.random() - 0.5) * 20000.0) + cx),
					(int) (((Math.random() - 0.5) * 20000.0) + cy));
			wc2 = new WorldCoordinates(
					(int) (((Math.random() - 0.5) * 20000.0) + cx),
					(int) (((Math.random() - 0.5) * 20000.0) + cy));

			if (WorldCoordinates.distanceBetweenCoordinates(wc1, wc2) < 15000
					&& WorldCoordinates.distanceBetweenCoordinates(wc1, wc2) > 5000)
				;
			else
				continue;

			try {
				origin = RouteEndPoint.createRouteEndPoint(wc1, engine
						.getMapSet());
				destination = RouteEndPoint.createRouteEndPoint(wc2, engine
						.getMapSet());
			}

			catch (MapopolisException e) {
				Engine.out(e.toString());
			}

			if (origin == null || destination == null)
				return null;

			if (origin.getX() == destination.getX()
					&& origin.getY() == destination.getY())
				return null;

			break;
		}

		Engine.out(wc1 + ", " + wc2);

		return route(origin, destination, n);

		// Engine.debug = false;

		// Route.loops = 0;
		// Route rt = Route.generateRoute(engine.getMapSet(), origin,
		// destination, false, false);

		// if (Route.loops < 500)
		// {
		// Engine.debug = true;

		// }
		// else
		// {
		// Engine.out("From " + origin + " to " + destination);
		// }

		// return r;
	}

	private Route route(RouteEndPoint origin, RouteEndPoint destination, int n)
			throws MapopolisException

	{
		Route.loops = 0;

		long time = System.currentTimeMillis();

		Route r = new Route(engine.getMapSet(), origin, destination, false,
				false);
		r.generateRoute();

		long iv = (System.currentTimeMillis() - time) * 1000;

		if (r == null) {
			Engine.out("Origin " + origin + " " + origin.getX() + " "
					+ origin.getY());
			Engine.out("Destination " + destination + " " + destination.getX()
					+ " " + destination.getY());
			Engine.out("NO ROUTE");
			return null;
		}

		int ratio = 0;

		if (r.straightLineDistance > 0)
			ratio = (r.totalTripDistance() * 100) / r.straightLineDistance;

		if (ratio > 300 || iv > 3000000) {
			Engine.out("Route " + n);
			Engine.out("Origin " + origin + " " + origin.getX() + " "
					+ origin.getY());
			Engine.out("Destination " + destination + " " + destination.getX()
					+ " " + destination.getY());
			Engine.out("Line distance:" + r.straightLineDistance);
			Engine.out("Trip distance:" + r.totalTripDistance());
			Engine.out("Ratio:" + ratio);
			Engine.out("Time:" + iv / 1000);
			Engine.out("Loops:" + Route.loops);
			Engine.out("");
		}

		return r;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		this.canvas = canvas;
		System.out.println("on draw");
		PixelCoordinates pos = new PixelCoordinates(0, 0);
		mv.renderFull(canvas, getWidth(), getHeight(), pos, paintRoute,
				selected);

	}

	/*
	 * int empty(Object a, WorldCoordinates b, Match m, int x, int y)
	 * 
	 * { PixelCoordinates p = (PixelCoordinates)a; return (((x >> 16) + (y <<
	 * 16)) & 333) | 118972; }
	 */

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		try {
			// WorldCoordinates wc = WorldCoordinates.random();
			// paintRoute = createRoute(wc.x, wc.y);
			System.out.println("on touch");
			PixelCoordinates p = new PixelCoordinates((int) event.getX(),
					(int) event.getY());
			WorldCoordinates wc = mv.convertPixelsToWorld(p);

			mv.zoomIn();
			mv.setCenterPointWorld(wc.x, wc.y);
			invalidate();
			refreshDrawableState();

		}

		catch (Exception exception)

		{
			Engine.out(exception.toString());
			exception.printStackTrace();
		}

		// PixelCoordinates p = new PixelCoordinates(e.getX(), e.getY());
		// WorldCoordinates wc = mv.convertPixelsToWorld(p);

		// repaint();

		/*
		 * 
		 * PixelCoordinates px = new PixelCoordinates(0, 0); WorldCoordinates wx
		 * = new WorldCoordinates(0, 0); Match mx = null;
		 * 
		 * long time = System.currentTimeMillis(); for (int i = 0; i < 1000000;
		 * ++i) empty(px, wx, mx, i, i + 1); Engine.out("millis = " +
		 * (System.currentTimeMillis() - time));
		 * 
		 * try
		 * 
		 * { //Engine.out("click");
		 * 
		 * PixelCoordinates p = new PixelCoordinates(e.getX(), e.getY());
		 * WorldCoordinates wc = mv.convertPixelsToWorld(p);
		 * 
		 * mv.setCenterPointWorld(wc.x, wc.y); this.repaint();
		 * 
		 * 
		 * Match m = null; //Match m =
		 * MapSearch.closestStreetFeatureToPixels(mv, p, mv.getGB());
		 * 
		 * //Engine.out("" + m);
		 * 
		 * if ( false && m != null ) { StreetRelativeLocation srl =
		 * m.mapFeature.
		 * streetRelativeLocationOfCoordinates(mv.convertPixelsToWorld(p));
		 * 
		 * //Engine.out(m.friendlyName() + " " + srl);
		 * 
		 * //mv.setCenterPointWorld(mv.convertPixelsToWorld(p));
		 * //m.mapFeature.coordinatesOfCenter()); //repaint();
		 * 
		 * if ( origin == null ) origin =
		 * RouteEndPoint.createRouteEndPoint(mv.convertPixelsToWorld(p)); else
		 * destination =
		 * RouteEndPoint.createRouteEndPoint(mv.convertPixelsToWorld(p));
		 * 
		 * if ( origin != null && destination != null ) { Engine.out("Origin " +
		 * origin); Engine.out("Destination " + destination);
		 * Route.generateRoute(engine.getMapSet(), origin, destination, false,
		 * false); } } }
		 * 
		 * catch (MapopolisException execption)
		 * 
		 * { Engine.out(execption.toString()); execption.printStackTrace(); }
		 */
		// TODO Auto-generated method stub
		return true;
	}

	void dump(int map, int rec) throws MapopolisException

	{
		for (int i = 0; i < engine.getMapSet().size(); ++i) {
			MapFile m = (MapFile) engine.getMapSet().elementAt(i);

			if (m.friendlyName().toLowerCase().indexOf("region" + map) >= 0) {
				// Engine.out("Testing " + m);
				m.getMapFeatureRecords()[rec].dump();
				break;
			}
		}
	}

	void organizeMinors()

	{
		Vector<MinorSet> t = new Vector<MinorSet>();
		for (int i = 0; i < minors.length; ++i) {
			MinorSet ms = new MinorSet();
			t.add(ms);
			ms.m = minors[i][0].toUpperCase();

			for (int j = 0; j < 4; ++j)
				if (j < minors[i].length)
					ms.strings[j] = minors[i][j].toUpperCase();
				else
					ms.strings[j] = "";
		}

		t = QSortAlgorithm.sort(t);

		for (int i = 0; i < t.size(); ++i) {
			MinorSet ms = (MinorSet) t.elementAt(i);
			// Engine.out("\"" + ms.m + "\",\"" + ms.strings[1] + "\",\"" +
			// ms.strings[2] + "\",\"" + ms.strings[3] + "\",");
		}
	}

	static String[][] minors =

	{

	{ "Cty", "County" },

	{ "Rd", "Road" },

	{ "Ln", "Lane" },

	{ "St", "Strt", "Street" },

	{ "Cir", "Cr", "Circ", "Circle" },

	{ "Dr", "Drive" },

	{ "Hwy", "Highway" },

	{ "Ave", "Av", "Avenue" },

	{ "Blvd", "Boulevard" },

	{ "Ct", "Crt", "Court" },

	{ "Wy", "Way" },

	{ "Run" },

	{ "Trl", "Trail" },

	{ "Pl", "Place" },

	{ "Ter", "Trce", "Terrace" },

	{ "Row" },

	{ "Pky", "Parkway" },

	{ "Aly", "Ally", "Alley" },

	{ "Rue" },

	{ "Sq", "Sqr", "Square" },

	{ "Xing", "Crossing" },

	{ "Plz", "Plaza" },

	{ "Cv" },

	{ "Lp", "Loop" },

	{ "Pk", "Pike" },

	{ "Wlk", "Wk", "Walk" },

	{ "Pth", "Path" },

	{ "Cres", "Crescent" },

	{ "Byp", "By", "Bypass" },

	{ "Sp", "Spur" },

	{ "Cswy", "Cwy", "Causeway" },

	{ "Pass" },

	{ "Brg", "Brdg", "Br", "Bridge" },

	{ "Tunl", "Tnl", "Tunnel" },

	{ "Fwy", "Frwy", "Freeway" },

	{ "Rte", "Rt", "Route" },

	{ "Tpke", "Tpk", "Turnpike" },

	{ "Grd", "Grade" },

	{ "Wkwy", "Walkway" },

	{ "Expy", "Exp", "Expressway" },

	{ "Rmp", "Ramp" },

	{ "Thwy", "Thruway", "Twy", "Throughway" },

	{ "Skwy", "Sky", "Skyway" },

	{ "Ovps", "Op", "Overpass" },

	{ "Arc", "Arcade" },

	{ "Unp", "Underpass" },

	{ "RMRd" },

	{ "FMRd" },

	{ "Thfr", "Thf", "Thoroughfare" },

	{ "Mtwy", "Mty", "Mountainway" },

	{ "Tfwy" },

	{ "W", "West" },

	{ "N", "North" },

	{ "S", "South" },

	{ "E", "East" },

	{ "NW", "Northwest" },

	{ "SW", "Southwest" },

	{ "NE", "Northeast" },

	{ "SE", "Southeast" },

	{ "United" },

	{ "States" },

	{ "State" },

	{ "Mount", "Mt" },

	{ "Saint", "St" },

	{ "Oval", "Ov" }

	};

}

class MinorSet implements ISortable

{
	String m;
	String[] strings = new String[4];

	public int compareTo(ISortable s) {
		return m.compareTo(((MinorSet) s).m);
	}
}
