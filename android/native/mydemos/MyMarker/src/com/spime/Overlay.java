package com.spime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class Overlay extends View {

	private int iX=0;
	private int iY=0;
	private int iWidth=0;
	private int iHeight=0;
	private Bitmap icon=null;
	Context context=null;
	public Overlay(Context context,int iPositionX,int iPositionY,int iResID) {
		super(context);
		iX=iPositionX;
		iY=iPositionY;
		this.context=context;
		 icon= BitmapFactory.decodeResource(context.getResources(),
				iResID);
		 iHeight=icon.getHeight();
		 setMinimumHeight(iHeight);
		 iWidth=icon.getWidth();
		 setMinimumWidth(iWidth);
	}
	public int getiWidth(){
		return iWidth;
	}
	public int getiHeight() {
		return iHeight;
	}
	public Overlay(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public Overlay(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFocusable(true); 
	}
	@Override 
	protected void onDraw(Canvas canvas) { 
		canvas.drawBitmap(icon, 0, 0, null);  
		
	}
	public boolean onTouchEvent(MotionEvent event) {       
		switch (event.getAction()) {         
		case MotionEvent.ACTION_DOWN:   
			Toast.makeText(context, "hai", Toast.LENGTH_SHORT).show();
		}
		return true;
	}

}
