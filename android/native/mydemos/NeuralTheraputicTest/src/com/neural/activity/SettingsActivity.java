package com.neural.activity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.neural.demo.R;
import com.neural.fragment.AlertDialogFragment.DialogClickListener;

public class SettingsActivity extends NeuralBaseActivity {
	private transient GestureDetector gestureDetector;
	
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	public interface KeyEventListener {
		public boolean onKeyDown(int keyCode, KeyEvent event);
	}

	private transient KeyEventListener eventListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting_fragment);
		View view =findViewById(R.id.root);
		gestureDetector = new GestureDetector(new MyGestureDetector());
		view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
		
	}

	public void setKeyEventListener(KeyEventListener eventListener) {
		this.eventListener = eventListener;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (eventListener != null) {
			eventListener.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
	private void showToast(String stMsg){
		Toast.makeText(this, stMsg, Toast.LENGTH_LONG).show();
	}
	class MyGestureDetector extends SimpleOnGestureListener {
		
		 public MyGestureDetector() {
		
		}
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
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;
		else
			return false;

	}
	@Override
	public void onBackPressed() 
	{
	   super.onBackPressed();
	    this.finish();
	    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	    return;
	}
	private void gotoGraphScreen(){
//		Intent intent = new Intent(this,VideoCapture.class);
//		 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		startActivity(intent);
	        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
		finish();
	}
	
	   
	      private DialogClickListener dialogClickListener =null;
	      
	      /**
	       * @param dialogClickListener the dialogClickListener to set
	       */
	      public void setDialogClickListener(DialogClickListener dialogClickListener) {
	         this.dialogClickListener = dialogClickListener;
	      }
	      
	       
	      public void doPositiveClick(int iMessageCode, final int iData) {
	         if(dialogClickListener != null){
	            dialogClickListener.doPositiveClick(iMessageCode,iData);
	         }
	      }
	      
	      public void doNegativeClick() {
	         if(dialogClickListener != null){
	            dialogClickListener.doNegativeClick();
	         }
	      }
}
