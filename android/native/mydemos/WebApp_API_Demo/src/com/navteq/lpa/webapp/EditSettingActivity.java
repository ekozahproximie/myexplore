package com.navteq.lpa.webapp;

import com.navteq.lpa.NavteqLPASettingsManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * EditSettingActivity.java
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 * 
 * This activity allows the user to change the value of a setting that is passed in.
 *
 */
public class EditSettingActivity extends Activity {

	TextView titleLabel;
	EditText editBox;
	
	String key;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // Set up the window to show full screen without a title
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    // Set content to xml layout for this screen
	    setContentView(R.layout.editsetting);
	    
	    Bundle bundle = getIntent().getExtras();
	    
	    key = bundle.getString("SETTINGKEY");
	    
	    titleLabel = (TextView)this.findViewById(R.id.setting_name_id);
	    titleLabel.setText("Enter new " + key);
	    
	    editBox = (EditText)this.findViewById(R.id.setting_edit_box_id);
	    editBox.setText(bundle.getString("SETTINGVALUE"));  
	    
	    Button save = (Button)this.findViewById(R.id.setting_edit_save_id);
	    save.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				NavteqLPASettingsManager.getInstance(getApplicationContext())
					.setSettingValue(key, editBox.getText().toString());
				finish();
			}
		});
	}
}
