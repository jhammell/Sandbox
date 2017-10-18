package org.neoninc.dpms.algorithms.wind2dprocessors;

/**
 * This class is used to calculate distorted flow around a tower or other similar object.
 * Distorted flow is calculated based on the X and Y offsets of the obstruction and the 
 * length of the boom containing the device.
 * 
 * Calculations assume a right-handed coordinate system, relative to the boom.
 * The (0,0) position is at the tower corner, or the base point of the boom.
 * The positive X axis corresponds to the boom, such that the position of the
 * device is at (boomLength, 0).
 * X and Y coordinates and boom length must be in the same units.
 * Angles are expressed in degrees.
 * Buffer angles are assumed to be 10 degrees by default.
 * 
 * @author gholling
 *
 */
public class DistortedFlow {
	protected double xc = 0.0;  // X coordinate of obstruction clockwise from the the boom
	protected double xcc = 0.0; // X coordinate of obstruction counterclockwise from the the boom
	protected double yc = 0.0;  // Y coordinate of obstruction clockwise from the boom
	protected double ycc = 0.0; // Y coordinate of obstruction counterclockwise from the boom
	protected double boomOrientation = 0.0; // Boom orientation, in degrees
	protected double boomLength = 0.0; // Boom length
	protected double bufferMin = 10.0;  // Buffer angle, in degrees
	protected double bufferMax = 10.0;  // Buffer angle, in degrees

	public DistortedFlow() {}
	public DistortedFlow (double xc, double yc, double xcc, double ycc, double boomOrientation, double boomLength) {
		this();
		this.xc=xc;
		this.xcc=xcc;
		this.yc=yc;
		this.ycc=ycc;
		this.boomOrientation = boomOrientation;
		this.boomLength = boomLength;
	}
	public DistortedFlow (double xc, double yc, double xcc, double ycc, double boomOrientation, double boomLength,
			double bufferMin, double bufferMax) {
		this (xc, yc, xcc, ycc, boomOrientation, boomLength);
		this.bufferMin = bufferMin;
		this.bufferMax = bufferMax;
	}
	public void setXc (double xc) { this.xc = xc; }
	public void setXcc (double xcc) { this.xcc = xcc; }
	public void setYc (double yc) { this.yc = yc; }
	public void setYcc (double ycc) { this.ycc = ycc; }
	public void setBoomOrientation (double boomOrientation) { this.boomOrientation = boomOrientation; }
	public void setBoomLength (double boomLength) { this.boomLength = boomLength; }
	public void setBufferMin (double bufferMin) { this.bufferMin = bufferMin; }
	public void setBufferMax (double bufferMax) { this.bufferMax = bufferMax; }
	public double getXc() { return xc; }
	public double getXcc() { return xcc; }
	public double getYc() { return yc; }
	public double getYcc() { return ycc; }
	public double getBoomOrientation() { return boomOrientation; }
	public double getBoomLength() { return boomLength; }
	public double getBufferMin() { return bufferMin; }
	public double getBufferMax() { return bufferMax; }
	
	/**
	 * Calculate the angle of distorted flow clockwise from the boom.
	 * @return - the distorted flow angle, in degrees.
	 */
	public double thresholdClockwise() {
		double result = Math.abs(Math.atan(yc / (boomLength + Math.abs(xc))));
		result *= (180.0 / Math.PI);
		return result;
	}
	/**
	 * Calculate the angle of distorted flow counterclockwise from the boom.
	 * @return - the distorted flow angle, in degrees.
	 */
	public double thresholdCounterClockwise() {
		double result = Math.abs(Math.atan(ycc / (boomLength + Math.abs(xcc))));
		return result * (180.0 / Math.PI);
	}
	/**
	 * Calculate the lower bound of the distorted wind field, including the buffer zones,
	 * corrected to true north.
	 * @return - The lower bound, in degrees.
	 */
	public double distortionMinThreshold() {
		double result = (boomOrientation - (thresholdClockwise() + bufferMin) + 180.0);
		result = result % 360.0;
		if (result < 0.0) result += 360.0;  // Correct for negative mod
		return result;
	}
	/**
	 * Calculate the upper bound of the distorted wind field, including buffer zones,
	 * corrected to true north.
	 * @return - the upper bound, in degrees.
	 */
	public double distortionMaxThreshold() {
		double result = (boomOrientation + (thresholdCounterClockwise() + bufferMax) + 180.0);
		result = result % 360.0;
		if (result < 0.0) result += 360.0;  // Correct for negative mod
		return result;
	}
}
