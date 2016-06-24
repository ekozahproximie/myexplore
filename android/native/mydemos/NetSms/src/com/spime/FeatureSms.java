package com.spime;

import java.sql.Date;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.lang.System; 
public class FeatureSms extends Activity {
	
	
		static final int TIME_DIALOG_ID = 0;
	
	private TextView mDateDisplay;
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private long lCurrentTime;
	
	static final int DATE_DIALOG_ID = 1;
	
	private ImageButton mPickTime;
	private ImageButton mPickDate;
	CheckBox schdule= null;
	 Button ok= null;
	Calendar c=null;
	java.util.Date date=null; 
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.featuresms);
//	TextView tv=(TextView)findViewById(R.id.TextView01);  
//	Typeface face=Typeface.createFromAsset(getAssets(), "fonts/HandmadeTypewriter.ttf");  
//	  
//	tv.setTypeface(face);  
	// capture our View elements   
	 
	 mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
	 
	 mPickDate = (ImageButton) findViewById(R.id.pickDate);    
	 // add a click listener to the button    
	 mPickDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				updateCurrenttime();
				showDialog(DATE_DIALOG_ID);
				
			}
		});
	 mPickTime = (ImageButton) findViewById(R.id.pickTime);    
	 // add a click listener to the button    
	 mPickTime.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				 updateCurrenttime();
				 showDialog(TIME_DIALOG_ID);
			     
				
			}
		});
	
	 schdule = (CheckBox) findViewById(R.id.schdule);  
	 ok = (Button) findViewById(R.id.Go);  
	 ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final EditText et = (EditText)findViewById(R.id.phono);
				String stPhoneno=et.getText().toString();
				if (!Utils.isPhoneNumberValid(stPhoneno,
						getApplicationContext())) {
					return;
				}
				String stMsg=((EditText) findViewById(R.id.txtMessage)).toString();
				if (stMsg == null || stMsg.equals("")) {
					Toast.makeText(getApplicationContext(),
							R.string.sendsms_invalidmsg, Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if(schdule.isChecked()){
					lCurrentTime=System.currentTimeMillis();
				  date =new java.util.Date(mYear,mMonth,mDay,mHour,mMinute); 
			     if(new java.util.Date(date.toLocaleString()) .before( new java.util.Date(new java.util.Date(lCurrentTime).toLocaleString() )  )){
			    	
			    	 Toast.makeText(getApplicationContext(),"Invaild time ", Toast.LENGTH_SHORT).show();
			     }else{
				  Toast.makeText(getApplicationContext(),"Schudled time:"+date.toLocaleString() , Toast.LENGTH_SHORT).show();
				  Bundle bundle= new Bundle();
					bundle.putString("myData", date+","+stPhoneno+","+stMsg);
					bundle.putLong("time", date.getTime());
					Intent service=new Intent(FeatureSms.this, FeatureSMSService.class);
					service.putExtras(bundle);
			     }
				}
			}
	 });
	 
	 schdule.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(schdule.isChecked()){
					lCurrentTime=System.currentTimeMillis();
				  date =new java.util.Date(mYear,mMonth,mDay,mHour,mMinute); 
			     if(new java.util.Date(date.toLocaleString()) .before( new java.util.Date(new java.util.Date(lCurrentTime).toLocaleString() )  )){
			    	 Log.v("Fes", date.toLocaleString()+" "+new java.util.Date(lCurrentTime).toLocaleString());
			    	 Toast.makeText(getApplicationContext(),"Invaild time ", Toast.LENGTH_SHORT).show();
			     }else{
				  Toast.makeText(getApplicationContext(),"Schudled time:"+date.toLocaleString() , Toast.LENGTH_SHORT).show();
			     }
				}
				
			}
		});
	 // get the current time    
	 
	 updateCurrenttime();
	
	// display the current date   
	updateDisplay();
}
private void updateCurrenttime(){
	 lCurrentTime =System.currentTimeMillis();
	 java.util.Date date = new java.util.Date(lCurrentTime);
		    mHour = date.getHours();
		    mMinute = date.getMinutes(); 
			mYear    = date.getYear()+1900;
			mMonth   = date.getMonth();
			mDay    = date.getDate();
}
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
//updates the time we display in the TextView
	private void updateDisplay() {
//		mTimeDisplay.setText(new StringBuilder().append(pad(mHour)).append(":")
//				.append(pad(mMinute)));
		mDateDisplay.setText( new StringBuilder().append(mMonth + 1).append("-")
				.append(mDay).append("-").append(mYear).append(" "));
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
					false);
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}
	
	// the callback received when the user "sets" the time in the dialog
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			updateDisplay();
		}
	};
	
	// the callback received when the user "sets" the date in the dialog  
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};
	
}
