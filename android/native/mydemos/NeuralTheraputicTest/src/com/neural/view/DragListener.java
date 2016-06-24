package com.neural.view;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neural.constant.GraphLineManager;
import com.neural.demo.R;
import com.neural.fragment.DeviceFragment;
import com.neural.fragment.DeviceFragment.ListItemData;
import com.neural.fragment.DeviceFragment.ListItemViewHolder;
import com.neural.sensor.NtDeviceManagement;
import com.neural.util.Utils;

import java.util.HashMap;

public class DragListener implements OnDragListener {

	private View parentView = null;
	private static final String TAG = DragListener.class.getSimpleName();
	private DeviceFragment deviceFragment =null;
	private transient HashMap< String, Integer> sensorPlacementMap= null;

	public DragListener(final View parentView,final DeviceFragment deviceFragment) {
		this.parentView = parentView;
		this.deviceFragment=deviceFragment;
		sensorPlacementMap=new HashMap<String, Integer>(1);
	}

	@Override
	public boolean onDrag(View layoutview, DragEvent dragevent) {
		int action = dragevent.getAction();
		switch (action) {
		case DragEvent.ACTION_DRAG_STARTED:
			Log.d(TAG, "Drag event started");
			break;
		case DragEvent.ACTION_DRAG_ENTERED:
			Log.d(TAG, "Drag event entered into " + layoutview.toString());
			break;
		case DragEvent.ACTION_DRAG_EXITED:
			Log.d(TAG, "Drag event exited from " + layoutview.toString());
			break;
		case DragEvent.ACTION_DROP:
			Log.d(TAG, "Dropped");
			final View view = (View) dragevent.getLocalState();
			final ViewGroup owner = (ViewGroup) view.getParent();
			SensorConnectionView connectionView =null;
			ImageView statusImgView=null;
			if(view instanceof DraggableDot ){
			   Object object =view.getTag();
			   if(object instanceof ListItemViewHolder){
			      ListItemViewHolder holder=(ListItemViewHolder)object;
			      connectionView=holder.getSensorConnectionView();
			      statusImgView= holder.getStatusView();
			      if(statusImgView != null){
			         statusImgView.setVisibility(View.GONE);
			         connectionView.setVisibility(View.VISIBLE);
			      }
			   }
			}
			owner.setTag(null);
			owner.removeView(view);
			LinearLayout container = (LinearLayout) layoutview;
			switch (container.getId()) {
			case R.id.right_bicep: {
				((DraggableDot) view).setLegend("RB");
				((DraggableDot) view).setCirclePaintColor(GraphLineManager.R_BICEP);
				if(connectionView != null){
				connectionView.setCirclePaintColor(GraphLineManager.R_BICEP);
				}
				container.setTag("RB");
				TextView tvRBD = (TextView) parentView.findViewById(R.id.rbd);
				tvRBD.setText(((DraggableDot) view).getDeviceName());
				
				final ListItemData itemData= deviceFragment.connectNTDeviceToMuscle( NtDeviceManagement.MUSCLE_GROUP_RIGHT_BICEP);
				if(itemData != null){
				   sensorPlacementMap.put(itemData.getDevAddress(), NtDeviceManagement.MUSCLE_GROUP_RIGHT_BICEP);
				}
				break;
			}
			case R.id.left_bicep: {
				((DraggableDot) view).setLegend("LB");
				((DraggableDot) view).setCirclePaintColor(GraphLineManager.L_BICEP);
				if(connectionView != null){
				connectionView.setCirclePaintColor(GraphLineManager.L_BICEP);
				}
				container.setTag("LB");
				TextView tvlBD = (TextView) parentView.findViewById(R.id.lbd);
				tvlBD.setText(((DraggableDot) view).getDeviceName());
				final ListItemData itemData= deviceFragment.connectNTDeviceToMuscle( NtDeviceManagement.MUSCLE_GROUP_LEFT_BICEP);
				if(itemData != null){
                                   sensorPlacementMap.put(itemData.getDevAddress(), NtDeviceManagement.MUSCLE_GROUP_LEFT_BICEP);
                                }
				break;
			}
			case R.id.right_tricep: {
				((DraggableDot) view).setLegend("RT");
				((DraggableDot) view).setCirclePaintColor(GraphLineManager.R_TRICEP);
				if(connectionView != null){
				connectionView.setCirclePaintColor(GraphLineManager.R_TRICEP);
				}
				container.setTag("RT");
				TextView tvRTD = (TextView) parentView.findViewById(R.id.rtd);
				tvRTD.setText(((DraggableDot) view).getDeviceName());
				final ListItemData itemData= deviceFragment.connectNTDeviceToMuscle( NtDeviceManagement.MUSCLE_GROUP_RIGHT_TRICEP);
				if(itemData != null){
                                   sensorPlacementMap.put(itemData.getDevAddress(), NtDeviceManagement.MUSCLE_GROUP_RIGHT_TRICEP);
                                }
				break;
			}
			case R.id.left_tricep: {
				((DraggableDot) view).setLegend("LT");
				((DraggableDot) view).setCirclePaintColor(GraphLineManager.L_TRICEP);
				if(connectionView != null){
				connectionView.setCirclePaintColor(GraphLineManager.L_TRICEP);
				}
				container.setTag("LT");

				TextView tvLTD = (TextView) parentView.findViewById(R.id.ltd);
				tvLTD.setText(((DraggableDot) view).getDeviceName());
				final ListItemData itemData= deviceFragment.connectNTDeviceToMuscle( NtDeviceManagement.MUSCLE_GROUP_LEFT_TRICEP);
				if(itemData != null){
                                   sensorPlacementMap.put(itemData.getDevAddress(), NtDeviceManagement.MUSCLE_GROUP_LEFT_TRICEP);
                                }
				break;
			}
			default:
				break;
			}
			if (view instanceof DraggableDot) {
				((DraggableDot) view).setRadius((int) Utils.convertDpToPixel(
						15, view.getContext()));
			}
			container.addView(view);
			view.setVisibility(View.VISIBLE);
			break;
		case DragEvent.ACTION_DRAG_ENDED:
			Log.d(TAG, "Drag ended");
			break;
		default:
			break;
		}
		checkDevieName();
		return true;
	}

	private void checkDevieName() {
		LinearLayout lrb = (LinearLayout) parentView
				.findViewById(R.id.right_bicep);
		Object key = lrb.getTag();

		if (key == null) {

			TextView tvRBD = (TextView) parentView.findViewById(R.id.rbd);
			tvRBD.setText(null);

		}
		LinearLayout llb = (LinearLayout) parentView
				.findViewById(R.id.left_bicep);
		key = llb.getTag();
		if (key == null) {

			TextView tvlBD = (TextView) parentView.findViewById(R.id.lbd);
			tvlBD.setText(null);

		}
		LinearLayout lrt = (LinearLayout) parentView
				.findViewById(R.id.right_tricep);
		key = lrt.getTag();
		if (key == null) {

			TextView tvRTD = (TextView) parentView.findViewById(R.id.rtd);
			tvRTD.setText(null);

		}

		LinearLayout llt = (LinearLayout) parentView
				.findViewById(R.id.left_tricep);
		key = llt.getTag();
		if (key == null) {

			TextView tvLTD = (TextView) parentView.findViewById(R.id.ltd);
			tvLTD.setText(null);

		}
	}
	
	public int getDeviceMusclePart(final String stDeviceAddress){
	   int iPalce=0;
	   if(sensorPlacementMap != null && sensorPlacementMap.size() > 0){
	      iPalce= sensorPlacementMap.get(stDeviceAddress);
	   }
	  return iPalce;
	}

}
