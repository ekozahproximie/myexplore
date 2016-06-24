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
 *      Camera
 *
 * File name:
 *	    MyPreviewCallback.java
 *
 * Author:
 *     sprabhu
 *
 * Created On:
 *     Nov 17, 201411:44:32 PM
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
package Camera;

import android.hardware.Camera;

import com.neural.demo.VideoCapture;


/**
 * @author sprabhu
 *
 */
public class MyPreviewCallback implements Camera.PreviewCallback{

   final VideoCapture videoCapture ;
   /**
    * 
    */
   public MyPreviewCallback(VideoCapture videoCapture) {
      this.videoCapture=videoCapture;
   }

  
   @Override
   public void onPreviewFrame(byte[] data, Camera camera) {
      
      if(videoCapture != null){
         videoCapture.sendOwnSurfaceData(data, camera);
      }
      
   }
   

}
