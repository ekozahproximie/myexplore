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
 *      com.example.android.effectivenavigation
 *
 * File name:
 *	    Test.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Mar 11, 20147:34:38 PM
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
package com.example.android.effectivenavigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;


/**
 * @author sprabhu
 *
 */
public class Test extends SherlockFragmentActivity {
   
   private static final String TAG_TAB_1 = "TAB_1";
   private static final String TAG_TAB_2 = "TAB_2";

   private static final String ARGUMENT_NAME = "name";

   
   private transient MyTabHost myTabHost =null;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       myTabHost=new MyTabHost();
      getSupportFragmentManager().beginTransaction().replace(android.R.id.content, myTabHost).commit();
       
      // setContentView(R.layout.test_main);
      
   }

   /*
    * (non-Javadoc)
    * @see android.support.v4.app.FragmentActivity#onBackPressed()
    * バックキーをタブ内フラグメントに処理させる。
    */
   @Override
   public void onBackPressed() {
       Fragment f = getSupportFragmentManager()
               .findFragmentByTag(myTabHost.mTabHost.getCurrentTabTag());
       if (f != null && f instanceof TabRoot) {
           TabRoot tabChild = (TabRoot) f;
           if (tabChild.onBackPressed()) {
               return;
           }
       }
       super.onBackPressed();
   }

   @Override
   protected void onDestroy() {
       super.onDestroy();
       myTabHost.mTabHost = null;
   }
   public static class MyTabHost extends Fragment{
      FragmentTabHost mTabHost;
    

      @Override
      public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
         
      }

      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
              Bundle savedInstanceState) {
          if (container == null) {
              return null;
          }
          final View view=inflater.inflate(R.layout.test_main, container, false);
         
          mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
          mTabHost.setup(getActivity(), getChildFragmentManager(),
                R.id.tab_container);
          mTabHost.getTabWidget().setStripEnabled(false);
          final Bundle args1 = new Bundle();
          args1.putString(ARGUMENT_NAME, TAG_TAB_1);
          mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_1)
                  .setIndicator(TAG_TAB_1), TabRoot.class, args1);

          final Bundle args2 = new Bundle();
          args2.putString(ARGUMENT_NAME, TAG_TAB_2);
          mTabHost.addTab(mTabHost.newTabSpec(TAG_TAB_2)
                  .setIndicator(TAG_TAB_2), TabRoot.class, args2);
          View view2 =mTabHost.getTabWidget().getChildTabViewAt(0);
         
          TextView title = (TextView) view2.findViewById(android.R.id.title);
          Log.i("test","width :"+  title.getWidth());
          Log.i("test","height :"+  title.getHeight());
          title.setWidth(200);
         // Log.i("test","getText :"+  title.setWidth(200));
        
          Log.i("test","getText :"+  title.getText());
          View view3 =mTabHost.getTabWidget().getChildTabViewAt(1);
          ((RelativeLayout) view3).removeAllViews();
          TextView title3 = (TextView) view3.findViewById(android.R.id.title);
          Log.i("test","getText3 :"+  title3.getText());
         final Button button = new Button(mTabHost.getContext());
         
          
          return view;
      }
      @Override
      public void onViewStateRestored(Bundle savedInstanceState) {
          super.onViewStateRestored(savedInstanceState);
         
      }

      @Override
      public void onDestroyView() {
          super.onDestroyView();
         
      }

      @Override
      public void onSaveInstanceState(Bundle outState) {
          super.onSaveInstanceState(outState);
        
      }
      /* (non-Javadoc)
       * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
       */
      @Override
      public void onActivityCreated(Bundle savedInstanceState) {
        
         super.onActivityCreated(savedInstanceState);
         //mTabHost.setup(getActivity(),getChildFragmentManager(), R.id.tab_container);

     
      }
   }
   /**
    * Tabに入れる親Fragment
    * 
    * @author 
    */
   public static class TabRoot extends Fragment implements OnClickListener {

       @Override
       public View onCreateView(LayoutInflater inflater, ViewGroup container,
               Bundle savedInstanceState) {
           if (container == null) {
               return null;
           }
           return inflater.inflate(R.layout.tab_root, container, false);
       }

       @Override
       public void onActivityCreated(Bundle savedInstanceState) {
           super.onActivityCreated(savedInstanceState);
           // 初回のみ自動で子を入れる
           if (savedInstanceState == null) {
               getChildFragmentManager()
                       .beginTransaction()
                       .addToBackStack(null)
                       .add(R.id.fragment_container, createNewChild())
                       .commit();
           }
       }

       /*
        * (non-Javadoc)
        * @see android.view.View.OnClickListener#onClick(android.view.View)
        * 子を追加する処理
        */
       @Override
       public void onClick(View v) {
           getChildFragmentManager()
                   .beginTransaction()
                   .addToBackStack(null)
                   .replace(R.id.fragment_container, createNewChild())
                   .commit();
       }

       /**
        * バックキーの処理
        * 
        * @return このFragmentが処理を行う場合TRUE
        */
       public boolean onBackPressed() {
           FragmentManager fm = getChildFragmentManager();
           if (fm.getBackStackEntryCount() == 1) {
               return false;
           } else {
               fm.popBackStack();
               return true;
           }
       }

       /**
        * 子Fragmentを作成する
        */
       Fragment createNewChild() {
           FragmentManager fm = getChildFragmentManager();
           Bundle args = getArguments();
           if (args == null) {
               args = new Bundle();
               args.putString(ARGUMENT_NAME, "Name unknown");
           } else {
               args = new Bundle(args);
           }
           args.putInt(TabChild.ARGUMENT_CHILD_COUNT, fm.getBackStackEntryCount() + 1);

           Fragment f = new TabChild();
           f.setArguments(args);
           return f;
       }
   }

   /**
    * Tabの子Fragment
    * 
    * @author noxi
    */
   public static class TabChild extends Fragment {

       private static final String ARGUMENT_CHILD_COUNT = "child_count";

       @Override
       public View onCreateView(LayoutInflater inflater, ViewGroup container,
               Bundle savedInstanceState) {
           if (container == null) {
               return null;
           }

           View v = inflater.inflate(R.layout.tab_child, container, false);
           Bundle args = getArguments();
           if (args != null
                   && args.containsKey(ARGUMENT_NAME)
                   && args.containsKey(ARGUMENT_CHILD_COUNT)) {
               String text = args.getString(ARGUMENT_NAME)
                       + "__" + args.getInt(ARGUMENT_CHILD_COUNT);
               Button button = (Button) v.findViewById(R.id.button);
               button.setText(text);

               Fragment f = getParentFragment();
               if (f instanceof OnClickListener) {
                   button.setOnClickListener((OnClickListener) f);
               }
           }

           return v;
       }

   }
 
}