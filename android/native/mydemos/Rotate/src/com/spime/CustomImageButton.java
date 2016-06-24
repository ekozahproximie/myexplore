package com.spime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class CustomImageButton extends View {
	

	private final static int WIDTH_PADDING = 8;
	private final static int HEIGHT_PADDING = 10;
	private  String label;
	private  int imageResId;
	private  Bitmap image;
	private final InternalListener listenerAdapter = new InternalListener();
	public CustomImageButton(Context context) {
		super(context);
		init(context, R.drawable.firefox_alt, null);
	}
	public CustomImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, R.drawable.firefox_alt, null);
	}

	public CustomImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, R.drawable.firefox_alt, null);
	}
private void init( Context context,int resImage, String label){
	this.label = label;
	this.imageResId = resImage;
	this.image = BitmapFactory.decodeResource(context.getResources(),
			imageResId);

	setFocusable(true);
	//setBackgroundColor(Color.WHITE);

	setOnClickListener(listenerAdapter);
	setClickable(true);
}
	
	
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
//		if (gainFocus == true) {
//			this.setBackgroundColor(Color.rgb(255, 165, 0));
//		} else {
//			this.setBackgroundColor(Color.WHITE);
//		}
	}
int idegree=0;
	protected void onDraw(Canvas canvas) {
//		Paint textPaint = new Paint();
//		textPaint.setColor(Color.BLACK);
		Matrix matrix = new Matrix();
        // resize the bit map
        //matrix.postScale(scaleWidth, scaleHeight);
        // rotate the Bitmap
        matrix.postRotate(90);
       canvas.rotate(90);
		if(image != null){
		canvas.drawBitmap(image,WIDTH_PADDING/2, HEIGHT_PADDING/2, null);
		}
		//canvas.rotate(idegree);
		idegree+=90;
		//idegree%=360;
//		if(label != null){
//		canvas.drawText(label, WIDTH_PADDING / 2,
//				(HEIGHT_PADDING / 2) + image.getHeight() + 8, textPaint);
//		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int preferred = image.getWidth() * 1;
		return getMeasurement(measureSpec, preferred);
	}

	private int measureHeight(int measureSpec) {
		int preferred = image.getHeight() * 1;
		return getMeasurement(measureSpec, preferred);
	}

	private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		int measurement = 0;

		switch (MeasureSpec.getMode(measureSpec)) {
		case MeasureSpec.EXACTLY:
			// This means the width of this view has been given.
			measurement = specSize;
			break;
		case MeasureSpec.AT_MOST:
			// Take the minimum of the preferred size and what
			// we were told to be.
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}

		return measurement;
	}

	public void setOnClickListener(OnClickListener newListener) {
		listenerAdapter.setListener(newListener);
	}

	public String getLabel() {
		return label;
	}

	/**
	 * Returns the resource id of the image.
	 */
	public int getImageResId() {
		return imageResId;
	}

	private class InternalListener implements View.OnClickListener {
		private OnClickListener listener = null;

		/**
		 * Changes the listener to the given listener.
		 * 
		 * @param newListener
		 *            The listener to change to.
		 */
		public void setListener(OnClickListener newListener) {
			listener = newListener;
		}

		@Override
		public void onClick(View v) {
			if (listener != null) {
				listener.onClick(CustomImageButton.this);
			}
		}
	}

}
