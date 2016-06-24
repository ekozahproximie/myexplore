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
 *	    A.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Jul 30, 20144:09:43 PM
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;


/**
 * @author sprabhu
 *
 */
public class A extends FragmentActivity {
   
   private static final String TAG="life";
   
   private static final String TEST_FRAGMENT="fragment_tag";
   /**
    * 
    */
   public A() {
     
   }
 
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.i(TAG,"A  onCreate() "+savedInstanceState);
      FragmentManager fragmentManager =getSupportFragmentManager();
      FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
    
      final Fragment fragment= fragmentManager.findFragmentByTag(TEST_FRAGMENT);
      if(fragment == null){
       TestFragment testfragment =TestFragment.getInstance();
       fragmentTransaction.replace(android.R.id.content, testfragment,TEST_FRAGMENT);
       fragmentTransaction.commit();
      }
      
   }
   
   
   
   @Override
   public void onStart() {
      Log.i(TAG,"A  onStart()");
      super.onStart();
   }
   
   
   @Override
   public void onResume() {
      Log.i(TAG,"A  onResume()");
      super.onResume();
   }
   @Override
   public void onPause() {
      Log.i(TAG,"A  onPause()");
      super.onPause();
   }
   
   @Override
   public void onStop() {
      Log.i(TAG,"A  onStop()");
      super.onStop();
   }
   
   @Override
   protected void onActivityResult(int arg0, int arg1, Intent arg2) {
   
      if(arg0 == 1){
         Log.i(TAG,"A  onActivityResult()");
      }else{
         Log.i(TAG,"A  default onActivityResult()");
        super.onActivityResult(arg0, arg1, arg2);
      }
   }
   
   @Override
   protected void onSaveInstanceState(Bundle outState) {
      Log.i(TAG,"A  onSaveInstanceState()");
      super.onSaveInstanceState(outState);
     /// outState.putLong("test", 123);
   }
  
   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState) {
      Log.i(TAG,"A  onRestoreInstanceState()");
      super.onRestoreInstanceState(savedInstanceState);
   }
   @Override
   public void onDestroy() {
      Log.i(TAG,"A  onDestroy()");
      super.onDestroy();
   }

   public void showProgressDialogFragment(final String stMessage){
      ProgressDialogFragment progressDialog = ProgressDialogFragment.newInstance(stMessage);
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      Fragment prev = getSupportFragmentManager().findFragmentByTag(
            ProgressDialogFragment.PROGRESS_FRAGMENT_TAG);
      if (prev == null) {
         transaction.add(progressDialog, ProgressDialogFragment.PROGRESS_FRAGMENT_TAG);
         transaction.commit(); 
      }
     // getSupportFragmentManager().executePendingTransactions();
      
   }
   
   public void dismissProgressDialogFragment(){
      Fragment prev = getSupportFragmentManager().findFragmentByTag(
            ProgressDialogFragment.PROGRESS_FRAGMENT_TAG);
      if (prev != null && prev instanceof ProgressDialogFragment) {
         Log.i("test", "dialog dismissed");
         ((ProgressDialogFragment) prev).dismissDialog();
      }
   }
   
}
