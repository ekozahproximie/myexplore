package com.spime;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GeoIntent extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button bt=(Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String uriString = "geo:" + 12.995973
                + "," + 80.252152;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
                Uri.parse(uriString));
            startActivity(intent);
				
			}
		});
    }
}