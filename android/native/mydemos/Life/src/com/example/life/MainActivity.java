package com.example.life;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;
import java.util.Set;

public class MainActivity extends FragmentActivity {
   /**
    * 
    */
   private static final String TESTDIALOG = "testdialog";
   
   private  static IncomingHandler handler = new IncomingHandler();
   private static class IncomingHandler extends Handler {
      
      private WeakReference<MainActivity> wr = null;

      private IncomingHandler() {

      }

      private void setMainActivity(final MainActivity activity) {
         wr = new WeakReference<MainActivity>(activity);
         
      }

      {
         Log.i(tag, "Handler  instance black:" + this.hashCode());
      }
    protected void finalize() throws Throwable {
       Log.i(tag, "Handler finalize");
       
    };
      @Override
      public void handleMessage(Message msg) {
         super.handleMessage(msg);
         final MainActivity activity = wr.get();
         if(activity != null){
            //activity. removeDialog(1);
            
            activity. dismissProgressDialogFragment();
         }else{
            
         }
         Log.i(tag, "handleMessage A :"+this.hashCode()+","+activity.hashCode());
      }
   }
   public static final boolean isThreadRunning(final String stThreadName) {
      boolean isThreadRuning = false;
      if (stThreadName == null || stThreadName.length() == 0) {
         return isThreadRuning;
      }
      if (Thread.getAllStackTraces() != null) {
         Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
         if (threadSet != null) {
            Thread[] runningThreadArray = threadSet
                  .toArray(new Thread[threadSet.size()]);
            for (int i = 0; i < runningThreadArray.length; i++) {
               Thread runningThread = runningThreadArray[i];
               if (runningThread != null) {
                  final String stRunningThreadName = runningThread.getName();
                  isThreadRuning = stThreadName.equals(stRunningThreadName);
                  if (isThreadRuning) {
                     break;
                  }
               }

            }
         }
      }

      return isThreadRuning;

   }
    public static final String tag="life";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler.setMainActivity(this);
        Log.i(tag, "onCreate A"+savedInstanceState+","+hashCode());
 
       if(savedInstanceState == null){
       Thread thread=  new Thread(){
            /* (non-Javadoc)
             * @see java.lang.Thread#run()
             */
            @Override
            public void run() {
             try {
               Thread.sleep(20000);
            } catch (InterruptedException e) {
              
               e.printStackTrace();
            }
             Log.i(tag, "sendEmptyMessage A");
             handler.sendEmptyMessage(1);
            }
         };
         thread.setName(TESTDIALOG);
         if(! isThreadRunning(TESTDIALOG)){
          showProgressDialogFragment("my dialog");
           
           // showDialog(1);
            thread.start();
         }
       }else{
        //  dismissProgressDialogFragment();
       }
    }
   
   @Override
   protected void onPrepareDialog(int id, Dialog dialog) {
     
      super.onPrepareDialog(id, dialog);
   }
    private Dialog initiateTwoButtonAlert(String displayText,
          String positiveButtonText, String negativeButtonText) {
     Dialog mDialog = new AlertDialog.Builder(this)
              .setTitle(getResources().getString(R.string.app_name))
              .setMessage(displayText)
              .setIcon(R.drawable.ic_action_search)
              .setPositiveButton(positiveButtonText, null)
              .setNegativeButton(negativeButtonText, null)
              .show(); 
     mDialog.setCanceledOnTouchOutside(false);
      WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
      layoutParams.dimAmount = 0.9f;
      mDialog.getWindow().setAttributes(layoutParams);
      mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
      return mDialog;
  }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
  
    @Override
    protected void onRestart() {
        Log.i(tag, "onRestart A");
        super.onRestart();
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
       
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(tag, "onRestoreInstanceState A");
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(tag, "onStart A");
    }
   
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(tag, "onResume A");
     
    }
   
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(tag, "onPause A");
    }
    
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(tag, "onStop A");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(tag, "onDestroy A");
    }
  
   @Override
   public void onConfigurationChanged(Configuration newConfig) {
     
      super.onConfigurationChanged(newConfig);
      Log.i(tag, "onConfigurationChanged A");
   }
   
   @Override
   protected Dialog onCreateDialog(int id) {
//      final Dialog dialog1 = new Dialog(this);
//      dialog1.setContentView(R.layout.test_fragment);
//      dialog1.setCanceledOnTouchOutside(false);
//      dialog1.setTitle(getString(R.string.app_name));
       ProgressDialog  dialog = new ProgressDialog(this);
       dialog.setCancelable(false);
       dialog.setMessage(getString(R.string.app_name));
      return  dialog; //initiateTwoButtonAlert("Hi", "Ok", "Cancel");
       
   }
    /* (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    /*@Override
    public Object onRetainNonConfigurationInstance() {
        Log.i(tag, "onRetainNonConfigurationInstance A");
        return super.onRetainNonConfigurationInstance();
    }*/
    /* (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(tag, "onSaveInstanceState A");
        super.onSaveInstanceState(outState);
    }
    
    public void onClick(View view){
       final Intent intent =new Intent(this,B.class);
        switch (view.getId()) {
            case R.id.start:
               intent.putExtra("test","hai");
               
                startActivity(intent);
                break;
            case R.id.startfor:
                startActivityForResult(new Intent(this,B.class),1);
                break;
            default:
                break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(tag, "onActivityResult A");
    }
    public void showProgressDialogFragment(final String stMessage){
       ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(stMessage);
       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       Fragment prev = getSupportFragmentManager().findFragmentByTag(
             ProgressDialogFragment.PROGRESS_FRAGMENT_TAG);
       if (prev == null) {
          transaction.add(progressDialog, ProgressDialogFragment.PROGRESS_FRAGMENT_TAG);
          transaction.commit(); 
       }
       getSupportFragmentManager().executePendingTransactions();
       
    }
    
    public void dismissProgressDialogFragment(){
       Fragment prev = getSupportFragmentManager().findFragmentByTag(
             ProgressDialogFragment.PROGRESS_FRAGMENT_TAG);
       Log.i(tag, "dismissProgressDialogFragment");
       if (prev != null && prev instanceof ProgressDialogFragment) {
        
          
          ((ProgressDialogFragment) prev).dismissDialog();
          Log.i("test", "dialog dismissed");
       }else {
          Log.i(tag, "dismissProgressDialogFragment:"+prev);
       }
    }
}
