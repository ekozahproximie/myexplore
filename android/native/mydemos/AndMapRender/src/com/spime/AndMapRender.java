package com.spime;

import com.mapopolis.viewer.MapViewFrame;
import com.mapopolis.viewer.engine.MapopolisException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class AndMapRender extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MapViewFrame frame;
		try {
			//frame = new MapViewFrame(getApplicationContext());
			 setContentView(R.layout.main);
			 frame =(MapViewFrame)findViewById(R.id.mapView);
			 ImageView imageView = (ImageView)findViewById(R.id.zoomin);
			 imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					frame.zoomin();
				}
			});
			 
			 ImageView zoomout = (ImageView)findViewById(R.id.zoomout);
			 zoomout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					frame.zoomout();
				}
			});
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }
}