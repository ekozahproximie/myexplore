
package com.mapopolis.viewer.utils;

import java.util.*;
import java.io.*;
import java.awt.*;

import java.net.*;

import com.mapopolis.viewer.engine.*;

public class Utilities

{
	// new navcard format 
	//
	//	feature record format
	//
	//		2 bytes = length of the whole feature record
	//		1 byte = descriptor byte
	//		1 byte = extended type byte
	//		2 bytes = length of points block
	//		point array
	//		compressed name string
	
	private static final String[] fragments =
					
	{
		"Z","I-","TH",
		"US-"," RD@"," ST@",
		" AVE@"," BLVD@","EAST ","WEST ", "NORTH ",
		"SOUTH ","N ","S ","E ","W ",
		" LN@"," WAY@"," DR@","RAMP@",
		"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
		"Q","R","S","T","U","V","W","X","Y","Z",
		"0","1","2","3","4","5","6","7","8","9",
		// this stands for end of string
		" ",	// 56
		"@",
		"'",
		"&","|","-","#","/"
	};
    
	
    private static final int tans[] = {
			/* 0.5*/ 261,
			/* 1.5*/ 785,
			/* 2.5*/ 1309,
			/* 3.5*/ 1834,
			/* 4.5*/ 2361,
			/* 5.5*/ 2888,
			/* 6.5*/ 3418,
			/* 7.5*/ 3949,
			/* 8.5*/ 4483,
			/* 9.5*/ 5020,
			/* 10.5*/ 5560,
			/* 11.5*/ 6103,
			/* 12.5*/ 6650,
			/* 13.5*/ 7202,
			/* 14.5*/ 7758,
			/* 15.5*/ 8319,
			/* 16.5*/ 8886,
			/* 17.5*/ 9458,
			/* 18.5*/ 10037,
			/* 19.5*/ 10623,
			/* 20.5*/ 11216,
			/* 21.5*/ 11817,
			/* 22.5*/ 12426,
			/* 23.5*/ 13044,
			/* 24.5*/ 13671,
			/* 25.5*/ 14309,
			/* 26.5*/ 14957,
			/* 27.5*/ 15617,
			/* 28.5*/ 16288,
			/* 29.5*/ 16973,
			/* 30.5*/ 17671,
			/* 31.5*/ 18384,
			/* 32.5*/ 19112,
			/* 33.5*/ 19856,
			/* 34.5*/ 20618,
			/* 35.5*/ 21398,
			/* 36.5*/ 22198,
			/* 37.5*/ 23019,
			/* 38.5*/ 23863,
			/* 39.5*/ 24730,
			/* 40.5*/ 25622,
			/* 41.5*/ 26541,
			/* 42.5*/ 27489,
			/* 43.5*/ 28468,
			/* 44.5*/ 29480, 30001
		 };

	private static final int sc[] =
	
	{
	/* 0.5*/ 29998,
	/* 1.5*/ 29989,
	/* 2.5*/ 29971,
	/* 3.5*/ 29944,
	/* 4.5*/ 29907,
	/* 5.5*/ 29861,
	/* 6.5*/ 29807,
	/* 7.5*/ 29743,
	/* 8.5*/ 29670,
	/* 9.5*/ 29588,
	/* 10.5*/ 29497,
	/* 11.5*/ 29397,
	/* 12.5*/ 29288,
	/* 13.5*/ 29171,
	/* 14.5*/ 29044,
	/* 15.5*/ 28908,
	/* 16.5*/ 28764,
	/* 17.5*/ 28611,
	/* 18.5*/ 28449,
	/* 19.5*/ 28279,
	/* 20.5*/ 28100,
	/* 21.5*/ 27912,
	/* 22.5*/ 27716,
	/* 23.5*/ 27511,
	/* 24.5*/ 27298,
	/* 25.5*/ 27077,
	/* 26.5*/ 26848,
	/* 27.5*/ 26610,
	/* 28.5*/ 26364,
	/* 29.5*/ 26110,
	/* 30.5*/ 25848,
	/* 31.5*/ 25579,
	/* 32.5*/ 25301,
	/* 33.5*/ 25016,
	/* 34.5*/ 24723,
	/* 35.5*/ 24423,
	/* 36.5*/ 24115,
	/* 37.5*/ 23800,
	/* 38.5*/ 23478,
	/* 39.5*/ 23148,
	/* 40.5*/ 22812,
	/* 41.5*/ 22468,
	/* 42.5*/ 22118,
	/* 43.5*/ 21761,
	/* 44.5*/ 21397,
	/* 45.5*/ 21027,
	/* 46.5*/ 20650,
	/* 47.5*/ 20267,
	/* 48.5*/ 19878,
	/* 49.5*/ 19483,
	/* 50.5*/ 19082,
	/* 51.5*/ 18675,
	/* 52.5*/ 18262,
	/* 53.5*/ 17844,
	/* 54.5*/ 17421,
	/* 55.5*/ 16992,
	/* 56.5*/ 16558,
	/* 57.5*/ 16118,
	/* 58.5*/ 15674,
	/* 59.5*/ 15226,
	/* 60.5*/ 14772,
	/* 61.5*/ 14314,
	/* 62.5*/ 13852,
	/* 63.5*/ 13385,
	/* 64.5*/ 12915,
	/* 65.5*/ 12440,
	/* 66.5*/ 11962,
	/* 67.5*/ 11480,
	/* 68.5*/ 10995,
	/* 69.5*/ 10506,
	/* 70.5*/ 10014,
	/* 71.5*/ 9519,
	/* 72.5*/ 9021,
	/* 73.5*/ 8520,
	/* 74.5*/ 8017,
	/* 75.5*/ 7511,
	/* 76.5*/ 7003,
	/* 77.5*/ 6493,
	/* 78.5*/ 5981,
	/* 79.5*/ 5467,
	/* 80.5*/ 4951,
	/* 81.5*/ 4434,
	/* 82.5*/ 3915,
	/* 83.5*/ 3396,
	/* 84.5*/ 2875,
	/* 85.5*/ 2353,
	/* 86.5*/ 1831,
	/* 87.5*/ 1308,
	/* 88.5*/ 785,
	/* 89.5*/ 261,
		      0
	
	};
	
	private static int[] hypotenuseByRatio;

	static PixelCoordinates rotate(int x, int y, int angle)

    {
        // angle in must be 0 - 359
        // orientation is 0 - 31
        // angle is 0 - 7 = angle * 11.25 degrees

        if (angle > 359) angle = 359;
        if (angle < 0) angle = 0;

        int cos, sin, t;

        //x = * ix;
        //y = * iy;

        boolean large = (x > 10000 || x < -10000 || y > 10000 || y < -10000);

        int a = (angle * 100) / 1125;

        switch (a)

        {
            case 0:
            default:

                cos = 32767;
                sin = 0;

                break;
            case 1:
                cos = 32110;
                sin = 6532;

                break;
            case 2:
                cos = 30381;
                sin = 12275;

                break;
            case 3:
                cos = 27324;
                sin = 18085;

                break;
            case 4:
                cos = 23170;
                sin = 23170;

                break;
            case 5:
                cos = 18085;
                sin = 27324;

                break;
            case 6:
                cos = 12275;
                sin = 30381;

                break;
            case 7:
                cos = 6532;
                sin = 32110;

                break;
        }

        if (large) {
            cos = (cos >> 7);
            sin = (sin >> 7);
        }

        int quad = (angle >> 3);

        switch (quad)

        {
            case 0:
            default:

                break;

            case 1:

                t = cos;
                cos = sin;
                sin = t;

                cos = -cos;

                break;

            case 2:

                sin = -sin;
                cos = -cos;

                break;

            case 3:

                t = cos;
                cos = sin;
                sin = t;

                sin = -sin;

                break;
        }

        if (large) {
            x = (x * cos - y * sin) >> 8;
            y = (x * sin + y * cos) >> 8;
        } else {
            x = (x * cos - y * sin) >> 15;
            y = (x * sin + y * cos) >> 15;
        }

        return new PixelCoordinates(x, y);
    }

	public static int toInt(byte b)

    {
        return ((int) b) & 0xff;
    }

	public static int atan(int x, int y) throws com.mapopolis.viewer.engine.MapopolisException
		
	{
		// north = 0
		// increases clockwise	
	
		if (	  x >= 0 && y >= 0 && Math.abs(x) >= Math.abs(y) ) return 90 - at(y, x);
		else if ( x >= 0 && y >= 0 && Math.abs(y) > Math.abs(x)	 ) return at(x, y);
		else if ( x >= 0 && y <= 0 && Math.abs(x) >= Math.abs(y) ) return 90 + at(y, x);
		else if ( x >= 0 && y <= 0 && Math.abs(y) > Math.abs(x)	 ) return 180 -	at(x, y);
		else if ( x <= 0 && y <= 0 && Math.abs(x) >= Math.abs(y) ) return 270 - at(y, x);
		else if ( x <= 0 && y <= 0 && Math.abs(y) > Math.abs(x)	 ) return 180 +	at(x, y);
		else if ( x <= 0 && y >= 0 && Math.abs(x) >= Math.abs(y) ) return 270 + at(y, x);
		else if ( x <= 0 && y >= 0 && Math.abs(y) > Math.abs(x)	 )
		{
			int r = 360 - at(x, y);
			if ( r == 360 )
				r = 0;
			return r;
		}

		throw new com.mapopolis.viewer.engine.MapopolisException("Invalid arguments - atan - MapUtilities");
	}
	
	private static int at(int x, int y)
		
	{
		if ( x < 0 )
			x = -x;
		
		if ( y < 0 )
			y = -y;
		
		//if ( y < x )
		//	err(2517);
			
		while ( true )
		{
			if ( x < 50000 && y < 50000 )
				break;
				
			x = x/10;
			y = y/10;			
		}
	
		int kk, k;
	
		if ( y == 0 )
			kk = 29999;
		else	
			kk = (30000 * x)/y;
			
		k = kk;
	
		for ( int i = 0; i < 46; ++i )
		{
			if ( k < tans[i] )
				return i;
		}
		
		// err(2519);
		
		return 0;
	}
	
	public static Vector sortStrings(Vector v)
	
	{
		Vector r = new Vector();
		
		for ( int i = 0; i < v.size(); ++i )
			r.addElement(new S((String) v.elementAt(i)));
		
		r = QSortAlgorithm.sort(r);
		Vector a = new Vector();
		
		for ( int i = 0; i < r.size(); ++i )
			a.addElement(((S) r.elementAt(i)).s);
		
		return a;
	}

    public static void appendVector(Vector v, Vector a)

    {
        for (int i = 0; i < a.size(); ++i)
            v.addElement(a.elementAt(i));
    }
    
    public static String translate(String s)
    
    {
    	return s;
    }

	public static int hypot(int x, int y)

    {
		if ( hypotenuseByRatio == null )
		{
			hypotenuseByRatio = new int[9901];
			
			for ( int i = 100; i <= 10000 ; ++i )
			{
				hypotenuseByRatio[i - 100] = (int) Math.sqrt((i * i) + 10000);
				//Engine.out(i + " " + hypotenuseByRatio[i - 100]);
			}
		}

		int hi, lo;

        if (x > y) 
        {
            hi = x;
            lo = y;
        } 
        else 
        {
            hi = y;
            lo = x;
        }

		int hyp;
		
		if ( lo == 0 )
			hyp = hi;
		else
		{
			int r = (hi * 100)/lo;

			if ( r > 10000 )
				hyp = hi;
			else
				hyp = (hypotenuseByRatio[r - 100] * lo)/100;
		}
		
		//Engine.out(x + " " + y + " " + hyp);
		
		return hyp;
		
		/*
        int sum = lo;

        for (int i = 2; i < 6; ++i) 
        {
            sum += lo;

            if (sum > hi) 
            {
                if (i == 2) return (hi * 13) / 10;
                else if (i == 3) return (hi * 12) / 10;
                else return (hi * 11) / 10;
            }
        }

        return hi;

        // 5:1 5 5.09 6
        // 4:1 4 4.12 5
        // 3:1 3 3.16 4
        // 2:1 2 2.23 3
        // 1:1 1 1.41 2
		*/
    }

	public static String[] splitFields(String s, String sep, boolean inQuotes)
		
	{
		String[] field = new String[500];
		int k;
		int n = 0;

		while ( true )
		{
			int start = 0;
			
			while ( true )
			{
				k = s.indexOf(sep, start);
			
				if ( !inQuotes )
					break;
				
				if ( k <= 0 )
					break;
				
				if ( k > 0 )
				{
					char pre = s.charAt(k - 1);
					char pst = s.charAt(k + 1);
					
					if ( pre == '\"' && pst == '\"' )
						break;
					else
						start = k + 1;
				}
			}
			
			if ( k < 0 )
				field[n] = s.substring(0, s.length()).trim();
			else if ( k == 0 )
				field[n] = "";
			else
				field[n] = s.substring(0, k).trim();

			n++;
			
			if ( k >= 0 )
				s = s.substring(k + 1);
			else
				break;
		}
		
		String[] f = new String[n];
		
		for ( int i = 0; i < n; ++i )
			f[i] = field[i];
		
		return f;
	}

	public static SN expandString(byte[] a, int off, boolean compressed)
		
	{
		try
		{
			if (!compressed)
			{
				SN sn = new SN();

				for (int i = 0; i < 10000; ++i)
					if (a[off + i] == 0)
					{
						sn.name = new String(a, off, i);
						sn.bytes = sn.name.length() + 1;

						//Engine.out("SN: " + sn.name + " " + sn.bytes);

						return sn;
					}

				return null;
			}

			int bit = 0;
			String out = "";
			int index = 0;

			while (true)
			{
				int t = bit & 7;
				int n = bit >> 3;

				// get next chars

				int k = 0;

				if (t < 2)
				{
					k = (a[n + off] >> (2 - t)) & 63;
				}
				else if (t == 2)
				{
					k = a[n + off] & 63;
				}
				else if (t > 2)
				{
					k = (((a[n + off] & 63) << (t - 2)) & 63);
					int k0 = ((((int)a[n + 1 + off]) & 0xff) >>> (8 - (t - 2)));
					k |= k0;
				}

				bit += 6;

				String f = fragments[k];

				out += f;

				if (out.endsWith("@"))
				{
					out = out.substring(0, out.length() - 1);
					break;
				}

				if (out.length() > 500)
				{
					//Engine.out("String exceeds 500 bytes: " + out);
					break;
				}
			}

			SN sn = new SN();


			//Engine.out(out);
			//byte[] b = out.getBytes();
			//for ( int i = 0; i < b.length; ++i ) Engine.out(i + " " + b[i]);



			sn.name = out.trim();
			sn.bytes = ((bit + 7) >> 3);

			return sn;
		}

		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
	}
	
	public static String geocode(String a)
				   
	{
		boolean useCSV = false;
		String ctype;
		
		if ( useCSV )
			ctype = "csv";
		else
			ctype = "xml";
		
		//http://maps.google.com/maps/geo?q=2065+lamberton+44118&output=xml&key=ABQIAAAAI4pj8M8rN9kq6mIun8oTZhQCuQnXkByuVeCx7nHuD1QjCkrXQBSgz7bTh6HtBANNMt2bJqNdCdyRgQ
		
		// this key works for combopage only
		
		String req = "http://maps.google.com/maps/geo?q=";
		String key = "ABQIAAAAI4pj8M8rN9kq6mIun8oTZhSYFNTssyfttfAXGaClXOO2Xsb6QxRsmargICLvwXK-WEhCfNWiRt_SnA";
		String tail = "&output=" + ctype + "&key=" + key;
				
		//String req = "http://maps.google.com/maps/geo?q=";
		//String tail = "&output=" + ctype + "&key=ABQIAAAAI4pj8M8rN9kq6mIun8oTZhQCuQnXkByuVeCx7nHuD1QjCkrXQBSgz7bTh6HtBANNMt2bJqNdCdyRgQ";
		
		String in = req + java.net.URLEncoder.encode(a) + tail;
		String s = getPage(in);
		
		//Engine.out(in);
		//Engine.out("");
		//Engine.out(s);
		
		//System.out.println(s);
		//return splitFields(s, ",", false);
		
		return s;
	}
	
	public static String getPage(String url)
		
	{
		String returnLine = "";
		
		URL u1 = null;
		
		try 
		
		{ 
			u1 = new URL(url); 
		}
		
		catch (MalformedURLException e) 
		
		{
			Engine.out(e.toString()); e.printStackTrace();
			return null;
		}

		try

		{
			DataInputStream input = new DataInputStream(u1.openStream());
			
			String inputLine;

			int n = 0;
			
			while ( (inputLine = input.readLine() ) != null ) 
            {
				returnLine += inputLine;
            }
		}

		catch (IOException e) 
	
		{
			Engine.out(e.toString()); e.printStackTrace();
			return null;
		}

		return returnLine;
	}

	public static String replace(String base, String s, String t)
    
    {
    	int k = base.indexOf(s);
		
    	if ( k < 0 ) return base;
    	
    	String rem = base.substring(k + s.length(), base.length());
    	
    	return base.substring(0, k) + t + rem;
    }

	public static String stringify(String[] s)
		
	{
		String r = "";
		
		for ( int i = 0; i < s.length - 1; ++i ) 
			r += s[i] + ", ";
		
		r += s[s.length - 1];
		
		return r;
	}

	public static int getZip(String s)
		
	{
		int z = -1;
			
		try
			
		{
			z = new Integer(s).intValue();	
		}
			
		catch (Exception e)
				
		{
			Engine.out(e.toString()); e.printStackTrace();		
		}	

		return z;
	}
	
	public static Vector getData(String file)
		
	{
		Vector r = new Vector();
		
		try
		
		{
			LineNumberReader in = new LineNumberReader(new FileReader(file));

			while ( true )
			{
				String s = in.readLine();
							
				if ( s == null )
					break;
	
				String[] fields = Utilities.splitFields(s, ",", false);
				
				//Engine.out(fields[0]);
				
				//if ( fields.length > 5 )
				//	if ( fields[5].equals("NO MATCH") )
				//	{
				//		//Engine.out(fields[2] + " " + fields[3]);
						
				r.addElement(fields);
				
				//	}
			}
		
		}
		
		catch (FileNotFoundException e)
			
		{
			Engine.out(e.toString()); e.printStackTrace();	
		}
		
		catch (IOException e)
			
		{
			Engine.out(e.toString()); e.printStackTrace();
		}
		
		return r;
	}

	/*
	public static void test2()
	{
		SoundPlayer sp = new SoundPlayer("d:\\1.wav");
		sp.Load();
		sp.Play();
	}
	*/
}