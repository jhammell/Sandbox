package org.neoninc.dpms.algorithms.processors;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamValue;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMeasStreamUtilities;
import org.neoninc.dpms.datastructures.DataProductName;
import org.neoninc.dpms.datastructures.algorithms.CvalUncertComputation;
import org.neoninc.dpms.datastructures.algorithms.L1AverageInputMeasStreams;
import org.neoninc.dpms.datastructures.algorithms.L1AverageOutputMeasStreams;


public class L1AverageProcessor {
	// logger instance
	static private Logger log = Logger.getLogger(L1AverageProcessor.class);
	public boolean CAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA = false;

	/*
	 * The input data stream. It is assumed that it has been: 1) Calibrated 2)
	 * L0 uncertainties are computed and stored along with the readout
	 */
	private L1AverageInputMeasStreams inputDataStream;

	/*
	 * All of the outputs. Note that some might be null, if client doesn't need
	 * derived data from this algorithm.
	 */
	private L1AverageOutputMeasStreams outputDataStream;

	/*
	 * Average time period (in seconds)
	 */
	private Long averageTimeInSecs;

	public L1AverageProcessor(L1AverageInputMeasStreams inputDataStream,
			L1AverageOutputMeasStreams outputDataStreams,
			Long averageTimeInSeconds) {
		this.inputDataStream = inputDataStream;
		this.outputDataStream = outputDataStreams;
		this.averageTimeInSecs = averageTimeInSeconds;
	}

	public boolean runAlgorithm() {

		// Check input
		if (inputDataStream == null
				|| inputDataStream.getInputDataStream() == null) {
			log.debug("Invalid input to runAverages - exiting");
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

		// The following are logging messages.
//		DataProductName dpNameObj = new DataProductName(inputMs.getMeasStrmName());
//		if(dpNameObj.getSubProductNumber().equals("01371")) {
//			log.debug("L1AverageProcessor Conductance inputMs name: '" + inputMs.getMeasStrmName() + "'");
//		} else if(dpNameObj.getSubProductNumber().equals("01374")) {
//			log.debug("L1AverageProcessor Temperature inputMs name: '" + inputMs.getMeasStrmName() + "'");
//		} else if(dpNameObj.getSubProductNumber().equals("01376")) {
//			log.debug("L1AverageProcessor Pressure inputMs name: '" + inputMs.getMeasStrmName() + "'");				
//		}
//		if(dpNameObj.getSubProductNumber().equals("01379")) {
//			log.debug("L1AverageProcessor surface water pressure inputMs name: '" + inputMs.getMeasStrmName() + "'");
//		}
				
		SynchronizedDescriptiveStatistics runningStat = new SynchronizedDescriptiveStatistics();
		Long sensorRate = inputMs.getFrequencyInMilli();

		int numPtsPerWindow = (int) (averageTimeInSecs / (sensorRate / 1000));
		Long intervalInMilli = averageTimeInSecs * 1000;

		// Preprocess data (Note: won't do anything if already done)
		inputMs.createPreprocessedMap();

		int startPoint = inputMs
				.getStartIndexFromEffectiveStartDate(inputDataStream
						.getEffectiveStartTime());
		int endPoint = inputMs.getEndIndexFromEffectiveEndDate(inputDataStream
				.getEffectiveEndTime());

//		log.debug("Before printing output - startPoint: '" + startPoint + "'    endPoint: '" + endPoint + "'");
//		printRdotValues(inputDataStream.getInputDataStream());
//		log.debug("After printing output ...");
		
		// Make a safe copy, as we're using this object to stamp stuff over and
		// over.
		Date currStartDate = (Date) inputDataStream.getEffectiveStartTime().clone();
		int windowIndex = 0;
		DPMSMStreamReadout maxRdot = null;
		Double maxCombU = null;
		
		while (startPoint <= endPoint) {

			DPMSMStreamReadout rdot = inputDataStream.getInputDataStream()
					.getPreprocessedReadoutByIndex(startPoint);

//			if(rdot != null) {
//				ArrayList<DPMSMStreamValue> valList = rdot.getValues();			
//				for(DPMSMStreamValue dVal : valList) {
//					log.debug("Inside dVal: '" + dVal + "'   ParentValueId: '" + dVal.getParentValueId() + "'    ValueType: '" + dVal.getValueType() + "'    '" + rdot.getReadoutStartTime() + "'    '" + rdot.getReadoutEndTime() + "'");
//				}
//			}
			
			startPoint++;
			windowIndex++;
//			log.debug("1-runningStat: '" + runningStat.getValues().length + "'    check include: '" + inputDataStream.includeMeasReadout(rdot) + "'");
			if (inputDataStream.includeMeasReadout(rdot)) {
				DPMSMStreamReadout tempRdot = processReadout(runningStat, maxCombU, rdot);
				if(tempRdot != null) {
					maxRdot = tempRdot;
					maxCombU = maxRdot.getCombinedUncertaintyForValueId(inputDataStream.getMeanInputValId());
				}
//				log.debug("2-runningStat: '" + runningStat.getValues().length + "'");
			}

			// Check for time interval done.
			if ((windowIndex % numPtsPerWindow) == 0) {
				// Done with 1min - write readouts if data available.
				Date eTime = new Date();
				eTime.setTime(currStartDate.getTime() + intervalInMilli);
				boolean localSuccess = extractTimeAverageData(inputMs, runningStat, currStartDate,
						eTime, maxRdot);
				if(!localSuccess)	{
					return false;
				}
				// Reset one minute datasets.
				currStartDate = eTime;
				maxRdot = null;
				maxCombU = null;
				runningStat.clear();
			}
		}

		return true;
	}

	protected boolean extractTimeAverageData(DPMSMeasStreamData inputMs,
			SynchronizedDescriptiveStatistics runningStat, Date currStartDate,
			Date eTime, DPMSMStreamReadout maxRdot) {
//		log.debug("RUNNING extractTimeAverageData runningStat.getValues(): '" + runningStat.getValues() + 
//					"'    runningStat.getValues().length: '" + runningStat.getValues().length + "'");
		//use common factor 2. instead - 10/29/2015
		double k95 = 2.;
		if (runningStat.getValues() != null
				&& runningStat.getValues().length > 1) {	
			int numPt = runningStat.getValues().length;
			Double standardError = runningStat.getStandardDeviation() / Math.sqrt(numPt);
			Double meanVal = runningStat.getMean();
			CvalUncertComputation l1Uncert = outputDataStream.getCvalUncertComputation();
			//return combU and dof
			if(l1Uncert != null) {
				l1Uncert.setL1meanValue(meanVal);
				CvalUncertComputation.Uncertainties uncerts = l1Uncert.computeL1Uncertainties(maxRdot,
					inputDataStream.getRawInputValId(), standardError, numPt);
				if(uncerts == null || uncerts.combinedU == null) {
					log.error("L1 uncertainty (or combined uncertainty) is null. Check!");
					return false;
				}
//				double k95 = QAQCProcessorUtilities.computeCoverageFactor(uncerts.effectiveDOF);
				Double expandedU = k95 * uncerts.combinedU;
				outputDataStream.writeL1Readouts(runningStat, standardError, expandedU, currStartDate, eTime);
			} else {
				// we should have uncertainty when there is mean value.
				log.error("The l1Uncert is null though there is mean value. Check!");
				return false;
			}
		} else if ( runningStat.getValues() != null
				&& runningStat.getValues().length == 1) {
			if(isCAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA()) {
				// Groundwater well ATBD requires to provide L1 expanded uncertainty even there is only one point data.
				Double expandedU = null;
				int numPt = runningStat.getValues().length;
				// It is one point. The standardError is assumed to be 0.
				Double standardError = 0.0;
				CvalUncertComputation l1Uncert = outputDataStream.getCvalUncertComputation();
				CvalUncertComputation.Uncertainties uncerts = l1Uncert.computeL1Uncertainties(maxRdot,
						inputDataStream.getRawInputValId(), standardError, numPt);
//				log.debug("Check uncerts.combinedU: '" + uncerts.combinedU + "'    numPt: '" + numPt + "'    maxRdot: '" + maxRdot + "'");
				// When maxRdot is null, uncerts.combinedU will be null. This causes null pointer exception.
				// Check whether uncerts.combinedU is null or not here is safe. For single point L1 statistics 
				// maxRdot is always equal to rdot of that point. It is handled in processReadout() method.
				//                                   May 26, 2016
				if(uncerts.combinedU != null) {
					expandedU = k95 * uncerts.combinedU;
					outputDataStream.writeL1Readouts(runningStat, standardError, expandedU,
							currStartDate, eTime);
				}
			} else {
				//02/08/2016 see email 02/05/2016 QA/QC IPT
				//in case of 1 measurement, standard error cannot be computed, scientists want the combined 
				//uncertainty be the measurement uncertainty, which have to be handled in each ATBD.
				Double standardError = DPMSMeasStreamUtilities.HANDLE_THIS_SPECIAL_NUMBER;
				CvalUncertComputation l1Uncert = outputDataStream.getCvalUncertComputation();
				CvalUncertComputation.Uncertainties uncerts = l1Uncert.computeL1Uncertainties(maxRdot,
						inputDataStream.getRawInputValId(), standardError, 1);
				Double expandedU = k95 * uncerts.combinedU;
				outputDataStream.writeL1Readouts(runningStat, DPMSMeasStreamUtilities.CANNOT_COMPUTE_MAGIC_NUMBER, 
						expandedU, currStartDate, eTime);
			}
		} else {
			log.debug("Couldn't compute L1Average between " + currStartDate
					+ " and " + eTime + "for measurement stream "
					+ inputMs.getMeasStrmName());
		}
		return true;
	}		
	
	//add value from current readout to "runningStat"
	//return current rdot as maxRdot only when its combU is bigger
	protected DPMSMStreamReadout processReadout(
			SynchronizedDescriptiveStatistics runningStat,
			Double maxCombU, DPMSMStreamReadout rdot) {
		
		Double val = rdot != null ? rdot
				.getValueForValueId(inputDataStream
						.getMeanInputValId()) : null;
		// To calculate one-point L1 statistics, we do not care combinedU and expandU. 
		// The inputStream's rdot is simply returned.
		//                     SG             May 26, 2016
		if(isCAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA() && val != null)  {
			runningStat.addValue(val);
			return rdot;
		}
		
		if (val != null) {
			runningStat.addValue(val);
			Double combinedU = rdot
					.getCombinedUncertaintyForValueId(inputDataStream
							.getMeanInputValId());
			Double expandU = rdot.getEffectiveDegOfFreedom(inputDataStream
					.getMeanInputValId());
			if (combinedU != null && expandU != null) {
				if (combinedU.isNaN() || combinedU.isInfinite()
						|| expandU.isInfinite() || expandU.isNaN()) {
					return null;
				}
				if (maxCombU == null || combinedU > maxCombU) {
					return rdot;
				}
			}
		}
		return null;
	}
	
	/**Running the average directly on raw data. This function will use every point
	 * for its final average
	 * @return
	 */
	public boolean runAlgorithmRawData() {

		// Check input
		if (inputDataStream == null
				|| inputDataStream.getInputDataStream() == null) {
			log.debug("Invalid input to runAverages - exiting");
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

		SynchronizedDescriptiveStatistics runningStat = new SynchronizedDescriptiveStatistics();

		// Creating start/end interval for the average
		long stTime = inputDataStream.getEffectiveStartTime().getTime();
		long edTime = inputDataStream.getEffectiveEndTime().getTime();
		long intervalInMilli = averageTimeInSecs * 1000;

		DPMSMStreamReadout maxRdot = null;
		Double maxCombU = null;

		for (DPMSMStreamReadout rdot : inputMs.getMSReadouts()) {
			if (rdot == null) {
				continue;
			}

			long rdotTime = rdot.getReadoutStartTime().getTime();

			// OUtside of bounds
			if (rdotTime < stTime || rdotTime > edTime) {
				continue;
			}

			// Passed the current average
			if (rdotTime >= stTime + intervalInMilli) {
				// Done with time period - write readouts if data available.
				Date stDate = new Date();
				stDate.setTime(stTime);
				Date eTime = new Date();
				eTime.setTime(stTime + intervalInMilli);
				extractTimeAverageData(inputMs, runningStat, stDate, eTime,
						maxRdot);
				// Reset one minute datasets.
				stTime = stTime + intervalInMilli;
				maxRdot = null;
				maxCombU = null;
				runningStat.clear();
			}

			if (inputDataStream.includeMeasReadout(rdot)) {
				DPMSMStreamReadout tempRdot = processReadout(runningStat,
						maxCombU, rdot);
				if (tempRdot != null) {
					maxRdot = tempRdot;
					maxCombU = maxRdot
							.getCombinedUncertaintyForValueId(inputDataStream
									.getMeanInputValId());
				}
			}

		}
		
		// Let's make sure we write the last ongoing average if we stopped having points
		if (runningStat.getValues() != null && runningStat.getValues().length > 0 ) {
			log.debug("Writing last average");
			// Done with time period - write readouts if data available.
			Date stDate = new Date();
			stDate.setTime(stTime);
			Date eTime = new Date();
			eTime.setTime(stTime + intervalInMilli);
			extractTimeAverageData(inputMs, runningStat, stDate, eTime,
					maxRdot);
			// Reset one minute datasets.
			stTime = stTime + intervalInMilli;
			maxRdot = null;
			maxCombU = null;
			runningStat.clear();
		}
		return true;

	}

	public boolean isCAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA() {
		return CAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA;
	}

	public void setCAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA(
			boolean cAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA) {
		CAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA = cAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA;
	}
	
	/**
	 * This method prints out rdot values for a given DPMSMeasStreamData object.
	 */
	private void printRdotValues(DPMSMeasStreamData streamData) {
		ArrayList<DPMSMStreamReadout> rdotList = streamData.getMSReadouts();
		for(DPMSMStreamReadout rdot : rdotList) {
			ArrayList<DPMSMStreamValue> valList = rdot.getValues();
			for(DPMSMStreamValue dVal : valList) {
				log.debug("L1AverageProcessor dVal: '" + dVal + "'   ParentValueId: '" + dVal.getParentValueId() + "'    ValueType: '" + dVal.getValueType() + "'    '" + rdot.getReadoutStartTime() + "'    '" + rdot.getReadoutEndTime() + "'");
			}
		}
	}
		
}
