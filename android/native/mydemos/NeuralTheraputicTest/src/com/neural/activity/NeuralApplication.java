package com.neural.activity;

import gueei.binding.Binder;
import android.app.Application;

public class NeuralApplication extends Application {
	@Override
    public void onCreate() {
	    super.onCreate();
	    Binder.init(this);
    }
}
