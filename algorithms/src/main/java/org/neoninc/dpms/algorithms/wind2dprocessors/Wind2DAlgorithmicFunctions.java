package org.neoninc.dpms.algorithms.wind2dprocessors;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.log4j.Logger;

public class Wind2DAlgorithmicFunctions {
	private static Logger log = Logger.getLogger(Wind2DAlgorithmicFunctions.class);
	public static double twoPi = 2 * Math.PI;

	/**
	 * Calculate the horizontal wind speed for a single datum.
	 * ATBD section 4.2.1.
	 * @param u - the U component
	 * @param v - the V component
	 * @return  - the wind speed, in the same units as u & v.
	 */
	public static double horizontalWindSpeed (double u, double v) {
		return Math.sqrt (u*u + v*v);
	}

	/**
	 * Calculate a positive mod operation.  In Java, (negative % x) returns a negative value.
	 * If we want a result between 0 and 2pi, we need to add 2pi to the result of 
	 * a negative mod.
	 */
	public static double modPositive (double value, double divisor) {
		double result = (value % divisor);
		if (result < 0.0) result += divisor;
		return result;
	}
	/**
	 * Calculate the wind direction for a single datum.
	 * ATBD section 4.2.2.
	 * @param u - the u component
	 * @param v - the v component
	 * @return - the wind direction, in radians (always positive).
	 */
	public static double windDirection (double u, double v) {
		if ((u < 0) && (v == 0.0)) return twoPi;
		return modPositive (twoPi + Math.atan2(-v, -u), twoPi);
	}

	/**
	 * Calculate the wind direction for a single datum.
	 * ATBD section 4.2.2.
	 * @param u - the u component
	 * @param v - the v component
	 * @param offset - An offset in radians, added after the direction is calculated.
	 * @return - the wind direction, in radians (always positive).
	 */
	public static double windDirection (double u, double v, double offset) {
		double result = windDirection(u,v) - (twoPi-offset);
		return modPositive (result, twoPi);
	}
	
	/**
	 * Calculate the unit vector wind direction.
	 * ATBD section 4.2.2.
	 * @param xAvg - the average vector X component
	 * @param yAvg - the average vector Y component
	 * @return - the wind direction, in radians (always positive).
	 */
	public static double unitVectorWindDirection (double xAvg, double yAvg) {
		if ((xAvg > 0.0) && (yAvg == 0.0)) return twoPi;
		return (twoPi + Math.atan2(yAvg, xAvg)) % twoPi;
	}

	/**
	 * Calculate the minimum angular distance between observation and mean.
	 * ATBD section 4.2.2.
	 * @param theta    - the wind direction, in radians.
	 * @param thetaBar - the mean wind direction.
	 * @return         - the minimum angular distance.
	 */
	public static double minimumAngularDistance(double theta, double thetaBar)
	{
		double angle = Math.abs(Math.acos(Math.cos(theta - thetaBar)));
		if ((thetaBar <= theta) && (theta < (thetaBar + Math.PI))) {
			return angle;
		}
		else if (((thetaBar - Math.PI) < theta) && (theta < thetaBar)) {
			return -angle;
		} 
		// only above ranges covered in ATBD; nothing else is expected? 
		else {
			log.debug ("Unexpected angle encountered in calculation of minimum angular distance.  Theta=" + theta + ", thetaBar=" + thetaBar);
		}
		return angle;
	}

	/**
	 * Calculate the vertical average distance vector for a group of wind direction measurements.
	 * ATBD section 4.2.2.
	 * @param thetas - the wind direction measurements.
	 * @return
	 */
	public static double verticalAverageDistanceVector(double[] thetas) {
		SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
		for (int i=0; i<thetas.length; ++i) {
			stats.addValue (Math.sin(thetas[i]));
		}
		return stats.getMean();
	}

	/**
	 * Calculate the horizontal average distance vector for a group of wind direction measurements.
	 * ATBD section 4.2.2.
	 * @param thetas - the wind direction measurements.
	 * @return
	 */
	public static double horizontalAverageDistanceVector(double[] thetas) {
		SynchronizedDescriptiveStatistics stats = new SynchronizedDescriptiveStatistics();
		for (int i=0; i<thetas.length; ++i) {
			stats.addValue (Math.cos(thetas[i]));
		}
		return stats.getMean();
	}
	
	/**
	 * Convert radians to degrees.
	 * @param angle - the angle in radians.
	 * @return - the angle in degrees.
	 */
	public static double convertToDegrees(double angle) {
		return 360.0*angle/twoPi;
	}

	/**
	 * Convert degrees to radians.
	 * @param angle - the angle in degrees.
	 * @return - the angle in radians.
	 */
	public static double convertToRadians(double angle) {
		return twoPi*angle/360.0;
	}
	
	/**
	 * Calculate basic wind speed statistics.
	 * ATBD section 4.2.1.
	 * @param windSpeeds - the array of wind speed values (can be calculated with horizontalWindSpeeds()).
	 * @return - a Stats object containing basic statistics.  Units are the same as for windSpeeds.
	 */
	public static Stats calculateWindSpeedStats (double[] windSpeeds) {
		SynchronizedDescriptiveStatistics sdsStats = new SynchronizedDescriptiveStatistics();
		for (int i=0; i<windSpeeds.length; ++i) {
			sdsStats.addValue (windSpeeds[i]);
		}
		Stats stats = new Stats();
		stats.minimum = sdsStats.getMin();
		stats.maximum = sdsStats.getMax();
		stats.arithmeticMean = sdsStats.getMean();
		stats.sampleVariance = sdsStats.getVariance();
		return stats;
	}
	
	/**
	 * Calculate wind direction statistics.
	 * @param thetas - the array of wind direction values, in radians (can be calculated with windDirections()).
	 * @return - a Stats object containing basic statistics.  Statistics are returned in radians.
	 * ATBD section 4.2.2
	 */
	public static Stats calculateWindDirectionStats (double[] thetas) {
		Stats result = new Stats();

		SynchronizedDescriptiveStatistics sdsStats = new SynchronizedDescriptiveStatistics();
		for (int i=0; i<thetas.length; ++i) {
			sdsStats.addValue (thetas[i]);
		}
		result.minimum = sdsStats.getMin();
		result.maximum = sdsStats.getMax();
				
		// Pass 1: Calculate the components of the average distance vector.
		// Then calculate the unit-vector mean wind direction.
		double yAvg = verticalAverageDistanceVector (thetas);
		double xAvg = horizontalAverageDistanceVector (thetas);
		double thetaAvg = unitVectorWindDirection (xAvg, yAvg);
		
		// Pass 2: Calculate the minimum angular distance between observation & mean.
		SynchronizedDescriptiveStatistics adStats = new SynchronizedDescriptiveStatistics();
		double[] signedMinimumAngularDistances = new double[thetas.length];
		for (int i=0; i<thetas.length; ++i) {
			signedMinimumAngularDistances[i] = minimumAngularDistance (thetas[i], thetaAvg);
			adStats.addValue (signedMinimumAngularDistances[i]);
		}
		double averageSignedMinimumAngularDistance = adStats.getMean();
		
		// Finally, calculate the arithmetic mean & sample variance
		//JLA: US4019: mod 2pidouble arithmeticMean = thetaAvg + averageSignedMinimumAngularDistance;
		double arithmeticMean = modPositive(thetaAvg + averageSignedMinimumAngularDistance,twoPi);
		
		double sumOfSquareDeltas = 0.0;
		double avgMinAngularDistanceSquared = averageSignedMinimumAngularDistance * averageSignedMinimumAngularDistance;
		double tmp = 0.0;
		for (int i=0; i<thetas.length; ++i) {
			tmp = signedMinimumAngularDistances[i];
			sumOfSquareDeltas += (tmp*tmp) - avgMinAngularDistanceSquared; 
		}
		double sampleVariance = sumOfSquareDeltas / (double)thetas.length;
		
		result.arithmeticMean = arithmeticMean;
		result.sampleVariance = sampleVariance;
		return result;
	}
		
	/**
	 * This class is used to encapsulate return values from statistical calculations.
	 * @author GHOLLING
	 *
	 */
	public static class Stats {
		public double minimum;
		public double maximum;
		public double arithmeticMean;
		public double sampleVariance;
	}

	public static void main(String[] args) {
		System.out.println(horizontalWindSpeed(0.5, 0.375));
	}

}
