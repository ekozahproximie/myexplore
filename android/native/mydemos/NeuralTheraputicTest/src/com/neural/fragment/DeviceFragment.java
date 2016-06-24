package com.neural.fragment;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.neural.activity.SettingsActivity;
import com.neural.demo.R;
import com.neural.fragment.AlertDialogFragment.DialogClickListener;
import com.neural.sensor.NtDevice;
import com.neural.sensor.NtDevice.DeviceHandlerInterface;
import com.neural.sensor.NtDevice.DeviceHandlerParameter;
import com.neural.sensor.NtDeviceManagement;
import com.neural.sensor.NtDeviceSettings;
import com.neural.sensor.NtSensorGraphAttributes;
import com.neural.view.DragListener;
import com.neural.view.DraggableDot;
import com.neural.view.SensorConnectionView;
import com.shimmerresearch.driver.Shimmer;

import java.util.Set;

public class DeviceFragment extends SettingAbstractFragment implements
		View.OnClickListener, DeviceHandlerInterface,DialogClickListener {

	private NtDeviceManagement ntDeviceManager = null;
    private BluetoothAdapter blueToothAdapter = null;
    private CustomListAdapter btDevicesArrayAdapter = null;

    private transient Button scan = null;
    private transient ProgressBar progressBar = null;
    
    private LayoutInflater inflater;
    
    
    private boolean isScanning = false;
 // Intent request codes
    private static final int REQUEST_ENABLE_BT  = 3;
    
    private static final int CONNECT = 0;
    private static final int DISCONNECT = 1;
    //
    
    private transient ListItemData listDataForDialog=null; 
    private transient  DragListener dragListener =null;
    
    
 // Debugging
    private static final String TAG = "DeviceFragment";
    private transient TextView bluestatus =null;
    //white bottom,Red top ,grey elbow
    public class ListItemData {        
    	
    	public static final int DEVICE_NONE = 0;
    	public static final int DEVICE_CONNECTED = 1;
    	public static final int DEVICE_DISCONNECTED = 2;
    	public static final int DEVICE_PAIRED = 3;
    	public static final int DEVICE_UNPAIRED = 4;
    	
    	protected int status;        
    	protected String devName;        
    	protected String devAddress;
    	protected boolean busy;
    	protected int iConnectedDeviceColor=0;
    	
    	ListItemData(int status, String devName, String devAddress) { 
    		this.status = status;       
    		this.devName = devName;   
    		this.devAddress = devAddress;
    		this.busy = false;
    		
    	}       
    	
    	@Override        
    	public String toString() { 
    		return "status: " + status + " " + devName + " " + devAddress;  
    	}
    	
      /**
       * @return the devAddress
       */
      public String getDevAddress() {
         return devAddress;
      }
    }

    private class CustomListAdapter extends ArrayAdapter<ListItemData> { 
    	public CustomListAdapter(Context context, int resource, int textViewResourceId) {
    		super(context, resource, textViewResourceId); 
    	} 
    	
    	@Override        
    	public View getView(int position, View convertView, ViewGroup parent) {  
    		
    		ListItemViewHolder holder = null;
    		final ListItemData listData = (ListItemData) getItem(position); 
    		
    		if (convertView == null) {     
    			convertView = inflater.inflate(R.layout.device_name, null); 
    			holder = new ListItemViewHolder(convertView);  
    			convertView.setTag(holder);  
    		}            
    		
    		holder = (ListItemViewHolder) convertView.getTag();   
    		holder.setDeviceName(listData.devName);
    		holder.updateStatus(listData.status, listData.devAddress);
    		holder.updateBusyBar(listData.busy);
    		holder.setListData(listData);
    		return convertView; 
    	}
    }
    
    public  class ListItemViewHolder {  
    	
   		private TextView devName = null;    
   		private TextView description = null;     
   		private ImageView status = null;    
   		private DraggableDot draggableDot =null;
   		private transient SensorConnectionView sensorConnectionView =null;
   		private int stStatusMsg[]= { R.string.unpaired, R.string.paired_connected, 
   				R.string.paired_not_connected, R.string.paired_only,R.string.unpaired };
   		private  ListItemData listData =null;
		private ProgressBar busyBar = null;
   		public ListItemViewHolder(final View row) {  
			devName = (TextView) row.findViewById(R.id.deviceTitle); 
			description = (TextView) row.findViewById(R.id.deviceDetail);   
			status = (ImageView) row.findViewById(R.id.deviceImgStatus); 
			busyBar = (ProgressBar) row.findViewById(R.id.busyProgressBar);
			draggableDot = (DraggableDot) row.findViewById(R.id.drag_dot_3);
			sensorConnectionView=(SensorConnectionView) row.findViewById(R.id.deviceImgPlaceStatus);
			draggableDot.setTag(this);
			
			
			OnClickListener clickListener = new OnClickListener() {
                           
                           @Override
                           public void onClick(final View v) {
                              switch (v.getId()) {
                                 case R.id.deviceImgStatus:
                                    //By default it connect to right bicep
                                    deviceConnectDialog(listData,NtDeviceManagement.MUSCLE_GROUP_RIGHT_BICEP);
                                    break;
                                 case R.id.deviceImgPlaceStatus:
                                    if(listData != null){
                                       int iMusclePart=dragListener.getDeviceMusclePart(listData.devAddress);
                                       deviceConnectDialog(listData,iMusclePart);
                                    }
                                    break;

                                 default:
                                    break;
                              }
                              
                           }
                        };
                        status.setOnClickListener(clickListener);
                        sensorConnectionView.setOnClickListener(clickListener);
                        draggableDot.setOnDragListener( new OnDragListener() {
                           
                           @Override
                           public boolean onDrag(View v, DragEvent event) {
                             setCurrentSelectedDeviceItem(listData);
                              return false;
                           }
                        });
			status.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					draggableDot.setVisibility(View.VISIBLE);
					return true;
				}
			});
    	} 
   		
               /**
                * @return the sensorConnectionView
                */
               public SensorConnectionView getSensorConnectionView() {
                  return sensorConnectionView;
               }
               
               /**
                * @return the status
                */
               public ImageView getStatusView() {
                  return status;
               }
         /**
          * @param listData the listData to set
          */
         private void setListData(ListItemData listData) {
            this.listData = listData;
         }
         
         public void setCurrentSelectedDeviceItem(final ListItemData itemData){
             listDataForDialog=itemData;
         }
    	public void setDeviceName(String newDevName) {   
   			devName.setText(newDevName);    
   			draggableDot.setDeviceName(newDevName);
   		}             

    	public void updateBusyBar(boolean busy) {
			busyBar.setVisibility((busy) ? View.VISIBLE : View.GONE);
    	}
    	
   		public void updateStatus(int newStatus, String devAddress) {
   		
			description.setText(getResources().getString(stStatusMsg[newStatus]));
			//image level list
			status.setImageLevel(newStatus);
			
			if (ListItemData.DEVICE_NONE != newStatus) {
				//for display the device address with status msg
				description.setText(description.getText().toString() + " ("
						+ devAddress + ")");
			}
   		}
    }
    @Override
    public void onStart() {
    	
    	super.onStart();
    	
    	// If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!blueToothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } 
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	 if(true) 
    	    Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
       
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            	initDeviceList();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                bluestatus.setVisibility(View.VISIBLE);
                bluestatus.setText(getString(R.string.bluetooth_not_enable));
            }
            break;
              
        }
    }

    @Override
	public int getShownIndex() {
		return SettingListFragment.DEVICE_SETTING;
	}

	public static DeviceFragment newInstance(int index) {
		DeviceFragment f = new DeviceFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		f.setArguments(args);

		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.device_settings, container, false);
		initUI(view);
		
		return view;
	}

	@Override
	public void onClick(View view) {
	}

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
	}
	private class ClickListener  implements OnClickListener{

      /* (non-Javadoc)
       * @see android.view.View.OnClickListener#onClick(android.view.View)
       */
      @Override
      public void onClick(View v) {
         switch (v.getId()) {
            case R.id.scan:
               doDiscovery(isScanning = !isScanning);
               break;

            default:
               break;
         }
         
      }
	   
	}
	private void initUI(final View view) {
				
		progressBar = (ProgressBar) view.findViewById(R.id.scanProgressBar);
		progressBar.setVisibility(View.GONE);
		
		scan = (Button) view.findViewById(R.id.scan); 
		scan.setText(R.string.start_scan);
		scan.setBackgroundColor(getResources().getColor(
				R.color.blue_enable));
		
		final ClickListener clickListener = new ClickListener();
                
		scan.setOnClickListener(clickListener);
		
		// setup device list
		btDevicesArrayAdapter = new CustomListAdapter(view.getContext(), R.layout.device_name, R.id.title);
        ListView pairedListView = (ListView) view.findViewById(R.id.deviceList);
        pairedListView.setAdapter(btDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(deviceClickListener);

       
        bluestatus = (TextView) view.findViewById(R.id.bluestatus);

        // sync the managed device with what the bluetooth adapter can see
        ntDeviceManager = NtDeviceManagement.getDefaultDeviceManager(getActivity().getApplicationContext());
		ntDeviceManager.removeStaleDevices();

        // Get the local Bluetooth adapter
        blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
        dragListener = new DragListener(view,this);
        LinearLayout right_bicep=(LinearLayout)view.findViewById(R.id.right_bicep);
        LinearLayout left_bicep=(LinearLayout)view.findViewById(R.id.left_bicep);
        
        LinearLayout left_tricep=(LinearLayout)view.findViewById(R.id.left_tricep);
        LinearLayout right_tricep=(LinearLayout)view.findViewById(R.id.right_tricep);
        
        right_bicep.setOnDragListener(dragListener);
        left_bicep.setOnDragListener(dragListener);
        left_tricep.setOnDragListener(dragListener);
        right_tricep.setOnDragListener(dragListener);
        initDeviceList();
        
	}
	
	private void initDeviceList() {

		btDevicesArrayAdapter.clear();
		ntDeviceManager.removeStaleDevices();
		
		// get the currently paired devices
        Set<BluetoothDevice> pairedDevices = blueToothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
            	
            	ListItemData itemData = new ListItemData(ListItemData.DEVICE_PAIRED,
            			device.getName(), device.getAddress());

            	NtDevice ntDevice = ntDeviceManager.getNtDeviceByAddress(device.getAddress()); 
            	if (ntDevice != null) {
            		if (ntDevice.getState() == NtDevice.STATE_CONNECTED) {
            			itemData.status = ListItemData.DEVICE_CONNECTED;
            		}
            		else {
            			itemData.status = ListItemData.DEVICE_DISCONNECTED;
            		}
            	}
            	
            	btDevicesArrayAdapter.add(itemData);
            }
        } else {
        	ListItemData itemData = new ListItemData(ListItemData.DEVICE_NONE, "", "");
            btDevicesArrayAdapter.add(itemData);
        }
	}

    private void doDiscovery(boolean doScan) {
        // If we're already discovering, stop it
        if (blueToothAdapter.isDiscovering()) {
        	blueToothAdapter.cancelDiscovery();
        }
        
    	if (doScan) {
    	        scan.setText(R.string.stop_scan);
    		progressBar.setVisibility(View.VISIBLE);
    		initDeviceList();
        	blueToothAdapter.startDiscovery();
    	}
    	else {
        	blueToothAdapter.cancelDiscovery();
		scan.setText(R.string.start_scan);
    		progressBar.setVisibility(View.GONE);
    	}
    }
    
    public ListItemData connectNTDeviceToMuscle(final int iMusclePart){
       if(listDataForDialog != null){
          doConnect(listDataForDialog,iMusclePart);
       }
       return listDataForDialog;
    }
    private void doConnect(ListItemData data,final int iMusclePart) {
    	
    	// TODO: muscle group needs to be assigned when the drag-drop GUI functionality is ready
		ntDeviceManager.connect(data.devName, data.devAddress, this, 
		      iMusclePart, data);
		
		data.busy = true;
		btDevicesArrayAdapter.notifyDataSetChanged();
    }
    
    private void doDisconnect(ListItemData data) {

		ntDeviceManager.disconnect(data.devAddress);
    	
		data.busy = false;
		data.status = ListItemData.DEVICE_DISCONNECTED;
		btDevicesArrayAdapter.notifyDataSetChanged();
    }
    
    // The on-click listener for all devices in the ListViews
    private OnItemClickListener deviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long id) {

        	// Cancel discovery because it's costly and we're about to connect
            blueToothAdapter.cancelDiscovery();

            final ListItemData listData = (ListItemData) av.getItemAtPosition(position); 
          //by default  connect to right bicep
            //NtDeviceManagement.MUSCLE_GROUP_RIGHT_BICEP
            deviceConnectDialog(listData,NtDeviceManagement.MUSCLE_GROUP_RIGHT_BICEP);
        }
        
    };
    private void deviceConnectDialog(final ListItemData listData,final int iMusclePart ){
       switch (listData.status) {
          case ListItemData.DEVICE_CONNECTED:{
             listDataForDialog=listData;
             final String stMsg=getResources().getString(R.string.confirm_disconnect) + " \"" + listData.devName + "\" ?";
             showDialog(DISCONNECT, R.string.connect_title, stMsg, R.string.yes, R.string.no, R.drawable.disconnect,iMusclePart);
              break;
          }
          
          case ListItemData.DEVICE_DISCONNECTED:
          case ListItemData.DEVICE_PAIRED:
          case ListItemData.DEVICE_UNPAIRED:{
             listDataForDialog=listData;
             final String stMsg=getResources().getString(R.string.confirm_connect) + " \"" + listData.devName + "\" ?";
             showDialog(CONNECT, R.string.connect_title, stMsg, R.string.yes, R.string.no, R.drawable.connect,iMusclePart);
              break;
          }
          case ListItemData.DEVICE_NONE:
              // not relevant
              return;
          }
    }
  
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
          ((SettingsActivity)activity).setDialogClickListener(this);
       
    }
    
    private void showDialog(int dialogId, int titleId, String stMessage, int posId, int negId, int iconId,final int iData){
      
//       if(getActivity() instanceof NeuralBaseActivity){
//          ((NeuralBaseActivity)(getActivity())).showAlertDialog(this, dialogId,
//                titleId, stMessage, posId,
//                negId,iconId);
//       }
       showDialog(titleId, stMessage, posId, negId, iconId, dialogId,iData);
       
       
    }
    void showDialog(int titleId, String stMessage, int posId, int negId, int iconId,int messageId,
          final int iData) {
    DialogFragment newFragment = AlertDialogFragment.newInstance(
          titleId,stMessage,iconId,posId,negId,messageId,iData);
    newFragment.show(getFragmentManager(), AlertDialogFragment.DIALOG_KEY);
    }
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver btDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	bluestatus.setVisibility(View.GONE);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	
                	ListItemData itemData = new ListItemData(ListItemData.DEVICE_UNPAIRED,
                			device.getName(), device.getAddress());

        			btDevicesArrayAdapter.add(itemData);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	
                if (btDevicesArrayAdapter.getCount() == 0) {
                	
                	ListItemData itemData = new ListItemData(ListItemData.DEVICE_NONE, "", "");

                    btDevicesArrayAdapter.add(itemData);
                }
                doDiscovery(isScanning = false);
            }
        }
    };

    @Override
	public void onPause() {
		super.onPause();
		 // setup bluetooth
                getActivity().unregisterReceiver(btDiscoveryReceiver);
		ntDeviceManager.setHandler(null);
	}

    @Override
	public void onResume() {
		super.onResume();
		 // setup bluetooth
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(BluetoothDevice.ACTION_FOUND);
	     // Register for broadcasts when discovery has finished
	        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	        getActivity().registerReceiver(btDiscoveryReceiver, filter);
		ntDeviceManager.setHandler(this);
	}
private void changeBusy(ListItemData data ,final boolean isBusy
      ,final int iStatus){
   if(data != null){
      data.busy = isBusy;
      data.status = iStatus;
   }
   btDevicesArrayAdapter.notifyDataSetChanged();
}
	@Override
	public void handleDeviceEvent(NtDevice.DeviceHandlerParameter param) {

		NtDeviceManagement.ManagedDevice mngDevice = (NtDeviceManagement.ManagedDevice) param.getPayload();
		ListItemData data = (ListItemData) mngDevice.getPayload();		
		NtDevice.DeviceEventHandlerParameter eventParam = null;
		
		switch (param.getEventType()) {
		
		case DeviceHandlerParameter.STATE_CHANGE:
			eventParam = (NtDevice.DeviceEventHandlerParameter) param;
			switch (eventParam.getNewState()) {
	        case Shimmer.STATE_CONNECTED:
	           changeBusy(data, false, ListItemData.DEVICE_CONNECTED);
	            break;

	        case Shimmer.STATE_CONNECTING:
	            Toast.makeText(getActivity(), 
	            		getResources().getString(R.string.connecting) + " " +
	            		mngDevice.getDevice().getDeviceName(), Toast.LENGTH_SHORT).show();
	            break;
	        
	        case Shimmer.STATE_NONE:
	    		if (data != null && data.status == ListItemData.DEVICE_CONNECTED) {
	    		   changeBusy(data, false,  ListItemData.DEVICE_DISCONNECTED);
	    		}
	            break;
			}
			break;
			
		case DeviceHandlerParameter.DEVICE_NAME_UPDATE:
			eventParam = (NtDevice.DeviceEventHandlerParameter) param;
			if (ntDeviceManager.getNtDeviceByName(mngDevice.getDevice().getDeviceName()) == null) {
				Toast.makeText(NtDeviceManagement.getDefaultDeviceManager(null).getContext(), 
						getResources().getString(R.string.no_matching_device) + " " +
								mngDevice.getDevice().getDeviceName(), Toast.LENGTH_SHORT).show();
			}
			break;
			
		case DeviceHandlerParameter.NOTIFICATION:{
			eventParam = (NtDevice.DeviceEventHandlerParameter) param;
			final String stNotification= eventParam.getNotification();
	        Toast.makeText(NtDeviceManagement.getDefaultDeviceManager(null).getContext(),
	              stNotification,
	        		Toast.LENGTH_SHORT).show();
	        if(stNotification.endsWith("is ready for Streaming")){
	           changeBusy(data,false, ListItemData.DEVICE_CONNECTED);
	           mngDevice.getDevice().writeSamplingRate(NtDeviceSettings.DEFAULT_SAMPLING_RATE);
	        }else if(stNotification.equals("Unable to connect device")){
                   changeBusy(data,false, ListItemData.DEVICE_DISCONNECTED);
                }else if(stNotification.equals("Device connection was lost")){
                   changeBusy(data,false, ListItemData.DEVICE_DISCONNECTED);
                }
	        Log.i("test", "eventParam.getNotification():"+eventParam.getNotification());
		}
			break;
			
		case DeviceHandlerParameter.CALIBRATED_DATA:
		case DeviceHandlerParameter.UNCALIBRATED_DATA:
			// do nothing
			break;
		}
	}

   @Override
   public void doPositiveClick(int iMessageCode,final int iMusclePart) {
      switch (iMessageCode) {
         case CONNECT:
            doConnect(listDataForDialog,iMusclePart);
            break;

         case DISCONNECT:
            doDisconnect(listDataForDialog);
            break;
         default:
            break;
      }
     
   }

   @Override
   public void doNegativeClick() {
      
   }
}
