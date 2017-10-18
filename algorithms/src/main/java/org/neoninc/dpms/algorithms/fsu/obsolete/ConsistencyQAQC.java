package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * This class conducts consistency test, assuming invalid value tests have been passed.
 * @sgui,    June 6, 2014
 */
public class ConsistencyQAQC {
	// instance of Logger
	static private Logger log = Logger.getLogger(ConsistencyQAQC.class);
	
	// A list of value Ids for consistency test
	private ArrayList<Long> fieldList; 
	
	// A list of DPMSMethActivity to be tested for consistency.
	private ArrayList<DPMSMethActivity> actyList;
	
	// a new meth stream to hold QA flag
	private DPMSMethStreamData qaQcMethStream; 
	
	// quality flag id for this test
	private Long qaQcValId;  

	// placeholder for concatenated fields 
    HashSet <String> hSet = new HashSet <String>();

	/**
	 * @param actyList - list of DPMSMethActivity to be tested for consistency
	 * @param fieldList - list of value ids (such as tagIDValId, sexValId, taxonIDValId, 
	 *        and lifeStageValId for small mammal) for consistency test
	 * @param qaQcMethStream - method stream to put consistency QA/AC values
	 * @param qaQcValId - QA/QC value id for consistency
	 */
	public ConsistencyQAQC(ArrayList<DPMSMethActivity> actyList,  ArrayList<Long> fieldList, DPMSMethStreamData qaQcMethStream, Long qaQcValId) {
		this.actyList = actyList;
		this.fieldList = fieldList;
		this.qaQcMethStream = qaQcMethStream;
		this.qaQcValId = qaQcValId;
	}
	
	/**
	 * This method runs algorithm to check data consistency.
	 * 
	 * @return boolean: true if this algorithm runs fine. Otherwise, it returns false.
	 */
	public boolean runAlgorithm() {
		boolean result = false;
		if (actyList == null || actyList.size() == 0 || fieldList == null || fieldList.size() == 0) {
			log.error("Invalid input for ConsistencyQAQC - no algorithm will be run!");
			return result;
		}

		result = testConsistency();

		return result;
	}
	
	/**
	 * This method conducts consistency test
	 * @return boolean: true if consistency test is successful. Otherwise, false.
	 */
	private boolean testConsistency() {
		boolean result = false;
		Double qaVal = 0.;

		DPMSMStreamReadout newRdot = new DPMSMStreamReadout();
		qaQcMethStream.getMSReadouts().add(newRdot);
	
		try {
			for(DPMSMethActivity mActy : actyList) {
				String concatStr = "";
				for(Long fieldValId : fieldList) {
					DPMSMethStreamData ms = mActy.findMethStreamByValId(fieldValId);
					if(ms != null) {
						DPMSMStreamReadout rdot = ms.findLatestReadout();
						if(rdot != null) {
							concatStr = concatStr + rdot.getValueStringForValueId(fieldValId).trim();
						} else {
							// What to do?
							log.debug("Readout is null!");
						}	
					} else {
						// What to do?
						log.debug("DPMSMethStreamData is null!");
					}
				}
				hSet.add(concatStr);		
			}
			result = true;
			if(hSet.size() > 1) {
				qaVal = 1.0;
			}
			newRdot.setValueForValueId(qaQcValId, qaVal, true);
		} catch(Exception ex) {
			result = false;		
			log.error("Exception happened: " + String.valueOf(ex));
		}
	
		return result;
	}
}

