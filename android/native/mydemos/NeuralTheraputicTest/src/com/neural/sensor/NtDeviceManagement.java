package com.neural.sensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.neural.dsp.smoothing.SignalProcessing;
import com.neural.sensor.NtDevice.DeviceHandlerInterface;
import com.shimmerresearch.driver.Shimmer;

import java.util.Set;

public final class NtDeviceManagement {

	// sensor placement in the body
	public static final int MUSCLE_GROUP_LEFT_BICEP = 0;
	public static final int MUSCLE_GROUP_RIGHT_BICEP = 1;
	public static final int MUSCLE_GROUP_LEFT_TRICEP = 2;
	public static final int MUSCLE_GROUP_RIGHT_TRICEP = 3;
	public static final int MUSCLE_GROUP_DEMO         = 4;

	public static final int MAX_MUSCLE_GROUP = 5;
	
	private ManagedDevice managedDevices[];
	
	private NtDeviceSettings devSettings = null;
	private NtSensorGraphAttributes graphAttributes = null;
	
	private Context context = null;	
    private BluetoothAdapter blueToothAdapter = null;	
    private static NtDeviceManagement defaultDeviceManager = null;
    
    private int iGraphWidth = 0;
    private  double dTimeBase = 0;
    private static final String TAG=NtDeviceManagement.class.getSimpleName();
    
	public static class ManagedDevice extends Handler {
		
		private NtDevice device = null;
        private Object payload = null;
        private int muscleGroup = MUSCLE_GROUP_LEFT_BICEP;
        private NtSensorData historicalData;

        public ManagedDevice(Context context, String deviceName, NtDeviceSettings devSettings, 
        		NtSensorGraphAttributes graphAttributes, int muscleGroup, Object payload) {
        	
        	this.payload = payload;
        	this.muscleGroup = muscleGroup;
        	
        	device = new NtDevice(context, this, deviceName, devSettings);
        	historicalData = new NtSensorData(graphAttributes);
        }
       
        @Override
        public void handleMessage(Message msg) {
        	device.postEventFromMessage(msg, this);
        }
        
        public NtDevice getDevice() {
        	return device;
        }

        public void setPayload(Object payload) {
        	this.payload = payload;
        }
        
        public Object getPayload() {
        	return payload;
        }
        
        public void setMuscleGroup(int muscleGroup) {
        	this.muscleGroup = muscleGroup;
        }
        
        public int getMuscleGroup() {
        	return muscleGroup;
        }

		public NtSensorData getHistoricalData() {
			return historicalData;
		}
    }
	
	private NtDeviceManagement(Context context) {
		
		this.context = context;
		
		managedDevices = new ManagedDevice[MAX_MUSCLE_GROUP];
		devSettings = new NtDeviceSettings();
		graphAttributes = new NtSensorGraphAttributes();

		blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public static synchronized NtDeviceManagement getDefaultDeviceManager(Context context) {
		if (context != null) {
			if (defaultDeviceManager == null) {
				defaultDeviceManager = new NtDeviceManagement(context);
			}
		}
		return defaultDeviceManager;
	}

	public void removeStaleDevices() {
		
        Set<BluetoothDevice> pairedDevices = blueToothAdapter.getBondedDevices();
        
        if (pairedDevices.size() > 0) {
        	boolean found = false;
        	
        	for (int muscleGroup = 0; muscleGroup < MAX_MUSCLE_GROUP; muscleGroup ++) {
        		
    			ManagedDevice mngDevice = managedDevices[muscleGroup];
    			if (mngDevice != null) {
    				
        			NtDevice ntDevice = mngDevice.getDevice();
                    for (BluetoothDevice btDevice : pairedDevices) {
                    	
                    	if (btDevice.getAddress().equalsIgnoreCase(ntDevice.getDeviceAddress())) {
                    		found = true;
                    		break;
                    	}
                    }
                    if (! found) {
                    	removeDevice(muscleGroup);
                    	muscleGroup --; // adjust from disconnection
                    }
    			}
        	}
        }
        else {
        	disconnectAll();
        }
	}
	
	private void removeDevice(int muscleGroup) {

		// re-positioning required
		ManagedDevice mngDevice = managedDevices[muscleGroup];
		if (mngDevice != null) {
			
			NtDevice ntDevice = mngDevice.getDevice();
			
			if (ntDevice.getState() == Shimmer.STATE_CONNECTED) {
				
				// stop streaming
				if (ntDevice.getStreamingStatus() == true) {
					ntDevice.stopStreaming();
					waitForInstructionToComplete(ntDevice);
					ntDevice.setHandler(null);
				}
				
				// disconnect
				ntDevice.stop();
				waitForInstructionToComplete(ntDevice);
				ntDevice.setHandler(null);
				
				// remove and re-add the current device
				managedDevices[muscleGroup] = null;
			}
		}
	}
	public void removeDemoDevice() {

           // re-positioning required
           ManagedDevice mngDevice = managedDevices[MUSCLE_GROUP_DEMO];
           if (mngDevice != null) {
                      // remove for demo data sensor
                     managedDevices[MUSCLE_GROUP_DEMO] = null;
                   
           }
     }
	public boolean connect(String devName, String devAddress, DeviceHandlerInterface handler, 
			int newPosition, Object payload) {
		
		boolean success = true;
		NtDevice ntDevice = null;
		ManagedDevice mngDevice = null;
		
		int currentPosition = getNtDeviceMuscleGroup(devAddress);
		if (currentPosition >= 0) {
			
			if (currentPosition != newPosition) {
				// re-position existing device (deprecate the target position)
				removeDevice(newPosition);

				mngDevice = managedDevices[currentPosition];
				managedDevices[newPosition] = mngDevice;
				managedDevices[currentPosition] = null;
				
				// update the position
				currentPosition = newPosition;
				mngDevice.setMuscleGroup(currentPosition);
				
				// update the rest
				mngDevice.setPayload(payload);
				mngDevice.getDevice().setHandler(handler);
			}
			
			ntDevice = managedDevices[currentPosition].getDevice();
			try{
	                final BluetoothDevice btDevice = blueToothAdapter.getRemoteDevice(ntDevice.getDeviceAddress());
			if (ntDevice.getState() != Shimmer.STATE_CONNECTED) {
				
				// connect the device if it's not already connected
				ntDevice.setHandler(handler);
				ntDevice.connect(btDevice); //address is just a string name, any name can be used
				ntDevice.setgetdatainstruction("a");
			}
			}catch(IllegalArgumentException e){
			   Log.e(TAG, e.getMessage(),e);
			}
			
		}
		else if (newPosition >= 0 && newPosition < MAX_MUSCLE_GROUP) {
			// adjust graph attributes 
			graphAttributes.setSpeed(SignalProcessing.DEFAULT_SMOOTH_WIDTH);
			
			// sanity check on newPosition
			mngDevice = new ManagedDevice(context, devName, devSettings, graphAttributes, newPosition, payload);
			ntDevice = mngDevice.getDevice();
			ntDevice.setDeviceAddress(devAddress);
			try{
	                 final BluetoothDevice btDevice = blueToothAdapter.getRemoteDevice(devAddress);
	                 
			ntDevice.setHandler(handler);
			
			ntDevice.connect(btDevice); //address is just a string name, any name can be used
			}catch(IllegalArgumentException e){
			   Log.e(TAG, e.getMessage(),e);
			}
			ntDevice.setgetdatainstruction("a");
			managedDevices[newPosition] = mngDevice;
		}
		else {
			success = false;
		}
		
		return success;
	}
	
	public void disconnect(String devAddress) {
		int position = getNtDeviceMuscleGroup(devAddress);
		disconnect(position);
	}
	
	public void disconnect(int position) {
		if (position >= 0 && position < MAX_MUSCLE_GROUP) {
			removeDevice(position);
		}
	}
	
	public void disconnectAll() {
		for (int muscleGroup = 0; muscleGroup < MAX_MUSCLE_GROUP; muscleGroup ++) {
			removeDevice(muscleGroup);
		}
	}
	
	public boolean startStreaming(String devAddress, DeviceHandlerInterface dataHandler) {
		
		boolean success = false;
		NtDevice ntDevice = getNtDeviceByMuscleGroup(getNtDeviceMuscleGroup(devAddress));
		if (ntDevice != null) {
			if (ntDevice.getState() == Shimmer.STATE_CONNECTED) {
				if (ntDevice.getStreamingStatus() == false) {
					
					// set the handler
					ntDevice.setHandler(dataHandler);

					// enable sensors
					devSettings.resetSensors();
					devSettings.enableSensor(NtDevice.SENSOR_EMG);
					ntDevice.setDeviceSettings(devSettings);
					
					// write to the device
					ntDevice.writeEnabledSensors(devSettings.getEnabledSensors());
					waitForInstructionToComplete(ntDevice);
					
					ntDevice.startStreaming();
					waitForInstructionToComplete(ntDevice);
				}
				success = true;
			}
		}
		
		return success;
	}
	
	public boolean startStreamingAll(DeviceHandlerInterface dataHandler) {
		
		for (int muscleGroup = 0; muscleGroup < MAX_MUSCLE_GROUP; muscleGroup ++) {

			ManagedDevice mngDevice = managedDevices[muscleGroup];
			if (mngDevice != null) {
				NtDevice ntDevice = mngDevice.getDevice();
				Log.i("test", "startStreamingAll devName:"+ntDevice.getDeviceName());
				if (ntDevice.getState() == Shimmer.STATE_CONNECTED) {
					if (ntDevice.getStreamingStatus() == false) {
						
						// set the handler
						ntDevice.setHandler(dataHandler);

						// enable sensors
						devSettings.resetSensors();
						devSettings.enableSensor(NtDevice.SENSOR_EMG);
						ntDevice.setDeviceSettings(devSettings);
						
						// write to the device
						ntDevice.writeEnabledSensors(devSettings.getEnabledSensors());
						waitForInstructionToComplete(ntDevice);
						
						ntDevice.startStreaming();
						Log.i("test", "startStreaming devName:"+ntDevice.getDeviceName());
						waitForInstructionToComplete(ntDevice);
					}else{
					   Log.i("test", "not connected devName:"+ntDevice.getDeviceName());
					}
				}
			}
		}
		return false;
	}
	public boolean startStreamingAll(DeviceHandlerInterface dataHandler,final boolean isNeedFilter) {
           
           for (int muscleGroup = 0; muscleGroup < MAX_MUSCLE_GROUP; muscleGroup ++) {

                   ManagedDevice mngDevice = managedDevices[muscleGroup];
                   if (mngDevice != null) {
                           NtDevice ntDevice = mngDevice.getDevice();
                           Log.i("test", "startStreamingAll devName:"+ntDevice.getDeviceName());
                           if (ntDevice.getState() == Shimmer.STATE_CONNECTED) {
                                   if (ntDevice.getStreamingStatus() == false) {
                                           
                                           // set the handler
                                           ntDevice.setHandler(dataHandler,isNeedFilter);

                                           // enable sensors
                                           devSettings.resetSensors();
                                           devSettings.enableSensor(NtDevice.SENSOR_EMG);
                                           ntDevice.setDeviceSettings(devSettings);
                                           
                                           // write to the device
                                           ntDevice.writeEnabledSensors(devSettings.getEnabledSensors());
                                           waitForInstructionToComplete(ntDevice);
                                           
                                           ntDevice.startStreaming();
                                           Log.i("test", "startStreaming devName:"+ntDevice.getDeviceName());
                                           waitForInstructionToComplete(ntDevice);
                                   }else{
                                      Log.i("test", "not connected devName:"+ntDevice.getDeviceName());
                                   }
                           }
                   }
           }
           return false;
   }
	public void stopStreaming(String devAddress) {
		
		int muscleGroup = getNtDeviceMuscleGroup(devAddress);
		NtDevice ntDevice = getNtDeviceByMuscleGroup(muscleGroup);
		if (ntDevice != null) {
			
			if (ntDevice.getStreamingStatus() == true) {
				ntDevice.stopStreaming();
				waitForInstructionToComplete(ntDevice);
				ntDevice.setHandler(null);
				
				ManagedDevice mngDevice = managedDevices[muscleGroup];
				mngDevice.getHistoricalData().resetXValues();
			}
		}
	}
	
	public void stopStreamingAll() {
		
		for (int muscleGroup = 0; muscleGroup < MAX_MUSCLE_GROUP; muscleGroup ++) {

			ManagedDevice mngDevice = managedDevices[muscleGroup];
			if (mngDevice != null) {
				NtDevice ntDevice = mngDevice.getDevice();
				
				if (ntDevice.getStreamingStatus() == true) {
					
					ntDevice.stopStreaming();
					waitForInstructionToComplete(ntDevice);
					ntDevice.setHandler(null);
					mngDevice.getHistoricalData().resetXValues();
				}
			}
		}
	}
	
	
	public void setHandler(DeviceHandlerInterface handler) {
		for (int muscleGroup = 0; muscleGroup < MAX_MUSCLE_GROUP; muscleGroup ++) {
			ManagedDevice mngDevice = managedDevices[muscleGroup];
			if (mngDevice != null) {
				mngDevice.getDevice().setHandler(handler);
			}
		}
	}

	public NtDeviceSettings getDevSettings() {
		return devSettings;
	}

	public void setDevSettings(NtDeviceSettings devSettings) {
		this.devSettings = devSettings;
	}

	public NtDevice getNtDeviceByAddress(String devAddress) {
		return getNtDeviceByMuscleGroup(getNtDeviceMuscleGroup(devAddress));
	}

	public Context getContext() {
		return context;
	}
	
	public NtDevice getNtDeviceByName(String devName) {
		for (int muscleGroup = 0; muscleGroup < managedDevices.length; muscleGroup ++) {
			ManagedDevice mngDevice = managedDevices[muscleGroup];
			if (mngDevice != null) {
				NtDevice ntDevice = mngDevice.getDevice();
				if (ntDevice.getDeviceName() != null &&
				      ntDevice.getDeviceName().equals(devName)) {
					return ntDevice;
				}
			}
		}
		return null;
	}
	
	public NtDevice getNtDeviceByMuscleGroup(int muscleGroup) {
		if (muscleGroup >= 0 && muscleGroup < MAX_MUSCLE_GROUP) {
			ManagedDevice mngDevice = managedDevices[muscleGroup];
			if (mngDevice != null) {
				return mngDevice.getDevice();
			}
		}
		return null;
	}
	
	public ManagedDevice getManagedDeviceByMuscleGroup(int muscleGroup) {
		if (muscleGroup >= 0 && muscleGroup < MAX_MUSCLE_GROUP) {
			ManagedDevice mngDevice = managedDevices[muscleGroup];
			if (mngDevice != null) {
				return mngDevice;
			}
		}
		return null;
	}
	

        public void clearAllManagedDeviceHistoryData() {
           if(managedDevices != null ){
                   for (final ManagedDevice managedDevice : managedDevices) {
                     if(managedDevice != null){
                        managedDevice.historicalData.clear();
                     }
                  }
           }
                        
        }
        
	private int getNtDeviceMuscleGroup(String devAddress) {
		for (int muscleGroup = 0; muscleGroup < managedDevices.length; muscleGroup ++) {
			ManagedDevice mngDevice = managedDevices[muscleGroup];
			if (mngDevice != null) {
				NtDevice ntDevice = mngDevice.getDevice();
				if (ntDevice.getDeviceAddress().equals(devAddress)) {
					return muscleGroup;
				}
			}
		}
		return -1;
	}
	
	private void waitForInstructionToComplete(NtDevice ntDevice) {
		while (ntDevice.getInstructionStatus()==false){};
	}
	
      /**
       * @param iGraphWidth the iGraphWidth to set
       */
      public void setGraphWidth(int iGraphWidth) {
         this.iGraphWidth = iGraphWidth;
      }
      
      /**
       * @return the iGraphWidth
       */
      public int getGraphWidth() {
         return iGraphWidth;
      }
      
      /**
       * @param dTimeBase the dTimeBase to set
       */
      public void setTimeBase(double dTimeBase) {
         this.dTimeBase = dTimeBase;
      }
      
      public int getTimeBase(){
        
         return (int) dTimeBase;
         
      }
}
