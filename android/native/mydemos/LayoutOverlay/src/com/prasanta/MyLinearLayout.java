/*
 * Copyright (C) 2010 Prasanta Paul, http://as-m-going-on.blogspot.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prasanta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 1.
 * Make sure 
 * android:layout_height="fill_parent"
 * 
 * 2.
 * Make sure, you pass a set of TabIcon instances
 * 
 * @author Prasanta Paul
 *
 */
public class MyLinearLayout extends LinearLayout {

	String TAG = "MyLinearLayout";
	Region minRegion;
	boolean isMinimized = false;
	Context context;
	LayoutListener listener;
	TabIcon[] tabs = null;
	
	/**
	 * Layout Event Listener
	 *
	 */
	public interface LayoutListener{
		public void clickHandler(int selction);
	}

	public MyLinearLayout(Context context) {
		super(context);
		this.context = context;
	}

	public MyLinearLayout(Context context, AttributeSet attrs){
		super(context, attrs);
		this.context = context;
	}
	
	public void registerListener(LayoutListener listener){
		this.listener = listener;
	}
	/**
	 * R.drawable. IDs
	 * @param icons
	 */
	public void setTabIcons(TabIcon[] tabs){
		this.tabs = tabs;
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		
		// if tab array is empty don't do anything
		if(tabs == null || tabs.length == 0)
			return;
		
		int x = 0;
		int y = canvas.getHeight() - 90;
		
		// draw minimizer
		Rect border = new Rect(x, y, x + 20, y + 50);
		drawMiniMizer(canvas, border);
		
		if(isMinimized)
			return;
		
		// shift x co-ordinate ahead of Minimizer
		x = x + 20;
		// draw tabs
		for(int i=0; i<tabs.length; i++){
			TabIcon ti = tabs[i];
			if(ti == null || ti.getIcon() == -1)
				continue;
			
			Rect tabBorder = new Rect(x, y, x + 30, y + 50);
			drawTab(canvas, tabBorder, ti.getIcon(), ti.isClicked());
			if(ti.getRegion() == null){
				ti.setRegion(new Region(tabBorder));
			}
			// each tab size is 30
			if(i < tabs.length -1)
				x = x + 30;
		}
	}

	private void drawMiniMizer(Canvas canvas, Rect border){
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.FILL);
		
		Path path = new Path();
		path.moveTo(border.left, border.top);
		
		path.lineTo(border.left, border.bottom);
		path.lineTo(border.right, border.bottom);
		path.lineTo(border.left, border.top);
		
		canvas.drawPath(path, paint);
		
		// set region
		if(minRegion == null){
			Region clip = new Region(border);
			minRegion = new Region();
			minRegion.setPath(path, clip);
		}
	}
	
	private void drawTab(Canvas canvas, Rect border, int icon, boolean isFocused){
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(0xFFFFFFFF);
		if(isFocused){
			paint.setColor(0xFF858585);
		}
		canvas.drawRect(border, paint);

		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), icon);
		canvas.drawBitmap(bmp, border.left + 5, border.top + 5, null);
		
		// draw border
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		canvas.drawRect(border, paint);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		
		Log.i(TAG, "dispatchTouchEvent");
		int x = (int)e.getRawX();
		int y = (int)e.getRawY() - 50; // adjust height
		
		if(e.getAction() != MotionEvent.ACTION_UP){
			if(minRegion != null && minRegion.contains(x, y)){
				return true;
			}
			else{
				boolean clickedOnTab = false;
				// check for tabs
				for(int i=0; i<tabs.length; i++){
					TabIcon ti = tabs[i];
					if(ti != null && ti.getRegion() != null && ti.getRegion().contains(x, y)){
						Log.i(TAG, "Tab Clicked: "+ ti.getIcon());
						clickedOnTab = true;
						break;
					}
				}
				
				if(clickedOnTab)
					return true;
			}
			return super.dispatchTouchEvent(e);
		}
		
		// user lifts his finger
		if(e.getAction() == MotionEvent.ACTION_UP){
			Log.i(TAG, "Clicked on: "+ x +", "+ y);
			
			if(minRegion != null)
				Log.i(TAG, "minRegion: "+ minRegion.getBounds());

			// display Tab Regions
			Log.i(TAG, "display tabs region");
			for(int i=0; i<tabs.length; i++){
				TabIcon ti = tabs[i];
				if(ti != null && ti.getRegion() != null)
					Log.i(TAG, "tab"+ i +" :"+ ti.getRegion().getBounds());
			}
			
			if(minRegion != null && minRegion.contains(x, y)){
				Log.i(TAG, "Clicked on minRegion");
				isMinimized = !isMinimized;
				invalidate();
				return true;
			}
			else {
				int selectedTab = -1;
				for(int i=0; i<tabs.length; i++){
					TabIcon ti = tabs[i];
					if(ti != null && ti.getRegion() != null && ti.getRegion().contains(x, y)){
						// toggle selected Tab
						Log.i(TAG, "Tab Clicked: "+ ti.getIcon());
						selectedTab = ti.getIcon();
						ti.setClicked(!ti.isClicked());
					}
					else{
						// set all other false
						ti.setClicked(false);
					}
				}
				invalidate();
				if(selectedTab != -1 && listener != null){
					// give call back to Listener
					listener.clickHandler(selectedTab);
				}
			}
		}
		
		return super.dispatchTouchEvent(e);
	}
}
