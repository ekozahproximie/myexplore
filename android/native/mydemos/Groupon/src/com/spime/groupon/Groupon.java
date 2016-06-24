package com.spime.groupon;

import android.app.Activity;
import android.os.Bundle;

public class Groupon extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        GrouponManager gManager= GrouponManager.getInstance();
//        gManager.sendRequest("http://api.groupon.com/v2/deals?division_id=san-francisco&client_id=2b804fa0b6f4d4cccac963669f7969fbbebb22ae&show=all&lat=37.775464,lng=-122.419624&radius=8 ");
    }
}