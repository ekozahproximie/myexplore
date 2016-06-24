package com.neural.dsp.smoothing;

import java.util.ArrayList;

public class Rectifier {

	public static double rectifiy(double rawValue) {
		return Math.abs(rawValue);
	}
	
	public static void rectify(ArrayList<Double> rawValues) {
		for (int i = 0; i < rawValues.size(); i ++) {
			double value = rawValues.get(i);
			value = Math.abs(value);  // rectify
			rawValues.set(i, value);
		}
	}
}
