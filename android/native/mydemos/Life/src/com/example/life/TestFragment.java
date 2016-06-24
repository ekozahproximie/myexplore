/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.example.life
 *
 * File name:
 *	    TestFragment.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Jul 30, 20144:11:20 PM
 *
 * Abstract:
 *
 *
 * Environment:
 *  Mobile Profile          :
 *  Mobile Configuration    :
 *
 * Notes:
 *
 * Revision History:
 *
 *
 */
package com.example.life;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;


/**
 * @author sprabhu
 *
 */
public class TestFragment extends Fragment implements View.OnClickListener{

   
   private static final String TAG="life";
   /**
    * 
    */
   public TestFragment() {
      Log.i(TAG,"fragment  public TestFragment()");
   }
 
   private TestFragment (int iTest){
      Log.i(TAG,"fragment  private TestFragment(int)");
   }
   public static TestFragment getInstance(){
      return new TestFragment(1);
   }
   @Override
   public void onAttach(Activity activity) {
      Log.i(TAG,"fragment  onAttach()");
      super.onAttach(activity);
   }
   
   @Override
   public void onActivityCreated(Bundle savedInstanceState) {
      Log.i(TAG,"fragment  onActivityCreated()");
      Log.i(TAG,"fragment  savedInstanceState:"+savedInstanceState);
      super.onActivityCreated(savedInstanceState);
      ((Button)getView().findViewById(R.id.start)).setOnClickListener(this);
      ((Button)getView().findViewById(R.id.startfor)).setOnClickListener(this);
      NumberFormat nf = NumberFormat.getInstance();
      if(nf instanceof DecimalFormat) {
          DecimalFormatSymbols sym = ((DecimalFormat) nf).getDecimalFormatSymbols();
          char decSeparator = sym.getDecimalSeparator();
          ((TextView)getView().findViewById(R.id.decimalvalue)).setText(String.valueOf(decSeparator));
      }
    
   }
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      //setRetainInstance(true);
      Log.i(TAG,"fragment  onActivityCreated()");
      Log.i(TAG,"fragment  onCreateView:"+savedInstanceState);
      return inflater.inflate(R.layout.test_fragment,container,false);
   }
   
   @Override
   public void onStart() {
      Log.i(TAG,"fragment  onStart()");
      super.onStart();
   }
   
   
   @Override
   public void onResume() {
      Log.i(TAG,"fragment  onResume()");
      super.onResume();
   }
   @Override
   public void onPause() {
      Log.i(TAG,"fragment  onPause()");
      super.onPause();
   }
   
   @Override
   public void onStop() {
      Log.i(TAG,"fragment  onStop()");
      super.onStop();
      
   }
   
   
   @Override
   public void onDestroy() {
      Log.i(TAG,"fragment  onDestroy()");
      super.onDestroy();
   }

   
   @Override
   public void onDestroyView() {
      Log.i(TAG,"fragment  onDestroyView()");
      super.onDestroyView();
   }
   
   @Override
   public void onDetach() {
      Log.i(TAG,"fragment  onDetach()");
      super.onDetach();
     
   }

   
   @Override
   public void onSaveInstanceState(Bundle outState) {
      Log.i(TAG,"fragment  onSaveInstanceState()");
      super.onSaveInstanceState(outState);
      outState.putString("test", "mytest data");
   }
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
   
   if(requestCode == 1){
      Log.i(TAG,"fragment  onActivityResult()");
   }else{
      Log.i(TAG,"fragment  default onActivityResult()");
      super.onActivityResult(requestCode, resultCode, data);
   }
}
   @Override
   public void onClick(View view) {
      switch (view.getId()) {
         case R.id.start:
           startActivityForResult(new Intent(getActivity(),B.class),1);
             break;
         case R.id.startfor:
            getActivity().startActivityForResult(new Intent(getActivity(),B.class),1);
             break;
         default:
             break;
     }
   }
   
   
   
}
