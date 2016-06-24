package com.trimble.assetservice;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.sample.compass.OrientationManager;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.trimble.myglassfleet.FleetMenuActivity;
import com.trimble.myglassfleet.R;
import com.trimble.vilicus.acdc.ACDCApi;
import com.trimble.vilicus.acdc.ACDCApi.NetWorkListener;
import com.trimble.vilicus.db.VilicusContentProvider;
import com.trimble.vilicus.entity.Asset;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;



public class AssetService extends Service {
   
   private static final String LIVE_CARD_TAG = "AssetServiceCardDemo";
   
   private static final String R3_LPASS = "R3Lpass";

   private static final String RELTESTAG_GMAIL_COM = "reltestag@gmail.com";


   private static final String LOG=AssetService.class.getSimpleName();
   
   
   private final AssetBinder mBinder = new AssetBinder();

   private transient TextToSpeech mSpeech;
   
   private transient ACDCApi mAcdc = null;
   
   private transient VilicusContentProvider vilicusContentProvider =null;
   
   private transient boolean isAppStoped=false;
   

   private transient LiveCard mLiveCard;
   
   private transient RemoteViews mLiveCardView;
   
   private transient OrientationManager mOrientationManager;
   
   /**
    * A binder that gives other components access to the speech capabilities provided by the
    * service.
    */
   public class AssetBinder extends Binder {
       /**
        * Read the current heading aloud using the text-to-speech engine.
        */
       public void readAssetNameAloud() {
           
           String headingText = "Not implemented";
           mSpeech.speak(headingText, TextToSpeech.QUEUE_FLUSH, null);
       }
   }

   @Override
   public void onCreate() {
      super.onCreate();
      // Even though the text-to-speech engine is only used in response to a menu action, we
      // initialize it when the application starts so that we avoid delays that could occur
      // if we waited until it was needed to start it up.
      mSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
          @Override
          public void onInit(int status) {
              // Do nothing.
          }
      });
      mAcdc=ACDCApi.getInstance(this);
      vilicusContentProvider=VilicusContentProvider.getInstance(this);
      
      SensorManager sensorManager =
            (SensorManager) getSystemService(Context.SENSOR_SERVICE);
      LocationManager locationManager =
            (LocationManager) getSystemService(Context.LOCATION_SERVICE);

     mOrientationManager = new OrientationManager(sensorManager, locationManager);
     startTrackUserOrientation(true);
   }

   
   
   @Override
   public IBinder onBind(Intent intent) {
      
      return mBinder;
   }
   
   @Override
   public int onStartCommand(Intent intent, int flags, int startId) {
      initLiveCard();
      mAcdc.serverMode(ACDCApi.REL_MODE);
      mAcdc.storeLoginInfo(RELTESTAG_GMAIL_COM, R3_LPASS, mAcdc.getOrganizationID());
      WorkerThread thread = new WorkerThread();
      thread.start();
      return START_STICKY;
   }
private void initLiveCard(){
   try{
   if (mLiveCard == null) {

      // Get an instance of a live card
      mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

      // Inflate a layout into a remote view
      mLiveCardView = new RemoteViews(getPackageName(),
          R.layout.activity_fleet_scroll);

    // Set up initial RemoteViews values
    
      
      mLiveCardView.setTextViewText(R.id.title_view,
              getString(R.string.app_name));
      

      // Set up the live card's action with a pending intent
      // to show a menu when tapped
      Intent menuIntent = new Intent(this, FleetMenuActivity.class);
      menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
          Intent.FLAG_ACTIVITY_CLEAR_TASK);
      mLiveCard.setAction(PendingIntent.getActivity(
          this, 0, menuIntent, 0));

      
      // Always call setViews() to update the live card's RemoteViews.
      mLiveCard.setViews(mLiveCardView);
      
      mLiveCard.attach(this);
      // Publish the live card
      mLiveCard.publish(PublishMode.REVEAL);

     
  }else{
     mLiveCard.navigate();
  }
   }catch(NoClassDefFoundError e){
      Log.e(LOG, e.getMessage(),e);
   }
}
   
   private  class WorkerThread extends Thread implements NetWorkListener {
      
      
      public WorkerThread() {
       super("Asset worker thread");
      }
      @Override
      public void run() {
           try {
              if(mAcdc.getTicket()  == null){
                 mAcdc.doRegistration(this);
              }else{
                   final boolean isAssetFetchSucess= mAcdc.getAssets();
                   if(isAssetFetchSucess){
                      readAllAsset();
                   }
              }
         } catch (UnknownHostException e) {
           Log.e(LOG, e.getMessage(),e);
         } catch (IOException e) {
            Log.e(LOG, e.getMessage(),e);
         }
      }
      @Override
      public void requestSucesss(boolean isSucess, String stErrorMsg) {
         readAllAsset();
         
      }
   }
   
   private void readAllAsset()
   {
     final List<Asset> assets =vilicusContentProvider.getAllAssets();
     if(assets != null){
        Log.i("test",assets.toString());
     }
   }
   @Override
   public void unbindService(ServiceConnection conn) {
      isAppStoped=true;
      super.unbindService(conn);
   }
   @Override
   public void onDestroy() {
      
      
      if (mLiveCard != null && mLiveCard.isPublished()) {
        
           mLiveCard.unpublish();
           mLiveCard = null;
       }
      mSpeech.shutdown();
      mSpeech = null;
      
      startTrackUserOrientation(false);
      mOrientationManager=null;
      
      mAcdc.close();
      vilicusContentProvider.clearAllCacheData();
      isAppStoped=true;
      super.onDestroy();
   }
   
   private void startTrackUserOrientation(final boolean isStart){
      
      if (isStart) {
         mOrientationManager.addOnChangedListener(mCompassListener);
         mOrientationManager.start();

         if (mOrientationManager.hasLocation()) {
             Location location = mOrientationManager.getLocation();
             
         }

        
     } else {
        
         mOrientationManager.removeOnChangedListener(mCompassListener);
         mOrientationManager.stop();

     }
   }
   
   private final OrientationManager.OnChangedListener mCompassListener =
         new OrientationManager.OnChangedListener() {

     @Override
     public void onOrientationChanged(OrientationManager orientationManager) {
        final float iUserHeading=orientationManager.getHeading();

         
     }

     @Override
     public void onLocationChanged(OrientationManager orientationManager) {
        final Location userLocation = orientationManager.getLocation();
         
     }

     @Override
     public void onAccuracyChanged(OrientationManager orientationManager) {
       final boolean  mInterference = orientationManager.hasInterference();
         
     }
 };
   
}
