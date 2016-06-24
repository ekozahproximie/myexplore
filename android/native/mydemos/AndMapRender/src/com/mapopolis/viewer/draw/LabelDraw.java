
package com.mapopolis.viewer.draw;



import java.util.Vector;

import android.graphics.Paint;

import com.mapopolis.viewer.engine.Engine;
import com.mapopolis.viewer.engine.MapopolisException;
import com.mapopolis.viewer.engine.PersistentSettings;
import com.mapopolis.viewer.utils.PixelCoordinates;

public class LabelDraw

{
	static int Up = 1;
	static int Across = 0;
	static int Down = -1;
	static int None = -99;
	
	public static int smallFontBytesPerLetterH = 7;
    public static int smallFontBytesPerLetterV = 5;
    public static int smallFontHeight = 7;
    public static int smallFontWidth = 5;
    public static int largeFontIntsPerLetterH = 11;
    public static int largeFontIntsPerLetterV = 11;
    public static int largeFontHeight = 11;
    public static int largeFontWidth = 11;
    public static int boldFontIntsPerLetterH = 11;
    public static int boldFontIntsPerLetterV = 11;
    public static int boldFontHeight = 11;
    public static int boldFontWidth = 11;
    public static int FontSmall = 0;
    public static int FontBold = 1;
    public static int FontLarge = 2;

	private static Vector pointsBuffer = null;

    private static int hhminx;
    private static int hhmaxx;
    private static int hhminy;
    private static int hhmaxy;
    private static int hdminx;
    private static int hdmaxx;
    private static int hdminy;
    private static int hdmaxy;
    private static int huminx;
    private static int humaxx;
    private static int huminy;
    private static int humaxy;
    private static int uuminx;
    private static int uumaxx;
    private static int uuminy;
    private static int uumaxy;
    private static int uhminx;
    private static int uhmaxx;
    private static int uhminy;
    private static int uhmaxy;
    private static int ddminx;
    private static int ddmaxx;
    private static int ddminy;
    private static int ddmaxy;
    private static int dhminx;
    private static int dhmaxx;
    private static int dhminy;
    private static int dhmaxy;

    private static int OverlapBytesPerRow;
    private static int screenWidth2;
    private static int screenHeight2;
    private static byte[] overlapTable;
    private static LinePosition linePosition;

    public static void beginLabels()

    {
    	if ( pointsBuffer == null )
    	{
	        setParameters();
	        pointsBuffer = new Vector();
    	}
    	
        // initialize the overlap box and letter array

        int n = (MapView.maxDisplayHeight * MapView.maxDisplayWidth) / 32;
        overlapTable = new byte[n];
    }

    private static void render(MapView mv)

    {
        mv.getGB().setColor(PersistentSettings.StreetLabelColor);

        // byte color = (byte) gb.color;
        //int[] bytes = mv.getGB().bytes;
        
        int width = mv.getGB().width;

        GraphicsBuffer gb = mv.getGB();

        for (int i = 0; i < pointsBuffer.size(); ++i) 
        {
            PixelCoordinates p = (PixelCoordinates) pointsBuffer.elementAt(i);
            gb.drawPoint(p.x, p.y);
        }

        pointsBuffer.removeAllElements();
    }

    private static void drawLetter(Letter letter)

    {
        int k = letter.k;

        if (letter.orientation == 0) drawLetterH(letter.xs, letter.ys, k);
        else if (letter.orientation == 1) drawLetterV(letter.xs, letter.ys, k, 1);
        else if (letter.orientation == -1)
                drawLetterV(letter.xs, letter.ys, k, 0);
    }

    private static void drawLetterH(int x, int y, int n)

    {
        if (PersistentSettings.currentTextFont != FontSmall) 
        {
            int i, j, w, maxj, base, index;
            char letter;
            char[] lp;

            if (PersistentSettings.currentTextFont == FontBold) 
            {
                lp = BoldFont.boldFontLettersh;
                base = n * boldFontIntsPerLetterH;
                w = BoldFont.boldFontWidths[n];
                maxj = boldFontIntsPerLetterH;
            } 
            else 
            {
                lp = LargeFont.largeFontLettersh;
                base = n * largeFontIntsPerLetterH;
                w = LargeFont.largeFontWidths[n];
                maxj = largeFontIntsPerLetterH;
            }

            for (j = 0; j < maxj; ++j) {
                letter = lp[base + j];

                for (i = 0; i < w; ++i)
                    if ((letter & (0x8000 >> i)) != 0) 
                    {
                        pointsBuffer.addElement(new PixelCoordinates(x + i, y + j));
                    }
            }
        } else 
        {
            char[] letters;
            int i, j, w, maxj, index;

            letters = SmallFont.smallFontLettersh;
            index = n * smallFontBytesPerLetterH;
            w = SmallFont.smallFontWidths[n];
            maxj = smallFontBytesPerLetterH;

            for (i = 0; i < w; ++i)
                for (j = 0; j < maxj; ++j)
                    if ((letters[index + j] & (0x80 >> i)) != 0) 
                    {
                        pointsBuffer.addElement(new PixelCoordinates(x + i, y + j));
                    }
        }
    }

    private static void drawLetterV(int x, int y, int n, int u)

    {
        if (PersistentSettings.currentTextFont != FontSmall) 
        {
            char letter;
            char[] lp;
            int i, j, w, base;

            if (PersistentSettings.currentTextFont == FontBold) {
                if (u != 0) {
                    lp = BoldFont.boldFontLettersu;
                    base = n * boldFontIntsPerLetterV;
                    w = boldFontWidth;
                } else {
                    lp = BoldFont.boldFontLettersd;
                    base = n * boldFontIntsPerLetterV;
                    w = BoldFont.boldFontWidths[n];
                }
            } else {
                if (u != 0) {
                    lp = LargeFont.largeFontLettersu;
                    base = n * largeFontIntsPerLetterV;
                    w = largeFontWidth;
                } else {
                    lp = LargeFont.largeFontLettersd;
                    base = n * largeFontIntsPerLetterV;
                    w = LargeFont.largeFontWidths[n];
                }
            }

            for (j = 0; j < w; ++j) 
            {
                letter = lp[base + j];

                for (i = 0; i < largeFontHeight; ++i)
                    if ((letter & (0x8000 >> i)) != 0) 
                    {
                        pointsBuffer.addElement(new PixelCoordinates(x + i, y + j));
                    }
            }
        } else {
            char[] letters;
            int i, j, w, index;

            if (u != 0) {
                letters = SmallFont.smallFontLettersu;
                index = n * smallFontBytesPerLetterV;
                w = smallFontWidth;
            } else {
                letters = SmallFont.smallFontLettersd;
                index = n * smallFontBytesPerLetterV;
                w = SmallFont.smallFontWidths[n];
            }

            for (i = 0; i < smallFontHeight; ++i)
                for (j = 0; j < w; ++j)
                    if ((letters[index + j] & (0x80 >> i)) != 0) 
                    {
                        pointsBuffer.addElement(new PixelCoordinates(x + i, y + j));
                    }
        }
    }

    private static int pixelLength(String str)

    {
        int x = 0, i, len = str.length();

        if (PersistentSettings.currentTextFont == FontLarge) {
            for (i = 0; i < len; ++i)
                x += LargeFont.largeFontWidths[getLetterIndex(str.charAt(i))]
                        + currentSpacing();
        } else if (PersistentSettings.currentTextFont == FontSmall) {
            for (i = 0; i < len; ++i)
                x += SmallFont.smallFontWidths[getLetterIndex(str.charAt(i))]
                        + currentSpacing();
        } else if (PersistentSettings.currentTextFont == FontBold) {
            for (i = 0; i < len; ++i)
                x += BoldFont.boldFontWidths[getLetterIndex(str.charAt(i))]
                        + currentSpacing();
        }

        return x;
    }

    private static int pixelLengthB(String str, int n)

    {
        int x = 0, i, len = str.length();

        if (PersistentSettings.currentTextFont == FontLarge) {
            for (i = 0; i < len && i < n && str.charAt(i) != '|'; ++i)
                x += LargeFont.largeFontWidths[getLetterIndex(str.charAt(i))]
                        + currentSpacing();
        } else if (PersistentSettings.currentTextFont == FontSmall) {
            for (i = 0; i < len && i < n && str.charAt(i) != '|'; ++i)
                x += SmallFont.smallFontWidths[getLetterIndex(str.charAt(i))]
                        + currentSpacing();
        } else if (PersistentSettings.currentTextFont == FontBold) {
            for (i = 0; i < len && i < n && str.charAt(i) != '|'; ++i)
                x += BoldFont.boldFontWidths[getLetterIndex(str.charAt(i))]
                        + currentSpacing();
        }

        return x;
    }

    private static int getLetterIndex(int cc)

    {
        int c = cc;

        if (c < 0) c += 256;

        if (c < 128) 
        {
            if (c > (byte) '{' || c == (byte) '/') return 0;
            else return c - 32;
        } 
        else 
        {
            if (c < 192) return 0;
            else {
                // 91 letters precede the upper character set (over ascii 128)
                return c - 192 + 91;
            }
        }
    }

    private static int currentSpacing()

    {
        if (PersistentSettings.currentTextFont == FontSmall) return 1;
        else if (PersistentSettings.currentTextFont == FontLarge) return 2;
        else return 2;
    }

    private static void setParameters()

    {
        int fw, fw2, fh, fh2;

        if (PersistentSettings.currentTextFont == FontSmall) {
            fw = smallFontWidth;
            fh = smallFontHeight;
        } else if (PersistentSettings.currentTextFont == FontLarge) {
            fw = largeFontWidth;
            fh = largeFontHeight;
        } else {
            fw = boldFontWidth;
            fh = boldFontHeight;
        }

        fw2 = fw / 2;
        fh2 = fh / 2;

        hhminx = 0;
        hhmaxx = fw2;
        hhminy = -(fh - 1);
        hhmaxy = fh + 1;

        hdminx = -(fw2 + 1);
        hdmaxx = fw2 + 1;
        hdminy = fw2 + 1;
        hdmaxy = fw + 1;

        huminx = 0;
        humaxx = fh + fh2;
        huminy = -(fh + fw + 1 + fw2);
        humaxy = -(fh + fw + 1);

        uuminx = -(fh - 1);
        uumaxx = fh + 1;
        uuminy = -fw2;
        uumaxy = 0;

        uhminx = fw2 + 1;
        uhmaxx = fw + 1;
        uhminy = -(fw2 + 1);
        uhmaxy = fw2 + 1;

        ddminx = -(fh - 1);
        ddmaxx = fh + 1;
        ddminy = 0;
        ddmaxy = fw2;

        dhminx = fh + fw + 1;
        dhmaxx = fh + fw + 1 + fw2;
        dhminy = 0;
        dhmaxy = fh + fh2;

        screenWidth2 = MapView.maxDisplayWidth >> 1;
        screenHeight2 = MapView.maxDisplayHeight >> 1;

        OverlapBytesPerRow = screenWidth2 >> 3;
    }

    public static int labelStreet(MapView mv, String name, Vector points, Paint color) throws MapopolisException

    {
        //
        // possible letter adjustment so as not to overwrite street line OR
        // use highest y under letter to set y for horizontal (for example) and
        // similar logic for x if up or down
        //

		//Engine.out("label " + name);
		
        int i, j, namepixlen, bestSoFarValue, lettersInBest, g, n;
        int max, trys, step;

        if (name.equals("Z") || name.equals("R")) return 0;

        name = capitalization(name);
        
        bestSoFarValue = 9999;
        lettersInBest = 0;

        trys = 3;

        Vector bestSoFarSet = null;
        int devFromPerfect = 0;

        for (i = 0; i < trys; ++i) 
        {
            linePosition = new LinePosition(points);
            linePosition.start(true, i, trys);

            //if ( linePosition.orientation == Across ) break;
            //debug = ( name.toUpperCase().indexOf("COVENTRY") >= 0 );
            //System.out.println(name + " " + linePosition);
            
            Vector holder = new Vector();

            devFromPerfect = attemptPut(mv, name, points, linePosition, 
            		holder,
                    100, color, i, trys, mv.getWidth(), mv.getHeight());

            //msg("dev " + devFromPerfect);

            if (devFromPerfect < 10) 
            {
                bestSoFarValue = devFromPerfect;
                bestSoFarSet = holder;
                break;
            } 
            else if (devFromPerfect < bestSoFarValue) 
            {
                bestSoFarValue = devFromPerfect;
                bestSoFarSet = holder;
            }
        }

        // now decide if we want to draw the street based on bestValueSoFar

        if (bestSoFarValue != 9999) if (devFromPerfect < 50) 
        {
            for (i = 0; i < bestSoFarSet.size(); ++i) 
            {
                Letter l = (Letter) bestSoFarSet.elementAt(i);
                addToOverlapTable(l);
                drawLetter(l);
                //System.out.println(l.k);
            }

            render(mv);
            return 1;
        }

        return 0;
    }

    private static int attemptPut(MapView mv, String streetname, Vector points,
            LinePosition linePosition, Vector letters, int immediateStop,
            Paint color, int n, int t, int width, int height)
            throws MapopolisException

    {
        int i, iX, iY, lw, kx, ky;
        int dev, len;
        boolean ok;
        Letter rletter = null;
        char c, lastc = 0, lastlastc;

        if (linePosition.pixels.size() < 25) 
        	return 1000;

        kx = 0;
        ky = 0;

        if (mv.zoom < 3) 
        {
            if (mv.zoom == 0) 
            	kx = ky = Draw.LargeStreetWidth0 >> 1;
            else if (mv.zoom == 1) 
            	kx = ky = Draw.LargeStreetWidth1 >> 1;
            else if (mv.zoom == 2)
            	kx = ky = Draw.LargeStreetWidth2 >> 1;
        }

        int orientation = linePosition.orientation;

        if (orientation < None)
        	return 1000;

        iX = -1;
        iY = -1;
        lw = 0;

        dev = 0;

        c = '$';
        len = streetname.length();

        for (i = 0; i < len; ++i) 
        {
            lastlastc = lastc;
            lastc = c;
            c = streetname.charAt(i);

            if (i == 0) 
            {
                iX = linePosition.x;
                iY = linePosition.y;

                int px = 0, py = 0;

                if (orientation == Across) py = -ky;
                else if (orientation == Up) px = -kx;
                else if (orientation == Down) px = kx;

                rletter = makeLetter(c, iX + px, iY + py, orientation, color, width, height);
            } 
            else if (orientation == Across) 
            {
                rletter = locatedLetter(c, 0, 
                		iX + lw + hhminx, 
						iX + lw + hhmaxx, 
						iY + hhminy - ky, 
						iY + hhmaxy - ky,
                        color, 0, -ky, width, height);
            }
            else if (orientation == Up) 
            {
                rletter = locatedLetter(c, 1, 
                		iX + uuminx - kx, 
						iX + uumaxx - kx, 
						iY - lw + uuminy, 
						iY - lw + uumaxy, 
						color, -kx, 0, width, height);
            }
            else if (orientation == Down) 
            {
                rletter = locatedLetter(c, -1, 
                		iX + ddminx + kx, 
						iX + ddmaxx + kx, 
						iY + lw + ddminy, 
						iY + lw + ddmaxy,
                        color, kx, 0, width, height);
            } 
            else 
            {
            	throw new MapopolisException("Invalid orientation");
            }

            // System.out.println(c + " " + rletter);

            ok = false;

            if (rletter.llx >= 0) 
            {
                int x, o;

                x = (rletter.xs + rletter.xe) >> 2;
                o = ((rletter.ys + rletter.ye) >> 2) * OverlapBytesPerRow
                        + (x >> 3);

                if ((overlapTable[o] & (0x80 >> (x & 7))) == 0) 
                	ok = true;
            }

            if (!ok) 
            {
                // calculate dev from perfect

                if (i <= 4) dev += 50;
                else {
                    if (c == ' ' || lastc == ' ') dev += 10;
                    else if (len < 5) 
                    {
                        // short names really have to almost be all there

                        dev += 50;
                    } 
                    else if (len > 12) 
                    {
                        // if have 10 letters of a long name, good enough

                        if (i > 10) dev += 15;
                        else if (i > 7) dev += 20;
                        else if (i > 5) dev += 25;
                        else dev += 30;
                    } 
                    else 
                    {
                        // moderate size names

                        if (i > 7) dev += 20;
                        else dev += 35;
                    }
                }

                // change last letter

                if (letters.size() > 0) 
                {
                    Letter tletter = (Letter) letters.elementAt(letters.size() - 1);
                    letters.removeElementAt(letters.size() - 1);

                    if (lastlastc == ' ') 
                    {
                        tletter = (Letter) letters.elementAt(letters.size() - 1);
                        letters.removeElementAt(letters.size() - 1);
                    }

                    rletter = makeLetter('%', tletter.llx, tletter.lly, tletter.orientation, color, width, height);
                    letters.addElement(rletter);
                }

                break;
            } 
            else 
            {
                letters.addElement(rletter);
            }

            lw = rletter.width + currentSpacing();

            iX = rletter.llx;
            iY = rletter.lly;

            orientation = rletter.orientation;

            if (dev >= immediateStop) break;
        }

        return dev;
    }

    private static void addToOverlapTable(Letter letter)

    {
        int x, y, k;

        // may have to increase 4

        int x0 = (letter.xs - 4) >> 1;
        int x1 = (letter.xe + 4) >> 1;
        int y0 = (letter.ys - 4) >> 1;
        int y1 = (letter.ye + 4) >> 1;

        if (x0 < 0) x0 = 0;
        if (x0 >= screenWidth2) x0 = screenWidth2 - 1;

        if (x1 < 0) x1 = 0;
        if (x1 >= screenWidth2) x1 = screenWidth2 - 1;

        if (y0 < 0) y0 = 0;
        if (y0 >= screenHeight2) y0 = screenHeight2 - 1;

        if (y1 < 0) y1 = 0;
        if (y1 >= screenHeight2) y1 = screenHeight2 - 1;

        k = OverlapBytesPerRow * y0;

        for (y = y0; y <= y1; ++y) {
            for (x = x0; x <= x1; ++x)
                overlapTable[k + (x >> 3)] |= (0x80 >> (x & 7));

            k += OverlapBytesPerRow;
        }
    }

    private static Letter locatedLetter(int c, int orient, 
    		int xmin, int xmax,
            int ymin, int ymax, 
            Paint color, int kx, int ky, int width,
            int height)

    {
        int npix, top;
        int x, y;

        top = linePosition.currPixel + 15;

        if (linePosition.pixels.size() < top) 
        	top = linePosition.pixels.size();

        if (top - linePosition.currPixel >= 5) 
        {
            for (npix = linePosition.currPixel + 3; npix < top; ++npix) 
            {
                PixelCoordinates p = (PixelCoordinates) linePosition.pixels.elementAt(npix);

                x = p.x + kx;
                y = p.y + ky;

                // if ( debug ) System.out.println(x + " " + y + ":" + xmin + " " + ymin + " " + xmax + " " + ymax + ":" + npix);                
                
                if (x >= xmin && x <= xmax && y >= ymin && y <= ymax) 
                {
                    Letter letter = makeLetter(c, x, y, orient, color, width, height);

                    if (letter.llx >= 0) 
                    {
                    	linePosition.currPixel = npix;
                        return letter;
                    }
                }
            }
        }

        Letter l = new Letter();
        l.llx = -1;
        return l;
    }

    private static Letter makeLetter(int ic, int ix, int iy, int io,
    		Paint color, int width, int height)

    {
        int w, h;
        int cwidth, cheight;
        Letter l = new Letter();

        if (PersistentSettings.currentTextFont == FontSmall) {
            cwidth = smallFontWidth;
            cheight = smallFontHeight;
        } else if (PersistentSettings.currentTextFont == FontLarge) {
            cwidth = largeFontWidth;
            cheight = largeFontHeight;
        } else {
            cwidth = boldFontWidth;
            cheight = boldFontHeight;
        }

        if (!checkLocation(ix, iy, io, width, height)) {
            l.llx = -1;
            return l;
        }

        l.orientation = io;

        l.llx = ix;
        l.lly = iy;

        l.color = color;

        l.k = getLetterIndex(ic);
        l.width = getWidthFor(l.k);

        if (l.orientation == 0) {
            l.xs = l.llx - (cwidth >> 1);
            l.ys = l.lly - (cheight + 2);
            l.xe = l.xs + l.width;
            l.ye = l.ys + cheight - 1;
        } else if (l.orientation == 1) {
            l.xs = l.llx - (cheight + 2);
            l.ys = l.lly - (cwidth >> 1) - 1;
            l.xe = l.xs + cheight - 1;
            l.ye = l.ys + l.width;
        } else if (l.orientation == -1) {
            l.xs = l.llx + 2;
            l.ys = l.lly - (cwidth >> 1);
            l.xe = l.xs + cheight - 1;
            l.ye = l.ys + l.width;
        }

        //////////////////////////////////////////////////

        if (l.xs < 0 || l.ys < 0) {
            l.llx = -1;
            return l;
        }

        w = 5;
        h = 7;

        if (l.orientation != 0) {
            w = 7;
            h = 5;
        }

        if (l.xs > (width - 1) - w || l.ys > (height - 1) - h) {
            l.llx = -1;
            return l;
        }

        return l;
    }

    private static boolean checkLocation(int x, int y, int orientation,
            int width, int height)

    {
        int cw, ch;

        if (PersistentSettings.currentTextFont == FontSmall) {
            cw = (smallFontWidth >> 1);
            ch = smallFontHeight + 2;
        } else if (PersistentSettings.currentTextFont == FontLarge) {
            cw = (largeFontWidth >> 1);
            ch = largeFontHeight + 2;
        } else {
            cw = (boldFontWidth >> 1);
            ch = boldFontHeight + 2;
        }

        if (orientation == 0) {
            if (x > cw && x < (width - 1) - cw)
                    if (y > ch && y <= (height - 1)) return true;
        } else if (orientation == 1) {
            if (x <= (width - 1) && x > ch)
                    if (y > cw && y < (height - 1) - cw) return true;
        } else if (orientation == -1) {
            if (x > 0 && x < (width - 1) - ch)
                    if (y > cw && y < (height - 1) - cw) return true;
        }

        return false;
    }

    private static int getWidthFor(int k)

    {
		try
		{
			if (PersistentSettings.currentTextFont == FontSmall) return SmallFont.smallFontWidths[k];
			else if (PersistentSettings.currentTextFont == FontLarge) return LargeFont.largeFontWidths[k];
			else if (PersistentSettings.currentTextFont == FontBold)
				return BoldFont.boldFontWidths[k];
		}
		
		catch (Exception e)
			
		{
			Engine.out("********** width invalid = " + k);	
		}

		return 0;
    }
    
    private static String capitalization(String name)
    
    {
        // capitalize only first letter in each word

        if (name.startsWith("US ") || name.startsWith("US-")) 
        	;
        else 
        {
            byte[] b = name.getBytes();

            for ( int i = 0; i < b.length; ++i) 
            {
                int c = 1;

                if (i == 0) c = 0;
                else if (b[i - 1] == ' ' || b[i - 1] == '|') c = 0;

                if (c != 0) if (b[i] < 128) 
                {
                    if (b[i] >= 'A' && b[i] <= 'Z') b[i] |= 0x20;
                } 
                else 
                {
                    // similar for European characters

                        if (b[i] >= 192 && b[i] <= 223) b[i] |= 0x20;
                    }
            }

            name = new String(b);
        }

        return name;
    }    
}

class LinePosition

{

    int x, y, orientation, currPixel;
    Vector pixels;

    @Override
    public String toString()
    
    {
    	return "[" + x + " " + y + " " + orientation + " " + currPixel + "]";
    }
    
    LinePosition(Vector p) throws MapopolisException

    {
        Vector points = p;

        // generate pixels

        pixels = new Vector();

        for (int i = 0; i < points.size() - 1; ++i) 
        {
            PixelCoordinates p0 = (PixelCoordinates) points.elementAt(i);
            PixelCoordinates p1 = (PixelCoordinates) points.elementAt(i + 1);

            addPixels(p0.x, p0.y, p1.x, p1.y);
        }
    }

    void start(boolean canCallback, int n, int t) throws MapopolisException

    {
        currPixel = (n * (pixels.size() - 15)) / (t * 2);

        if (currPixel <= 0) 
        	currPixel = 1;

        if (currPixel >= pixels.size() - 1) 
        {
            orientation = LabelDraw.None;
            return;
        }

        x = ((PixelCoordinates) pixels.elementAt(currPixel)).x;
        y = ((PixelCoordinates) pixels.elementAt(currPixel)).y;

        int x0 = ((PixelCoordinates) pixels.elementAt(currPixel - 1)).x;
        int y0 = ((PixelCoordinates) pixels.elementAt(currPixel - 1)).y;
        int x1 = ((PixelCoordinates) pixels.elementAt(currPixel + 1)).x;
        int y1 = ((PixelCoordinates) pixels.elementAt(currPixel + 1)).y;

        if (Math.abs(y1 - y0) > Math.abs(x1 - x0)) 
        {
            if (y1 > y0) 
            	orientation = LabelDraw.Down;
            else 
            {
                if (canCallback) 
                {
                	reversePixels();
                    start(false, n, t);
                } 
                else 
                	orientation = LabelDraw.Up;
            }
        } 
        else 
        {
            if (x0 > x1) 
            {
                if (canCallback) 
                {
                	reversePixels();
                    start(false, n, t);
                } 
                else 
                	orientation = LabelDraw.None;
            }

            orientation = LabelDraw.Across;
        }
    }

    private void reversePixels()

    {
        Vector p = new Vector(pixels.size(), 1);

        for (int i = pixels.size() - 1; i >= 0; --i)
            p.addElement(pixels.elementAt(i));

        pixels = p;
    }

    private void addPixels(int x0, int y0, int x1, int y1)

    {
        int pixj = 0;
        int pixy = y0;
        int pixx = x0;

        int adx, ady, xstep, ystep, pixi, dx, dy;

        dy = y1 - y0;
        dx = x1 - x0;

        if (dx < 0) {
            adx = -dx;
            xstep = -1;
        } else {
            adx = dx;
            xstep = 1;
        }

        if (dy < 0) {
            ady = -dy;
            ystep = -1;
        } else {
            ady = dy;
            ystep = 1;
        }

        if (adx > ady) 
        {
            for (pixi = 0; pixi <= adx; ++pixi) 
            {
                pixels.addElement(new PixelCoordinates(pixx, pixy));

                if ((pixj += ady) >= adx) {
                    pixy += ystep;
                    pixj -= adx;
                }

                pixx += xstep;
            }
        } else 
        {
            for (pixi = 0; pixi <= ady; ++pixi) 
            {
                pixels.addElement(new PixelCoordinates(pixx, pixy));

                if ((pixj += adx) >= ady) 
                {
                    pixx += xstep;
                    pixj -= ady;
                }

                pixy += ystep;
            }
        }
    }
}