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
 *      com.neural.fragment
 *
 * File name:
 *	    GameFragment.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     17-Jan-201512:51:04 pm
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
package com.neural.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.neural.view.MultiTouchView;


/**
 * @author sprabhu
 *
 */
public class GameFragment extends SettingAbstractFragment {

   /**
    * 
    */
   public GameFragment() {
     
   }
  
   public static GameFragment newInstance(int index) {
      GameFragment f = new GameFragment();

           // Supply index input as an argument.
           Bundle args = new Bundle();
           args.putInt("index", index);
           f.setArguments(args);

           return f;
   }
  
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      final MultiTouchView multiTouchView = new MultiTouchView(getActivity());
      return  multiTouchView;
   }


  
   @Override
   public int getShownIndex() {
      return SettingListFragment.GAME_SETTING;
   }


  
   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
  
      return false;
   }
}
