package com.trimble.agmantra.layers;

import com.trimble.agmantra.utils.Mercator;

public class PolarCoord {

	public double dAngle;
	public double dRadius;

	public PolarCoord(CPoint RectCoord) {
		dRadius = Math.sqrt( (double)RectCoord.iX * (double)RectCoord.iX + (double)RectCoord.iY * (double)RectCoord.iY );

		  if ( RectCoord.iX == 0 && RectCoord.iY == 0 ) {
		    dAngle = 0;
		    }
		  else {
		    dAngle = Math.atan2( (double)RectCoord.iY, (double)RectCoord.iX );

		    // atan2 returns zero for angle of 180 degrees, or pi, in Windows CE
		    if ( dAngle == 0 && RectCoord.iX < 0 ) {
		      dAngle = Mercator.PI;
		      }
		    }

		  if ( dAngle < 0 ) {
		    dAngle += (2 * Mercator.PI);
		    }

	}
};