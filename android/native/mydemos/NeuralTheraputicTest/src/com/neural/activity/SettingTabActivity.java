/**
 * Copyright Trimble Inc., 2013 - 2014 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.neural.activity
 *
 * File name:
 *	    SettingTabActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     12:12:37 AM
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
package com.neural.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTabHost;
import android.util.TypedValue;
import android.widget.TabHost;
import android.widget.TextView;

import com.neural.demo.R;
import com.neural.fragment.AlertDialogFragment.DialogClickListener;
import com.neural.fragment.DeviceFragment;
import com.neural.fragment.GraphFragment;
import com.neural.fragment.MediaFragmet;
import com.neural.fragment.RehabFragment;


/**
 * @author sprabhu
 *
 */
public class SettingTabActivity extends NeuralBaseActivity  {

   //private static final String TAB_SELECTION_INDEX="tab_index";
   private FragmentTabHost mTabHost;
   private static final int TEXT_SIZE=17;
   private static final String TAB_SELECT_INDEX="tab_select_index";
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.setting_tab);
       
       mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
       mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
     
       mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.graph_settings)).
             setIndicator(getString(R.string.graph_settings),
                   getResources().getDrawable( R.drawable.button_graph_setting)),
                   GraphFragment.class, null);
       
       mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.rehab_settings)).
             setIndicator(getString(R.string.rehab_settings),
                   getResources().getDrawable( R.drawable.button_rehab_setting)),
                   RehabFragment.class, null);
       
       mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.media_settings)).
             setIndicator(getString(R.string.media_settings),
                   getResources().getDrawable( R.drawable.button_media_setting)),
                   MediaFragmet.class, null);
       
       mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.device_settings)).
             setIndicator(getString(R.string.device_settings),
                   getResources().getDrawable( R.drawable.button_device_setting)),
                   DeviceFragment.class, null);
       for(int i=0; i < mTabHost.getTabWidget().getChildCount(); i++){
          //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tabbar_bg_nor);
          mTabHost.getTabWidget().getChildAt(i).setTag(String.valueOf(i));
          TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
          if(tv != null){
          tv.setTextColor(Color.WHITE);
         // tv.setTextAppearance(this, Typeface.BOLD);
          tv.setTextAppearance(this,
                android.R.style.TextAppearance_Medium);
          tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
          tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,TEXT_SIZE);
          }
       }
       mTabHost.setCurrentTab(getLastTabSelectIndex());
       mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
          @Override
          public void onTabChanged(String tabId) {
              
//                     Activity currentActivity = getCurrentActivity();
//             for(int i=0; i < tabHost.getTabWidget().getChildCount(); i++){
//                tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tabbar_bg_nor);
//                
//             }
             int index=0;
             if(tabId.equals(getString(R.string.graph_settings))){
                
                index=0;
               
             }else if(tabId.equals(getString(R.string.rehab_settings))){
                
                index=1;
               
             }else if(tabId.equals(getString(R.string.media_settings))){
                
            
                index=2;
             }else if(tabId.equals(getString(R.string.device_settings))){
                
                index=3;
               
             }
             
          }
      });
   }
  
   @Override
   protected void onPause() {
      storeTabSelectIndex(mTabHost.getCurrentTab());
      super.onPause();
   }
   private void storeTabSelectIndex(int index){
      final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(this);
      final Editor editor =preferences.edit();
      editor.putInt(TAB_SELECT_INDEX, index);
      editor.commit();
      
   }
   private int getLastTabSelectIndex(){
      final SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(this);
      return preferences.getInt(TAB_SELECT_INDEX, 0);
      
   }
   
       /*final ActionBar actionBar = getActionBar();
 
    // Hide Actionbar Icon
       actionBar.setDisplayShowHomeEnabled(false);

       // Hide Actionbar Title
       actionBar.setDisplayShowTitleEnabled(false);

       // Create Actionbar Tabs
       actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
       //�bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
       

       actionBar.addTab(actionBar.newTab()
               .setText(getString(R.string.graph_settings))
               .setIcon(R.drawable.button_graph_setting)
               .setTabListener(new FragmentTabListener<GraphFragment>(
                       this,getString(R.string.graph_settings), GraphFragment.class)));
       actionBar.addTab(actionBar.newTab()
               .setText(getString(R.string.rehab_settings))
               .setIcon(R.drawable.button_rehab_setting)
               .setTabListener(new FragmentTabListener<RehabFragment>(
                       this, getString(R.string.rehab_settings), RehabFragment.class)));
       actionBar.addTab(actionBar.newTab()
               .setText(getString(R.string.button_media_setting))
               .setIcon(R.drawable.button_device_setting)
               .setTabListener(new FragmentTabListener<MediaFragmet>(
                       this, getString(R.string.media_settings), MediaFragmet.class)));
       actionBar.addTab(actionBar.newTab()
               .setText(getString(R.string.device_settings))
               .setIcon(R.drawable.button_device_setting)
               .setTabListener(new FragmentTabListener<DeviceFragment>(
                       this, getString(R.string.device_settings), DeviceFragment.class)));

       if (savedInstanceState != null) {
           actionBar.setSelectedNavigationItem(savedInstanceState.getInt(TAB_SELECTION_INDEX, 0));
       }
   }

   @Override
   protected void onSaveInstanceState(Bundle outState) {
       super.onSaveInstanceState(outState);
       outState.putInt(TAB_SELECTION_INDEX, getActionBar().getSelectedNavigationIndex());
   }*/
   @Override
   public void onBackPressed() 
   {
      super.onBackPressed();
       this.finish();
      overridePendingTransition(R.anim.slide_in_left, R.anim.right_left);
       return;
   }
   
      private DialogClickListener dialogClickListener =null;
      
      /**
       * @param dialogClickListener the dialogClickListener to set
       */
      public void setDialogClickListener(DialogClickListener dialogClickListener) {
         this.dialogClickListener = dialogClickListener;
      }
      
       
      public void doPositiveClick(int iMessageCode, final int iData) {
         if(dialogClickListener != null){
            dialogClickListener.doPositiveClick(iMessageCode,iData);
         }
      }
      
      public void doNegativeClick() {
         if(dialogClickListener != null){
            dialogClickListener.doNegativeClick();
         }
      }
}
