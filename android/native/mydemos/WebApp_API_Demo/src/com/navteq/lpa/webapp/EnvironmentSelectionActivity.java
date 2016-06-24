package com.navteq.lpa.webapp;

import com.navteq.lpa.NavteqLPASettingsManager;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * EditSettingActivity.java
 * 
 * @author Jeff Cardillo
 * @copyright 2011 Navteq, Inc. All rights reserved.
 * 
 * This activity allows the user to change the value of the environment setting.
 *
 */
public class EnvironmentSelectionActivity extends Activity {

	TextView titleLabel;
	EditText editBox;
	
	String key;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    key = NavteqLPASettingsManager.ENVIRONMENT_KEY;
	    
	    // Set up the window to show full screen without a title
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    // Set content to xml layout for this screen
	    setContentView(R.layout.environmentselection);
	    
	    final RadioGroup environmentGroup = (RadioGroup)findViewById(R.id.environmentGroup);
	    
	     NavteqLPASettingsManager settingsManager = NavteqLPASettingsManager.getInstance(this.getApplicationContext());
	    
	    RadioButton newRadioButton = new RadioButton(this);
	    newRadioButton.setText(NavteqLPASettingsManager.ENV_STAGE_KEY);
	    environmentGroup.addView(newRadioButton);
	    newRadioButton.setTextColor(Color.BLACK);
    	newRadioButton.setChecked(newRadioButton.getText().equals(settingsManager.
	    		getSettingValue(NavteqLPASettingsManager.ENVIRONMENT_KEY)));
	    
	    newRadioButton = new RadioButton(this);
	    newRadioButton.setText(NavteqLPASettingsManager.ENV_QA_KEY);
	    environmentGroup.addView(newRadioButton);
	    newRadioButton.setTextColor(Color.BLACK);
    	newRadioButton.setChecked(newRadioButton.getText().equals(settingsManager.
	    		getSettingValue(NavteqLPASettingsManager.ENVIRONMENT_KEY)));
    	
	    newRadioButton = new RadioButton(this);
	    newRadioButton.setText(NavteqLPASettingsManager.ENV_INT_KEY);
	    environmentGroup.addView(newRadioButton);
	    newRadioButton.setTextColor(Color.BLACK);
    	newRadioButton.setChecked(newRadioButton.getText().equals(settingsManager.
	    		getSettingValue(NavteqLPASettingsManager.ENVIRONMENT_KEY)));	   
	    
	    Button save = (Button)this.findViewById(R.id.setting_edit_save_id);
	    save.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				RadioButton selected = (RadioButton)findViewById(environmentGroup.getCheckedRadioButtonId());
				
				NavteqLPASettingsManager.getInstance(getApplicationContext())
					.setSettingValue(key, selected.getText().toString());
				finish();
			}
		});
	}
}
