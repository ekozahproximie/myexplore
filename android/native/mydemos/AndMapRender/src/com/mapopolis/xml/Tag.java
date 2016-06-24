
package com.mapopolis.xml;

import java.util.*;

public class Tag

{
	int start, end, startc, endc;
	String string;
	String name;
	boolean open;
	boolean close;
	Vector<Attribute> attributes;		// attributes in the tag
	String adata;
	
	Tag(String str, int s, int e, int ss, int ee)
		
	{
		startc = ss;
		endc = ee;
		
		start = s;
		end = e;
		string = str;
		
		if ( s < 0 )
			return;
		
		open = close = false;

		start = nextNonSpace(string, start);
		
		int n = end;

		while ( true )
		{
			if ( string.charAt(n) != ' ' )
				break;
			n--;
		}
		
		end = n;
		
		int dataEnd = end;
		
		int nameEnd, nameStart;
		
		if ( string.charAt(start) == '/' )
		{
			close = true;
			nameStart = nextNonSpace(string, start + 1);
		}
		else
		{
			open = true;
			nameStart = nextNonSpace(string, start);
		}

		if ( string.charAt(end) == '/' )
		{
			close = true;
			dataEnd--;
		}
		
		nameEnd = lastNonSpace(string, nameStart);
		
		//out(start + " " + end + "     " + nameStart + " " + nameEnd);
		name = string.substring(nameStart, nameEnd + 1);
		
		if ( name.startsWith("!") || name.startsWith("?") )
			name = null;
		
		if ( dataEnd - nameEnd > 0 )
		{
			adata = Attribute.clean(string.substring(nameEnd + 1, dataEnd + 1));
			
			int k = adata.indexOf("=");
			
			if ( k > 0 )
			{
				String nn = adata.substring(0, k);
				//if ( !nn.equals("ID") && !nn.equals("CLASS") && !nn.equals("HREF") ) 
				//	Form1.out("Adata name " + nn);
			}
		}
	}
	
	int nextNonSpace(String s, int n)
		
	{
		while ( true )
		{
			if ( s.charAt(n) != ' ' )
				return n;
			n++;
		}
	}
	
	int lastNonSpace(String s, int n)
		
	{
		while ( true )
		{
			if ( s.charAt(n) == ' ' || s.charAt(n) == '>' )
				return n - 1;
			n++;
		}
	}
	
	public String toString()
		
	{
		if ( true )
			return "(=" + name + "= " + (open? "open ":"") + (close? "close ":"") + ") ";
		else
			return "";
	}
}