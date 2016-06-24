package com.android.test;





import android.app.Activity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;


public class PhoneStateActivity extends Activity {

    private static final String APP_NAME = "PhoneState";

    private static final int EXCELLENT_LEVEL = 75;

    private static final int GOOD_LEVEL = 50;

    private static final int MODERATE_LEVEL = 25;

    private static final int WEAK_LEVEL = 0;

    private static final int INFO_SERVICE_STATE_INDEX = 0;

    private static final int INFO_CELL_LOCATION_INDEX = 1;

    private static final int INFO_CALL_STATE_INDEX = 2;

    private static final int INFO_CONNECTION_STATE_INDEX = 3;

    private static final int INFO_SIGNAL_LEVEL_INDEX = 4;

    private static final int INFO_SIGNAL_LEVEL_INFO_INDEX = 5;

    private static final int INFO_DATA_DIRECTION_INDEX = 6;

    private static final int INFO_DEVICE_INFO_INDEX = 7;

    private static final int[] info_ids = {
            R.id.serviceState_info, R.id.cellLocation_info, R.id.callState_info,
            R.id.connectionState_info, R.id.signalLevel, R.id.signalLevelInfo, R.id.dataDirection,
            R.id.device_info
    };
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone);
     
         startSignalLevelListener();
         displayTelephonyInfo();
    }

   
    @Override
    protected void onPause() {
        super.onPause();
        
        //stopListening();
    }
  
    @Override
    protected void onResume() {
        super.onResume();
     
        
        startSignalLevelListener();
    }

    @Override
    protected void onDestroy() {
        stopListening();

        super.onDestroy();
    }

    private void setTextViewText(int id, String text) {
        ((TextView)findViewById(id)).append(text);
    }

    private void setSignalLevel(int id, int infoid, int level) {
        int progress = (int)((((float)level) / 31.0) * 100);
        String signalLevelString = getSignalLevelString(progress);

        ((ProgressBar)findViewById(id)).setProgress(progress);
        ((TextView)findViewById(infoid)).setText(signalLevelString);
        
        Log.i("signalLevel ", "" + progress);
    }

    private String getSignalLevelString(int level) {
        String signalLevelString = "Weak";

        if (level > EXCELLENT_LEVEL)
            signalLevelString = "Excellent";
        else if (level > GOOD_LEVEL)
            signalLevelString = "Good";
        else if (level > MODERATE_LEVEL)
            signalLevelString = "Moderate";
        else if (level > WEAK_LEVEL)
            signalLevelString = "Weak";

        return signalLevelString;
    }

    private void stopListening() {
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        
    }

    private void setDataDirection(int id, int direction) {
        int resid = getDataDirectionRes(direction);

        ((ImageView)findViewById(id)).setImageResource(resid);
    }

    private int getDataDirectionRes(int direction) {
        int resid = R.drawable.data_none;

        switch (direction) {
            case TelephonyManager.DATA_ACTIVITY_IN:
                resid = R.drawable.data_in;
                break;
            case TelephonyManager.DATA_ACTIVITY_OUT:
                resid = R.drawable.data_out;
                break;
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                resid = R.drawable.data_both;
                break;
            case TelephonyManager.DATA_ACTIVITY_NONE:
                resid = R.drawable.data_none;
                break;
            default:
                resid = R.drawable.data_none;
                break;
        }

        return resid;
    }
    int m_iMcc =0;
    int m_iMnc =0;
    private void startSignalLevelListener() {
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        int events = PhoneStateListener.LISTEN_SIGNAL_STRENGTH
                | PhoneStateListener.LISTEN_DATA_ACTIVITY | PhoneStateListener.LISTEN_CELL_LOCATION
                | PhoneStateListener.LISTEN_CALL_STATE
                | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
                | PhoneStateListener.LISTEN_SERVICE_STATE;

        tm.listen(phoneStateListener, events);
        
    }
    private boolean isSIMEnabled() {
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        int state = tm.getSimState();
        boolean isSIMStateEnabled = false;
        switch (state) {

            case android.telephony.TelephonyManager.SIM_STATE_READY: {
                logString("SIMEnabled is enabled on the SET: " + state);
                isSIMStateEnabled=true;
                break;
            }
            case android.telephony.TelephonyManager.SIM_STATE_UNKNOWN:
            case android.telephony.TelephonyManager.SIM_STATE_ABSENT:
            case android.telephony.TelephonyManager.SIM_STATE_PIN_REQUIRED:
            case android.telephony.TelephonyManager.SIM_STATE_PUK_REQUIRED:
            case android.telephony.TelephonyManager.SIM_STATE_NETWORK_LOCKED: {

                logString("SIMEnabled is not enabled on the SET: " + state);
                break;
            }

            default:
                break;
        }
        return isSIMStateEnabled;
    }
    private void displayTelephonyInfo() {
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        GsmCellLocation loc = (GsmCellLocation)tm.getCellLocation();

        int cellid = 0;
        int lac = 0;
        if(loc != null){
        cellid = loc.getCid();
         lac = loc.getLac();
        }
        String networkOperator = tm.getNetworkOperator();
        String deviceid = tm.getDeviceId();
        String phonenumber = tm.getLine1Number();
        String softwareversion = tm.getDeviceSoftwareVersion();
        String operatorname = tm.getNetworkOperatorName();
        String simcountrycode = tm.getSimCountryIso();
        String simoperator = tm.getSimOperatorName();
        String simserialno = tm.getSimSerialNumber();
        String subscriberid = tm.getSubscriberId();
        String networktype = getNetworkTypeString(tm.getNetworkType());
        String phonetype = getPhoneTypeString(tm.getPhoneType());
        networkOperator = tm.getNetworkOperator();
        if (networkOperator != null && networkOperator.length() > 0) {
            try {
                 m_iMcc = Integer.parseInt(networkOperator.substring(0, 3));
                 m_iMnc = Integer.parseInt(networkOperator.substring(3));
                logString("MCC: " + m_iMcc);
                logString("MNC: " + m_iMnc);
            } catch (NumberFormatException e) {
            }
        }else
        {
            Log.i(APP_NAME, "NULL Value received for network operator");
        }
        logString("CellID: " + cellid);
        logString("LAC: " + lac);
        logString("Device ID: " + deviceid);
        logString("Phone Number: " + phonenumber);
        logString("Software Version: " + softwareversion);
        logString("Operator Name: " + operatorname);
        logString("SIM isEnabled: " + isSIMEnabled());
        logString("SIM Country Code: " + simcountrycode);
        logString("SIM Operator: " + simoperator);
        logString("SIM Serial No.: " + simserialno);
        logString("Sibscriber ID: " + subscriberid);

        String deviceinfo = "";
        NetInfo ninfo = new NetInfo(this);
        deviceinfo += ("CellID: " + cellid + "\n");
        deviceinfo += ("LAC: " + lac + "\n");
        deviceinfo += ("Device ID: " + deviceid + "\n");
        deviceinfo += ("Phone Number: " + phonenumber + "\n");
        deviceinfo += ("NetworkOperator: " + networkOperator + "\n");
        deviceinfo += ("MCC: " + m_iMcc + "\n");
        deviceinfo += ("MNC: " + m_iMnc + "\n");
        deviceinfo += ("Software Version: " + softwareversion + "\n");
        deviceinfo += ("Operator Name: " + operatorname + "\n");
        deviceinfo += ("SIM isEnabled: " + isSIMEnabled() + "\n");
        deviceinfo += ("SIM Country Code: " + simcountrycode + "\n");
        deviceinfo += ("SIM Operator: " + simoperator + "\n");
        deviceinfo += ("SIM Serial No.: " + simserialno + "\n");
        deviceinfo += ("Subscriber ID: " + subscriberid + "\n");
        deviceinfo += ("Network Type: " + networktype + "\n");
        deviceinfo += ("Phone Type: " + phonetype + "\n");
        deviceinfo += ("WIFI : " + ninfo.getWiFiMACAddress() + "\n");
        
        List<NeighboringCellInfo> cellinfo = tm.getNeighboringCellInfo();

        if (null != cellinfo) {
            for (NeighboringCellInfo info : cellinfo) {
                deviceinfo += ("\tCellID: " + info.getCid() + ", RSSI: " + info.getRssi() + "\n");
            }
        }

        setTextViewText(info_ids[INFO_DEVICE_INFO_INDEX], deviceinfo);
    }

    private String getNetworkTypeString(int type) {
        String typeString = "Unknown";

        switch (type) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
                typeString = "EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                typeString = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                typeString = "UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                typeString = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                typeString = "UNKNOWN";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                typeString = "1xRTT";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                typeString = "EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                typeString = "EVDO_A";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                typeString = "HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                typeString = "HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                typeString = "HSUPA";
                break;

            default:
                typeString = "NOT RETERIVE";
                break;
        }

        return typeString;
    }

    private String getPhoneTypeString(int type) {
        String typeString = "Unknown";

        switch (type) {
            case TelephonyManager.PHONE_TYPE_GSM:
                typeString = "GSM";
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                typeString = "CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                typeString = "UNKNOWN";
                break;
            default:
                typeString = "NOT RETERIVE";
                break;
        }

        return typeString;
    }

    private int logString(String message) {
        return Log.i(APP_NAME, message);
    }

    private final PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallForwardingIndicatorChanged(boolean cfi) {
            Log.i(APP_NAME, "onCallForwardingIndicatorChanged " + cfi);

            super.onCallForwardingIndicatorChanged(cfi);
        }
      
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            String callState = "UNKNOWN";

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    callState = "IDLE";
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    callState = "Ringing (" + incomingNumber + ")";
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    callState = "Offhook";
                    break;
            }

            setTextViewText(info_ids[INFO_CALL_STATE_INDEX], callState);

            Log.i(APP_NAME, "onCallStateChanged " + callState);

            super.onCallStateChanged(state, incomingNumber);
        }
        @Override
        public void onCellInfoChanged(java.util.List<android.telephony.CellInfo> cellInfo) {
           StringBuilder builder = new StringBuilder();
           Log.i(APP_NAME, "onCellInfoChanged " + cellInfo);
           
           builder.append("CellInfo: " +cellInfo);
           for (CellInfo cellInfo2 : cellInfo) {
              Log.i(APP_NAME, "CellInfo of " + cellInfo2.toString());
              builder.append(cellInfo2.toString());
           
           }
           setTextViewText(info_ids[INFO_DEVICE_INFO_INDEX], builder.toString());
           super.onCellInfoChanged(cellInfo);
           
        };
        @Override
        public void onCellLocationChanged(CellLocation location) {
            String locationString = location.toString();

            setTextViewText(info_ids[INFO_CELL_LOCATION_INDEX], locationString);

            Log.i(APP_NAME, "onCellLocationChanged " + locationString);

            super.onCellLocationChanged(location);
        }

        @Override
        public void onDataActivity(int direction) {
            String directionString = "none";

            switch (direction) {
                case TelephonyManager.DATA_ACTIVITY_IN:
                    directionString = "IN";
                    break;
                case TelephonyManager.DATA_ACTIVITY_OUT:
                    directionString = "OUT";
                    break;
                case TelephonyManager.DATA_ACTIVITY_INOUT:
                    directionString = "INOUT";
                    break;
                case TelephonyManager.DATA_ACTIVITY_NONE:
                    directionString = "NONE";
                    break;
                default:
                    directionString = "UNKNOWN: " + direction;
                    break;
            }

            setDataDirection(info_ids[INFO_DATA_DIRECTION_INDEX], direction);

            Log.i(APP_NAME, "onDataActivity " + directionString);

            super.onDataActivity(direction);
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            String connectionState = "Unknown";

            switch (state) {
                case TelephonyManager.DATA_CONNECTED:
                    connectionState = "Connected";
                    break;
                case TelephonyManager.DATA_CONNECTING:
                    connectionState = "Connecting";
                    break;
                case TelephonyManager.DATA_DISCONNECTED:
                    connectionState = "Disconnected";
                    break;
                case TelephonyManager.DATA_SUSPENDED:
                    connectionState = "Suspended";
                    break;
                default:
                    connectionState = "Unknown: " + state;
                    break;
            }

            setTextViewText(info_ids[INFO_CONNECTION_STATE_INDEX], connectionState);

            Log.i(APP_NAME, "onDataConnectionStateChanged " + connectionState);

            super.onDataConnectionStateChanged(state);
        }

        @Override
        public void onMessageWaitingIndicatorChanged(boolean mwi) {
            Log.i(APP_NAME, "onMessageWaitingIndicatorChanged " + mwi);

            super.onMessageWaitingIndicatorChanged(mwi);
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            String serviceStateString = "UNKNOWN";

            switch (serviceState.getState()) {
                case ServiceState.STATE_IN_SERVICE:
                    serviceStateString = "IN SERVICE";
                    break;
                case ServiceState.STATE_EMERGENCY_ONLY:
                    serviceStateString = "EMERGENCY ONLY";
                    break;
                case ServiceState.STATE_OUT_OF_SERVICE:
                    serviceStateString = "OUT OF SERVICE";
                    break;
                case ServiceState.STATE_POWER_OFF:
                    serviceStateString = "POWER OFF";
                    break;
                default:
                    serviceStateString = "UNKNOWN";
                    break;
            }

            setTextViewText(info_ids[INFO_SERVICE_STATE_INDEX], serviceStateString);

            Log.i(APP_NAME, "onServiceStateChanged " + serviceStateString);
            
            super.onServiceStateChanged(serviceState);
        }

        @Override
        public void onSignalStrengthChanged(int asu) {
            Log.i(APP_NAME, "onSignalStrengthChanged " + asu);
          
            setSignalLevel(info_ids[INFO_SIGNAL_LEVEL_INDEX],
                    info_ids[INFO_SIGNAL_LEVEL_INFO_INDEX], asu);

            super.onSignalStrengthChanged(asu);
        }
    };
}
