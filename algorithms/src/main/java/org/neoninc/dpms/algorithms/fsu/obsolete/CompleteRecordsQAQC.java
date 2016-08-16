package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * changed to check L1 method activity, by passing in a list of quality flags which the 
 * corresponding value can not be null, the any of the flags shows the input value was 
 * not valid, then the complete record flag will be raised.
 * 
 * //check if the record is complete by go through a list of value ids, find corresponding 
 * //values in input L0 method activity, make sure no value is null, return a new method 
 * //stream holding the flag, (which may be attached to L1 activity)
 *
 * @author lzhang
 */
public class CompleteRecordsQAQC {
	
	static private Logger log = Logger.getLogger(CompleteRecordsQAQC.class);
	
	private ArrayList<Long> qfList; // a list of value ids need to be tested
	private DPMSMethActivity inputActy;
	private Long qaQcValId;  // quality flag id for this test
	private DPMSMethStreamData qaQcMethStream; // a new meth stream to hold QA flag
	
	/**
	 * @param qfList - list of value ids need to be tested for complete records
	 * @param inputActy - activity needs to be tested
	 * @param qaQcMethStream - method stream to put QA/AC values
	 * @param qaQcValId - QA/QC value id
	 */
	public CompleteRecordsQAQC(ArrayList<Long> qfList, DPMSMethActivity inputActy, DPMSMethStreamData qaQcMethStream, Long qaQcValId) {
		this.qfList = qfList;
		this.inputActy = inputActy;
		this.qaQcMethStream = qaQcMethStream;
		this.qaQcValId = qaQcValId;
	}
	
	
	public boolean runAlgorithm(){
		if (inputActy == null) {
			log.error("Invalid input for CompleteRecordsQAQC - no algorithm will be run");
			return false;
		}

		Date startDate = inputActy.getStartDate();
		Date endDate = inputActy.getEndDate();

		Double qaVal = 0.;
		for(Long qfValId : qfList) {
//			DPMSMethStreamData ms = inputActy.findMethStreamByValId(qfValId);
//			if( ms == null) {
//				qaVal = 1.;
//				qaQcMethStream.addReadoutValue(startDate, endDate, qaQcValId, qaVal);
//				log.debug("Input Activity does not include field "+qfValId+", record not complete.");
//				return true;
//			}
//			DPMSMStreamReadout rdot = ms.findLatestReadout();
//			String valStr = rdot.getValueStringForValueId(qfValId);
//			if(valStr == null || valStr.equalsIgnoreCase("NA") ||
//					valStr.equalsIgnoreCase("N/A") ||
//					valStr.equalsIgnoreCase("NAN") ||
//					valStr.equalsIgnoreCase("nodata") ) {
			if(inputActy.findValueByValId(qfValId) == 1.) {
				qaVal = 1.;
				log.debug("NA input for field"+qfValId+", record not complete");
				qaQcMethStream.addReadoutValue(startDate, endDate, qaQcValId, qaVal);
				return true;
			}
		}
//		System.out.println(qaVal);
		qaQcMethStream.addReadoutValue(startDate, endDate, qaQcValId, qaVal);
		log.debug("Record is complete!");
		return true;
	}
	
}
