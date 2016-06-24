/*
 * Copyright (C) 2010 Prasanta Paul, http://as-m-going-on.blogspot.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prasanta;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OverlayActivity extends Activity implements MyLinearLayout.LayoutListener{
	
	String TAG = "OverlayActivity";
	String[] items = {"Item1 Its a Long Text 1", 
					  "Item2 Its a Long Text 2", 
					  "Item3 Its a Long Text 3", 
					  "Item4 Its a Long Text 4", 
					  "Item5 Its a Long Text 5", 
					  "Item6 Its a Long Text 6", 
					  "Item7 Its a Long Text 7", 
					  "Item8 Its a Long Text 8", 
					  "Item9 Its a Long Text 9", 
					  "Item10 Its a Long Text 10", 
					  "Item11 Its a Long Text 11", 
					  "Item12 Its a Long Text 12", 
					  "Item13 Its a Long Text 13", 
					  "Item14 Its a Long Text 14"};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayAdapter<String> array = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, items);
    	
        // standard List
        setContentView(R.layout.list);
        MyLinearLayout myLayout = (MyLinearLayout)findViewById(R.id.mylin_layout);
        
        
        // Create Tab Icon instances 
         
        TabIcon ti1 = new TabIcon();
        ti1.setIcon(R.drawable.mm_20_white);
        
        TabIcon ti2 = new TabIcon();
        ti2.setIcon(R.drawable.list_20);
        
        TabIcon[] tabs = {ti1, ti2};
        
        // set Tabs
        myLayout.setTabIcons(tabs);
        // set Layout click Listener
        myLayout.registerListener(this);
        
        ListView lv = (ListView)findViewById(R.id.ListView01);
        lv.setAdapter(array);
    }
    
	public void clickHandler(int selection) {
		Log.i(TAG, "Tab Handle Call back : "+ selection);
		if(selection == R.drawable.mm_20_white){
			// Do some
		}
	}
}