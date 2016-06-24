/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trimble.reporter.img;


import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A controller shows thumbnail picture on a button. The thumbnail picture
 * corresponds to a URI of the original picture/video. The thumbnail bitmap
 * and the URI can be saved to a file (and later loaded from it).
 */
public class ThumbnailController {

    @SuppressWarnings("unused")
    private static final String TAG = "ThumbnailController";
    private final ContentResolver mContentResolver;
    private Uri mUri;
    private Bitmap mThumb;
    private ImageView mButton;
    private Drawable[] mThumbs;
    private TransitionDrawable mThumbTransition;
    private boolean mShouldAnimateThumb;
    private final Resources mResources;

    // The "frame" is a drawable we want to put on top of the thumbnail.
    public ThumbnailController(Resources resources,
            ImageView button, ContentResolver contentResolver) {
        mResources = resources;
        mButton = button;
        mContentResolver = contentResolver;
    }

    public void setData(Uri uri, Bitmap original) {
        // Make sure uri and original are consistently both null or both
        // non-null.
        if (uri == null || original == null) {
            uri = null;
            original = null;
        }
        mUri = uri;
        updateThumb(original);
    }

    public Uri getUri() {
        return mUri;
    }

    private static final int BUFSIZE = 4096;

    // Stores the data from the specified file.
    // Returns true for success.
    public boolean storeData(String filePath) {
        if (mUri == null) {
            return false;
        }

        FileOutputStream f = null;
        BufferedOutputStream b = null;
        DataOutputStream d = null;
        try {
            f = new FileOutputStream(filePath);
            b = new BufferedOutputStream(f, BUFSIZE);
            d = new DataOutputStream(b);
            d.writeUTF(mUri.toString());
            mThumb.compress(Bitmap.CompressFormat.PNG, 100, d);
            d.close();
        } catch (IOException e) {
            return false;
        } finally {
            closeSilently(f);
            closeSilently(b);
            closeSilently(d);
        }
        return true;
    }
    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // ignore
            }
        }
    }
    // Loads the data from the specified file.
    // Returns true for success.
    public boolean loadData(String filePath) {
        FileInputStream f = null;
        BufferedInputStream b = null;
        DataInputStream d = null;
        try {
            f = new FileInputStream(filePath);
            b = new BufferedInputStream(f, BUFSIZE);
            d = new DataInputStream(b);
            Uri uri = Uri.parse(d.readUTF());
            Bitmap thumb = BitmapFactory.decodeStream(d);
            setData(uri, thumb);
            d.close();
        } catch (IOException e) {
            return false;
        } finally {
            closeSilently(f);
            closeSilently(b);
            closeSilently(d);
        }
        return true;
    }

    public void updateDisplayIfNeeded() {
        if (mUri == null) {
            mButton.setImageDrawable(null);
            return;
        }

        if (mShouldAnimateThumb) {
            mThumbTransition.startTransition(500);
            mShouldAnimateThumb = false;
        }
    }

    private void updateThumb(Bitmap original) {
        if (original == null) {
            mThumb = null;
            mThumbs = null;
            return;
        }

        LayoutParams param = mButton.getLayoutParams();
        final int miniThumbWidth = param.width
                - mButton.getPaddingLeft() - mButton.getPaddingRight();
        final int miniThumbHeight = param.height
                - mButton.getPaddingTop() - mButton.getPaddingBottom();
        mThumb = ThumbnailUtils.extractThumbnail(
                original, miniThumbWidth, miniThumbHeight);
        
        Drawable drawable;
        if (mThumbs == null) {
            mThumbs = new Drawable[2];
            mThumbs[1] = new BitmapDrawable(mResources, mThumb);
            drawable = mThumbs[1];
            mShouldAnimateThumb = false;
        } else {
            mThumbs[0] = mThumbs[1];
            mThumbs[1] = new BitmapDrawable(mResources, mThumb);
            mThumbTransition = new TransitionDrawable(mThumbs);
            drawable = mThumbTransition;
            mShouldAnimateThumb = true;
        }
        mButton.setImageDrawable(null);
        mButton.setImageDrawable(drawable);
    }

    public boolean isUriValid() {
        if (mUri == null) {
            return false;
        }
        try {
            ParcelFileDescriptor pfd =
                    mContentResolver.openFileDescriptor(mUri, "r");
            if (pfd == null) {
                Log.e(TAG, "Fail to open URI.");
                return false;
            }
            pfd.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
    /**
     * @param button the button to set
     */
    public void setButton(ImageView button) {
        mButton = button;
    }
    
    // Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.
    public static Bitmap rotate(Bitmap b, int degrees) {
        return rotateAndMirror(b, degrees, false);
    }

    // Rotates and/or mirrors the bitmap. If a new bitmap is created, the
    // original bitmap is recycled.
    public static Bitmap rotateAndMirror(Bitmap b, int degrees, boolean mirror) {
        if ((degrees != 0 || mirror) && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees,
                    (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            if (mirror) {
                m.postScale(-1, 1);
                degrees = (degrees + 360) % 360;
                if (degrees == 0 || degrees == 180) {
                    m.postTranslate((float) b.getWidth(), 0);
                } else if (degrees == 90 || degrees == 270) {
                    m.postTranslate((float) b.getHeight(), 0);
                } else {
                    throw new IllegalArgumentException("Invalid degrees=" + degrees);
                }
            }

            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }
    
    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            Log.e(TAG, "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
    public static Bitmap makeBitmap(byte[] jpegData, int maxNumOfPixels) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length,
                    options);
            if (options.mCancel || options.outWidth == -1
                    || options.outHeight == -1) {
                return null;
            }
            options.inSampleSize = computeSampleSize(
                    options, -1, maxNumOfPixels);
            options.inJustDecodeBounds = false;

            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length,
                    options);
        } catch (OutOfMemoryError ex) {
            Log.e("Test", "Got oom exception ", ex);
            return null;
        }
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    public void saveLatLon(String stFilePath, double latitude,double longitude){
        //double latitude = 12.989985;
        //double longitude = 80.249172;
        ExifInterface exif;
        try {
            exif = new ExifInterface(stFilePath);
            int num1Lat = (int)Math.floor(latitude);
            int num2Lat = (int)Math.floor((latitude - num1Lat) * 60);
            double num3Lat = (latitude - ((double)num1Lat+((double)num2Lat/60))) * 3600000;

            int num1Lon = (int)Math.floor(longitude);
            int num2Lon = (int)Math.floor((longitude - num1Lon) * 60);
            double num3Lon = (longitude - ((double)num1Lon+((double)num2Lon/60))) * 3600000;

            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, num1Lat+"/1,"+num2Lat+"/1,"+num3Lat+"/1000");
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, num1Lon+"/1,"+num2Lon+"/1,"+num3Lon+"/1000");


            if (latitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "N"); 
            } else {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, "S");
            }

            if (longitude > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "E");    
            } else {
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, "W");
            }

            exif.saveAttributes();

        } catch (IOException e) {
            Log.e("PictureActivity", e.getLocalizedMessage());
        }   
    }
}
