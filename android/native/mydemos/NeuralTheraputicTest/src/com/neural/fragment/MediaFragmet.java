package com.neural.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.neural.demo.R;
import com.neural.quickblox.UsersListActivity;
import com.neural.quickblox.activity.SignInActivity;
import com.neural.quickblox.definitions.QBQueries;
import com.neural.quickblox.helper.DataHolder;
import com.neural.quickblox.managers.QBManager;
import com.neural.setting.SettingsManager;
import com.quickblox.core.QBCallback;
import com.quickblox.core.result.Result;
import com.quickblox.module.users.model.QBUser;

public class MediaFragmet extends SettingAbstractFragment implements
		View.OnClickListener,QBCallback  {

	private transient boolean isVolumeON = false;

	private transient Switch muteSwitch = null;



	private transient SeekBar deviceVolumeBar = null;
	
	private transient SeekBar videoZoomBar = null;
	
	private transient AudioManager audioManager =null;
	
	private static final int USER_LOGIN=101;
	
	private static final int SELECT_USER=102;
	
	private static final int SHOW_LOGOUT=1;
	
	
	@Override
	public int getShownIndex() {

		return SettingListFragment.MEDIA_SETTING;
	}

	public static MediaFragmet newInstance(int index) {
		MediaFragmet f = new MediaFragmet();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		gestureDetector = new GestureDetector(new MyGestureDetector());
		View view = inflater.inflate(R.layout.media_settings, container, false);
		initUI(view);
		return view;
	}
	
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.btn_selectcallee){
                   final Intent intent = new Intent(getActivity(),UsersListActivity.class);
                   startActivityForResult(intent,SELECT_USER);
                }  else  if(view.getId() == R.id.btn_login){
                        final ToggleButton toggleLogin = (ToggleButton) view;
                        if (toggleLogin.isChecked()) {
                           getActivity().showDialog(SHOW_LOGOUT);
                           // call query to sign out by current user
                           QBManager.signOut(this, QBQueries.QB_QUERY_LOG_OUT_QB_USER);
                        } else {
                           final Intent intent = new Intent(getActivity(),
                                 SignInActivity.class);
                           startActivityForResult(intent, USER_LOGIN);
                        }
                     }
		
	

	}

	private void initUI(final View view) {
		SettingsManager manager = SettingsManager.getInstance();
		audioManager = (AudioManager) getActivity()
				.getSystemService(Context.AUDIO_SERVICE);
		muteSwitch = (Switch) view.findViewById(R.id.mute_switch);
		
		muteSwitch.setChecked( manager.getMuteOn(getActivity()));

		
		deviceVolumeBar = (SeekBar) view.findViewById(R.id.volumeseek);
		videoZoomBar= (SeekBar) view.findViewById(R.id.videozoomSeek);
		videoZoomBar.setProgressDrawable(getResources().getDrawable(
				R.drawable.seek_background));
		videoZoomBar.setProgress(manager.getVideoZoom(getActivity()));
		
		initVolumeControl();
		final Button btnLogin=(Button)view.findViewById(R.id.btn_login);
		//btnLogin.setOnClickListener(this);
		final Button btnSelectCallee=(Button)view.findViewById(R.id.btn_selectcallee);
		btnSelectCallee.setOnClickListener(this);
		
		view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	
	super.onActivityCreated(savedInstanceState);
	
	}
	
	@Override
	public void onResume() {
	   super.onResume();
	         setUserName();
	        setCalleeUserName();
	
	}
	private void setUserName(){
	   
	   final QBUser  qbUser=DataHolder.getDataHolder().getSignInQbUser();
	   final ToggleButton btnLogin=(ToggleButton)getView().findViewById(R.id.btn_login);
	   btnLogin.setOnClickListener(this);
	   final Button btn_selectCallee=(Button)getView().findViewById(R.id.btn_selectcallee);
	   final TextView tvUserName=(TextView) getView().findViewById(R.id.txt_username);
	   final String stUserName= (qbUser != null &&  qbUser.getLogin() != null) ?  qbUser.getLogin():
	      SignInActivity.getStringPrefrenceValue(getActivity(), SignInActivity.FULL_USER_NAME, null);
	   if(stUserName != null){
	      tvUserName.setText(":"+stUserName);
	      btn_selectCallee.setEnabled(true);
	   }else{
	      tvUserName.setText(":"+getString(R.string.no_user_loggedin));
	      btn_selectCallee.setEnabled(false);
	   }
	   btnLogin.setChecked(stUserName == null ?true:false);
	   
	}
	private void setCalleeUserName(){
           final String stCalleeUserName=SignInActivity.getStringPrefrenceValue(getActivity(),
                 SignInActivity.CALLEE_USER_NAME, null);
           final TextView tvUserName=(TextView) getView().findViewById(R.id.txt_calleeusername);
           if(stCalleeUserName != null){
              tvUserName.setText(":"+stCalleeUserName );
           }else{
              tvUserName.setText(":"+getString(R.string.no_callee_Selected));
           }
           
        }

   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
         case USER_LOGIN:
            setUserName();
            break;
         case SELECT_USER:
            if (resultCode == Activity.RESULT_OK) {
               setCalleeUserName();
            } else {
               signOut();
            }
            break;
         default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
      }

   }
	
	private void signOut(){
	   DataHolder.getDataHolder().setSignInQbUser(null);
	   SignInActivity.clear(getActivity().getApplicationContext());
	   setUserName();
	}
	 @Override
	    public void onComplete(Result result) {
	    }

	    @Override
	    public void onComplete(Result result, Object query) {
	        QBQueries qbQueryType = (QBQueries) query;
	        if (result.isSuccess()) {
	            switch (qbQueryType) {
	                case QB_QUERY_LOG_OUT_QB_USER:
	                    Toast.makeText(getActivity(), getString(R.string.user_log_out_msg),Toast.LENGTH_SHORT).show();;
	                    signOut();
	                    break;
	            }
	        } else {
	           Toast.makeText(getActivity(), result.getErrors().get(0),Toast.LENGTH_SHORT).show();;
	            
	        }
	        getActivity().dismissDialog(SHOW_LOGOUT);
	    }
	private void initVolumeControl() {
		try {
			
			deviceVolumeBar.setMax(audioManager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
			deviceVolumeBar.setProgress(audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC));
			deviceVolumeBar.setProgressDrawable(getResources().getDrawable(
					R.drawable.seek_background));
			deviceVolumeBar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// add here your implementation
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// add here your implementation
						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							audioManager.setStreamVolume(
									AudioManager.STREAM_MUSIC, progress, 0);

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	     switch (keyCode) {
	        case KeyEvent.KEYCODE_VOLUME_UP:
	            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	            //Raise the Volume Bar on the Screen
	            deviceVolumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)             );
	            return true;
	        case KeyEvent.KEYCODE_VOLUME_DOWN:
	            //Adjust the Volume
	            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,            AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	            //Lower the VOlume Bar on the Screen
	            deviceVolumeBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
	             );
	            return true;
	        default:
	            return false;
	     }
	}
	private void storeUIState() {
		SettingsManager manager = SettingsManager.getInstance();
		manager.storeMute(isVolumeON, getActivity());
		manager.storeVideoZoom(videoZoomBar.getProgress(), getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		storeUIState();
	}

   
  
	
	
	
}
