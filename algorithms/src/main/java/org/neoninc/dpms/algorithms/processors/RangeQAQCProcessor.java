package org.neoninc.dpms.algorithms.processors;

import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.algorithms.QAQCInputMeasStreams;

public class RangeQAQCProcessor {

	static private Logger log = Logger.getLogger(RangeQAQCProcessor.class);

	QAQCInputMeasStreams input;

	public RangeQAQCProcessor(QAQCInputMeasStreams input) {
		super();
		this.input = input;
	}

	public boolean runAlgorithm() {

		DPMSMeasStreamData inputData = input != null ? input
				.getInputDataStream() : null;
		if (inputData == null || inputData.getMSReadouts().size() < 1) {
			log.error("Invalid input for RangeQAQCVisitor - no algorithm will be run");
			return false;
		}

		//change to exist when both min and max are null
		if (input.getMinValueThreshold() == null
				&& input.getMaxValueThreshold() == null) {
			log.warn("Both Min/Max range threshold value for range test not set. Exiting.");
			return false;
		}

		// Use start/end index for effective start/end dates.
		for (DPMSMStreamReadout rdot : inputData.getMSReadouts()) {
			if ( rdot == null ) {
				continue;
			}
			Double val = rdot.getValueForValueId(input.getMeanInputValId());
			compareToThreshold(rdot, val);
		}
		return true;
	}

	public void compareToThreshold(DPMSMStreamReadout rdot, Double val) {
		if (val != null) {
			if (val.isNaN() || val.isInfinite()) {
				input.writeFlagValue(rdot, 1.);
			} else if (input.getMinValueThreshold() != null && val < input.getMinValueThreshold()) {
				input.writeFlagValue(rdot, 1.);
			} else if (input.getMaxValueThreshold() != null && val > input.getMaxValueThreshold()) {
				input.writeFlagValue(rdot, 1.);
			} else {
				// Passed...
				input.writeFlagValue(rdot, 0.);
			}
		}
		
	}
	
}
