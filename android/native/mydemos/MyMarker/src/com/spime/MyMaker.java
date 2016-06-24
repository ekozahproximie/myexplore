package com.spime;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

public class MyMaker extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.main);
        MyLayout myLayout = (MyLayout)findViewById(R.id.mylayout);
       Overlay overlay= new Overlay(this,200,200,R.drawable.ic_route_dest_slant);
      LinearLayout  lai=(LinearLayout)findViewById(R.id.lai);
      AbsoluteLayout.LayoutParams params = 
    		 new AbsoluteLayout.LayoutParams(75,75,200,200);
    		 params.x = 200; 
    		  params.y = 200; 
    		  overlay.setLayoutParams(params); 
    		  myLayout.addView(overlay);
    		  Overlay overlay1= new Overlay(this,200,200,R.drawable.ic_route_dest_slant);
    		  AbsoluteLayout.LayoutParams params1 = 
    	    		 new AbsoluteLayout.LayoutParams(75,75,200,200);
    		  params1.x = 210; 
    		  params1.y = 190; 
    		  overlay1.setLayoutParams(params1); 
    		  myLayout.addView(overlay1);
    }
    /**
     * int width = 100, height =50, x = 10, y = 20; 
     * Button button = new Button(this); 
     * AbsoluteLayout myLayout = (AbsoluteLayout)findViewById(R.id.myLayout); 
     * myLayout.add(button, new AbsoluteLayout.LayoutParams(width, height, x, y)); 
     */
    /**
     *  LayoutParams params = mLayout.generateLayoutParams(); 
     *  params.x = remoteObject.x;
     *  params.y = remoteObject.y; 
     *  mLayout.addView(button, params); 
     */
	/**
	 * AbsoluteLayout.LayoutParams params = 
	 * (AbsoluteLayout.LayoutParams)start_it.getLayoutParams(); 
	 * params.x = 200; 
	 * params.y = 200; 
	 * start_it.setLayoutParams(params); 
	 */
	/*public void draw(Canvas canvas, MapView mapV, boolean shadow) {
		if (shadow) {
			Projection projection = mapV.getProjection();
			Point pt = new Point();
			projection.toPixels(globalGeoPoint, pt);
			GeoPoint newGeos = new GeoPoint(selectedLat + (100), selectedLong);
			Point pt2 = new Point();
			projection.toPixels(newGeos, pt2);
			float circleRadius = Math.abs(pt2.y - pt.y);
			Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			circlePaint.setColor(0x30000000);
			circlePaint.setStyle(Style.FILL_AND_STROKE);
			canvas.drawCircle((float) pt.x, (float) pt.y, circleRadius,
					circlePaint);
			circlePaint.setColor(0x99000000);
			circlePaint.setStyle(Style.STROKE);
			canvas.drawCircle((float) pt.x, (float) pt.y, circleRadius,
					circlePaint);
			Bitmap markerBitmap = BitmapFactory.decodeResource(
					getApplicationContext().getResources(), R.drawable.pin);
			canvas.drawBitmap(markerBitmap, pt.x,
					pt.y - markerBitmap.getHeight(), null);
			super.draw(canvas, mapV, shadow);
		}
	} */
}