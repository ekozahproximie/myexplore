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
 *	    SettingTabFragment.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     3:20:29 PM
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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author sprabhu
 *
 */
public class SettingTabFragment extends Fragment {

   private FragmentTabHost mTabHost;
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
           Bundle savedInstanceState) {
      /* mTabHost = new FragmentTabHost(getActivity());
      mTabHost.setup(getActivity(), getChildFragmentManager(),R.id.fragment1.);
     
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
       
      */

       return mTabHost;
   }

   @Override
   public void onDestroyView() {
       super.onDestroyView();
       mTabHost = null;
   }
}
