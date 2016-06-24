package com.neural.dsp.smoothing;

import java.util.ArrayList;

public class Filter {

	double[] b;
	double[] a;

	public Filter(double[] B, double[] A) {
		this.b = B;
		this.a = A;

		if (a[0] == 1.) {
			for (int i = 1; i < a.length; ++i)
				a[i] /= a[0];
			for (int i = 0; i < b.length; ++i)
				b[i] /= a[0];
		}
	}

	public double[] filter(ArrayList<Double> sample) {
		double[] yv = new double[sample.size()];
		for (int i = 0; i < sample.size(); ++i) {
			// compute the output
			double buf = b[0] * sample.get(i);
			for (int j = 1; j < b.length; ++j)
				buf += b[j] * sample.get((i - j + sample.size()) % sample.size());

			for (int j = 1; j < a.length; ++j)
				buf -= a[j] * yv[(i - j + yv.length) % yv.length];

			// save the result
			yv[i] = (double) buf;
		}

		return yv;
	}

}
