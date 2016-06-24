package com.spime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SmsInbox extends ListActivity implements OnInitListener {
    private static final String TAG = "SmsInbox";
	private ListAdapter mAdapter;
	private Context context = null;
	static List<ImageAndText> list = null;
	ImageAndTextListAdapter adapter = null;
	ListView lv=null;
	final int MY_DATA_CHECK_CODE=45; 
 private final int CONTEXTMENU_FORWARDITEM=1;
 private final int CONTEXTMENU_READITEM=2;
 private static TextToSpeech mTts;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		try {
			Intent checkIntent = new Intent();  

		       checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);  
		       startActivityForResult(checkIntent, MY_DATA_CHECK_CODE); 
			// Initialize text-to-speech. This is an asynchronous operation.
			// The OnInitListener (second argument) is called after
			// initialization completes.
			// TextToSpeech.OnInitListener
			mTts = new TextToSpeech(this, this); 
			String SORT_ORDER = "date DESC";
			context = getApplicationContext();
			int count = 0;
			// setContentView(R.layout.inbox);
			String stAMSG[] = null;
			
			
			try {
				list = getSmsDetails(this, -1, false);
			} catch (Throwable e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			this.adapter = new ImageAndTextListAdapter(this, list);
			setListAdapter(this.adapter);
		
			 lv = getListView();
			

			lv.setTextFilterEnabled(true);
			registerForContextMenu(lv);
//			lv.setOnItemClickListener(new OnItemClickListener() {
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					try {
//						ImageAndText text = list.get(position);
//						SmsInbox.readMessage(text.getText());
//						Toast.makeText(getApplicationContext(), text.getText(),
//								Toast.LENGTH_SHORT).show();
//					} catch (ClassCastException e) {
//						// TODO: handle exception
//					}
//				}
//			});

		} catch (Throwable e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	// Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will indicate this.
            int result = mTts.setLanguage(Locale.US);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
               // Lanuage data is missing or the language is not supported.
                Log.e(TAG, "Language is not available.");
            } else {
                // Check the documentation for other possible result codes.
                // For example, the language may be available for the locale,
                // but not for the specified country and variant.

                // The TTS engine has been successfully initialized.
                // Allow the user to press the button for the app to speak again.
               
                // Greet the user.
               
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Forward Menu"); 
		menu.add(0, CONTEXTMENU_FORWARDITEM, 0, 
		"Forward this Message ?"); 
		menu.add(0, CONTEXTMENU_READITEM, 1, 
		"Read this Message ?"); 
		

	}
	
	@Override
	public	boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		ImageAndText text  =(ImageAndText) lv.getAdapter().getItem( 
				info.position); 
		switch (item.getItemId()) {
		case CONTEXTMENU_FORWARDITEM:
			
			
			Bundle b= new Bundle();
			 b.putString("message",text.getBody());
			Intent myIntent=new Intent(getApplicationContext(),SMSTabActivity.class);
			myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			myIntent.putExtra("data",b);
			 startActivity(myIntent);

			
			return true;
		case CONTEXTMENU_READITEM:
			SmsInbox.readMessage(text.getBody());
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance 
				mTts = new TextToSpeech(this, this);
			} else { // missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}
	public List getSmsDetails(Context context, long ignoreThreadId,
			boolean unreadOnly) {
		String stMSG[] = null;
		List<ImageAndText> list = new ArrayList<ImageAndText>();
		String SORT_ORDER = "date DESC";
		int count = 0;

		Cursor cursor = context.getContentResolver().query(
				Uri.parse("content://sms/inbox"),
				new String[] { "_id", "thread_id", "address", "person", "date",
						"body" }, null, null, SORT_ORDER);

		if (cursor != null) {
			try {
				count = cursor.getCount();
				if (count > 0) {
					// String[] columns = cursor.getColumnNames();
					// for (int i=0; i<columns.length; i++) {
					// Log.v("Info","columns " + i + ": " + columns[i] + ": "
					// + cursor.getString(i));
					// }
					stMSG = new String[count];
					cursor.moveToFirst();

					for (int i = 0; i < count; i++) {

						long messageId = cursor.getLong(0);
						long threadId = cursor.getLong(1);
						String address = cursor.getString(2);
						long contactId = cursor.getLong(3);
						Uri uri = ContentUris.withAppendedId(
								People.CONTENT_URI, contactId);

						String contactId_string = String.valueOf(contactId);
						long timestamp = cursor.getLong(4);
						int id = cursor.getColumnIndex(People._ID);

						String stData = null;

						String stImgPath = uri.toString();

						Bitmap bitmap = People.loadContactPhoto(context, uri,R.drawable.nouser, null);
						// Then query for this specific record:
						Cursor cur = managedQuery(uri, null, null, null, null);

						stData=getColumnData(cur);
						if(stData != null ){
							address=stData+address;
						}
						String body = cursor.getString(5);

						stMSG[i] = address + ":" + body;

						ImageAndText text = new ImageAndText(stImgPath,
								stMSG[i], bitmap);
						text.setBody(body);
						list.add(text);
						cursor.moveToNext();

					}

				}
			} finally {
				cursor.close();
			}
		}

		return list;
	}

	private String getColumnData(Cursor cur) {
		if (cur.moveToFirst()) {
			String name;
			int nameColumn = cur.getColumnIndex(People.NAME);
			
			
			do {
				name = cur.getString(nameColumn);
				
				return name;
			} while (cur.moveToNext());
		}
		return null;
	}

	// Implements TextToSpeech.OnInitListener.    
	
	
public static void readMessage(String stMessage) {
	// Drop all pending entries in the playback queue.  
	if(mTts != null){	
	mTts.speak(stMessage, TextToSpeech.QUEUE_FLUSH, null);
	}
	
}
	@Override
	public void onDestroy() {
		// Don't forget to shutdown!
		if (mTts != null) {
			mTts.stop();
			mTts.shutdown();
		}
		super.onDestroy();

	}

	
}

class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {

	public ImageAndTextListAdapter(Activity activity,
			List<ImageAndText> imageAndTexts) {
		super(activity, 0, imageAndTexts);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();
		LayoutInflater inflater = activity.getLayoutInflater();

		// Inflate the views from XML
		View rowView = inflater.inflate(R.layout.inbox, null);
		ImageAndText imageAndText = getItem(position);
		/**
		 * Intent service=new Intent(GMaps.this, MyLocationService.class);
			Bundle bundle= new Bundle();
			bundle.putString("myAlert", lat+","+lon);
			service.putExtras(bundle);
			startService(service);
		 */
		// Load the image and set it on the ImageView
		ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
		try {
			imageView.setImageBitmap(imageAndText.getBit());
		} catch (RuntimeException e) {
			// TODO: handle exception
			Bitmap bMap = BitmapFactory.decodeResource(rowView.getResources(),
					R.drawable.nouser);
			// BitmapFactory.decodeFile("/sdcard/test2.png");

			imageView.setImageBitmap(bMap);
		}
		// Set the text on the TextView
		TextView textView = (TextView) rowView.findViewById(R.id.text);
		textView.setText(imageAndText.getText());

		return rowView;
	}

}
