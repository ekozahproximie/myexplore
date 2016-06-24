package com.trimble.ag.lono.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.trimble.mobile.android.util.Utils;

public class CustomPivotView extends View {

   private static final String TAG                   = CustomPivotView.class
                                                           .getSimpleName();

   public static final int    SUBSTANCE_DRY         = 0;
   public static final int    SUBSTANCE_FERTIGATION = 1;
   public static final int    SUBSTANCE_WATER       = 2;
   public static final int    SUBSTANCE_EFFLUENT    = 3;

   public static final int    ROTATION_NONE         = 0;
   public static final int    ROTATION_FORWARD      = 1;
   public static final int    ROTATION_BACKWARD     = 2;
   public static final int    ROTATION_UN_AVAILABLE  =3;
   public static final int    PIVOT_ARM_SWEEP       = 20;
   private transient Paint     mBorderColor;
   private transient Paint     mFillColor;
   private transient Paint     mCenterCircleFillColor;
   private transient MaskFilter mEmboss;
   
   private transient boolean   isHasApporximateArm=false;
   private transient double  dApporximateArmHeading=-1;
   private transient Paint     mPivotArmColor=null;
   private transient Paint     mArrowColor;

   private transient int       mRadius;
   private transient double       mStartAngle;
   private transient double       mStopAngle;
   private transient int       mHeading;
   private transient int       mRotationDir;
   private transient int       mInnerCircleRadius;
   
   private transient int       mPivotArrowLegth=0;
   protected transient int     mSubstanceType;
   private transient boolean       mIsStatusValid;
   private transient boolean       mIsPumpOff;
   private transient boolean       mIsArrowDrawn;
   private transient float       fShadowRadius;
   private transient float       fDx;
   private transient float       fDy;
   private transient float       fLineWidth;
   private transient float       fLineInterval;
   
   private transient RectF     mOval                 = null;
   private transient Path      path                  = null;

   public static final String DRY                   = "dry";
   public static final String WATER                 = "water";
   public static final String FERTIGATION           = "fertigation";
   public static final String EFFLUENT              = "effluent";

   public static final String DIRECTION_NONE                  = "none";
   public static final String DIRECTION_FORWARD               = "clockwise";
   public static final String DIRECTION_BACKWARD              = "antiClockwise";
   //public static final String UNAVAILABLE           = "unavailable";

   public static final String PUMP_ON = "on";
   public static final String PUMP_OFF = "off";
   
   public static final int PUMP_STATUS_ON = 1;
   public static final int PUMP_STATUS_OFF = 0;
   
   private transient Drawable drawable = null;
   private transient int iWarningIconSize;
   private transient Bitmap bitmap = null;
   
   public static final String NO_VALUE = "-";
   
   public CustomPivotView(Context context) {
      super(context);

   }

   public CustomPivotView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

   }

   public CustomPivotView(Context context, AttributeSet attrs) {
      super(context, attrs);
      init(context, attrs);
   }

   private void init(final Context context, final AttributeSet attrs) {
      setFocusable(true);
      setClickable(true);

      drawable = getResources().getDrawable(R.drawable.pivot_warning);
      iWarningIconSize = (int) Utils.convertDpToPixel(getResources().getDimension(R.dimen.warning_icon_size)*2,context);
      bitmap = ((BitmapDrawable) drawable).getBitmap();         
      
      if(android.os.Build.VERSION.SDK_INT >= 11 ) {
         setLayerType(View.LAYER_TYPE_SOFTWARE, null);
      }

      // look up any layout-defined attributes
      TypedArray attrArray = context.obtainStyledAttributes(attrs,
            R.styleable.PivotStatusView);

      final int N = attrArray.getIndexCount();
      for (int i = 0; i < N; i++) {
         int attrIndex = attrArray.getIndex(i);
         switch (attrIndex) {
            case R.styleable.PivotStatusView_radius: {
               mRadius = attrArray.getDimensionPixelSize(attrIndex, 0);
            }
               break;
            case R.styleable.PivotStatusView_heading: {
               mHeading = attrArray.getInt(attrIndex, -1);
            }
               break;
            case R.styleable.PivotStatusView_innercircleradius: {
               mInnerCircleRadius = attrArray.getDimensionPixelSize(attrIndex,
                     0);
            }
               break;
            case R.styleable.PivotStatusView_startangle: {
               mStartAngle = attrArray.getInt(attrIndex, 0);
            }
               break;
            case R.styleable.PivotStatusView_stopangle: {
               mStopAngle = attrArray.getInt(attrIndex, 0);
            }
               break;
            case R.styleable.PivotStatusView_substance: {
               mSubstanceType = attrArray.getInt(attrIndex, SUBSTANCE_DRY);
            }
               break;
            case R.styleable.PivotStatusView_rotation: {
               mRotationDir = attrArray.getInt(attrIndex, ROTATION_NONE);
            }
               break;
            
            case R.styleable.PivotStatusView_arrowlength: {
               mPivotArrowLegth = attrArray.getDimensionPixelSize(attrIndex,
                     0);
            }
               break;
               
            case R.styleable.PivotStatusView_dash_line_interval: {
               fLineInterval = attrArray.getDimensionPixelSize(attrIndex,
                     0);
            }
               break;
               
            case R.styleable.PivotStatusView_dash_line_width: {
               fLineWidth = attrArray.getDimensionPixelSize(attrIndex,
                     0);
            }
               break;
               
            case R.styleable.PivotStatusView_shadow_dx: {
               fDx = attrArray.getDimensionPixelSize(attrIndex,
                     0);
            }
               break;
               
            case R.styleable.PivotStatusView_shadow_dy: {
               fDy = attrArray.getDimensionPixelSize(attrIndex,
                     0);
            }
               break;
               
            case R.styleable.PivotStatusView_shadow_radius: {
               fShadowRadius = attrArray.getDimensionPixelSize(attrIndex,
                     0);
            }
               break;
         
            default:
               break;
         }
      }
      
      path = new Path();
      mBorderColor = new Paint();
      mBorderColor.setAntiAlias(true);
      mBorderColor.setStyle(Style.FILL_AND_STROKE);
      //mEmboss = new EmbossMaskFilter(new float[] {0f, -1.0f, 0.5f}, 0.8f, 15f, 1f);
      //mBorderColor.setMaskFilter(mEmboss);
      mBorderColor.setShadowLayer(fShadowRadius, fDx, fDy, Color.BLACK);
      
      mFillColor = new Paint();
      mFillColor.setAntiAlias(true);
      mFillColor.setStyle(Style.FILL);
      mFillColor.setColor(0x3FD00000);
      
      mCenterCircleFillColor = new Paint();
      mCenterCircleFillColor.setAntiAlias(true);
      mCenterCircleFillColor.setStyle(Style.FILL);
      mCenterCircleFillColor.setColor(0xFF000000);
      mCenterCircleFillColor.setStrokeWidth(fLineWidth);
      
      mArrowColor = new Paint();
      mArrowColor.setAntiAlias(true);
      mArrowColor.setStrokeWidth(6.5f);
      mArrowColor.setStyle(Style.STROKE);
      mArrowColor.setColor(Color.BLACK);
      mArrowColor.setStrokeJoin(Join.ROUND);
      
      mPivotArmColor= new Paint();
      mPivotArmColor.setDither(true);
      mPivotArmColor.setAntiAlias(true);
      mPivotArmColor.setStyle(Style.STROKE);
      mPivotArmColor.setStrokeWidth(fLineWidth);
      mPivotArmColor.setStrokeJoin(Paint.Join.ROUND);
      mPivotArmColor.setStrokeCap(Paint.Cap.ROUND);
      mPivotArmColor.setColor(Color.BLACK);
      mPivotArmColor.setPathEffect(new DashPathEffect(new float[]{fLineInterval,fLineInterval},0));
      
      setPivotFillColor(mSubstanceType);

   }

   protected void onDraw(Canvas canvas) {
      float wf = getWidth();
      float hf = getHeight();

      final float cx = wf / 2;
      final float cy = hf / 2;
      
    
      drawPivotCoverage(mStartAngle, mStopAngle, mFillColor, mBorderColor,
            mRadius, canvas, wf, hf);
      
      drawCenterCircle(cx, cy, mInnerCircleRadius, mCenterCircleFillColor,
            canvas, mIsStatusValid);
     
      drawPivotArm(mHeading, canvas,
            mCenterCircleFillColor,mStopAngle,mStartAngle,cx, cy,mRadius, mIsStatusValid);
      
      drawPivotDashArm(mHeading, canvas, mPivotArmColor, cx, cy, mRadius, mIsStatusValid);
      
      drawTriangle(mRadius, cx, cy, canvas, mHeading, mRotationDir,
               mCenterCircleFillColor, mIsStatusValid);         
      
      drawWarningIcon(canvas, cx, cy,mIsStatusValid);
   }

   private void drawWarningIcon(final Canvas canvas, final float cx, final float cy, final boolean isStatusValid){
      if(isStatusValid){
         return;
      }
      
         bitmap = Bitmap.createScaledBitmap(bitmap, iWarningIconSize, iWarningIconSize, true);
         canvas.drawColor(Color.TRANSPARENT);
         canvas.drawBitmap(bitmap, cx - iWarningIconSize/2, cy-iWarningIconSize/2, null);         
   }
   
   private void drawPivotCoverage( double iStartAngle,  double iStopAngle,
         final Paint fillColor, final Paint borderColor, final int iRadius,
         final Canvas canvas, float wf, float hf) {
      // This is the area you want to draw on
      if (mOval == null) {
	     wf = wf -  getPaddingRight();
     	 hf = hf -  getPaddingBottom();

         mOval = new RectF(getPaddingLeft(), getPaddingTop(), wf, hf);
      }
       double sweepAngle = iStopAngle - iStartAngle;
      // canvas.drawOval(mOval, fillColor);
      if(iStartAngle >= iStopAngle){
         iStopAngle=iStopAngle + (360-iStartAngle);
         //iStopAngle=0;
         //iStartAngle=360;
         sweepAngle=iStopAngle;
         Log.d(TAG, "Pivot coverage StartAngle > StopAngle"+iStartAngle+","+iStopAngle);
      }
      
      // Calculate how much of an angle you want to sweep out here

      // 270 is vertical. I found that starting the arc from just slightly
      // less than vertical makes it look better when the circle is almost
      // complete.
      final float iRoundofForArc = (float)(iStartAngle + ARC_CIRCLE_DIFF) % 360;
      
      canvas.drawArc(mOval, iRoundofForArc,  (float)sweepAngle, true, fillColor);
      canvas.drawArc(mOval, iRoundofForArc,  (float)sweepAngle, true, borderColor);

   }

   private void drawCenterCircle(final float cx, final float cy,
         final int iInnerCircleRadius, final Paint centerCricleColor,
         final Canvas canvas, final boolean isStatusValid) {
      
      if( ! isStatusValid){
         return;
      }
      
      canvas.drawCircle(cx, cy, iInnerCircleRadius, centerCricleColor);
   }

   private final static int ARC_CIRCLE_DIFF = 270;

   private void drawPivotArm( int iHeading,
         final Canvas canvas, final Paint pivotArmColor,
         final double dStopAngle,final double dStartAngle,final float cx, final float cy,final float fRadius,
         final boolean isStatusValid) {
      
      if( ! isStatusValid || ! isHasApporximateArm){
         return;
        
      }
      
      if(isHasApporximateArm){
         iHeading=(int) dApporximateArmHeading;
        
      }
      if(iHeading == -1){
         Log.d(TAG, "Pivot Heading is -1");
         return;
      }
      
      if(! isHeadingValid(iHeading, (int)mStartAngle, (int)mStopAngle)){
         Log.d(TAG, "Pivot Approximate Heading is :"+iHeading+" not with in :"+dStartAngle+","+dStopAngle);
         return;
      }
   
   
     
      final int iRoundofForArc = (iHeading + ARC_CIRCLE_DIFF) % 360;
   
        final int stopAngleX = getXFromAngle(fRadius,iRoundofForArc,cx);
        final int stopAngleY = getYFromAngle(fRadius,iRoundofForArc,cy);
       canvas.drawLine(cx, cy, stopAngleX,  stopAngleY, pivotArmColor);
     
      
   }

   private void drawPivotDashArm(final int iHeading, 
         final Canvas canvas, final Paint pivotArmColor,
         final float cx,final float cy,final float fRadius, final boolean isStatusValid) {
     
      if( ! isStatusValid){
         return;
      }
      
      if(iHeading == -1 || ! isHeadingValid(iHeading,(int) mStartAngle,(int) mStopAngle ) ){
         Log.d(TAG, "Pivot Dash arm  Heading is -1 or iHeading >  mStopAngle"+iHeading+","+mStopAngle);
         return;
      }
    
      final int iRoundofForArc = (iHeading + ARC_CIRCLE_DIFF) % 360;
      
      final int stopAngleX = getXFromAngle(fRadius,iRoundofForArc,cx);
      final int stopAngleY = getYFromAngle(fRadius,iRoundofForArc,cy);
     canvas.drawLine(cx, cy, stopAngleX,  stopAngleY, pivotArmColor);
     
    
   }
 
   private void drawTriangle(final int iRadius, final float cx, final float cy,
         final Canvas canvas,  int iHeading, int iRotation,
         final Paint triangleFillColor, final boolean isStatusValid) {
      
      if( ! isStatusValid || ! mIsArrowDrawn){
         return;
      }
      
      if(iRotation == ROTATION_NONE || iHeading == -1){
         Log.d(TAG, "Pivot rotation is none or Pivot Heading is -1");
         return;
      }
      if(isHasApporximateArm ){
         iHeading=(int) dApporximateArmHeading;
       }
      
      if( ! isHeadingValid(iHeading,(int) mStartAngle,(int) mStopAngle)){
         Log.d(TAG, "Pivot rotation is  not within the range ");
         return; 
      }
      int iRoundofForArc = 0;
      int iRoundofArcForArrow=0;
 
      int x = 0;
      int y = 0;

      int x0 = 0;
      int y0 = 0;
    
      
      
      int iFirstPointRadius = iRadius;
      int iSecondPointRadius = (int)(iRadius * 3/4f);
      float iThirdPointRadius = (iFirstPointRadius + iSecondPointRadius ) / 2;
      int iThirdPointAngle = 0;
      
      path.reset();

      int x1 = 0, y1 = 0;
      if (ROTATION_FORWARD == iRotation) {
         iRoundofForArc = ((iHeading + ARC_CIRCLE_DIFF) % 360 ) + ((mPivotArrowLegth))+2;
         iRoundofArcForArrow= ((iHeading + ARC_CIRCLE_DIFF) % 360 ) + (( mPivotArrowLegth)/2);
         iThirdPointAngle = iRoundofForArc + 8;
         
      } else if (ROTATION_BACKWARD == iRotation) {
         iRoundofForArc = ((iHeading + ARC_CIRCLE_DIFF) % 360 ) - (mPivotArrowLegth)-2;
         iRoundofArcForArrow=((iHeading + ARC_CIRCLE_DIFF) % 360 ) - ((mPivotArrowLegth)/2);
         iThirdPointAngle = iRoundofForArc - 8;
      }
      
      x = getXFromAngle(iFirstPointRadius,iRoundofForArc,cx);
      y = getYFromAngle(iFirstPointRadius,iRoundofForArc,cy);
      
      x0 = getXFromAngle(iSecondPointRadius,iRoundofForArc,cx);
      y0 = getYFromAngle(iSecondPointRadius,iRoundofForArc,cy);
      
      x1 = getXFromAngle(iThirdPointRadius,iThirdPointAngle,cx);
      y1 = getYFromAngle(iThirdPointRadius,iThirdPointAngle,cy); 
      
      
    
      
      path.moveTo(x, y);
      path.lineTo(x0, y0);
      path.lineTo(x1, y1);
      path.close();
      canvas.drawPath(path, triangleFillColor);
      
      drawPivotArrow(canvas, iRoundofArcForArrow, iFirstPointRadius, iSecondPointRadius, cx, cy,iRoundofForArc);
    
   }
   
   public static boolean isHeadingValid(final int iHeading,final int iStartAngle,final int iStopAngle){
      if(iHeading == -1 || iStartAngle == -1 || iStopAngle == -1){
         return false;
      }
      final boolean isHeadingValid= 
            ( iStartAngle > iStopAngle   &&   ( iHeading >= iStartAngle ?   iHeading <= 360  : iHeading <= iStopAngle )) 
                                          ||
               ( iHeading >= iStartAngle && iHeading <= iStopAngle );    
      return isHeadingValid; 
   }
   
   //https://www.tbray.org/ongoing/When/200x/2009/01/02/Android-Draw-a-Curved-Line
   private void drawPivotArrow(final Canvas canvas,
         final int iRoundofArcForArrow ,
         final int iFirstPointRadius,final int iSecondPointRadius,final float cx,final float cy,int iRoundofForArc){
      
      int iArrowStartX0=0;
      int iArrowStartY0=0;
      
      int iArrowEndX0=0;
      int iArrowEndY0=0;
      
      iArrowEndX0= getXFromAngle((iFirstPointRadius + iSecondPointRadius)/2,iRoundofArcForArrow,cx);
      iArrowEndY0=getYFromAngle((iFirstPointRadius + iSecondPointRadius)/2,iRoundofArcForArrow,cy);
      
      iArrowStartX0 = getXFromAngle((iFirstPointRadius + iSecondPointRadius)/2,iRoundofForArc,cx); 
      iArrowStartY0 = getYFromAngle((iFirstPointRadius + iSecondPointRadius)/2,iRoundofForArc,cy);
     
    
      
      path.reset();
      path.moveTo( iArrowStartX0,iArrowStartY0);
      path.lineTo(iArrowEndX0,iArrowEndY0 );
      path.close();
      canvas.drawPath(path, mArrowColor);
   }

   private int getXFromAngle(final float fRadius,final int iHeading,final float cx){
      final int iX= (int) (fRadius* Math.cos(iHeading  * (Math.PI / 180) ) + cx);
      return iX;
   }
   private int getYFromAngle(final float fRadius,final int iHeading,final float cy){
      final int iY= (int) (fRadius* Math.sin(iHeading  * (Math.PI / 180) ) + cy);
      return iY;
   }

   @Override
   protected void onMeasure(int widthSpec, int heightSpec) {
      int totalWidth = 2 * mRadius + getPaddingLeft() + getPaddingRight();
      int totalHeight = 2 * mRadius + getPaddingBottom() + getPaddingTop();
      setMeasuredDimension(totalWidth, totalHeight);
   }

   public void updateData(final PivotData pivotData) {
      if (pivotData != null) {
         mStartAngle = pivotData.dStartAngle;
         mStopAngle = pivotData.dStopAngle;
         mHeading = pivotData.iHeading;
         mSubstanceType = pivotData.iSubstanceType;
         mRotationDir = pivotData.iRotationDir;
         mIsStatusValid = pivotData.isStatusValid;
         mIsPumpOff = pivotData.isPumpOff;
         isHasApporximateArm=pivotData.isHasValidApproximatPivotArm();
         dApporximateArmHeading=pivotData.getApproximatePivotArmHeading();
         this.mIsArrowDrawn = pivotData.isArrowDrawn;
         setPivotFillColor(mSubstanceType);
         invalidate();
      }

   }

   private void setPivotFillColor(final int iSubstanceType) {

      switch (iSubstanceType) {
         case SUBSTANCE_DRY:
            mFillColor.setColor(0x8F828282);
            mBorderColor.setColor(0xFF828282);
            break;
         case SUBSTANCE_FERTIGATION:
            mFillColor.setColor(0x8F73C83C);
            mBorderColor.setColor(0xFF73C83C);
            break;
         case SUBSTANCE_WATER:
            mFillColor.setColor(0x8F1464B4);
            mBorderColor.setColor(0xFF1464B4);
            break;
         case SUBSTANCE_EFFLUENT:
            mFillColor.setColor(0x8F875A28);
            mBorderColor.setColor(0xFF875A28);
            break;
         default:
            // dry
            mFillColor.setColor(0x8F828282);
            mBorderColor.setColor(0xFF828282);
            break;
      }
   }

   public static int getSubstanceByName(String stSubstanceType) {
      int iSubStanceType = SUBSTANCE_DRY;
      if (stSubstanceType != null) {
         stSubstanceType = stSubstanceType.toLowerCase();
         if (stSubstanceType.equals(DRY.toLowerCase())) {
            iSubStanceType = SUBSTANCE_DRY;
         } else if (stSubstanceType.equals(WATER.toLowerCase())) {
            iSubStanceType = SUBSTANCE_WATER;
         } else if (stSubstanceType.equals(FERTIGATION.toLowerCase())) {
            iSubStanceType = SUBSTANCE_FERTIGATION;
         } else if (stSubstanceType.equals(EFFLUENT.toLowerCase())) {
            iSubStanceType = SUBSTANCE_EFFLUENT;
         }
      }

      return iSubStanceType;
   }

   public static int getDirectionByName(String stDirection) {
      int iDirection = ROTATION_UN_AVAILABLE;
      if (stDirection != null) {
         stDirection = stDirection.toLowerCase();
         if (stDirection.equals(DIRECTION_NONE.toLowerCase())) {
            iDirection = ROTATION_NONE;
         } else if (stDirection.equals(DIRECTION_FORWARD.toLowerCase())) {
            iDirection = ROTATION_FORWARD;
         } else if (stDirection.equals(DIRECTION_BACKWARD.toLowerCase())) {
            iDirection = ROTATION_BACKWARD;
         }
      }

      return iDirection;
   }

   public static boolean isPivotOff(String stDirection) {
      boolean isPivotOff = true;
      if (stDirection != null) {
         stDirection = stDirection.toLowerCase();
         if (stDirection.equals(DIRECTION_BACKWARD.toLowerCase()) || stDirection.equals(DIRECTION_FORWARD.toLowerCase())) {
            isPivotOff = false;
         } 
      }

      return isPivotOff;
   }
   
   public static String getPivotStatus(final String stDirection, final Context context){
      String stPivotStatus = context.getString(R.string.Status_Off);
      final int iRotation = getDirectionByName(stDirection);
      if(iRotation == ROTATION_BACKWARD || iRotation == ROTATION_FORWARD){
         stPivotStatus = context.getString(R.string.Status_On);
      }
      return stPivotStatus;
   }
   
   public static class PivotData {

      private double dStartAngle    = 0;
      private double dStopAngle     = 0;
      private int iSubstanceType = 0;
      private int iHeading       = 0;
      private int iRotationDir   = 0;
      private boolean isStatusValid = false;
      private double dDepth   = 0;
      private double dApplicationRate=0;
      private double dPivotMinRotationPeriod=0;
      private long lTimeLastResport=0;
      private int iApporximateHeading=0;
      private boolean isPumpOff = false;
      private boolean isArrowDrawn = false;
      
      private PivotData(final double dStartAngle, final double dStopAngle,
            int iHeading, int iSubstanceType, int iRotationDir, final boolean isStatusValid,
            final boolean isPumpOff, final boolean isArrowDrawn) {
         if (iSubstanceType < SUBSTANCE_DRY
               || iSubstanceType > SUBSTANCE_EFFLUENT) {
            iSubstanceType = SUBSTANCE_DRY;
         }
         
         if(isStatusValid){
            if(iRotationDir == ROTATION_UN_AVAILABLE){
               iRotationDir = ROTATION_NONE;
            }
            if(iHeading == -1){
               iHeading = (int) dStartAngle;
            }
         }
         
         this.dStartAngle = dStartAngle;
         this.dStopAngle = dStopAngle;
         this.iSubstanceType = iSubstanceType;
         this.iHeading = iHeading;
         this.iRotationDir = iRotationDir;
         this.isStatusValid = isStatusValid;
         this.isPumpOff = isPumpOff;
         this.isArrowDrawn = isArrowDrawn;
      }

      public PivotData(final double dStartAngle, final double dStopAngle,
            final int iHeading, final String stSubStanceType,
            final String stRotationDir, final boolean isStatusValid, final String stPumpStatus, 
            final boolean isArrowDrawn) {
         this(dStartAngle, dStopAngle, iHeading,
               getSubstanceByName(stSubStanceType),
               getDirectionByName(stRotationDir),isStatusValid, isPivotOff(stRotationDir),
               isArrowDrawn);
      }
      public static final long _1_HOUR= 60L *1000L *60L;
      public static final long _12_HOURS= _1_HOUR * 12L; 
      private boolean isVariableValid(final double  dValue){
         return dValue != 0 && dValue != -1;
      }
   
         public boolean isHasValidApproximatPivotArm() {
            final boolean isValid = (isVariableValid(dDepth)
                  && isVariableValid(dApplicationRate)
                  && isVariableValid(dPivotMinRotationPeriod) && isVariableValid(lTimeLastResport))
                  && (System.currentTimeMillis() - lTimeLastResport < _12_HOURS);
            return isValid;
         }
   
         public double getApproximatePivotArmHeading() {
            if (!isHasValidApproximatPivotArm()) {
               return -1;
            }
            long lTimeSinceLastStatus = System.currentTimeMillis()
                  - lTimeLastResport;
            final double timeDelta = ( lTimeSinceLastStatus/ (dPivotMinRotationPeriod * _1_HOUR));
            final double dHeadingDelta = timeDelta * 360.0;
            double dApprHeading = dHeadingDelta * (dDepth / dApplicationRate);
            final double degreeminutesCon = (1d / 60d);
            if (iRotationDir == ROTATION_BACKWARD) {
               dApprHeading = iHeading - dApprHeading; // dHeading-
   // (iHeading/ (1d / 60d));
            } else if (iRotationDir == ROTATION_FORWARD) {
               dApprHeading = iHeading  + dApprHeading; // dHeading +
   // (iHeading/ (1d / 60d));
            }
   
            return dApprHeading % 360;
         }
      
      
      /**
       * @param iHeading
       *           the iHeading to set
       */
      public void setHeading(int iHeading) {
         this.iHeading = iHeading;
      }

      /**
       * @param iRotationDir
       *           the iRotationDir to set
       */
      public void setRotationDir(int iRotationDir) {
         this.iRotationDir = iRotationDir;
      }

      /**
       * @param dStartAngle
       *           the dStartAngle to set
       */
      public void setStartAngle(int iStartAngle) {
         this.dStartAngle = iStartAngle;
      }

      /**
       * @param dStopAngle
       *           the dStopAngle to set
       */
      public void setStopAngle(int iStopAngle) {
         this.dStopAngle = iStopAngle;
      }

      /**
       * @param iSubstanceType
       *           the iSubstanceType to set
       */
      public void setSubstanceType(int iSubstanceType) {
         this.iSubstanceType = iSubstanceType;
      }
      public void setSubstanceType(String stSubstanceType) {
         this.iSubstanceType = getSubstanceByName(stSubstanceType);
      }
      public void setRotationDir(String stDirection) {
         this.iRotationDir = getDirectionByName(stDirection);
      }
      
      /**
       * @param dDepth the dDepth to set
       */
      public void setDepth(double dDepth) {
         this.dDepth = dDepth;
      }
      
      /**
       * @param dApplicationRate the dApplicationRate to set
       */
      public void setApplicationRate(double dApplicationRate) {
         this.dApplicationRate = dApplicationRate;
      }
      
      /**
       * @param dPivotMinRotationPeriod the dPivotMinRotationPeriod to set
       */
      public void setPivotMinRotationPeriod(double dPivotMinRotationPeriod) {
         this.dPivotMinRotationPeriod = dPivotMinRotationPeriod;
      }
      
      /**
       * @param lTimeLastResport the lTimeLastResport to set
       */
      public void setTimeLastResport(long lTimeLastResport) {
         this.lTimeLastResport = lTimeLastResport;
      }
      
      
      /**
       * @param iApporximateHeading the iApporximateHeading to set
       */
      public void setApporximateHeading(int iApporximateHeading) {
         this.iApporximateHeading = iApporximateHeading;
      }
      /**
       * @return the iRotationDir
       */
      public int getRotationDir() {
         return iRotationDir;
      }
      
   }
   
}
