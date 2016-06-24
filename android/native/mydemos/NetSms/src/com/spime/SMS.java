package com.spime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class SMS extends android.app.Activity implements Runnable {

	/** holds the map of callbacks */
	protected HashMap<Integer, ResultCallbackIF> _callbackMap = new HashMap<Integer, ResultCallbackIF>();

	public static interface ResultCallbackIF {

		public void resultOk(Intent intent);

		public void resultCancel(Intent intent);

	}// end interface ResultCallbackIF

	private void updateUIFromPreferences() {
		boolean rememberChecked = prefs.getBoolean(PREF_CHECKED, false);
		String stUserName = prefs.getString(PREF_USERNAME, "");
		String stPassword = prefs.getString(PREF_PASSWORD, "");
		edUserID.setText(stUserName);
		edPasword.setText(stPassword);
		chkRememberPassword.setChecked(rememberChecked);
	}

	private void savePreferences() {
		boolean autoUpdateChecked = chkRememberPassword.isChecked();
		String stUserName = edUserID.getText().toString();
		String stPassword = edPasword.getText().toString();
		Editor editor = prefs.edit();
		
		if (autoUpdateChecked) {
			
			editor.putBoolean(PREF_CHECKED, autoUpdateChecked);
			editor.putString(PREF_USERNAME, stUserName);
			editor.putString(PREF_PASSWORD, stPassword);
			
		} else {
			
			editor.putBoolean(PREF_CHECKED, autoUpdateChecked);
			editor.putString(PREF_USERNAME, "");
			editor.putString(PREF_PASSWORD, "");
			
		}
		editor.putString(USER_USERNAME, stUserName);
		editor.putString(USER_PASSWORD, stPassword);
		editor.commit();
	}

	public static final String USER_DETAILS = "USER_DETAILS";
	public static final String PREF_USERNAME = "USER_NAME";
	public static final String PREF_PASSWORD = "USER_PASS";
	public static final String USER_PASSWORD = "PASS";
	public static final String USER_USERNAME = "NAME";
	public static final String PREF_CHECKED = "USER_REMEMBER";
	private static final int LOGIN = 1;
	private static final int CREATE_USER = 2;
	private static final int FORGET_PASSWORD = 3;
	private static final int DIALOG_TEXT_ENTRY=4;
	private static final int DIALOG_REALLY_EXIT_ID = 1;
	static final int DATE_DIALOG_ID = 0;
	private boolean isCanceled = false;

	SharedPreferences prefs;
	EditText edUserID = null;
	EditText edPasword = null;
	CheckBox chkRememberPassword = null;
	EditText edDob = null;

	static ProgressDialog dialog = null;
	static String stUserID = null;
	static String stPasword = null;

	private int mYear;
	private int mMonth;
	private int mDay;
	
	
	WebView webview;
	String stUserName = null;
	String stEmail = null;
	String stPhoneno = null;
	int CALL_TO = 0;
	Dialog createDialog=null;
	Dialog forgetDialog=null;
	DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			System.out.println("isCanceled");
			isCanceled = true;
			// Toast.makeText(getApplicationContext(), "Cancel",
			// Toast.LENGTH_SHORT).show();
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Log.v("SMS", "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.login);
		edUserID = (EditText) findViewById(R.id.txtUserName);
		edPasword = (EditText) findViewById(R.id.txtPassword);
		chkRememberPassword = (CheckBox) findViewById(R.id.chkRememberPassword);
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.loadUrl("http://news.way2sms.com/?p=117250");
		webview.setWebViewClient(new MyWebViewClient());
		prefs = getSharedPreferences(USER_DETAILS, Activity.MODE_WORLD_READABLE);
		updateUIFromPreferences();
		Button btLogin = (Button) findViewById(R.id.buttonSignIn);
		btLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stUserID = edUserID.getText().toString().trim();
				stPasword = edPasword.getText().toString().trim();
				if (stUserID == null || stUserID.equals("")
						|| stPasword == null || stPasword.equals("")) {
					Toast.makeText(getApplicationContext(),
							R.string.login_waring, Toast.LENGTH_SHORT).show();
				} else {
					if (!Utils.isPhoneNumberValid(stUserID,
							getApplicationContext())) {
						return;
					}
					CALL_TO = LOGIN;
					calltoSpecific();
				}

			}
		});

		final ImageButton imageButton = (ImageButton) findViewById(R.id.forgot);
		imageButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//
				try {

					forgetDialog = new Dialog(SMS.this);
					forgetDialog.setContentView(R.layout.custom_dialog);
					forgetDialog.setTitle("Forget Password");
					final EditText edUserID = (EditText) forgetDialog
							.findViewById(R.id.txtUserName);
					forgetDialog.setCancelable(true);
					Button btOk = (Button) forgetDialog.findViewById(R.id.ok);
					Button btCancel = (Button) forgetDialog
							.findViewById(R.id.cancel);

					btCancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							forgetDialog.cancel();
							// finish();
						}
					});

					btOk.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String stUserID = edUserID.getText().toString()
									.trim();
							if (!Utils.isPhoneNumberValid(stUserID,
									getApplicationContext())) {
								return;
							}

							forgetDialog.cancel();
							CALL_TO = FORGET_PASSWORD;
							calltoSpecific();
						}
					});
					//forgetDialog.show();
					showDialog(DIALOG_TEXT_ENTRY);
					 
				} catch (android.view.WindowManager.BadTokenException e) {
					// TODO: handle exception
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});

		final ImageButton imageButtonCreate = (ImageButton) findViewById(R.id.create);

		imageButtonCreate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    createDialog = new Dialog(SMS.this);
				createDialog.setContentView(R.layout.createuser);
				createDialog.setTitle("Create User");
				createDialog.setCancelable(true);

				final EditText edName = (EditText) createDialog
						.findViewById(R.id.EditTextName);
				final EditText edEmail = (EditText) createDialog
						.findViewById(R.id.EditTextEmail);
				final EditText edPhoneno = (EditText) createDialog
						.findViewById(R.id.phoneno);

				edDob = (EditText) createDialog.findViewById(R.id.dob);
				// get the current date
				final Calendar c = Calendar.getInstance();
				mYear = c.get(Calendar.YEAR);
				mMonth = c.get(Calendar.MONTH);
				mDay = c.get(Calendar.DAY_OF_MONTH);

				edDob.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						showDialog(DATE_DIALOG_ID);
						return true;
					}
				});
				Button btOk = (Button) createDialog.findViewById(R.id.ok);
				Button btCancel = (Button) createDialog
						.findViewById(R.id.cancel);

				btCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						createDialog.cancel();
						// finish();
					}
				});
				btOk.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						stPhoneno = edPhoneno.getText().toString();
						stUserName = edName.getText().toString();
						stEmail = edEmail.getText().toString();
						String stDOB = edDob.getText().toString();
						if (stUserName.equals("")) {
							Toast.makeText(getApplicationContext(),
									R.string.name_warning, Toast.LENGTH_SHORT)
									.show();
							return;
						}
						if (stEmail.equals("")) {
							Toast.makeText(getApplicationContext(),
									R.string.email_warning, Toast.LENGTH_SHORT)
									.show();
							return;
						}
						if (stDOB.equals("")) {
							Toast.makeText(getApplicationContext(),
									R.string.dob_warning, Toast.LENGTH_SHORT)
									.show();
							return;
						}
						if (!Utils.isPhoneNumberValid(stPhoneno,
								getApplicationContext())) {
							return;
						}
						CALL_TO = CREATE_USER;
						calltoSpecific();
						createDialog.dismiss();
					}

				});
				createDialog.show();

			}
		});

	}

	private void calltoSpecific() {
		if (CALL_TO == FORGET_PASSWORD || CALL_TO == CREATE_USER) {
			dialog = ProgressDialog.show(SMS.this, "",
					"Send... Please wait...", true);
		} else if (CALL_TO == LOGIN) {
			dialog = ProgressDialog.show(SMS.this, "", "Login. Please wait...");
			dialog.setCancelable(true);
			dialog.setOnCancelListener(onCancelListener);
			isCanceled=false;

		}
		dialog.show();

		try {

			Thread thread = new Thread(this);
			thread.start();
		} catch (IllegalThreadStateException e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Thread illegal",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		 Dialog dialog=null;
		try{
		switch (id) {
		case DATE_DIALOG_ID: {
			dialog = new DatePickerDialog(SMS.this, mDateSetListener, mYear,
					mMonth, mDay);
			break;
		}
		case DIALOG_REALLY_EXIT_ID: {
			dialog = new AlertDialog.Builder(this).setMessage(
					"Are you sure you want to exit?").setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									SMS.this.finish();
								}
							}).setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).create();
			break;
		}
		case DIALOG_TEXT_ENTRY:
            // This example shows how to add a custom layout to an AlertDialog
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
				dialog = new AlertDialog.Builder(SMS.this).setIcon(
						R.drawable.alert_dialog_icon).setTitle(
						R.string.alert_dialog_text_entry)
						.setView(textEntryView).setPositiveButton(
								R.string.alert_dialog_ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										 EditText edUserID = (EditText) textEntryView
										.findViewById(R.id.username_edit);
										/* User clicked OK so do some stuff */
										 String stUserID = edUserID.getText().toString()
											.trim();
									if (!Utils.isPhoneNumberValid(stUserID,
											getApplicationContext())) {
										return;
									}

									
									CALL_TO = FORGET_PASSWORD;
									calltoSpecific();
									}
								}).setNegativeButton(
								R.string.alert_dialog_cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {

										/* User clicked cancel so do some stuff */
									}
								}).create();
        break;
		default:
			dialog = null;
			break;
		}
		
		}catch (IllegalArgumentException e) {
			// TODO: handle exception
			Log.v("SMS", ""+e.getMessage());
		}
		return dialog;
	}

	private void updateDisplay() {
		edDob.setText(new StringBuilder().append(mMonth + 1).append("/")
				.append(mDay).append("/").append(mYear).append(" "));
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	public void launchSubActivity(Class<SMSTabActivity> subActivityClass,
			ResultCallbackIF callback) {

		Intent i = new Intent(this, subActivityClass);

		Random rand = new Random();
		int correlationId = rand.nextInt();

		_callbackMap.put(correlationId, callback);

		startActivityForResult(i, correlationId);

	}

	@Override
	protected void onActivityResult(int correlationId, int resultCode,
			Intent intentData) {
		// TODO Auto-generated method stub
		try {
			ResultCallbackIF callback = _callbackMap.get(correlationId);

			switch (resultCode) {
			case Activity.RESULT_CANCELED:
				callback.resultCancel(intentData);
				_callbackMap.remove(correlationId);
				break;
			case Activity.RESULT_OK:
				callback.resultOk(intentData);
				_callbackMap.remove(correlationId);
				break;
			default:
				Log.e("Error",
						"Couldn't find callback handler for correlationId");
			}
		} catch (Exception e) {
			Log.e("Error", "Problem processing result from sub-activity", e);
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		Log.v("SMS", "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		Log.v("SMS", "Back pressed");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showDialog(DIALOG_REALLY_EXIT_ID);
			if (dialog != null)
				dialog.cancel();
			// if(thread != null)
			// thread.stop();
		}

		return true;
		// use this instead if you want to preserve onKeyDown() behavior
		// return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v("SMS", "onPause");
		if(createDialog != null)
		createDialog.cancel();
		if(forgetDialog != null)
			forgetDialog.cancel();
		savePreferences();
		// globel resource should be free after usage
		

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Log.v("SMS", "onStart");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Utils.ST_ERROR_MSG = null;

		Log.v("SMS", "onRestart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v("SMS", "onResume");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.v("SMS", "onStop");
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("run ...");
		// TODO Auto-generated method stub
		switch (CALL_TO) {
		case LOGIN: {

			if (Utils.login(stUserID, stPasword, getApplicationContext())) {
				
				if (!isCanceled) {
					launchSubActivity(SMSTabActivity.class,
							new SMS.ResultCallbackIF() {

								@Override
								public void resultOk(Intent intent) {
									// TODO Auto-generated method stub
									Log.v("SMS", "result ok");
								}

								@Override
								public void resultCancel(Intent intent) {
									// TODO Auto-generated method stub
									Log.v("SMS", "result cancel");
								}
							});
				}else{
					Utils.ST_ERROR_MSG="Login canceled";
				}
				
			} else {
				Log.v("SMS","Error in login");
			}
			break;
		}
		case CREATE_USER: {
			Utils.newUser(stUserName, mDay, mMonth, mYear, stEmail, stPhoneno,
					getApplicationContext());
			break;
		}
		case FORGET_PASSWORD: {
			Utils.forgetPaaword(stUserID, getApplicationContext());
			break;
		}
		}
		handler.sendEmptyMessage(0);
	}

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.cancel();
			Toast.makeText(getApplicationContext(), Utils.ST_ERROR_MSG, Toast.LENGTH_SHORT)
					.show();
			// globel resource should be free after usage
			Utils.ST_ERROR_MSG = null;

		}
	};
}