/**
 * Copyright Trimble Inc., 2011 - 2012 All rights reserved.
 *
 * Licensed Software Confidential and Proprietary Information of Trimble Inc.,
 * made available under Non-Disclosure Agreement OR License as applicable.
 *
 * Product Name:
 *      
 *
 * Module Name:
 *      com.widgets.joystick
 *
 * File name:
 *		LaserDirectionCommandListener.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Jan 10, 2013 1:07:59 PM
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



package com.widgets.joystick;

/**
 * @author sprabhu
 *
 */

public interface LaserDirectionCommandListener {
    
    
    //This callback used to find whether laser unit connected or not.
    public void onLaserConnectionStatus(int iConnectionStatus,int iLaserID);
    
  //This is given the movement command to laser control unit.
    public void doLaserMove(int iDirection,int iSpeed,long lLaserID);
   
  //This is callback for every movement of laser.
   public void onLaserMoveStatus(int iDirection,int iSpeed,int iLaserID,int iStaus,int iErrorCode);
   
 //This is status of current laser position and speed.
   public int[] getLasterDirection_Speed();
   
   
   public void doLaserMoveStop(int iDirection,int iSpeed,long iLaserID);
   

   public void onLaserMoveStopStatus(int iLaserID,int iStatus,int iErrorCode);
   //This is callback for stop movement of laser.

}
