package com.widgets.joystick;

public interface JoystickMovedListener {
	public void OnMoved(int pan, int tilt,double angle_deg);
	public void OnReleased();
	public void OnReturnedToCenter();
}
