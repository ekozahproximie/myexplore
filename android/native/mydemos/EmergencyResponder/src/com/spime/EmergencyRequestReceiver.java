package com.spime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class EmergencyRequestReceiver extends BroadcastReceiver {
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	@Override
	public void onReceive(Context context, Intent _intent) {
		if (_intent.getAction().equals(SMS_RECEIVED)) {
			String queryString = context.getString(R.string.querystring);
			Bundle bundle = _intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++)
					messages[i] = SmsMessage
							.createFromPdu((byte[]) pdus[i]);
				for (SmsMessage message : messages) {
					if (message.getMessageBody().toLowerCase()
							.contains(queryString)) {
						Bundle bundle2= new Bundle();
					if(	EmergencyResponder.isAlreadyInvoked){
						return;
					}
						if(EmergencyResponder.isAlreadyInvoked== false){
							EmergencyResponder.isAlreadyInvoked=true;
						}
						
						Log.i("SPIME", "EmergencyRequestReceiver invoked");
						bundle2.putString("phno", message.getOriginatingAddress());
						Intent intent= new Intent(context,EmergencyResponder.class);
						intent.putExtra("msg", bundle2);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent); 

					}
				}
			}
		}

	}

}
