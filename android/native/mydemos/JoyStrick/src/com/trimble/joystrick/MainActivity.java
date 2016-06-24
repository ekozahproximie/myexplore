package com.trimble.joystrick;

import com.widgets.joystick.JoystickView;
import com.widgets.joystick.JoystickView1;
import com.widgets.joystick.JoystickView1.OnJoystickMoveListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JoystickView1 joystickView1 =(JoystickView1)findViewById(R.id.js);
        JoystickView1.OnJoystickMoveListener listener = new  OnJoystickMoveListener() {
            
            @Override
            public void onValueChanged(int angle, int power, int direction) {
                // TODO Auto-generated method stub
                
            }
        };
        joystickView1.setOnJoystickMoveListener(listener, 2000);
        JoystickView joystickView =(JoystickView)findViewById(R.id.js1);
        joystickView.setMovementConstraint(JoystickView.CONSTRAIN_CIRCLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
