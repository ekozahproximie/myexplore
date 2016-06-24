/**
 * Copyright Trimble Inc., 2014 - 2015 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 * 
 *
 * Module Name: com.neural.demo
 *
 * File name: DemoDataInjector.java
 *
 * Author: sprabhu
 *
 * Created On: Feb 22, 20141:08:17 PM
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
package com.neural.demo;

import android.content.Context;

import com.neural.sensor.NtDevice.DeviceHandlerInterface;
import com.neural.sensor.NtDevice.DeviceHandlerParameter;
import com.neural.sensor.NtDeviceManagement;
import com.neural.util.Utils;
import com.neural.view.GraphView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author sprabhu
 *
 */
public final class DemoDataInjector implements DeviceHandlerInterface {

   private transient Context                 context            = null;

   private transient boolean                 isStopDemo         = false;

   private static final String               THREAD_NAME        = "demo_thread_name";

   private OnDemoDataUpdateListener          dataUpdateListener = null;
   /**
    * 
    */
   private static final String               A_B_C_D            = "a:b:c:d";
   /**
    * 
    */
   private static final String               DEMO_DEVICE        = "Demo device";
   private static transient DemoDataInjector demoDataInjector   = null;

   private transient NtDeviceManagement      ntDeviceManagement = null;

   private GraphView                         graphView          = null;

   private DemoDataInjector(final Context context) {
      if (demoDataInjector != null) {
         throw new RuntimeException("Use getInstance method !");
      }
      this.context = context;
      ntDeviceManagement = NtDeviceManagement.getDefaultDeviceManager(context.getApplicationContext());
   }

   public static DemoDataInjector getInstance(final Context context) {
      if (demoDataInjector == null) {
         demoDataInjector = new DemoDataInjector(context);
      }
      return demoDataInjector;
   }

   public void connectDeviceAsDemo(
         final OnDemoDataUpdateListener dataUpdateListener, GraphView graphView) {
      this.dataUpdateListener = dataUpdateListener;
      isStopDemo = false;
      this.graphView = graphView;
      if (ntDeviceManagement
            .getNtDeviceByMuscleGroup(NtDeviceManagement.MUSCLE_GROUP_DEMO) == null) {
         ntDeviceManagement.connect(DEMO_DEVICE, A_B_C_D, this,
               NtDeviceManagement.MUSCLE_GROUP_DEMO, null);
      }

      startDemoThread();
   }

   public void disConnectDeviceAsDemo() {
      dataUpdateListener = null;
      isStopDemo = true;
      ntDeviceManagement.disconnect(NtDeviceManagement.MUSCLE_GROUP_DEMO);
      removeDevice();
   }

   private void startDemoThread() {
      if(Utils.isThreadRunning(THREAD_NAME)){
         return;
      }
      final Thread thread = new Thread(runnable, THREAD_NAME);
      thread.start();
   }

   private final Runnable runnable = new Runnable() {

                                      @Override
                                      public void run() {
                                         readDataFromAsset();

                                      }
                                   };

   @Override
   public void handleDeviceEvent(DeviceHandlerParameter param) {

   }

   private void readDataFromAsset() {
      try {

         while (!isStopDemo) {
            final InputStream in = context.getAssets().open("demo_data.txt");
            final BufferedReader bufferedInputStream = new BufferedReader(
                  new InputStreamReader(in));

            String stData = null;
            float fData = 0.0f;
            final int iDataLimit = 100;
            int iCount = 0;
            while (!isStopDemo
                  && (stData = bufferedInputStream.readLine()) != null) {
               fData += Float.parseFloat(stData);
               iCount++;
               if (iCount == iDataLimit) {
                  if (dataUpdateListener != null) {
                     fData = fData / iDataLimit;
                     try {
                        Thread.sleep(20);
                     } catch (InterruptedException e) {
                        e.printStackTrace();
                     }
                     if(dataUpdateListener != null){
                        fData =fData *75;
                      dataUpdateListener.onDemoDataUpdate(fData);
                     }
                  }
                  iCount = 0;
                  fData = 0;
                 
               }

            }

         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         if (graphView != null) {
            graphView.clearGraph();
            // graphView=null;
         }
      }

   }

   private void removeDevice() {
      if (ntDeviceManagement != null) {
         ntDeviceManagement.removeDemoDevice();
      }
   }

   public void clear() {
      disConnectDeviceAsDemo();
      removeDevice();
   }
}
