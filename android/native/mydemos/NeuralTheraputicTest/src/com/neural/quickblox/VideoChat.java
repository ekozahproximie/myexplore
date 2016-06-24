/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.neural.quickblox
 *
 * File name: VideoChat.java
 *
 * Author: sprabhu
 *
 * Created On: Nov 9, 201412:14:06 AM
 *
 * Abstract:
 *
 *
 * Environment: Mobile Profile : Mobile Configuration :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.neural.quickblox;

import static com.neural.quickblox.definitions.Consts.APP_ID;
import static com.neural.quickblox.definitions.Consts.AUTH_KEY;
import static com.neural.quickblox.definitions.Consts.AUTH_SECRET;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.neural.demo.R;
import com.neural.demo.VideoCapture;
import com.neural.quickblox.activity.SignInActivity;
import com.neural.quickblox.helper.DataHolder;
import com.neural.quickblox.modle.listener.OnCallDialogListener;
import com.neural.quickblox.ui.view.OpponentSurfaceView;
import com.neural.quickblox.ui.view.OwnSurfaceView;
import com.quickblox.core.QBCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.internal.core.communication.RestResult;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.SessionCallback;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat.core.QBVideoChatController;
import com.quickblox.module.videochat.model.definition.VideoChatConstants;
import com.quickblox.module.videochat.model.listeners.OnQBVideoChatListener;
import com.quickblox.module.videochat.model.objects.CallState;
import com.quickblox.module.videochat.model.objects.CallType;
import com.quickblox.module.videochat.model.objects.VideoChatConfig;
import com.quickblox.module.videochat.model.utils.Debugger;

import org.jivesoftware.smack.XMPPException;

/**
 * @author sprabhu
 *
 */
public final class VideoChat {

   private transient Context   context      = null;

   private AlertDialog.Builder builder;

   public static final int     LOGIN_DIALOG = 1000;

   

   private VideoChatConfig     videoChatConfig;

   private QBUser              qbLoginUser;
   
   private QBUser              qbCalleeUser;

   private transient boolean   isLogggedIn  = false;

   private Activity            activity     = null;

   private static VideoChat    videoChat    = null;

   private AlertDialog         alertDialog  = null;
   
   private ProgressBar opponentImageLoadingPb;
   
   private QBCreateSessionCallback createSessionCallback =null;
   
   Ringtone callRingtone =null;

   /**
    * 
    */
   private VideoChat(final Context context) {
      this.context = context;
      Uri notification = RingtoneManager
            .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
      callRingtone = RingtoneManager
    .getRingtone(context, notification);
   }

   public static synchronized VideoChat getInstance(final Context context) {
      if (videoChat == null) {
         videoChat = new VideoChat(context);
      }
      return videoChat;
   }

   public void setActivity(final Activity activity) {
      this.activity = activity;
   }

   private void createLogin() {
      // Getting app credentials --
// http://quickblox.com/developers/Getting_application_credentials
      QBSettings.getInstance().fastConfigInit(String.valueOf(APP_ID), AUTH_KEY,
            AUTH_SECRET);
   }

   private void createSession(String login, final String password) {
      createSessionCallback = new QBCreateSessionCallback(
            password);
      QBAuth.createSession(login, password,createSessionCallback);
   }

   class QBCreateSessionCallback implements QBCallback {

      private final String password;

      QBCreateSessionCallback(String password) {
         this.password = password;
      }

      @Override
      public void onComplete(Result result) {
         
         if (result.isSuccess()) {
            if(result instanceof QBSessionResult){
               // save current user
               final int iUserId = ((QBSessionResult) result).getSession()
                     .getUserId();
            
               DataHolder.getDataHolder().setCurrentQbUser(iUserId, password);
               QBChatService.getInstance().loginWithUser(
                     DataHolder.getDataHolder().getCurrentQbUser(), loginListener);
            }else{
               if(result instanceof RestResult){
                  RestResult restResult =(RestResult)result;
                 
               }
               
            }
           
            
            
         } else {
            if (result instanceof RestResult && ! result.isSuccess()) {
               RestResult restResult =(RestResult)result;
               Toast.makeText(context, restResult.getErrors().toString(), Toast.LENGTH_SHORT)
               .show();   
            } else {
               isLogggedIn = false;
               Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT)
                     .show();
            }
            dismissAlertDialog();
         }

      }

      @Override
      public void onComplete(Result result, Object contextResult) {
         if(!result.isSuccess()){
            dismissAlertDialog();
            Toast.makeText(context,  result.getErrors().toString(), Toast.LENGTH_SHORT)
            .show();
         }
      }
   }
private void initSecondUser(){
   final String stUserId=SignInActivity.getStringPrefrenceValue(activity,
         SignInActivity.CALLEE_USER_ID, null);
   
   if(stUserId == null ){
      return;
   }

   final int iUserId = Integer.parseInt(stUserId);
   
   qbCalleeUser = new QBUser(iUserId);
   
   final QBUser qbLoginUser =DataHolder.getDataHolder().getCurrentQbUser() ;
   this.qbLoginUser=qbLoginUser;
}
   private SessionCallback loginListener = new SessionCallback() {

              @Override
              public void onLoginSuccess() {
                 try {
                    QBVideoChatController.getInstance()
                          .initQBVideoChatMessageListener();
                    isLogggedIn = true;
                    initSecondUser();
                    setQbListener();
                 } catch (XMPPException e) {
                    e.printStackTrace();
                 }
                 activity.runOnUiThread(new Runnable() {
                  
                  @Override
                  public void run() {
                     setUpCallOption();
                     activity.dismissDialog(LOGIN_DIALOG);
                     
                  }
               });
                
                 
              }

           @Override
           public void onLoginError(
                 final String error) {
              activity.dismissDialog(LOGIN_DIALOG);
              Toast.makeText(context,R.string.error_login,Toast.LENGTH_SHORT).show();
           }
        };
private void setUpCallOption(){
   // show next context
   if(activity instanceof VideoCapture){
      final VideoCapture videoCapture =(VideoCapture) activity;
      videoCapture. showCallOption();   
   }
}
   public void onResume() {
      try {
         final String login= SignInActivity.getStringPrefrenceValue(activity,
               SignInActivity.LOGIN_NAME, null);
         final String password= SignInActivity.getStringPrefrenceValue(activity,
               SignInActivity.PASS_WORD, null);
         final String stCalleeUserId=SignInActivity.getStringPrefrenceValue(activity,
               SignInActivity.CALLEE_USER_ID, null);
         if(login == null && password == null){
            isLogggedIn=false;
         }
         final boolean isUserLoggedIn=! isLogggedIn  && stCalleeUserId != null 
               && login != null && password != null;
         if( isUserLoggedIn){
            activity.showDialog(LOGIN_DIALOG);
            createLogin();
            createSession(login, password);
         }else{
            setUpCallOption();
         }
         
         setQbListener();
      } catch (XMPPException e) {

         e.printStackTrace();
      }
   }
   public void appExit(){
      builder=null;
      alertDialog =null;
      if(createSessionCallback != null){
         QBAuth.deleteSession(createSessionCallback);
      }
      finishCall();
      activity=null;
      qbLoginUser=null;
      qbCalleeUser=null;
      isLogggedIn=false;
      videoChatConfig=null;
      
      
      // destroy session after app close
      DataHolder.getDataHolder().setSignInQbUser(null);
     
      
   }
private void setQbListener() throws XMPPException{
   if (qbLoginUser != null) {
         QBVideoChatController.getInstance().setQBVideoChatListener(qbLoginUser,
            qbMyVideoChatListener);
   }
}
   public void showCallUserActivity() {
      if(isLogggedIn == false ){
         return;
      }
     
      activity.showDialog(1);
      videoChatConfig = QBVideoChatController.getInstance().callFriend(qbCalleeUser,
            CallType.VIDEO_AUDIO, null);
   }
private void dismissAlertDialog(){
   if(activity != null){
      activity.removeDialog(1);
   }
}
private void renderOpponetView(byte[] videoData){
   if(videoData != null){
      Log.i("test", "onOpponentVideoDataReceive: "+videoData.length);
   }
    opponentView.render(videoData);
}
   private OnQBVideoChatListener qbMyVideoChatListener = new OnQBVideoChatListener() {
      @Override
      public void onCameraDataReceive(byte[] videoData) {
          //
      }

      @Override
      public void onMicrophoneDataReceive(byte[] audioData) {
          QBVideoChatController.getInstance().sendAudio(audioData);
      }

      @Override
      public void onOpponentVideoDataReceive(final byte[] videoData) {
         renderOpponetView(videoData);
      }

      @Override
      public void onOpponentAudioDataReceive(byte[] audioData) {
         if(audioData != null){
            Log.i("test", "onOpponentAudioDataReceive: "+audioData.length);
         }
          QBVideoChatController.getInstance().playAudio(audioData);
      }

      @Override
      public void onProgress(boolean progress) {
         if(opponentImageLoadingPb == null){
            opponentImageLoadingPb = (ProgressBar) activity.findViewById(R.id.opponentImageLoading);
         }
         opponentImageLoadingPb.setVisibility(progress ? View.VISIBLE : View.GONE);
      }
        @Override
        public void onVideoChatStateChange(
              CallState state,
              VideoChatConfig receivedVideoChatConfig) {
           onVideoChatState(state, receivedVideoChatConfig);
        }
     };

   private void onVideoChatState(CallState state,
         VideoChatConfig receivedVideoChatConfig) {
      Debugger.logConnection("onVideoChatStateChange: " + state);
      dismissAlertDialog();
      videoChatConfig = receivedVideoChatConfig;

  switch (state) {
     case ACCEPT:
        Toast.makeText(context, R.string.ACCEPT, Toast.LENGTH_SHORT).show();
        showCallDialog();
        break;
     case ON_ACCEPT_BY_USER:
        QBVideoChatController.getInstance().onAcceptFriendCall(
                    videoChatConfig,
                    null);
        startVideoChatActivity();
        break;
     case ON_REJECTED_BY_USER:
        Toast.makeText(context,R.string.reject_by_user,Toast.LENGTH_SHORT).show();
        showCallEndButton(false);
        break;
     case ON_DID_NOT_ANSWERED:
        Toast.makeText( context,R.string.user_not_answer,Toast.LENGTH_SHORT).show();
        showCallEndButton(false);
        break;
     case ON_CALL_END:
        Toast.makeText( context,R.string.ON_CALL_END,Toast.LENGTH_SHORT).show();
        showCallEndButton(false);
        break;
    case ON_CALL_START:
        Toast.makeText( context,R.string.ON_CALL_START,Toast.LENGTH_SHORT).show();
        break;
    case ON_CONNECTED:
        Toast.makeText( context,R.string.ON_CONNECTED,Toast.LENGTH_SHORT).show();
        break;
    case ON_START_CONNECTING:
        Toast.makeText( context,R.string.ON_START_CONNECTING,Toast.LENGTH_SHORT).show();
        break;
    case ON_CANCELED_CALL:
       Toast.makeText( context,R.string.ON_CANCELED_CALL,Toast.LENGTH_SHORT).show();
       showCallEndButton(false);
        videoChatConfig = null;
        if (alertDialog != null
              && alertDialog
                    .isShowing()) {
           alertDialog
                 .dismiss();
        }
        autoCancelHandler
              .removeCallbacks(autoCancelTask);
        break;
  }
     }
   private Handler               autoCancelHandler   = new Handler(
                                                           Looper.getMainLooper());
   private Runnable              autoCancelTask      = new Runnable() {

              @Override
              public void run() {
                 if (alertDialog != null
                       && alertDialog
                             .isShowing()) {
                    alertDialog
                          .dismiss();
                 }
              }
           };

   private void showCallDialog() {
      autoCancelHandler.postDelayed(autoCancelTask, 30000);
      alertDialog = showCallDialog(activity, new OnCallDialogListener() {

         @Override
         public void onAcceptCallClick() {
            QBVideoChatController.getInstance().acceptCallByFriend(
                  videoChatConfig, null);
            startVideoChatActivity();
            autoCancelHandler.removeCallbacks(autoCancelTask);
         }

         @Override
         public void onRejectCallClick() {
            showCallEndButton(false);
            QBVideoChatController.getInstance().rejectCall(videoChatConfig);
            autoCancelHandler.removeCallbacks(autoCancelTask);
         }
      });
   }

   public AlertDialog showCallDialog(Context context,
         final OnCallDialogListener callDialogListener) {
      callRingtone.play();
      if (builder == null) {
         DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               switch (which) {
                  case DialogInterface.BUTTON_POSITIVE:
                     callDialogListener.onAcceptCallClick();
                     deleteCallDialog();
                     break;
                  case DialogInterface.BUTTON_NEGATIVE:
                     callDialogListener.onRejectCallClick();
                     deleteCallDialog();
                     break;
               }
// dismissDialog();
            }
         };
         builder = new AlertDialog.Builder(context);
         builder.setTitle(context.getString(R.string.calling_dialog_title))
               .setMessage(R.string.calling_dialog_txt)
               .setPositiveButton(VideoChatConstants.YES, onClickListener)
               .setNegativeButton(VideoChatConstants.NO, onClickListener)
               .show();
      }

      return builder.create();
   }
   private OpponentSurfaceView opponentView;
  
  private void showCallEndButton(final boolean isShowCallEndButton){
        
     ((VideoCapture)activity).restoreCallButton(isShowCallEndButton);
     
     opponentView = (OpponentSurfaceView) activity.findViewById(R.id.opponentView);
     opponentImageLoadingPb = (ProgressBar) activity.findViewById(R.id.opponentImageLoading);
     final View cameraLayout =(View)activity.findViewById(R.id.surface_camera);
     if(cameraLayout == null){
        return;
     }
     final LinearLayout.LayoutParams layoutParams =(LayoutParams) cameraLayout.getLayoutParams();
     final LinearLayout opponentLayout = (LinearLayout)activity.findViewById(R.id.opponentLayout);
     if(opponentLayout == null || opponentView == null){
        return;
     }
     if(isShowCallEndButton){
        layoutParams.weight=0.75f;  
        opponentView.setVisibility(View.VISIBLE);
        opponentLayout.setVisibility(View.VISIBLE);
     }else{
        opponentLayout.setVisibility(View.GONE);
        opponentView.setVisibility(View.GONE);
        layoutParams.weight=1.0f;
     }
     cameraLayout.setLayoutParams(layoutParams);
  }
  
  public void test(){
     showCallEndButton(true);
     final VideoCapture videoCapture =(VideoCapture)activity;
     final OwnSurfaceView ownSurfaceView = videoCapture.getOwnSurfaceView();
     if(ownSurfaceView != null){
    ownSurfaceView.setCameraDataListener(new OwnSurfaceView.CameraDataListener() {
        @Override
        public void onCameraDataReceive(byte[] data) {
           renderOpponetView(data);
        }
    });
     }
  }
   private void startVideoChatActivity() {
      if(activity == null){
         return;
      } 
      showCallEndButton(true);
     
      final VideoCapture videoCapture =(VideoCapture)activity;
      final OwnSurfaceView ownSurfaceView = videoCapture.getOwnSurfaceView();
      if(ownSurfaceView != null){
         ownSurfaceView.setCameraDataListener(new OwnSurfaceView.CameraDataListener() {
         @Override
         public void onCameraDataReceive(byte[] data) {
             if (videoChatConfig != null && videoChatConfig.getCallType() != CallType.VIDEO_AUDIO) {
                 return;
             }
           
             QBVideoChatController.getInstance().sendVideo(data);
         }
     });
      }
      try {
         setQbListener();
      } catch (XMPPException e) {
          e.printStackTrace();
      }
   }
   public void finishCall(){
      if(videoChatConfig != null){
         showCallEndButton(false);
         QBVideoChatController.getInstance().finishVideoChat(videoChatConfig);
      }
    
   }

   private void deleteCallDialog() {
      final Handler h = new Handler();
      h.postDelayed(new Runnable() {

         @Override
         public void run() {
            callRingtone.stop();
            builder = null;
         }
      }, 2000);
   }

   public void dismissDialog() {
      builder = null;
   }
}
