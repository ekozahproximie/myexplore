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
 *      com.trimble.agmantra
 *
 * File name:
 *		PhotoActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Jun 26, 2012 8:58:44 PM
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

package com.trimble.reporter;

import com.trimble.agent.R;
import com.trimble.reporter.app.TCCApplication;
import com.trimble.reporter.img.ThumbnailController;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * @author sprabhu
 */

public class PhotoActivity extends BaseActivity {

    private static final String URI = "com.trimble.mobile.android.media.URI";

    private long lJobId = 100;

    private ImageView phototaken = null;

    public static final String PHOTO_URI = "photouri";

    public static final String FROM_TEMPLATE = "fromtemplate";

    private boolean isFromTemplate = false;

    private String stPhotoURI = null;

    private String stOldPhotoURI = null;

   
    public static final String TITLE = "title";

    public static final String IMAGE_URI = "img_uri";

    private static final String TAG = "photoactivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.phototaken);
        phototaken = (ImageView)findViewById(R.id.photoview);
        Bundle bundle = getIntent().getExtras();

        boolean isDelPreView = false;
        if (bundle != null) {
            String stTitle = bundle.getString(TITLE);
            if (stTitle != null) {
                TextView tvTitle = (TextView)findViewById(R.id.titletext);
                tvTitle.setText(stTitle);
            }
            isFromTemplate = bundle.getBoolean(FROM_TEMPLATE);

            if (isFromTemplate) {
                ((Button)findViewById(R.id.photodone)).setVisibility(View.VISIBLE);
                // ((Button)findViewById(R.id.upload)).setVisibility(View.GONE);
                stPhotoURI = bundle.getString(IMAGE_URI);
                ((Button)findViewById(R.id.deletephoto)).setVisibility(View.GONE);
                if (stPhotoURI != null && stPhotoURI.length() != 0) {
                    stOldPhotoURI = stPhotoURI;
                    photoUri = Uri.parse(stPhotoURI);
                    photoTaken(stPhotoURI, 0);
                    ((Button)findViewById(R.id.deletephoto)).setVisibility(View.VISIBLE);
                    isDelPreView = true;
                }

            }
            if (!isDelPreView) {
                takePhoto();
            } else {
                photoUri = Uri.parse(stPhotoURI);
            }

        }

    }

    private Uri photoUri;

    public void takePhoto() {

        TCCApplication app = (TCCApplication)getApplication();
        photoUri = null;
        try {
            // photoUri =
            // TrimbleBaseApplication.createMediaUri(Media.TYPE_PHOTO,
            // imageName, null,
            // System.currentTimeMillis(), app);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setsFilePath(getTempFileString());
        photoUri = CurrentUri;
        FillPhotoList();

        if (photoUri != null)
            app.takePhoto(this, ACTIVITY_REQUEST_CODE_PHOTO, photoUri);
        else
            Toast.makeText(this, getString(R.string.error_no_sd_card), Toast.LENGTH_SHORT).show();
    }

    private void photoTaken(String uriPath, int size) {
      
        try {
            // phototaken.setImageURI(Uri.parse(uriPath));
            setLimitedSizePic();
            // setFullWindowPic();
        } catch (OutOfMemoryError e) {

        }
    }

    private void setLimitedSizePic() {
        String stFileName = photoUri.getPath();
        FileInputStream in = null;
        try {
            in = new FileInputStream(new File(stFileName));

            byte[] data = new byte[in.available()];
            in.read(data, 0, data.length);

            Bitmap bitmap = ThumbnailController.makeBitmap(data, 1024 * 150);

            int degree = ThumbnailController.getExifOrientation(stFileName);
            /* Decode the JPEG file into a Bitmap */

            bitmap = ThumbnailController.rotate(bitmap, degree);
            /* Associate the Bitmap to the ImageView */
            phototaken.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(URI, photoUri);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle state) {
        photoUri = (Uri)state.get(URI);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_REQUEST_CODE_PHOTO:
                if (resultCode == RESULT_OK && photoUri != null) {
                    TCCApplication app = (TCCApplication)getApplication();
                    if (data != null) { // for hero and eris perhaps etc.
                        if (data.getData() != null) { // Looks like HTC
                                                      // incredible needs
                                                      // this check as they are
                                                      // returning
                                                      // null
                            photoUri = data.getData();
                        }
                    }
                    int size = app.getSizeFromUri(photoUri);

                    Log.i(TAG, "onActivityResult photoUri" + photoUri + "Size=" + size);
                    deleteFileFromGallary();
                    Log.i(this.getClass().getName(), "photoUri :" + photoUri.getPath());
                    if (isFromTemplate) {
                        if (stOldPhotoURI != null) {
                            app.cancelPhotoCapture(Uri.parse(stOldPhotoURI));
                        }

                        sendResult();
                    } else {
                        photoTaken(photoUri.toString(), size);
                    }

                    // photoUri = null;
                } else if (resultCode == RESULT_CANCELED && photoUri != null) {
                    deletePhoto();
                    photoUri = null;
                    setResult(RESULT_CANCELED);
                    finish();
                } else {
                   // findViewById(R.id.upload).setVisibility(View.GONE);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onClick(View clikedView) {
        switch (clikedView.getId()) {
            case R.id.home: {
                finish();
                break;
            }
            case R.id.takeanother: {
                // deletePhoto();
                takePhoto();
                break;
            }
            /*
             * case R.id.upload: { Log.i(LOG, "Uploading image file started.." +
             * photoUri); if (photoUri != null) { uploadPhoto(true); } break; }
             */
            case R.id.photodone: {
                sendResult();
                break;
            }
            case R.id.deletephoto: {

                deletePhoto();
                photoUri = null;
                sendResult();
                break;

            }
            default:
                break;
        }
    }

    private void sendResult() {
        Intent intent = new Intent();
        String stPhotoNoURI = null;
        if (photoUri != null) {
            stPhotoNoURI = photoUri.toString();
        }
        intent.putExtra(PHOTO_URI, stPhotoNoURI);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void deletePhoto() {
        if (photoUri != null) {
            ((TCCApplication)getApplication()).cancelPhotoCapture(photoUri);
        }
    }

    public static float convertDp_To_Pixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static float convertPixels_To_Dp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static Bitmap getThumbnail(Uri uri, Context context, final int THUMBNAIL_SIZE)
            throws FileNotFoundException, IOException {
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;
        // optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // optional
        Bitmap tempBitmap = BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        // tempBitmap.recycle();
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
                : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;

        // optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // optional
        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    public static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if (k == 0)
            return 1;
        else
            return k;
    }

    /** Variable to instance of data base. */

    public void uploadPhoto(boolean isCheckDeviceAssocation) {

        String stSdcardState = Environment.getExternalStorageState();

        if (stSdcardState.equals(Environment.MEDIA_SHARED)) {

            Toast.makeText(this, getString(R.string.error_msg_sdcard_write), Toast.LENGTH_LONG)
                    .show();
            finish();
            return;

        } else if (stSdcardState.equals(Environment.MEDIA_REMOVED)) {
            Toast.makeText(this, getString(R.string.error_msg_sdcard), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        finish();
    }

    public ArrayList<String> GalleryList = new ArrayList<String>();

    private void FillPhotoList() {
        // initialize the list!
        GalleryList.clear();
        String[] projection = {
            MediaStore.Images.ImageColumns.DISPLAY_NAME
        };
        // intialize the Uri and the Cursor, and the current expected size.
        Cursor c = null;
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // Query the Uri to get the data path. Only if the Uri is valid.
        if (u != null) {
            c = managedQuery(u, projection, null, null, null);
        }
        // If we found the cursor and found a record in it (we also have the
        // id).
        if ((c != null) && (c.moveToFirst())) {
            do {
                // Loop each and add to the list.
                GalleryList.add(c.getString(0));
            } while (c.moveToNext());
        }
    }

    private String sFilePath = "";

    private File CurrentFile = null;

    private Uri CurrentUri = null;

    private String getTempFileString() {
        // Only one time will we grab this location.
        final File path = new File(getFlagStoreDir() + String.valueOf(lJobId) + File.separator);
        // // If this does not exist, we can create it here.
        if (!path.exists()) {
            path.mkdirs();
        }
        //
        return new File(path, "description"+ ".jpeg")
                .getPath();
    }

    /**
     * @return
     */
    public static String getFlagStoreDir() {

        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "irctcc" + File.separator;
    }

    public void setsFilePath(String value) {
        // We just updated this value. Set the property first.
        sFilePath = value;
        // // initialize these two
        CurrentFile = null;
        CurrentUri = null;
        // // If we have something real, setup the file and the Uri.
        if (!sFilePath.equalsIgnoreCase("")) {
            CurrentFile = new File(sFilePath);
            CurrentUri = Uri.fromFile(CurrentFile);
        }
    }

    public void deleteFileFromGallary() {
        // This is ##### ridiculous. Some versions of Android save // to the
        // MediaStore as well. Not sure why! We don't know what // name Android
        // will give either, so we get to search for this // manually and remove
        // it.
        String[] projection = {
                MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA, BaseColumns._ID,
        };
        // intialize the Uri and the Cursor, and the current expected size.

        Cursor c = null;
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        if (CurrentFile != null) {

            // Query the Uri to get the data path. Only if the Uri is valid,
            // and we had a valid size to be searching for.
            if ((u != null) && (CurrentFile.length() > 0)) {
                c = managedQuery(u, projection, null, null, null);
            }
            // If we found the cursor and found a record in it (we also have the
            // size).
            if ((c != null) && (c.moveToFirst())) {
                do {
                    // Check each area in the gallary we built before.

                    boolean bFound = false;
                    for (String sGallery : GalleryList) {
                        if (sGallery.equalsIgnoreCase(c.getString(1))) {
                            bFound = true;
                            break;
                        }
                    }

                    // // To here we looped the full gallery.
                    if (!bFound) {
                        // This is the NEW image. If the size is bigger, copy
                        // it.

                        // Then delete it!
                        File f = new File(c.getString(2));
                        // Ensure it's there, check size, and delete!
                        if ((f.exists()) && (CurrentFile.length() < c.getLong(0))
                                && (CurrentFile.delete())) {
                            // Finally we can stop the copy.

                            try {
                                CurrentFile.createNewFile();
                                FileChannel source = null;
                                FileChannel destination = null;
                                try {
                                    source = new FileInputStream(f).getChannel();
                                    destination = new FileOutputStream(CurrentFile).getChannel();
                                    destination.transferFrom(source, 0, source.size());
                                } finally {
                                    if (source != null) {
                                        source.close();
                                    }
                                    if (destination != null) {
                                        destination.close();
                                    }
                                }
                            } catch (IOException e) {
                                // Could not copy the file over.
                                // app.CallToast(PhotosActivity.this,
                                // getString(R.string.ErrorOccured), 0);
                            }

                        }
                        ContentResolver cr = getContentResolver();
                        cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID
                                + "=" + c.getString(3), null);
                        break;
                    }
                } while (c.moveToNext());
            }

        }
    }

    public static boolean checkBitmapFitsInMemory(long bmpwidth, long bmpheight, int bmpdensity) {
        long reqsize = bmpwidth * bmpheight * bmpdensity;
        long allocNativeHeap = android.os.Debug.getNativeHeapAllocatedSize();
        final long heapPad = (long)Math
                .max(4 * 1024 * 1024, Runtime.getRuntime().maxMemory() * 0.1);
        if ((reqsize + allocNativeHeap + heapPad) >= Runtime.getRuntime().maxMemory()) {
            return false;
        }
        return true;
    }

}
