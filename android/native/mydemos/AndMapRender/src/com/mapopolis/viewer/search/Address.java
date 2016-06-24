
package com.mapopolis.viewer.search;

import java.util.*;
import com.mapopolis.viewer.engine.*;

public class Address

{
	String original;
	String clean;
	
	Vector minorWords = new Vector();
	Vector tokens;
	
	int number = -1;
	String street;
	
	public Address(String s, boolean searchForNumber, int m)
		
	{
		original = s;
		
		clean = removeCharacters(s, "*'/,\"").toUpperCase();
		clean = replaceCharactersWithSpace(clean, "&.-").toUpperCase();

		if ( clean.startsWith("0 ") )
			clean = clean.substring(2);
		
		tokens = getTokens(clean);
		
		// remove last m tokens
		
		for ( int i = 0; i < m; ++i )
			if ( tokens.size() > 2 )
				tokens.removeElementAt(tokens.size() - 1);

		// deal with # first
		
		removeAptNumbers(tokens);
		
		// change 2 letter abbreviations	

		convertAbbreviations(tokens);
		
		// find and remove minors
		
		removeMinors(tokens, searchForNumber);
		
		if ( searchForNumber )
		{
			findNumber(tokens, false);
		
			if ( number < 1 )
				findNumber(tokens, true);
		}
		
		// last thing
		
		clean = "";
		
		for ( int i = 0; i < tokens.size(); ++i )
			clean += tokens.elementAt(i) + " ";
		
		if ( minorWords.size() > 0 )
		{
			for ( int i = 0; i < minorWords.size(); ++i )
				clean += minorWords.elementAt(i) + " ";
		}
		
		street = clean;
		
		clean = number + " " + clean;
		
		////////////////
		// misspellings?
		// logic in v 2
		////////////////
	}
	
	public String getClean()
		
	{
		return clean.trim();	
	}

	public int getNumber()
	
	{
		return number;
	}
	
	public String getStreet()
		
	{
		return street;	
	}
	
	public Vector getTokens()
		
	{
		return tokens;	
	}
	
	Vector getTokens(String s)
					   
	{
		StringTokenizer st = new StringTokenizer(s);
		
		//int n = 0;
		
		Vector v = new Vector();
		
		while (st.hasMoreTokens())
			
		{
			//tokens[n++] = st.nextToken();
			v.addElement(st.nextToken());
		}
		
		return v;
	}

	public static String replaceCharactersWithSpace(String s, String rc)
									  
	{
		byte[] start = s.getBytes();
		byte[] removes = rc.getBytes();
		
		for ( int i = 0; i < start.length; ++i )
		{
			byte b = start[i];
			
			for ( int j = 0; j < removes.length; ++ j )
				if ( b == removes[j] )
				{
					b = (byte) ' ';
					break;
				}

			start[i] = b;
		}
		
		return new String(start);		
	}
	
	public static String removeCharacters(String s, String rc)
		
	{
		byte[] removes = rc.getBytes();
	
		byte[] start = s.getBytes();
		
		byte[] out = new byte[1000];
		
		int n = 0;
		
		for ( int i = 0; i < start.length; ++i )
		{
			byte b = start[i];
			
			boolean found = false;
			
			for ( int j = 0; j < removes.length; ++ j )
				if ( b == removes[j] )
				{
					found = true;
					break;
				}
			
			if ( !found )
				out[n++] = b;
		}
		
		byte[] r = new byte[n];
		
		for ( int i = 0; i < n; ++i ) r[i] = out[i];
		
		return new String(r);		
	}
	
    @Override
	public String toString()
		
	{
		return "[" + original + ", " + clean + "]";	
	}
	
	/*
	public void printSmallWords()
		
	{
		for ( int i = 0; i < tokens.size(); ++i )
		{
			String t = (String) tokens.elementAt(i);
			
			if ( t.length() <= 3 )
				if ( !isNumeric(t) )
					Engine.out(t);
		}
	}
	*/
	
	void convertAbbreviations(Vector v)
		
	{
		Vector n = new Vector();
		
		for ( int i = v.size() - 1; i >= 0; --i )
		{
			String t = (String) v.elementAt(i);
			
			if ( t.toUpperCase().equals("US") )
			{
				v.removeElementAt(i);
				n.addElement("UNITED");
				n.addElement("STATES");
			}
			else if ( t.toUpperCase().equals("SR") )
			{
				v.removeElementAt(i);
				n.addElement("STATE");
				n.addElement("ROUTE");
			}
			else if ( t.toUpperCase().equals("CR") )
			{
				v.removeElementAt(i);
				n.addElement("COUNTY");
				n.addElement("ROAD");
			}
			else if ( t.toUpperCase().equals("SH") )
			{
				v.removeElementAt(i);
				n.addElement("STATE");
				n.addElement("HIGHWAY");
			}			
		}
		
		for ( int i = 0; i < n.size(); ++i )
			v.addElement(n.elementAt(i));			
	}
	
	void removeAptNumbers(Vector v)
		
	{
		for ( int i = 0; i < v.size(); ++i )
		{
			String a = ((String) v.elementAt(i)).toUpperCase();
			
			if ( a.equals("#") 
				 || a.equals("LOT") 
				 || a.equals("UNIT") 
				 || a.equals("APT")
				 || a.equals("APARTMENT")
				 || a.equals("SUITE")
				 || a.equals("STE")
				 )
			{
				if ( i + 2 < v.size() )
				{
					String n1 = ((String) v.elementAt(i + 1)).toUpperCase();
					String n2 = ((String) v.elementAt(i + 2)).toUpperCase();
					
					if ( isNumeric(n2) )
					{
						// remove # and next number
						
						v.removeElementAt(i + 1);
						v.removeElementAt(i);
					}
					else
					{
						// just remove #
						
						v.removeElementAt(i);
					}
				}
				else if ( i + 2 == v.size() )
				{
					// at the end so remove # and next number
						
					v.removeElementAt(i + 1);
					v.removeElementAt(i);
				}
				
				return;
			}
			else if ( a.startsWith("#") )
			{
				if ( i + 1 < v.size() )
				{
					String n1 = ((String) v.elementAt(i + 1)).toUpperCase();
					
					if ( isNumeric(n1) )
						v.removeElementAt(i);
					else
						v.setElementAt(a.substring(1), i);
				}
				else
					v.removeElementAt(i);
			}
		}
	}
	
	boolean isNumeric(String s)
		
	{
		byte[] b = s.getBytes();
		
		for ( int i = 0; i < b.length; ++i ) 
			if ( b[i] < (byte) '0' || b[i] > (byte) '9' )
				return false;
		
		return true;
	}
	
	void removeMinors(Vector t, boolean number)
		
	{
		for ( int i = t.size() - 1; i >= 0; --i )
		{
			// must be left with at least 1 token + number if searchForNumber

			if ( number )
			{
				if ( t.size() < 3 )
					return;
			}
			else
			{
				if ( t.size() < 2 )
					return;
			}
			
			String r = searchMinors((String) t.elementAt(i));

			if ( r != null )
			{
				minorWords.addElement(r);
				t.removeElementAt(i);
			}			
		}
	}
	
	void findNumber(Vector t, boolean m1)
		
	{
		for ( int i = 0; i < t.size(); ++i )
		{
			String s = (String) t.elementAt(i);
			
			// remove last character
			
			if ( m1 ) s = s.substring(0, s.length() - 1);
			
			if ( isNumeric(s) && s.length() > 0 )
			{
				try
				
				{
					number = new Integer(s).intValue();
				}
				
				catch (Exception e)
					
				{
					Engine.out(e.toString()); //e.printStackTrace();
				}
				
				if ( number >= 0 )
				{
					tokens.removeElementAt(i);
					return;
				}
			}
		}
	}

	public static int fullMatch(GeoMatch g1, GeoMatch g2, boolean c, boolean streetOnly)
		
	{
		String s1 = g1.addressNumber + " " + g1.streetName;
		String s2 = g2.addressNumber + " " + g2.streetName;
		
		Address a = new Address(s1, true, 0);
		Address b = new Address(s2, true, 0);
		
		//Engine.out("compare " + a + "  " + b);
		
		if ( (g1.addressNumber == g2.addressNumber) || streetOnly )
			return streetMatch(a, b, c);
		else
			return 0;
	}

	public static int fullMatch(Address a, Address b, boolean c, boolean streetOnly)
		
	{
		//Address a = new Address(g1.addressNumber + " " + g1.streetName, true);
		//Address b = new Address(g2.addressNumber + " " + g2.streetName, true);
		
		if ( (a.number == b.number) || streetOnly )
			return streetMatch(a, b, c);
		else
			return 0;
	}
	
	static int streetMatch(Address a, Address b, boolean compress)
		
	{
		int k;
		
		if ( compress )
		{
			String ac = "";
			String bc = "";
			
			for ( int i = 0; i < a.tokens.size(); ++i ) ac += ((String) a.tokens.elementAt(i));
			for ( int i = 0; i < b.tokens.size(); ++i ) bc += ((String) b.tokens.elementAt(i));
			
			ac = crunch(ac);
			bc = crunch(bc);
			
			//Engine.out("crunched =" + ac + " " + bc);
			
			int acl = ac.length();
			int bcl = bc.length();
			
			int lenDiff = Math.abs(acl - bcl);
			
			if ( lenDiff == 0 )
			{
				if ( ac.equals(bc) )
					k = 100;
				else if ( acl > 0 )
				{
					int matches = 0;
					
					for ( int i = 0; i < acl; ++i )
						if ( ac.charAt(i) == bc.charAt(i) )
							matches++;
					
					if ( acl == 0 )
						k = 0;
					else
						k = (100 * matches)/acl;
				}
				else
					k = 0;
			}
			else if ( lenDiff == 1 )
			{
				String small, big;
				
				if ( acl > bcl )
				{
					big = ac;
					small = bc;
				}
				else
				{
					big = bc;
					small = ac;
				}
				
				boolean inc = false;
				int matches = 0;
				
				for ( int i = 0; i < small.length(); ++i )
				{
					if ( inc )
					{
						if ( small.charAt(i) == big.charAt(i + 1) )
							matches++;
						else
						{
							matches = 0;
							break;
						}
					}
					else
					{
						if ( small.charAt(i) == big.charAt(i) )
							matches++;
						else if ( small.charAt(i) == big.charAt(i + 1) )
						{
							matches++;
							inc = true;
						}
						else
						{
							matches = 0;
							break;
						}
					}
				}
				
				if ( acl == 0 )
					k = 0;
				else
					k = (100 * matches)/acl;
			}
			else
				k = 0;
		}
		else
		{
			k = matchScore(a.tokens, b.tokens);
		}
		
		int m = matchScore(a.minorWords, b.minorWords);
		
		return k + (m/10);
	}
	
	static int matchScore(Vector a, Vector b)
		
	{
		// fraction of a in b + fraction of b in a
		
		if ( a.size() == 0 && b.size() == 0 )
			return 100;
		else if ( a.size() == 0 || b.size() == 0 )
			return 0;

		int ab = 0, ba = 0;
		
		for ( int i = 0; i < a.size(); ++i )
		{
			if ( inVector(b, (String) a.elementAt(i)) )
				ab++;
		}

		for ( int i = 0; i < b.size(); ++i )
		{
			if ( inVector(a, (String) b.elementAt(i)) )
				ba++;
		}

		return ((ab * 50)/a.size()) + ((ba * 50)/b.size());		
	}
	
	static boolean inVector(Vector v, String s)
		
	{
		for ( int i = 0; i < v.size(); ++i )
		{
			String t = (String) v.elementAt(i);
			if ( t.equals(s) )
				return true;
		}
		
		return false;
	}
	
	static String crunch(String s)
		
	{
		if ( s.equals("") )
			return s;
		
		byte[] b = s.toUpperCase().getBytes();

		/*
		for ( int i = 0; i < b.length; ++i )
		{
			if ( b[i] == (byte) 'S' ) b[i] = (byte) 'C';
			if ( b[i] == (byte) 'K' ) b[i] = (byte) 'C';
			if ( b[i] == (byte) 'J' ) b[i] = (byte) 'G';
		}
		*/
		
		byte[] out = new byte[b.length];
		int n = 0;

		out[n++] = b[0];
		
		for ( int i = 1; i < b.length; ++i )
		{
			if ( b[i] != b[i - 1] )
				out[n++] = b[i];
		}
		
		byte[] xo = new byte[b.length];
		int m = 0;
		
		xo[m++] = out[0];
		
		for ( int i = 1; i < n - 1; ++i )
			if ( 
				out[i] == (byte) 'A' ||
				out[i] == (byte) 'E' ||
				out[i] == (byte) 'I' ||
				out[i] == (byte) 'O' ||
				out[i] == (byte) 'U' ||
				out[i] == (byte) 'Y' )
				;
			else
				xo[m++] = out[i];
		
		if ( n > 0 && m < b.length )
			xo[m++] = out[n - 1];
		
		String r = new String(xo, 0, m);
		
		//Engine.out(s + "-->" + r);
		
		return r;
	}
	
	static String[][] minors = 

	
	{

	{"Cty","County"},

	{"Rd","Road"},

	{"Ln","Lane"},

	{"St","Strt","Street"},

	{"Cir","Cr","Circ","Circle"},

	{"Dr","Drive"},

	{"Hwy","Highway"},

	{"Ave","Av","Avenue"},

	{"Blvd","Boulevard"},

	{"Ct","Crt","Court"},

	{"Wy","Way"},

	{"Run"},

	{"Trl","Trail"},

	{"Pl","Place"},

	{"Ter","Trce","Terrace"},

	{"Row"},

	{"Pky","Parkway"},

	{"Aly","Ally","Alley"},

	{"Rue"},

	{"Sq","Sqr","Square"},

	{"Xing","Crossing"},

	{"Plz","Plaza"},

	{"Cv"},

	{"Lp","Loop"},

	{"Pk","Pike"},

	{"Wlk","Wk","Walk"},

	{"Pth","Path"},

	{"Cres","Crescent"},

	{"Byp","By","Bypass"},

	{"Sp","Spur"},

	{"Cswy","Cwy","Causeway"},

	{"Pass"},

	{"Brg","Brdg","Br","Bridge"},

	{"Tunl","Tnl","Tunnel"},

	{"Fwy","Frwy","Freeway"},

	{"Rte","Rt","Route"},

	{"Tpke","Tpk","Turnpike"},

	{"Grd","Grade"},

	{"Wkwy","Walkway"},

	{"Expy","Exp","Expressway"},

	{"Rmp","Ramp"},

	{"Thwy","Thruway","Twy","Throughway"},

	{"Skwy","Sky","Skyway"},

	{"Ovps","Op","Overpass"},

	{"Arc","Arcade"},

	{"Unp","Underpass"},

	{"RMRd"},

	{"FMRd"},

	{"Thfr","Thf","Thoroughfare"},

	{"Mtwy","Mty","Mountainway"},

	{"Tfwy"},

	{"W","West"},

	{"N","North"},

	{"S","South"},

	{"E","East"},

	{"NW","Northwest"},

	{"SW","Southwest"},

	{"NE","Northeast"},

	{"SE","Southeast"},

	{"United"},

	{"States"},

	{"State"},
	
	{"Mount","Mt"},
	
	{"Saint","St"},
	
	{"Oval","Ov"}
	
	

	};

	static
				   
	{
		for ( int i = 0; i < minors.length; ++i )
		{
			for ( int j = 0; j < minors[i].length; ++ j )
			{
				minors[i][j] = minors[i][j].toUpperCase();
				//System.out.println(minors[i][j]);
			}
		}
	}
	
	String searchMinors(String s)
		
	{
		for ( int i = 0; i < minors.length; ++i )
		{
			for ( int j = 0; j < minors[i].length; ++ j )
			{
				if ( minors[i][j].equals(s) )
					return minors[i][0];
			}
		}
		
		return null;
	}
}
