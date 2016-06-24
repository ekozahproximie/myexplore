package com.neural.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.neural.demo.R;

public abstract class SettingAbstractFragment extends Fragment {
	public  abstract  int getShownIndex() ;
	
	public abstract boolean onKeyDown(int keyCode, KeyEvent event);
	
	public transient GestureDetector gestureDetector;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	
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
	
	public void gotoGraphScreen(){
		getActivity().setResult(Activity.RESULT_OK);
		getActivity().finish();
		getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.right_left);
	}

}
