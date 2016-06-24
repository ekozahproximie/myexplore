package com.neural.dsp.smoothing;

public class RectangularSmooth extends MovingAverage {

	public RectangularSmooth(int smoothWidth) {
		super(smoothWidth);
	}
	
	public double computeSmoothValue() {
		double total = 0;
		for (int i = 0; i < smoothWidth; i ++) {
			total += rawValues.get(i);
		}
		rawValues.clear();
		return (total/smoothWidth); 
	}
	
}
