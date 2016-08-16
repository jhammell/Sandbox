package org.neoninc.dpms.algorithms.processors;

import org.apache.log4j.Logger;
//import org.neoninc.dpms.algorithms.QAQCWrapper;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.algorithms.QAQCWindowInputMeasStreams;

public class DeltaQAQCProcessor {

	static private Logger log = Logger.getLogger(DeltaQAQCProcessor.class);

	// Input time series
	private QAQCWindowInputMeasStreams input;

	public DeltaQAQCProcessor(QAQCWindowInputMeasStreams input) {
		super();
		this.input = input;
	}

	public boolean runAlgorithm() {

			log.error("Invalid call to DeltaQAQCVisitor - algorithm is defunct");
			return false;

		/* 
		 * TODO: CODE DIRECTLY THERE RATHER THAN USING WRAPPERS... 
		// Raw data.
		double[] dataArray = inputData.getCalibratedValuesForValId(input.getMeanInputValId());
		if (dataArray == null) {
			log.error("Invalid input for DeltaQAQCVisitor - no algorithm will be run");
			return false;
		}
		if (input.getWindowSize() == null || input.getWindowStep() == null || input.getSingleValueThreshold() == null ||
				input.getDasTimeTolerance() == null ) {
			log.warn("Invalid input for Delta test - exiting");
			return false;
		}

		// Time-based input
		Double winSize = (double) (inputData.getFrequencyInMilli() * input.getWindowSize());
		Double winStep = (double) (inputData.getFrequencyInMilli() * input.getWindowStep());
		double tolerance = inputData.getFrequencyInMilli() + 2* input.getDasTimeTolerance();

		DeltaQAQC deltaTest = QAQCWrapper.runDeltaQAQC(dataArray,
				inputData.getOriginalTimeValues(), winSize, 0, winStep,
				input.getSingleValueThreshold(), tolerance);

		QAQCProcessorUtilities.storeQAQCFlags(inputData, deltaTest.getFlagValues(),
				input.getQaQcFlagValId(), log);
		return true;
		 */
	}


}
