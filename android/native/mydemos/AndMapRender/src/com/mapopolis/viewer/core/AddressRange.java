package com.mapopolis.viewer.core;

class AddressRange
{
	int from, to;
	boolean even;
	boolean left;

	AddressRange(int f, int t, boolean e, boolean l)
	{
		from = f;
		to = t;
		even = e;
		left = l;
	}

	private static boolean even(int n)
	{
		return (n & 1) == 0;
	}

	boolean containsAddress(int n)
	{
		if (even == even(n))
		{
			int max, min;

			if (from > to)
			{
				max = from;
				min = to;
			}
			else
			{
				max = to;
				min = from;
			}

			if (n >= min && n <= max) return true;
		}

		return false;
	}

	public String toString()
	{
		return from + " " + to + " " + (even ? "even" : "odd") + " " + (left ? "left" : "right");
	}
}