package org.neoninc.dpms.algorithms.processors;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.algorithms.PersistenceInputMeasStreams;

public class PersistenceQAQCProcessor {
	static private Logger log = Logger.getLogger(PersistenceQAQCProcessor.class);

	// Input time series
	private PersistenceInputMeasStreams input;
	private HashMap<Integer, Double> flagMap = new HashMap<>();
	private static final double MAX_WINSIZE = 24. * 60. * 60. ;  //loop not longer than 24 hours in seconds
	
	static public class PointInformation {
		public Double minVal = null;
		public Double maxVal = null;
		public int minIdx = 0;
		public int maxIdx = 0;
		public int stPoint = 0; //start point for this round of test run
		public int loopIdx = 0; //keep track of loop index
	}
	
	public PersistenceQAQCProcessor(PersistenceInputMeasStreams input) {
		this.input = input;
	}

	public boolean runAlgorithm() {
		DPMSMeasStreamData inputData = input != null ? input.getInputDataStream() : null;
		if (inputData == null || inputData.getMSReadouts().size() < 1) {
			log.error("Invalid input for PersistenceQAQCVisitor - no algorithm will be run");
			return false;
		}

		if (input.getSingleValueThreshold() == null || input.getIntervalThreshold() == null) {
			log.warn("No value or interval threshold to evaluate persistence test. Exiting.");
			return false;
		}
		
		//working on pre-processed data to ensure value difference is on two neighbor points
		inputData.createPreprocessedMap();
		
		Long freqInMilli = inputData.getFrequencyInMilli();
		int stIdx = inputData.getStartIndexFromEffectiveStartDate(input.getEffectiveStartTime());
		int edIdx = inputData.getEndIndexFromEffectiveEndDate(input.getEffectiveEndTime());
		long valId = input.getMeanInputValId();
//		long qaQcValId = input.getQaQcFlagValId();
		double threshold = input.getSingleValueThreshold();
		//in thresholds.csv, interval threshold is in seconds, here transfer it to points
		double interval = input.getIntervalThreshold() * 1000/freqInMilli;   
		
		PointInformation testRunPoints = new PointInformation();		
		testRunPoints.stPoint = stIdx;
		for (int i=stIdx; i<edIdx; i++) {
			DPMSMStreamReadout rdot = inputData.getPreprocessedReadoutByIndex(i);
			if(rdot == null) {
				continue;
			}
			Double val = rdot.getValueForValueId(valId);
			if(val == null || val.isNaN() || val.isInfinite()) {
				//if flag it here: flagMap.put(i, 1.), might be replaced later @ #150, ignore this
				//range test should catch this
				continue;
			}
			
			setMinMaxVal(i, val, testRunPoints);
			if(testRunPoints.minVal == testRunPoints.maxVal && i == edIdx-1) {
				//last point is the only point to compare, check it with the data before
				Double beforeVal = inputData.getPreprocessedReadoutByIndex(i-1).getValueForValueId(valId);
				if(Math.abs(val-beforeVal) > threshold) {
					flagMap.put(i, 0.);
				} else {
					flagMap.put(i, -1.);
				}
			}
			if(testRunPoints.minVal == testRunPoints.maxVal || testRunPoints.minVal == null || testRunPoints.maxVal == null) {
				continue;
			}
						
			//New persistence test run might start before where the for loop is now
			//the loopIdx will track the new start point of next test run and assign back to i
			checkPersistence(testRunPoints, threshold, interval, i, edIdx, freqInMilli);
			if (testRunPoints.loopIdx != i) {
				i = testRunPoints.loopIdx;
			}
		}
		
		storePersistenceFlags(flagMap);

		return true;
	}
	
	
	public void storePersistenceFlags(HashMap<Integer, Double> flagMap) {
		int numPts = input.getInputDataStream().getNumberOfPreprocessedPoints();
		log.trace("Number of preprocessed points: " + numPts);
		for (int iloop = 0; iloop < numPts; iloop++) {
			DPMSMStreamReadout rdot = input.getInputDataStream()
					.getPreprocessedReadoutByIndex(iloop);
			if (rdot == null) {
				continue;
			}
			Double persistFlag = flagMap.get(iloop);
			if (persistFlag != null) {
				input.writeFlagValue(rdot, persistFlag);
			} else {
				//never be hit?
				input.writeFlagValue(rdot, -1.);
			}
		}
		
	}
	
	
	public void checkPersistence(PointInformation testRunPoints,
			double threshold, double interval, int currIdx,
			int edIdx, long freqInMilli) {
		double persistFlag;
		//checkPoint: first appearance of min and max
		int checkPoint = Math.min(testRunPoints.minIdx, testRunPoints.maxIdx);
		//length: from start point to current point
		int length = currIdx - testRunPoints.stPoint + 1;
		if((testRunPoints.maxVal-testRunPoints.minVal) <= threshold) {
			//when difference between peaks is less than stated threshold
			if (length >= MAX_WINSIZE/(freqInMilli/1000.) ) {
				//if length longer than 24hours
				//raise flags for all data, including current point. start next run from next point
				persistFlag = 1.;
				assignFlags(testRunPoints.stPoint, currIdx+1, flagMap, persistFlag);
				testRunPoints.stPoint = currIdx + 1;
				testRunPoints.minVal = null;
				testRunPoints.maxVal = null;
			}
			if (currIdx == edIdx-1) {
				//if last point, the loop shall end afterwards
				if(length > interval) {
					//if length longer than interval,raise flags for all
					persistFlag = 1.;
				} else {
					//if length shorter than interval, put -1
					persistFlag = -1.;
				}
				assignFlags(testRunPoints.stPoint, currIdx+1, flagMap, persistFlag);
			}
			//otherwise will continue the loop
			testRunPoints.loopIdx = currIdx;
			return;
		} else {
			//when difference between peaks is more than stated threshold
			if (length > interval) {
				//if length longer than stated interval
				//raise flags from stPoint to i-1, start next run from i
				persistFlag = 1.;
				assignFlags(testRunPoints.stPoint, currIdx, flagMap, persistFlag);
				testRunPoints.stPoint = currIdx;
			} else {
				//if length shorter than stated interval, start next run from checkPoint+1
				persistFlag = 0.;
				assignFlags(testRunPoints.stPoint, checkPoint+1, flagMap, persistFlag);
				testRunPoints.stPoint = checkPoint + 1;
			}
			//a new test run will start from stPoint, for loop has to go backwards
			testRunPoints.loopIdx = testRunPoints.stPoint - 1;
			testRunPoints.minVal = null;
			testRunPoints.maxVal = null;
		}
	}
	
	
	public void assignFlags(int stPoint, int edPoint, HashMap<Integer,Double> flagMap, double persistFlag) {
		for(int iloop=stPoint; iloop<edPoint; iloop++) {
			flagMap.put(iloop, persistFlag);
		}
		return;
	}
	

	public void setMinMaxVal(int currIdx, Double val, PointInformation minMaxPt) {
		if(minMaxPt.minVal == null || minMaxPt.maxVal == null) {
			minMaxPt.minVal = val;
			minMaxPt.maxVal = val;
			minMaxPt.minIdx = currIdx;
			minMaxPt.maxIdx = currIdx;
			return;
		}

		if(val < minMaxPt.minVal) {
			minMaxPt.minVal = val;
			minMaxPt.minIdx = currIdx;
		}		
		if(val > minMaxPt.maxVal) {
			minMaxPt.maxVal = val;
			minMaxPt.maxIdx = currIdx;
		}
		return;
	}

}
