package com.trimble.scout;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.glass.media.CameraManager;
import com.google.android.glass.view.WindowUtils;
import com.trimble.agmantra.jobsync.JobSyncManager;
import com.trimble.agmantra.jobsync.jobuploader.JobUploadListener;
import com.trimble.agmantra.jobsync.jobuploader.JobUploaderQueue;
import com.trimble.scout.encode.VideoJob;
import com.trimble.scout.net.JobUploader;
import com.w9jds.gdk_progress_widget.SliderView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class MainActivity extends ScoutActivity implements JobUploadListener {
	
    /**
    * 
    */
   private static final String SCOUT = "Scout";
   private MediaRecorder mRecorder;
    private Handler h2;

    private Uri fileUri;
    public static final int MEDIA_TYPE_AUDIO = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final String MY_CAMERA_VIDEO="MyCameraVideo";
    private TextView status_text_view;
    
    private TextView time_status_text_view;
    private SliderView slider;
    
    private JobSyncManager mJobSyncManager = null;
    
    private  final TimerRunable  timerRunable = new TimerRunable();
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJobSyncManager=JobSyncManager.getInstance(getApplicationContext());
        getWindow().requestFeature(getVoiceCommands());

        setContentView(R.layout.activity_main);
        
        status_text_view = (TextView) findViewById(R.id.status_text_view);
        time_status_text_view= (TextView) findViewById(R.id.time_status_text_view);
        h2 = new Handler();  
        
        
        mRecorder = new MediaRecorder();
        
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        
        slider = (SliderView) findViewById (R.id.slider);
        
    }
   private void testCode(){
      VideoJob job = new  VideoJob( new File(Environment.getExternalStorageDirectory()
            +"/20140818_134739_378.mp4"), 
            getLocation(), this, true);
      job.createJobSendFile(job.getLocation());
   }
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == getVoiceCommands()) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        // Pass through to super to setup touch menu.
        return super.onCreatePanelMenu(featureId, menu);
    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
		
		if ( featureId == getVoiceCommands() ) {
			if (true == itemAction(item.getItemId())) {
				return true;
			}
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	private static final int MAX_TIME=15;	
	public boolean itemAction(int id){
		boolean ret = false;

		switch (id) {
		case R.id.action_exit:
			finish();
			ret = true;
			break;
		case R.id.action_audio:	        	        
	        final File fl =getOutputMediaFile(MEDIA_TYPE_AUDIO);
	        
	        if (fl.exists())
	        	fl.delete();
	        
	        mRecorder.setOutputFile(fl.getAbsolutePath());
	        
	        try {
	        	mRecorder.prepare();
	        	
	        
	        } catch (IllegalStateException e) {
	        	e.printStackTrace();
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	        
	        mRecorder.start();
    		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	        status_text_view.setText("Recording audio ...");
	        
	        time_status_text_view.setVisibility(View.VISIBLE);
	        h2.post(timerRunable);
	        
	        h2.postDelayed(new Runnable() {
	        	@Override
	        	public void run() {
	        		
                           Log.d(SCOUT, "Data completed.");
                           mRecorder.stop();
                           status_text_view.setText("Completed.");
                           sendDataToServer(fl.getAbsolutePath(),false);
                           h2.removeCallbacks(timerRunable);
                           timerRunable.clear();
	    	        new Handler().postDelayed (new Runnable() {
	    	        	@Override
	    	        	public void run(){
	    	        		status_text_view.setText("");
	    	        		time_status_text_view.setText("");
	    	        		time_status_text_view.setVisibility(View.GONE);
	    	        	}
	    	        }, 2000);
	        	}
	        }, MAX_TIME*1000);  
	        
	              
			ret = true;
			break;
			
		case R.id.action_video:
			startVideoCapture();
			ret = true;
			break;
		}
		return ret;
	}
   private class TimerRunable implements Runnable{

     private int iTime=0;
     
     private void clear(){
        iTime=0;
     }
      @Override
      public void run() {
         time_status_text_view.setText(String.format("0:%2d/0:%2d", iTime,MAX_TIME));
         if(iTime <= MAX_TIME){
            iTime++;
         }
         h2.postDelayed(timerRunable
               , 1000);
         
      }
   }
      
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.d(SCOUT, "Option selected :" + item.toString());
		int id = item.getItemId();
		if (true ==itemAction(id)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Display menu when user taps on touchpad or dismisses this activity if user swipes down
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
          if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
              openOptionsMenu();
              return true;
          } else if (keyCode == KeyEvent.KEYCODE_BACK) {
        	  finish();
          }
          return false;
    }
	
    @Override
    protected void onPause() {
    	if ( mRecorder != null ) {
    		mRecorder.release();
    		//mRecorder = null;
    	}
    	
    	super.onPause();
    	
    }
        
    public void startVideoCapture(){
        
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        
        // create a file to save the video
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); 
         
        // set the image file name  
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  
         
        // set the video image quality to high
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); 

        // start the Video Capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
               
    }
    
    private  Uri getOutputMediaFileUri(int type){
    	
    	Uri u = Uri.fromFile(getOutputMediaFile(type));
    	Log.d(SCOUT,"Uri - " + u);
        
        return u;
  }
    
    private  File getOutputMediaFile(int type){
        
        // Check that the SDCard is mounted
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getPath(),MY_CAMERA_VIDEO );
        File mediaFile=null; 
         
        // Create the storage directory(MyCameraVideo) if it does not exist
        if (! mediaStorageDir.exists()){
             
            if (! mediaStorageDir.mkdirs()){
                 
                //output.setText("Failed to create directory MyCameraVideo.");
                 
                //Toast.makeText(ActivityContext, "Failed to create directory MyCameraVideo.", 
                 //       Toast.LENGTH_LONG).show();
                 
                Log.d("MyCameraVideo", "Failed to create directory MyCameraVideo.");
                return null;
            }
        }
 
         
        // Create a media file name
         
        // For unique file name appending current timeStamp with file name
        java.util.Date date= new java.util.Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                             .format(date.getTime());
         
        
         
        if(type == MEDIA_TYPE_VIDEO) {
             
            // For unique video file name appending current timeStamp with file name
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+
                  File.separator+MY_CAMERA_VIDEO+File.separator+timeStamp+".mp4");
             
        } else if ( type == MEDIA_TYPE_AUDIO){
           mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+
                 File.separator+MY_CAMERA_VIDEO+File.separator+timeStamp+".mp4");
           
        }
 
        return mediaFile;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       
       // After camera screen this code will executed
        
       if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {

               Log.d("waveform", "result of recording - " + resultCode );
           if (resultCode == RESULT_OK) {
               //Toast.makeText(this, "Video saved to:" +
               //                                       data.getData(), Toast.LENGTH_LONG).show();
               Bundle extras = data.getExtras();
               
               if ( extras != null ) {
                       
                       Log.d(SCOUT, "Extras - " + extras);
                       
                       for (String key : extras.keySet()) {
                           Object value = extras.get(key);
                           Log.d(SCOUT, String.format("Extras(data): %s %s (%s)", key,  
                               value.toString(), value.getClass().getName()));
                       }

                   String pictureFilePath = extras.getString(getExtraVideoPath());
                   sendDataToServer(pictureFilePath,true);
               }
               
           }
       }
   }

    public void sendDataToServer(final  String pictureFilePath,final boolean isVideo){
       Log.d(SCOUT, "video FilePath = " + pictureFilePath);
       JobUploader jobUploaed = JobUploader.getInstance(getApplicationContext());
      final File file = new
       File(pictureFilePath);
       Log.d(SCOUT, "video File = " + file.getName());
       
       final VideoJob videoJob = new VideoJob(file , getLocation(),getApplicationContext(),isVideo);
       
       jobUploaed.updateVideoToServer(videoJob);

       slider.startIndeterminate();
    }
    
    private String getExtraVideoPath(){
       return  CameraManager.EXTRA_VIDEO_FILE_PATH;
    }
    private int getVoiceCommands(){
       return  WindowUtils.FEATURE_VOICE_COMMANDS;
    }
    
 
    @Override
    protected void onResume() {
     
        
       JobUploaderQueue jobUploaderQueue =mJobSyncManager.jobUploadQueue;
       jobUploaderQueue.addJobUploadListener(this);
       
       super.onResume();
        
        
    }
   @Override
   protected void onDestroy() {
      JobUploaderQueue jobUploaderQueue =mJobSyncManager.jobUploadQueue;
      jobUploaderQueue.removeJobUploadListener(this);
      super.onDestroy();
      stopSlider();
   }
   
   private void stopSlider(){
      slider.stopIndeterminate();
   }
   @Override
   public void onJobUploadStatus(int iStatus, long lJobID) {
      
      
   }
  
   @Override
   public void onJobUploadSuccess(String stJobFilePath, long lJobID) {
     
      stopSlider();
   }
   
   @Override
   public void onJobUploadFailure(String stJobFilePath, long lJobID, int iStatus) {
      stopSlider();
      
   }
   
   @Override
   public void onAllJobUploaded() {
   
      
   }

   
}
