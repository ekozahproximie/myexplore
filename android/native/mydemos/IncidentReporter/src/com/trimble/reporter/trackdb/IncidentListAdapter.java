package com.trimble.reporter.trackdb;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.trimble.reporter.R;
import com.trimble.reporter.img.ThumbnailController;
import com.trimble.reporter.utils.Utils;

public class IncidentListAdapter extends SimpleCursorAdapter {

	private Cursor resultCursor;

	private Context context;

	private int layout;

	private String[] from;

	private int[] to;
	
	private Activity activity=null;
  
	private ThumbnailController mThumbController; 
	
	private HashMap< String, String> hm =null;
	public IncidentListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to,Activity activity,HashMap< String, String> hm) {
		super(context, layout, c, from, to);
		this.resultCursor = c;
		this.context = context;
		this.layout = layout;
		this.from = from;
		this.to = to;
		this.activity=activity;
		this.hm=hm;
		
	}
	public void setHm(HashMap<String, String> hm) {
		this.hm = hm;
		notifyDataSetChanged();
	}
	@Override
	public void changeCursor(Cursor cursor) {
		super.changeCursor(cursor);
		this.resultCursor = cursor;
	}

	@Override
	public View getView(int position, View inView, ViewGroup parent) {
		View v = inView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(layout, null);
		}
		resultCursor.moveToPosition(position);

		for (int i = 0; i < from.length; i++) {
			TextView view = (TextView) v.findViewById(to[i]);
			view.setText(resultCursor.getString(resultCursor
					.getColumnIndex(from[i])));
		}

		final String latitude = resultCursor.getString(resultCursor
				.getColumnIndex(TrackTable.COLUMN_LATITUDE));
		final String longitude = resultCursor.getString(resultCursor
				.getColumnIndex(TrackTable.COLUMN_LONGTITUDE));
		final String incidentInternal = resultCursor.getString(resultCursor
				.getColumnIndex(TrackTable.COLUMN_INCIDENT_INTERNAL));
		if(hm != null){
			String stID= hm.get(incidentInternal);
			if(stID != null){
				TextView textView =(TextView)v.findViewById(R.id.statusinfo);
				textView.setText(stID);
			}
		}
		
		try{
		byte []bImgData=resultCursor.getBlob(resultCursor
				.getColumnIndex(TrackTable.COLUMN_IMAGE));
		
		if(bImgData != null){
		Bitmap bitmap= Utils.makeBitmap(bImgData, 100*100,activity);
		ImageView incidentPhoto = (ImageView) v.findViewById(R.id.img);
		incidentPhoto.setImageBitmap(bitmap);
		}
		
		}catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		Button mapIcon = (Button) v.findViewById(R.id.map);
		mapIcon.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("geo:" + latitude + "," + longitude
						+ "z=18"));
				context.startActivity(intent);

			}
		});

		return (v);
	}
	
	private void setPic( String mCurrentPhotoPath,ImageView imvFlag) {

        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */

        /* Get the size of the ImageView */
        int targetW = imvFlag.getWidth();
        int targetH = imvFlag.getHeight();
        
        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        
       
        /* Figure out which way needs to be reduced less */
        int scaleFactor = 16;
        /*if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH); 
        }*/

        /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        int degree = ThumbnailController.getExifOrientation(mCurrentPhotoPath);
        /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        bitmap = ThumbnailController.rotate(bitmap, degree);
        /* Associate the Bitmap to the ImageView */
        imvFlag.setImageBitmap(bitmap);
       
    }
}
