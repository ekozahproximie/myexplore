/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.reporter
 *
 * File name:
 *		ReporterActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 14, 2012 7:15:08 PM
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


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.trimble.reporter.img.ThumbnailController;
import com.trimble.reporter.looper.DataSend;
import com.trimble.reporter.looper.LooperThread;
import com.trimble.reporter.map.GMapActivity;
import com.trimble.reporter.trackdb.TrackDBManager;
import com.trimble.reporter.utils.Utils;

/**
 * @author sprabhu
 */

public class ReporterActivity extends BaseActivity implements LocationListener {

    public static final String PICKLIST_ITEM = "catagory_item";

    public static final String PICKLIST_TITLE = "picklist_title";

    public static final String PICKLIST_ARR_ITEMS = "picklist_item";

    public static final String PICKLIST_TYPE = "picklist_type";

    public static final String SELECTION_INDEX = "select_index";

    private static final int PICKLIST_CATEGORY_TYPE = 0;

    private static final int D_CATEGORY_SELECTE_INDEX = 0;

    private static final String D_LATITUDE = "12.989985";

    private static final String D_LONGITUDE = "80.249172";

    public static final String LATITUDE = "lat";

    public static final String LONGITUDE = "lon";

    public String[] lstCategoryType = null;

    private TextView edLatitude = null;

    private TextView edLongtitude = null;

    private TextView txt_longitude = null;

    private TextView txt_latitude = null;

    private int iIndex = 0;

    private LocationManager mLocationManager = null;

    private String stPhotoImgURI = null;

    private ThumbnailController mThumbController;
    
    private static final String ID="id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lstCategoryType = getResources().getStringArray(R.array.category);
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.report);
        edLatitude = (TextView)findViewById(R.id.ed_lat);
        edLongtitude = (TextView)findViewById(R.id.ed_lon);
        txt_longitude = (TextView)findViewById(R.id.txt_longitude);
        txt_latitude = (TextView)findViewById(R.id.txt_latitude);
        updateUI();
    }
    
    public void onClick(View onClick) {
        switch (onClick.getId()) {
            case R.id.rl_lst_severity:
            case R.id.lbl_severity:
            case R.id.txt_severity_val:
                startCategoryList();
                break;
            case R.id.cancel:{
                Utils.deleteJobFileDir(new File(PhotoActivity.getFlagStoreDir()));
                finish();
            }
            break;
            case R.id.send:{
                sendData();
               // finish();
            }
                break;
            case R.id.img_template:{
                Intent intent = new Intent(this, PhotoActivity.class);
                intent.putExtra(PhotoActivity.TITLE, getString(R.string.incidentcapture));
                intent.putExtra(PhotoActivity.FROM_TEMPLATE, true);
                intent.putExtra(PhotoActivity.IMAGE_URI, stPhotoImgURI);
                startActivityForResult(intent, PHOTO_TAKE);
                break;
            }
            case R.id.locmap:{
                Intent intent = new Intent(this, GMapActivity.class);
                
                double dLat=0.0;
                double dLon=0.0;
                
                if(edLatitude.getText().toString().length() != 0){
                    dLat=Double.parseDouble(edLatitude.getText().toString());
                }
                if(edLongtitude.getText().toString().length() != 0){
                    dLon=Double.parseDouble(edLongtitude.getText().toString());
                }
                
                intent.putExtra(GMapActivity.GEO_LOCATION, dLat+" "+dLon);
                startActivityForResult(intent, LAT_LON_TAKE);
                break;
            }

            default:
                break;
        }
    }

    private void startCategoryList() {
        // start template list activity
        Intent intent = new Intent(this, CatagoryListActivity.class);
        String stPassData[] = new String[lstCategoryType.length];

        int i = 0;
        int iIndex = 0;
        TextView tvFlagType = (TextView)findViewById(R.id.txt_severity_val);
        String stSelection = tvFlagType.getText().toString().trim();
        for (String stData : lstCategoryType) {
            stData = stData.trim();

            if (stSelection != null && stData != null && stSelection.equals(stData)) {
                iIndex = i;
            }
            stPassData[i++] = stData;

        }

        intent.putExtra(PICKLIST_TYPE, PICKLIST_CATEGORY_TYPE);
        intent.putExtra(PICKLIST_TITLE, getString(R.string.categorytype));
        intent.putExtra(PICKLIST_ARR_ITEMS, stPassData);
        intent.putExtra(SELECTION_INDEX, iIndex);

        startActivityForResult(intent, CATEGORY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case CATEGORY_PICK: {

                    Bundle extra = data.getExtras();
                    if (extra != null) {
                        String sItemChosen = extra.getString(PICKLIST_ITEM);

                        switch (extra.getInt(PICKLIST_TYPE)) {
                            case PICKLIST_CATEGORY_TYPE: {
                                iIndex = extra.getInt(SELECTION_INDEX);
                                ((TextView)findViewById(R.id.txt_severity_val))
                                        .setText("traffic");

                            }
                                break;
                        }

                    }
                    break;
                }
                case PHOTO_TAKE: {
                    if (data != null) { // for hero and eris perhaps etc.
                        // Looks like HTC
                        // incredible needs
                        // this check as they are
                        // returning
                        // null
                        Bundle bundle = data.getExtras();
                        stPhotoImgURI = bundle.getString(PhotoActivity.PHOTO_URI);

                        // update image element value and set image thumbnail to
                        // UI
                        setPhotoImage();

                    }
                    // photoUri = null;

                    break;
                }
                case LAT_LON_TAKE:{
                    Bundle bundle = data.getExtras();
                    String stLat=bundle.getString(LATITUDE);
                    String stLon=bundle.getString(LONGITUDE);
                    setLatLon(stLat, stLon);
                    break;
                }
                    
                case HIT:{
                    final DataSend dataSend = new DataSend();
                    dataSend.stURL=LooperThread.HIT+"imei="+Utils.getDeviceUUID(this);
                    dataSend.isPost=false;
                    storeToDB();
                    Thread thread = new Thread() {

                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000*30);
                            } catch (InterruptedException e) {
                              
                                e.printStackTrace();
                            }
                            LooperThread.getInstance().addData(dataSend);
                        }
                    };
                    thread.start();
                    finish();
                    break;
                }

                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }else if(resultCode == RESULT_CANCELED){
            if (requestCode == HIT) {
                final DataSend dataSend = new DataSend();
                dataSend.stURL=LooperThread.HIT+"imei="+Utils.getDeviceUUID(this)+"&";
                dataSend.isPost=false;
                storeToDB();
                Thread thread = new Thread() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000*30);
                        } catch (InterruptedException e) {
                          
                            e.printStackTrace();
                        }
                        LooperThread.getInstance().addData(dataSend);
                    }
                };
                thread.start();
                finish();
            }
        }
    }

    private void setPhotoImage() {

        if (stPhotoImgURI != null) {
            InputStream input = null;

            try {
                Uri uri = Uri.parse(stPhotoImgURI);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;

                input = getContentResolver().openInputStream(uri);

                int degree = ThumbnailController.getExifOrientation(uri.getPath());
                Bitmap lastPictureThumb = BitmapFactory.decodeStream(input, null, options);

                lastPictureThumb = ThumbnailController.rotate(lastPictureThumb, degree);
                mThumbController.setButton(((ImageView)findViewById(R.id.img_template)));
                mThumbController.setData(uri, lastPictureThumb);
                // mThumbController.loadData(stPhotoImgURI);
                mThumbController.updateDisplayIfNeeded();
                lastPictureThumb = null;

                uri = null;

            } catch (Exception e) {
                setDefalutImage();
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                setDefalutImage();
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        } else {
            setDefalutImage();

        }
    }

    private void setDefalutImage() {
        if (((ImageView)findViewById(R.id.img_template)) != null) {
            ((ImageView)findViewById(R.id.img_template)).setImageDrawable(null);
            ((ImageView)findViewById(R.id.img_template))
                    .setBackgroundResource(R.drawable.img_default_flag_photo);
        }
    }
  
    @Override
    protected void onPause() {
        mLocationManager.removeUpdates(this);
        super.onPause();
        saveUI();
    }

    @Override
    protected void onResume() {

        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
    }
    private static final String TAG="ReporterActivity";
    private String stCategory=null;
    private double dLat=0.0;
    private double dLon=0.0;
    private String stComments=null;
    private void sendData(){
       
        
        if(edLatitude.getText().toString().length() != 0){
            dLat=Double.parseDouble(edLatitude.getText().toString());
        }
        if(edLongtitude.getText().toString().length() != 0){
            dLon=Double.parseDouble(edLongtitude.getText().toString());
        }
        Uri uri =null;
        if(stPhotoImgURI != null){
        uri = Uri.parse(stPhotoImgURI);
        mThumbController.saveLatLon(uri.getPath(), dLat, dLon);
        Log.i(TAG, "stPhotoImgURI path:"+uri.getPath());
        
        }
         stCategory=((TextView)findViewById(R.id.txt_severity_val)).getText().toString();
        
         stComments=((TextView)findViewById(R.id.txt_notes)).getText().toString();
       final DataSend dataSend = new DataSend();
        
        
        String stFileName="";
       
       
        if(stPhotoImgURI != null){
            stFileName=uri.getPath();
            String stDesFile_path=stFileName;
          
            /*try {
                Utils.saveUriImage(uri, this, stDesFile_path);
            } catch (FileNotFoundException e1) {
               
                e1.printStackTrace();
            } catch (IOException e1) {
               
                e1.printStackTrace();
            }*/
           
          
        }
       
        if(Utils.isInternetAvailable(this)){
            showToast(getString(R.string.your));
        }else{
            showToast(getString(R.string.connection_error));
            return;
        }
       
        
        String stJsonName=PhotoActivity.getFlagStoreDir() + String.valueOf(100) +File.separator+Utils.getDeviceUUID(this)+".json";
        JSONObject manJson = new JSONObject();
        try {
            manJson.put("lat", String.valueOf(dLat));
            manJson.put("lon", String.valueOf(dLon));
            manJson.put("category", stCategory);
            manJson.put("comments", stComments);
            String stPhoneID=Utils.getDeviceUUID(this);
            manJson.put("phoneUid",stPhoneID );
           
            SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(this);
            long id=preferences.getLong(ID, 0);
            Editor editor = preferences.edit();
            editor.putLong(ID, ++id);
            editor.commit();
            manJson.put("incidentIdInternal",stPhoneID+"_"+id );
            BufferedWriter writer=null;
            try {
                writer = new BufferedWriter(new FileWriter(stJsonName));
                writer.write(manJson.toString());
            } catch (IOException e) {
                
                e.printStackTrace();
            }finally{
                if(writer != null){
                    try {
                        writer.close();
                    } catch (IOException e) {
                       
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            
            e.printStackTrace();
        }
       
       // thread.start();
        String [] filePaths= {stFileName,stJsonName};//tcc.meme@tccattachments.hydrogen.myconnectedsite.com
        Utils.sendEmail_attchament(this, new String[]{"tcc.irtcc.IR.Request@tccattachments.hydrogen.myconnectedsite.com"}, "hackathon", "hackathon", filePaths, "Send email",HIT);
        
      
        
    }
   private void storeToDB(){
	   byte bImgData[]=null;
	   if(stPhotoImgURI != null){
       bImgData=storeMediaData( stPhotoImgURI);
	   }
	   SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(this);
       long id=preferences.getLong(ID, 0);
       String stPhoneID=Utils.getDeviceUUID(this);
       TrackDBManager dbManager = TrackDBManager.getInstance(this);
       
       dbManager.insertOrUpdateThumbnail(bImgData, dLat, dLon, stComments,stCategory,stPhoneID+"_"+id );
       
   }
   
    public byte[] storeMediaData(String stFileName) {
    	byte bData[]=null;
        try {
        		if(stFileName == null || ! new File(Uri.parse(stFileName).getPath()).exists()){
        			return bData;
        		}
           InputStream is = new FileInputStream(new File(Uri.parse(stFileName).getPath()));
           ByteArrayOutputStream os = new ByteArrayOutputStream(is.available());
           
           int read = 0;
           byte[] bytes = new byte[4096];
    
           while ((read = is.read(bytes)) != -1) {
              os.write(bytes, 0, read);
           } 
           bData= os.toByteArray();
           is.close();
           os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bData;
     }
    
    private void saveUI() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Editor editor = preferences.edit();
        editor.putInt(SELECTION_INDEX, iIndex);
        editor.putString(LATITUDE, edLatitude.getText().toString());
        editor.putString(LONGITUDE, edLongtitude.getText().toString());
        editor.commit();
    }

    private void updateUI() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int index = preferences.getInt(SELECTION_INDEX, D_CATEGORY_SELECTE_INDEX);
        ((TextView)findViewById(R.id.txt_severity_val)).setText(lstCategoryType[index]);
        setLatLon(preferences.getString(LATITUDE, D_LATITUDE),preferences.getString(LONGITUDE, D_LONGITUDE));
        // remove button handler
        ImageView button_Image = (ImageView)findViewById(R.id.img_template);
          // thumbnail controller
        if (mThumbController == null && button_Image != null) {
            ContentResolver mContentResolver = getContentResolver();
            mThumbController = new ThumbnailController(getResources(),
                    ((ImageView)findViewById(R.id.img_template)), mContentResolver);

        }
        if (button_Image != null)
            // set thumbnail image
            setPhotoImage();

    }
private void setLatLon(String stLat,String stLon){
    edLatitude.setText(stLat);
    edLongtitude.setText(stLon);
    txt_latitude.setText(stLat);
    txt_longitude.setText(stLon);
}
    @Override
    public void onLocationChanged(Location location) {

        txt_latitude.setText(String.valueOf(location.getLatitude()));
        txt_longitude.setText(String.valueOf(location.getLongitude()));
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

}
