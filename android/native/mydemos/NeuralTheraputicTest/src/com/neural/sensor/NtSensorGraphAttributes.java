package com.neural.sensor;

public class NtSensorGraphAttributes {

	public static final int DEFAULT_MAX_HISTORY = 1000;
	
	private float speed = 1.0f;
	private int maxHistoryToKeep;
	
	public NtSensorGraphAttributes() {
		maxHistoryToKeep = DEFAULT_MAX_HISTORY;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public int getMaxHistoryToKeep() {
		return maxHistoryToKeep;
	}
	
	public void setMaxHistoryToKeep(int maxHistoryToKeep) {
		this.maxHistoryToKeep = maxHistoryToKeep;
	}
}
