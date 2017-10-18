package org.neoninc.dpms.algorithms.qaqctests;

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
public class StepTest {
	
	public static int [] runStepTest(Double[] vals, double stepThreshold) {
		int [] flags = new int [vals.length];
		flags[0] = -1; //no previous value to compare with
		
		for(int i=1; i<vals.length; i++) {
			Double preValue = vals[i-1];
			Double value = vals[i];
			int [] twoFlags = checkStep(value, preValue, stepThreshold, flags[i-1]);
			flags[i-1] = twoFlags[0];
			flags[i] = twoFlags[1];
		}

		return flags;
	}
	
	public static int[] checkStep(Double value, Double preValue, double stepThreshold, int preFlag) {
		int[] twoFlags = {preFlag, -1};
		
		if( (value != null && (Double.isNaN(value) || Double.isInfinite(value))) ||
				(preValue != null && (Double.isNaN(preValue) || Double.isInfinite(preValue))) ){
			twoFlags[0] = 1;
			twoFlags[1] = 1;
			return twoFlags;
		}
		
		//if preValue is missing, its flag will be -1, and flag for value
		// is also -1 which might change next round;
		if(preValue != null) {
			//if value is missing, flag for preValue keeps the same from comparing
			//with its previous value and flag for value is -1
			if (value != null) {
				if(Math.abs(value-preValue) > stepThreshold) {
					twoFlags[0] = 1;
					twoFlags[1] = 1;
				} else {
					if(preFlag == -1)	twoFlags[0] = 0;
					twoFlags[1] = 0;
				}
			} 
		}
		
		return twoFlags;
	}
}
