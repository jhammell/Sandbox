package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * The test checks whether a "value" of "field id" in a method activity 
 * is matching one of the patterns provided.
 * After done checking, attach flag value(1-fail) to the tested readout 
 * 
 * @author lzhang
 */
public class ValidationRegExQAQC {
	
	static private Logger log = Logger.getLogger(ValidationRegExQAQC.class);
	
	private Long fieldValId; // value id need to be tested
	private DPMSMethActivity inputActy;
	private Long qaQcValId; //quality flag id
	private ArrayList <String> regExList; // a list of regular expression to test against

	/**
	 * @param fieldId - value id need to be tested for "range"
	 * @param inputActy - activity needs to be tested and attach the QFs to
	 * @param qaQcValId - QA/QC value id
	 * @param regExList - a list of patterns the value tested should follow
	 */
	public ValidationRegExQAQC(Long fieldId, DPMSMethActivity inputActy, Long qaQcValId,
			ArrayList<String> regExList) {
		this.fieldValId = fieldId;
		this.inputActy = inputActy;
		this.qaQcValId = qaQcValId;
		this.regExList = regExList;
	}
	
	public boolean runAlgorithm(){
		if (inputActy == null || regExList == null) {
			log.error("Invalid input for validationRegExQAQC - no algorithm will be run");
			return false;
		}
		
		double qaVal;
		
		//find method stream by given value id in the method activity
		DPMSMethStreamData ms = inputActy.findMethStreamByValId(fieldValId);
		if( ms == null) {
			log.error("Input Activity does not include field "+fieldValId+", no algorithm will be run.");
			return false;
		}
		//only readout with latest transaction will be checked
		DPMSMStreamReadout rdot = ms.findLatestReadout();
		//get string value to check
		String val = rdot.getValueStringForValueId(fieldValId);
		if(val == null) {
			log.debug("The value being tested is a NULL.");
			qaVal = -1.;
			rdot.setValueForValueId(qaQcValId, qaVal, true);
			return true;
		} 
		
		for(String pattern : regExList) {
			if(Pattern.matches(pattern, val)) {
				qaVal = 0.;
				log.debug("The value " + val + " matches pattern " + pattern);
				rdot.setValueForValueId(qaQcValId, qaVal, true);
				return true;
			}
		}
		qaVal = 1.;
		log.debug("The value " + val + " doesn't match any pattern provided.");
		rdot.setValueForValueId(qaQcValId, qaVal, true);
		return true;
	}
	
}
