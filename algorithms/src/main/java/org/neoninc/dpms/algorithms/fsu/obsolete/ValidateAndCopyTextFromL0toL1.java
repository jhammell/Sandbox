/**
 * 
 */
package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMStreamValue;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * @author bpenn
 * 
 * Description: This class looks for a string associated with L0 data and validates it, then copies it to the L1 data
 * 
 * input:
 * L0fieldList - list of L0 data fields
 * L0valId - valId of desired L0 data field
 * L1valId - valId of target L1 data field
 * L1fieldList - list of L1 data fields
 * L1valTypeId - val type of target L1 MethStream
 * L0inputActy - L0 activity
 * L1inputActy - l1 activity
 * 
 * output:
 * boolean - success is true; fail is false;
 *
 */
public class ValidateAndCopyTextFromL0toL1 {

	static private Logger log = Logger.getLogger(ValidateAndCopyTextFromL0toL1.class);
	
	private ArrayList<Long> L0fieldList; // a list of L0 value ids that need to be tested
	private ArrayList<Long> L1fieldList; // a list of L1 value ids that need to be tested
	private String trapCoordinate;
	private DPMSMethActivity L0inputActy;
	private DPMSMethActivity L1inputActy;
	private Long L0valId;
	private Long L1valId;
	private Long L1valTypeId;
	private ArrayList<String> LookupList;
	
	ValidateAndCopyTextFromL0toL1(ArrayList<Long> L0fieldList, Long L0valId, Long L1valId, ArrayList<Long> L1fieldList, Long L1valTypeId, DPMSMethActivity L0inputActy, DPMSMethActivity L1inputActy, ArrayList<String> LookupList){
		this.L0valId = L0valId;
		this.L1valId = L1valId;
		this.L0fieldList = L0fieldList;
		this.L1fieldList = L1fieldList;
		this.L1valTypeId = L1valTypeId;
		this.L0inputActy = L0inputActy;
		this.L1inputActy = L1inputActy;
		this.LookupList = LookupList;
	}
	
	public boolean runAlgorithm() {
		
		if (L1inputActy == null || L0inputActy == null) {
			log.error("Error: Invalid input for DuplicateRecordsQAQC - inputActy == null - no algorithm will be run");
			return false;
		}

		// Look for string associated with L0 valId in L0 input data.  Make sure it is valid.
		
		Double qaVal = 0.;
		boolean found = false;
		String valStr = null;
		for(Long fieldValId : L0fieldList) {
			DPMSMethStreamData ms = L0inputActy.findMethStreamByValId(fieldValId);
			if (ms != null){
				for(DPMSMStreamReadout rdot : ms.getMSReadouts()){
					for(DPMSMStreamValue val : rdot.getValues()){
						if (val.getParentValueId().equals(L0valId)){
							valStr = val.getValueString();
							if (LookupList.contains(valStr)) {
								found = true;
							}
						}
					}
				}
			}
		}
		if (!found){
			log.debug("Error: ValidateAndCopyTextFromL0toL1: L0 Input Activity does not include field "+L0valId+" or fieldstring is invalid..");
			return false;	
		}
		//copy and insert L0 string into L1 activity methStream
		found = false;
		for(DPMSMethStreamData ms  : L1inputActy.getMethStreams()) {
			if (ms.getMStreamValTypeID().equals(L1valTypeId)) {
				DPMSMStreamReadout rdot = new DPMSMStreamReadout();
				ms.getMSReadouts().add(rdot);
				DPMSMStreamValue val = new DPMSMStreamValue();
				val.setParentValueId(L1valId);
				val.setValueString(valStr);
				rdot.getValues().add(val);
				found = true;
			}
		}
		if (!found){
			log.debug("Error: ValidateAndCopyTextFromL0toL1: L1 Input Activity "+L1valId+" not found!");
			return false;	
		}
		else {
			log.debug("ValidateAndCopyTextFromL0toL1: L1 successfully updated with new text!");	
		}
	return true;
	}
}
