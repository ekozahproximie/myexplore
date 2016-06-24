//package com.neural.view;
//
//
//
//import android.content.Context;
//import android.view.OrientationEventListener;
//
//public class MyOrientationEventListener extends OrientationEventListener {
//
//	private boolean mMediaRecorderRecording=false;
//	private int mOrientation;
//
//	public MyOrientationEventListener(Context context) {
//		super(context);
//		
//	}
//
//	public MyOrientationEventListener(Context context, int rate) {
//		super(context, rate);
//		
//	}
//
//	@Override
//	public void onOrientationChanged(int orientation) {
//		 if (mMediaRecorderRecording) return;
//         // We keep the last known orientation. So if the user first orient
//         // the camera then point the camera to floor or sky, we still have
//         // the correct orientation.
//         if (orientation == ORIENTATION_UNKNOWN) return;
//         mOrientation = roundOrientation(orientation);
//        
//	}
//
//	public static int roundOrientation(int orientation) {
//        return ((orientation + 45) / 90 * 90) % 360;
//    }
//	
//	public int getOrientation() {
//		return mOrientation;
//	}
//}
