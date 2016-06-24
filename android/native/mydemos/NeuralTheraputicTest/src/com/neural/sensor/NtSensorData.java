package com.neural.sensor;

import java.util.ArrayList;
import java.util.Date;

public class NtSensorData {
	
	private ArrayList<NTSensorCoordinate> coordinates = null;
	private NtSensorGraphAttributes attributes = null;
	
	public static class NTSensorCoordinate {
		
		private double x;
		private double y;
		private long timeStamp;
		
		public NTSensorCoordinate() {
			x = y = 0;
			timeStamp = 0;
		}
		
		public NTSensorCoordinate(double x, double y) {
			this.x = x;
			this.y = y;
			
			// time stamp
			Date dateSetter = new Date();
			timeStamp = dateSetter.getTime();
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}

		public long getTimeStamp() {
			return timeStamp;
		}

		public void setTimeStamp(long timeStamp) {
			this.timeStamp = timeStamp;
		}
	}

	public NtSensorData(NtSensorGraphAttributes attributes) {
		coordinates = new ArrayList<NTSensorCoordinate>(attributes.getMaxHistoryToKeep());
		this.attributes = attributes;
	}
	
	public NTSensorCoordinate getLastCoordinate() {
		NTSensorCoordinate lastCoordinate = null;
		if (coordinates.size() > 0) {
			lastCoordinate = coordinates.get(coordinates.size() - 1);
		}
		return lastCoordinate;
	}
	
	public ArrayList<NTSensorCoordinate> getCoordinates() {
		return coordinates;
	}
	
	public NTSensorCoordinate computeDataPoint(double newY) {
		
		double lastX = 0;
		
		// compute coordinates
		NTSensorCoordinate lastCoordinate = getLastCoordinate();
		if (lastCoordinate != null) {
			lastX = lastCoordinate.getX();
		}
		
		NTSensorCoordinate newCoordinate = new NTSensorCoordinate(lastX + attributes.getSpeed(), newY);
		return newCoordinate;
	}
	public NTSensorCoordinate computeDataPoint(final double newY,final double dCurrentSpeed) {
           
           double lastX = 0;
           
           // compute coordinates
           NTSensorCoordinate lastCoordinate = getLastCoordinate();
           if (lastCoordinate != null) {
                   lastX = lastCoordinate.getX();
           }
           
           NTSensorCoordinate newCoordinate = new NTSensorCoordinate(lastX + dCurrentSpeed, newY);
           return newCoordinate;
   }
	public boolean addDataPoint(NTSensorCoordinate newDataPoint) {

		NTSensorCoordinate lastCoordinate = getLastCoordinate();
		if (lastCoordinate != null) {
			
			// sanity check for X
			if (lastCoordinate.getX() >= newDataPoint.getX()) {
				return false;
			}
		}
		
		if (coordinates.size() == attributes.getMaxHistoryToKeep()) {
			coordinates.remove(0);
		}
		
		coordinates.add(newDataPoint);
		return true;
	}
	
	public void resetXValues() {
		
		double lastX = 0;

		for (int i = 0; i < coordinates.size(); i ++) {
			NTSensorCoordinate coordinate = coordinates.get(i);
			coordinate.setX(lastX);
			lastX += attributes.getSpeed();
		}
	}

	public void clear() {
		coordinates.clear();
	}
}
