package com.mapopolis.viewer.engine;

import android.os.Environment;
import android.util.Log;

import com.mapopolis.viewer.core.*;

import java.util.*;
import java.io.*;

public class Engine

{
	public static final boolean LargeDevice = false;
	// this causes all features and points of TYPE 1 and 2
	// feature records to be initialized at start
	public static final boolean InitMajor = false;
	public static final int MaxCachePages = 100;
	public static final int BoxMargin = 1000;
	public static final boolean debug = false;
	// public static final String ImageFile =
	// "D:\\Documents and Settings\\Administrator\\Desktop\\1.gif";//"D:\\Progra~1\\lws-3.0.3\\webapps\\ROOT\\1.gif";
	private static PrintStream out;
	public static Vector<String> allCategories;

	private boolean isAlreadyStarted = false;
	Vector<MapFile> mapSet;

	String MapDir;
	static String output;

	public Engine(String dir, String out)

	{
		MapDir = dir;
		output = out;
	}

	public void start() throws MapopolisException

	{
		if (isAlreadyStarted)
			return;

		try

		{

			try {

				boolean mExternalStorageAvailable = false;
				boolean mExternalStorageWriteable = false;
				String state = Environment.getExternalStorageState();

				if (Environment.MEDIA_MOUNTED.equals(state)) {
					// We can read and write the media
					mExternalStorageAvailable = mExternalStorageWriteable = true;
				} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
					// We can only read the media
					mExternalStorageAvailable = true;
					mExternalStorageWriteable = false;

				} else {
					// Something else is wrong. It may be one of many other
					// states, but all we need
					// to know is we can neither read nor write
					mExternalStorageAvailable = mExternalStorageWriteable = false;
				}
				File root = Environment.getExternalStorageDirectory();
				if (mExternalStorageAvailable &&mExternalStorageWriteable
						&& root.canWrite()) {
					File gpxfile = new File(root, "gpxfile.txt");
					gpxfile.createNewFile();
					FileWriter gpxwriter = new FileWriter(gpxfile, true);
					BufferedWriter out = new BufferedWriter(gpxwriter);
					out.write("Hello world");
					out.close();
				}
			} catch (IOException e) {
				Log.e("Error", "Could not write file " + e.getMessage());
			}

			Vector<String> vecMapNames = new Vector<String>();
			

			String[] stMapFiles = new File(MapDir).list();

			for (int i = 0; i < stMapFiles.length; ++i)
				if (stMapFiles[i].indexOf(".pdb") > 0) {
					// Engine.out(MapDir + files[i]);
					vecMapNames.addElement(MapDir + stMapFiles[i]);
					// break;
				}

			// initialize all maps

			System.out.println("init all maps");
			mapSet = MapFile.initAllMaps(vecMapNames);
			System.out.println("finish init all maps");
			/*if (false) {
				allCategories = new Vector<String>();

				for (int i = 0; i < mapSet.size(); ++i) {
					MapFile m = (MapFile) mapSet.elementAt(i);
					m.test();
				}

				// print categories

				for (int i = 0; i < allCategories.size(); ++i)
					Engine.out((String) allCategories.elementAt(i));
			}

			if (false) {
				File root = Environment.getExternalStorageDirectory();
				File gpxfile = null;
				if (root.canWrite()) {
					gpxfile = new File(root, "StatesInMapsDB.pdb");
					gpxfile.createNewFile();

				}
				RandomAccessFile in = new RandomAccessFile(gpxfile, "rw");

				in.seek(88);
				int k = 88;

				for (int i = 0; i < mapSet.size(); ++i) {
					MapFile m = (MapFile) mapSet.elementAt(i);
					byte[] line = m.statesInMapEntry();

					in.seek(k);
					k += 100;
					in.write(line);
				}

				byte[] line = new byte[100];
				line[0] = (byte) 'A';

				in.seek(k);
				k += 100;
				in.write(line);

				in.seek(k);
				k += 100;
				in.write(line);

				in.close();
			}
*/
			isAlreadyStarted = true;
		}

		catch (IOException e)

		{
			Engine.out(e.toString());
			e.printStackTrace();
		}
	}

	private String searchxxx(String addr, String city, String state)
			throws MapopolisException

	{
		/*
		 * Vector v1 = MapSearch.find(addr, city, state, allSearchAreas);
		 * 
		 * if ( v1.size() == 0 ) { return "No Match"; } else { Match match =
		 * ((Match) v1.elementAt(0));
		 * 
		 * MapView mv = new MapView(mapSet);
		 * 
		 * mv.setZoom(3); mv.setDetail(5);
		 * 
		 * mv.setCenterPointWorld(match.getWorldCoordinates());
		 * 
		 * mv.renderFull(null, 300, 200, new PixelCoordinates(0, 0), null,
		 * match); mv.saveImage(ImageFile);
		 * 
		 * return ((Match) v1.elementAt(0)).toString(); }
		 */

		/*
		 * 
		 * Match origin = (Match) v1.elementAt(0); Match destination = (Match)
		 * v2.elementAt(0);
		 * 
		 * Route r = Route.generateRoute(mapSet, origin, destination, false,
		 * false);
		 * 
		 * for ( int i = 0; i < r.numberOfSteps(); ++i )
		 * System.out.println(r.routeStepText(i)); r =
		 * Route.generateRoute(mapSet, destination, origin, false, false); for (
		 * int i = 0; i < r.numberOfSteps(); ++i )
		 * System.out.println(r.routeStepText(i));
		 */

		return null;
	}

	public Vector<MapFile> getMapSet()

	{
		return mapSet;
	}

	public static void out(String s)

	{
		try

		{
			if (out == null) {
				File root = Environment.getExternalStorageDirectory();
				if (root.canWrite()) {
					File gpxfile = new File(root, output);
					gpxfile.createNewFile();
					out = new PrintStream(new FileOutputStream(gpxfile));
				}

			}

			out.println(s);
			System.out.println(s);
		}

		catch (Exception e)

		{
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}
