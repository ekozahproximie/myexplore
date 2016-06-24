package com.trimble.agmantra.login;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.trimble.agmantra.scout.acdc.ScoutACDCApi;
import com.trimble.agmantra.scout.acdc.ScoutACDCApi.NetWorkListener;
import com.trimble.scout.MainActivity;


public class NetWorkListenerImpl implements NetWorkListener{

   private Activity activity =null;
   
   private ScoutACDCApi mAcdc = null;
   private transient boolean isFromLogin=false;
   
   private static final String TAG="ACDC";
   public NetWorkListenerImpl(final  Activity activity) {
      this.activity=activity;
      mAcdc=ScoutACDCApi.getInstance(activity);
   }
   @Override
   public void requestSucesss(boolean isSucess, String stErrorMsg) {
      mAcdc.unRegisterNetworkListener();
     // activity.dismissDialog(FramBaseActivity.SHOW_LOGIN_PROGRESS);
      if(isSucess){
              openActivity(MainActivity.class);
              //activity.setResult(Activity.RESULT_OK);
      }else{
            mAcdc.storeLoginInfo(null, null,null);
              Message message=handler.obtainMessage();
              message.obj=stErrorMsg;
              handler.sendMessage(message);
      }
      
   }
   private transient final Handler handler = new Handler(){
      public void handleMessage(android.os.Message msg) {
         Log.i(TAG, "error msg"+(String)msg.obj);
              
      };
};
   
   public void doLogin(final String stUserName,final String stPassword,boolean isFromLogin){
      this.isFromLogin=isFromLogin;
      mAcdc.storeLoginInfo(stUserName, stPassword, null);
      mAcdc.doRegistration(this);
   }
   private void openActivity(final Class<?> activityToOpen) {
      
   }

}
