
package com.mapopolis.xml;

import java.util.Vector;





public class XML

{
	Attribute a;
	
	public XML(String s)
		
	{		
		if ( s == null )
			return;
		
		a = buildTree(s);
	}

	public String findData(String s, int n)
		
	{
		Attribute a = find(s, n);
		
		if ( a == null )
			return null;
		else
			return a.data;
	}
	
	public Attribute find(String s, int n)
	
	{
		return a.find(s, n);
	}
	
	public void print()
		
	{
		a.print(System.out, "");
	}
	
	public String getString()
	
	{
		return a.getString("", new Vector<String>());
	}
	
	/*
	void printAll(Attribute a, Vector ignore, String s)
				  
	{
		Attribute r;
		int t = 0;
		
		while ( true )
		{
			r = a.find(s, t++);
				
			if ( r == null )
				break;

			String line = r.getString("", ignore);
						
			String[] res = null;
			String tag = "DETAIL_INFO.ADDRESS_STR";
			
			String x = "?", y = "?";
			
			int k = line.indexOf(tag);
			
			if ( k < 0 )
			{
				System.out.println("Cannot find address string");
				//System.out.println(line);
			}
			else
			{
				k += tag.length() + 2;
				String tail = line.substring(k);
				int m = tail.indexOf("\"");
				
				if ( m < 0 )
				{
					System.out.println("Cannot find address string end");
				}
				else
				{
					String add = tail.substring(0, m);
					
					if ( add.charAt(0) >= '0' && add.charAt(0) <= '9' )
						;
					else
					{
						// find first comma and delete to that point
						
						int e = add.indexOf(",");
						if ( e < 0 )
							System.out.println("Cannot find comma");
						else
							add = add.substring(e + 1);
					}
					
					res = geocode(add);
					
					if ( res[0].trim().equals("200") && (res[1].trim().equals("8") || res[1].trim().equals("7")) )
					{
						x = res[2];
						y = res[3];
					}
					else						
						System.out.println(add + "------------>" + res[0] + " " + res[1] + " " + res[2] + " " + res[3]);
				}
			}
			
			String co = " LAT=\"" + x + "\" LNG=\"" + y + "\"";
			
			out.println(line + co);
		}
		
		System.out.println(t);
	}
	*/
	
	/*
	String[] geocode(String a)
				   
	{
		boolean useCSV = false;
		String ctype;
		
		if ( useCSV )
			ctype = "csv";
		else
			ctype = "xml";
		
		String req = "http://maps.google.com/maps/geo?q=";
		String tail = "&output=" + ctype + "&key=ABQIAAAAI4pj8M8rN9kq6mIun8oTZhQCuQnXkByuVeCx7nHuD1QjCkrXQBSgz7bTh6HtBANNMt2bJqNdCdyRgQ";

		String s = getPage(req + java.net.URLEncoder.encode(a) + tail);
		
		System.out.println(s);
		
		return splitFields(s, ",", false);
	}
	*/
	/*
	private static String getPage(String url)
		
	{
		String returnLine = "";
		
		URL u1 = null;
		
		try 
		
		{ 
			u1 = new URL(url); 
		}
		
		catch (MalformedURLException ex) 
		
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

		catch (IOException ex) 
	
		{ 
			Engine.out(e.toString()); e.printStackTrace();
			return null;
		}

		return returnLine;
	}
	
	public static String[] splitFields(String s, String sep, boolean inQuotes)
		
	{
		String[] field = new String[50];
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
	*/

	Attribute buildTree(String s)
						
	{
		Attribute a = new Attribute("TOP");
		findAttributes(a, s, 0);
		return a;
			
		//a.print(out, "");
	}
	
	int findAttributes(Attribute a, String s, int n)
		
	{
		// starting at n looking for attributes
	
		Tag openTag = null;
		Attribute openAttribute = null;
		boolean subs = false;
		
		if ( s == null )
		{
			System.out.println("find attributes: string is null");
			return -1;
		}
		
		//System.out.println("Find for " + a + " in " + s);
		
		while ( true )
		{
			Tag t = nextTag(s, n);

			if ( t == null )
				return -1;

			if ( t.open && t.close )
			{
				if ( openTag != null )
				{
					// add empty attribute to open attribute
					openAttribute.add(new Attribute(t));
				}
				else
				{
					// add an empty attribute to the calling attribute
					a.add(new Attribute(t));
				}
				n = t.end;
			}
			else if ( t.open )
			{
				if ( openTag != null )
				{
					// a sub attribute
					subs = true;
					// process this piece
					int k = findAttributes(openAttribute, s, n);
					n = k;
				}
				else
				{
					openTag = t;
					openAttribute = new Attribute(t);
					a.add(openAttribute);
					n = t.end;
					
					// see if tag has any attributes
				}
			}
			else // t.close
			{
				if ( openTag != null )
				{
					if ( t.name.equals(openTag.name) )
					{
						// found close
						if ( !subs )
						{
							// pure data
							// out(openTag.name + " " + t.name + " " + openTag.endc + " " + t.startc);
							String sub = s.substring(openTag.endc + 1, t.startc);
							openAttribute.data = clean(sub);
						}
						return t.end + 1;
					}
				}

				String op;
				if ( openTag == null ) op = "NoOpenTag"; else op = openTag.name;
				
				System.out.println("Error: Found close tag " + t + " while inside " + op);

				return t.start - 1;
			}
			// loop end
		}
	}
	
	static String clean(String s)
				 
	{
		if ( s.length() < 1 )
			return s;
		
		int n1 = 0, n2 = s.length() - 1;

		while ( true )
		{
			if ( n1 >= s.length() || s.charAt(n1) > 32 )
				break;
			n1++;
		}
		
		while ( true )
		{
			if ( n2 < 0 || s.charAt(n2) > 32 )
				break;
			n2--;
		}
		
		if ( n1 > n2 )
			return "";
		
		return s.substring(n1, n2 + 1);
	}
	
	Tag nextTag(String s, int n)
		
	{
		if ( s == null )
		{
			System.out.println("next tag: string is null");
			return null;
		}
		
		int k = s.indexOf("<", n);
		
		if ( k < 0 )
			return null;
		
		int e = s.indexOf(">", k + 1);
		
		if ( e <= k || e - k > 500 )
		{
			System.out.println("Error: Invalid tag " + s + " " + k + " " + e);
			return null;
		}
				
		String t = s.substring(k + 1, e);
		
		Tag tag = new Tag(s, k + 1, e - 1, k , e);
		
		if ( tag.name == null )
			return nextTag(s, e + 1);
		else
			return tag;
	}
}