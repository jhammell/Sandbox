package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * This class implements algorithm defined in 5.1.2 - 5 of NEON.DOC.001244.
 * @author sgui, May 29, 2014
 */
public class CompleteTrapCoordFateQFs {
	// Instantiates logger instance
	static private Logger log = Logger.getLogger(CompleteTrapCoordFateQFs.class);
	
	// input meth activity
	private DPMSMethActivity inputActy;

	// value Id of trap coordinate
	private Long trapCoordValId;  
	
	// value Id of trap status
	private Long trapStatusValId;  

	// value Id of fate
	private Long fateValId;  

	// value Id of taxonID
	private Long taxonIDValId;  

	// value Id of completeTrapCoordinateQF
	private Long compTrapCoordQFValId;  

	// value Id of completeFateQF
	private Long compFateQFValId;  

	// value Id of trap coordinate
	private Long pubTrapCoordValId;  

	// value Id of fate
	private Long pubFateValId;  

	// a new meth stream to hold L1 values of trapCoordinate and completeTrapCoordinateQF
	private DPMSMethStreamData trapCoordMethStream; 
	
	// a new meth stream to hold L1 values of fate and QA completeTrapFateQF
	private DPMSMethStreamData fateMethStream; 
	
	/**
	 * Constructor to instantiate field values.
	 * 
	 * @param inputActy - activity needs to be tested
	 * @param trapCoordMethStream - method stream to hold L1 values of trapCoordinate 
	 *              and completeTrapCoordinateQF flag
	 * @param fateMethStream    - method stream to hold L1 values of fate and completeFateQF flag
	 * @param trapCoordValId    - Long L0 value Id for trapCoordinate
	 * @param trapStatusValId   - Long L0 value Id for trapStatus
	 * @param fateValId         - Long L0 value Id for fate
	 * @param taxonIDValId      - Long L0 value Id for taxonID
	 * @param compTrapCoordQFValId - Long L1 value Id for completeCoordinateQF
	 * @param compFateQFValId   - Long L1 value Id for competeFateQF
	 * @param pubTrapCoordValId - Long L1 value Id for trapCoordinate 
	 * @param pubFateValId      - Long L1 value Id for fate
	 */
	public CompleteTrapCoordFateQFs(DPMSMethActivity inputActy, 
			                        DPMSMethStreamData trapCoordMethStream, 
			                        DPMSMethStreamData fateMethStream,
			                        Long trapCoordValId,
			                        Long trapStatusValId,
			                        Long fateValId,
			                        Long taxonIDValId,
			                        Long compTrapCoordQFValId, 
			                        Long compFateQFValId,
			                        Long pubTrapCoordValId,
			                        Long pubFateValId
			) {
		this.inputActy = inputActy;
		this.trapCoordMethStream  = trapCoordMethStream;
		this.fateMethStream       = fateMethStream;
		this.trapCoordValId       = trapCoordValId;
		this.trapStatusValId      = trapStatusValId;
		this.fateValId            = fateValId;
		this.taxonIDValId         = taxonIDValId;
		this.compTrapCoordQFValId = compTrapCoordQFValId;
		this.compFateQFValId      = compFateQFValId;
		this.pubTrapCoordValId    = pubTrapCoordValId;
		this.pubFateValId         = pubFateValId;
	}
	
	/**
	 * This method runs algorithm to generate completeTrapCoordinateQF and
	 * completeFateQF flags.
	 * 
	 * @return boolean: true if this algorithm runs fine. Otherwise, it returns false.
	 */
	public boolean runAlgorithm() {
		boolean resultTrapCoord = false;
		boolean resultFate = false;
		if (inputActy == null) {
			log.error("Invalid input for CompleteTrapCoordFateQFs - no algorithm will be run!");
			return false;
		}

		// for completeTrapCoordinateQF
		resultTrapCoord = generateCompTrapCoordQF(this.inputActy); 
		if(! resultTrapCoord) {
			log.error("The algorithm for generating completeTrapCoordinateQF did NOT run successfully!");
		}

		// for completeFateQF
		resultFate = generateCompFateQF(this.inputActy); 
		if(! resultFate) {
			log.error("The algorithm for generating completeFateQF did NOT run successfully!");
		}
		
		return resultTrapCoord && resultFate;
	}
	
	/**
	 * This method generates completeTrapCoordinateQF flag.
	 * 
	 * @param mActy: Meth activity for input meth streams.
	 * @return boolean: true if this method runs fine. Otherwise, it returns false.
	 */
	private boolean generateCompTrapCoordQF(DPMSMethActivity mActy) {
		boolean result = false;
		String trapCoordinate = null;
		String trapStatus = null;
		// populates initial value with zero [5.1.2 - 5.a (see NEON.DOC.001244)] 
		Double compTrapCoordQF = 0.0;
		
		try {	
			trapCoordinate = mActy.findValueStringByValId(trapCoordValId);
			trapStatus = mActy.findValueStringByValId(trapStatusValId); 
			// implementation of 5.1.2 - 5.b.i.A (see NEON.DOC.001244)
//			log.debug("trapStatus.trim(): '" + trapStatus.trim() + "'");
			if(trapCoordinate == null) {
//				log.debug("Reached here!!!");
				if(trapStatus != null && (! trapStatus.trim().equals("1") && ! trapStatus.trim().equals("6"))) {
					compTrapCoordQF = 1.;
				}
			}		
			// The completeTrapCoordinateQF algorithm runs successfully.
			result = true;
		} catch(Exception e) {
			// deals with situation that wrong value Ids are provided.
			log.error("Check whether trapCoordValId and trapStatusValId (" + trapCoordValId + "/" + trapStatus + ")! " + String.valueOf(e));
			result = false;
		}
		
		DPMSMStreamReadout newRdot = new DPMSMStreamReadout();
		trapCoordMethStream.getMSReadouts().add(newRdot);
	
		String trapCoordStr = mActy.findValueStringByValId(trapCoordValId);
		newRdot.setValueStringForValueId(pubTrapCoordValId, trapCoordStr, true);
		newRdot.setValueForValueId(compTrapCoordQFValId, compTrapCoordQF, true);
		newRdot.setReadoutTranTime(new Date());

		return result;	
	}

	/**
	 * This method generates completeFateQF flag.
	 * 
	 * @param mActy: Meth activity for input meth streams.
	 * @return boolean: true if this method runs fine. Otherwise, it returns false.
	 */
	private boolean generateCompFateQF(DPMSMethActivity mActy) {
		boolean result = false;
		String fate = "";
		String taxonID = "";
		// populates initial value with zero [5.1.2 - 5.a (see NEON.DOC.001244)] 
		Double compFateQF = 0.0;
		
		try {	
			fate = mActy.findValueStringByValId(fateValId);
			log.debug("-- taxonIDValId: '" + taxonIDValId + "'");
			taxonID = mActy.findValueStringByValId(taxonIDValId); 
			// implementation of 5.1.2 - 5.c.i.A (see NEON.DOC.001244)
			if(fate == null) {
				log.debug("REACHED HERE!!!  " + taxonID);
				if(taxonID != null) {
					compFateQF = 1.;
				}
			}		
			// The completeFateQF algorithm runs successfully.
			result = true;
		} catch(Exception e) {
			// deals with situation that wrong value Ids are provided.
			log.error("Check whether fateValId and taxonIDValId (" + fateValId + "/" + taxonIDValId + ")! " + String.valueOf(e));
			result = false;
		}
		
		DPMSMStreamReadout newRdot = new DPMSMStreamReadout();
		fateMethStream.getMSReadouts().add(newRdot);

		String fateStr = mActy.findValueStringByValId(fateValId);
		newRdot.setValueStringForValueId(pubFateValId, fateStr, true);
		newRdot.setValueForValueId(compFateQFValId, compFateQF, true);
		newRdot.setReadoutTranTime(new Date());

		return result;	
	}

}
