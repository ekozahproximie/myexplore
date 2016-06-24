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
 *      com.neural.fragment
 *
 * File name:
 *	    FragmentTabListener.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     12:14:42 AM
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
package com.neural.fragment;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;


/**
 * @author sprabhu
 *
 */
public  class  FragmentTabListener <T extends Fragment> implements ActionBar.TabListener {
   private  Activity mActivity=null;
   private  String mTag=null;
   private  Class<T> mClass=null;
   private  Bundle mArgs=null;
   private Fragment mFragment=null;

   public FragmentTabListener(Activity activity, String tag, Class<T> clz) {
       this(activity, tag, clz, null);
   }

   public FragmentTabListener(Activity activity, String tag, Class<T> clz, Bundle args) {
       mActivity = activity;
       mTag = tag;
       mClass = clz;
       mArgs = args;

       // Check to see if we already have a fragment for this tab, probably
       // from a previously saved state.  If so, deactivate it, because our
       // initial state is that a tab isn't shown.
       mFragment = mActivity.getFragmentManager().findFragmentByTag(mTag);
       if (mFragment != null && !mFragment.isDetached()) {
           FragmentTransaction ft = mActivity.getFragmentManager().beginTransaction();
           ft.detach(mFragment);
           ft.commit();
       }
   }

   public void onTabSelected(Tab tab, FragmentTransaction ft) {
       if (mFragment == null) {
           mFragment = Fragment.instantiate(mActivity, mClass.getName(), mArgs);
           ft.add(android.R.id.content, mFragment, mTag);
       } else {
           ft.attach(mFragment);
       }
   }

   public void onTabUnselected(Tab tab, FragmentTransaction ft) {
       if (mFragment != null) {
           ft.detach(mFragment);
       }
   }

   public void onTabReselected(Tab tab, FragmentTransaction ft) {
       Toast.makeText(mActivity, "Reselected!", Toast.LENGTH_SHORT).show();
   }
}