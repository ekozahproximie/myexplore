package com.trimble.ag.lono.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

   private transient CustomPivotView customPivotView = null;
   
   private transient int iStartAngle=0;
   
   private transient int iStopAngle=360;
   
   private transient int iHeading=90;
   
   private transient int iDirection = 2;
   
   private transient int iApproximation = 180;
   private transient CustomPivotView.PivotData pivotData =null; 
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      customPivotView = (CustomPivotView) findViewById(R.id.pivot_icon);
      pivotData = new CustomPivotView.PivotData(iStartAngle, iStopAngle, iHeading, 
            getString(R.string.dry), getString(R.string.none),true,"on",true);
      TextWatcher  textWatcher = new TextWatcher() {
         
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
            final String stData=s.toString().trim();
            if(stData.length() != 0){
               iStartAngle=Integer.parseInt(stData);
               handler.removeCallbacks(runnable);
               handler.postDelayed(runnable, 1000);
            }
            
         }
         
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            
         }
      };
      EditText editText1 =(EditText)findViewById(R.id.editText1);
      editText1.addTextChangedListener(textWatcher);
      EditText editText2 =(EditText)findViewById(R.id.editText2);
      editText2.addTextChangedListener( new TextWatcher() {
         
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
          final String stData=s.toString().trim();
          if(stData.length() != 0){
             iStopAngle=Integer.parseInt(stData);
             handler.removeCallbacks(runnable);
             handler.postDelayed(runnable, 1000);
          }
          
         }
         
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void afterTextChanged(Editable s) {
          
            
         }
      });
      
      EditText editText3 =(EditText)findViewById(R.id.editText3);
      editText3.addTextChangedListener( new TextWatcher() {
         
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
          final String stData=s.toString().trim();
          if(stData.length() != 0){
             iHeading=Integer.parseInt(stData);
             handler.removeCallbacks(runnable);
             handler.postDelayed(runnable, 1000);
          }
          
         }
         
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            
         }
         
         @Override
         public void afterTextChanged(Editable s) {
          
            
         }
      });
      EditText editText4 =(EditText)findViewById(R.id.editText4);
      editText4.addTextChangedListener( new TextWatcher() {
         
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count) {
          final String stData=s.toString().trim();
          if(stData.length() != 0){
             iApproximation=Integer.parseInt(stData);
             handler.removeCallbacks(runnable);
             handler.postDelayed(runnable, 1000);
          }
          
         }
         
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            
         }
         
         @Override
         public void afterTextChanged(Editable s) {
          
            
         }
      });
      
   }
   private Runnable runnable = new Runnable() {
      
      @Override
      public void run() {
         pivotData.setHeading(iHeading);
         pivotData.setStartAngle(iStartAngle);
         pivotData.setStopAngle(iStopAngle);
         pivotData.setRotationDir(iDirection);
         pivotData.setDepth(6.1214);
         pivotData.setApplicationRate(7.5072);
         pivotData.setPivotMinRotationPeriod(6.8);
         pivotData.setApporximateHeading(iApproximation);
         pivotData.setTimeLastResport(System.currentTimeMillis()-60*60*1000);
         customPivotView.updateData(pivotData);
         
      }
   };
 private Handler handler = new Handler();
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.btn_dry: {

            pivotData.setSubstanceType(CustomPivotView.SUBSTANCE_DRY);
            handler.postDelayed(runnable, 0);
            break;
         }
         case R.id.btn_water: {
            pivotData.setSubstanceType(CustomPivotView.SUBSTANCE_WATER);
            handler.postDelayed(runnable, 0);
            break;
         }
         case R.id.btn_fertigation: {

            pivotData.setSubstanceType(CustomPivotView.SUBSTANCE_FERTIGATION);
            handler.postDelayed(runnable, 0);
            break;
         }
         case R.id.btn_effluent: {
            pivotData.setSubstanceType(CustomPivotView.SUBSTANCE_EFFLUENT);
            handler.postDelayed(runnable, 0);
           
            break;
         }
         case R.id.btn_direction: {
            final int iRotationDir=pivotData.getRotationDir();
            iDirection =(iRotationDir == CustomPivotView.ROTATION_BACKWARD ? CustomPivotView.ROTATION_FORWARD: CustomPivotView.ROTATION_BACKWARD);
            handler.postDelayed(runnable, 0);
           
            break;
         }
         default:
            break;

      }
   }

}
