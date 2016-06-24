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
 *	    DialogFragment.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     3:28:37 PM
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.neural.activity.SettingTabActivity;
import com.neural.activity.SettingsActivity;


/**
 * @author sprabhu
 *
 */
public class AlertDialogFragment extends DialogFragment {
   
   public static final String TITLE="title";
   
   public static final String ICON="icon";
   
   public static final String POS="pos_bt_title";
   
   public static final String NEG="neg_bt_title";
   
   public static final String MESSAGE_CODE="msg_code";
   
   public static final String MESSAGE_STRING="msg_string";
   
   public static final String MESSAGE_DATA="msg_data";
   
   
   public static final String DIALOG_KEY="dialog";
   
   public interface DialogClickListener{
      public void doPositiveClick(final int iMessageCode,final int iData);
      
      public void doNegativeClick();
      
   }

   public static AlertDialogFragment newInstance(int title,String stMessage,int icon,int pos,int neg,int iMessageCode,
         int iMusclePart) {
       final AlertDialogFragment frag = new AlertDialogFragment();
       final Bundle args = new Bundle();
       args.putInt(TITLE, title);
       args.putInt(ICON, icon);
       args.putInt(POS, pos);
       args.putInt(NEG, neg);
       args.putInt(MESSAGE_CODE, iMessageCode);
       args.putString(MESSAGE_STRING, stMessage);
       args.putInt(MESSAGE_DATA, iMusclePart);
       frag.setArguments(args);
       return frag;
   }

   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
       final int title = getArguments().getInt(TITLE);
       final int icon=  getArguments().getInt(ICON);
       final int pos=  getArguments().getInt(POS);
       final int neg=  getArguments().getInt(NEG);
       final int iMessageCode=getArguments().getInt(MESSAGE_CODE);
       final int iData=getArguments().getInt(MESSAGE_DATA);
       final String stMessgae=getArguments().getString(MESSAGE_STRING);
       return new AlertDialog.Builder(getActivity())
               .setIcon(icon)
               .setTitle(title)
               .setMessage(stMessgae)
               .setPositiveButton(pos,
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int whichButton) {
                           ((SettingsActivity)getActivity()).doPositiveClick(iMessageCode,iData);
                       }
                   }
               )
               .setNegativeButton(neg,
                   new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int whichButton) {
                          ((SettingsActivity)getActivity()).doNegativeClick();
                       }
                   }
               )
               .create();
   }
}

