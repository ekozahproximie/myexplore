package com.neural.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.neural.constant.GraphLineManager;
import com.neural.demo.OnDemoDataUpdateListener;
import com.neural.demo.R;
import com.neural.fragment.RehabFragment;
import com.neural.sensor.NtDevice;
import com.neural.sensor.NtDevice.DeviceDataHandlerParameter;
import com.neural.sensor.NtDevice.DeviceEventHandlerParameter;
import com.neural.sensor.NtDevice.DeviceHandlerInterface;
import com.neural.sensor.NtDevice.DeviceHandlerParameter;
import com.neural.sensor.NtDeviceManagement;
import com.neural.sensor.NtDeviceManagement.ManagedDevice;
import com.neural.sensor.NtDeviceSettings;
import com.neural.sensor.NtSensorData.NTSensorCoordinate;
import com.neural.setting.SettingsManager;
import com.shimmerresearch.driver.Shimmer;

import java.util.ArrayList;

public class GraphView extends View implements DeviceHandlerInterface,
                     SensorEventListener,OnDemoDataUpdateListener {

	
	private static final float GRAPH_MARGIN = 5;
	
	private Context context = null;

	private Bitmap bitmap;
	private Paint paint = new Paint();
	private Canvas canvas = new Canvas();
	
	private float lastKnownX;
	private float yScale = 0;
	private float maxX;
	
	private NtDeviceManagement managedDevices = null;
	private MuscleGraph graphs[] = new MuscleGraph[NtDeviceManagement.MAX_MUSCLE_GROUP];
	
	private transient Paint mPreDrawnPaint=null;
	private transient Paint mConstantPaint=null;
	private transient Paint mFreezePaint=null;
	private transient Path    mPreDrawnPath=null;
	private transient Path    mPreDrawnBoundaryPath=null;
	private transient Path    mFreezePath=null;
	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 4;
	private transient boolean isPreDrawn=false;
	private transient boolean isPreDrawnComplete=false;
	private transient boolean iConstantTragetLineDrawn=false;
	private transient MaskFilter  mEmboss;
	private transient MyGestureDetectorCompat mGestureDetector;
	private transient MyXScaleGestureDetector mXScaleDetector;
	private transient MyYScaleGestureDetector mYScaleDetector;
	
	
	private transient Paint mGridColor= null;
	
	private transient int iMaxLegendTxtHeight=-1; 
	
	private transient final int LEGEND_TXT_Y=60;
	
	private transient final int LEGEND_TXT_PADDING=20;
	
	private transient final int LEGEND_LINE_PADDING=5;
	
	private transient final int LEGEND_TXT_START_X=60;
	
	private transient int i_legend_txt_x=LEGEND_TXT_START_X;
	
	private transient SettingsManager settingsManager =null;
	
	private transient  int SCALE_TXT_Y=60;
	
	private transient final int SCALE_TXT_X=60;
        
        private transient final int SCALE_TXT_PADDING=20;
	
	private transient final int graphLegendLabels[] = {
              R.string.left_bicep, R.string.right_bicep, R.string.left_tricep, R.string.right_tricep,
              R.string.demo_mode
      };
	
	private transient int iGraphViewHeight=0;
	
	private transient int iGraphViewWidth=0;
	
	private static final String LOG=GraphView.class.getSimpleName();
	
	
	
	 private transient final Rect txtRect= new Rect();
	 
	 private transient ScaleLayout scaleLayout =null;
	 
	 private transient  float fConstantValue = -1;
	 
	 private transient  float fBoundaryValue = -1;
	 
	 private transient  boolean isBoundaryEnabled = false;
	 
	 private transient  boolean isDrawGrid = false;
	 
	 private transient  boolean isFreezeMode = false;
	 
	 
	 private PathRecorder pathRecorder = null;
	 
	 private transient boolean isSizeChanged=false; 
	 
	 private transient DashPathEffect dashPathEffect =null;
	 
	 private transient boolean isClear=false;
	 
	 private transient int iEmgOffsetScale=1;
	 
	 private transient double dSpeed=0;
	 
	 private int iNeedUpdate=0;
	 
	 private final static int SPEED_UPDATED=1;
	 
	 private final static int EMG_OFFSET_UPDATED=2;
	 

	private class MuscleGraph {
	
		private int graphColor;
		private float baseLine;
		private float graphHeight;
		private  int iGroupIndex;

		public int getGraphColor() {
			return graphColor;
		}
		
		public void setGraphColor(int graphColor) {
			this.graphColor = graphColor;
		}
		
		public float getBaseLine() {
			return baseLine;
		}
		
		public void setBaseLine(float baseLine) {
			this.baseLine = baseLine;
		}
		
		public float getGraphHeight() {
			return graphHeight;
		}

		public void setGraphHeight(float graphHeight) {
			this.graphHeight = graphHeight;
		}
		
               /**
                * @param iGroupIndex the iGroupIndex to set
                */
               public void setGroupIndex(int iGroupIndex) {
                  this.iGroupIndex = iGroupIndex;
               }
               
               /**
                * @return the iGroupIndex
                */
               public int getGroupIndex() {
                  return iGroupIndex;
               }
	};
	
	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		initUI();
	}

	public GraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
		initUI();
	}

	public GraphView(Context context) {
		super(context);
		this.context = context;
		initUI();
	}

	private void initUI() {
		
		managedDevices = NtDeviceManagement.getDefaultDeviceManager(context.getApplicationContext());
		settingsManager=SettingsManager.getInstance();
		pathRecorder  =PathRecorder.getInstance();
		initDisplayVariable();
		/* mSensorManager = (SensorManager)context. getSystemService(Activity.SENSOR_SERVICE);
		 testPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		 testPaint.setStyle(Style.FILL);
		 testPaint.setColor(Color.RED);*/
		 
		GraphLineManager glm=GraphLineManager.getInstance();
		final int graphAxisColors[]=glm.getGraphAxisColors();
		for (int i = 0; i < NtDeviceManagement.MAX_MUSCLE_GROUP; i++) {
			graphs[i] = new MuscleGraph();
			graphs[i].iGroupIndex=i;
			graphs[i].setGraphColor(graphAxisColors[i]);
		}
		
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(2.5f);	
		enablePreDrawnMode();
		initGridView();
	}
	private void initGridView(){
	   if(isDrawGrid){
	   if(mGridColor == null){
	            mGridColor= new Paint();
	            mGridColor.setFlags(Paint.ANTI_ALIAS_FLAG);
	           mGridColor.setAntiAlias(true);
	           mGridColor.setDither(true);
	           
	           mGridColor.setStyle(Paint.Style.STROKE);
	           mGridColor.setStrokeJoin(Paint.Join.ROUND);
	           mGridColor.setStrokeCap(Paint.Cap.ROUND);
	           mGridColor.setStrokeWidth(1);
	   }
	  
	   mGridColor.setColor(settingsManager.getGridColor(context));
	   }
	}
	private void initDisplayVariable(){
	    isPreDrawn = settingsManager.isPreDrawnEnabled(context);
            isDrawGrid = settingsManager.getGridDisplay(context);
            iConstantTragetLineDrawn=settingsManager.isConstantModeEnabled(context);
            isBoundaryEnabled =settingsManager.isBoundaryModeEnable(context);
            isFreezeMode = settingsManager.isFreezeEnabled(context);
            
            fConstantValue= settingsManager.getTragetRangeValue(context);
            fBoundaryValue= settingsManager.getBoundaryModeRange(context);
	}
	private void enablePreDrawnMode(){
	   
	   
           if(isFreezeMode !=  settingsManager.isFreezeEnabled(context)){
              pathRecorder.clear();
              pathRecorder.stopRecord(false);
           }
           
           
	   if( (isPreDrawn  != settingsManager.isPreDrawnEnabled(context) )
	         || ( isFreezeMode != settingsManager.isFreezeEnabled(context) )
	         ||  (isDrawGrid  != settingsManager.getGridDisplay(context) )   
	         || ( iConstantTragetLineDrawn != settingsManager.isConstantModeEnabled(context) )
	         || ( isBoundaryEnabled != settingsManager.isBoundaryModeEnable(context) )
	         || (fConstantValue != settingsManager.getTragetRangeValue(context) )
	         || (fBoundaryValue != settingsManager.getBoundaryModeRange(context) )
	         ){
	      clearGraph(); 
	   }
	   
	   
	   initDisplayVariable();
            
            if (isPreDrawn) {
               if (mPreDrawnPaint == null) {
                  mPreDrawnPaint = new Paint();
               }
                  mPreDrawnPaint.setAntiAlias(true);
                  mPreDrawnPaint.setDither(true);
                  mPreDrawnPaint.setColor(0xFFFF0000);
                  mPreDrawnPaint.setStyle(Paint.Style.STROKE);
                  mPreDrawnPaint.setStrokeJoin(Paint.Join.ROUND);
                  mPreDrawnPaint.setStrokeCap(Paint.Cap.ROUND);
                  mPreDrawnPaint.setStrokeWidth(8);
                  mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6,
                        3.5f);
                  mPreDrawnPaint.setMaskFilter(mEmboss);
               
               if (mPreDrawnPath == null) {
                  mPreDrawnPath = new Path();
               }
               if(mPreDrawnBoundaryPath == null){
                  mPreDrawnBoundaryPath = new Path();
               }
               dashPathEffect=new DashPathEffect(new float[] {5,5}, 0);
            }else 
            if(iConstantTragetLineDrawn){
               if(mConstantPaint == null){
                  mConstantPaint = new Paint();
               }
            mConstantPaint.setAntiAlias(true);
            mConstantPaint.setDither(true);
            mConstantPaint.setColor(0xFF7293c6);
            mConstantPaint.setStyle(Paint.Style.STROKE);
          
            mConstantPaint.setStrokeWidth(8);
            mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 }, 0.4f, 6,
                  3.5f);
          //  mConstantPaint.setMaskFilter(mEmboss);
            dashPathEffect=new DashPathEffect(new float[] {15,5}, 0);
            }else if (isFreezeMode){
               
               if (mFreezePath == null) {
                  mFreezePath = new Path();
               }
               
               if(mFreezePaint == null){
                  mFreezePaint = new Paint();
               }
               mFreezePaint.setAntiAlias(true);
               mFreezePaint.setDither(true);
               mFreezePaint.setStyle(Style.STROKE);
               mFreezePaint.setColor(0x7F4755A1);
               mFreezePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
               mFreezePaint.setStrokeWidth(2.5f);   
               dashPathEffect=new DashPathEffect(new float[] {5,5}, 0);
            }
            
            if(iGraphViewHeight != 0){
               //isResumed=
            }
          
	}
	
	public void setUpGraphDisaply(){
	   setGraphBaseLine();
	   enablePreDrawnMode();
	   initGridView();
           invalidate();
           
	}
	private void clearGraph(int w, int h,Bitmap.Config config){
	   if(bitmap != null){
	      synchronized (bitmap) {
	         bitmap.recycle();
            }
	   }
	   bitmap = Bitmap.createBitmap(w, h,config);
           canvas.setBitmap(bitmap);
           canvas.drawColor(0x20FFFFFF);
           
	}
    @Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		
                clearGraph(w, h, Bitmap.Config.ARGB_8888);
                iGraphViewHeight=h;
                iGraphViewWidth=w;
                final NtDeviceManagement deviceManagement = NtDeviceManagement.getDefaultDeviceManager(context.getApplicationContext());
                deviceManagement.setGraphWidth(w);
                setGraphBaseLine();
		
		maxX = (w < h) ? w : w;
		Log.i("test", "w:"+w);
		lastKnownX = maxX;
		//Log.i("test", "onSizeChanged ");
		  /*  mYOffset = h * 0.5f;
	            mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
	            mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
		   */
		super.onSizeChanged(w, h, oldw, oldh);
		isSizeChanged=true;
		//Log.i("test", "onSizeChanged ");
		
	}
    private void setGraphBaseLine(){
       if(iGraphViewHeight == 0){
          Log.e(LOG, "Graph view height is zero");
          return;
       }
    // compute center line for each graph
       float drawingHeight = iGraphViewHeight ;
       float graphHeight = drawingHeight / NtDeviceManagement.MAX_MUSCLE_GROUP;
       float workingHeight = graphHeight - (GRAPH_MARGIN * 2);
       float baseLine = graphHeight;
        
       
       
       // save scale
       yScale = 200; //workingHeight - GRAPH_MARGIN;

       // set the center line
       for (int i = 0; i < NtDeviceManagement.MAX_MUSCLE_GROUP; i++) {
               graphs[i].setGraphHeight(workingHeight);
               //To draw line and sensor data
               //graphs[i].setBaseLine(baseLine + CONTROL_ICON_HEIGHT - GRAPH_MARGIN);
               int iCenterOfScreen=getCenterEMGValues(iGraphViewHeight,
                     RehabFragment.getSensorPosition(i, context));
               Log.i("test", "iCenterOfScreen:"+iCenterOfScreen);
               graphs[i].setBaseLine(iCenterOfScreen);
               baseLine += graphHeight;
       }
    }
          private int getCenterEMGValues(int iScreenHeight,final String stKey){
             final int emgOffset=settingsManager.getEMGOffset(context,stKey);
             
             final boolean isEmgOffsetSet= ! (emgOffset == SettingsManager.EMG_OFFSET_CENTER );
             int iCenterOfScreen=iScreenHeight/2;
             
             if(isEmgOffsetSet){
                //below center
                final int iOffsetChange=Math.abs(SettingsManager.EMG_OFFSET_CENTER 
                        - emgOffset);
                final int iAboveCenterHeight=iScreenHeight-iCenterOfScreen;
                final int iUnitDivision=iAboveCenterHeight/SettingsManager.EMG_OFFSET_CENTER;
                final int iPixelChange=iUnitDivision * iOffsetChange;
                
                     if(emgOffset > SettingsManager.EMG_OFFSET_CENTER){
                        //above center
                        iCenterOfScreen -=iPixelChange;
                          
                     }else{
                       iCenterOfScreen +=iPixelChange;
                     }
             }
             
             return iCenterOfScreen;
          }
	private void paintGraphMargins(MuscleGraph graph) {
		
		paint.setColor(Color.LTGRAY);
		paint.setStyle(Style.STROKE);
		paint.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));	
		
		// boundary height
		float boundaryHeight = graph.getGraphHeight() / 4; // default is 1/4 working space
		
		// draw upper bounds margin
		canvas.drawLine(0, graph.getBaseLine() - (boundaryHeight * 3), maxX, 
				graph.getBaseLine() - (boundaryHeight * 3), paint);
		
		// draw lower bounds margin
		canvas.drawLine(0, graph.getBaseLine() - boundaryHeight, maxX, 
				graph.getBaseLine() - boundaryHeight, paint);
		
		// draw center line
		paint.setColor(Color.DKGRAY);
		paint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));	
		canvas.drawLine(0, graph.getBaseLine() - (boundaryHeight * 2), maxX, 
				graph.getBaseLine() - (boundaryHeight * 2), paint);
	}
	
	private void paintGraphBaseLine(MuscleGraph graph, int muscleGroup) {
	   final int graphLabels[] = {
                 R.string.left_bicep, R.string.right_bicep, R.string.left_tricep, R.string.right_tricep,
                 R.string.demo_mode
         };
		
		final int [] graphLineColor =GraphLineManager.getInstance().getGraphLineColor();
		float iOldLineWidth=paint.getStrokeWidth();
		paint.setColor(graphLineColor[muscleGroup]);
		paint.setStyle(Style.FILL);
		paint.setStrokeWidth(5);
		paint.setPathEffect(null);	

		// draw center line
		canvas.drawLine(0, graph.getBaseLine(), maxX, graph.getBaseLine(), paint);
		
		// draw the label
		//canvas.drawText(getResources().getString(graphLabels[muscleGroup]), 10, graph.getBaseLine() - 5, paint);
		paint.setStrokeWidth(iOldLineWidth);
	}
	
	private void drawSensorLegend( int muscleGroup){
	   
	   
	   float fOldTextSize=paint.getTextSize();
	   
	   final int [] graphLineColor =GraphLineManager.getInstance().getGraphLineColor();
	   paint.setColor(graphLineColor[muscleGroup]);
	   paint.setTextSize(18);
           paint.setFakeBoldText(true);
           
           
            final String stTextLabel=context.getResources().getString(graphLegendLabels[muscleGroup]);
            paint.getTextBounds(stTextLabel, 0, stTextLabel.length(), txtRect);
            
	   canvas.drawLine(i_legend_txt_x, 
	                    LEGEND_TXT_Y-      iMaxLegendTxtHeight-LEGEND_LINE_PADDING,
	                   i_legend_txt_x +    txtRect.right,
	                    LEGEND_TXT_Y -     iMaxLegendTxtHeight-LEGEND_LINE_PADDING, paint);
	   
	   paint.setColor(Color.BLACK);
	   
	   canvas.drawText(stTextLabel, i_legend_txt_x, LEGEND_TXT_Y, paint);
	   paint.setTextSize(fOldTextSize);
           paint.setFakeBoldText(false);
	   i_legend_txt_x += txtRect.right + LEGEND_TXT_PADDING;
	}
	private void drawScaleValueLegend( ){
	   if(scaleLayout != null){
	      scaleLayout.updateValues("Left Bicep");
	   }
        }
	public void setScaleLayout(final ScaleLayout scaleLayout){
	   this.scaleLayout=scaleLayout;
	}
	private void drawConstantLine(boolean isForceDrawn){
           if(! iConstantTragetLineDrawn){
              return;
           }
          final float fMaxHeight=getHeight()  ;
          final float fCurrentConstantValue= settingsManager.getTragetRangeValue(context) ;
          final float iConstantUnit= fMaxHeight / (SettingsManager.MAX_CONSTANT_VALUE+SettingsManager.MIN_CONSTANT_VALUE);
          
          if(isForceDrawn && fCurrentConstantValue == fConstantValue ){
             
             return;
          }
          
          if(fCurrentConstantValue != fConstantValue){
             final float CurrentRange= fMaxHeight - (fConstantValue*iConstantUnit);
             mConstantPaint.setXfermode(new PorterDuffXfermode(
                   PorterDuff.Mode.CLEAR));
             canvas.drawLine(0, CurrentRange, getWidth(), CurrentRange, mConstantPaint);
             fConstantValue= fCurrentConstantValue;
             mConstantPaint.setXfermode(null);
          }
             final float CurrentRange= fMaxHeight - (fConstantValue*iConstantUnit);
             canvas.drawLine(0, CurrentRange, getWidth(), CurrentRange, mConstantPaint);
          
         
        }
	private void drawPreDrawnLine(){
	   if(isPreDrawn){
	      mPreDrawnPaint.setStrokeWidth(8);
	      mPreDrawnPaint.setMaskFilter(mEmboss);
	      mPreDrawnPaint.setPathEffect(null);
              canvas.drawPath(mPreDrawnPath, mPreDrawnPaint);
            }
	}
	
	private void drawFreezeLine(){
	   if(isFreezeMode){
	      mFreezePath.reset();
	      mFreezePaint.setPathEffect(null);
	      pathRecorder.getPathPoints(mFreezePath);
              canvas.drawPath(mFreezePath, mFreezePaint);
           }
	}
	private void drawConstantBoundaryLine(){
           if(  ! iConstantTragetLineDrawn){
              return;
           }
          final float fMaxHeight=getHeight()  ;
          final float iConstantUnit= fMaxHeight / (SettingsManager.MAX_CONSTANT_VALUE+SettingsManager.MIN_CONSTANT_VALUE);
          final float CurrentRange= fMaxHeight - (fConstantValue*iConstantUnit);
          mConstantPaint.setStrokeWidth(8);
         // mConstantPaint.setMaskFilter(mEmboss);
          mConstantPaint.setPathEffect(null);
          canvas.drawLine(0, CurrentRange, getWidth(), CurrentRange, mConstantPaint);
          
          
          if(isBoundaryEnabled){
             fBoundaryValue= settingsManager.getBoundaryModeRange(context);
             mConstantPaint.setMaskFilter(null);
             mConstantPaint.setPathEffect(dashPathEffect);
             mConstantPaint.setStrokeWidth(6);
             canvas.drawLine(0, CurrentRange + fBoundaryValue, getWidth(), CurrentRange +fBoundaryValue, mConstantPaint);
             canvas.drawLine(0, CurrentRange - fBoundaryValue, getWidth(), CurrentRange - fBoundaryValue, mConstantPaint);
             
          }
         
        }
	
	
	
	private void drawFreezeBoundaryLine(){
           if(  ! isFreezeMode){
              return;
           }
          
          if(isBoundaryEnabled){
             fBoundaryValue= settingsManager.getBoundaryModeRange(context);
             mFreezePaint.setPathEffect(dashPathEffect);
             mFreezePaint.setStrokeWidth(2.5f);
             
             pathRecorder.getBoundaryPathPoints(mFreezePath,fBoundaryValue);
             canvas.drawPath(mFreezePath, mFreezePaint);
             pathRecorder.getBoundaryPathPoints(mFreezePath,-fBoundaryValue);
             canvas.drawPath(mFreezePath, mFreezePaint);     
          }
         
        }
	
	private void drawPreDrawnBoundaryLine(){
           if(  ! isPreDrawn){
              return;
           }
          
          if(isBoundaryEnabled){
             fBoundaryValue= settingsManager.getBoundaryModeRange(context);
             mPreDrawnPaint.setPathEffect(dashPathEffect);
             mPreDrawnPaint.setStrokeWidth(2.5f);
             mPreDrawnPaint.setMaskFilter(null);
             pathRecorder.getPreDrawnBoundaryPathPoints(mPreDrawnBoundaryPath,fBoundaryValue);
             canvas.drawPath(mPreDrawnBoundaryPath, mPreDrawnPaint);
             pathRecorder.getPreDrawnBoundaryPathPoints(mPreDrawnBoundaryPath,-fBoundaryValue);
             canvas.drawPath(mPreDrawnBoundaryPath, mPreDrawnPaint);     
          }
         
        }
	
	
    private void paintXYGraph(MuscleGraph currentGraph, NTSensorCoordinate lastCoordinate, NTSensorCoordinate newCoordinate) {
		
		final float lastHistoricalY = (lastCoordinate == null) ? 0 : (float) lastCoordinate.getY();
		final float lastHistoricalX = (lastCoordinate == null) ? 0 : (float) lastCoordinate.getX();
		
		float lastY = currentGraph.getBaseLine() 
		      - ( lastHistoricalY * getEMGScale(currentGraph.getGroupIndex()));
		
		final float newY = currentGraph.getBaseLine() - 
	 	                   ((float) newCoordinate.getY() * getEMGScale(currentGraph.getGroupIndex() ))
		      ;
	
		paint.setColor(currentGraph.getGraphColor());
		canvas.drawLine(lastHistoricalX, lastY, (float)newCoordinate.getX(), newY, paint);
		
		addToPathRecord((float)newCoordinate.getX(),newY);

		
		if (newCoordinate.getX() > lastKnownX) {
			lastKnownX = (float) newCoordinate.getX();
		}
		if( lastKnownX >= maxX){
		   if(! pathRecorder.isCloseRecord()){
		   // Log.i("test", "stop Record from paintXYGraph");
		   stopPathRecord(true);
		   }
		}
    }
	
    private void clearAllHistoricalCoordinates(){
    
       managedDevices.clearAllManagedDeviceHistoryData();
    }
    private void paintHistoricalCoordinates() {
    	
		synchronized (this) {
		   
			for (int muscleGroup = 0; muscleGroup < NtDeviceManagement.MAX_MUSCLE_GROUP; muscleGroup ++) {
				
				ManagedDevice mngDevice = managedDevices.getManagedDeviceByMuscleGroup(muscleGroup);
				if (mngDevice != null) {
					
					MuscleGraph currentGraph = graphs[mngDevice.getMuscleGroup()];
					
					// reset x coordinates
					mngDevice.getHistoricalData().resetXValues();
					NTSensorCoordinate lastCoordinate = null;
					
					// draw coordinates
					ArrayList<NTSensorCoordinate> sensorData = mngDevice.getHistoricalData().getCoordinates();
					for (int i = 0; i < sensorData.size(); i ++) {
						NTSensorCoordinate newCoordinate = sensorData.get(i);
						paintXYGraph(currentGraph, lastCoordinate, newCoordinate);
						lastCoordinate = newCoordinate;
					}
					
					if (sensorData.size() > 0) {
						// draw continuation
						paint.setColor(Color.RED);
						paint.setStyle(Style.STROKE);
						paint.setPathEffect(new DashPathEffect(new float[] {10,10}, 0));	

						// draw red-line 
						canvas.drawLine((float)lastCoordinate.getX(), currentGraph.getBaseLine() - currentGraph.getGraphHeight()/2, 
								(float) lastCoordinate.getX(), currentGraph.getBaseLine() + currentGraph.getGraphHeight()/2, paint);
						
						// add resume text
						paint.setStyle(Style.FILL);
						paint.setPathEffect(null);	
						canvas.drawText(getResources().getString(R.string.resume_graph), (float) lastCoordinate.getX() + 5, 
								currentGraph.getBaseLine() - currentGraph.getGraphHeight()/2 + 15, paint);
						
						// reset painter
						paint.setColor(Color.BLACK);
					}
				}
			}
		}
    }
    long lOldTime = 0;
    long lCount = 0;
    private void addNewCoordinates(ManagedDevice mngDevice, DeviceDataHandlerParameter data) {
    	      
		synchronized (this) {
		         if(lOldTime == 0){
		            lOldTime = System.currentTimeMillis();
		         }
			NTSensorCoordinate lastCoordinate = mngDevice.getHistoricalData().getLastCoordinate();
			NTSensorCoordinate newCoordinate = mngDevice.getHistoricalData().
			            computeDataPoint(data.getData(),getXMove());
			if (mngDevice.getHistoricalData().addDataPoint(newCoordinate)) {

				// let is show on the screen
				if (bitmap != null) {
					MuscleGraph currentGraph = graphs[mngDevice.getMuscleGroup()];
					paintXYGraph(currentGraph, lastCoordinate, newCoordinate);					
				}
				 
				if (lastKnownX >= maxX) {
   				        clearAllHistoricalCoordinates();
   				        lCount =0;
                                        lOldTime = System.currentTimeMillis();
					//mngDevice.getHistoricalData().clear();
				}
				lCount ++;
//				if(lOldTime + 1000 < System.currentTimeMillis() ){
//				  
//				   lCount =0;
//				   lOldTime = System.currentTimeMillis();
//				}
				 
				invalidate();
			}
		}
    }
    private double getCurrentSpeed(){
     
     
//     final  float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, 
//             getResources().getDisplayMetrics());
//     final double iGraphWidthCM=(iGraphViewWidth  / px)/10;
       
     final double dCurrentSpeed= (getSettingSpeed(context) * 1024) / iGraphViewWidth ;
    // http://www.calculator.org/calculate-online/health-fitness/running.aspx
   // Log.i("test", "dCurrentSpeed:"+dCurrentSpeed);
       return dCurrentSpeed;
    }
    private double getXMove(){
       return 1;
    }
    private void addNewCoordinates( float  data) {
       
       synchronized (this) {
                final ManagedDevice mngDevice =managedDevices.getManagedDeviceByMuscleGroup(NtDeviceManagement.MUSCLE_GROUP_DEMO);
                if(mngDevice == null){
                   return;
                }

               NTSensorCoordinate lastCoordinate = mngDevice.getHistoricalData().getLastCoordinate();
               NTSensorCoordinate newCoordinate = mngDevice.getHistoricalData().computeDataPoint(data,
                     getXMove());
              
               if (mngDevice.getHistoricalData().addDataPoint(newCoordinate)) {
                        
                       // let is show on the screen
                       if (bitmap != null) {
                               MuscleGraph currentGraph = graphs[mngDevice.getMuscleGroup()];
                               paintXYGraph(currentGraph, lastCoordinate, newCoordinate);                                      
                       }
                      
                      
                       if (lastKnownX >= maxX) {
                               mngDevice.getHistoricalData().clear();
                       }

                       postInvalidate();
               }
       }
}
    private void stopPathRecord(final boolean isCloseRecord){
       if(isFreezeMode){
          pathRecorder.stopRecord(isCloseRecord);
       }
    }
    private void addToPathRecord(final float x,final float y){
       if(isFreezeMode){
          pathRecorder.add(x, y,lastKnownX,maxX);
       }
    }
	@Override
	protected void onDraw(Canvas drawCanvas) {
		
		synchronized (this) { 
		   //drawGridView(drawCanvas, getWidth(), getHeight());
		   
			if (bitmap != null) {
				
				if (lastKnownX >= maxX || isSizeChanged ) {
				   isSizeChanged=false;
				   resetGraphData();
				   //isResumed=true;
				}
				
				   synchronized (bitmap) {
				      if(! bitmap.isRecycled()){
				      drawCanvas.drawBitmap(bitmap, 0, 0, null);
                                    }
				}
				
			}
		}
		 drawPreDrawnLine();
		//drawConstantLine(true);
		
		   
		
	}
	private void resetGraphData(){
	   clearGraph(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
           lastKnownX = 0;
           Log.i("test1","resetGraphData");
           drawGridView(canvas , getWidth(), getHeight());
           canvas.drawColor(0x20FFFFFF);
//           i_legend_txt_x=LEGEND_TXT_START_X;
//           if(iMaxLegendTxtHeight == -1){
//              iMaxLegendTxtHeight = getMaxTextHeight();
//           }
//           for (int i = 0; i < NtDeviceManagement.MAX_MUSCLE_GROUP; i++) {
//                   //paintGraphMargins(graphs[i]);
//                   //paintGraphBaseLine(graphs[i], i);
//                  // drawSensorLegend(i);
//           }
           
           
             drawPreDrawnLine();
             drawFreezeLine();
            //drawConstantLine(false);
           // paintHistoricalCoordinates();
            clearAllHistoricalCoordinates();
            drawScaleValueLegend();
            drawConstantBoundaryLine();
            drawFreezeBoundaryLine();
            drawPreDrawnBoundaryLine();
	}
	private int getMaxTextHeight(){
	   paint.setTextSize(18);
           paint.setFakeBoldText(true);
          
           final Rect txtRect= new Rect();
           for (int i = 0; i < NtDeviceManagement.MAX_MUSCLE_GROUP; i++) {
            final String stTextLabel=context.getResources().getString(graphLegendLabels[i]);
            paint.getTextBounds(stTextLabel, 0, stTextLabel.length(), txtRect);
            if (iMaxLegendTxtHeight < txtRect.height() ){
               iMaxLegendTxtHeight= txtRect.height();
            }
           }
           return iMaxLegendTxtHeight;
	}
	int iCount =0;
	@Override
	public void handleDeviceEvent(DeviceHandlerParameter param) {

		ManagedDevice mngDevice = (ManagedDevice) param.getPayload();
		DeviceEventHandlerParameter eventParam = null;
		
		switch (param.getEventType()) {
		
		case DeviceHandlerParameter.STATE_CHANGE:
			eventParam = (NtDevice.DeviceEventHandlerParameter) param;
			switch (eventParam.getNewState()) {
	        case Shimmer.STATE_CONNECTED:
	            break;

	        case Shimmer.STATE_CONNECTING:
	            Toast.makeText(getContext(), 
	            		getResources().getString(R.string.connecting) + " " +
	            		mngDevice.getDevice().getDeviceName(), Toast.LENGTH_SHORT).show();
	            break;
	        
	        case Shimmer.STATE_NONE:
	            Toast.makeText(getContext(), getResources().getString(R.string.device_disconnected) + " " +
	            		mngDevice.getDevice().getDeviceName(), Toast.LENGTH_SHORT).show();
	            break;
			}
			break;
			
		case DeviceHandlerParameter.DEVICE_NAME_UPDATE:
			eventParam = (NtDevice.DeviceEventHandlerParameter) param;
			if (managedDevices.getNtDeviceByName(mngDevice.getDevice().getDeviceName()) == null) {
	            Toast.makeText(getContext(), getResources().getString(R.string.no_matching_device) + " " +
								mngDevice.getDevice().getDeviceName(), Toast.LENGTH_SHORT).show();
			}
			break;
			
		case DeviceHandlerParameter.NOTIFICATION:{
                   eventParam = (NtDevice.DeviceEventHandlerParameter) param;
                   final String stNotification= eventParam.getNotification();
                   Toast.makeText(context,
                 stNotification,
                           Toast.LENGTH_SHORT).show();
                  if (stNotification.endsWith("is ready for Streaming")) {
                     mngDevice.getDevice().writeSamplingRate(
                           NtDeviceSettings.DEFAULT_SAMPLING_RATE);
                  }
           
           }
                   break;
			
		case DeviceHandlerParameter.CALIBRATED_DATA:
		        //Log.i("test","getDeviceName: "+mngDevice.getDevice().getDeviceName());
		     if(iCount >= getCurrentSpeed()){
		        iCount =0 ; 
		     }else{
		        iCount ++;
		        break;
		     }
			addNewCoordinates(mngDevice, (DeviceDataHandlerParameter) param);
			break;

		case DeviceHandlerParameter.UNCALIBRATED_DATA:
			// don't support this right now
			break;
		}
	}
	 private void touch_start(float x, float y) {
           // mPath.reset();
            mPreDrawnPath.moveTo(x, y);
            mX = x;
            mY = y;
            pathRecorder.add(x, y);
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPreDrawnPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
            pathRecorder.add(x, y);
        }
        private void touch_up() {
            mPreDrawnPath.lineTo(mX, mY);
            // commit the path to our offscreen
            canvas.drawPath(mPreDrawnPath, mPreDrawnPaint);
            // kill this so we don't double draw
           // mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
        // Let the ScaleGestureDetector inspect all events.
           mXScaleDetector.onTouchEvent(event);
           mYScaleDetector.onTouchEvent(event);
           drawScaleValueLegend( );
           
           if(!isPreDrawn || isPreDrawnComplete){
              if(mGestureDetector != null && 
                    mGestureDetector.onTouchEvent(event)){
                 iNeedUpdate=0;
                return true;
              }
              return super.onTouchEvent(event);
           }
           
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    if(mGestureDetector != null ){
                       mGestureDetector.setScaleStarted(false);
                    }
                    invalidate();
                    break;
            }
            return true;
        }
        private transient boolean isResumed=false;
        public void clearGraph(){
           
           lastKnownX = 0;
           isSizeChanged=true;
           //Log.i("test", "clearGraph :");
           if(getWidth() == 0 || getHeight() == 0){
              return;
           }
           clearGraph(getWidth(), getHeight() ,Bitmap.Config.ARGB_8888);
          // Log.i("test", "clearGraph :");
           postInvalidate();
           
        }
        
      /**
       * @param isResumed the isResumed to set
       */
      public void setResumed(boolean isResumed) {
         //Log.i("test", "setResumed :"+isResumed);
         this.isResumed = isResumed;
         lastKnownX=0;
         iNeedUpdate=0;
      }
        public void clearPreDrawn(){
           clearGraph();
           if(mPreDrawnPath != null){
              mPreDrawnPath.reset();  
           }
           invalidate();
        }
        
        public void lockPredrawn(final boolean isPreDrawnComplete){
           if(isPreDrawnComplete){
              pathRecorder.stopRecord(true);
           }
           this.isPreDrawnComplete=isPreDrawnComplete;
        }


/**
 * @param mGestureDetector the mGestureDetector to set
 */
public void setGestureDetector(MyGestureDetectorCompat gestureDetector) {
   this.mGestureDetector = gestureDetector;
   if( mXScaleDetector == null){
     mXScaleDetector = new MyXScaleGestureDetector(context, new MyAxisXScaleListener(mGestureDetector));
     mYScaleDetector = new MyYScaleGestureDetector(context, new MyAxisYScaleListener(mGestureDetector));
   }
}
private final static int GRID_COUNT=10;
private void drawGridView(final Canvas  canvas,final int iScreenWidth,final int iScreenHeight ){
   final int iCellWidth =iScreenWidth/GRID_COUNT;
   final int iCellHeight=iScreenHeight/GRID_COUNT;//LEGEND_TXT_Y+10;
   if(isDrawGrid){
      int x=iCellWidth;
      int y=iCellHeight;
      //draw y line vertical 
      for (int i = 0; i < GRID_COUNT; i++) {
         canvas.drawLine(x, 0, x, iScreenHeight, mGridColor);
         x+=iCellWidth;
      }
    //draw x line horizontal
      for (int i = 0; i < GRID_COUNT; i++) {
         canvas.drawLine(0, y, iScreenWidth, y, mGridColor);
         y+=iCellHeight;
      }
   }
}

public void onResume() {
   iNeedUpdate=0;
   clearGraph();
   
}
/*public void onResume() {
  
    mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST);
    mSensorManager.registerListener(this,
            mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_FASTEST);
    mSensorManager.registerListener(this, 
            mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_FASTEST);
}


public void onStop() {
    mSensorManager.unregisterListener(this);
   
}
private float mSpeed=1.0f;
private float mYOffset=1.0f;
private float   mScale[] = new float[2];
private float   mLastValues[] = new float[3*2];
private Paint testPaint= new Paint();
private SensorManager mSensorManager=null;

*/
public void onSensorChanged(SensorEvent event) {
  /* //Log.d(TAG, "sensor: " + sensor + ", x: " + values[0] + ", y: " + values[1] + ", z: " + values[2]);
   synchronized (this) {
       if (bitmap != null) {
           
          
           if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
             
           } else {
               float deltaX = mSpeed;
               float newX = lastKnownX + deltaX;

               int j = (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) ? 1 : 0;
              if(j == 0){
               for (int i=2 ; i< 3 ; i++) {
                   int k = i+j*3;
                   final float v = mYOffset + event.values[i] * mScale[j];
                   // for demo scale
                   yScale=1;
                   addNewCoordinates(v);
                   //canvas.drawLine(lastKnownX, mLastValues[k], newX, v, testPaint);
                   mLastValues[k] = v;
               }
              }
               if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                //  lastKnownX += mSpeed;
               }
           }
           invalidate();
       }
   }*/
   }


@Override
public void onAccuracyChanged(Sensor sensor, int accuracy) {

   
}


@Override
public void onDemoDataUpdate(float fData) {

  // Log.i("test","demo data received");
   if ( maxX == 0 || ! isResumed ){
      return;
   }
   
   //Log.i("test","demo data received");
   yScale=200;
   addNewCoordinates(fData);
}

private int getEMGScale(final int iGroupIndex){
  if( (iNeedUpdate & EMG_OFFSET_UPDATED) == 0){
    
     iEmgOffsetScale = settingsManager.getEMGScale(context,
           RehabFragment.getSensorPosition(iGroupIndex, context));
     Log.i("test1", "iEmgOffsetScale:"+iEmgOffsetScale);
     iNeedUpdate |=EMG_OFFSET_UPDATED;
  }
  
  return   iEmgOffsetScale * 32;
}
private double getSettingSpeed(final Context context){
   if( (iNeedUpdate & SPEED_UPDATED) == 0){
      dSpeed=settingsManager.getTimeBase(context)-1;
       iNeedUpdate |=SPEED_UPDATED;
       Log.i("test1", "dSpeed:"+dSpeed);
       final NtDeviceManagement deviceManagement =NtDeviceManagement.getDefaultDeviceManager(context);
       deviceManagement.setTimeBase(dSpeed);
   }
   return dSpeed;
}
}
