package com.neural.activity;




import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.neural.constant.Constants;
import com.neural.demo.R;

/**
 * @author mrueger
 *
 */
public class AlertDialogActivity extends Activity {
   private TextView titleView;
   private TextView messageView;
   private ImageView iconView;
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
   protected void applyIconResource(Bundle extras,ImageView  imageView, String key){
      if (imageView != null
          && extras.containsKey(key)){
         imageView.setVisibility(View.VISIBLE);
         int id = extras.getInt(key);
         if (id > 0){
            imageView.setBackgroundDrawable(null);
            imageView.setImageResource(id);
         }
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
      applyTextResource(extras, titleView, Constants.Dialog.DIALOG_TITLE);
      applyTextResource(extras, messageView, Constants.Dialog.ALERT_MESSAGE);
      applyText(extras, messageView, Constants.Dialog.ALERT_MESSAGE_TEXT);
      applyTextResource(extras, positiveButton, Constants.Dialog.ALERT_POS);
      applyTextResource(extras, negativeButton, Constants.Dialog.ALERT_NEG);
      applyTextResource(extras, neutralButton, Constants.Dialog.ALERT_NEUTRAL);
      applyIconResource(extras, iconView,  Constants.Dialog.DIALOG_TITLE_ICON);
   }
   protected void setupContentView(){
      setContentView(R.layout.alert_dialog);
   }
   protected void setupViews(){
      titleView = (TextView) findViewById(R.id.title_view);
      messageView = (TextView) findViewById(R.id.message_view);
      iconView = (ImageView) findViewById(R.id.icon_view);
      positiveButton = (Button) findViewById(R.id.positive_button);
      positiveButton.setOnClickListener(new OnClickListener(){
         @Override
         public void onClick(View v) {
            setResult(Constants.Dialog.ALERT_RESULT_POS);
            finish();
         }
      });
      negativeButton = (Button) findViewById(R.id.negative_button);
      negativeButton.setOnClickListener(new OnClickListener(){
         @Override
         public void onClick(View v) {
            setResult(Constants.Dialog.ALERT_RESULT_NEG);
            finish();
         }
      });
      neutralButton = (Button) findViewById(R.id.neutral_button);
      neutralButton.setOnClickListener(new OnClickListener(){
         @Override
         public void onClick(View v) {
            setResult(Constants.Dialog.ALERT_RESULT_NEUTRAL);
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
   public AlertDialogActivity setIcon(int iconId){
      iconView.setImageResource(iconId);
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
