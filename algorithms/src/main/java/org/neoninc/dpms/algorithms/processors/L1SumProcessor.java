package org.neoninc.dpms.algorithms.processors;

import java.util.Date;
import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.algorithms.CvalUncertComputation;
import org.neoninc.dpms.datastructures.algorithms.L1AverageInputMeasStreams;
import org.neoninc.dpms.datastructures.algorithms.L1SumOutputMeasStreams;


public class L1SumProcessor {

	static private Logger log = Logger.getLogger(L1SumProcessor.class);

	/*
	 * The input data stream. It is assumed that it has been: 1) Calibrated 2)
	 * L0 uncertainties are computed and stored along with the readout
	 */
	private L1AverageInputMeasStreams inputDataStream;

	/*
	 * All of the outputs. Note that some might be null, if client doesn't need
	 * derived data from this algorithm.
	 */
	private L1SumOutputMeasStreams outputDataStream;

	/*
	 * Average time period (in seconds)
	 */
	private Long sumTimeInSecs;

	public L1SumProcessor(L1AverageInputMeasStreams inputDataStream,
			L1SumOutputMeasStreams outputDataStreams,
			Long sumTimeInSeconds) {
		this.inputDataStream = inputDataStream;
		this.outputDataStream = outputDataStreams;
		this.sumTimeInSecs = sumTimeInSeconds;
	}

	/**
	 * sum the values within the time period, compute 
	 * 
	 * @return
	 */
	public boolean runAlgorithm() {
		
		// Check input
		if (inputDataStream == null
				|| inputDataStream.getInputDataStream() == null) {
			log.debug("Invalid input to runSums - exiting");
			return false;
		}
		DPMSMeasStreamData inputMs = inputDataStream.getInputDataStream();
		if (inputMs.getFrequencyInMilli() == null
				|| inputMs.getStartDate() == null
				|| inputMs.getEndDate() == null) {
			log.debug("Measurement stream " + inputMs.getMeasStrmName()
					+ " is not correctly set-up - exiting.");
			return false;
		}
		
		//no pre-processing, all data within the time range will be counted and summed
		//considering intermittent data
		long stTime = inputDataStream.getEffectiveStartTime().getTime();
		long edTime = inputDataStream.getEffectiveEndTime().getTime();
		long intervalInMilli = sumTimeInSecs * 1000;
		long frequencyInMilli = inputMs.getFrequencyInMilli();
		ArrayList<Integer> bucketIdxList = new ArrayList<>();
		
		//use TreeMap instead of HashMap to get sorted Date key
		TreeMap<Date, SynchronizedDescriptiveStatistics> sumMap = new TreeMap<Date, SynchronizedDescriptiveStatistics>();
		DPMSMStreamReadout notNullRdot = null;
		for (DPMSMStreamReadout rdot : inputMs.getMSReadouts()) {
			if(rdot == null) {
				continue;
			}
			if(notNullRdot == null) {
				notNullRdot = rdot;
			}
			
			long rdotTime = rdot.getReadoutStartTime().getTime();
			if(rdotTime < stTime || rdotTime > edTime) {
				continue;
			}
			//same purpose as createPreprocessedMap, for same bucket only keep one point
			int bucketIdx = (int) ((rdotTime-stTime)/frequencyInMilli);
			if(bucketIdxList.contains(bucketIdx)) {
				continue;
			} else {
				bucketIdxList.add(bucketIdx);
			}
			
			Double val = rdot.getValueForValueId(inputDataStream.getMeanInputValId());
			int currIdx = (int) ((rdotTime-stTime)/intervalInMilli);
			Date currStartDate = new Date();
			currStartDate.setTime(stTime + currIdx * intervalInMilli);
			SynchronizedDescriptiveStatistics runningStat = null;
			if(sumMap.keySet().contains(currStartDate)) {
				runningStat = sumMap.get(currStartDate);
			} else {
				runningStat = new SynchronizedDescriptiveStatistics();
				sumMap.put(currStartDate, runningStat);				
			}
			if (val != null) {
				runningStat.addValue(val);
			}				
		}

//		TreeMap<Date, SynchronizedDescriptiveStatistics> sumMapSort = 
//				new TreeMap<Date, SynchronizedDescriptiveStatistics>(sumMap);
		Set<Date> timeSet = sumMap.keySet(); ///?????sort
		for (Date currStartDate : timeSet) {
			SynchronizedDescriptiveStatistics runningStat = sumMap.get(currStartDate);  //!!!!!!sort
//		Set set = sumMap.entrySet();
//		Iterator iterator = set.iterator();
//		while (iterator.hasNext()) {
//			Map.Entry me = (Map.Entry)iterator.next();
//			SynchronizedDescriptiveStatistics runningStat = (SynchronizedDescriptiveStatistics)me.getValue();
//			Date currStartDate = (Date)me.getKey();
			Date eTime = new Date();
			eTime.setTime(currStartDate.getTime() + intervalInMilli);
			extractTimeAverageData(inputMs, runningStat, currStartDate,
					eTime, notNullRdot);
		}

		return true;
	}

	protected void extractTimeAverageData(DPMSMeasStreamData inputMs,
			SynchronizedDescriptiveStatistics runningStat, 
			Date currStartDate, Date eTime, DPMSMStreamReadout rdot) {
		
		if (runningStat.getValues() != null) {
			double l1Sum = runningStat.getSum();
			double numberTips = runningStat.getN();
			CvalUncertComputation l1Uncert = outputDataStream
					.getCvalUncertComputation();
			//return combU and dof
			if(l1Uncert != null) {
				l1Uncert.setL1meanValue(numberTips);
				CvalUncertComputation.Uncertainties uncerts = l1Uncert.computeL1SumUncertainties(rdot,
					inputDataStream.getRawInputValId(),  l1Sum);
				try {
//					double k95 = QAQCProcessorUtilities
//							.computeCoverageFactor(uncerts.effectiveDOF);
					//use common factor 2. instead - 10/29/2015
					double k95 = 2.;
					double expandedU = k95 * uncerts.combinedU;
					outputDataStream.writeL1Readouts(runningStat, expandedU,
							currStartDate, eTime);
				} catch (Exception e) {
					log.debug("k95 computation invalid");
				}
			} else {
				log.debug("compute L1Sum but no uncertainties will be calculated"
						+ " for measurement stream " + inputMs.getMeasStrmName());
				/* here call output for sun presence in direct&diffuse radiation,
				   which requires 75% sun presence within time period */
				outputDataStream.writeL1SimpleReadouts(runningStat, currStartDate, eTime);
			}
		} else {
			log.debug("Couldn't compute L1Sum between " + currStartDate
					+ " and " + eTime + "for measurement stream "
					+ inputMs.getMeasStrmName());
		}

	}		
	
}
