package org.neoninc.dpms.algorithms.processors;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.algorithms.QAQCInputMeasStreams;

public class GapQAQCProcessor {

	static private Logger log = Logger.getLogger(GapQAQCProcessor.class);

	QAQCInputMeasStreams input;
	
	public GapQAQCProcessor(QAQCInputMeasStreams input) {
		this.input = input;
	}

	public boolean runAlgorithm() {

		log.error("Invalid call to GapQAQCVisitor - no algorithm to run");
		return false;
		
		/* 
		DPMSMeasStreamData inputData = input != null ? input.getInputDataStream() : null;
		if (inputData == null || inputData.getMSReadouts().size() < 1) {
		}

		if ( input.getSingleValueThreshold() == null ) {
			log.warn("Invalid input for Gap test - exiting");
			return false;
		}

		// TODO: CODE DIRECTLY THERE RATHER THAN USING WRAPPERS... 
		// Get raw data
		double[] dataArray = inputData.getCalibratedValuesForValId(input.getMeanInputValId());
		if (dataArray == null) {
			log.error("Invalid input for GapQAQCVisitor - no algorithm will be run");
			return false;
		}

		// Run test.
		StepQAQC test = QAQCWrapper.runGapQAQC(dataArray,
				inputData.getOriginalTimeValues(), input.getSingleValueThreshold());

		QAQCProcessorUtilities.storeQAQCFlags(inputData, test.getFlagValues(),
				input.getQaQcFlagValId(), log);
		return true;
				
		 */

	}


}
