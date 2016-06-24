package com.spime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;

import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AndroidDemo extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        
        TextView text = (TextView) findViewById(R.id.txt);
        //int iSum=add(2, 5);
        Log.i("SPIME","in java "+add(2, 5));
        String st=add(2, 5);
        byte b[]=st.getBytes();
        try {
			text.setText(new String(b,"UTF-16"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //text.setText("2 + 5 = " + 7);
      
//        ContentValues values = new ContentValues();
//
//		values.put(
//				ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
//				"India");
//		
//		values.put(
//				ContactsContract.CommonDataKinds.StructuredPostal.CITY,
//				"Chennai");
//		values.put(
//				ContactsContract.CommonDataKinds.StructuredPostal.STREET,
//				"First Ave");
//		values.put(Phone.CONTACT_ID,58);
//		values.put(Phone.TYPE, Phone.TYPE_MOBILE);
//
//		// addContactintent
//		// .putExtra(
//		// ContactsContract.Intents.Insert.POSTAL_,
//		// ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
//		
//		Uri uri = getContentResolver()
//				.insert(People.CONTENT_URI, values);
//		 ContentProviderOperation.Builder mBuilder;
//		 mBuilder=newInsertCpo(RawContacts.CONTENT_URI, true).withValues(values);
//		  ArrayList<  ContentProviderOperation>operationList = 
//			  new ArrayList<ContentProviderOperation>(1);
//		 
//		 //startActivity(new Intent(Intent.ACTION_EDIT, uri));
//		ContentResolver mContentResolver = this.getContentResolver();
//		operationList.add(mBuilder.build());
//		 try {
//			mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (OperationApplicationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		
//        setContentView(layout);
    }
    private static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
            ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    }
    public static ContentProviderOperation.Builder newInsertCpo(Uri uri,
            boolean yield) {
            return ContentProviderOperation.newInsert(
                addCallerIsSyncAdapterParameter(uri)).withYieldAllowed(yield);
        }
    static {
    	System.loadLibrary("AndroidDemo");
    }
    private native String add(int x, int y);
}