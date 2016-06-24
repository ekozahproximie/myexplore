
package com.mapopolis.viewer.draw;



import android.graphics.Paint;

class Letter

{
	public String toString()
	
	{
		return llx + " " + lly + " " + orientation;
	}
	
    int orientation;

    int llx;

    int lly;

    int xs;

    int xe;

    int ys;

    int ye;

    int xbits;

    int ybits;

    int k;

    int width;

    Paint color;
}