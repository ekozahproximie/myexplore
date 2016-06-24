
package com.mapopolis.viewer.search;

import com.mapopolis.xml.*;
//import com.google.soap.search.*;

import com.mapopolis.viewer.engine.*;
import com.mapopolis.viewer.utils.*;
import com.mapopolis.viewer.core.*;

import java.util.*;
import java.io.*;
import java.net.*;
//import javax.servlet.*;
//import javax.servlet.http.*;

public class WebServices

{
	String file;
	Engine engine;	
		
	public WebServices()
		
	{
		
	}
		
	public String getMapopolisMatch(String address, String zip, String mapDir, String outputFile) throws MapopolisException
		
	{
		if ( engine == null )
		{
			engine = new Engine(mapDir, outputFile);
		}
		
		Vector v = mapopolisResult(address, zip, null, null, null, false, engine.getMapSet(), 0);
		
		String s = "";
		
		for ( int i = 0; i < v.size(); ++i )
		{
			s += ((GeoMatch) v.elementAt(i)).toString() + "<br>";
		}
		
		return s;
	}
	
	public String getAddressMatch(String address, String zip)
	{
		//rs.setContentType("text/html");
		//PrintWriter output = rs.getWriter();
		
		//String address = rq.getParameter("a");
		//String zip = rq.getParameter("z");
		
		String s = "";
		
		s += (address + ", " + zip + ", ");
			
		GeoMatch m = melissaResult(address, zip, "", "");
		GeoMatch g = googleResult(address, zip, "", "");
		
		if ( m.status == GeoMatch.NOMATCH )
		{
			Address a = new Address(address, true, 0);
			m = melissaResult(a.getClean(), zip, "", "");
		}

		if ( g.status == GeoMatch.NOMATCH )
		{
			Address a = new Address(address, true, 0);
			g = googleResult(a.getClean(), zip, "", "");
		}
			
			//out("M:" + m);
			//out("G:" + g);
			
			// http://maps.google.com/maps?f=q&hl=en&q=40.8+-82.9
			
		double x = 0;
		double y = 0;
			
		if ( m.status != GeoMatch.NOMATCH && g.status != GeoMatch.NOMATCH )
		{
			int d = distance(g, m);
			if ( d < 500 )
			{
				s += ("Both, ");
				//out("<br>");	
				//out("Delta:" + d + "<br>");
				//out("<a href=http://maps.google.com/maps?f=q&hl=en&z=16&q=" + ((m.lat + g.lat)/2) + "+" + ((m.lng + g.lng)/2) + ">Map it</a>");
				x = ((m.lat + g.lat)/2);
				y = ((m.lng + g.lng)/2);
			}
			else
			{
				// choose the best match
				//out("Delta:" + d + "<br>");
				
				if ( g.score > m.score )
				{
					s += ("Google, ");
					x = g.lat;
					y = g.lng;
				}
				else
				{
					s += ("Melissa, ");
					x = m.lat;
					y = m.lng;
				}
			}
		}
		else if ( m.status == GeoMatch.NOMATCH && g.status == GeoMatch.NOMATCH )
		{
			s += ("NotFound, ");
			//out("<br><br>");
		}
		else if ( m.status == GeoMatch.NOMATCH )
		{
			// google only
			//out("Result: Google");
			//out("<br>");	
			//out("<a href=http://maps.google.com/maps?f=q&hl=en&z=16&q=" + g.lat + "+" + g.lng + ">Map it</a>");
			s += ("Google, ");
			x = g.lat;
			y = g.lng;
		}
		else
		{
			// melissa only
			//out("Result: Melissa");
			//out("<br>");	
			//out("<a href=http://maps.google.com/maps?f=q&hl=en&z=16&q=" + m.lat + "+" + m.lng + ">Map it</a>");
			s += ("Melissa, ");
			x = m.lat;
			y = m.lng;
		}

		s += (x + ", " + y);
		s += ("\n");
		
		return s;
	}		
	
	public WebServices(String f, Engine e)
		
	{
		file = f;	
		engine = e;
		out("start WebServices");
	}
	
	/*
	void test(Vector v, boolean isCity) throws MapopolisException
							  
	{
		Vector testdata = Utilities.getData(file);
		
		for ( int i = 0; i < testdata.size(); ++i )
		{
			String[] f = (String[]) testdata.elementAt(i);
			
			out("");
			out(Utilities.stringify(f));
			
			//addr, zip, city, isCity
			GeoMatch mpr = mapopolisResult(f[2], f[3], f[4], "", isCity, v, 0);
			
			GeoMatch ggr = googleResult(f[2], f[3], "", v);
			GeoMatch mlr = melissaResult(f[2], f[3], "", "", v);
			
			//String p = "";
			
			int cx, cy;
			
			if ( mpr.status == GeoMatch.ADDRESSMATCH && ggr.status == GeoMatch.ADDRESSMATCH && mlr.status == GeoMatch.ADDRESSMATCH )
			{
				cx = (mpr.mapX + mlr.mapX + ggr.mapX)/3;
				cy = (mpr.mapY + mlr.mapY + ggr.mapY)/3;

				out("Map:" + mpr.toString()+ " dif=" + diff(mpr.mapX, mpr.mapY, cx, cy));
				out("Goo:" + ggr.toString()+ " dif=" + diff(ggr.mapX, ggr.mapY, cx, cy));
				out("Mel:" + mlr.toString()+ " dif=" + diff(mlr.mapX, mlr.mapY, cx, cy));
				
				out("Map-Goo " + diff(mpr.mapX, mpr.mapY, ggr.mapX, ggr.mapY));
				out("Map-Mel " + diff(mpr.mapX, mpr.mapY, mlr.mapX, mlr.mapY));
				out("Mel-Goo " + diff(mlr.mapX, mlr.mapY, ggr.mapX, ggr.mapY));
			}
			else
			{
				out("Map:" + mpr.toString());
				out("Goo:" + ggr.toString());
				out("Mel:" + mlr.toString());
			}
		}
	}
	*/
	
	int diff(int x0, int y0, int x1, int y1)
		
	{
    	int dx = Math.abs(x0 - x1);
    	int dy = Math.abs(y0 - y1);
    	
    	return Utilities.hypot(dx, dy);
	}
	
	void googleTest() throws MapopolisException
									
	{
		Vector testdata = Utilities.getData(file);
		
		for ( int i = 0; i < testdata.size(); ++i )
		{
			String[] f = (String[]) testdata.elementAt(i);
			//out(googleResult(f[2], f[3]));
		}
	}
	
	void serviceobjectsTest() throws MapopolisException
									
	{
		Vector testdata = Utilities.getData(file);
		
		for ( int i = 0; i < testdata.size(); ++i )
		{
			String[] f = (String[]) testdata.elementAt(i);
			out(Utilities.stringify(f) + ", " + serviceobjectsResult(f[2], f[3]));
		}
	}
	
	public void test(Vector v, boolean isCity) throws MapopolisException
		
	{
		Vector testdata = Utilities.getData(file);

		for ( int i = 0; i < 1000; ++i ) //testdata.size(); ++i )
		{
			String[] f = (String[]) testdata.elementAt(i);

			String zip = f[3];
			String address = f[2];
			
			out("");
			out("Mapping: " + address + ", " + zip + "<br>");

			GeoMatch m = melissaResult(address, zip, "", "");
			GeoMatch g = googleResult(address, zip, "", "");
			
			if ( m.status == GeoMatch.NOMATCH )
			{
				Address a = new Address(address, true, 0);
				m = melissaResult(a.getClean(), zip, "", "");
			}

			if ( g.status == GeoMatch.NOMATCH )
			{
				Address a = new Address(address, true, 0);
				g = googleResult(a.getClean(), zip, "", "");
			}
			
			//out("M:" + m);
			//out("G:" + g);
			
			// http://maps.google.com/maps?f=q&hl=en&q=40.8+-82.9
			
			if ( m.status != GeoMatch.NOMATCH && g.status != GeoMatch.NOMATCH )
			{
				int d = distance(g, m);
				if ( d < 500 )
				{
					out("Result: 2");
					out("<br>");	
					out("Delta:" + d + "<br>");
					out("<a href=http://maps.google.com/maps?f=q&hl=en&z=16&q=" + ((m.lat + g.lat)/2) + "+" + ((m.lng + g.lng)/2) + ">Map it</a>");
				}
				else
				{
					// choose the best match
					out("Delta:" + d + "<br>");
				}
			}
			else if ( m.status == GeoMatch.NOMATCH && g.status == GeoMatch.NOMATCH )
			{
				out("<br><br>");
			}
			else if ( m.status == GeoMatch.NOMATCH )
			{
				// google only
				out("Result: Google");
				out("<br>");	
				out("<a href=http://maps.google.com/maps?f=q&hl=en&z=16&q=" + g.lat + "+" + g.lng + ">Map it</a>");
			}
			else
			{
				// melissa only
				out("Result: Melissa");
				out("<br>");	
				out("<a href=http://maps.google.com/maps?f=q&hl=en&z=16&q=" + m.lat + "+" + m.lng + ">Map it</a>");
			}
			
			out("<br><br>");			
		}		
	}
	
	int distance(GeoMatch g1, GeoMatch g2)
		
	{
		int x = (g1.mapX - g2.mapX);
		int y = (g1.mapY - g2.mapY);
		return (int) Math.sqrt(x * x + y * y);
	}

	public void melissaTest(Vector v) throws MapopolisException
									
	{
		Vector testdata = Utilities.getData(file);
		
		for ( int i = 0; i < testdata.size(); ++i )
		{
			String[] f = (String[]) testdata.elementAt(i);
			//out(Utilities.stringify(f) + ", " + melissaResult(f[2], f[3], "", "", v));
		}
	}
	
	public void mapopolisTest(Vector v, boolean isCity) throws MapopolisException
		
	{
		boolean full = false;
		Vector testdata = Utilities.getData(file);
		
		//out("start test");
		
		
		for ( int i = 0; i < testdata.size(); ++i )
		{
			String[] f = (String[]) testdata.elementAt(i);

			String zip = f[3];
			String address = f[2];
			
			//address = Utilities.replace(address, f[2], "");

			String in = address + " " + zip;
			
			//out("");
			//out("");
			//out(in);
			
			GeoMatch best = null;
			int bestscore = 0;
			
			Vector matches = mapopolisResult(address, zip, null, null, null, isCity, v, 0);
			
			for ( int k = 0; k < matches.size(); ++k )
			{
				GeoMatch g = (GeoMatch) matches.elementAt(k);
				if ( g.score > bestscore )
				{
					bestscore = g.score;
					best = g;
				}
			}
			
			String result = address + ", " + zip + ", ";
			
			if ( best == null ) 
				result += "Not Found";//out("Mapopolis: Not found");
			else 
				result += best;
					
			out(result);
			
			/*
			if ( address.startsWith("0 ") ) address = address.substring(2);
			
			GeoMatch gg = googleResult(address, "", city, state, v);
			out("Google: " + gg);
			
			GeoMatch mm = melissaResult(address, "", city, state, v);
			out("Melissa: " + mm);
			*/
		}
		
			//GeoMatch map = mapopolisResult(f[2], f[3], v);
			//GeoMatch result = null;
			
			//out("");
			//out(in);
			
			/*
			if ( map.score == 110 )
			{
				//if ( full ) out("Exact: " + in);
				result = map;
				out("result:" + result);
				continue;
			}
			*/

			/*
			GeoMatch gg = googleResult(f[2], f[3], v);

			int mgMatchScore = Address.fullMatch(map, gg, true, false);
			int gsep = distance(map, gg);

			if ( compareResults(map, gg, mgMatchScore, gsep, in, "map-gg") )
			{
				result = map;
				out("result:" + result);
				continue;
			}
			
			GeoMatch mel = melissaResult(f[2], f[3], v);

			out("");
			out("Melissa match: " + mel);

			int mmMatchScore = Address.fullMatch(map, mel, true, false);
			int msep = distance(map, gg);
			
			if ( compareResults(map, mel, mmMatchScore, msep, in, "map-mel") )
			{
				result = map;
				out("result:" + result);
				continue;
			}

			int eeMatchScore = Address.fullMatch(gg, mel, true, false);
			int esep = distance(mel, gg);
			
			int c = 0;
			
			if ( map.status == map.NOMATCH ) c++;
			if ( gg.status == gg.NOMATCH ) c++;
			if ( mel.status == mel.NOMATCH ) c++;
			
			if ( c >= 2 )
			{
				out("Two engines could not find this address");
				continue;
			}

			if ( compareResults(gg, mel, eeMatchScore, esep, in, "map-mel") )
			{
				result = map;
				out("result:" + result);
				continue;
			}

			out("map:" + map);//s + ", " + ggs + ", mapopolis/google match " + mgMatchScore + ", distance " + gsep);
			out("gg:" + gg);
			out("mel:" + mel);
			
			out("map/gg:" + mgMatchScore + " sep=" + gsep);
			out("map/mel:" + mmMatchScore + " sep=" + msep);
			out("gg/mel:" + eeMatchScore + " sep=" + esep);
						
			out("no result");
			*/
			
			
			
			
			
			
			
			
			/*
			if ( map.score == 0 )
			{
				// try both google and melissa
				
				//if ( false )
				if ( gg.score == 0 )
					out("Both fail: " + in);
				else
					out("google only: " + in + " " + gg);

				continue;
			}
			
			
			if ( gg.score == 0 && map.score >= 100 )
			{
				//if ( false )
				out("Mapopolis only: " + in + " " + map);
				
				continue;
			}
			*/

		
	}

	boolean compareResults(GeoMatch a, GeoMatch b, int mscore, int sep, String in, String cp)
						   
	{
		if ( a.score >= 100 && b.score >= 100 && sep < 300 )
		{
			///out(	"Close Match 1: " + in + " ---a:" + 
			//			a.addressNumber + " " + a.streetName	+
			//			" ---b: " + b.addressNumber + " " + b.streetName );
			return true;
		}

		if ( a.score >= 70 && b.score >= 70 && mscore >= 100 && sep < 300 )
		{
			//out(	"Close Match 2: " + in + " ---a:" + 
			//			a.addressNumber + " " + a.streetName	+
			//			" ---b: " + b.addressNumber + " " + b.streetName );
			return true;
		}
				
		return false;
	}

	String serviceobjectsResult(String adr, String zip)
		
	{
		String url1 = "http://trial.serviceobjects.com/gcr/GeoCoder.asmx/GetGeoLocation?Address=";
		String url2 = "&City=&State=&PostalCode=";
		String url3 = "&LicenseKey=WS34-UQY3-WVG1";
		
		Address a = new Address(adr, true, 0);

		int z = Utilities.getZip(zip);
			
		if ( z <= 0 )
			;
		
		z = z + 100000;
		String nz = "" + z;
		nz = nz.substring(1);

		String url = url1 + java.net.URLEncoder.encode(a.getClean()) + url2 + nz + url3;
		
		//System.out.println(url);
		
		String p = Utilities.getPage(url);
		
		//System.out.println(p);
		
		XML x = new XML(p);
		
		String lat = x.findData("Location.Latitude", 0);
		String lon = x.findData("Location.Longitude", 0);
		String lev = x.findData("Location.Level", 0);
		
		if ( lat == null || lon == null || lev == null )
			return "NO MATCH";
		else
			return "MATCH, " + lat + ", " + lon + ", " + lev + ", ";
	}
	
	GeoMatch googleResult(String adr, String zip, String city, String state)
		
	{
		Address a = new Address(adr, true, 0);

		int z = Utilities.getZip(zip);
		if ( z <= 0 )
			return new GeoMatch(GeoMatch.NOMATCH);

		z = z + 100000;
		String nz = "" + z;
		nz = nz.substring(1);

		String g = Utilities.geocode(adr + "," + nz);
		
		//String in = adr.trim() + "," + (city +" " + state).trim();
		//out("Google input:" + in);
		//String g = Utilities.geocode(in);
				
		XML x = new XML(g);

		out(g);
		x.print();
		out(x.getString());
				
		boolean match1 = (g.indexOf("Accuracy=\"8\"") > 0);
		boolean match2 = (g.indexOf("Accuracy=\"7\"") > 0);
		boolean match3 = (g.indexOf("Accuracy=\"6\"") > 0);

		String addr = x.findData("kml.Response.Placemark.address", 0);
			
		if ( addr == null )
			addr = "";			
			
		if ( match1 || match2 || match3 )
		{
			String r =  x.findData("kml.Response.Placemark.Point.coordinates", 0);
				
			int k1 = r.indexOf(",");
			
			if ( k1 > 0 )
			{
				int k2 = r.indexOf(",", k1 + 1);
				if ( k2 > 0 )
				{
					int k = addr.indexOf(",");
					
					if ( k > 0 )
						addr =  addr.substring(0, k);
					
					Address ga = new Address(addr, true, 0);
					
					int sc1 = Address.fullMatch(a, ga, true, false);
					int sc2 = Address.fullMatch(a, ga, true, true);
					
					//GeoMatch geo =  new GeoMatch(GeoMatch.ADDRESSMATCH, r.substring(0, k1), r.substring(k1 + 1, k2), ga.getNumber(), ga.getStreet(), sc);
					
					boolean addrMismatch = false;
					
					if ( ga.getNumber() != 0 )
						if ( sc1 == 0 )
							addrMismatch = true;
					
					if ( sc2 > 75 && !addrMismatch )
					{
						GeoMatch geo =  new GeoMatch(GeoMatch.ADDRESSMATCH, r.substring(0, k1), r.substring(k1 + 1, k2), addr, sc1 * 1000000 + sc2);
						
						if ( match3 ) geo.status = GeoMatch.Google6;
						if ( match2 ) geo.status = GeoMatch.Google7;
						if ( match1 ) geo.status = GeoMatch.Google8;
						
						return geo;
					}
				}
			}
		}

		return new GeoMatch(GeoMatch.NOMATCH);
	}



	//void test()
	//{
	//	
	//	new HttpServletRequest();
	//}


	// Melissa Data
	
	// "http://xml.melissadata.com/xml.asp", "2490 lee blvd", "" + 44118, "112544665");
	



	public GeoMatch melissaResult(String address, String zip, String city, String state)
		
	{
		String URL = "http://xml.melissadata.com/xml.asp";
		String id = "112544665";//112544665

		String xmldata = createSendingXML(address, zip, city, state, id);
		String param = xmldata;
	  
		//out(param);

		URL url;
		String inputLine = "";
		String output = "";
	  
		try
		{
		  url = new URL(URL);
		  
		  HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
		  urlConn.setDoInput (true);
		  urlConn.setDoOutput (true);
		  urlConn.setUseCaches (false);
		  urlConn.setRequestMethod("POST");
		  urlConn.setRequestProperty("Accept-Language","en");
		  urlConn.setAllowUserInteraction(false);
		  urlConn.setRequestProperty("Content-length",String.valueOf(param.length()));    
		  urlConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		  DataOutputStream out=new DataOutputStream(urlConn.getOutputStream());
		  out.writeBytes(param);
		  out.flush();
		  out.close();


		  System.out.println(new String(param));


		  BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
		  
		  while ((inputLine = in.readLine()) != null)
		  {
				output += inputLine;
				//inputLine = in.readLine();
				//System.out.println(inputLine);
			}
		}
	  
		catch(ProtocolException e)
			  
		{
			  out(e.toString()); e.printStackTrace();
			  return null;
		}
	  
		catch(IOException e)
			  
		{
			  out(e.toString()); e.printStackTrace();
			  return null;
		}
	  
		if ( output == null )
		{
			  System.out.println("output is null");
			  return new GeoMatch(GeoMatch.NOMATCH);
		}

		XML x = new XML(output);
	
		x.print();
		out(x.getString());
	  
		String lat = x.findData("RecordSet.Record.Latitude", 0);
		String lon = x.findData("RecordSet.Record.Longitude", 0);
		String geo = x.findData("RecordSet.Record.GeocodeStatusCode", 0);
		
		String addr = x.findData("RecordSet.Record.Address", 0).trim();
		
		if ( addr.endsWith(";") )
			addr = addr.substring(0, addr.length() - 1);
			
		// what about street match?

		if ( lat != null && lon != null && geo != null )
			if ( geo.equals("9") || geo.equals("7") )
			{
				Address a = new Address(address, true, 0);
				Address mel = new Address(addr, true, 0);
				
				int sc1 = Address.fullMatch(a, mel, true, false);
				int sc2 = Address.fullMatch(a, mel, true, true);
					
				//GeoMatch geo =  new GeoMatch(GeoMatch.ADDRESSMATCH, r.substring(0, k1), r.substring(k1 + 1, k2), ga.getNumber(), ga.getStreet(), sc);
				//return new GeoMatch(GeoMatch.ADDRESSMATCH, lon, lat, mel.getNumber(), mel.getStreet(), sc);
				
				if ( sc2 > 75 )
					return new GeoMatch((geo.equals("9")?GeoMatch.ZIP4MATCH:GeoMatch.ZIP2MATCH), lon, lat, addr, sc1 * 1000000 + sc2);
			}
		
		// return no match
		
		return new GeoMatch(GeoMatch.NOMATCH);
	}
	
	String createSendingXML(String address, String zip, String city, String state, String id)
	
	{
	String xmlstr = "<?xml version=\"1.0\"?>";		

	xmlstr += "<RecordSet>";
	xmlstr += "<CustomerID>"; 
	xmlstr += id;
	xmlstr += "</CustomerID>";
        /* 
         * If wanted to check multiple addresses, could insert a loop here
         * Close loop just before </RecordSed> tag
        */
        xmlstr += "<Record>";
        xmlstr += "<CustRecNo>";
        xmlstr += "1235488";
        xmlstr += "</CustRecNo>";
        xmlstr += "<Address>";	/** w/o address then no address information are returned */
        xmlstr += address;
        xmlstr += "</Address>";
        xmlstr += "<Address2>";
        //xmlstr += sAddress2;
        xmlstr += "</Address2>";
        xmlstr += "<City>";
        xmlstr += city;
        xmlstr += "</City>";
        xmlstr += "<State>";
        xmlstr += state;
        xmlstr += "</State>";
        xmlstr += "<Zip>";
        xmlstr += zip;
        xmlstr += "</Zip>";
        xmlstr += "<Plus4>";
        //xmlstr += sPlus4;
        xmlstr += "</Plus4>";
        xmlstr += "<AddressTypeString>";
        xmlstr += "</AddressTypeString>";
        xmlstr += "<CityAbbreviation>";
        xmlstr += "</CityAbbreviation>";
        xmlstr += "<CarrierRoute>";
        xmlstr += "</CarrierRoute>";
        xmlstr += "<DeliveryPointCode>";
        xmlstr += "</DeliveryPointCode>";
        xmlstr += "<DeliveryPointCheckDigit>";
        xmlstr += "</DeliveryPointCheckDigit>";
        xmlstr += "<CountyFips>";
        xmlstr += "</CountyFips>";
        xmlstr += "<CountyName>";
        xmlstr += "</CountyName>";
        xmlstr += "<TimeZone>";
        xmlstr += "</TimeZone>";
        xmlstr += "<TimeZoneCode>";
        xmlstr += "</TimeZoneCode>";
        xmlstr += "<Msa>";
        xmlstr += "</Msa>";
        xmlstr += "<Pmsa>";
        xmlstr += "</Pmsa>";
        xmlstr += "<AreaCodeOfZip>";
        xmlstr += "</AreaCodeOfZip>";
        xmlstr += "<AddressTypeCode>";
        xmlstr += "</AddressTypeCode>";
        /*
		xmlstr += "<Lacs>";
        xmlstr += "</Lacs>";
        xmlstr += "<PrivateMailBox>";
        xmlstr += "</PrivateMailBox>";
        xmlstr += "<CongressionalDistrict>";
        xmlstr += "</CongressionalDistrict>";
		*/
        xmlstr += "<ParsedAddressRange>";
        xmlstr += "</ParsedAddressRange>";
        xmlstr += "<ParsedPreDirection>";
        xmlstr += "</ParsedPreDirection>";
        xmlstr += "<ParsedStreetName>";
        xmlstr += "</ParsedStreetName>";
        xmlstr += "<ParsedSuffix>";
        xmlstr += "</ParsedSuffix>";
        xmlstr += "<ParsedPostDirection>";
        xmlstr += "</ParsedPostDirection>";
        xmlstr += "<ParsedSuiteName>";
        xmlstr += "</ParsedSuiteName>";
        xmlstr += "<ParsedSuiteRange>";
        xmlstr += "</ParsedSuiteRange>";
        xmlstr += "<ParsedGarbage>";
        xmlstr += "</ParsedGarbage>";
        xmlstr += "<AddressErrorCode>";
        xmlstr += "</AddressErrorCode>";
        xmlstr += "<AddressErrorString>";
        xmlstr += "</AddressErrorString>";
        xmlstr += "<AddressStatusCode>";
        xmlstr += "</AddressStatusCode>";
        xmlstr += "<AddressDatabaseDate>";
        xmlstr += "</AddressDatabaseDate>";
        xmlstr += "<Latitude>";
        xmlstr += "</Latitude>";
        xmlstr += "<Longitude>";
        xmlstr += "</Longitude>";
        /*
		xmlstr += "<CensusTract>";
        xmlstr += "</CensusTract>";
        xmlstr += "<CensusBlock>";
        xmlstr += "</CensusBlock>";
		*/
        xmlstr += "<GeocodeErrorCode>";
        xmlstr += "</GeocodeErrorCode>";
        xmlstr += "<GeocodeStatusCode>";
        xmlstr += "</GeocodeStatusCode>";
        xmlstr += "<GeocodeDatabaseDate>";
        xmlstr += "</GeocodeDatabaseDate>";
        
		/*
		xmlstr += "<Phone>";
        //xmlstr += sPhone;
        xmlstr += "</Phone>";
        xmlstr += "<PhoneAreaCode>";
        xmlstr += "</PhoneAreaCode>";
        xmlstr += "<PhonePrefix>";
        xmlstr += "</PhonePrefix>";
        xmlstr += "<PhoneSuffix>";
        xmlstr += "</PhoneSuffix>";
        xmlstr += "<PhoneExtension>";
        xmlstr += "</PhoneExtension>";
        xmlstr += "<PhoneCity>";
        xmlstr += "</PhoneCity>";
        xmlstr += "<PhoneState>";
        xmlstr += "</PhoneState>";
        xmlstr += "<PhoneDistance>";
        xmlstr += "</PhoneDistance>";
        xmlstr += "<PhoneCountyFips>";
        xmlstr += "</PhoneCountyFips>";
        xmlstr += "<PhoneCountyName>";
        xmlstr += "</PhoneCountyName>";
        xmlstr += "<PhoneLatitude>";
        xmlstr += "</PhoneLatitude>";
        xmlstr += "<PhoneLongitude>";
        xmlstr += "</PhoneLongitude>";
        xmlstr += "<PhoneTimeZone>";
        xmlstr += "</PhoneTimeZone>";
        xmlstr += "<PhoneTimeZoneCode>";
        xmlstr += "</PhoneTimeZoneCode>";
        xmlstr += "<PhoneMsa>";
        xmlstr += "</PhoneMsa>";
        xmlstr += "<PhonePmsa>";
        xmlstr += "</PhonePmsa>";
        xmlstr += "<PhoneCountry>";
        xmlstr += "</PhoneCountry>";
        xmlstr += "<PhoneNewAreaCode>";
        xmlstr += "</PhoneNewAreaCode>";
        xmlstr += "<PhoneErrorCode>";
        xmlstr += "</PhoneErrorCode>";
        xmlstr += "<PhoneStatusCode>";
        xmlstr += "</PhoneStatusCode>";
        xmlstr += "<PhoneDatabaseDate>";
        xmlstr += "</PhoneDatabaseDate>";
		*/
		/*
        xmlstr += "<FirstName>";
        xmlstr += "</FirstName>";
        xmlstr += "<MiddleName>";
        xmlstr += "</MiddleName>";
        xmlstr += "<LastName>";
        xmlstr += "</LastName>";
        xmlstr += "<NamePrefix>";
        xmlstr += "</NamePrefix>";
        xmlstr += "<NameSuffix>";
        xmlstr += "</NameSuffix>";
        xmlstr += "<Gender>";
        xmlstr += "</Gender>";
        xmlstr += "<NameStatusCode>";
        xmlstr += "</NameStatusCode>";
        xmlstr += "<NameDatabaseDate>";
        xmlstr += "</NameDatabaseDate>";
        xmlstr += "<FullName>";
        //xmlstr += sFullName;
        xmlstr += "</FullName>";
		*/
        xmlstr += "</Record>";
        xmlstr += "</RecordSet>";
		return xmlstr;
	}

	public Vector mapopolisResult(String adr, String zip, Vector v) throws MapopolisException
	{
		return mapopolisResult(adr, zip, null, null, null, false, v, 0);
	}

	public Vector mapopolisResult(String adr, String zip, String city, String county, String state, boolean isCity, Vector v, int m) throws MapopolisException
		
	{
		Address a = new Address(adr, true, m);
		int z = 0;
		
		if ( !isCity ) z = Utilities.getZip(zip);	
		
		

		/*
		if ( false )
		{
			String gr = WebServices.googleResult(f[2], f[3]);
			out(a.getClean() + " ==> " + gr);
		}
			
		if ( false )
		{
		String sr = WebServices.spellingSuggestions(a.getClean());
		if ( sr != null && !sr.equals("null") )
			out(a.getClean() + " ==> " + sr);
		}
		*/
		//if ( true ) continue;

		String str = "";
			
		if ( z < 0 )
		{
			//return new GeoMatch(GeoMatch.NOMATCH);
			return null;
		}

		MapSearch searcher = new MapSearch(v);

		Vector all = new Vector();
		Vector r;
		
		// try address match no compression
		//r = trySearch(a, z, true, false, city, county, state, isCity, v, m);
		r = searcher.fuzzySearch(a, z, true, false, county + state, isCity, m);
		if ( r != null ) append(all, r);
			
		// try address match with compression
		//r = trySearch(a, z, true, true, city, county, state, isCity, v, m);
		r = searcher.fuzzySearch(a, z, true, true, county + state, isCity, m);
		if ( r != null ) append(all, r);

		// try street match no compression
		//r = trySearch(a, z, false, false, city, county, state, isCity, v, m);
		r = searcher.fuzzySearch(a, z, false, false, county + state, isCity, m);
		if ( r != null ) append(all, r);

		// try street match with compression
		//r = trySearch(a, z, false, true, city, county, state, isCity, v, m);
		r = searcher.fuzzySearch(a, z, true, true, county + state, isCity, m);
		if ( r != null ) append(all, r);

		trimResults(all);
		
		Vector g = new Vector();
		
		for ( int i = 0; i < all.size(); ++i )
		{
			TempResult tr = (TempResult) all.elementAt(i);
			g.addElement(tr2geo(tr));
		}
		
		return g;
		
		//return new GeoMatch(GeoMatch.NOMATCH);
			
		//Vector rs = MapSearch.fuzzySearch(a, z, false, true);
		//trimResults(rs);

		//if ( rs.size() == 1 )
		//{
		//	TempResult tr = (TempResult) rs.elementAt(0);
		//	String name = (new MapFeature(tr.mfr, tr.index)).getName();
		//	
		//	out(str + ", " +  (new MapFeature(tr.mfr, tr.index)).getName() + ", STREET MATCH, " + tr.score + ", " + gr);
		//	found++;
		//}
		//else if ( rs.size() > 1 )
		//{
		//	TempResult tr = (TempResult) rs.elementAt(0);
		//	out(str + ", " + (new MapFeature(tr.mfr, tr.index)).getName() + ", MULTIPLE STREET MATCH," + ", " + gr);
		//	found++;
		//}
		//else			
	}
	
	GeoMatch trySearch(Address a, int z, boolean adr, boolean cmp, String city, String county, String state, boolean isCity, Vector v, int m) throws MapopolisException
		
	{
		MapSearch searcher = new MapSearch(v);
		Vector r = searcher.fuzzySearch(a, z, adr, cmp, county + state, isCity, m);
			
		if ( r == null )
		{
			out("fuzzy returns no results");
			return null;
			//return new GeoMatch(GeoMatch.NOMATCH);
		}
		
		out("fuzzy returns results:" + r.size());
		
		trimResults(r);
		
		//for ( int i = 0; i < r.size(); ++i )
		//{
		//	TempResult tr = (TempResult) r.elementAt(i);
		//	
		//	out("" + tr);
		//}
		
		
		
		//TempResult best = null;
		//int score = 0;
		
		/*
		for ( int i = 0; i < r.size(); ++i )
		{
			TempResult tr = (TempResult) r.elementAt(i);
			
			if ( tr.score > score && tr.score > 50 )
			{
				best = tr;
				score = tr.score;
			}
		}
		
		if ( best != null )
		{
			r.removeAllElements();
			r.addElement(best);
		}
		*/
		
		out("r size=" + r.size());
		
		//out("ADDRESS: " + adr + " COMPRESS: " + cmp + " " + r.size());

		if ( r.size() > 1 )
		{
			GeoMatch g;
			
			if ( adr )
				g = new GeoMatch(GeoMatch.MULTIPLEADDRESSMATCH);
			else
				g = new GeoMatch(GeoMatch.MULTIPLESTREETMATCH);

			tempResultsToGeoMatches(r, g, adr);
			out("returning " + g);
			return g;
		}
			
		if ( r.size() == 1 )
		{
			// success
					
			TempResult tr = (TempResult) r.elementAt(0);
								
			if ( adr )
			{
				if ( tr.srl != null )
				{
					WorldCoordinates wc = tr.srl.onStreetCoordinates();
					return new GeoMatch(GeoMatch.ADDRESSMATCH, wc.x, wc.y, tr.srl.address, tr.address.getStreet(), tr.score);
				}
			}
			else
			{
				MapFeature mf = new MapFeature(tr.mfr, tr.index);
				String name = mf.getName();
				WorldCoordinates wc = mf.coordinatesOfCenter();
				
				return new GeoMatch(GeoMatch.STREETMATCH, wc.x, wc.y, 0, name, tr.score);
			}
		}
		
		return null;
	}
	
	GeoMatch tr2geo(TempResult tr) throws MapopolisException
		
	{
		if ( tr.srl != null )
		{
			WorldCoordinates wc = tr.srl.onStreetCoordinates();
			return new GeoMatch(GeoMatch.ADDRESSMATCH, wc.x, wc.y, tr.srl.address, tr.address.getStreet(), tr.score);
		}
		else
		{
			MapFeature mf = new MapFeature(tr.mfr, tr.index);
			String name = mf.getName();
			WorldCoordinates wc = mf.coordinatesOfCenter();
				
			return new GeoMatch(GeoMatch.STREETMATCH, wc.x, wc.y, 0, name, tr.score);
		}		
	}
	
	void tempResultsToGeoMatches(Vector r, GeoMatch gm, boolean address) throws MapopolisException
		
	{
		gm.mulipleMatches = new Vector();
		
		for ( int i = 0; i < r.size(); ++i )
		{
			TempResult tr = (TempResult) r.elementAt(i);
			
			if ( tr.srl == null )
				continue;
			
			WorldCoordinates wc = tr.srl.onStreetCoordinates();
			
			GeoMatch g = new GeoMatch(address?GeoMatch.ADDRESSMATCH:GeoMatch.STREETMATCH, wc.x, wc.y, 
									  tr.srl.address, tr.srl.featureName(), tr.score);
			
			gm.mulipleMatches.addElement(g);			
		}		
	}

	void trimResults(Vector r)
		
	{
		int m = 0;
		
		for ( int i = 0; i < r.size(); ++i )
		{
			TempResult tr = (TempResult) r.elementAt(i);

			if ( tr.score > m )
				m = tr.score;
		}
		
		// max score is 110
		
		for ( int i = r.size() - 1; i >= 0; --i )
		{
			TempResult tr = (TempResult) r.elementAt(i);
			
			if ( (m == 110 && tr.score < m) || (tr.score < 50) )
					r.removeElementAt(i);
		}
	}
	/*
	private int distance(GeoMatch a, GeoMatch b)
		
	{
		//out(a.mapX + " " +  b.cx + " " + a.cy + " " + b.cy);
		
		int dx = (int) Math.abs(a.mapX - b.mapX);
		int dy = (int) Math.abs(a.mapY - b.mapY);
		
		return dx + dy;
	}
	*/
	void append(Vector a, Vector b)
		
	{
		for ( int i = 0; i < b.size(); ++i ) a.addElement(b.elementAt(i));	
	}
	
	void out(String s)
		
	{
		if ( engine != null )
			engine.out(s);
		else
			System.out.println(s);
	}

	/*
	
	GoogleSearch gs;
	
	String spellingSuggestions(String s)
	
	{
		if ( gs == null )
		{
			gs = new com.google.soap.search.GoogleSearch();
			gs.setKey("ODe9XUJQFHLTkXh2UxU2TVZFUq+MfX83");
		}
		try
	
		{
			String r = gs.doSpellingSuggestion(s);
			return r;
		}
		
		catch (com.google.soap.search.GoogleSearchFault e)
			
		{
			out(e.toString()); e.printStackTrace();
		}
		
		return "?";
	}
	
	 */
}
