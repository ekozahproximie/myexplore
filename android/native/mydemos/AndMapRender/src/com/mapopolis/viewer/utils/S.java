
package com.mapopolis.viewer.utils;

class S implements ISortable

{
	String s;
	
	public S(String t)
	
	{
		s = t;
	}
	
	public int compareTo(ISortable t)
	
	{
		return s.compareTo(((S) t).s);
	}
}
