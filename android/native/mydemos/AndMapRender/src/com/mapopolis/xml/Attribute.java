
package com.mapopolis.xml;

import java.util.*;
import java.io.*;

public class Attribute

{
	Tag tag;
	Vector<Attribute> attributes = new Vector<Attribute>();
	public String data;
	
	Attribute(String s)
			  
	{
		tag = new Tag("", -1, -1, -1, -1);
		tag.name = s;
	}
	
	Attribute(Tag t)
		
	{
		tag = t;
		data = "";
	}
	
	public Attribute finde(String name, int n)
	{
		Attribute a = find(name, n);
		if ( a == null )
		{
			Attribute b = new Attribute("");
			b.data = "";
			return b;
		}
		else
			return a;
	}
	
	public Attribute find(String name, int n)
		
	{
		// find the n-th occurence of the tag with the given name
		
		int k = name.indexOf(".");
		
		String tname;
		String rname = null;
		
		if ( k < 0 )
			tname = name;
		else
		{
			tname = name.substring(0, k);
			rname = name.substring(k + 1);
		}
		
		int count = 0;
		
		//System.out.println(tname + " ============= " + rname);
		
		for ( int i = 0; i < attributes.size(); ++i )
		{
			Attribute a = (Attribute) attributes.elementAt(i);
			
			//System.out.println("consider tag " + a.tag.name.trim() + " " + tname + " " + i + " " + n);
			
			if ( a.tag.name.trim().equals(tname) )
			{
				if ( rname == null )
				{
					// at lowest level
					
					if ( count == n )
						return a;
					
					count++;
				}
				else
				{
					return a.find(rname, n);
				}
			}
		}
		
		return null;
	}
	
	void add(Attribute a)
		
	{
		attributes.addElement(a);
	}
	
	public String toString()
		
	{
		return "Attribute: " + tag.name;	
	}
	
	void print(PrintStream out, String pre)
		
	{
		//if ( tag.adata != null && !tag.adata.equals("") )
		//	out.println(tag.name + " ADATA=" + tag.adata);
		
		
		if ( data != null && !data.equals("") )
		{
			out.println(pre + tag.name + "=" + data + ";");
		}
		else
		{
			if ( attributes.size() > 0 )
				out.println(pre + tag.name);
			for ( int i = 0; i < attributes.size(); ++i )
			{
				Attribute a = (Attribute) attributes.elementAt(i);
				a.print(out, pre + "   ");
			}
		}
	}
	
	String getString(String pre, Vector<String> vecIgnore)
		
	{
		// clean " out of data
		
		if ( data != null && !data.equals("") )
		{
			String p;
			
			if ( !pre.equals("") )
				p = pre + "." + tag.name;
			else
				p = tag.name;
			
			if ( vecIgnore != null )
			{
				for ( int i = 0; i < vecIgnore.size(); ++i )
				{
					if ( p.startsWith((String) vecIgnore.elementAt(i)) )
						return "";
				}
			}
			
			return p + "=\"" + clean(data) + "\" ";
		}
		else
		{
			if ( attributes.size() > 0 )
				if ( !pre.equals("") )
					pre = pre + "." +  tag.name;
				else
					pre = tag.name;

			String s = "";
			
			for ( int i = 0; i < attributes.size(); ++i )
			{
				Attribute a = (Attribute) attributes.elementAt(i);
				s += a.getString(pre, vecIgnore);
			}
			
			return s;
		}
			
	}
	
	public static String clean(String s)
		
	{
		byte[] b = s.getBytes();
		
		for ( int i = 0; i < b.length; ++i )
			if ( b[i] == (byte) '"' )
				b[i] = (byte) '\'';
		
		return new String(b);
	}
}