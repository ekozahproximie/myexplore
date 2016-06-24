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
 *		GameControls.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Jan 8, 2013 8:13:24 PM
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

/*Copyright Edward Nutting. This may be adapted but all adaptations must at least credit myself and http://blog.trostalex.com/?p=114 as the original sources.*/
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
 
public class GameControls implements OnTouchListener{
 
    public enum ControlModes
    {
        Game,
        Menu
    }
    
        //The centre x/y coordinates of the joystick thumb pad
    public static int initx = 550;
    public static int inity = 400;
 
        //The min x/y and max x/y coordinates for the joystick touching point - defines the box surrounds the joystick basically
    public static int minx = 0;
    public static int miny = 0;
    public static int maxx = 0;
    public static int maxy = 0;
    
        //The exact x/y coordinates of the latest point being touched on the screen
    public Point _touchingPoint = new Point(initx,inity);
        //The angle the thumb pad is from the centre of the joystick in degrees.
        //Where 0 = south on the screen or in other words 1 unit in the direction of 0 degrees would mean an increase of 1 in the y coordinates
    public double ControllerAngle = 0.0;
        //Distance from the centre of the joystick to the thumb pad - set to a maximum of maxx - minx so that the thumb pad actually follows a circle round.
    private double ControllerDistance = 0.0;
        //The percentage (as decimal number so 0.97 not 97%) distance of the the thumb pad from the centre of the joystick
        //Calculated by (ControllerDistance / ((maxx - minx) / 2)) or in simplified form (ControllerDistance * 2 / maxx - minx)
    public float ControllerPercentage = 0.0f;
    private Boolean _dragging = false;
    
        //The x/y coordinates and width/height of the fire button
    public static int FireButtonX = 0;
    public static int FireButtonY = 0;
    public static int FireButtonWidth = 0;
    public static int FireButtonHeight = 0;
        //Whether the fire button is currently being pressed or not
    public boolean FireButton_Pressed = false;
 
        //The current control mode - default is Game (see constructor below)
    public ControlModes ControlsMode;
    
    public GameControls()
    {
        super();
        ControlsMode = ControlModes.Game;
    }
    
    public boolean onTouch(View v, MotionEvent event) {
        update(event);
        return true;
    }
 
    private MotionEvent lastEvent;
    public void update(MotionEvent event){
 
        if (event == null && lastEvent == null)
        {
            return;
        }else if(event == null && lastEvent != null){
            event = lastEvent;
        }else{
            lastEvent = event;
        }
        
        if(ControlsMode == ControlModes.Game)
        {
            for(int i = 0; i < event.getPointerCount(); i++)
            {
                int x = (int)event.getX(i);
                int y = (int)event.getY(i);
                
                boolean IsFireButton = false;
                if(x > FireButtonX - 30 && x < FireButtonX + FireButtonWidth + 30 &&
                        y > FireButtonY - 30 && y < FireButtonY + FireButtonHeight + 30)
                    IsFireButton = true;
                
                //drag drop 
                if (event.getActionIndex() == i && (event.getAction() == MotionEvent.ACTION_DOWN || 
                        event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN)){
                    if(IsFireButton)
                        FireButton_Pressed = true;
                    else
                        _dragging = true;
                }else if (event.getActionIndex() == i && (event.getAction() == MotionEvent.ACTION_UP || 
                        event.getAction() == MotionEvent.ACTION_POINTER_2_UP)){
                    if(IsFireButton)
                        FireButton_Pressed = false;
                    else
                        _dragging = false;
                }
                
                if(!IsFireButton)
                {
                    _touchingPoint = new Point();
                    
                    if ( _dragging ){
                        // get the pos
                        _touchingPoint.x = x;
                        _touchingPoint.y = y;
            
                        // bound to a box
                        if( _touchingPoint.x < minx){
                            _touchingPoint.x = minx;
                        }
                        if ( _touchingPoint.x > maxx){
                            _touchingPoint.x = maxx;
                        }
                        if (_touchingPoint.y < miny){
                            _touchingPoint.y = miny;
                        }
                        if ( _touchingPoint.y > maxy ){
                            _touchingPoint.y = maxy;
                        }
            
                        //get the angle
                        ControllerAngle = Math.atan2(_touchingPoint.x - initx,_touchingPoint.y - inity)/(Math.PI/180);
                        //Atan2 returns negative values for northen hemisphere angles and positive for below. 
                                                //I wanted all positive with a range of 0-360 so this code sorts that out.
                                                ControllerAngle += 360;
                        if(ControllerAngle > 360)
                        {
                            ControllerAngle = ControllerAngle - 360;
                        }
                        
                    }else if (!_dragging)
                    {
                        // Snap back to center when the joystick is released
                        _touchingPoint.x = (int) initx;
                        _touchingPoint.y = (int) inity;
                    }
                
                                        //Using Pythagoras to calculate distance from centre.
                    double a = _touchingPoint.x - initx;
                    double b = _touchingPoint.y - inity;
                                        //Use the absolute (i.e. non-negative) value
                    ControllerDistance = Math.abs(Math.sqrt((a * a) + (b * b)));
                    ControllerDistance = ControllerDistance > (maxx - minx) / 2 ? (maxx - minx) / 2 : ControllerDistance;
                    ControllerPercentage = (float)(ControllerDistance * 2) / (maxx - minx);
                }
            }
        }
        else if(ControlsMode == ControlModes.Menu)
        {
            if((event.getAction() == MotionEvent.ACTION_DOWN || 
                        event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN))
            {
                _touchingPoint = new Point((int)event.getX(), (int)event.getY());
            }
            else if((event.getAction() == MotionEvent.ACTION_UP || 
                        event.getAction() == MotionEvent.ACTION_POINTER_2_UP))
            {
                _touchingPoint = null;
            }
        }
    }/*
      * This will handle direction and acceleration in joystick where direction
      * is based on where the user points it (0 degrees is south not north as
      * you may expect but it worked for my application) and acceleration is
      * based on the (calculated by my code and provided value of) the
      * percentage distance of the joystick thumb pad from the centre of the
      * game pad. The code does not include rendering but this can easily be
      * done from the given variables. All static variables must be set up
      * properly before calling update etc. The code also allows a second button
      * which is just a push button which I used (and called) FireButton, hence
      * FireButton_IsPressed. The code successfully handles multi-touch input of
      * up to two pointers. Finally note the code also allows input mode to be
      * set to menu, in which case the _touchingPoint is simply set, reducing
      * computation and simplifying other code (also handles multi-touch on a
      * menu of up to 2 pointers, eliminating most of the risk of hitting two
      * menu items at once).
      */
}
