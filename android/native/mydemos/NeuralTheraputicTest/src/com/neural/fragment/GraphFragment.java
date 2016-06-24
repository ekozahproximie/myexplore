package com.neural.fragment;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;

import com.neural.colorpicker.ColorPickerDialog;
import com.neural.colorpicker.ColorPickerDialog.OnColorChangedListener;
import com.neural.demo.R;
import com.neural.setting.SettingsManager;

public class GraphFragment extends SettingAbstractFragment  {

	private transient Switch graphSwitch = null;

	private transient Switch gridSwitch = null;

	private transient SeekBar timeBaseBar =null;
	
	private transient ColorPickerDialog gridColorPickerDialog = null;
	
	private transient ColorPickerDialog baseColorPickerDialog = null;
	
	private transient View.OnClickListener clickListener  =null;
	
	private transient Button gridcolor =null;
	
	private transient Button basecolor =null;
	
	
	private transient int iTimeProgressValue=0;
	
	private transient int iEMGScaleProgressValue=0;
	
	@Override
	public int getShownIndex() {

		return SettingListFragment.GRAPH_SETTING;
	}

	public static GraphFragment newInstance(int index) {
		GraphFragment f = new GraphFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.graph_settings, container,
				false);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		initUI(view);
		return view;
	}

	

	@Override
	public void onPause() {

		super.onPause();
		storeUIState();
		if( baseColorPickerDialog != null ){
			baseColorPickerDialog.cancel();
		}
		
		if( gridColorPickerDialog != null ){
			gridColorPickerDialog.cancel();
		}
		
		
	}

	private void initUI(final View view) {
		SettingsManager manager = SettingsManager.getInstance();
		
		timeBaseBar =(SeekBar)view.findViewById(R.id.timebaseSeek);
		timeBaseBar.setProgressDrawable(getResources().getDrawable(R.drawable.seek_background));
		
		graphSwitch = (Switch) view.findViewById(R.id.graph_switch);
		graphSwitch.setChecked(manager.getGraphStyle(getActivity()) == SettingsManager.BAR_STYLE);
		

		

		gridSwitch = (Switch) view.findViewById(R.id.grid_switch);
		gridSwitch.setChecked(manager.getGridDisplay(getActivity()));
		
		timeBaseBar.setMax(SettingsManager.MAX_TIME_SCALE);
		iTimeProgressValue=(int)manager.getTimeBase(getActivity());
		timeBaseBar.setProgress((int)manager.getTimeBase(getActivity()) -SettingsManager.MIN_TIME_SCALE);
		timeBaseBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                  
                  @Override
                  public void onStopTrackingTouch(SeekBar seekBar) {
                    final int iValue=seekBar.getProgress()+SettingsManager.MIN_TIME_SCALE;
                    iTimeProgressValue=iValue;
                  }
                  
                  @Override
                  public void onStartTrackingTouch(SeekBar seekBar) {
                    
                     
                  }
                  
                  @Override
                  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                     
                     
                  }
               } );
		view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
		 clickListener = new OnClickListener() {
			
			@Override
			public void onClick(final View view) {
				
				switch (view.getId()) {
				case R.id.gridcolor:
					showGridColorPicker();
					break;
				case R.id.basecolor:
					showBaseColorPicker();
					break;
				

				default:
					break;
				}
			}
		};
		 gridcolor = (Button) view.findViewById(R.id.gridcolor);
		 gridcolor.setOnClickListener(clickListener);
		
		 basecolor = (Button) view.findViewById(R.id.basecolor);
		basecolor.setOnClickListener(clickListener);
		initButtonColor();
		
	}
	
	private void initButtonColor(){
		gridcolor.setBackgroundColor(SettingsManager.getInstance().getGridColor(getActivity()));
		basecolor.setBackgroundColor(SettingsManager.getInstance().getBaseColor(getActivity()));
	}

	private void showGridColorPicker() {

		OnColorChangedListener changedListener = new OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {

				SettingsManager.getInstance().storeGridColor(color,
						getActivity());
				initButtonColor();
			}
		};
		baseColorPickerDialog = new ColorPickerDialog(getActivity(),
				changedListener, SettingsManager.getInstance().getGridColor(
						getActivity()));
		baseColorPickerDialog.show();
	}

	private void showBaseColorPicker() {

		OnColorChangedListener changedListener = new OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {

				SettingsManager.getInstance().storeBaseColor(color,
						getActivity());
				initButtonColor();
			}
		};
		baseColorPickerDialog = new ColorPickerDialog(getActivity(),
				changedListener, SettingsManager.getInstance().getBaseColor(
						getActivity()));
		baseColorPickerDialog.show();
	} 

	private void storeUIState() {
		SettingsManager manager = SettingsManager.getInstance();

		manager.storeGraphDisplayStyle(
				graphSwitch.isChecked() ? SettingsManager.BAR_STYLE
						: SettingsManager.LINEAR_STYLE, getActivity());

		manager.storeGridDisplay(gridSwitch.isChecked(), getActivity());
		manager.storeTimeBase(iTimeProgressValue, getActivity());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
}
