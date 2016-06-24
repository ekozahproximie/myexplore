
package com.mapopolis.viewer.draw;



import java.util.Vector;

import com.mapopolis.viewer.utils.PixelCoordinates;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;

public class GraphicsBuffer

{ 
    int [] bytes;
    private Bitmap image;
    public int width, height;

    Paint color;
    int colorInt = 0xff000000;

    public GraphicsBuffer(int width, int height)

    {
        this.width = width;
        this.height = height;

        //ColorModel colorModel = ColorModel.getRGBdefault();
        bytes = new int[width * height];

        //image = Toolkit.getDefaultToolkit().createImage(new
        // MemoryImageSource(width, height, colorModel, bytes, 0, width));
    }

    public void paintBackground()

    {
        for (int i = 0; i < bytes.length; ++i)
            bytes[i] = 0xffffff88;
    }

    public void setColor(Paint c)

    {
        //if ( !MapView.drawMap ) return;

        //if ( c == PersistentSettings.WaterColor ) color = ColorBlue;
        //else if ( c == PersistentSettings.GreenColor ) color = ColorGreen;
        //else if ( c == PersistentSettings.FacilityColor ) color =
        // ColorDarkRedBlue;
        //else if ( c == PersistentSettings.StreetLabelColor ) color =
        // ColorBlack;
        //else
        //	color = ColorBlue;

        color = c;
        colorInt = color.getColor();//(color.getAlpha()<<24) |
                                  // (color.getRed()<<16) |
                                  // (color.getGreen()<<8) | (color.getBlue());
    }

    public void drawPoint(int x, int y)

    {
        bytes[y * width + x] =  colorInt;
    }

    public void fill()

    {
        int c = (int) (Math.random() * 1000000000);
        for (int i = 0; i < bytes.length; ++i)
            bytes[i] =  c;
    }

    public void drawLine(int x0, int y0, int x1, int y1)

    {
        //if ( !MapView.drawMap ) return;

        int dy, dx;
        int i, j, x, y, adx, ady, ix, iy;

        dy = y1 - y0;
        dx = x1 - x0;

        if (dx == 0 && dy == 0) {
            if (x0 >= 0 && x0 <= width - 1 && y0 >= 0 && y0 <= height - 1) {
                bytes[y0 * width + x0] =  colorInt;
            }
            return;
        }

        adx = Math.abs(dx);
        ady = Math.abs(dy);

        j = 0;
        y = y0;
        x = x0;

        if (adx > ady) {
            if (dx < 0) {
                if (dy < 0) {
                    // dx < 0 dy < 0

                    ix = -1;
                    iy = -1;
                } else {
                    // dx < 0 dy > 0

                    ix = -1;
                    iy = 1;
                }
            } else {
                if (dy < 0) {
                    // dx > 0 dy < 0

                    ix = 1;
                    iy = -1;

                } else {
                    // dx > 0 dy > 0

                    ix = 1;
                    iy = 1;
                }
            }

            for (i = 0; i <= adx; ++i) {
                if (x >= 0 && x <= width - 1 && y >= 0 && y <= height - 1) {
                    bytes[y * width + x] =  colorInt;
                }

                j += ady;
                if (j >= adx) {
                    y += iy;
                    j -= adx;
                }
                x += ix;
            }
        } else {
            if (dy < 0) {
                if (dx < 0) {
                    // dx < 0 dy < 0

                    ix = -1;
                    iy = -1;
                } else {
                    // dx > 0 dy < 0

                    ix = 1;
                    iy = -1;
                }
            } else {
                if (dx < 0) {
                    // dx < 0 dy > 0

                    ix = -1;
                    iy = 1;
                } else {
                    // dx > 0 dy > 0

                    ix = 1;
                    iy = 1;
                }
            }

            for (i = 0; i <= ady; ++i) {
                if (x >= 0 && x <= width - 1 && y >= 0 && y <= height - 1) {
                    bytes[y * width + x] =  colorInt;
                }

                j += adx;
                if (j >= ady) {
                    x += ix;
                    j -= ady;
                }
                y += iy;
            }
        }
    }

    public Bitmap getImage()

    {
        //MemoryImageSource ms = new MemoryImageSource(width, height, ColorModel.getRGBdefault(), bytes, 0, width);
        //image = Toolkit.getDefaultToolkit().createImage(ms);
        Bitmap bitmapimage = Bitmap.createBitmap(bytes, 0, width, width, height,Bitmap.Config.ARGB_8888);
        image=bitmapimage;
        return image;
    }

    public void fillPolygon(Vector<PixelCoordinates> pts)

    {
    /*
     * 
     * int[] x = new int[pts.size()]; int[] y = new int[pts.size()];
     * 
     * for (int i = 0; i < pts.size(); ++i) { PixelCoordinates p =
     * (PixelCoordinates) pts.elementAt(i); x[i] = p.x; y[i] = p.y; }
     * 
     * graphics.fillPolygon(x, y, pts.size());
     *  
     */
    }

    public Paint getBG() 
	
	{
        return new Paint(Color.YELLOW);
    }
}