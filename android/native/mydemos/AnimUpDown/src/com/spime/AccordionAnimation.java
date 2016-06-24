package com.spime;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class AccordionAnimation {
	public static Animation inFromBottomAnimation() {
		Animation inFromBottom = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromBottom.setDuration(350);
		inFromBottom.setInterpolator(new AccelerateInterpolator());
		return inFromBottom;
	}

	public static Animation outToTopAnimation() {
		Animation outtoTop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f);
		outtoTop.setDuration(350);
		outtoTop.setInterpolator(new AccelerateInterpolator());
		return outtoTop;
	}

	public static Animation inFromTopAnimation() {
		Animation inFromTop = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromTop.setDuration(350);
		inFromTop.setInterpolator(new AccelerateInterpolator());
		return inFromTop;
	}

	public static Animation outToBottomAnimation() {
		Animation outtoBottom = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f);
		outtoBottom.setDuration(350);
		outtoBottom.setInterpolator(new AccelerateInterpolator());
		return outtoBottom;
	}
}
