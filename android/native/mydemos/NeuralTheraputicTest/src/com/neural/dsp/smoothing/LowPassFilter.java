package com.neural.dsp.smoothing;

public class LowPassFilter {
	
	public static double DEFAULT_SMOOTH = 50;
	
	private double lastSmoothValue = 0;
	private double smoothing = DEFAULT_SMOOTH;
	
	public LowPassFilter() {
		this.smoothing = DEFAULT_SMOOTH;
	}
	
	public LowPassFilter(double smoothing) {
		this.smoothing = (smoothing < DEFAULT_SMOOTH) ? DEFAULT_SMOOTH : smoothing;
	}
	
	public double getSmoothValue(double newValue) {
		if (lastSmoothValue == 0) {
			lastSmoothValue = newValue;
		} else {
			lastSmoothValue += (newValue - lastSmoothValue) / smoothing;
		}
		return lastSmoothValue;
	}
	
	public void setSmoothingVariable(double smoothing) {
		this.smoothing = smoothing;
	}
	
	public double getSmoothingVariable() {
		return smoothing;
	}
}
