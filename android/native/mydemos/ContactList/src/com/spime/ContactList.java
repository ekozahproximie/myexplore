package com.spime;

import java.io.InputStream;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ContactList extends ListActivity {
    /** Called when the activity is first created. */
	 private SimpleCursorAdapter myAdapter;
	 
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
//	        Cursor cursor = getContentResolver().query(People.CONTENT_URI, null, null, null, null);
//	        startManagingCursor(cursor);
//	 
//	        String[] columns = new String[] {People.NAME, People.NUMBER};
//	        int[] names = new int[] {R.id.contact_name, R.id.phone_number};
//	 
//	        myAdapter = new SimpleCursorAdapter(this, R.layout.main, cursor, columns, names);
//	        setListAdapter(myAdapter);
//	        
	      //query for the people in your address book
	        Cursor cursor = getContentResolver().query(People.CONTENT_URI, null, null, null,People.NAME + " ASC");
	        startManagingCursor(cursor);

	        //bind the name and the number fields
	        String[] columns = new String[] { People.NAME, People.NUMBER,People.PRIMARY_PHONE_ID };
	        int[] to = new int[] { R.id.name_entry, R.id.number_entry,R.id.profile_pic };
	        SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, R.layout.main, cursor, columns, to);
	        ContactListCursorAdapter adapter =new ContactListCursorAdapter(this, R.layout.main, cursor, columns, to);
	        this.setListAdapter(adapter);

	    }
	     
	    
	    protected void onListItemClick(ListView listView, View view, int position, long id) {
	        super.onListItemClick(listView, view, position, id);
	 
	        //Intent intent = new Intent(Intent.ACTION_CALL);
	        Cursor cursor = (Cursor) myAdapter.getItem(position);
	        String phoneId = cursor.getString(cursor.getColumnIndex(People.PRIMARY_PHONE_ID));
	        //intent.setData(Uri.withAppendedPath(Phones.CONTENT_URI, phoneId));
	         Toast.makeText(view.getContext(),phoneId, Toast.LENGTH_SHORT).show();
	        ///startActivity(intent);
	    }
	   class  SimpleContactAdapter extends CursorAdapter{

		public SimpleContactAdapter(Context context, Cursor c) {
			super(context, c);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
			Cursor c = getCursor();

	        final LayoutInflater inflater = LayoutInflater.from(context);
	        View v = inflater.inflate(R.layout.main, parent, false);

	        int idCol = c.getColumnIndex(People._ID);
	        int nameCol = c.getColumnIndex(People.NAME);
	        int numCol = c.getColumnIndex(People.NUMBER);

	        String name = c.getString(nameCol);
	        String number = c.getString(numCol);
	        long id = c.getLong(idCol);

	        // set the name here
	        TextView name_text = (TextView) v.findViewById(R.id.name_entry);
	        if (name_text != null) {
	            name_text.setText(name);
	        }

	        // set the profile picture
	        ImageView profile = (ImageView) v.findViewById(R.id.profile_pic);
	        if (profile != null) {
	            // retrieve the contact photo as a Bitmap
	            Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, id);
	            Bitmap bitmap = People.loadContactPhoto(context, uri, R.drawable.icon, null);

	            // set it here in the ImageView
	            profile.setImageBitmap(bitmap);
	        }

	        // set the contact phone number
	        TextView number_text = (TextView) v.findViewById(R.id.number_entry);
	        if (number_text != null) {
	            number_text.setText(number);
	        }

	        return v;
		}
		
	   }
	   public class ContactListCursorAdapter extends SimpleCursorAdapter implements Filterable {

		    private Context context;

		    private int layout;

		    public ContactListCursorAdapter (Context context, int layout, Cursor c, String[] from, int[] to) {
		        super(context, layout, c, from, to);
		        this.context = context;
		        this.layout = layout;
		    }

		    @Override
		    public View newView(Context context, Cursor cursor, ViewGroup parent) {

		        Cursor c = getCursor();

		        final LayoutInflater inflater = LayoutInflater.from(context);
		        View v = inflater.inflate(layout, parent, false);

		        int idCol = c.getColumnIndex(People._ID);
		        int nameCol = c.getColumnIndex(People.NAME);
		        int numCol = c.getColumnIndex(People.NUMBER);

		        String name = c.getString(nameCol);
		        String number = c.getString(numCol);
		        long id = c.getLong(idCol);

		        // set the name here
		        TextView name_text = (TextView) v.findViewById(R.id.name_entry);
		        if (name_text != null) {
		            name_text.setText(name);
		        }

		        // set the profile picture
		        ImageView profile = (ImageView) v.findViewById(R.id.profile_pic);
		        if (profile != null) {
		            // retrieve the contact photo as a Bitmap
		            Uri uri = ContentUris.withAppendedId(People.CONTENT_URI, id);
		            Bitmap bitmap = People.loadContactPhoto(context, uri, R.drawable.icon, null);

		            // set it here in the ImageView
		            profile.setImageBitmap(bitmap);
		        }

		        // set the contact phone number
		        TextView number_text = (TextView) v.findViewById(R.id.number_entry);
		        if (number_text != null) {
		            number_text.setText(number);
		        }

		        return v;
		    }

		    @Override
		    public void bindView(View v, Context context, Cursor c) {

		        int nameCol = c.getColumnIndex(People.NAME);

		        String name = c.getString(nameCol);

		        /**
		         * Next set the name of the entry.
		         */
		        TextView name_text = (TextView) v.findViewById(R.id.name_entry);
		        if (name_text != null) {
		            name_text.setText(name);
		        }
		    }

		    @Override
		    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		        if (getFilterQueryProvider() != null) { return getFilterQueryProvider().runQuery(constraint); }

		        StringBuilder buffer = null;
		        String[] args = null;
		        if (constraint != null) {
		            buffer = new StringBuilder();
		            buffer.append("UPPER(");
		            buffer.append(People.NAME);
		            buffer.append(") GLOB ?");
		            args = new String[] { constraint.toString().toUpperCase() + "*" };
		        }

		        return context.getContentResolver().query(People.CONTENT_URI, null,
		                buffer == null ? null : buffer.toString(), args, People.NAME + " ASC");
		    }
		}

}