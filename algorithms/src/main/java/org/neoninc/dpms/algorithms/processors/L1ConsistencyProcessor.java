package org.neoninc.dpms.algorithms.processors;

import java.util.Collections;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.algorithms.L1ConsistencyInputMeasStreams;

public class L1ConsistencyProcessor {

	static private Logger log = Logger.getLogger(L1ConsistencyProcessor.class);

	/* 
	 * The input data. Note that flags get added to that input data
	 * while running this algorithm.
	*/
	private L1ConsistencyInputMeasStreams inputDataStream;
	

	public L1ConsistencyProcessor(L1ConsistencyInputMeasStreams inputDataStream) {
		this.inputDataStream = inputDataStream;
	}
	
	public boolean runAlgorithm() {
		
		// Check input
		if (inputDataStream == null ) {
			log.debug("Invalid input to Consistency algorithm - exiting");
			return false;
		}
		
		if (inputDataStream.getConsistFactorVal() == null) {
			log.warn("Invalid consistency factor given - will not run consistency checks");
			return false;
		}
		// Consistency check will compare a given level measurement stream data
		// with the one above first, and only in case of failure will compare
		// with
		// the one below if available. Algorithm will start at highest level and
		// go down
		DPMSMeasStreamData msDataAbove = null;
		Set<Long> indexes = inputDataStream.getInputDataStreamsPerLevel().keySet();
		Long topLevel = Collections.max(indexes);
		Long botLevel = Collections.min(indexes);

		// Need to set the 'sensor frequency', start/end date on
		Long level = topLevel;
		while (level >= 1) {
			DPMSMeasStreamData msData = inputDataStream.getInputDataStream(level);
			if (msData == null) {
				level--;
				continue;
			}
			if (msDataAbove == null) {
				msDataAbove = msData;
				level--;
				continue;
			}
			
			testTwoLevels(msDataAbove, botLevel, level, msData);
			level--;
			msDataAbove = msData;
		}
		return true;
	}

	/**
	 * @param consistFactorVal
	 * @param dataValId
	 * @param consistCheckFlagValId
	 * @param msDataAbove
	 * @param botLevel
	 * @param level
	 * @param msData
	 * @param startIndex
	 */
	protected void testTwoLevels(DPMSMeasStreamData msDataAbove,
			Long botLevel, Long level, DPMSMeasStreamData msData) {
		
		// Check if we have a separate start date, which index to start with.
		Integer startIndex = msData.getStartIndexFromEffectiveStartDate(inputDataStream.getEffectiveStartTime());

		// Preprocessing to make sure we compare time stamps that are
		// comparable
		msDataAbove.createPreprocessedMap();
		msData.createPreprocessedMap();
		for (int iloop = startIndex; iloop < msData.getNumberOfPreprocessedPoints(); iloop++) {

			DPMSMStreamReadout rdot = msData
					.getPreprocessedReadoutByIndex(iloop);
			DPMSMStreamReadout rdotAbove = msDataAbove
					.getPreprocessedReadoutByIndex(iloop);

			compareReadouts(rdot, rdotAbove, level, botLevel);
			
		}
	}

	private void compareReadouts(DPMSMStreamReadout rdot,
			DPMSMStreamReadout rdotAbove, Long rdotLevel, Long botLevel) {
		Double consistFactorVal = inputDataStream.getConsistFactorVal();
		Long dataValId = inputDataStream.getMeanInputValId();
		Long consistCheckFlagValId = inputDataStream.getConsistCheckFlagValId();

		// Get values
		Double valAbove = rdotAbove != null ? rdotAbove
				.getValueForValueId(dataValId) : null;
		Double flagValAbove = rdotAbove != null ? rdotAbove
				.getValueForValueId(consistCheckFlagValId) : null;
		Double val = rdot != null ? rdot.getValueForValueId(dataValId)
				: null;
		Double diff = null;
		
		if (val != null && valAbove != null) {
			diff = Math.abs(valAbove - val);
			// Compare the difference with the combined uncertainty of
			// the original data.
			Double combUncert = rdot
					.getCombinedUncertaintyForValueId(dataValId);

			if (combUncert != null
					&& diff <= consistFactorVal * combUncert) {
				// Bottom passes.
				inputDataStream.writeConsistencyFlag(rdot, 0.);
			} else if (rdotLevel.compareTo(botLevel) == 0) {
				// Bottom fails and can't depend on something below...
				inputDataStream.writeConsistencyFlag(rdot, 1.);
			}
		}

		// Check if the above meas stream readout had a flag. If not,
		// evaluate it
		if (flagValAbove == null && valAbove != null) {
			if (diff == null) {
				// Couldn't be evaluated!
				inputDataStream.writeConsistencyFlag(rdotAbove, -1.);
			} else {
				// Compare the difference with the combined uncertainty
				// of the original data.
				Double combUncert = rdotAbove
						.getCombinedUncertaintyForValueId(dataValId);
				if (combUncert != null
						&& diff <= consistFactorVal * combUncert) {
					inputDataStream.writeConsistencyFlag(rdotAbove, 0.);
				} else {
					inputDataStream.writeConsistencyFlag(rdotAbove, 1.);
				}
			}
		}
		

		// If at bottom level, flag the bottom as -1 if there and no
		// readout for above
		if (valAbove == null && rdot != null) {
			inputDataStream.writeConsistencyFlag(rdot, -1.);
		}
		return;
	}
	
}
