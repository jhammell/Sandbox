package org.neoninc.dpms.algorithms.wind2dprocessors;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.neoninc.dpms.datastructures.algorithms.CvalUncertComputation;

public class Wind2DDirectionUncertaintyFunctions {
	public static double sqrt3 = Math.sqrt(3.0);
	//TODO: CONVERT THE BELOW TO EXTERNAL CONSTANTS
	// Wind direction uncertainty constant, ATBD section 6.1.9
	public static double directionUncertainty = 0.0349;
	// Reference values for effective degrees of freedom, ATBD section 6.1.10
	public static double veffa = 100.0;
	public static double veffo = 100.0;
	
	/**
	 * Calculate the combined measurement uncertainty for an individual, valid L0 wind direction observation.
	 * NOTE: Theta is unused.  Uncertainty is a constant.
	 * ATBD section 6.1.9.
	 * @return - the combined measurement uncertainty.
	 */
	public static double combinedL0MeasurementUncertainty (double theta, double orientationUncertainty) {
		return Math.sqrt(directionUncertainty*directionUncertainty + orientationUncertainty*orientationUncertainty);
	}
	
	/**
	 * Calculate the effective degrees of freedom for an individual L0 wind direction measurement.
	 * The combined measurement uncertainty is also returned as a side effect.
	 * ATBD section 6.1.10.
	 * @param theta - The wind direction (radians).
	 * @return - Effective degrees of freedom
	 */
	public static CvalUncertComputation.Uncertainties effectiveL0DegreesOfFreedom (double theta, double orientationUncertainty) {
		double cmu = combinedL0MeasurementUncertainty (theta, orientationUncertainty);
		double effectiveDOF = Math.pow (cmu, 4) / (Math.pow(directionUncertainty, 4)/veffa + Math.pow(orientationUncertainty, 4)/veffo);
		
		CvalUncertComputation.Uncertainties result = new CvalUncertComputation.Uncertainties();
		result.combinedU = cmu;
		result.effectiveDOF = effectiveDOF;
		return result;
	}
	/**
	 * Calculate the sample variance of a set of L1 mean wind direction values.
	 * ATBD section 6.2.1.1
	 * @param thetas - The wind direction values.
	 * @return - The sample variance.
	 */
	public static double uNatSq (double[] thetas) {
		SynchronizedDescriptiveStatistics sdsStats = new SynchronizedDescriptiveStatistics();
		double uNatSq = 0.0;
		double avgTheta = 0.0;
		for (int i=0; i<thetas.length; ++i) {
			double tmp = thetas[i];
			avgTheta += tmp;
			sdsStats.addValue (tmp);
			uNatSq += (tmp*tmp);
		}
		double arithmeticMean = sdsStats.getMean();
		avgTheta = avgTheta / thetas.length;
		uNatSq = (uNatSq / thetas.length) - (arithmeticMean*arithmeticMean);
		return uNatSq;
	}
	/**
	 * Calculate the combined uncertainty of a set of L1 mean wind direction values.
	 * ATBD section 6.2.1.1.
	 * @param thetas - The wind direction values.
	 * @return - The combined uncertainty.
	 */
	public static double combinedL1Uncertainty (double[] thetas, double orientationUncertainty) {
		double averageCombinedSquareMeasurementUncertainty = 0.0;
		for (int i=0; i<thetas.length; ++i) {
			double tmp = combinedL0MeasurementUncertainty(thetas[i], orientationUncertainty);
			averageCombinedSquareMeasurementUncertainty += (tmp*tmp);
		}
		averageCombinedSquareMeasurementUncertainty /= thetas.length;
		double result = Math.sqrt(uNatSq(thetas) + averageCombinedSquareMeasurementUncertainty);
		return result;
	}
	/**
	 * Calculate the effective degrees of freedom for an array of (L1) wind direction values.
	 * The combined measurement uncertainty is also returned as a side effect.
	 * @param thetas - the wind direction values.
	 * @return - the effective degrees of freedom.
	 */
	public static CvalUncertComputation.Uncertainties effectiveL1DegreesOfFreedom (double[] thetas, double orientationUncertainty) {
		double sum = 0.0;
		double tmp = 0.0;
		int numPoints = thetas.length;
		for (int i=0; i<numPoints; ++i) {
			tmp = combinedL0MeasurementUncertainty (thetas[i], orientationUncertainty)/numPoints;
			tmp = Math.pow(tmp, 4) / effectiveL0DegreesOfFreedom(thetas[i], orientationUncertainty).effectiveDOF;
			sum += tmp;
		}

		double ucSbar = combinedL1Uncertainty (thetas, orientationUncertainty);
		double ucSbar4 = Math.pow(ucSbar, 4);
		tmp = uNatSq (thetas);
		double uNat4 = tmp*tmp;
		double veffsBar = ucSbar4 / ((uNat4/(numPoints-1)) + sum);
		
		CvalUncertComputation.Uncertainties result = new CvalUncertComputation.Uncertainties();
		result.combinedU = ucSbar;
		result.effectiveDOF = veffsBar;
		return result;
	}
}
