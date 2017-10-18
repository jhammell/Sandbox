package org.neoninc.dpms.algorithms.processors;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSQAQCValues;
import org.neoninc.dpms.datastructures.QualityMetricsReadoutHelper;
import org.neoninc.dpms.datastructures.algorithms.QMQFInputMeasStreams;
import org.neoninc.dpms.datastructures.algorithms.QMQFOutputMeasStreams;

/**
 * Processor class that generates the QM/QF flags
 * @author fpradeau
 *
 */
public class QMQFDataProcessor {

	static private Logger log = Logger.getLogger(QMQFDataProcessor.class);

	// Input time series and parameters
	private QMQFInputMeasStreams inputData;

	// Outputs
	private QMQFOutputMeasStreams outputData;

	// QA/QC flag value map
	private DPMSQAQCValues qaQcValueMap;

	public QMQFDataProcessor(QMQFInputMeasStreams inputData, QMQFOutputMeasStreams outputData,
			DPMSQAQCValues qaQcValueMap) {
		super();
		this.inputData = inputData;
		this.outputData = outputData;
		this.qaQcValueMap = qaQcValueMap;
	}

	public boolean runAlgorithm() {

		if (inputData == null || inputData.getInputData() == null ) {
			log.error("Invalid input - no algorithm will be run");
			return false;
		}

		// Cloning as we might be having a reference to an object and don't want
		// to update the original...
		ArrayList<Long> l0Flags = (ArrayList<Long>) inputData.getL0QaQcFlags().clone();
		l0Flags.add(qaQcValueMap.getAlphaL0ValId());
		l0Flags.add(qaQcValueMap.getBetaL0ValId());
		// Initialize our readout helper
		QualityMetricsReadoutHelper qmHelper = outputData.initializeReadoutHelper(l0Flags, qaQcValueMap);

		Long sensorRate = inputData.getInputData().getFrequencyInMilli();
		int numPtsPerWindow = (int) (inputData.getAverageTimeInSecs() / (sensorRate / 1000));
		Long intervalInMilli = inputData.getAverageTimeInSecs() * 1000;

		qmHelper.setNumPointsPerWindow(numPtsPerWindow);

		// Pre-process if not already done
		DPMSMeasStreamData input = inputData.getInputData();
		input.createPreprocessedMap();
		// Use start/end index for effective start/end dates.
		Integer startIndex = input.getStartIndexFromEffectiveStartDate(inputData.getEffectiveStartDate());
		Integer endIndex = input.getEndIndexFromEffectiveEndDate(inputData.getEffectiveEndDate());
		
		// Keep track of points within the current window
		Integer windowIndex = 0;
		// Make a safe copy, as we're using this object to stamp stuff over and
		// over.
		Date effStartDate = (Date) inputData.getEffectiveStartDate().clone();
		
		// Keep indexes for null values to be able to derive null/gap flags
		for ( Integer index = startIndex; index <= endIndex; index++ ) {
			DPMSMStreamReadout rdot = input.getPreprocessedReadoutByIndex(index);

			// Evaluate QFalpha and beta flags
			if (rdot != null && inputData.isDoComputeL0AlphaBetaFlags()) {
				evaluateQFAlphaAndBetaFlags(rdot);
			}
			qmHelper.addFlagValueFromReadout(index.longValue(), rdot);
			windowIndex++;
			// Check for 1min done.
			if ((windowIndex % numPtsPerWindow) == 0) {
				// Done with 1min - write readouts if data available.
				Date eTime = new Date();
				eTime.setTime(effStartDate.getTime() + intervalInMilli);
				outputData.writeL1QMReadouts(qmHelper, qaQcValueMap, effStartDate, eTime);
				qmHelper.cleanAllRunningData();
				effStartDate = eTime;
			}
		}

		return true;
	}

	/**Function evaluating QF Alpha and Beta flags from the individual QF flags
	 * that are assumed to be already on the readout. Only exception is null and
	 * gap.
	 * @param rdot
	 */
	private void evaluateQFAlphaAndBetaFlags(DPMSMStreamReadout rdot) {
		if (rdot == null) {
			return;
		}
		// Check values for valueIds that are to be used in the alpha and
		// beta test.
		Long qfAlphaFlagValueId = qaQcValueMap.getAlphaL0ValId();
		Long qfBetaFlagValueId = qaQcValueMap.getBetaL0ValId();
		Long l0NullFlagValueId = qaQcValueMap.getNullTestL0ValId();
		Long l0GapFlagValueId = qaQcValueMap.getGapTestL0ValId();

		// Already computed? Should be done...
		if (rdot.getValueForValueId(qfAlphaFlagValueId) != null) {
			return;
		}

		// Initialize alpha/beta to 0.
		if(inputData.getL0AlphaBetaQaQcFlags() != null) {
			rdot.setValueForValueId(qfAlphaFlagValueId, (double) 0, true);
			rdot.setValueForValueId(qfBetaFlagValueId, (double) 0, true);
			for (Long valId : inputData.getL0AlphaBetaQaQcFlags()) {
				// Special casing null/gap flags as they are not used on
				// existing readouts
				// (e.g. on non-null readouts), but only used at QM creation
				// time.
				boolean isNullFlag = l0NullFlagValueId != null
						&& valId.compareTo(l0NullFlagValueId) == 0;
				boolean isGapFlag = l0GapFlagValueId != null
						&& valId.compareTo(l0GapFlagValueId) == 0;
				if (rdot.getValueForValueId(valId) != null) {
					if (rdot.getValueForValueId(valId).compareTo((double) 1) == 0) {
						// Update or create if not there.
						rdot.setValueForValueId(qfAlphaFlagValueId, (double) 1,
								true);
					} else if (rdot.getValueForValueId(valId).compareTo(
							(double) -1) == 0) {
						rdot.setValueForValueId(qfBetaFlagValueId, (double) 1,
								true);
					}
				} else if (!isNullFlag && !isGapFlag) {
					// If the flag is not there, we assume it failed to be computed.
					// We need to special-case
					// the null/gap flags, because it never gets created (e.g. missing
					// readouts are nulls, others are not), so we don't want to
					// hit this particular clause because the null/gap flags
					// are not set..
					rdot.setValueForValueId(qfBetaFlagValueId, (double) 1, true);
				}
			}
		}
	}

}
