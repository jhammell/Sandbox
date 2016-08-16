package org.neoninc.dpms.algorithms.wind2dprocessors;

import java.util.Date;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.algorithms.L1AverageOutputMeasStreams;

/**
 * This class calculates wind direction statistics (mean and variance)
 * for 2D wind data streams.  The calculations are performed differently
 * than standard statistics.
 * @author GHOLLING
 *
 */
public class Wind2DDirectionL1AverageOutputMeasStreams extends
		L1AverageOutputMeasStreams {
	static private Logger log = Logger.getLogger(Wind2DDirectionL1AverageOutputMeasStreams.class);
	/**
	 * Write L1 readouts to the associated measurement streams.
	 * 
	 * Note that this is a replacement for the superclass method.
	 *
	 * @param stats - basic statistics data container object
	 * @param standardError - the standard error (ignored)
	 * @param expandedU - the expanded uncertainty (ignored)
	 */
	@Override
	public void writeL1Readouts(SynchronizedDescriptiveStatistics stats,
			Double standardError, Double expandedU, Date stTime, Date eTime) {
		if (stats == null) {
			log.debug ("stats object was null");
			return;
		}
		//
		// Extract the raw data, so we can use it for stats
		//
		double[] data = stats.getValues();
		if (data == null) {   // Should never happen according to apache commons doc
			log.debug ("data object was null");
			return;
		}
		//
		// Calculate the wind direction statistics
		//
		Wind2DAlgorithmicFunctions.Stats windStats = 
				Wind2DAlgorithmicFunctions.calculateWindDirectionStats(data);
		//
		// Write stats to L1 streams.
		//
		DPMSMeasStreamData l1MeasStream = getOutputDataStream();
		if (l1MeasStream != null) {
			DPMSMStreamReadout rdot = l1MeasStream.addReadoutValue(stTime,
					eTime, getMeanValId(), windStats.arithmeticMean);

			// Standard error
			rdot.setValueForValueId(getStandErrorValId(), standardError, true);

			// Expanded uncertainty
			rdot.setValueForValueId(getExpandUncertValId(), expandedU, true);

			// Number of points used
			rdot.setValueForValueId(getNumPtsValId(), (double) data.length, true);
		}

		// Min
		DPMSMeasStreamData l1MinMeasStream = getMinDataStream();
		if (l1MinMeasStream != null) {
			l1MinMeasStream.addReadoutValue(stTime, eTime, getMinValId(),
					stats.getMin());
		}
		// Max
		DPMSMeasStreamData l1MaxMeasStream = getMaxDataStream();
		if (l1MaxMeasStream != null) {
			l1MaxMeasStream.addReadoutValue(stTime, eTime, getMaxValId(),
					stats.getMax());
		}
		// Variance
		DPMSMeasStreamData l1VarMeasStream = getVarianceDataStream();
		if (l1VarMeasStream != null) {
			l1VarMeasStream.addReadoutValue(stTime, eTime, getVarianceValId(),
					windStats.sampleVariance);
		}
	}
}
