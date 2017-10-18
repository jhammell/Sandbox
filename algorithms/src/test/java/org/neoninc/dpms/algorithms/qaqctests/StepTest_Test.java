package org.neoninc.dpms.algorithms.qaqctests;

import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

public class StepTest_Test {
	private Double missingVal = null;

	@Test
	public void testStepTest() {
		double stepThreshold = 65.;
		Double [] vals = {20.1, 22.3, 24.8, 70.1, 98.1, 26.2, Double.NEGATIVE_INFINITY, 34.5, 11.7, Double.NaN, 20.};
		
		int[] flags = StepTest.runStepTest(vals, stepThreshold);
		System.out.println("step threshold: " + stepThreshold );
		for (int i=0; i<vals.length; i++) {
			System.out.println(vals[i] + "  " + flags[i]);
		}
		
		System.out.println();
		
		Double [] vals_2 = {22., 21., 20., missingVal, 135., missingVal, 139., 15., 17., 19., 22., 18.,
				-52., -60., -65., 22., 23., 17., missingVal, 18., 22., -50.};
		int[] flags_2 = StepTest.runStepTest(vals_2, stepThreshold);
		System.out.println("step threshold: " + stepThreshold );
		for (int i=0; i<vals_2.length; i++) {
			System.out.println(vals_2[i] + "  " + flags_2[i]);
		}
		assertEquals("flags[0]", 0, flags_2[0]);
		assertEquals("flags[4]", -1, flags_2[4]);
		assertEquals("flags[7]", 1, flags_2[7]);
		assertEquals("flags[13]", 0, flags_2[13]);
	}
}
