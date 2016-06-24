/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.agmantra.utils
 *
 * File name:
 *		Mecator.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Jul 18, 2012 10:56:05 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */

package com.trimble.agmantra.layers;


/**
 * @author sprabhu
 */

public class VilicusMercator {
    public static final int MEAN_RADIUS_OF_EARTH = 6370997;

    public static final int iDeg180InMeters = 20015115; // 15320889;

    public static final int iDegMinLat = -21506583;

    public static final int iDegMaxLat = 18929129;

    public static final double dDegreesToMetersFactor = 2 * Math.PI * MEAN_RADIUS_OF_EARTH / 360.0;

    public static final long lMaxMeters = (((long)1) << 31) - 1;

    public static final long lMinMeters = -lMaxMeters - 1;

    public static final double PI = 3.1415926535897932384626433832795;

    public static final double PIBY2 = 1.5707963267948966192313216916398;

    public static final double PIBY4 = 0.78539816339744830961566084581988;

    public static final double PIBY180 = 0.017453292519943295769236907684886;
    
    public static final double     FEET_PER_METER            =  3.280839895;

    /**
     * major and minor axes of the earth in metres.
     */
    private static final double TV_A = 6378137;

    private static final double TV_B = 6356752.314;

    /**
     * flattening of earth
     */
    private static final double TV_F = (TV_A - TV_B) / TV_A;

    private static double Round(double Value, int Decimals) {

        double dMult = Math.pow(10.0, Decimals);
        long lVal;
        if (Value > 0)
            lVal = (long)((Value * dMult) + 0.5);
        else
            lVal = (long)((Value * dMult) - 0.5);
        return (double)lVal / dMult;
    }

    public static long latToY(double dLat) {
		/*
		 * Mercator's Projection overflows 32 bits when something close to -90
		 * degrees is provided as input. This does not happen if + 90 degrees is
		 * provided as input. To prevent this problem, we find the projection of
		 * abs(dLat) and finally apply the sign.
		 */
		int sign = 1;
		if (dLat < 0) {
			sign = -1;
		}
		double radianLat = Math.toRadians(Math.abs(dLat));
		double dY = Math.toDegrees(Math.log(Math.tan((Math.PI / 4)
				+ (radianLat / 2))))
				* dDegreesToMetersFactor;
		if (dY > lMaxMeters) {
			System.out.println("Input Lat " + dLat
					+ " exceeds limit. Using limit instead.");
			return sign * lMaxMeters;
		}
		return Math.round(sign * dY);
	}

	public static long lonToX(double dLon) {
		double dX = dLon * dDegreesToMetersFactor;
		if (dX > lMaxMeters) {
			System.out.println("Input Lon " + dLon
					+ " exceeds limit. Using limit instead.");
			return lMaxMeters;
		}
		if (dX < lMinMeters) {
			System.out.println("Input Lon " + dLon
					+ " exceeds limit. Using limit instead.");
			return lMinMeters;
		}
		return Math.round(dX);
	}

	public static double xToLon(long dX) {
		double dLon = dX / dDegreesToMetersFactor;
		return dLon;
	}

	public static double yToLat(long dY) {
		double radianY = Math.toRadians(dY / dDegreesToMetersFactor);
		double dLat = Math.toDegrees(Math.atan(Math.sinh(radianY)));
		return dLat;
	}

    /**
     * Computes distance between two points assuming that the earth is a sphere
     * 
     * @param x1 longitude of first point in metres
     * @param y1 latitude of first point in metres
     * @param x2 longitude of second point in metres
     * @param y2 latitude of second point in metres
     * @return distance between the two points
     */
    public static double sphericalFormulaDistance(int x1, int y1, int x2, int y2) {
        double lat1 = yToLat(y1), lat2 = yToLat(y2), lon1 = xToLon(x1), lon2 = xToLon(x2);
        double phi1 = Math.toRadians(lat1), phi2 = Math.toRadians(lat2), lambda1 = Math
                .toRadians(lon1), lambda2 = Math.toRadians(lon2);
        return MEAN_RADIUS_OF_EARTH
                * Math.acos(Math.sin(phi1) * Math.sin(phi2) + Math.cos(phi1) * Math.cos(phi2)
                        * Math.cos(lambda2 - lambda1));
    }

    /**
     * Computes distance between two points assuming that the earth is a geoid.
     * Uses T. Vincenty's Algorithm.
     * 
     * @param x1 longitude of first point in metres
     * @param y1 latitude of first point in metres
     * @param x2 longitude of second point in metres
     * @param y2 latitude of second point in metres
     * @return distance between the two points
     */
    public static double tvFormulaDistance(int x1, int y1, int x2, int y2) {
        double lat1 = yToLat(y1), lat2 = yToLat(y2), lon1 = xToLon(x1), lon2 = xToLon(x2);

        double phi1 = Math.toRadians(lat1), phi2 = Math.toRadians(lat2), U1 = Math.atan((1 - TV_F)
                * Math.tan(phi1)), U2 = Math.atan((1 - TV_F) * Math.tan(phi2)), lambda1 = Math
                .toRadians(lon1), lambda2 = Math.toRadians(lon2), L = lambda2 - lambda1;

        double lambdaPrevious = Double.POSITIVE_INFINITY, lambda;

        double cosU2 = Math.cos(U2), cosU1sinU2 = Math.cos(U1) * Math.sin(U2), sinU1cosU2 = Math
                .sin(U1) * Math.cos(U2), sinU1sinU2 = Math.sin(U1) * Math.sin(U2), cosU1cosU2 = Math
                .cos(U1) * Math.cos(U2);

        double cosSquareAlpha = 0, cos2SigmaM = 0, sinSigma = 0, cosSigma = 0, sigma = 0;

        int noOfIterations = 0;
        for (lambda = L; (lambdaPrevious - lambda) > .000001;) {
            if (noOfIterations++ > 1000) {
                // NavCardGenerate.xmsg("Between ("+lat1+","+lon1+") and ("+lat2+","+lon2+") in degrees, Vincenty's distance computation algorithm is looping too many times, using Spherical Algorithm instead.");
                return sphericalFormulaDistance(x1, y1, x2, y2);
            }

            lambdaPrevious = lambda;
            double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
            // Computing sin sigma
            {
                double squareTemp = cosU2 * sinLambda;

                double total = squareTemp * squareTemp;
                squareTemp = cosU1sinU2 - sinU1cosU2 * cosLambda;
                total += squareTemp * squareTemp;

                sinSigma = Math.sqrt(total);
            }
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);

            double sinAlpha = cosU1cosU2 * sinLambda / sinSigma;
            cosSquareAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1sinU2 / cosSquareAlpha;

            double C = TV_F / 16 * cosSquareAlpha * (4 + TV_F * (4 - 3 * cosSquareAlpha));
            lambda = L
                    + (1 - C)
                    * TV_F
                    * sinAlpha
                    * (sigma + C * sinSigma
                            * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        }

        double uSquare = cosSquareAlpha * (TV_A * TV_A - TV_B * TV_B) / (TV_B * TV_B);
        double A = 1 + uSquare / 16384
                * (4096 + uSquare * (-768 + uSquare * (320 - 175 * uSquare)));
        double B = uSquare / 1024 * (256 + uSquare * (-128 + uSquare * (74 - 47 * uSquare)));
        double deltaSigma = B
                * sinSigma
                * (cos2SigmaM + B
                        / 4
                        * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                                * (-3 + 4 * sinSigma * sinSigma)
                                * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double returnValue = TV_B * A * (sigma - deltaSigma);

        // if return value is NaN
        if (returnValue != returnValue) {
            // NavCardGenerate.xmsg("Between ("+lat1+","+lon1+") and ("+lat2+","+lon2+") in degrees, Vincenty's distance computation algorithm returns NaN, using Spherical Algorithm instead.");
            if(x1 == x2 && y1==y2)
            {
               return 0;
            }
            return sphericalFormulaDistance(x1, y1, x2, y2);
        }
        return returnValue;
    }
    
    public static int MoveEast(CPoint mPoint, double Dist) {
 	   
 	   double dLat = yToLat(mPoint.iY);
 	   double dLon = xToLon(mPoint.iX);

 	   //Long += RadToDeg( ( Dist / EarthRad ) / cos( DegToRad( Lat ) ) );
 	   dLon += Math.toDegrees(( Dist / MEAN_RADIUS_OF_EARTH ) / Math.cos( Math.toRadians( dLat ) ) );

 	   if ( dLon >  180 ) dLon -= 360;
 	   if ( dLon < -180 ) dLon += 360;

 	   return (int)lonToX(dLon);
 	   }

    public static int MoveNorth (CPoint mPoint, double Dist) {

 	   double dLat = yToLat(mPoint.iY);

 	   //Lat += RadToDeg( Dist / EarthRad );
 	   dLat += Math.toDegrees( Dist / MEAN_RADIUS_OF_EARTH );

 	   if ( dLat >  90 ) dLat =  85;
 	   if ( dLat < -90 ) dLat = -85;

 	   return (int)latToY(dLat);
 	   }
    public static CPoint meterToPixels(int iXinMeter, int iYinMeter,
          int iZoom, int iCenterX, int iCenterY, int iMapWidth,
          int iMapHeight, int iOrientation) {
  
       CPoint p = new CPoint(iXinMeter, iYinMeter);
  
  if (p != null) {
          p.iX = (int) ((p.iX- iCenterX) / Math.pow(2.0, iZoom));
          p.iY = (int) ((p.iY - iCenterY) / Math.pow(2.0, iZoom));

          rotate(p, iOrientation);

          p.iX += iMapWidth / 2;
          p.iY += iMapHeight / 2;

          p.iY = (iMapHeight - 1) - p.iY;
         /* try {
                  if (NativeManager.nativeCalls == null) {
                          Log.i("SPIME","NativeManager.nativeCalls is null");
                          return null;
                  }
                  p = NativeManager.nativeCalls.JNITransform(p.x, p.y);
          } catch (NullPointerException e) {
                  Log.i("SPIME_ERROR", "Nullpoint exception -->meterToPixels");
                  e.printStackTrace();
          }*/
  }
  
  return p;

}

public static CPoint pixelTometer(int iXinPixel, int iYinPixel,
          int iZoom, int iCenterX, int iCenterY, int iMapWidth,
          int iMapHeight, int iOrientation) {
   CPoint p = new CPoint(iXinPixel, iYinPixel);
  
  //p = NativeManager.nativeCalls.JNIReverseTransform(p.iX,p.iY);

  p.iX = p.iX - (iMapWidth / 2);
  p.iY = p.iY - (iMapHeight / 2);

  rotate(p, iOrientation);

  p.iX= iCenterX + (int) (p.iX * Math.pow(2.0, iZoom));
  p.iY = iCenterY - (int) (p.iY * Math.pow(2.0, iZoom));

  return p;
}

public static void rotate(CPoint p, int iOrientation)

{
  if (iOrientation == 0)
          return;

  long x, y, cos, sin, t;

  x = p.iX;
  y = p.iY;

  // int angle = (orientation/10) % 9;
  int angle = iOrientation % 90;

  switch (angle) {
  case 0:
  default:
          cos = 32768;
          sin = 0;
          break;
  case 1:
          cos = 32781;
          sin = 571;
          break;
  case 2:
          cos = 32766;
          sin = 1143;
          break;
  case 3:
          cos = 32741;
          sin = 1714;
          break;
  case 4:
          cos = 32706;
          sin = 2285;
          break;
  case 5:
          cos = 32661;
          sin = 2855;
          break;
  case 6:
          cos = 32606;
          sin = 3425;
          break;
  case 7:
          cos = 32541;
          sin = 3993;
          break;
  case 8:
          cos = 32466;
          sin = 4560;
          break;
  case 9:
          cos = 32382;
          sin = 5126;
          break;
  case 10:
          cos = 32287;
          sin = 5690;
          break;
  case 11:
          cos = 32183;
          sin = 6252;
          break;
  case 12:
          cos = 32069;
          sin = 6812;
          break;
  case 13:
          cos = 31945;
          sin = 7371;
          break;
  case 14:
          cos = 31812;
          sin = 7927;
          break;
  case 15:
          cos = 31668;
          sin = 8480;
          break;
  case 16:
          cos = 31515;
          sin = 9032;
          break;
  case 17:
          cos = 31353;
          sin = 9580;
          break;
  case 18:
          cos = 31181;
          sin = 10125;
          break;
  case 19:
          cos = 30999;
          sin = 10668;
          break;
  case 20:
          cos = 30808;
          sin = 11207;
          break;
  case 21:
          cos = 30608;
          sin = 11743;
          break;
  case 22:
          cos = 30398;
          sin = 12275;
          break;
  case 23:
          cos = 30179;
          sin = 12803;
          break;
  case 24:
          cos = 29951;
          sin = 13327;
          break;
  case 25:
          cos = 29714;
          sin = 13848;
          break;
  case 26:
          cos = 29467;
          sin = 14364;
          break;
  case 27:
          cos = 29212;
          sin = 14876;
          break;
  case 28:
          cos = 28948;
          sin = 15383;
          break;
  case 29:
          cos = 28675;
          sin = 15886;
          break;
  case 30:
          cos = 28393;
          sin = 16384;
          break;
  case 31:
          cos = 28103;
          sin = 16876;
          break;
  case 32:
          cos = 27804;
          sin = 17364;
          break;
  case 33:
          cos = 27496;
          sin = 17846;
          break;
  case 34:
          cos = 27180;
          sin = 18323;
          break;
  case 35:
          cos = 26856;
          sin = 18794;
          break;
  case 36:
          cos = 26524;
          sin = 19260;
          break;
  case 37:
          cos = 26184;
          sin = 19720;
          break;
  case 38:
          cos = 25835;
          sin = 20173;
          break;
  case 39:
          cos = 25479;
          sin = 20621;
          break;
  case 40:
          cos = 25115;
          sin = 21062;
          break;
  case 41:
          cos = 24743;
          sin = 21497;
          break;
  case 42:
          cos = 24364;
          sin = 21926;
          break;
  case 43:
          cos = 23978;
          sin = 22347;
          break;
  case 44:
          cos = 23584;
          sin = 22762;
          break;
  case 45:
          cos = 23183;
          sin = 23170;
          break;
  case 46:
          cos = 22775;
          sin = 23571;
          break;
  case 47:
          cos = 22359;
          sin = 23964;
          break;
  case 48:
          cos = 21938;
          sin = 24351;
          break;
  case 49:
          cos = 21509;
          sin = 24730;
          break;
  case 50:
          cos = 21074;
          sin = 25101;
          break;
  case 51:
          cos = 20632;
          sin = 25465;
          break;
  case 52:
          cos = 20185;
          sin = 25821;
          break;
  case 53:
          cos = 19731;
          sin = 26169;
          break;
  case 54:
          cos = 19271;
          sin = 26509;
          break;
  case 55:
          cos = 18805;
          sin = 26841;
          break;
  case 56:
          cos = 18333;
          sin = 27165;
          break;
  case 57:
          cos = 17856;
          sin = 27481;
          break;
  case 58:
          cos = 17373;
          sin = 27788;
          break;
  case 59:
          cos = 16886;
          sin = 28087;
          break;
  case 60:
          cos = 16393;
          sin = 28377;
          break;
  case 61:
          cos = 15894;
          sin = 28659;
          break;
  case 62:
          cos = 15392;
          sin = 28932;
          break;
  case 63:
          cos = 14884;
          sin = 29196;
          break;
  case 64:
          cos = 14372;
          sin = 29451;
          break;
  case 65:
          cos = 13855;
          sin = 29697;
          break;
  case 66:
          cos = 13335;
          sin = 29935;
          break;
  case 67:
          cos = 12810;
          sin = 30163;
          break;
  case 68:
          cos = 12281;
          sin = 30381;
          break;
  case 69:
          cos = 11749;
          sin = 30591;
          break;
  case 70:
          cos = 11213;
          sin = 30791;
          break;
  case 71:
          cos = 10674;
          sin = 30982;
          break;
  case 72:
          cos = 10131;
          sin = 31164;
          break;
  case 73:
          cos = 9585;
          sin = 31336;
          break;
  case 74:
          cos = 9037;
          sin = 31498;
          break;
  case 75:
          cos = 8485;
          sin = 31651;
          break;
  case 76:
          cos = 7931;
          sin = 31794;
          break;
  case 77:
          cos = 7375;
          sin = 31928;
          break;
  case 78:
          cos = 6816;
          sin = 32051;
          break;
  case 79:
          cos = 6255;
          sin = 32165;
          break;
  case 80:
          cos = 5693;
          sin = 32270;
          break;
  case 81:
          cos = 5128;
          sin = 32364;
          break;
  case 82:
          cos = 4562;
          sin = 32449;
          break;
  case 83:
          cos = 3995;
          sin = 32523;
          break;
  case 84:
          cos = 3427;
          sin = 32588;
          break;
  case 85:
          cos = 2857;
          sin = 32643;
          break;
  case 86:
          cos = 2287;
          sin = 32688;
          break;
  case 87:
          cos = 1715;
          sin = 32723;
          break;
  case 88:
          cos = 1144;
          sin = 32748;
          break;
  case 89:
          cos = 572;
          sin = 32763;
          break;

  }

  int quad = Math.abs(iOrientation / 90);

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

  p.iX = (int) ((x * cos - y * sin) >> 15);
  p.iY = (int) ((x * sin + y * cos) >> 15);

}
}
