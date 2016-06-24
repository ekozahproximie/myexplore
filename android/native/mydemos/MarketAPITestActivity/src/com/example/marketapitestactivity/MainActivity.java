package com.example.marketapitestactivity;

import com.gc.android.market.api.MarketSession; 
import com.gc.android.market.api.MarketSession.Callback; 
import com.gc.android.market.api.model.Market.AppsRequest; 
import com.gc.android.market.api.model.Market.AppsResponse; 
import com.gc.android.market.api.model.Market.ResponseContext; 
 
import android.app.Activity; 
import android.os.Bundle; 
import android.provider.Settings.Secure; 
import android.util.Log; 
import android.view.Menu;


public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Market API", "Started"); 
        
        String email = "mapmantest@gmail.com"; 
        String pass = "mapman"; 
        String AndroidId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID); 
 
        MarketSession session = new MarketSession(); 
        session.login(email,pass); 
      //  session.getContext().setAndroidId(AndroidId); 
 
        String query = "maps"; 
        AppsRequest appsRequest = AppsRequest.newBuilder() 
                                        .setQuery(query) 
                                        .setStartIndex(0).setEntriesCount(10) 
                                        .setWithExtendedInfo(true) 
                                        .build(); 
 
        session.append(appsRequest, new Callback<AppsResponse>() { 
                 @Override 
                 public void onResult(ResponseContext context, AppsResponse response) { 
                        Log.d("Market API", "Got response"); 
                 } 
        }); 
 
        session.flush(); 

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
