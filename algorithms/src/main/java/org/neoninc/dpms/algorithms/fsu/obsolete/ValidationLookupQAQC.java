package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.Map;
import java.util.List;
import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * This validation class is able to validate an input String 
 * and produce the output defined by the plausibility ATDB.
 * Validation of a String value by this class includes:
 * - recognize if the String is null or empty
 * - use "CanBeNull" to determine if null or empty is valid or not
 * - use a lookup Map or String[] to determine if String matches a valid value
 * - if using a Map, use the String value from the Map as the output value.
 */
public class ValidationLookupQAQC {
	
	static private Logger log = Logger.getLogger(ValidationLookupQAQC.class);
	
	private DPMSMethActivity inputActy;
	private Long inputValID;  // ValID of data to be tested
	private boolean inputCanBeNull;  // true if input value String is allowed to be null

	private Long outputValTypeID;  // the VAL_TYPE_ID of MethStream where results should go
	private Long outputValID; // the ValID for creating the output data value
	private Long outputQFValID; // the ValID for creating the output Quality Flag
	
	private String[] lookupList; // an array of Strings to use for validation
	private Map<String, String> lookupMap; // a Map to use for validation

	/**
	 * @param inputActy - activity containing value to be tested
	 * @param inputValID - ValID of input to be tested
	 * @param outputValTypeID - ValTypeID of output to be created
	 * @param outputValID - ValID of output value to be created
	 * @param outputQFValID - ValID of QA/QC value to be created
	 * @param lookupList - a list of lookup values (should be input as Strings)
	 * @param lookuMap - a Map of lookup values (should be input as Strings)
	 */
	public ValidationLookupQAQC(DPMSMethActivity inputActy,
								Long inputValID,
								boolean inputCanBeNull,
								Long outputValTypeID,
								Long outputValID,
								Long outputQFValID,
								String[] lookupList,
								Map<String, String> lookupMap) {
		this.inputActy = inputActy;
		this.inputValID = inputValID;
		this.inputCanBeNull = inputCanBeNull;

		this.outputValTypeID = outputValTypeID;
		this.outputValID = outputValID;
		this.outputQFValID = outputQFValID;
		
		this.lookupList = lookupList;
		this.lookupMap = lookupMap;
	}
	

	/**
	 * @param inputActy - activity containing value to be tested
	 * @param inputValID - ValID of input to be tested
	 * @param outputValTypeID - ValTypeID of output to be created
	 * @param outputValID - ValID of output value to be created
	 * @param outputQFValID - ValID of QA/QC value to be created
	 * @param lookupList - a list of lookup values (should be input as Strings)
	 */
	public boolean runAlgorithmForList() {
		if (inputActy == null || lookupList == null) {
			log.error("Invalid input for ValidationLookupQAQC - no algorithm will be run");
			return false;
		}
		
		// find the input value String, which can be null or empty.
		// inputCanBeNull determines whether null/empty string is valid or invalid.
		String inputString = inputActy.findValueStringByValId(inputValID);
		String outputString = null;
		boolean isValid = false;
		if ( (inputString == null) || (inputString.isEmpty()) ) {
			if ( inputCanBeNull ) isValid = true;
		} else {
			// have a valid, non-null, non-empty input string, 
			// now validate from lookup list
			for ( String s : lookupList ) {
				if ( s.equalsIgnoreCase(inputString) ) {
					isValid = true;
					outputString = inputString;
					break;
				}
			}
		}
		
		// we now know if inputString is valid or invalid
		// if valid, outputQF = 0.0, otherwise outputQF = 1.0
		double outputQF = 1.0;
		if ( isValid ) outputQF = 0.0;
		
		// now we have out output value and quality flag, see if we can find where to put it

		// find the output activity associated with the input activity
		DPMSMethActivity outputActy = inputActy.getLxReference();
		// find the outputMethStream to which new Readout and Value should be added
		DPMSMethStreamData msd = outputActy.findMethStreamByValTypeID(outputValTypeID);
		if ( msd == null ) {
			// this is a DPMS Error, not invalid input data
			log.error("ValidationLookupQAQC could not find output MethStream - algorithm terminated unsuccessfully");
			return false;
		}
		
		// always output both a value and a quality flag
		// get what we need to create a new Readout and Value
		Date startDate = inputActy.getStartDate();
		Date endDate = inputActy.getEndDate();
		
		// now add both the data value and the quality flag to the output MethStream
		msd.addReadoutValueString(startDate, endDate, outputValID, outputString);
		msd.addReadoutValue(startDate, endDate, outputQFValID, outputQF);

		return true;
	}
	

	/**
	 * @param inputActy - activity containing value to be tested
	 * @param inputValID - ValID of input to be tested
	 * @param outputValTypeID - ValTypeID of output to be created
	 * @param outputValID - ValID of output value to be created
	 * @param outputQFValID - ValID of QA/QC value to be created
	 * @param lookuMap - a Map of lookup values (should be input as Strings)
	 */
	public boolean runAlgorithmForMap() {
		if (inputActy == null || lookupMap == null) {
			log.error("Invalid input for ValidationLookupQAQC - no algorithm will be run");
			return false;
		}
		
		// find the input value String, which can be null or empty.
		// inputCanBeNull determines whether null/empty string is valid or invalid.
		String inputString = inputActy.findValueStringByValId(inputValID);
		boolean isValid = false;
		String outputString = null;
		if ( (inputString == null) || (inputString.isEmpty()) ) {
			if ( inputCanBeNull ) isValid = true;
		} else {
			// have a valid, non-empty, non-null string
			// try to get new output string from Map
			try {
				isValid = true;
				outputString = lookupMap.get(inputString);
			} catch (Exception e) {
				// nothing to do here, leave isValid false and outputString null
			}
		}
		
		// we now know if inputString is valid or invalid
		// if valid, outputQF = 0.0, otherwise outputQF = 1.0
		double outputQF = 1.0;
		if ( isValid ) outputQF = 0.0;
		
		// now we have out output value and quality flag, see if we can find where to put it

		// find the output activity associated with the input activity
		DPMSMethActivity outputActy = inputActy.getLxReference();
		// find the outputMethStream to which new Readout and Value should be added
		DPMSMethStreamData msd = outputActy.findMethStreamByValTypeID(outputValTypeID);
		if ( msd == null ) {
			// this is a DPMS Error, not invalid input data
			log.error("ValidationLookupQAQC could not find output MethStream - algorithm terminated unsuccessfully");
			return false;
		}
		
		// always output both a value and a quality flag
		// get what we need to create a new Readout and Value
		Date startDate = inputActy.getStartDate();
		Date endDate = inputActy.getEndDate();
		
		// now add both the data value and the quality flag to the output MethStream
		msd.addReadoutValueString(startDate, endDate, outputValID, outputString);
		msd.addReadoutValue(startDate, endDate, outputQFValID, outputQF);

		return true;
	}
	
}
