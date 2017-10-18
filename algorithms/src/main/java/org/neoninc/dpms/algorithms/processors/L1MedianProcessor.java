package org.neoninc.dpms.algorithms.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.algorithms.L1InputMeasStreams;
import org.neoninc.dpms.datastructures.algorithms.L1MedianOutputMeasStreams;


public class L1MedianProcessor {
	private static final Double ZERO = new Double(0d);
	// logger instance
	static private Logger log = Logger.getLogger(L1MedianProcessor.class);
//	public boolean CAL_EXPANDED_UNCERT_WITH_ONE_POINT_DATA = false;

	/*
	 * The input data stream. It is assumed that it has been: 1) Calibrated 2)
	 * L0 uncertainties are computed and stored along with the readout
	 */
	private L1InputMeasStreams inputDataStream;

	/*
	 * All of the outputs. Note that some might be null, if client doesn't need
	 * derived data from this algorithm.
	 */
	private L1MedianOutputMeasStreams outputDataStream;

	/*
	 * Median time period (in seconds)
	 */
	private Long medianTimeInSecs;
	
	private Long rawValId;

	public L1MedianProcessor(L1InputMeasStreams inputDataStream,
			L1MedianOutputMeasStreams outputDataStreams,
			Long medianTimeInSeconds) {
		this.inputDataStream = inputDataStream;
		this.outputDataStream = outputDataStreams;
		// for NEON.DOC.002675 this is: this will be 1800/3600
		this.medianTimeInSecs = medianTimeInSeconds;
		this.rawValId = inputDataStream.getRawInputValId();
	}

	public boolean runAlgorithm() {

		// Check input
		if (inputDataStream == null
				|| inputDataStream.getInputDataStream() == null) {
			log.debug("Invalid input to runMedians - exiting");
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
		
				
		Long sensorRate = inputMs.getFrequencyInMilli();

		int numPtsPerWindow = (int) (medianTimeInSecs / (sensorRate / 1000));
		Long intervalInMilli = medianTimeInSecs * 1000;

		int startPoint = inputMs.getStartIndexFromEffectiveStartDate(inputDataStream.getEffectiveStartTime());
		int endPoint = inputMs.getEndIndexFromEffectiveEndDate(inputDataStream.getEffectiveEndTime());

		// Make a safe copy, as we're using this object to stamp stuff over and
		// over.
		Date currStartDate = (Date) inputDataStream.getEffectiveStartTime().clone();
		int windowIndex = 0;

		List<Double> validData = new ArrayList<>();
		
		while (startPoint <= endPoint) {

			// startpoint is 0-based
			DPMSMStreamReadout rdot = inputDataStream.getInputDataStream().getPreprocessedReadoutByIndex(startPoint);
			
			startPoint++;
			
			

			if (inputDataStream.includeMeasReadout(rdot)) {
				validData.add(rdot.getValues().get(0).getValue());
			}
		
			// windowIndex is 1-based.
			windowIndex++;

			// Check for time interval done.
			if ( (windowIndex % numPtsPerWindow) == 0) {
				// Done with 1min - write readouts if data available.
				Date eTime = new Date();
				eTime.setTime(currStartDate.getTime() + intervalInMilli);
				List<Double> nonZeroVals = new ArrayList<>();

				for (int i = 0; i < validData.size(); i++) {
					Double v = validData.get(i);
					if (v.compareTo(ZERO) > 0) {
						nonZeroVals.add(v);
					}
				}
				final Double[] a = nonZeroVals.toArray(new Double[] {});
				final double[] au = new double[a.length];
				for (int i = 0; i < a.length; i++) {
					au[i] = a[i].doubleValue();
				}
				boolean localSuccess = extractL1Data(au, currStartDate,
						eTime);
				if(!localSuccess)	{
					return false;
				}
				// Reset one minute datasets.
				currStartDate = eTime;
				windowIndex=0;
				validData.clear();
			}
			
		}

		return true;
	}

	protected boolean extractL1Data(double[] arr, Date currStartDate, Date eTime) {
		if (arr != null) {
			int numPt = arr.length;
			if(numPt>0) {
				boolean isEven = numPt%2==0;
				int halfLen = numPt/2;
				double [] sortedArr = new double[numPt];
				
				
				System.arraycopy(arr,0,sortedArr,0,numPt);
				Arrays.sort(sortedArr);
				
				double median,mad, min, max;
				min = sortedArr[0];
				max = sortedArr[numPt-1];
				if(isEven) {
					median = (sortedArr[halfLen-1]  + sortedArr[halfLen])/2; 
				} else {
					median = sortedArr[halfLen];
				}
				// compute mad for sortedArr
				double [] madDiffArr = new double[numPt];
				for( int i = 0; i<numPt;i++) {
					madDiffArr[i] = Math.abs(sortedArr[i]-median);
				}
				if(isEven) {
					mad = (madDiffArr[halfLen-1]  + madDiffArr[halfLen])/2; 
				} else {
					mad = madDiffArr[halfLen];
				}

				L1MedianOutputMeasStreams.writeL1Readouts(this.outputDataStream.getMedianDataStream(),median,this.outputDataStream.getMedianValId(),currStartDate, eTime);
				L1MedianOutputMeasStreams.writeL1Readouts(this.outputDataStream.getMadDataStream(),     mad,this.outputDataStream.getMadValId(), currStartDate, eTime);
				L1MedianOutputMeasStreams.writeL1Readouts(this.outputDataStream.getMinDataStream(),      min,this.outputDataStream.getMinValId(), currStartDate, eTime);
				L1MedianOutputMeasStreams.writeL1Readouts(this.outputDataStream.getMaxDataStream(),    max, this.outputDataStream.getMaxValId(), currStartDate, eTime);
				L1MedianOutputMeasStreams.writeL1Readouts(this.outputDataStream.getNumPtsDataStream(),new Double(numPt), this.outputDataStream.getNumPtsValId(), currStartDate, eTime);
			}
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
						.getMedianInputValId()) : null;
		
		if (val != null) {
			runningStat.addValue(val);
			Double combinedU = rdot
					.getCombinedUncertaintyForValueId(inputDataStream
							.getMedianInputValId());
			Double expandU = rdot.getEffectiveDegOfFreedom(inputDataStream
					.getMedianInputValId());
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
		
}
