package org.neoninc.dpms.algorithms.qaqctests;

//import org.neoninc.dpms.datastructures.DPMSMeasStreamUtilities;
import org.testng.annotations.Test;

public class RangeTest_Test {
	
//	private double missingVal = DPMSMeasStreamUtilities.HANDLE_THIS_SPECIAL_NUMBER; // -9999.
	private Double missingVal = null;

	@Test
	public void testRangeTest() {
		double minVal = 20.0;
		double maxVal = 40.0;
		Double [] vals = {Double.NaN, 20.1, 22.3, 24.8, 70.1, 26.2, 30.9, 34.5, 11.7, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
		
		int[] flags = RangeTest.runRangeTest(vals, minVal, maxVal);
		System.out.println("Range min: " + minVal + ";  Range max: " + maxVal);
		for (int i=0; i<vals.length; i++) {
			System.out.println(vals[i] + "  " + flags[i]);
		}
		
		System.out.println();
		
		Double [] vals_2 = {missingVal, missingVal, 24.8, 70.1, 26.2, 30.9, 34.5, 11.7, 20.0};
		int[] flags_2 = RangeTest.runRangeTest(vals_2, minVal, maxVal);
		System.out.println("Range min: " + minVal + ";  Range max: " + maxVal);
		for (int i=0; i<vals_2.length; i++) {
			System.out.println(vals_2[i] + "  " + flags_2[i]);
		}
	}
}
