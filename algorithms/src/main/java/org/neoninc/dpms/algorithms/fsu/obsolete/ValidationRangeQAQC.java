package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * The test checks whether a "value" of "field id" in a method activity 
 * is within the range of minimum and maximum provided.
 * After done checking, attach flag value(1-fail) to the tested readout 
 * 
 * @author lzhang
 */
public class ValidationRangeQAQC {
	
	static private Logger log = Logger.getLogger(ValidationRangeQAQC.class);
	
	private Long fieldValId; // value id need to be tested
	private DPMSMethActivity inputActy;
	private Long qaQcValId; //quality flag id 
	private Double minValue;
	private Double maxValue;

	/**
	 * @param fieldId - value id need to be tested for "range"
	 * @param inputActy - activity needs to be tested and attach the QFs to
	 * @param qaQcValId - QA/QC value id
	 * @param minValue - minimum value of the range
	 * @param maxValue - maximum value of the range
	 */
	public ValidationRangeQAQC(Long fieldId, DPMSMethActivity inputActy, Long qaQcValId,
			Double minValue, Double maxValue) {
		this.fieldValId = fieldId;
		this.inputActy = inputActy;
		this.qaQcValId = qaQcValId;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	
	public boolean runAlgorithm(){
		if (inputActy == null || (minValue == null && maxValue == null)) {
			log.error("Invalid input for ValidationRangeQAQC - no algorithm will be run");
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
		//get value (either a String or a Number) to check
		Double val = rdot.getValueForValueId(fieldValId);
		if(val != null) {
			if( (minValue==null || (minValue!=null && val>=minValue)) && 
					(maxValue==null || (maxValue!=null && val<=maxValue)) ){
				qaVal = 0.;
				log.debug("Value " + val + " is within the given range.");
			} else {
				qaVal = 1.;
				log.debug("Value " + val + " is NOT within the given range.");
			}
			rdot.setValueForValueId(qaQcValId, qaVal, true);
			return true;
		} else {
			String valStr = rdot.getValueStringForValueId(fieldValId);
			if(valStr != null) {
				try {
					Double valNum = Double.parseDouble(valStr);
					if( (minValue==null || (minValue!=null && valNum>=minValue)) && 
							(maxValue==null || (maxValue!=null && valNum<=maxValue)) ){
						qaVal = 0.;
						log.debug("Value " + valNum + " is within the given range.");
					} else {
						qaVal = 1.;
						log.debug("Value " + valNum + " is NOT within the given range.");
					}
					rdot.setValueForValueId(qaQcValId, qaVal, true);
					return true;
				} catch (Exception e) {
					e.getMessage();
					return false;
				}
			}
		}
		return true;
	}
	
}
