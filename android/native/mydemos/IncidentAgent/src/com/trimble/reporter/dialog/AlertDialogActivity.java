/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.trimble.reporter.dialog
 *
 * File name:
 *		AlertDialogActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Dec 14, 2012 4:34:47 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *	Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */



package com.trimble.reporter.dialog;

import com.trimble.reporter.BaseActivity;
import com.trimble.agent.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author sprabhu
 *
 */

public class AlertDialogActivity extends Activity {
    private TextView titleView;
    private TextView messageView;
    private Button positiveButton;
    private Button neutralButton;
    private Button negativeButton;
    
    public AlertDialogActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setupContentView();
       setupViews();
       Bundle extras = getIntent().getExtras();
       if (extras != null){
          applyParameters(extras);
       }
    }
    protected void applyTextResource(Bundle extras, Button button, String key){
       if (button != null
           && extras.containsKey(key)){
          button.setVisibility(View.VISIBLE);
          int id = extras.getInt(key);
          if (id > 0){
             button.setText(id);
          }
       }
    }
    protected void applyText(Bundle extras, TextView textView, String key){
       if (textView != null
             && extras.containsKey(key)){
            textView.setVisibility(View.VISIBLE);
            textView.setText(extras.getString(key));
         }
    }
    protected void applyTextResource(Bundle extras, TextView textView, String key) {
       if (textView != null) {
          if (extras.containsKey(key)) {
             int id = extras.getInt(key);
             if (id > 0){
                textView.setText(id);
             }
          } else {
             textView.setVisibility(View.GONE);
          }
       }
    }
    protected void applyParameters(Bundle extras){
       applyTextResource(extras, titleView, BaseActivity.DIALOG_TITLE);
       applyTextResource(extras, messageView, BaseActivity.ALERT_MESSAGE);
       applyText(extras, messageView, BaseActivity.ALERT_MESSAGE_TEXT);
       applyTextResource(extras, positiveButton, BaseActivity.ALERT_POS);
       applyTextResource(extras, negativeButton, BaseActivity.ALERT_NEG);
       applyTextResource(extras, neutralButton, BaseActivity.ALERT_NEUTRAL);
    }
    protected void setupContentView(){
       setContentView(R.layout.alert_dialog);
    }
    protected void setupViews(){
       titleView = (TextView) findViewById(R.id.title_view);
       messageView = (TextView) findViewById(R.id.message_view);
       positiveButton = (Button) findViewById(R.id.positive_button);
       positiveButton.setOnClickListener(new OnClickListener(){
          @Override
          public void onClick(View v) {
             setResult(BaseActivity.ALERT_RESULT_POS);
             finish();
          }
       });
       negativeButton = (Button) findViewById(R.id.negative_button);
       negativeButton.setOnClickListener(new OnClickListener(){
          @Override
          public void onClick(View v) {
             setResult(BaseActivity.ALERT_RESULT_NEG);
             finish();
          }
       });
       neutralButton = (Button) findViewById(R.id.neutral_button);
       neutralButton.setOnClickListener(new OnClickListener(){
          @Override
          public void onClick(View v) {
             setResult(BaseActivity.ALERT_RESULT_NEUTRAL);
             finish();
          }
       });
    }
    public AlertDialogActivity setTitleResource(int titleId){
       titleView.setText(titleId);
       return this;
    }
    public AlertDialogActivity setTitle(String titleString){
       titleView.setText(titleString);
       return this;
    }
   
    public AlertDialogActivity setMessage(int messageId){
       messageView.setText(messageId);
       return this;
    }
    public AlertDialogActivity setPositiveButton(int labelId, OnClickListener onClickListener){
       positiveButton.setText(labelId);
       positiveButton.setOnClickListener(onClickListener);
       return this;
    }
    public AlertDialogActivity setNegativeButton(int labelId, OnClickListener onClickListener){
       negativeButton.setText(labelId);
       negativeButton.setOnClickListener(onClickListener);
       return this;
    }
    public AlertDialogActivity setNeutralButton(int labelId, OnClickListener onClickListener){
       neutralButton.setText(labelId);
       neutralButton.setOnClickListener(onClickListener);
       return this;
    }
}
