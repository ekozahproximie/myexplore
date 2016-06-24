package com.neural.dsp.smoothing;

import java.util.ArrayList;

abstract class MovingAverage {

	public static final int DEFAULT_SMOOTH_WIDTH = 3;
	
	protected double smoothWidth = DEFAULT_SMOOTH_WIDTH;
	protected ArrayList<Double> rawValues = null;
	
	public MovingAverage(double smoothWidth) {
		this.smoothWidth = ((smoothWidth % 2) != 0) ? smoothWidth : smoothWidth + 1;
		if (this.smoothWidth == 0) {
			this.smoothWidth = DEFAULT_SMOOTH_WIDTH;
		}
		rawValues = new ArrayList<Double>();
	}
	
	public boolean addRawValue(double rawValue, String dataType) {
		rawValues.add(rawValue);
		return (rawValues.size() < smoothWidth) ? false : true;
	}
	
	public double getWidth() {
		return smoothWidth;
	}
	
	public void setWidth(double smoothWidth) {
		this.smoothWidth = smoothWidth;
	}
	
	public void clear() {
		rawValues.clear();
	}

	abstract public double computeSmoothValue();
}
