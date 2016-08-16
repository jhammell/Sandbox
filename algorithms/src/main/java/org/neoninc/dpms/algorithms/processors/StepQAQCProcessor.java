package org.neoninc.dpms.algorithms.processors;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.algorithms.QAQCInputMeasStreams;

public class StepQAQCProcessor  {

	static private Logger log = Logger.getLogger(StepQAQCProcessor.class);

	// Input time series
	private QAQCInputMeasStreams input;
	
	public StepQAQCProcessor(QAQCInputMeasStreams input) {
		this.input = input;
	}

	public boolean runAlgorithm() {
		
		DPMSMeasStreamData inputData = input != null ? input.getInputDataStream() : null;
		if (inputData == null || inputData.getMSReadouts().size() < 1) {
			log.error("Invalid input for StepQAQCVisitor - no algorithm will be run");
			return false;
		}

		if ( input.getSingleValueThreshold() == null) {
			log.warn("Invalid input for Step test - exiting");
			return false;
		}
		
		//working on pre-processed data to ensure value difference is on two neighbor points
		inputData.createPreprocessedMap();
		
		long valId = input.getMeanInputValId();
		long qaQcValId = input.getQaQcFlagValId();
		double threshold = input.getSingleValueThreshold();
		
		int stIdx = inputData.getStartIndexFromEffectiveStartDate(input.getEffectiveStartTime());
		int edIdx = inputData.getEndIndexFromEffectiveEndDate(input.getEffectiveEndTime());
		for (int i=stIdx; i<edIdx; i++) {
			DPMSMStreamReadout rdot = inputData.getPreprocessedReadoutByIndex(i);
			DPMSMStreamReadout rdotNext = inputData.getPreprocessedReadoutByIndex(i+1);
			
			if (rdot == null) {
				continue;
			}
			
			Double val = rdot.getValueForValueId(valId);
			
			if(rdotNext == null) {
				if(rdot.getValueForValueId(qaQcValId) == null)
					input.writeFlagValue(rdot, -1.);
				continue;
			}
			
			Double valNext = rdotNext.getValueForValueId(valId);
			if(val == null || valNext == null) {
				//only when no assignment from previous loop
				if(rdot.getValueForValueId(qaQcValId) == null)
					input.writeFlagValue(rdot, -1.);
				input.writeFlagValue(rdotNext, -1.);
			} else if (Math.abs(val-valNext) <= threshold) {
				//NaN will fail this condition and be raised high
				//if -1 or null from previous loop, assign 0; keep raised flag otherwise
				if(rdot.getValueForValueId(qaQcValId) == null || rdot.getValueForValueId(qaQcValId) == -1.)
					input.writeFlagValue(rdot, 0.);
				input.writeFlagValue(rdotNext, 0.);
			} else {
				//raise the flag when step is larger than threshold
				input.writeFlagValue(rdot, 1.);
				input.writeFlagValue(rdotNext, 1.);
			}
		}

		return true;
	}
	
	
	public boolean runAlgorithmV1() {

//		DPMSMeasStreamData inputData = input != null ? input.getInputDataStream() : null;
//		if (inputData == null || inputData.getMSReadouts().size() < 1) {
//			log.error("Invalid input for GapQAQCVisitor - no algorithm will be run");
//			return false;
//		}
//
//		if ( input.getSingleValueThreshold() == null || input.getDasTimeTolerance() == null ) {
//			log.warn("Invalid input for Step test - exiting");
//			return false;
//		}
//
//		/* 
//		 * TODO: CODE DIRECTLY THERE RATHER THAN USING WRAPPERS... 
//		 */
//		// Get raw data
//		double[] dataArray = inputData.getCalibratedValuesForValId(input.getMeanInputValId());
//		if (dataArray == null) {
//			log.error("Invalid input for StepQAQCVisitor - no algorithm will be run");
//			return false;
//		}
//
//		// Time-based input
//		double tolerance = inputData.getFrequencyInMilli() + 2* input.getDasTimeTolerance();
//
//		// CODE COULD BE DIRECTLY THERE RATHER THAN USING WRAPPERS?
//		StepQAQC test = QAQCWrapper.runStepQAQC(dataArray,
//				inputData.getOriginalTimeValues(), input.getSingleValueThreshold(), tolerance);
//
//		QAQCProcessorUtilities.storeQAQCFlags(inputData, test.getFlagValues(),
//				input.getQaQcFlagValId(), log);
		return true;
	}

}
