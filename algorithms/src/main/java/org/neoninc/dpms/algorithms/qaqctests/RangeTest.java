package org.neoninc.dpms.algorithms.qaqctests;

/*
 * This algorithm is checking whether data in a timeseries is out of given range,
 * and return flags of same size. Flags can only have values of -1 (data is missing), 
 * 0 (data is within range) and 1 (data is out of range).
 * Some pre-processing might need before this check to form the timeseries, such as
 * filling missing time stamps, picking the right one if multiple data are in the 
 * same time window, and handle special cases like NaN, infinite value, etc.
 */
public class RangeTest {
	
	public static int[] runRangeTest(Double[] vals, double minVal, double maxVal) {
		int [] flags = new int[vals.length];
		for(int i=0; i<vals.length; i++) {
			flags[i] = checkRange(vals[i], minVal, maxVal);
		}
		return flags;
	}
	
	public static int checkRange(Double value, double minVal, double maxVal) {
		if(value == null)
			return -1;
		if(Double.isNaN(value) || Double.isInfinite(value)) {
			return 1;
		}
		if(value > maxVal || value < minVal) {
			return 1;
		} else {
			return 0;
		}
	}
}
