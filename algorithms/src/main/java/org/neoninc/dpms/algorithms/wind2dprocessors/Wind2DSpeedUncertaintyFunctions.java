package org.neoninc.dpms.algorithms.wind2dprocessors;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.neoninc.dpms.datastructures.algorithms.CvalUncertComputation;

public class Wind2DSpeedUncertaintyFunctions {
	public static double sqrt2 = Math.sqrt(2.0);
	public static double sqrt3 = Math.sqrt(3.0);
	// Wind speed vs. accuracy table, ATBD section 6.1.2
	public static double[] windSpeedValues =     {0.01, 5.0, 12.0, 32.0, 65.0, 70.0};
	public static double[] windSpeedAccuracies = { 1.0, 1.0,  2.0,  3.0,  4.0,  4.0};
	public static double[][] windUncertaintyConstants =
		{{0.01, 0.0}, {0.0271, 0.0857}, {0.036, 0.1920}, {0.0497, 0.6303}, {0.0400, 0.0}};
	//TODO: CONVERT THE BELOW TO EXTERNAL CONSTANTS
	// Reference values for effective degrees of freedom, ATBD section 6.1.10
	public static double veffv = 100.0;
	public static double veffu = 100.0;
	public static double veffr = 100.0;
	public static double veffa = 100.0;
	// Reference values for expanded uncertainty, ATBD section 6.2.1.2
	// This value is obtained from a table.  Does this table already exist somewhere?
	public static double k95 = 0.0;  //TODO: CORRECT THIS (AD[11], table 5)
	
	/**
	 * Calculate L0 wind speed accuracy.  ATBD section 6.1.2.
	 * @param windSpeed - The wind speed whose accuracy is to be determined.
	 * @return - the associated accuracy value, as a percentage.
	 */
	public static double windSpeedAccuracy (double windSpeed) {
		if (windSpeed < windSpeedValues[0]) {
			return windSpeed * windSpeedAccuracies[0];
		} else {
			for (int i=0; i<windSpeedValues.length-1; ++i) {
				if ((windSpeed >= windSpeedValues[i]) &&
					(windSpeed < windSpeedValues[i+1])) {
					return windUncertaintyConstants[i][0] * windSpeed - windUncertaintyConstants[i][1];
				}
			}
		}
		return windSpeedAccuracies[windSpeedAccuracies.length-1];
	}
	
	/**
	 * Calculate L0 velocity component measurement uncertainty.  ATBD section 6.1.2.
	 * @param u - the U component
	 * @param v - the V component
	 * @return  - the uncertainty values for the U and V components.
	 */
	public static VelocityComponentValues windSpeedUncertainty (double u, double v) {
		VelocityComponentValues result = new VelocityComponentValues();
		if ((u==0.0) && (v==0.0)) {
			result.u = (1.0/sqrt2) * u;
			result.v = (1.0/sqrt2) * v;
		} else {
			double windVelocity = Wind2DAlgorithmicFunctions.horizontalWindSpeed (u, v);
			double uncertainty = windSpeedAccuracy (windVelocity);
			result.u = Math.sqrt(u*u/(u*u+v*v)) * uncertainty;
			result.v = Math.sqrt(v*v/(u*u+v*v)) * uncertainty;			
		}
		return result;
	}
	
	/**
	 * Calculate the combined measurement uncertainty for L0 wind speed observations. ATBD section 6.1.9.
	 * @param u - the U component.
	 * @param v - the V component.
	 * @return - the combined measurement uncertainty of the vector components of
	 * an individual wind speed observation.
	 */
	public static VelocityComponentValues combinedL0MeasurementUncertainty (double u, double v, double resolutionUncertainty) {
		VelocityComponentValues result = new VelocityComponentValues();
		VelocityComponentValues vu = windSpeedUncertainty (u, v);
		result.u = Math.sqrt(vu.u*vu.u + resolutionUncertainty*resolutionUncertainty);
		result.v = Math.sqrt(vu.v*vu.v + resolutionUncertainty*resolutionUncertainty);
		return result;
	}
	
	/**
	 * Calculate the combined measurement uncertainty for L0 wind speed observations. ATBD section 6.1.9.
	 * @param windSpeed - the horizontal wind speed.
	 * @return - the combined measurement uncertainty of an individual wind speed observation.
	 */
	public static double combinedL0MeasurementUncertainty (double windSpeed, double resolutionUncertainty) {
		double wsu = windSpeedAccuracy (windSpeed);
		return Math.sqrt(wsu*wsu + resolutionUncertainty*resolutionUncertainty);
	}
		
	/**
	 * Calculate the effective degrees of freedom for a 1 Hz L0 vector speed component measurement.
	 * The combined measurement uncertainty is also returned as a side effect.
	 * ATBD section 6.1.10.
	 * @param u - the U component.
	 * @param v - the V component.
	 * @return - Effective degrees of freedom, as U and V components.
	 */
	public static VelocityComponentValues effectiveL0DegreesOfFreedom (double u, double v, double resolutionUncertainty) {
		VelocityComponentValues result = new VelocityComponentValues();
		VelocityComponentValues vcu = windSpeedUncertainty (u, v);
		VelocityComponentValues cvmu = combinedL0MeasurementUncertainty (u, v, resolutionUncertainty);
		result.u = Math.pow (cvmu.u, 4) / (Math.pow(vcu.u, 4)/veffu + Math.pow(resolutionUncertainty, 4)/veffr);
		result.v = Math.pow (cvmu.v, 4) / (Math.pow(vcu.v, 4)/veffv + Math.pow(resolutionUncertainty, 4)/veffr);
		result.combinedUncertaintyU = cvmu.u;
		result.combinedUncertaintyV = cvmu.v;
		return result;
	}

	/**
	 * Calculate the effective degrees of freedom for an individual L0 wind speed measurement.
	 * The combined measurement uncertainty is also returned as a side effect.
	 * ATBD section 6.1.10.
	 * @param windSpeed - the wind speed.
	 * @return - Effective degrees of freedom
	 */
	public static CvalUncertComputation.Uncertainties effectiveL0DegreesOfFreedom (double windSpeed, double resolutionUncertainty) {
		double vcu = windSpeedAccuracy (windSpeed);
		double cvmu = combinedL0MeasurementUncertainty (windSpeed, resolutionUncertainty);
		double effectiveDOF = Math.pow (cvmu, 4) / (Math.pow(vcu, 4)/veffa + Math.pow(resolutionUncertainty, 4)/veffr);

		CvalUncertComputation.Uncertainties result = new CvalUncertComputation.Uncertainties();
		result.combinedU = cvmu;
		result.effectiveDOF = effectiveDOF;
		return result;
	}

	/**
	 * Calculate the sample variance of a set of L1 mean wind speed values.
	 * ATBD section 6.2.1.1
	 * @param windSpeeds - the wind speed values.
	 * @return - The sample variance.
	 */
	public static double uNatSq (double[] windSpeeds) {
		SynchronizedDescriptiveStatistics sdsStats = new SynchronizedDescriptiveStatistics();
		double uNatSq = 0.0;
		for (int i=0; i<windSpeeds.length; ++i) {
			sdsStats.addValue (windSpeeds[i]);
			double tmp = windSpeeds[i];
			uNatSq += (tmp*tmp);
		}
		double arithmeticMean = sdsStats.getMean();
		uNatSq = (uNatSq / windSpeeds.length) - arithmeticMean*arithmeticMean;
		return uNatSq;
	}
	/**
	 * Calculate the combined uncertainty of a set of L1 mean wind speed values.
	 * ATBD section 6.2.1.1.
	 * @param windSpeeds - the wind speed values.
	 * @return - The combined uncertainty.
	 */
	public static double combinedL1Uncertainty (double[] windSpeeds, double resolutionUncertainty) {
		double averageCombinedSquareMeasurementUncertainty = 0.0;
		for (int i=0; i<windSpeeds.length; ++i) {
			double tmp = combinedL0MeasurementUncertainty(windSpeeds[i], resolutionUncertainty);
			averageCombinedSquareMeasurementUncertainty += (tmp*tmp);
		}
		averageCombinedSquareMeasurementUncertainty /= windSpeeds.length;
		double result = Math.sqrt(uNatSq(windSpeeds) + averageCombinedSquareMeasurementUncertainty);
		return result;
	}
	/**
	 * Calculate the effective degrees of freedom for an array of (L1) wind speed values.
	 * The combined measurement uncertainty is also returned as a side effect.
	 * @param windSpeeds - the wind speed values.
	 * @return - the effective degrees of freedom.
	 */
	public static CvalUncertComputation.Uncertainties effectiveL1DegreesOfFreedom (double[] windSpeeds, double resolutionUncertainty) {
		double sumQuads = 0.0;
		double tmp = 0.0;
		int numPoints = windSpeeds.length;
		for (int i=0; i<numPoints; ++i) {
			tmp = combinedL0MeasurementUncertainty(windSpeeds[i], resolutionUncertainty) / numPoints;
			double effDofScalar = effectiveL0DegreesOfFreedom(windSpeeds[i], resolutionUncertainty).effectiveDOF;
			tmp = Math.pow(tmp, 4) / effDofScalar;
			sumQuads += tmp;
		}

		double ucSbar = combinedL1Uncertainty (windSpeeds, resolutionUncertainty);
		double ucSbar4 = Math.pow(ucSbar, 4);
		tmp = uNatSq (windSpeeds);
		double uNat4 = tmp*tmp;
		double veffsBar = ucSbar4 / ((uNat4/(numPoints-1)) + sumQuads);
		
		CvalUncertComputation.Uncertainties result = new CvalUncertComputation.Uncertainties();
		result.combinedU = ucSbar;
		result.effectiveDOF = veffsBar;
		return result;
	}
	/**
	 * Calculate the expanded uncertainty of a set of L1 mean wind speed values.
	 * ATBD section 6.2.1.1. 
	 * @param windSpeeds - the wind speed values.
	 * @return - the expanded uncertainty.
	 */
	public static double expandedL1Uncertainty (double[] windSpeeds, double resolutionUncertainty) {
		double ucSbar = combinedL1Uncertainty (windSpeeds, resolutionUncertainty);
		double result = k95 * ucSbar;
		return result;
	}
	
	/**
	 * This class is used to hold U and V velocity components for methods that return both.
	 * @author GHOLLING
	 */
	public static class VelocityComponentValues {
		public double u;
		public double v;
		public double combinedUncertaintyU;
		public double combinedUncertaintyV;
	}
}
