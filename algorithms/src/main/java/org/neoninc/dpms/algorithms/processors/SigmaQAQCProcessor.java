package org.neoninc.dpms.algorithms.processors;

import java.util.ArrayList;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.algorithms.QAQCWindowInputMeasStreams;

public class SigmaQAQCProcessor {

	static private Logger log = Logger.getLogger(SigmaQAQCProcessor.class);

	// Input time series
	private QAQCWindowInputMeasStreams input;

	public SigmaQAQCProcessor(QAQCWindowInputMeasStreams input) {
		super();
		this.input = input;
	}

	public boolean runAlgorithm() {

		log.error("Invalid call to SigmaQAQCVisitor - algorithm will not be run");
		return false;
		
		/*
		DPMSMeasStreamData inputData = input != null ? input
				.getInputDataStream() : null;
		if (inputData == null || inputData.getMSReadouts().size() < 1) {
			log.error("Invalid input for SigmaQAQCVisitor - no algorithm will be run");
			return false;
		}

		if (input.getWindowSize() == null || input.getWindowStep() == null ) {
			log.warn("Invalid input for Sigma test - exiting");
			return false;
		}
		
		// Both thresholds cannot be null? 
		if (input.getMaxValueThreshold() == null && input.getMinValueThreshold() == null ) {
			log.warn("Invalid input for Sigma test - no threshold defined. Exiting");
			return false;
		}

		// We will be working on the preprocessed data. Window size/steps are
		// given in number of points,
		// and the sigma test is based on the assumption that preprocessing has
		// been done
		inputData.createPreprocessedMap();

		// Main loop: we need to go through all points in the original time
		// series. pointNumber will track
		// that position in the overall time series.
		Integer pointNumber = input.getFirstWindowStartIndex();
		while (pointNumber != null ) {
			
			// Now evaluating the sub-window. For each sub-window, we create a
			// Statistics object to
			// compute sigma for that window. windowPos is the point moving
			// within the current window.
			SynchronizedDescriptiveStatistics windowDistrib = QAQCProcessorUtilities
					.createWindowsStatistics(inputData,
							input.getMeanInputValId(), pointNumber,
							input.getWindowSize());

			// Now 'flag' all data within the window if it is not within min/max
			// sigma cutoff values. Note that if either min or max thresholds are not set,
			//  test will pass
			double windowStd = windowDistrib.getStandardDeviation();
			if ( ( input.getMinValueThreshold() != null && windowStd < input.getMinValueThreshold() ) 
					&& (input.getMaxValueThreshold() != null && windowStd > input.getMaxValueThreshold()) ) {
				log.debug("Sigma test failed. Std Dev = " + windowStd +", min threshold = " + 
					input.getMinValueThreshold() +", max threshold = " + input.getMaxValueThreshold());
				input.writeFlagForWindow(pointNumber, 1.);
			} else {
				input.writeFlagForWindow(pointNumber, 0.);
			}
			
			// Going to next window if any.
			pointNumber = input.getNextWindowStartIndex(pointNumber);
		}
		return true;
		*/
	}

}
