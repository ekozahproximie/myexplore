package com.neural.sensor;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.neural.dsp.smoothing.SignalProcessing;
import com.shimmerresearch.driver.FormatCluster;
import com.shimmerresearch.driver.ObjectCluster;
import com.shimmerresearch.driver.Shimmer;

import java.util.Collection;

public class NtDevice extends Shimmer {
	
	private static final String ACCELOROMETER = "Accelerometer";
	private static final String GYROSCOPE = "Gyroscope";
	private static final String MAGNETOMETER = "Magnetometer";
	private static final String GSR = "GSR";
	private static final String EMG = "EMG";
	private static final String ECG = "ECG";
	private static final String STRAINGAUGE = "StrainGauge";
	private static final String HEARTRATE = "HeartRate";
	private static final String EXPBOARDA0 = "ExpBoardA0";
	private static final String EXPBOARDA7 = "ExpBoardA7";
	private static final String TIMESTAMP = "TimeStamp";
	
	private static final String TOKEN_CALIBRATED = "Calibrated";
	private static final String TOKEN_UNCALIBRATED = "Uncalibrated";
	
	// device handling
	private String deviceName;
	private String deviceAddress;
	private String enabledSensorNames[];
	private NtDeviceSettings settings = null;
	private DeviceHandlerInterface handler = null;
	private boolean isFilterNeed=true;
	
	// data smoothing
	private static final int SIGNAL_PROC_CALIBRATED = 0;
	private static final int SIGNAL_PROC_UNCALIBRATED = 1;
	private static final int NUM_SIGNAL_PROC = 2;
	
	private  SignalProcessing signalProcessor[] = new SignalProcessing[NUM_SIGNAL_PROC];
	
	public static final int EVENT_STATE_NONE = 0;
	public static final int EVENT_STATE_CONNECTING = 1;
	public static final int EVENT_STATE_CONNECTED = 2;
	
	private NtDeviceManagement deviceManagement = null;

	public class DeviceHandlerParameter {
		
		public static final int CALIBRATED_DATA = 0;
		public static final int UNCALIBRATED_DATA = 1;
		public static final int STATE_CHANGE = 2;
		public static final int DEVICE_NAME_UPDATE = 3;
		public static final int NOTIFICATION = 4;
		
		protected NtDevice device = null;
		protected int eventType = 0;
		protected Object payload = null;
		public DeviceHandlerParameter(NtDevice device, int eventType, Object payload) {
			this.device = device;
			this.eventType = eventType;
			this.payload = payload;
		}
		
		public NtDevice getDevice() {
			return device;
		}
		
		public int getEventType() {
			return eventType;
		}
		
		public Object getPayload() {
			return payload;
		}
	}
	
	public class DeviceEventHandlerParameter extends DeviceHandlerParameter {
		
		private int newState = 0;
		private String updatedDeviceName = null;
		private String notification = null;
		
		public DeviceEventHandlerParameter(NtDevice device, int eventType, int newState, 
				String updatedDeviceName, String notification, Object payload) {
			super(device, eventType, payload);
			
			this.newState = newState;
			this.updatedDeviceName = updatedDeviceName;
			this.notification = notification;
		}

		public NtDevice getDevice() {
			return device;
		}

		public int getNewState() {
			return newState;
		}

		public String getUpdatedDeviceName() {
			return updatedDeviceName;
		}

		public String getNotification() {
			return notification;
		}
	}
	
	public class DeviceDataHandlerParameter extends DeviceHandlerParameter {
		
		private String sensorName = null;
		private double data = 0;
		private String units = null;
		
		public DeviceDataHandlerParameter(NtDevice device, int eventType, String sensorName, 
				double data, String units, Object payload) {
			super(device, eventType, payload);

			this.sensorName = sensorName;
			this.data = data;
			this.units = units;
		}

		public NtDevice getDevice() {
			return device;
		}

		public String getSensorName() {
			return sensorName;
		}

		public double getData() {
			return data;
		}

		public String getUnits() {
			return units;
		}
	}
	
	public interface DeviceHandlerInterface {
		public void handleDeviceEvent(DeviceHandlerParameter param);
	}
	
    public NtDevice(Context context, Handler handler, String myName, NtDeviceSettings devSettings) {
    	super(context, handler, myName, devSettings.getSamplingRate(), 
    			devSettings.getAccelRange(), devSettings.getGsrRange(), 
    			devSettings.getEnabledSensors(), devSettings.isContinousSync());
    	
    	settings = devSettings;
    	deviceName = myName;
    	deviceAddress = "";
    	setSensorNames(devSettings);
    	deviceManagement = NtDeviceManagement.getDefaultDeviceManager(context.getApplicationContext());
    	for (int i = 0; i < NUM_SIGNAL_PROC; i ++) {
    		signalProcessor[i] = new SignalProcessing(SignalProcessing.DEFAULT_SMOOTH_WIDTH);
    	}
    }
    long lTime =0; 
    // Message parser
    public synchronized void postEventFromMessage(Message msg, Object payload) {
    	 
    	   
             if(handler != null) {
    		
        	DeviceHandlerParameter param = null;
        	
        	switch (msg.what) {
        	
        	case Shimmer.MESSAGE_STATE_CHANGE:
        		param = new DeviceEventHandlerParameter(this, DeviceHandlerParameter.STATE_CHANGE, msg.arg1, 
        				getBluetoothAddress(), null, payload);
        		handler.handleDeviceEvent(param);
        		break;
        		
        	case Shimmer.MESSAGE_READ:
        	    if ((msg.obj instanceof ObjectCluster) && (enabledSensorNames.length > 0)) {
        	    	harvestDeviceData((ObjectCluster) msg.obj, payload); 
        	    }
        		break;
        		
        	case Shimmer.MESSAGE_DEVICE_NAME:
    			param = new DeviceEventHandlerParameter(this, DeviceHandlerParameter.DEVICE_NAME_UPDATE, 
    					getState(), getBluetoothAddress(), null, payload);
        		handler.handleDeviceEvent(param);
        		break;
        		
        	case Shimmer.MESSAGE_ACK_RECEIVED:
        		break;
        		
        	case Shimmer.MESSAGE_TOAST:
    			param = new DeviceEventHandlerParameter(this, DeviceHandlerParameter.NOTIFICATION, 
    					getState(), getBluetoothAddress(), msg.getData().getString(TOAST), payload);
        		handler.handleDeviceEvent(param);
        		break;
        	case Shimmer. MESSAGE_SAMPLING_RATE_RECEIVED:
        	   param = new DeviceEventHandlerParameter(this, DeviceHandlerParameter.NOTIFICATION, 
                         getState(), getBluetoothAddress(), msg.getData().getString(TOAST), payload);
        	   handler.handleDeviceEvent(param);
        	    break;
        	case Shimmer. MESSAGE_INQUIRY_RESPONSE:
        	   param = new DeviceEventHandlerParameter(this, DeviceHandlerParameter.NOTIFICATION, 
                         getState(), getBluetoothAddress(), msg.getData().getString(TOAST), payload);
        	      handler.handleDeviceEvent(param);
        	   break;
        		    
        	}
    	}    		
    }
    
    
    private int iCount=0;
    private  double dYValue=0;
    
	private void harvestDeviceData(ObjectCluster objectCluster, Object payload) {
	 
	   
	   DeviceDataHandlerParameter param = new DeviceDataHandlerParameter(this, DeviceHandlerParameter.CALIBRATED_DATA, null, 0, null, payload);
		
		for (int i = 0; i < enabledSensorNames.length; i ++) {
			 // first retrieve all the possible formats for the current sensor device
	    	Collection<FormatCluster> ofFormats = objectCluster.mPropertyCluster.get(enabledSensorNames[i]); 
	    	FormatCluster formatCluster = ((FormatCluster)objectCluster.returnFormatCluster(ofFormats, TOKEN_CALIBRATED)); 

	    	if (formatCluster != null) {

	    		final String units = ((FormatCluster)objectCluster.returnFormatCluster(ofFormats, TOKEN_UNCALIBRATED)).mUnits;
	    		
	    		if(! isFilterNeed){
	    		   //For game 
	    		sendRawValue(param, formatCluster, objectCluster, units, enabledSensorNames[i]);
	    		continue;
	    		}
	    		
	    		// Smooth data before passing it to the handler
	    		if ( signalProcessor[SIGNAL_PROC_CALIBRATED].addRawValue(formatCluster.mData, units)) {
	    			
	    			double processedSignal[] = signalProcessor[SIGNAL_PROC_CALIBRATED].computeSmoothValue();
	    			//by ideal it would be 50
	    			final int iDataCount= 1;//(deviceManagement.getTimeBase() * 1024)/ deviceManagement.getGraphWidth();
	    			
	    			
	    			  //Log.i("test", "processedSignal :"+processedSignal.length);
	    			for (int y = 0; y < processedSignal.length; y ++) {
		    			// Obtain calibrated data value that has been smoothed
			 	    	param.eventType = DeviceHandlerParameter.CALIBRATED_DATA;
			 	    	param.sensorName = enabledSensorNames[i];
			 	    	
			 	    	param.data = processedSignal[y]; // calibrated data
			 	    	param.units = units;
			 	    	param.data=signalProcessor[SIGNAL_PROC_CALIBRATED].movingAverage(param.data);
			 	    	
			 	    	if(signalProcessor[SIGNAL_PROC_CALIBRATED].iState != SignalProcessing.STEADYSTATE ){
			 	    	   continue;
			 	    	}
			 	    	
			 	    	handler.handleDeviceEvent(param);
			 	    	
	    			}
	    			signalProcessor[SIGNAL_PROC_CALIBRATED].clear();
	    		}
	 	    	
	    		if (enabledSensorNames[i] != HEARTRATE) {
	    			
		    		// Obtain un-calibrated data that has been smoothed
		    		if (signalProcessor[SIGNAL_PROC_UNCALIBRATED].addRawValue(((FormatCluster)objectCluster.returnFormatCluster
		    				(ofFormats, TOKEN_UNCALIBRATED)).mData, units)) {

		    			double processedSignal[] = signalProcessor[SIGNAL_PROC_CALIBRATED].computeSmoothValue();
		    			for (int y = 0; y < processedSignal.length; y ++) {
			    			param.eventType = DeviceHandlerParameter.UNCALIBRATED_DATA;
				 	    	param.sensorName = enabledSensorNames[i];
				 	    	param.data = processedSignal[y]; // un-calibrated data
				 	    	param.units = units;
				 	    	handler.handleDeviceEvent(param);
		    			}		    			
		    			signalProcessor[SIGNAL_PROC_UNCALIBRATED].clear();
		    		}
	    		}
	 	    }
		}
	}
	
	private void sendRawValue(final DeviceDataHandlerParameter param ,
	      final  FormatCluster formatCluster,final ObjectCluster objectCluster, final String units 
	      ,final String stEnableSensorName){
	
	// Smooth data before passing it to the handler
           if ( signalProcessor[SIGNAL_PROC_CALIBRATED].addRawValue(formatCluster.mData, units)) {
                   
                   double processedSignal[] = signalProcessor[SIGNAL_PROC_CALIBRATED].computeSmoothValue();
                   //by ideal it would be 50
                   final int iDataCount=90;
                   
                   
                     //Log.i("test", "processedSignal :"+processedSignal.length);
                   for (int y = 0; y < processedSignal.length; y ++) {
                           // Obtain calibrated data value that has been smoothed
                           param.eventType = DeviceHandlerParameter.CALIBRATED_DATA;
                           param.sensorName = stEnableSensorName;
                           
                           param.data = processedSignal[y]; // calibrated data
                           param.units = units;
                           
                           if(iCount < iDataCount ){
                              dYValue += param.data;
                              iCount++;
                             continue;
                           }else{
                              param.data=(dYValue/iDataCount);
                              iCount=0;
                              dYValue=0;
                              
                           }
                           handler.handleDeviceEvent(param);
                           
                   }
                   signalProcessor[SIGNAL_PROC_CALIBRATED].clear();
           }
           
	}
	
	public SignalProcessing SignalProcessor(int type) {
		if ((type >= 0) && (type <= NUM_SIGNAL_PROC)) {
			return signalProcessor[type];
		}
		return null;
	}

	public String getDeviceName() {
		return deviceName;
	}
	
	public NtDeviceSettings getSettings() {
		return settings;
	}
	
    public String getDeviceAddress() {
		return deviceAddress;
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}
	
	public void setDeviceSettings(NtDeviceSettings devSettings) {
		this.settings = devSettings;
		setSensorNames(devSettings);
	}
	
	private void setSensorNames(NtDeviceSettings devSettings) {
		int availableSensors[] = { NtDeviceSettings.SENSOR_ACCELOROMETER, NtDeviceSettings.SENSOR_GYROSCOPE, 
				NtDeviceSettings.SENSOR_MAGNETOMETER, NtDeviceSettings.SENSOR_ECG, NtDeviceSettings.SENSOR_EMG, 
				NtDeviceSettings.SENSOR_GSR, NtDeviceSettings.SENSOR_EXP_BOARD_A7, 
				NtDeviceSettings.SENSOR_EXP_BOARD_A0, NtDeviceSettings.SENSOR_STRAIN_GAUGE, 
				NtDeviceSettings.SENSOR_HEART_RATE, NtDeviceSettings.SENSOR_TIMESTAMP 
		};
		String sensorNames[] = { ACCELOROMETER, GYROSCOPE, MAGNETOMETER, ECG, EMG, GSR, EXPBOARDA7, 
				EXPBOARDA0, STRAINGAUGE, HEARTRATE, TIMESTAMP
		};
		
		int howManySensors = 0;
		int enabledSensors = devSettings.getEnabledSensors();
		for (int i = 0; i < availableSensors.length; i ++) {
			if ((availableSensors[i] & enabledSensors) > 0) {
				howManySensors ++;
			}
		}
		
    	enabledSensorNames = new String[0];
		if (howManySensors > 0) {
			enabledSensorNames = new String[howManySensors];
			for (int i = 0, y = 0; i < availableSensors.length; i ++) {
				if ((availableSensors[i] & enabledSensors) > 0) {
					enabledSensorNames[y++] = sensorNames[i];
				}
			}
		}
	}
	
	public synchronized void setHandler(DeviceHandlerInterface handler) {
		this.handler = handler;
		clear();
	}
	public synchronized void setHandler(DeviceHandlerInterface handler,final boolean isFilterNeed) {
           this.handler = handler;
           this.isFilterNeed=isFilterNeed;
           clear();
	}
	private void clear(){
	   for (int i = 0; i < NUM_SIGNAL_PROC; i ++) {
              signalProcessor[i].clear();
	   }
	}
}
