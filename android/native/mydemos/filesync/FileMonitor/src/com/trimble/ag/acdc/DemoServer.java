/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Spime Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *	com.trimble.ag.acdc     
 *
 * Module Name:
 *      com.trimble.ag.acdc
 *
 * File name:
 *	DemoServer.java
 *
 * Author:
 *      karthiga
 *
 * Created On:
 *     Jun 19, 20145:22:15 PM
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
package com.trimble.ag.acdc;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.trimble.ag.nabu.acdc.res.ACDCResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


/**
 * @author kmuruga
 *
 */
public class DemoServer {
private  Context appContext;
   
   private static DemoServer demoServer =null;
   
   private static final String LOG=DemoServer.class.getSimpleName();
   private DemoServer(final Context appContext) {
      this.appContext=appContext;
   }
   
   public static synchronized DemoServer getInstance(final Context context ){
      if(demoServer == null){
         demoServer=new DemoServer(context);
      }
      
      return demoServer;
   }

   public boolean isJSONFileFound(final String stFileName){
      final AssetManager assetManager =appContext.getAssets();
      boolean isAssetFileFound=false;
      try {
         assetManager.open(stFileName);
            isAssetFileFound=true;   
         
         
      } catch (IOException e) {
         Log.e(LOG, stFileName ,e);
      }
      return isAssetFileFound;
   }
   
   
   public ACDCResponse getResponse(final int iResponseCode,final String stFileName){
      ACDCResponse acdcResponse = null;
      final AssetManager assetManager =appContext.getAssets();
         InputStream inputStream=null;
         BufferedReader reader =null;
         InputStreamReader streamReader =null;
          try {
             inputStream= assetManager.open(stFileName);
             streamReader= new InputStreamReader(inputStream);
             reader = new BufferedReader(streamReader);
             StringBuilder builder  = new StringBuilder();
             String stData= null;
             while ((stData=reader.readLine()) != null) {
               builder.append(stData);
             }
             acdcResponse= new ACDCResponse(builder.toString().getBytes(), iResponseCode);
         } catch (IOException e) {
            Log.e(LOG, e.getMessage(),e);
         }finally{
            closeStream(reader);
            closeStream(streamReader);
           
         }
      
      return acdcResponse;
   }
   
   private void closeStream(final Reader reader){
      if(reader != null){
         try {
            reader.close();
         } catch (IOException e) {
            Log.e(LOG, e.getMessage(),e);
         }
      }
   }
}
