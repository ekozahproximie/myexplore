package com.neural.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.bounce.balloongame.AndroidGame;
import com.neural.activity.DeviceSettingsActivity;
import com.neural.activity.GameActivity;
import com.neural.activity.GraphSettingsActivity;
import com.neural.activity.MediaSettingsActivity;
import com.neural.activity.RehabSettingsActivity;
import com.neural.activity.SettingsActivity;
import com.neural.activity.SettingsActivity.KeyEventListener;
import com.neural.demo.R;
import com.neural.setting.SettingItem;
import com.neural.setting.SettingListAdapter;

public class SettingListFragment extends ListFragment implements KeyEventListener {
	private transient boolean mDualPane;
	private transient int mCurCheckPosition = 0;
	private transient SettingAbstractFragment details=null;
	
	public static final int GRAPH_SETTING=0;
	public static final int REHAP_SETTING=1;
	public static final int MEDIA_SETTING=2;
	public static final int DEVICE_SETTING=3;
	public static final int GAME_SETTING=4;
	public static final int GAME2_SETTING=5;
	public static final int REPORT=6;
	
	private transient GestureDetector gestureDetector;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	 @Override
     public void onActivityCreated(Bundle savedInstanceState) {
         super.onActivityCreated(savedInstanceState);
         gestureDetector = new GestureDetector(new MyGestureDetector());
         String stSettingTitle[]=getResources().getStringArray(R.array.setting_title);
         // Populate list with our static array of titles.
         setListAdapter(initAdaptar());
         getListView().setDivider(this.getResources().getDrawable(R.drawable.blue_color));
         getListView().setDividerHeight(2);
         getListView().setBackgroundColor(0X7f707070);
         // Check to see if we have a frame in which to embed the details
         // fragment directly in the containing UI.
         View detailsFrame = getActivity().findViewById(R.id.settingdetails);
         mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

         if (savedInstanceState != null) {
             // Restore last state for checked position.
             mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
         }

         if (mDualPane) {
             // In dual-pane mode, the list view highlights the selected item.
             getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
             // Make sure our UI is in the correct state.
             showDetails(mCurCheckPosition);
         }
         getListView().setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 return gestureDetector.onTouchEvent(event);
             }
         });
     }

     @Override
     public void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);
         outState.putInt("curChoice", mCurCheckPosition);
     }
     
     @Override
     public void onListItemClick(ListView l, View v, int position, long id) {
         showDetails(position);
     }

     /**
      * Helper function to show the details of a selected item, either by
      * displaying a fragment in-place in the current UI, or starting a
      * whole new activity in which it is displayed.
      */
     void showDetails(int index) {
        if( index == REPORT){
           return;
        }
         mCurCheckPosition = index;

         if (mDualPane && index != GAME2_SETTING) {
             // We can display everything in-place with fragments, so update
             // the list to highlight the selected item and show the data.
             getListView().setItemChecked(index, true);
            
             // Check what fragment is currently shown, replace if needed.
              details = (SettingAbstractFragment)
                     getFragmentManager().findFragmentById(R.id.settingdetails);
             if (details == null || details.getShownIndex() != index) {
                 // Make new fragment to show this selection.
                 details = getInstance(index);

                 // Execute a transaction, replacing any existing fragment
                 // with this one inside the frame.
                 FragmentManager fm=getActivity().getSupportFragmentManager();
                 FragmentTransaction ft=fm.beginTransaction();
               
                 if (index == 0) {
                     ft.replace(R.id.settingdetails, details);
                 } else {
                     ft.replace(R.id.settingdetails, details);
                 }
                 ((SettingsActivity)getActivity()).setKeyEventListener(this);
                 ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                 ft.commit();
             }

         } else {
             // Otherwise we need to launch a new activity to display
             // the dialog fragment with selected text.
             Intent intent = new Intent();
             Class<?> calss =GraphSettingsActivity.class;
             if(index == MEDIA_SETTING){
            	 calss =MediaSettingsActivity. class;
             }else if(index == REHAP_SETTING){
            	 calss =RehabSettingsActivity.class;
             }else if(index == DEVICE_SETTING){
            	 calss =DeviceSettingsActivity.class;
             }else if(index == GAME_SETTING){
                calss =GameActivity.class;
             }else if(index == GAME2_SETTING){
                calss =AndroidGame.class;
             }
             intent.setClass(getActivity(), calss);
             intent.putExtra("index", index);
             startActivity(intent);
         }
     }
     
     private SettingAbstractFragment getInstance(int index){
    	 SettingAbstractFragment abstractFragment = null;
    	 if(index == GRAPH_SETTING ){
    		 abstractFragment =GraphFragment.newInstance(index);
         }else if(index == MEDIA_SETTING){
        	 abstractFragment =MediaFragmet.newInstance(index);
         }else if(index == REHAP_SETTING){
        	 abstractFragment =RehabFragment.newInstance(index);
         }else if(index == DEVICE_SETTING){
        	 abstractFragment =DeviceFragment.newInstance(index);
         }else if(index == GAME_SETTING){
            abstractFragment =GameFragment.newInstance(index);
         }
    	 
    	 return abstractFragment;
     }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if(details != null){
			 details.onKeyDown(keyCode, event);
		 }
		return false;
	}
	class MyGestureDetector extends SimpleOnGestureListener {
		
		 @Override
      public boolean onDown(MotionEvent e) {
          return true;
      }
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					//showToast("right to left swipe");
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					// left to right swipe
					gotoGraphScreen();
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}
	}
	
	private void gotoGraphScreen(){
	   getActivity().setResult(Activity.RESULT_OK);
           getActivity().finish();
           getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	private SettingListAdapter initAdaptar(){
		SettingItem setting_data[] = new SettingItem[]
		        {
		            
		            
		            new SettingItem(R.drawable.button_graph_setting, getString(R.string.graph_settings)),
		            new SettingItem(R.drawable.button_rehab_setting,getString(R.string.rehab_settings)),
		            new SettingItem(R.drawable.button_media_setting, getString(R.string.media_settings)),
		            new SettingItem(R.drawable.button_device_setting, getString(R.string.device_settings)),
		            new SettingItem(R.drawable.finger_icon_hover, getString(R.string.Games)),
		            new SettingItem(R.drawable.balloon_icon_hover, getString(R.string.Ballon_Games)),
		            new SettingItem(R.drawable.button_report_setting, getString(R.string.Reports))
		          
		        };
		
		SettingListAdapter adapter = new SettingListAdapter(getActivity(), 
                R.layout.settinglist_item_row, setting_data);
		return adapter;
	}
 }

