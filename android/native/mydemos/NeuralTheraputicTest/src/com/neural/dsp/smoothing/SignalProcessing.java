package com.neural.dsp.smoothing;

import java.util.ArrayList;

public class SignalProcessing {

	private static final String UINT8 = "u8";
	private static final String INT8 = "i8";
	private static final String UINT12 = "u12";
	private static final String UINT16 = "u16";
	private static final String INT16 = "i16";
	
	public static final int DEFAULT_SMOOTH_WIDTH = 1;
	// public static final int DEFAULT_ORDER = 2;
	// public static final float DEFAULT_FREQ_CUT = 5;
	// public static final boolean DEFAULT_PASS = true;

	// private Butterworth butterWorth = null;
	// private Filter filter = null;
	private ArrayList<Double> rawValues;
	// private int smoothWidth = 0;
	
	private double currentValue[] = null;
	private LowPassFilter lowPassFilter = null;

	public SignalProcessing(int smoothWidth, int order, float cut, boolean low) {
		
  		//  this.smoothWidth = (smoothWidth < DEFAULT_SMOOTH_WIDTH) ? DEFAULT_SMOOTH_WIDTH : smoothWidth;
		//  butterWorth = new Butterworth((order < DEFAULT_ORDER) ? DEFAULT_ORDER : order, cut, low);
		//  filter = new Filter(butterWorth.computeB(), butterWorth.computeA());
		//  rawValues = new ArrayList<Double>(DEFAULT_SMOOTH_WIDTH);
	}
	
	public SignalProcessing(int smoothWidth) {
		
		this.lowPassFilter = new LowPassFilter();
		currentValue = new double[1];
		rawValues = new ArrayList<Double>(50);
	}
	
	public LowPassFilter getFilter() {
		return lowPassFilter;
	}
	
	public boolean addRawValue(double rawValue, String dataType) {
		
		double value = rawValue;
        if (dataType == INT8) {
        	value += 127;
        }
        else if (dataType == INT16) {
        	value += 2047;
        }
        
        // rectify on the fly and cache the value
        value = Rectifier.rectifiy(value);
        
        // signal the caller when we have enough data cached
        // rawValues.add(Double.valueOf(value));
        // return (rawValues.size() >= smoothWidth) ? true : false;

        // low pass filter
        currentValue[0] = value; //lowPassFilter.getSmoothValue(value);
        return true; // always signal availability of data        
	}
	public boolean addNewRawValue(double rawValue, String dataType) {
                    
                    double value = rawValue;
            if (dataType == INT8) {
                    value += 127;
            }
            else if (dataType == INT16) {
                    value += 2047;
            }
            
            // rectify on the fly and cache the value
            value = Rectifier.rectifiy(value);
            
            // signal the caller when we have enough data cached
             rawValues.add(Double.valueOf(value));
             return (rawValues.size() >= 50) ? true : false;
         
                
   }
	public double[] computeSmoothValue() {
		// apply pass band fitler
		// double result[] = filter.filter(rawValues);		
		// return result;
		return currentValue;
	}
	public double[] newComputeSmoothValue() {
           // apply pass band fitler
            double result[] =  new double[rawValues.size()];         
           // return result;
            int i=0;
	   for (double d : rawValues) {
	      result[i++]=d;
         }
           return result;
   }
	
	public double removeZero(){
           return rawValues.remove(0);
        }
	public void clear() {
		currentValue[0] = 0;
		if(rawValues != null){
		// rawValues.clear();
		}
	}
	
	int index = 0;
	double CICOutput = 0;
	public static final int INIT =1;
	public static final int STEADYSTATE =2;
	public int iState = INIT;
	private ArrayList<Double> sampleBuf =new ArrayList<Double>(WINDOW_LEN); //Initialize with zeros
	private static final int WINDOW_LEN =64;
	
	public  double  movingAverage(double input)
	{
	        
	        
	       
	        

	        switch(iState)
	        {
	                case INIT:{ //Wait till the Samplexbuf is filled with input samples
	                        if (index < WINDOW_LEN)
	                        {
	                                sampleBuf.add(index, input);
	                                index = index + 1;
	                        }
	                        else
	                        {
	                                index      = 0;
	                                CICOutput  = sum(sampleBuf)/WINDOW_LEN; //Compute Average for the first and only time
	                                iState      = STEADYSTATE;
	                        }
	                break;
	                }
	                case STEADYSTATE:{
	                        
	                        CICOutput = CICOutput + ((input - sampleBuf.get(0))/WINDOW_LEN); //Compute moving average

//	                        for(int i = 1; i < WINDOW_LEN;i++) //Shift sampleBuf - Circular buffer implementation
//	                        {
//	                                sampleBuf(i-1) = sampleBuf(i);
//	                        }
	                        sampleBuf.remove(0);
	                        sampleBuf.add(WINDOW_LEN-1, input); //Update Latest sample
	                        break;   
	                }
	        }
	                        
	        return(CICOutput);

	}
	private double sum(final ArrayList<Double> arrayList){
	   double sum=0;
	   for (final Double double1 : arrayList) {
             sum = sum +double1;
         }
	   return sum;
	}
}
