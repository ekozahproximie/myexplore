package com.neural.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;

import com.neural.demo.R;
import com.neural.sensor.NtDeviceManagement;
import com.neural.setting.SettingsManager;

import java.util.ArrayList;

public class RehabFragment extends SettingAbstractFragment implements OnItemSelectedListener  {

	

	private transient SeekBar emgScaleBar = null;

	private transient SeekBar emgOffsetBar = null;

	private transient SeekBar tragerModeBar = null;

	private transient SeekBar currentTragetValueBar = null;

	private transient SeekBar boundaryModeBar = null;

	private transient Switch boundarySwitch = null;
   
   	private transient Spinner spinner_devicelist = null;
   	
   	private transient  int currentTragetValue = 0;
   	
   	private transient String stCurrentSensor=null;
   	
   	private static final String DEVICE_INDEX="device_index";
   	

	@Override
	public int getShownIndex() {

		return SettingListFragment.REHAP_SETTING;
	}

	public static RehabFragment newInstance(int index) {
		RehabFragment f = new RehabFragment();

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
		View view = inflater.inflate(R.layout.rehab_settings, container, false);
		initUI(view);
		return view;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return false;
	}

	

	private void initUI(final View view) {
		

		emgScaleBar = (SeekBar) view.findViewById(R.id.emgscaleseek);
		emgScaleBar.setProgressDrawable(getResources().getDrawable(
				R.drawable.seek_background));
		
		emgScaleBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		    int emgScaleValue = 0;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            	emgScaleValue = progress;
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

		
		emgOffsetBar = (SeekBar) view.findViewById(R.id.emgoffsetseek);
		emgOffsetBar.setProgressDrawable(getResources().getDrawable(
				R.drawable.seek_background));
		

		tragerModeBar = (SeekBar) view.findViewById(R.id.traget_seek);
		tragerModeBar.setProgressDrawable(getResources().getDrawable(
				R.drawable.seek_background));
		

		currentTragetValueBar = (SeekBar) view.findViewById(R.id.constant_traget_seek);
		currentTragetValueBar.setProgressDrawable(getResources().getDrawable(
				R.drawable.seek_background));
		
		
		currentTragetValueBar.setProgress(currentTragetValue - SettingsManager.MIN_CONSTANT_VALUE);
		currentTragetValueBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                  
           public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
           }
           public void onStartTrackingTouch(SeekBar seekBar) {
           }
           public void onStopTrackingTouch(SeekBar seekBar) {
              currentTragetValue = seekBar.getProgress() + SettingsManager.MIN_CONSTANT_VALUE;
           }
       });
		
		boundaryModeBar = (SeekBar) view.findViewById(R.id.boundmodeSeek);
		boundaryModeBar.setProgressDrawable(getResources().getDrawable(
				R.drawable.seek_background));
		
		
		
		
		boundarySwitch = (Switch) view.findViewById(R.id.boundary_switch);
		
		
		view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
		spinner_devicelist = (Spinner)view.findViewById(R.id.spinner_devicelist);
		final ArrayList<String> listOfData = new ArrayList<String>(NtDeviceManagement.MAX_MUSCLE_GROUP);
		listOfData.add(getResources().getString(R.string.left_bicep));
		listOfData.add(getResources().getString(R.string.right_bicep));
		listOfData.add(getResources().getString(R.string.left_tricep));
		listOfData.add(getResources().getString(R.string.right_tricep));
		
		final ArrayAdapter<String> adapter = 
		      new ArrayAdapter<String>(getActivity(), 
		            android.R.layout.simple_spinner_item, android.R.id.text1, listOfData) ;
		adapter.setDropDownViewResource(R.layout.device_spinner_item);
		spinner_devicelist.setAdapter(adapter);
		spinner_devicelist.setOnItemSelectedListener(this);
		spinner_devicelist.setSelection(getDeviceLastSelectionIndex());
		stCurrentSensor=getSensorPosition(getDeviceLastSelectionIndex(), getActivity());
		setUIValue();
	}
	private void setUIValue(){
	   final SettingsManager manager = SettingsManager.getInstance();
	   final int iEmgScale=manager.getEMGScale(getActivity(),stCurrentSensor);
	   emgScaleBar.setProgress(iEmgScale);
	   boundarySwitch.setChecked( manager.isBoundaryModeEnable(getActivity() ));
           boundaryModeBar.setProgress(manager.getBoundaryModeRange(getActivity()));
           currentTragetValue=manager.getTragetRangeValue(getActivity());
           final int iEmgOffest=manager.getEMGOffset(getActivity(),stCurrentSensor);
           emgOffsetBar.setProgress(iEmgOffest);
           tragerModeBar.setProgress(manager.getTragetMode(getActivity()));
           Log.i("test", " setUIValue :"+stCurrentSensor + ",EmgScale:"+iEmgScale+",EmgOffset:"+iEmgOffest);
	}
	@Override
	public void onPause() {
		
		super.onPause();
		storeUIState();
		storeDeviceSelection();
	}
	private void storeUIState() {
		SettingsManager manager = SettingsManager.getInstance();

		manager.storeBoundaryMode(boundarySwitch.isChecked(), getActivity());
		manager.storeEMGScale(emgScaleBar.getProgress(), getActivity(),stCurrentSensor);
		manager.storeEMGOffset(emgOffsetBar.getProgress(), getActivity(),stCurrentSensor);
		manager.storeTragetMode(tragerModeBar.getProgress(), getActivity());
		manager.storeTragetRangeValue(currentTragetValue, getActivity());
		manager.storeBoundaryModeRange(boundaryModeBar.getProgress(), getActivity());
	}

  private transient boolean isInit=true;
   @Override
   public void onItemSelected(AdapterView<?> parent, View view, int position,
         long id) {
      
      if(isInit){
         isInit=false;
         return;
      }
      storeUIState();
      stCurrentSensor =getSensorPosition(position, getActivity());
      setUIValue();
   }

  
   @Override
   public void onNothingSelected(AdapterView<?> parent) {
     
      
   }
	
   public static String getSensorPosition(final int position,final Context context){
      String stCurrentSensor = null;
      switch (position) {
         case 0:
            stCurrentSensor=context.getString(R.string.left_bicep);
            break;
         case 1:
            stCurrentSensor=context.getString(R.string.right_bicep);
            break;
         case 2:
            stCurrentSensor=context.getString(R.string.left_tricep);
            break;
         case 3:
            stCurrentSensor=context.getString(R.string.right_tricep);
            break;

         default:
            break;
      }
      return stCurrentSensor;
   }
   
   private void storeDeviceSelection(){
      final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
      final Editor editor = preferences.edit();
      editor.putInt(DEVICE_INDEX, spinner_devicelist.getSelectedItemPosition());
      editor.commit();
   }
   private int getDeviceLastSelectionIndex(){
      final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
      return preferences.getInt(DEVICE_INDEX, 0);
   }
}
