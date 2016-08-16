package org.neoninc.dpms.algorithms.fsu;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * This class implements algorithm defined in 5.1.2 - 5 of NEON.DOC.001244.
 * @author sgui, May 29, 2014
 */
public class CompleteTrapCoordQF {
	// Instantiates logger instance
	static private Logger log = Logger.getLogger(CompleteTrapCoordQF.class);

	// value Id of trap coordinate
	private Long trapCoordValId;  
	
	// value Id of trapCoordinateQF
	private Long invalidTrapCoordQFValId; 
	
	// value Id of trap status
	private Long trapStatusValId;   
	
	// value Id of trapStatusQF
	private Long invalidTrapStatusQFValId;  

	// value Id of completeTrapCoordinateQF
	private Long completeTrapCoordQFValId;  
	
	// ValTypeID of completeTrapCoordinateQF
	private Long completeTrapCoordQFValTypeId;   
	
	/**
	 * Constructor to instantiate field values.
	 * 
	 * @param trapCoordValId    - Long L0 value Id for trapCoordinate
	 * @param trapStatusValId   - Long L0 value Id for trapStatus
	 * @param completeTrapCoordQFValId - Long L1 value Id for completeCoordinateQF
	 * @param completeTrapCoordQFValTypeId - Long L1 ValTypeID for trapCoordinate 
	 */
	public CompleteTrapCoordQF(Long trapCoordValId, Long invalidTrapCoordQFValId,
			                        Long trapStatusValId, Long invalidTrapStatusQFValId,
			                        Long completeTrapCoordQFValId, 
			                        Long completeTrapCoordQFValTypeId
			) {
		this.trapCoordValId       = trapCoordValId;
		this.invalidTrapCoordQFValId       = invalidTrapCoordQFValId;
		this.trapStatusValId      = trapStatusValId;
		this.invalidTrapStatusQFValId       = invalidTrapStatusQFValId;
		this.completeTrapCoordQFValId = completeTrapCoordQFValId;
		this.completeTrapCoordQFValTypeId    = completeTrapCoordQFValTypeId;
	}
	
	/**
	 * This method runs algorithm to generate completeTrapCoordinateQF flag.
	 * 
	 * @param mActy: Meth activity for output meth streams.
	 * @return boolean: true if this algorithm runs fine. Otherwise, it returns false.
	 */
	public boolean runAlgorithm(DPMSMethActivity mActy) {

		if (mActy == null) {
			log.error("Invalid input for CompleteTrapCoordQF - no algorithm will be run!");
			return false;
		}
		DPMSMethStreamData msd = mActy.findMethStreamByValTypeID(completeTrapCoordQFValTypeId);
		if ( msd == null ) {
			log.error("Missing MethStream for CompleteTrapCoordQF - no algorithm will be run!");
			return false;
		}
		
		String trapCoordinate = mActy.findValueStringByValId(trapCoordValId);
		Double trapCoordQF = mActy.findValueByValId(invalidTrapCoordQFValId);
		String trapStatus = mActy.getLxReference().findValueStringByValId(trapStatusValId);  // trapStatus is in L0 only!!!
		Double trapStatusQF = mActy.findValueByValId(invalidTrapStatusQFValId);
		
		// populates initial value with zero [5.1.2 - 5.a (see NEON.DOC.001244)] 
		Double compTrapCoordQF = 0.0;  // zero means ok, 1 means not ok, -1 means other QFs not ok.
		
		if ( (trapCoordQF == null) || (trapCoordQF.compareTo(0.0) > 0) ||
			 (trapStatusQF == null) || (trapStatusQF.compareTo(0.0) > 0) ) {
			compTrapCoordQF = -1.0;
		} else {	
			// implementation of 5.1.2 - 5.b.i.A (see NEON.DOC.001244)
			if( (trapCoordinate == null) || trapCoordinate.isEmpty() ) {
				if( (trapStatus != null) && (! trapStatus.trim().equals("1") && ! trapStatus.trim().equals("6")) ) {
					compTrapCoordQF = 1.;
				}
			}		
		}
		msd.addReadoutValue(mActy.getStartDate(), mActy.getEndDate(), completeTrapCoordQFValId, compTrapCoordQF);
		return true;
	}

}
