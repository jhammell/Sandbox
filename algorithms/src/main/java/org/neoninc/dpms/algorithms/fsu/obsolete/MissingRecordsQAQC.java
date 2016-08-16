package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * check if there are less records(activity) than defined number with 
 * "unique key" (e.g. plotID+date), flag will be attached to the 
 * current Level-1 method activity with a new method stream
 */
public class MissingRecordsQAQC {
	
	static private Logger log = Logger.getLogger(MissingRecordsQAQC.class);
	
	private ArrayList<DPMSMethActivity> inputActyList;
	private Long qaQcValId;  // quality flag id for missing records
	private Long qaQcValTypeId;
	private int numLimit;
	
	/**
	 * @param inputActyList - list of activities need to be tested and attached flag with
	 * @param qaQcValId - QA/QC value id
	 * @param numLimit - number limit for records
	 */
	public MissingRecordsQAQC(Long qaQcValId, Long qaQcValTypeId, int numLimit,	ArrayList<DPMSMethActivity> inputActyList) {
		this.inputActyList = inputActyList;
		this.qaQcValId = qaQcValId;
		this.qaQcValTypeId = qaQcValTypeId;
		this.numLimit = numLimit;
	}
	
	
	/**
	 * check missing records by unique key (grouping variable)
	 * eg. for each plotId+date, has to have at least "numlimit" of records
	 * 
	 * @return
	 */
	public boolean runAlgorithm(){
		if (inputActyList == null) {
			log.error("Invalid input for missingRecordsQAQC - no algorithm will be run");
			return false;
		}

		HashMap<String, Integer> keyCounts = new HashMap<String, Integer>();
		Double qaVal = 0.;
		for(DPMSMethActivity acty : inputActyList) {
			String dupKey = findUniqueKey(acty);
			checkCountsforDupKey(dupKey, keyCounts);
		}
		for(DPMSMethActivity acty : inputActyList) {
			String dupKey = findUniqueKey(acty);
			int numCount = keyCounts.get(dupKey);
			System.out.println(numCount);
			if (numCount < numLimit) {
				qaVal = 1.;
			} else {
				qaVal = 0.;
			}
			acty.findMethStreamByValTypeID(qaQcValTypeId).addReadoutValue(acty.getStartDate(), acty.getEndDate(), qaQcValId, qaVal);

			//
//			System.out.println("qaVal= "+qaVal+"  Value="+acty.findValueByValId(qaQcValId));
		}
		return true;
	}
	
	public boolean runAlgorithmOnlyCountNights(){
		if (inputActyList == null) {
			log.error("Invalid input for missingRecordsQAQC - no algorithm will be run");
			return false;
		}
		HashMap<String, Integer> keyCounts = new HashMap<String, Integer>();
		Double qaVal = 0.;
		for(DPMSMethActivity acty : inputActyList) {
			String uniqKey = findUniqueKey(acty);
			checkCountsforDupKey(uniqKey, keyCounts);
		}
		int numCount = keyCounts.size();
		System.out.println("num= "+numCount);
		for(DPMSMethActivity acty : inputActyList) {
			if (numCount < numLimit) {
				qaVal = 1.;
			} else {
				qaVal = 0.;
			}
			DPMSMethStreamData qaQcMethStream = acty.findMethStreamByValTypeID(qaQcValTypeId);
			qaQcMethStream.addReadoutValue(acty.getStartDate(), acty.getEndDate(), qaQcValId, qaVal);
			//
//			System.out.println(" qaVal= "+qaVal+"  Value="+acty.findValueByValId(qaQcValId));
//			System.out.println();
		}

		return true;
	}
	
	/**
	 * find unique key by plotID+date
	 * 
	 * @param acty
	 * @return
	 */
	public String findUniqueKey(DPMSMethActivity acty) {
		String plotId = acty.getLocnInfo().getPlotID();
		Long startDate = acty.getStartDate().getTime();
		String dupKey = plotId+startDate.toString();
		return dupKey;
	}
	
	/**
	 * @param dupKey
	 * @param keyCounts
	 */
	public void checkCountsforDupKey(String dupKey, HashMap<String, Integer> keyCounts) {
		if(keyCounts.get(dupKey) == null) {
			keyCounts.put(dupKey, 1);
		} else {
			keyCounts.put(dupKey, keyCounts.get(dupKey)+1);
		}		
	}
	
}
