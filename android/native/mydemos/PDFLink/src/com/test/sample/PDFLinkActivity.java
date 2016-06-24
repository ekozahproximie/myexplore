package com.test.sample;





import android.app.Activity;
import android.os.Bundle;

public class PDFLinkActivity extends Activity {
	ImageMap mImageMap;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // find the image map in the view
        mImageMap = (ImageMap)findViewById(R.id.map);
        
        // add a click handler to react when areas are tapped
        mImageMap.addOnImageMapClickedHandler(new ImageMap.OnImageMapClickedHandler() {
			@Override
			public void onImageMapClicked(int id) {
				// when the area is tapped, show the name in a 
				// text bubble
				mImageMap.showBubble(id);
			}

			@Override
			public void onBubbleClicked(int id) {
			    mImageMap.launchBrowser(id);
			    
			}
		});
    }
}