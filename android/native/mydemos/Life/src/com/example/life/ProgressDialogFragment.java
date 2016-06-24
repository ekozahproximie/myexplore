/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 * 
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 * 
 * Product Name:Lono
 * 
 * 
 * Module Name: com.trimble.lono.dialog
 * 
 * File name: ProgressDialogFragment.java
 * 
 * Author: karthiga
 * 
 * Created On: Mar 2, 201410:47:23 PM
 * 
 * Abstract:
 * 
 * 
 * Environment: Mobile Profile : Mobile Configuration :
 * 
 * Notes:
 * 
 * Revision History:
 * 
 * 
 */
/**
 * 
 */
package com.example.life;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author kmuruga
 * 
 */
public class ProgressDialogFragment extends DialogFragment {

   public static final String  PROGRESS_FRAGMENT_TAG = "dialogFragment";

   private final static String MESSAGE_ARG           = "message";

   private boolean             isCancel              = false;

   private static final String DIALOG_CANCELLED      = "isCancel";

   private static final String LOG                   = ProgressDialogFragment.class
                                                           .getSimpleName();

   public ProgressDialogFragment() {

   }

   public static ProgressDialogFragment newInstance(final String stMessage) {
      ProgressDialogFragment frag = new ProgressDialogFragment();
      Bundle args = new Bundle();
      args.putString(MESSAGE_ARG, stMessage);
      frag.setArguments(args);
      return frag;
   }

   public static ProgressDialogFragment newInstance(final int iMessage) {
      ProgressDialogFragment frag = new ProgressDialogFragment();
      Bundle args = new Bundle();
      args.putInt(MESSAGE_ARG, iMessage);
      frag.setArguments(args);
      return frag;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      Log.i(LOG, "onCreate :" + savedInstanceState);
      super.onCreate(savedInstanceState);
      if(savedInstanceState == null){
         setDialogCacelledState(getActivity(), false);
      }
   }
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
         Bundle savedInstanceState) {
      Log.i(LOG, "onCreateView :" + savedInstanceState);
      return super.onCreateView(inflater, container, savedInstanceState);
   }
   @Override
   public void onActivityCreated(Bundle savedInstance) {
      Log.i(LOG, "onActivityCreated :" + savedInstance);
      super.onActivityCreated(savedInstance);
      isCancel = getDialogCacelledState();

   }
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      Log.i(LOG, "onCreateDialog :" + savedInstanceState);
      // isCancel = getDialogCacelledState();
      // if(isCancel){
      // return null;
      // }

      final ProgressDialog dialog = new ProgressDialog(getActivity());

      String message = getDialogMessage();
      dialog.setMessage(message);

      dialog.setProgressStyle(R.style.DefaultProgressDialogTheme);
      dialog.setCancelable(false);
      dialog.setCanceledOnTouchOutside(false);
      setRetainInstance(true);

      // Disable the back button
      OnKeyListener keyListener = new OnKeyListener() {

         @Override
         public boolean onKey(DialogInterface dialog, int keyCode,
               KeyEvent event) {

            if (keyCode == KeyEvent.KEYCODE_BACK) {
               return true;
            }
            return false;
         }

      };
      dialog.setOnKeyListener(keyListener);
      return dialog;
   }

   @Override
   public void onResume() {
      super.onResume();
      Log.i(LOG, "onResume:" + isCancel);
      if (isCancel) {
         dismissDialog();
      }

      if (((ProgressDialog) getDialog()) != null) {
         ProgressDialog dialog = ((ProgressDialog) getDialog());

         String message = getDialogMessage();
         dialog.setMessage(message);
      }
   }

   public void dismissDialog() {
      isCancel = true;
      setDialogCacelledState(getActivity(), isCancel);
      Log.i("life", "isAdded"+isAdded()+"isRemoving:"+isRemoving());
      internalDialogDismiss();
   }

   private void internalDialogDismiss() {
      if (getDialog() != null) {
         getDialog().dismiss();
      } else {
         dismiss();
      }
   }

   @Override
   public void onDestroyView() {
      Log.i(LOG, "onDestroyView");
      if (getDialog() != null && getRetainInstance())
         getDialog().setDismissMessage(null);
      super.onDestroyView();
      // internalDialogDismiss();
      setDialogCacelledState(getActivity(), isCancel);
      // Log.i("test", "destroy view");
   }

   @Override
   public void onSaveInstanceState(Bundle bundle) {
      Log.i(LOG, "onSaveInstanceState :" + bundle);
      bundle.putString("hai", "poad");
      super.onSaveInstanceState(bundle);
      setDialogCacelledState(getActivity(), isCancel);
   }

  

   private boolean getDialogCacelledState() {
      final SharedPreferences preferences = PreferenceManager
            .getDefaultSharedPreferences(getActivity());
      final boolean isCancel = preferences.getBoolean(DIALOG_CANCELLED, false);
      return isCancel;
   }

   private static final String THREAD_NAME = "dialog_preference";

   public void setDialogCacelledState(final Context context,
         final boolean isDialogCancelled) {
      if (MainActivity.isThreadRunning(THREAD_NAME) || context == null) {
         return;
      }
      final Thread thread = new Thread() {

         @Override
         public void run() {
            final SharedPreferences preferences = PreferenceManager
                  .getDefaultSharedPreferences(context);
            final Editor editor = preferences.edit();
            editor.putBoolean(DIALOG_CANCELLED, isCancel);
            editor.commit();
         }

      };
      thread.setName(THREAD_NAME);
      thread.start();
      try {
         thread.join();
      } catch (InterruptedException e) {
         Log.e(LOG, e.getMessage(), e);
      }
   }

   private String getDialogMessage() {
      String message = null;
      Bundle bundle = getArguments();
      final Object object =bundle.get(MESSAGE_ARG);
      
      if (bundle.containsKey(MESSAGE_ARG) && bundle.getInt(MESSAGE_ARG, 0) != 0) {
         message = getString(bundle.getInt(MESSAGE_ARG));
      } else {
         message = bundle.getString(MESSAGE_ARG);
      }

      if (message == null) message = getString(R.string.loading);

      return message;
   }

}
