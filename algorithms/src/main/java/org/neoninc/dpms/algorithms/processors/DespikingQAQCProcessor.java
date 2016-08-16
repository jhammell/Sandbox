package org.neoninc.dpms.algorithms.processors;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.algorithms.DespikingInputMeasStreams;

/**Implementation of ATBD NEON.DOC.000783, despiking of L0, calibrated data
 * @author fpradeau
 *
 */
public class DespikingQAQCProcessor {

	private static final Object DESPIKING_METHOD_A_STRING = "A";

	static private Logger log = Logger.getLogger(DespikingQAQCProcessor.class);

	/**
	 * Small class to keep track of flag counts and number of assessments while
	 * running through the despiking test. Most of it is needed for method B,
	 * while method A should mostly have a numAssessment = 1 and num flags = 0
	 * or 1 since points get tested once only
	 * 
	 * @author fpradeau
	 * 
	 */
	static public class PointInformation {
		// Main index in the parent time series (preprocessed index)
		public Integer mainMeasStreamIndex = new Integer(-1);
		// Number of assessments for the point. Method A should have 0 or 1,
		//   method B more than one
		public Integer numAssessments = new Integer(0);
		// Counting the times the point was flagged as a possible spike
		public Integer highFlagCount = new Integer(0);
		// Final value for the despiking plausible flag. Initial value is 'wasn't evaluated'
		public Integer finalPlausibleFlag = new Integer(-1);
		// Final value for the despiking spurious flag. Initial value is 'wasn't evaluated'
		public Integer finalSpuriousFlag = new Integer(-1);
		
		/**
		 * Default comparator for sort function on list of PointInformation.
		 * 
		 * @param otherPt
		 * @return
		 */
		public int compareTo(PointInformation otherPt) {
			if (otherPt == null ) {
				return -1;
			}
			return mainMeasStreamIndex.compareTo(otherPt.mainMeasStreamIndex);
		}

	}

	// Input time series
	private DespikingInputMeasStreams input;

	public DespikingQAQCProcessor(DespikingInputMeasStreams input) {
		this.input = input;
	}

	public boolean runAlgorithm() {

		DPMSMeasStreamData inputData = input != null ? input
				.getInputDataStream() : null;
		if (inputData == null || inputData.getMSReadouts().size() < 1) {
			log.error("Invalid input for DespikingQAQCVisitor - no algorithm will be run");
			return false;
		}

		if (input.getWindowSize() == null || input.getWindowStep() == null
				|| input.getMadValueThreshold() == null) {
			log.warn("No window data (step/size) to evaluate despiking test. Exiting.");
			return false;
		}
		if (!input.getMethodName().equals(DESPIKING_METHOD_A_STRING) && input.getMinSpikeCountPercentage() == null ) {
			log.warn("No Min Spike Count percentage to evaluate despiking test method B. Exiting.");
			return false;
		}

		// We will be working on the preprocessed data. Window size/steps are
		// given in number of points, and the despiking ATBD calls that
		// preprocessing has to be applied
		inputData.createPreprocessedMap();

		// Keeping track of the number of fails (method B) or just pass/fail
		// flags for
		// center point (method A)
		TreeMap<Integer, PointInformation> failCounts = new TreeMap<>();

		// Run the windows on the data.
		runWindowsOnData(inputData, failCounts);

		// Sort based on keys
		// Evaluate flags
		storeDespikingFlags(failCounts);
		return true;
	}

	/**
	 * Run through the data with sliding window to evaluate the counts/feasible
	 * spikes
	 * 
	 * @param inputData
	 * @param failCounts
	 */
	protected void runWindowsOnData(DPMSMeasStreamData inputData,
			TreeMap<Integer, PointInformation> failCounts) {
		// Main loop: we need to go through all points in the original time
		// series. pointNumber will track
		// that position in the overall time series.
		Integer pointNumber = input.getFirstWindowStartIndex();
		while (pointNumber != null) {

			// For method A, if mid point is null, just continue.
			DPMSMStreamReadout midPt = null;
			if (input.getMethodName().equals(DESPIKING_METHOD_A_STRING)) {
				midPt = inputData.getPreprocessedReadoutByIndex(input
						.getWindowMidPointIndex(pointNumber));
				if (midPt == null) {
					// No mid point to evaluate, moving on...
					pointNumber = input.getNextWindowStartIndex(pointNumber);
					continue;
				}
			}
			// Evaluating the sub-window. For each sub-window, we create a
			// Statistics object.
			SynchronizedDescriptiveStatistics windowDistrib = QAQCProcessorUtilities
					.createWindowsStatistics(inputData,
							input.getMeanInputValId(), pointNumber,
							input.getWindowSize());

			// Less than 4 points - ATBD wants to flag all points as NA
			if (windowDistrib.getValues().length < 4) {
				input.writeFlagsForWindow(pointNumber, -1., -1.);
				// Going to next window if any.
				pointNumber = input.getNextWindowStartIndex(pointNumber);
				continue;
			}

			// Get median of the initial window points.
			Double medianVal = QAQCProcessorUtilities.computeMedian(windowDistrib
					.getValues());

			// Compute window MAD
			Double madForWindow = computeMADForWindow(windowDistrib, medianVal);

			// Compute Mean Absolute Deviation (MADadj) from ATBD:
			// MADadj=bn*q*k*madForWindow
			Double bn = computeCorrectionFactor(windowDistrib.getValues().length);
			Double MADadj = bn * input.getMadValueThreshold()
					* input.getScaleFactor() * madForWindow;
			log.trace("  MADadj: " + MADadj + ", window median: " + medianVal);

			// Now evaluate flags
			if (input.getMethodName().equals(DESPIKING_METHOD_A_STRING)) {
				Integer tempIndex = input.getWindowMidPointIndex(pointNumber);
				updateFlagForPoint(midPt, tempIndex, failCounts,
						MADadj, medianVal);
			} else {
				Integer windowPos = 0;
				// Looping over the window while making sure we don't go over
				// the 'end' of the time series
				while (windowPos < input.getWindowSize()) {
					DPMSMStreamReadout rdot = inputData
							.getPreprocessedReadoutByIndex(pointNumber
									+ windowPos);
					updateFlagForPoint(rdot, pointNumber + windowPos,
							failCounts, MADadj, medianVal);
					// Moving along.
					windowPos++;
				}

			}

			// Move the window
			pointNumber = input.getNextWindowStartIndex(pointNumber);

		}
	}

	/**
	 * Method evaluating final feasible/spurrious flags after the run
	 * 
	 * @param failCounts
	 */
	private void storeDespikingFlags(
			TreeMap<Integer, PointInformation> failCounts) {
		Integer numConsecutiveSpikes = 0;
		Integer startFlagIndex = 0;
		Integer minNumberOfAssessments = (int) Math.floor( input.getWindowSize().doubleValue() / input.getWindowStep().doubleValue());
		
		for (int iloop : failCounts.keySet()) {
			PointInformation info = failCounts.get(iloop);

			// No evaluation: no go (default constructor set flags properly to
			// -1)
			if (info.numAssessments < 1 ) {
				continue;
			}
			
			// For method B, we need a minimum number of evaluation per ATBD. Otherwise, flags 
			//  should stay at -1, eg. cannot evaluate
			if ( !input.getMethodName().equals(DESPIKING_METHOD_A_STRING) && info.numAssessments < minNumberOfAssessments ) {
				continue;
			}

			// Flags set to pass by default and is set to high based on logic
			info.finalSpuriousFlag = 0;
			info.finalPlausibleFlag = 0;

			// Method A is straight lookup
			boolean pass = info.highFlagCount < 1;

			// Method B: compare with threshold percentage
			if (!input.getMethodName().equals(DESPIKING_METHOD_A_STRING)) {
				Double percentage = 100. * info.highFlagCount.doubleValue() / info.numAssessments.doubleValue();
				pass = Math.floor(percentage) < input.getMinSpikeCountPercentage();
				log.debug("Method B flag for pt "+info.mainMeasStreamIndex + ":" + pass +
						" fail counts: "+info.highFlagCount + 
						" num assessments: "+info.numAssessments + 
						", percentage: "+percentage);
			}
			if (pass) {
				numConsecutiveSpikes = 0;
			} else {
				info.finalSpuriousFlag = 1;
				// Mark the start of the data to assign flags.
				if (numConsecutiveSpikes.equals(0)) {
					startFlagIndex = iloop;
				}
				numConsecutiveSpikes++;
			}

			// Have we reached the threshold? In which case, we mark all consecutive points
			//  as 'feasible' spikes
			if (numConsecutiveSpikes >= input.getNumSpikePointsThreshold()) {

				// Set flags for data that is part of this run
				while (startFlagIndex <= iloop) {
					PointInformation prevPt = failCounts.get(startFlagIndex);
					if (prevPt != null) {
						// ATBD specifically calls that spurious is turned off
						// if it was deemed plausible.
						prevPt.finalSpuriousFlag = 0;
						prevPt.finalPlausibleFlag = 1;
					}
					startFlagIndex++;
				}
			}
		}

		// Now write everything
		int numPts = input.getInputDataStream().getNumberOfPreprocessedPoints();
		log.trace("Number of preprocessed points: " + numPts);
		for (int iloop = 0; iloop < numPts; iloop++) {
			PointInformation info = failCounts.get(iloop);
			DPMSMStreamReadout rdot = input.getInputDataStream()
					.getPreprocessedReadoutByIndex(iloop);
			if (info != null) {
				log.trace("index is "+iloop+", PointInformation "+info.mainMeasStreamIndex);
				input.writeFeasibleFlag(rdot,
						info.finalPlausibleFlag.doubleValue());
				input.writeSpuriousFlag(rdot,
						info.finalSpuriousFlag.doubleValue());
			} else {
				// Never looked at.
				input.writeFeasibleFlag(rdot, -1.);
				input.writeSpuriousFlag(rdot, -1.);
			}
		}
	}

	/**
	 * Evaluate if the point passes/fails the despiking test and update the main
	 * structure of failed counts
	 * 
	 * @param midPt
	 * @param failCounts
	 * @param MADadj
	 */
	private void updateFlagForPoint(DPMSMStreamReadout midPt, Integer index,
			TreeMap<Integer, PointInformation> failCounts, Double MADadj,
			Double medWindow) {
		Double val = midPt != null ? midPt.getValueForValueId(input
				.getMeanInputValId()) : null;
		if (val == null) {
			return;
		}
		PointInformation count = failCounts.get(index);
		if (count == null) {
			count = new PointInformation();
			count.mainMeasStreamIndex = index.intValue();
			log.trace("Create PointInformation for index "+index);
			failCounts.put(index, count);
		}

		// Incrementing the number of assessments.
		count.numAssessments++;

		// Failing test?
		if (val < (medWindow - MADadj) || val > (medWindow + MADadj)) {
			log.trace("Failed point Index: " + index + ", value: " + val);
			count.highFlagCount++;
		}
	}

	/**
	 * Compute Median of the absolute values for the residuals of a distribution
	 * 
	 * @param windowDistrib
	 * @param medianVal
	 * @return MAD value
	 */
	private Double computeMADForWindow(
			SynchronizedDescriptiveStatistics windowDistrib, Double medianVal) {
		if (windowDistrib == null) {
			return null;
		}

		// Now compute MAD
		SynchronizedDescriptiveStatistics tempStat = new SynchronizedDescriptiveStatistics();
		for (double val : windowDistrib.getSortedValues()) {
			tempStat.addValue(Math.abs(val - medianVal));
		}
		Double mad = QAQCProcessorUtilities.computeMedian(tempStat.getSortedValues());
		return mad;
	}

	/**
	 * Calculates the correction factor that contributes to the despiking
	 * thresholds for a window.
	 * 
	 * @param nWindowPoints
	 *            : the number of points in the window
	 * @return the correction factor
	 * 
	 */
	private Double computeCorrectionFactor(int nWindowPoints) {

		Double correctionFactor = null;

		if (nWindowPoints > 9) {
			correctionFactor = nWindowPoints / (nWindowPoints - 0.8);
		} else if (nWindowPoints == 9) {
			correctionFactor = 1.107;
		} else if (nWindowPoints == 8) {
			correctionFactor = 1.129;
		} else if (nWindowPoints == 7) {
			correctionFactor = 1.140;
		} else if (nWindowPoints == 6) {
			correctionFactor = 1.200;
		} else if (nWindowPoints == 5) {
			correctionFactor = 1.206;
		} else if (nWindowPoints == 4) {
			correctionFactor = 1.363;
		}

		return correctionFactor;
	}

	/**
	 * Initial implementation of the despiking routine.
	 * 
	 * @return
	 */
	public boolean runAlgorithmV1() {

		/*
		DPMSMeasStreamData inputData = input != null ? input
				.getInputDataStream() : null;
		if (inputData == null || inputData.getMSReadouts().size() < 1) {
			log.error("Invalid input for DespikingQAQCVisitor - no algorithm will be run");
			return false;
		}

		if (input.getWindowSize() == null || input.getWindowStep() == null
				|| input.getMadValueThreshold() == null) {
			log.warn("No window data (step/size) to evaluate despiking test. Exiting.");
			return false;
		}

		double[] dataArray = inputData.getCalibratedValuesForValId(input
				.getMeanInputValId());
		if (dataArray == null) {
			log.error("Invalid input for DespikingQAQCVisitor - no algorithm will be run");
			return false;
		}

		// TODO: Improve logic for setting initial central point
		int centralPointIdx = (int) Math.round(input.getWindowSize() / 2);

		double frequency = inputData.getFrequencyInMilli().doubleValue();
		Double winSize = (double) (inputData.getFrequencyInMilli() * input
				.getWindowSize());

		Double winStep = (double) (inputData.getFrequencyInMilli() * input
				.getWindowStep());

		DespikeQAQC despikeTest = QAQCWrapper.runDespikeQAQC(dataArray,
				inputData.getOriginalTimeValues(), winSize, winStep, input
						.getMadValueThreshold().doubleValue(), input
						.getScaleFactor().doubleValue(), input
						.getNumSpikePointsThreshold().intValue(), input
						.getMethodName(), input.getMinSpikeCountPercentage(),
				input.getMaxMissingPercentage(), frequency, input
						.getDasTimeTolerance(), centralPointIdx);

		QAQCProcessorUtilities.storeQAQCFlags(inputData,
				despikeTest.getSpuriousFlagValues(),
				input.getQaQcSpurriousFlagValId(), log);
		QAQCProcessorUtilities.storeQAQCFlags(inputData,
				despikeTest.getFeasibleFlagValues(),
				input.getQaQcFeasibleFlagValId(), log);
				*/
		return true;
	}

}
