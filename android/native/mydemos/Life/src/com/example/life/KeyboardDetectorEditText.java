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
 *	    KeyboardDetectorEditText.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Jul 30, 20147:08:31 PM
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

import android.content.Context;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author sprabhu
 *
 */
public class KeyboardDetectorEditText extends EditText  {
   
   private ArrayList<IKeyBoardInfo> keyboardListener = new ArrayList<IKeyBoardInfo>();

   private Context mContext=null;

   /**
    * @param context
    */
   public KeyboardDetectorEditText(Context context) {
       super(context);
       mContext=context;
       setMaxAllowedCharacter("5.2", 2);
       
   }
   /**
    * @param context
    * @param attrs
    */
   public KeyboardDetectorEditText(Context context, AttributeSet attrs) {
       super(context, attrs);
       mContext=context;
       setMaxAllowedCharacter("5.2", 2);
   }

   /**
    * @param context
    * @param attrs
    * @param defStyle
    */
   public KeyboardDetectorEditText(Context context, AttributeSet attrs, int defStyle) {
       super(context, attrs, defStyle);
       mContext=context;
       setMaxAllowedCharacter("5.2", 2);
   }
   
   
   public void setMaxAllowedCharacter(final String stData,final int iDataType){
     
      int digitsBeforeZero=0; 
      int digitsAfterZero=0;
      int iMaxLength=0;
      int indexOfDot=stData.indexOf(".");
      if(indexOfDot != -1){
         digitsBeforeZero=Integer.parseInt(stData.substring(0,indexOfDot));
         digitsAfterZero=Integer.parseInt(stData.substring(indexOfDot+1));
         setDecimalLimitByPrecision(digitsBeforeZero, digitsAfterZero,true);
      }else{
         iMaxLength=Integer.parseInt(stData);
         setMaxLength(iMaxLength); 
      }
      setInputDigits(iDataType);
   }
   
   
   private void setInputDigits(final int iDataType){
      switch (iDataType) {
         case 1:
            setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
            break;
         case 2:
            
            setKeyListener(DigitsKeyListener.getInstance("0123456789.,-"));
            setInputType(getInputType()|InputType.TYPE_CLASS_PHONE);
            break;
         default:
            setKeyListener(DigitsKeyListener.getInstance());
            break;
      } 
      
      Log.i("test", "me:"+Build.MANUFACTURER);
      
   }
   private void setMaxLength(final int iMaxLength){
      InputFilter[] fArray = new InputFilter[1];
      fArray[0] = new InputFilter.LengthFilter(iMaxLength);
      setFilters(fArray);
   }
   public void setDecimalLimitByPrecision(final int digitsBeforeZero, final int digitsAfterZero,final boolean isNegative){
     
      DecimalDigitsInputFilter decimalDigitsInputFilter = new DecimalDigitsInputFilter(digitsBeforeZero, digitsAfterZero,isNegative);
      setFilters(new InputFilter[] {decimalDigitsInputFilter});
   }
  
   public class DecimalDigitsInputFilter implements InputFilter {

      Pattern mPattern;
      StringBuilder builder =null;

      public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero,final boolean isNegative) {
          DecimalFormatSymbols d = new DecimalFormatSymbols(Locale.getDefault());
          final String s = String.valueOf(d.getDecimalSeparator());
          builder= new StringBuilder();
          if(isNegative){
             mPattern = Pattern.compile("(((-)?+[1-9][0-9]{0,"+(digitsBeforeZero-1)+"}|0)\\"+s+"[0-9]{0,"+(digitsAfterZero)+"})?|(-)?+([1-9][0-9]{0,"+(digitsBeforeZero-1)+"}|0(\\"+s+"[0-9]{0,"+(digitsAfterZero)+"})?)|(-)?");
          }else{
             mPattern = Pattern.compile("(([1-9][0-9]{0,"+(digitsBeforeZero-1)+"}|0)\\"+s+"[0-9]{0,"+(digitsAfterZero)+"})?|([1-9][0-9]{0,"+(digitsBeforeZero-1)+"}|0)");
          }
      }

      @Override
      public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
         
//         builder.setLength(0);
//          Matcher matcher = mPattern.matcher( builder.append(dest.toString()).append(source.toString()).toString() );
//          if (!matcher.matches())
//              return "";
          return null;
      }
   }
   
   @Override
   public void addTextChangedListener(TextWatcher watcher) {
    
      super.addTextChangedListener(watcher);
   }
  
   public void addKeyboardStateChangedListener(IKeyBoardInfo listener) {
       if(listener != null){
           keyboardListener.add(listener);    
       }
       
   }

   public void removeKeyboardStateChangedListener(IKeyBoardInfo listener) {
       keyboardListener.remove(listener);
   }
   @Override
   public boolean onKeyPreIme(int keyCode, KeyEvent event) {
    
       
       if (keyCode == KeyEvent.KEYCODE_BACK && 
               event.getAction() == KeyEvent.ACTION_UP) {
        // User has pressed Back key. So hide the keyboard
            InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(this.getWindowToken(), 0);
            notifyKeyboardHidden();
                   return false;
           }
           return super.dispatchKeyEvent(event);
      
   }
   private void notifyKeyboardHidden() {
       for (IKeyBoardInfo listener : keyboardListener) {
           listener.onKeyboardHidden(getTag());
       }
   }
   private String getDecimalFormatSymbols(){
      
      String stData=".";
      final NumberFormat nf = NumberFormat.getInstance();
      if(nf instanceof DecimalFormat) {
          DecimalFormatSymbols sym = ((DecimalFormat) nf).getDecimalFormatSymbols();
          char decSeparator = sym.getDecimalSeparator();
          stData=String.valueOf(decSeparator);
      }
      
      return stData;
   }
   private void notifyKeyboardShown() {
       for (IKeyBoardInfo listener : keyboardListener) {
           listener.onKeyboardShown();
       }
   }

   
  
}
