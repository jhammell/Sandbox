package org.neoninc.dpms.algorithms.wind2dprocessors;


import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.testng.annotations.*;
import org.testng.Assert;

public class Wind2DProcessorTest {

	@Test
	public void computeHorizontalWindSpeedTest() {
		// check regular values (doubles)
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.horizontalWindSpeed(0.5, 0.375),
				0.625, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.horizontalWindSpeed(1.0, 0.375),
				1.0680004681646913, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.horizontalWindSpeed(3.0, 4.0), 5.0, 0.001);

		// check with a zero for U, V, and both
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.horizontalWindSpeed(0.0, 0.25), 0.25, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.horizontalWindSpeed(0.25, 0.0), 0.25, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.horizontalWindSpeed(0.0, 0.0), 0.0, 0.001);
	}

	@Test
	public void computeWindDirectionTest() {
		// check for U < 0 and V = 0 
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(-0.25, 0.0),
				2 * Math.PI, 0.001);

		// other cases:
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(0.0, 0.0), Math.PI, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(0.25, 0.0), Math.PI, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(0.0, 0.25),
				4.71238898038469, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(0.0, -0.25),
				1.5707963267948966, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(-0.25, -0.25),
				0.7853981633974483, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(0.25, 0.25),
				3.9269908169872414, 0.001);
		//AIS 2D wind, sensor rotates for V component. Before and after rotation, sum of wind directions should be 2*offset
		//if offset is 0, then the sum should be 2*PI
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(-0.437,  -0.9, 2.0354), 3.15476, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(-0.437,  0.9, 2.0354), 0.916629, 0.001);
		AssertJUnit.assertEquals((Wind2DAlgorithmicFunctions.windDirection(-0.437,  0.9, 2.0354)
				+ Wind2DAlgorithmicFunctions.windDirection(-0.437,  -0.9, 2.0354))%(2*Math.PI), 2*2.0354, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(-0.25,  -0.25, Math.PI), 3.92699, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(-0.25,  -0.25, Math.PI/2.0), 2.3561, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.windDirection(-0.25,  -0.25, Math.PI/4.0), 1.5707, 0.001);
	}
	
	// TODO: @Test verticalAverageDistanceVectorTest
	// TODO: @Test horizontalAverageDistanceVectorTest
	
	@Test
	public void computeUnitVectorMeanWindDirectionTest() {
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.unitVectorWindDirection(0, 0), 0.0, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.unitVectorWindDirection(-0.25, 0), Math.PI, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.unitVectorWindDirection(0.25, 0), Math.PI*2, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.unitVectorWindDirection(0, 0.25), 1.5708, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.unitVectorWindDirection(0, -0.25), 4.7124, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.unitVectorWindDirection(-0.25, -0.25), 3.9269908169872414, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.unitVectorWindDirection(0.25, 0.25), 0.7853981633974483, 0.001);
	}
		
	@Test
	public void computeMinimumAngularDistanceTest() throws Exception {
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.minimumAngularDistance(0.56, 0.56), 0.0, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.minimumAngularDistance(0.35, 0.56),-0.20999999999999988, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.minimumAngularDistance(0.56, 0.35), 0.20999999999999988, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.minimumAngularDistance(1.56, 0.56), 1.0, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.minimumAngularDistance(0.56, 1.56), -1.0, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.minimumAngularDistance(0, 0), 0.0, 0.001);
	}
	
	@Test
	public void convertToDegreesTest() {
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.convertToDegrees(0.0), 0.0, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.convertToDegrees(1.0), 57.29577951308232, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.convertToDegrees(Math.PI), 180.0, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.convertToDegrees(0.5), 28.64788975654116, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.convertToDegrees(2*Math.PI), 360.0, 0.001);
		AssertJUnit.assertEquals(Wind2DAlgorithmicFunctions.convertToDegrees(Wind2DAlgorithmicFunctions.convertToRadians(90.0)), 90.0, 0.001);
	}
	
	@Test
	public void distortedFlowTest() {
		// Harvard Forest & Sterling numbers
		DistortedFlow distortedFlow = new DistortedFlow(0.0, -2.35, 0.0, 2.96, 270.0, 2.96);
		AssertJUnit.assertEquals(distortedFlow.getXc(), 0.0, 0.001);
		AssertJUnit.assertEquals(distortedFlow.getYc(), -2.35, 0.001);
		AssertJUnit.assertEquals(distortedFlow.getXcc(), 0.0, 0.001);
		AssertJUnit.assertEquals(distortedFlow.getYcc(), 2.96, 0.001);
		AssertJUnit.assertEquals(distortedFlow.getBoomOrientation(), 270.0, 0.001);
		AssertJUnit.assertEquals(distortedFlow.getBoomLength(), 2.96, 0.001);
		double maxThreshold = distortedFlow.distortionMaxThreshold();
		double minThreshold = distortedFlow.distortionMinThreshold();
		AssertJUnit.assertEquals (minThreshold, 41.5533, 0.001);
		AssertJUnit.assertEquals(maxThreshold, 145.000, 0.001);
		// Guanaica Forest numbers
		DistortedFlow distortedFlow2 = new DistortedFlow(-2.35, -2.96, 0.0, 2.35, 90.0, 2.96);
		maxThreshold = distortedFlow2.distortionMaxThreshold();
		minThreshold = distortedFlow2.distortionMinThreshold();
		AssertJUnit.assertEquals (minThreshold, 230.863, 0.001);
		AssertJUnit.assertEquals(maxThreshold, 318.447, 0.001);
		// Bartlett Forest numbers
		DistortedFlow distortedFlow3 = new DistortedFlow(-2.35, 0.0, 0.0, 2.35, 180.0, 2.96);
		maxThreshold = distortedFlow3.distortionMaxThreshold();
		minThreshold = distortedFlow3.distortionMinThreshold();
		AssertJUnit.assertEquals (minThreshold, 350.0, 0.001);
		AssertJUnit.assertEquals(maxThreshold, 48.446, 0.001);
	}
}
