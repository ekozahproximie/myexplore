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
 *      com.trimble.xspot.jc
 *
 * File name:
 *		LaserDirectionControl.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Jan 9, 2013 4:53:41 PM
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

import android.util.Log;

/**
 * @author sprabhu
 */

public class LaserDirectionControl implements JoystickMovedListener {

   

    public static final int DIR_RIGHT = 1;

    public static final int DIR_LEFT = 2;

    public static final int HIGH = 1;

    public static final int MEDIUM = 2;

    public static final int SLOW = 3;

    String TAG = "JoystickView";

    private int iDirectionFlag=0;

    private int iCurrentDirectionFlag=0;
    
    private long lLaserID=-1;
    
    private int icurrentDirection = 0;
    
    private int icurrentSpeed = 0;
    
    private LaserDirectionCommandListener mDirectionCommand =null;
    /**
     * 
     */
    public LaserDirectionControl(long lLaserID) {
        this.lLaserID=lLaserID;
    }
    
    private void findDirection_Speed(double dAngle_In_deg) {
        
        if (dAngle_In_deg >= 0 && dAngle_In_deg <=30) {
            icurrentDirection = DIR_RIGHT;
            icurrentSpeed = MEDIUM;
            iDirectionFlag = 1<<2;
            
        }else if (dAngle_In_deg >= 30 && dAngle_In_deg <= 90) {
            icurrentDirection = DIR_RIGHT;
            icurrentSpeed = SLOW;
            iDirectionFlag = 1<<3;
            
        } else if (dAngle_In_deg >= 90 && dAngle_In_deg <= 150) {
            icurrentDirection = DIR_LEFT;
            icurrentSpeed = SLOW;
            iDirectionFlag = 1<<4;
            
        } else if (dAngle_In_deg >= 150 && dAngle_In_deg <= 210) {
            icurrentDirection = DIR_LEFT;
            icurrentSpeed = MEDIUM;
            iDirectionFlag = 1<<5;
            
        } else if (dAngle_In_deg >= 210 && dAngle_In_deg <= 270) {
            icurrentDirection = DIR_LEFT;
            icurrentSpeed = HIGH;
            iDirectionFlag = 1<<6;
            
        } else if (dAngle_In_deg >= 270 && dAngle_In_deg <= 330) {
            icurrentDirection = DIR_RIGHT;
            icurrentSpeed = HIGH;
            iDirectionFlag = 1<<1;
            
        }else if (dAngle_In_deg >= 330 ) {
            icurrentDirection = DIR_RIGHT;
            icurrentSpeed = MEDIUM;
            iDirectionFlag = 1<<2;
            
        }
        
        if(iCurrentDirectionFlag != iDirectionFlag){
            iCurrentDirectionFlag=iDirectionFlag;
            switch(iCurrentDirectionFlag){
                case 1<<1:
                    //right high
                    Log.i(TAG, "Right + high");
                    break;
                case 1<<2:
                    //right medium
                    Log.i(TAG, "right + medium");
                    break;
                case 1<<3:
                    //right slow
                    Log.i(TAG, "right + slow");
                    break;
                case 1<<4:
                    //left slow
                    Log.i(TAG, "left + slow");
                    break;
                case 1<<5:
                    //left medium
                    Log.i(TAG, "left + medium");
                    break;
                case 1<<6:
                    //left high
                    Log.i(TAG, "left + high");
                    break;
            }
            if(mDirectionCommand != null){
                mDirectionCommand.doLaserMove(icurrentDirection, icurrentSpeed,lLaserID);
            }
        }
    }
    public void clearState(){
        iCurrentDirectionFlag=0;
        iDirectionFlag=0;
    }
    
    @Override
    public void OnMoved(int pan, int tilt, double angle_deg) {
       
        findDirection_Speed(angle_deg);
    }
   
    @Override
    public void OnReleased() {
        clearState();
        
    }
   
    @Override
    public void OnReturnedToCenter() {
        clearState();
        if(mDirectionCommand != null){
            mDirectionCommand.doLaserMoveStop(icurrentDirection, icurrentSpeed,lLaserID);
        }
    }
    
    /**
     * @param directionCommand the directionCommand to set
     */
    public void setDirectionCommandListener(LaserDirectionCommandListener directionCommand) {
        mDirectionCommand = directionCommand;
    }
    
}
