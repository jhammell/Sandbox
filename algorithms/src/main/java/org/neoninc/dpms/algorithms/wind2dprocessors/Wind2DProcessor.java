package org.neoninc.dpms.algorithms.wind2dprocessors;

import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;

import org.apache.log4j.Logger;
/*
 * Class that encapsulates methods for processing 2D wind data.
 */
public class Wind2DProcessor {
	private static Logger log = Logger.getLogger(Wind2DProcessor.class);
	
	private DPMSMeasStreamData inputMsU;
	private DPMSMeasStreamData inputMsV;
	private DPMSMeasStreamData inputMsHealth;
	private DPMSMeasStreamData outputMsWindSpeed;
	private DPMSMeasStreamData outputMsWindDirection;
	private Long inputUValID;
	private Long inputVValID;
	private Long inputHealthValID;
	private Long outputSpeedValID;
	private Long outputDirValID;
	private Long outputSensorTestValID;
	private double gammaRotation;
	private boolean isAIS;
	
	/**
	 * Constructor.  The formal parameters will be used for the wind speed & wind direction calculations.
	 * @param inputMsU - The U component.
	 * @param inputMsV - The V component.
	 * @param inputMsHealth - The component that represents health status.
	 * @param outputMs - The object that will contain the output wind speed data.
	 * @param inputUValID - The value ID that represents data values in the U component
	 * @param inputVValID - The value ID that represents data values in the V component
	 * @param inputHealthValID - The value ID that represents data values in the Health component.
	 * @param outputSpeedValID - The value ID that will be used to insert data into the output wind speed object.
	 * @param outputDirValID - The value ID that will be used to insert data into the output wind direction object.
	 * @param outputSensorTestValID - The value ID that will be used to insert sensor test data in the wind speed or direction object.
	 * @param gammaRotation - The rotation offset from true north (radians).
	 */
	public Wind2DProcessor (
		DPMSMeasStreamData inputMsU,
		DPMSMeasStreamData inputMsV,
		DPMSMeasStreamData inputMsHealth,
		DPMSMeasStreamData outputMsWindSpeed,
		DPMSMeasStreamData outputMsWindDirection,
		Long inputUValID, Long inputVValID, Long inputHealthValID, 
		Long outputSpeedValID, Long outputDirValID, Long outputSensorTestValID,
		double gammaRotation, boolean isAIS) {
			this.inputMsU = inputMsU;
			this.inputMsV = inputMsV;
			this.inputMsHealth = inputMsHealth;
			this.outputMsWindSpeed = outputMsWindSpeed;
			this.outputMsWindDirection = outputMsWindDirection;
			this.inputUValID = inputUValID;
			this.inputVValID = inputVValID;
			this.inputHealthValID = inputHealthValID;
			this.outputSpeedValID = outputSpeedValID;
			this.outputDirValID = outputDirValID;
			this.outputSensorTestValID = outputSensorTestValID;
			this.gammaRotation = gammaRotation;
			this.isAIS = isAIS;
	}

	/**
	 * Calculate the horizontal wind speed.  Calculations are based on U and V vector components,
	 * which are contained in the inputMsU and inputMsV parameters.
	 */
	
	public boolean computeVectorHorizontalWindSpeed() {

		if ((inputMsU==null) || (inputMsV==null) || 
			(outputMsWindSpeed==null) ||(outputSpeedValID==null)) {
			log.error ("Null input values.  Exiting...");
			return false;
		}
		// Preprocess data (Note: won't do anything if already done)
		// Preprocessing allows us to step efficiently through two related streams
		// (inputMsU and inputMsv).
		inputMsU.createPreprocessedMap();
		inputMsV.createPreprocessedMap();
		if (inputMsHealth != null) inputMsHealth.createPreprocessedMap();
		
		// go through each 1Hz measurement
		int npoints = inputMsU.getNumberOfPreprocessedPoints();
		for (int i=0; i<npoints; ++i) {
			DPMSMStreamReadout rdotU = inputMsU.getPreprocessedReadoutByIndex(i);
			DPMSMStreamReadout rdotV = inputMsV.getPreprocessedReadoutByIndex(i);
			DPMSMStreamReadout rdotHealth = null;
			if (inputMsHealth != null) rdotHealth = inputMsHealth.getPreprocessedReadoutByIndex(i);
			
			if ((rdotU==null) || (rdotV==null)) continue;

			Double valUObj = rdotU != null ? rdotU
					.getValueForValueId(inputUValID) : null;
			Double valVObj = rdotV != null ? rdotV
					.getValueForValueId(inputVValID) : null;

			double valU = (valUObj == null) ? 0.0 : valUObj.doubleValue();
			double valV = (valVObj == null) ? 0.0 : valVObj.doubleValue();
			// calculate horizontal wind speed at 1 Hz and put in a measurement stream
			double horizontalWindSpeed = Wind2DAlgorithmicFunctions.horizontalWindSpeed(valU, valV);

			DPMSMStreamReadout rdotNew = outputMsWindSpeed.addReadoutValue
			(rdotU.getReadoutStartTime(), rdotU.getReadoutEndTime(),
					outputSpeedValID, new Double(horizontalWindSpeed));
			sensorTest (rdotHealth, rdotNew);
		}	
		return true;
	}

	/**
	 * Calculate the wind direction.  Calculations are based on U and V vector components,
	 * which are contained in the inputDataStreamU and inputDataStreamV parameters.
	 */
	public boolean computeVectorWindDirection() {
		
		if ((inputMsU==null) || (inputMsV==null) || (outputMsWindDirection==null) ||
				(outputDirValID==null)) {
				log.error ("Null input values.  Exiting...");
				return false;
			}
		// Preprocess data (Note: won't do anything if already done)
		// Preprocessing allows us to step efficiently through two related streams
		// (inputMsU and inputMsv).
		inputMsU.createPreprocessedMap();
		inputMsV.createPreprocessedMap();
		if (inputMsHealth != null) inputMsHealth.createPreprocessedMap();
		
		// go through each 1Hz measurement
		int npoints = inputMsU.getNumberOfPreprocessedPoints();
		for (int i=0; i<npoints; ++i) {
			DPMSMStreamReadout rdotU = inputMsU.getPreprocessedReadoutByIndex(i);
			DPMSMStreamReadout rdotV = inputMsV.getPreprocessedReadoutByIndex(i);
			DPMSMStreamReadout rdotHealth = null;
			if (inputMsHealth != null) rdotHealth = inputMsHealth.getPreprocessedReadoutByIndex(i);

			if ((rdotU==null) || (rdotV==null)) continue;

			Double valUObj = rdotU != null ? rdotU
					.getValueForValueId(inputUValID) : null;
			Double valVObj = rdotV != null ? rdotV
					.getValueForValueId(inputVValID) : null;

			double valU = (valUObj == null) ? 0.0 : valUObj.doubleValue();
			double valV = (valVObj == null) ? 0.0 : valVObj.doubleValue();
			
			//at aquatic sites, 2D anemometer is oriented upward, different from tower site which is oriented downward
			if(isAIS) valV = -1. * valV;
			
			// calculate the angle of the wind direction at 1 Hz and put in a measurement stream
			double windDirectionTheta = Wind2DAlgorithmicFunctions.windDirection(valU, valV, gammaRotation);

			DPMSMStreamReadout rdotNew = outputMsWindDirection.addReadoutValue
			(rdotU.getReadoutStartTime(), rdotU.getReadoutEndTime(),
					outputDirValID, new Double(windDirectionTheta));
			sensorTest (rdotHealth, rdotNew);
		}
		return true;
	}

	/**
	 * Convert raw U & V values from the input data streams into wind speed and wind direction data.
	 * This method assumes that the input data stream has been:
	 *    (1) Calibrated
	 *    (2) L0 uncertainties computed and stored with the readout
	 * @return - true if the values were combined successfully, false otherwise.
	 */
	public boolean combineRawValues () {
		// Check that input exists
		if ((inputMsU == null) || (inputMsV == null) ||
			(outputMsWindSpeed == null) || (outputMsWindDirection == null)) {
			log.debug("Invalid input to combineRawValues - exiting");
			return false;
		}
		if (inputMsU.getFrequencyInMilli() == null
				|| inputMsU.getStartDate() == null
				|| inputMsU.getEndDate() == null) {
			log.debug("Measurement stream " + inputMsU.getMeasStrmName()
					+ " is not correctly set-up - exiting.");
			return false;
		}
		if (inputMsV.getFrequencyInMilli() == null
				|| inputMsV.getStartDate() == null
				|| inputMsV.getEndDate() == null) {
			log.debug("Measurement stream " + inputMsV.getMeasStrmName()
					+ " is not correctly set-up - exiting.");
			return false;
		}

		boolean result = true;
		boolean localResult = true;
		// compute the horizontal wind speed
		localResult = computeVectorHorizontalWindSpeed();
		result &= localResult;
		
		// compute the wind direction (in radians)
		localResult = computeVectorWindDirection();
		result &= localResult;

		return result;
	}
	
	/**
	 * Perform a sensor test on the input readout, producing output to the output readout.
	 * ATBD section 5.3.
	 * Error codes are described in the C3 document, NEON.DOC.000387
	 */
	public void sensorTest(DPMSMStreamReadout rdotHealth, DPMSMStreamReadout rdotOut) {
		if ((rdotHealth==null) || (rdotOut == null)) {
			log.debug("Null input or output stream.  No sensor test performed.");
			return;
		}
		Double valHealthObj = rdotHealth != null ? rdotHealth
				.getValueForValueId(inputHealthValID) : null;
		double valHealth = (valHealthObj == null) ? 0.0 : valHealthObj.doubleValue();
		
		double qfE = 0.0;   // The error code - 0 if an error occurs, 1 otherwise
		if ((valHealth>0) && (valHealth<=10)) {  // 0-10: catastrophic failure
			qfE = 1.0;
		} else if ((valHealth>=50) && (valHealth<60)) {  // 50-59: Marginal operation
			qfE = 1.0;
		}
		
		rdotOut.setValueForValueId(outputSensorTestValID, qfE, true);
	}
}
