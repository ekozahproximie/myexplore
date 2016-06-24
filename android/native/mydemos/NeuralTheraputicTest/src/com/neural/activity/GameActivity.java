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
 *      com.neural.activity
 *
 * File name:
 *	    GameActivity.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     11-Jan-20158:41:55 pm
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
package com.neural.activity;

import com.neural.fragment.GameFragment;
import com.neural.fragment.SettingListFragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;


/**
 * @author sprabhu
 *
 */
public class GameActivity  extends SettingsActivity{
   
   private GameFragment gameFragment = null;

   /**
    * 
    */
   private static final String MY_GAME = "MyGame";

   /**
    * 
    */
   public GameActivity() {
      // TODO Auto-generated constructor stub
   }

   /* (non-Javadoc)
    * @see com.neural.activity.NeuralBaseActivity#onCreate(android.os.Bundle)
    */
   @Override
   protected void onCreate(Bundle savedInstanceState) {
    
      super.onCreate(savedInstanceState);

      if (getResources().getConfiguration().orientation
              == Configuration.ORIENTATION_LANDSCAPE) {
          // If the screen is now in landscape mode, we can show the
          // dialog in-line with the list so we don't need this activity.
          finish();
          return;
      }

      
      final FragmentManager fragmentManager =getSupportFragmentManager();
      final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
      final Fragment fragment =fragmentManager.findFragmentByTag(MY_GAME);
      if(fragment == null){
          gameFragment = GameFragment.newInstance(SettingListFragment.GAME_SETTING);
         fragmentTransaction.add(android.R.id.content, gameFragment, MY_GAME); 
         fragmentTransaction.commit();
      }else{
         gameFragment=(GameFragment) fragment;
      }
      
   }
   

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
             if(gameFragment != null){
                gameFragment.onKeyDown(keyCode, event);
             }
           return super.onKeyDown(keyCode, event);
   }
}
