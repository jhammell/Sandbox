package org.neoninc.dpms.algorithms.qaqctests;

import org.neoninc.dpms.datastructures.DPMSMeasStreamUtilities;

/*
 * This algorithm is checking whether data in a timeseries change too much from its neighbor,
 * and return flags of same size. Flags can only have values of -1 (data is missing), 
 * 0 (change from both sides is within threshold) and 1 (either change is out of range).
 * A data need to be compared with data before and after: if itself is missing, then 
 * test flag is missing (-1); if both before and after are missing, then test is missing;
 * if either side has data, do the comparison using threshold, either side fails the test
 * the flag will be raised
 * 
 * Some pre-processing might need before this check to form the timeseries, such as
 * filling missing time stamps, picking the right one if multiple data are in the 
 * same time window, and handle special cases like NaN, infinite value, etc.
 */
public class PersistenceTest {
	
//	private static final int initialFlagValue = -1;
	private static final double missingValue = DPMSMeasStreamUtilities.HANDLE_THIS_SPECIAL_NUMBER;

	public static int [] runPersistenceTest(double[] vals, double stepThreshold) {
		int [] flags = new int [vals.length];
		for(int i=0; i<vals.length; i++) {
			flags[i] = -1;  //initialFlagValue;
		}
		
		for(int i=0; i<vals.length-1; i++) {
		}

		return flags;
	}
}
