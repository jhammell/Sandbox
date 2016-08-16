package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMStreamValue;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

public class IdQualFieldsQAQC {

	static private Logger log = Logger.getLogger(IdQualFieldsQAQC.class);
	
	private ArrayList<Long> fieldList; // a list of value ids need to be tested
	private DPMSMethActivity inputActy;
	private ArrayList<String> lookupTable;
	private Long qaQcValId;  // meth stream id for this test
	private DPMSMethStreamData qaQcMethStream; // a new meth stream to hold QA flag
	private Long Msfield = 163L;
	private Long targetValId;
	
	/**
	 * Description: 
	 * 
	 * Verify that certain values in mam_capturedata_db are found in corresponding lookup table.  Only looking for existence in
	 * mam_capturedata_db
	 *
	 * 
	 * Parameters:
	 * 
	 * Input:
	 * @param fieldList - list of value ids to be tested for duplicate records
	 * @param inputActy - activity to be tested - contains only codes that exist in lookupTable
	 * @param lookupTable - List of eligible lookup table values.
	 * @param qaQcValId - QA/QC value id
	 * 
	 * Output:
	 * @param qaQcMethStream - method stream containing QA/AC values
	 */
	
	public IdQualFieldsQAQC(ArrayList<Long> fieldList, DPMSMethActivity inputActy, ArrayList<String> lookupTable, Long targetValId, Long Msfield, DPMSMethStreamData qaQcMethStream,Long qaQcValId) {
		this.fieldList = fieldList;
		this.inputActy = inputActy;
		this.qaQcMethStream = qaQcMethStream;
		this.qaQcValId = qaQcValId;
	    this.lookupTable = lookupTable;
	    this.targetValId = targetValId;
	    this.Msfield  = Msfield;
	}
	
	
	public boolean runAlgorithm(){
		if (inputActy == null) {
			log.error("Invalid input for VerifyFieldAreValidQAQC - inputActy == null - no algorithm will be run");
			return false;
		}

		DPMSMStreamReadout newRdot = new DPMSMStreamReadout();
		qaQcMethStream.getMSReadouts().add(newRdot);

		/*
		 * Loop over all elements of the field list and look for entries in method activities that match current lookup table entries 
		 */
		Double qaVal = 0.;
		String valStr = null;
		for(Long fieldValId : fieldList) {
			if (fieldValId.equals (Msfield)) {  //is this the right field?
				DPMSMethStreamData ms = inputActy.findMethStreamByValId(targetValId);  //get Method Stream Data for one activity, i.e., column in spread sheet
				if(ms != null){
					for(DPMSMStreamReadout rdot : ms.getMSReadouts()){
						ArrayList<DPMSMStreamValue> vals = rdot.getValues();
						for (DPMSMStreamValue v : vals) {
							if(v.getParentValueId().equals(targetValId)) {
								valStr = v.getValueString();
								if (lookupTable.contains(valStr)) {
									System.out.println("Value for "+valStr+" found in lookup table!");
									qaVal = 1.;
									log.debug("Field matches known value found for "+fieldValId);
									newRdot.setValueForValueId(qaQcValId, qaVal, true);	
								}								
							}
						}
					}
				}
			}
		}
		newRdot.setValueForValueId(qaQcValId, qaVal, true);
		System.out.println(qaVal);
		if (qaVal == 0) log.debug("No values Found for lookup table search!");
		return true;
	}

}
