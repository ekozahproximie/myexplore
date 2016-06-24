package com.neural.dsp.smoothing;

public class TriangularSmooth extends MovingAverage {

	public TriangularSmooth(double smoothWidth) {
		super(smoothWidth);
	}
	
	public double computeSmoothValue() {
		
		double runUpIndex = smoothWidth / 2;
		double total = 0;
		double triangleMultiplier = 1;
		double divider = 0;
		
		for (int i = 0; i < smoothWidth; i ++) {
			total += (triangleMultiplier * rawValues.get(i));
			divider += triangleMultiplier;
			
			if (i < runUpIndex) {
				triangleMultiplier ++;
			}
			else {
				triangleMultiplier --;
			}
		}
		rawValues.clear();
		return (total/divider); 
	}
}
