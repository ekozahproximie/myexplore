package com.spime;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class TextAnimation extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final Context context= this;
		
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int rawContactInsertIndex = ops.size();
		ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
				.withValue(RawContacts.ACCOUNT_TYPE, null)
				.withValue(RawContacts.ACCOUNT_NAME, null).build());
		ops.add(ContentProviderOperation
				.newInsert(Data.CONTENT_URI)
				.withValueBackReference(Data.RAW_CONTACT_ID,
						rawContactInsertIndex)
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
				.withValue(StructuredName.DISPLAY_NAME, "Mike Sullivan")
				.build());
		try {
			getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OperationApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
		
		Animation a = AnimationUtils.loadAnimation(this, R.anim.alpha);
		a.reset();
		TextView tv = (TextView) findViewById(R.id.firstTextView);
		tv.clearAnimation();
		tv.startAnimation(a);
		a.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
				Animation a = AnimationUtils.loadAnimation(context, R.anim.alpha1);
				a.reset();
				TextView tv = (TextView) findViewById(R.id.firstTextView);
				
				tv.clearAnimation();
				tv.startAnimation(a);
			}
		});
		a = AnimationUtils.loadAnimation(this, R.anim.translate);
		a.reset();
		tv = (TextView) findViewById(R.id.secondTextView);
		tv.clearAnimation();
		tv.startAnimation(a);
		a = AnimationUtils.loadAnimation(this, R.anim.rotate);
		a.reset();
		tv = (TextView) findViewById(R.id.thirdTextView);
		tv.clearAnimation();
		tv.startAnimation(a);
		AnimationListener animationListener = new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
				
			}
		}; 
	}
}