package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * Implementation of FSU ValidationTest: Data Type per section
 * 5.1.2.4 of NEON.DOC.001247 dated 05/14/2014.
 * Step A-B: Find the given input QaQcValID in the given input MethActivity.
 *		B.aa: If not found, add the given QaQcValID with value of 0 in the 
 *				given output MethStream.
 * Step D: Convert value of inputValID and if conversion is successful, 
 * 				set the converted value in the given output MethStream
 *				and return true.
 * Step D: If conversion is unsuccessful, 
 *      D.aa: set the value to -9999 in the given output MethStream and
 *      D.bb set the given Quality Flag value to 1 in the given output
 *				MethStream and return.
 * Returns false if inputActy or outputMethStream is null.
 * 
 */
public class ValidateDataTypeQAQC {
	
	static private Logger log = Logger.getLogger(ValidateDataTypeQAQC.class);
	
	private DPMSMethActivity mInputActy;
	private Long mInputValID;
	private DPMSMethStreamData mOutputMethStream; // the output MethStream
	private Long mOutputValID;
	private Long mQaQcValId;  // quality flag id for this test
	
	/**
	 * @param inputActy - the input MethActivity where inputValID should be found.
	 * @param inputValID - ValID for the input data String to be validated.
	 * @param mOutputMethStream - method stream in which to put converted data value and QA/AC flag.
	 * @param outputValID - ValID for the converted output value.
	 * @param qaQcValId - QA/QC value id.
	 * 
	 * For now, only implementation needed is conversion from input String to output unsigned int.
	 */
	public ValidateDataTypeQAQC(DPMSMethActivity inputActy, Long inputValID,
			DPMSMethStreamData outputMethStream, Long outputValID, Long qaQcValId) {
		this.mInputActy = inputActy;
		this.mInputValID = inputValID;
		this.mOutputMethStream = outputMethStream;
		this.mOutputValID = outputValID;
		this.mQaQcValId = qaQcValId;
	}
	
	/**
	 * Run the algorithm converting input String to output unsigned int.
	 * @return true if inputs are valid, otherwise false.
	 */
	public boolean runAlgorithmUnsignedInt(){
		if ( (this.mInputActy == null) || (this.mOutputMethStream == null) )  {
			log.error("Invalid input for ValidateDataTypeQAQC - no algorithm will be run");
			return false;
		}

		// Step D - find String version of data value and attempt to convert to unsigned integer
		String valString = mInputActy.findValueStringByValId(mInputValID);
		try {
			Integer intValue = new Integer(valString);
			if ( intValue >= 0 ) {
				// success, we have a valid unsigned integer value
				mOutputMethStream.addReadoutValue(mInputActy.getStartDate(), mInputActy.getEndDate(),
						mOutputValID, intValue.doubleValue());
				// Step B.aa - set the QF to zero
				mOutputMethStream.addReadoutValue(mInputActy.getStartDate(), mInputActy.getEndDate(),
						mQaQcValId, 0.0);
				return true;
			} else {
				// Step D - String could not be converted to unsigned integer
				mOutputMethStream.addReadoutValue(mInputActy.getStartDate(), mInputActy.getEndDate(),
						mOutputValID, -9999.);
				mOutputMethStream.addReadoutValue(mInputActy.getStartDate(), mInputActy.getEndDate(),
						mQaQcValId, 1.0);
			}
		} catch (Exception e) {
			// Step D - String could not be converted to Integer
			mOutputMethStream.addReadoutValue(mInputActy.getStartDate(), mInputActy.getEndDate(),
					mOutputValID, -9999.);
			mOutputMethStream.addReadoutValue(mInputActy.getStartDate(), mInputActy.getEndDate(),
					mQaQcValId, 1.0);
		}

		return true;
	}
	
}
