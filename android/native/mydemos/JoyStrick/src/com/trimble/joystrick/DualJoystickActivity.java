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
 *      com.trimble.joystrick
 *
 * File name:
 *		DualJoystickActivity.java
 *
 * Author:
 *      sprabhu
 *
 * Created On:
 *     Jan 8, 2013 8:02:11 PM
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



package com.trimble.joystrick;

/**
 * @author sprabhu
 *
 */

import com.widgets.joystick.DualJoystickView;
import com.widgets.joystick.JoystickMovedListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DualJoystickActivity extends Activity {

    TextView txtX1, txtY1;
    TextView txtX2, txtY2;
    DualJoystickView joystick;
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dualjoystick);

        txtX1 = (TextView)findViewById(R.id.TextViewX1);
        txtY1 = (TextView)findViewById(R.id.TextViewY1);
        
        txtX2 = (TextView)findViewById(R.id.TextViewX2);
        txtY2 = (TextView)findViewById(R.id.TextViewY2);

        joystick = (DualJoystickView)findViewById(R.id.dualjoystickView);
        
        joystick.setOnJostickMovedListener(_listenerLeft, _listenerRight);
    }

    private JoystickMovedListener _listenerLeft = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt,double angle) {
            txtX1.setText(Integer.toString(pan));
            txtY1.setText(Integer.toString(tilt));
        }

        @Override
        public void OnReleased() {
            txtX1.setText("released");
            txtY1.setText("released");
        }
        
        public void OnReturnedToCenter() {
            txtX1.setText("stopped");
            txtY1.setText("stopped");
        };
    }; 

    private JoystickMovedListener _listenerRight = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pan, int tilt,double angle) {
            txtX2.setText(Integer.toString(pan));
            txtY2.setText(Integer.toString(tilt));
        }

        @Override
        public void OnReleased() {
            txtX2.setText("released");
            txtY2.setText("released");
        }
        
        public void OnReturnedToCenter() {
            txtX2.setText("stopped");
            txtY2.setText("stopped");
        };
    }; 

}
