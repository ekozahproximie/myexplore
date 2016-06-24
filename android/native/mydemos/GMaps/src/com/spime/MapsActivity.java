package com.spime;


import android.os.Bundle;


import com.google.android.maps.MapActivity;

public class MapsActivity extends MapActivity {
	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
