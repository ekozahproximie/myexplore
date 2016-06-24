package com.spime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
public class SendSMS extends Activity implements Runnable , OnInitListener {
	private static final String[] PEOPLE_PROJECTION = new String[] {
			Contacts.People._ID, Contacts.People.PRIMARY_PHONE_ID,
			Contacts.People.TYPE, Contacts.People.NUMBER,
			Contacts.People.LABEL, Contacts.People.NAME };

	// XXX compiler bug in javac 1.5.0_07-164, we need to implement Filterable
	// to make compilation work
	private static SendSMS me=null;
	String stPhoneNumber =null;
	String stMsg ="";
	static EditText edSendMsg=null;
	private TextToSpeech tts;
	WebView webview;
	public static class ContactListAdapter extends CursorAdapter implements
			Filterable {
		public ContactListAdapter(Context context, Cursor c) {
			super(context, c);
			mContent = context.getContentResolver();
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final LayoutInflater inflater = LayoutInflater.from(context);
			final TextView view = (TextView) inflater.inflate(
					android.R.layout.simple_dropdown_item_1line, parent, false);
			view.setText(cursor.getString(5));
			Log.i("Senthil", "newView");
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((TextView) view).setText(cursor.getString(5));
			Log.i("Senthil", "bindView");
		}

		@Override
		public String convertToString(Cursor cursor) {
			Log.i("Senthil", "convertToString");
			String stData=cursor.getString(0);
			Uri myPerson = Uri.withAppendedPath(People.CONTENT_URI, stData);
			String[] projection = new String[] { People.NAME, People.NUMBER };
			// Then query for this specific record:
			Uri phonesUri = Uri.withAppendedPath(myPerson, People.Phones.CONTENT_DIRECTORY);
			Cursor cur = me.managedQuery(phonesUri, projection, null, null, null);
			cur.moveToFirst();
			 stData=getColumnData(cur);
			if(stData == null){
				stData= cursor.getString(5);
			}
			return stData;
		}

		private String getColumnData(Cursor cur) {
			String phoneNumber =null;
			if (true) {
			
				
				int phoneColumn = cur.getColumnIndex(People.NUMBER);
				String imagePath;
				if( phoneColumn == -1){
					return null;
				}
				do {
					try{
					// Get the field values
					
					phoneNumber = cur.getString(phoneColumn);
					
					if(phoneNumber != null){
					System.out.println(phoneNumber);
					}
					}catch(android.database.CursorIndexOutOfBoundsException e){
						e.printStackTrace();
					}
					// Do something
																// with the
																// values. ...
				} while (cur.moveToNext());
			}
			return phoneNumber;
		}
		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (getFilterQueryProvider() != null) {
				return getFilterQueryProvider().runQuery(constraint);
			}
			StringBuilder buffer = null;
			String[] args = null;
			if (constraint != null) {
				buffer = new StringBuilder();
				buffer.append("UPPER(");
				buffer.append(Contacts.ContactMethods.NAME);
				buffer.append(") GLOB ?");
				args = new String[] { constraint.toString().toUpperCase() + "*" };
			}
			return mContent.query(Contacts.People.CONTENT_URI,
					PEOPLE_PROJECTION, buffer == null ? null : buffer
							.toString(), args,
					Contacts.People.DEFAULT_SORT_ORDER);
		}

		private ContentResolver mContent;
	}
	@Override
	public void onInit(int initStatus) {
		if (initStatus == TextToSpeech.SUCCESS)
		{
			tts.speak("Hello World", TextToSpeech.QUEUE_FLUSH, null);
		}
	}
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		tts = new TextToSpeech(this, this);
		setContentView(R.layout.sendsms);
		me=this;
		edSendMsg=(EditText)findViewById(R.id.sendmsg);
		Bundle b=this.getIntent().getBundleExtra("data"); 
		if( b != null){
			stMsg=b.getString("message");
			if(stMsg.length() > 140){
				stMsg=stMsg.substring(0,140);
			}
		}
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl("http://blog.way2sms.com/?p=156");
		webview.setWebViewClient(new MyWebViewClient());
		 ContentResolver content = getContentResolver();
		 Cursor cursor = content.query(Contacts.People.CONTENT_URI,
		 PEOPLE_PROJECTION, null, null,
		 Contacts.People.DEFAULT_SORT_ORDER);
		 ContactListAdapter adapter = new ContactListAdapter(
		 this, cursor);
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.txtPhoneNo);
		 textView.setAdapter(adapter);

		final Button btSend = (Button) findViewById(R.id.btnSendSMS);
		final EditText etPhono = (EditText) findViewById(R.id.txtPhoneNo);
		final EditText etMsg = (EditText) findViewById(R.id.txtMessage);
		final Button btClear = (Button) findViewById(R.id.Clear);
		btClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etMsg.setText("");
			}
		});
		etMsg.setText(stMsg);
		btSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 stPhoneNumber = etPhono.getText().toString();
				 stMsg = etMsg.getText().toString();
				if (stPhoneNumber == null || stPhoneNumber.equals("")) {
					Toast
							.makeText(getApplicationContext(),
									R.string.sendsms_invalidphoneno,
									Toast.LENGTH_SHORT).show();
					return;
				}
				if (stMsg == null || stMsg.equals("")) {
					Toast.makeText(getApplicationContext(),
							R.string.sendsms_invalidmsg, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if(!Utils.isPhoneNumberValid(stPhoneNumber, getApplicationContext())){
            		return;
            	}
				
				sendSMS();
			}
		});
		final Button btLogout = (Button) findViewById(R.id.logout);
		btLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logout();
			}
		});
		
		

	}
	
  
 private void sendSMS( ){
	 CALL_TO = SEND_SMS;
	 Thread t = new Thread(this);
		t.start();
	 
 }
  ProgressDialog dialog = null;
	private void logout() {
		
		dialog = ProgressDialog.show(SendSMS.this, "",
				"Logout. Please wait...", true);
		dialog.show();
		CALL_TO = LOGOUT;
		Thread t = new Thread(this);
		t.start();
		
	}
int CALL_TO=0;
public static final int LOGOUT=1;
public static final int SEND_SMS=2;
	public void returnToCaller(Intent intent) {
		// sets the result for the calling activity
		setResult(RESULT_OK, intent);
		 //equivalent of 'return'
		finish();
	}

	
@Override
protected void onRestart() {
	// TODO Auto-generated method stub
	super.onRestart();
	Log.v("Send SMS","onRestart");
}
@Override
protected void onStart() {
	// TODO Auto-generated method stub
	super.onStart();
	Log.v("Send SMS","onStart");
}
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	Log.v("Send SMS","onResume");
}
@Override
protected void onPause() {
	// TODO Auto-generated method stub
	super.onPause();
	Log.v("Send SMS","onPause");
	// globel resource should be free after usage 
	

}
@Override
protected void onStop() {
	// TODO Auto-generated method stub
	super.onStop();
	Log.v("Send SMS","onStop");
}
@Override
protected void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	Log.v("Send SMS","onDestroy");
}
public  Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
		
		if(Utils.ST_ERROR_MSG != null){
		Toast.makeText(getApplicationContext(), Utils.ST_ERROR_MSG,
				Toast.LENGTH_SHORT).show();
		}
		// globel resource should be free after usage 
		Utils.ST_ERROR_MSG=null;
	
       if(SEND_SMS == CALL_TO){
    	   LayoutInflater inflater = getLayoutInflater();
   		View layout = inflater.inflate(R.layout.toast_layout,
   				(ViewGroup) findViewById(R.layout.sendsms));
   		ImageView image = (ImageView) layout.findViewById(R.id.image);
   		image.setImageResource(R.drawable.outbox_icon);
   		TextView text = (TextView) layout.findViewById(R.id.text);
   		text.setText(getApplicationContext().getString(R.string.sendsms_msgsend));
   		Toast toast = new Toast(getApplicationContext());
   		toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
   		toast.setDuration(Toast.LENGTH_LONG);
   		toast.setView(layout);
   		toast.show();
   		String stOldData=edSendMsg.getText().toString();
		stOldData+=stPhoneNumber+":"+stMsg+"\n";
		try{
		edSendMsg.setText(stOldData);
		}catch(Throwable t){
			t.printStackTrace();
		}
		}
       if(dialog != null){
		dialog.cancel();
       }
			

	}
};
@Override
public void run() {
	// TODO Auto-generated method stub
	switch (CALL_TO) {
	case LOGOUT:
		Utils.logout(getApplicationContext());
		handler.sendEmptyMessage(0);
		returnToCaller(new Intent());
		
		break;
	case SEND_SMS:
		
		Utils.sendSMS(stPhoneNumber, stMsg,getApplicationContext());
		break;
	default:
		break;
	}
	handler.sendEmptyMessage(0);
}
}
