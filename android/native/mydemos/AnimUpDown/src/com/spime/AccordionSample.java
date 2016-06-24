package com.spime;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewFlipper;

public class AccordionSample extends Activity  {
	
	private ViewFlipper flipper;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		flipper = (ViewFlipper) findViewById(R.id.flipper);
		LayoutInflater inflater = getLayoutInflater();
		final View firstView = inflater.inflate(R.layout.first_view, flipper,
				false);
		flipper.addView(firstView);
		final View secondView = inflater.inflate(R.layout.second_view, flipper,
				false);
		
		findViewById(R.id.btn_prev).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				flipper.setInAnimation(AccordionAnimation
						.inFromBottomAnimation());
				flipper.setOutAnimation(AccordionAnimation.outToTopAnimation());
				flipper.showPrevious();
				
			}
		});
		findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				flipper.setInAnimation(AccordionAnimation.inFromTopAnimation());
				flipper.setOutAnimation(AccordionAnimation
						.outToBottomAnimation());
				flipper.showNext();
			}
		});
	}

	
}