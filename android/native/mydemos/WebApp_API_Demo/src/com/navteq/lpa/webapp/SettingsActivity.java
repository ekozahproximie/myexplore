/**
 * 
 */
package com.navteq.lpa.webapp;

import com.navteq.lpa.NavteqLPASettingsManager;
import com.navteq.lpa.webapp.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * SettingsActivity.java
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 * 
 * This screen shows all registered settings in a list. Tapping on a
 * list item will allow you to change the value of that setting.
 *
 */
public class SettingsActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    // Set up the window to show full screen without a title
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    // Set content to xml layout for this screen
	    setContentView(R.layout.settings);
	    
	    ListView listView = (ListView)this.findViewById(R.id.settings_list_view_id);
	    
	    int[] colors = {0, 0xFF000000, 0}; 
	    listView.setDivider(new GradientDrawable(Orientation.RIGHT_LEFT, colors));
	    listView.setDividerHeight(1);
	    
	    
		Button restoreDefaults = (Button)this.findViewById(R.id.settings_restore_defaults_id);
		restoreDefaults.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				NavteqLPASettingsManager.getInstance(getApplicationContext())
					.resetSettings();
				setupSettingsListView();
			}
		});
	    
	    setupSettingsListView();
	}
	
	@Override
	protected void onResume() {
	
		super.onResume();
		
		setupSettingsListView();
	}
	
	private void setupSettingsListView()
	{
		ListAdapter listItemAdapter = new SettingsAdapter(this,
											android.R.layout.simple_list_item_1,
											NavteqLPASettingsManager.getInstance(getApplicationContext()).getSettingKeysAsArray());
		
		ListView lv = (ListView)this.findViewById(R.id.settings_list_view_id);
		lv.setAdapter(listItemAdapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
					String key = ((TextView)arg1.findViewById(R.id.setting_key_id)).getText().toString();

					if(key.equals(NavteqLPASettingsManager.ENVIRONMENT_KEY))
					{
						Intent i = new Intent(SettingsActivity.this, EnvironmentSelectionActivity.class);
						startActivity(i);
					}
					else
					{
						Intent i = new Intent(SettingsActivity.this, EditSettingActivity.class);
						i.putExtra("SETTINGKEY", key);
						i.putExtra("SETTINGVALUE", NavteqLPASettingsManager.getInstance(getApplicationContext()).getSettingValue(key));
						
						startActivity(i);
					}
				
			}
		});
	}
	
	public class SettingsAdapter extends ArrayAdapter<String> {

		String[] settingKeys;
		
		public SettingsAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
			
			settingKeys = objects;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			
			LayoutInflater inflater=getLayoutInflater();
			
			View row=inflater.inflate(R.layout.settinglistrow, parent, false);
			TextView keyLabel=(TextView)row.findViewById(R.id.setting_key_id);
			keyLabel.setText(settingKeys[position]);
		
			TextView valueLabel=(TextView)row.findViewById(R.id.setting_value_id);
			valueLabel.setText(NavteqLPASettingsManager.getInstance(getApplicationContext()).getSettingValue(settingKeys[position]));

			return row;
		}
	}
}
