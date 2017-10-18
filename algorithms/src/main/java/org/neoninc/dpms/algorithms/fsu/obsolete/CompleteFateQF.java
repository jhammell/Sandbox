package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * This class implements algorithm defined in 5.1.2 - 5 of NEON.DOC.001244.
 * @author sgui, May 29, 2014
 */
public class CompleteFateQF {
	// Instantiates logger instance
	static private Logger log = Logger.getLogger(CompleteFateQF.class);

	// value Id of fate
	private Long fateValId;  
	
	// value Id of invalidFateQF
	private Long invalidFateQFValId;

	// value Id of taxonID
	private Long taxonIDValId;  
	
	// value Id of taxonID
	private Long invalidTaxonIDQFValId;

	// value Id of completeFateQF
	private Long completeFateQFValId;  

	// ValTypeId of CompleteFateQF
	private Long completeFateQFValTypeId;
	
	/**
	 * Constructor to instantiate field values.
	 * 
	 * @param fateValId         - Long L1 value Id for fate
	 * @param invalidFateQFValId - Long L1 valID for invalidFateQF
	 * @param taxonIDValId      - Long L1 value Id for taxonID
	 * @param invalidTaxonIDQFValId      - Long L1 value Id for invalidTaxonIDQFValId
	 * @param completeFateQFValId   - Long L1 value Id for competeFateQF
	 * @param completeFateQFValTypeId      - Long L1 ValTypeID for competeFateQF
	 */
	public CompleteFateQF( Long fateValId, Long invalidFateQFValId,
			               Long taxonIDValId, Long invalidTaxonIDQFValId,
			               Long completeFateQFValId,
			               Long completeFateQFValTypeId
			) {
		this.fateValId            = fateValId;
		this.invalidFateQFValId   = invalidFateQFValId;
		this.taxonIDValId         = taxonIDValId;
		this.invalidTaxonIDQFValId    = invalidTaxonIDQFValId;
		this.completeFateQFValId      = completeFateQFValId;
		this.completeFateQFValTypeId  = completeFateQFValTypeId;
	}
	
	/**
	 * This method runs algorithm to generate completeFateQF flag.
	 * 
	 * @param mActy: Meth activity for output meth streams.
	 * @return boolean: true if this algorithm runs fine. Otherwise, it returns false.
	 */
	public boolean runAlgorithm(DPMSMethActivity mActy) {

		if (mActy == null) {
			log.error("Invalid input for CompleteFateQF - no algorithm will be run!");
			return false;
		}
		DPMSMethStreamData msd = mActy.findMethStreamByValTypeID(completeFateQFValTypeId);
		if ( msd == null ) {
			log.error("Missing MethStream for CompleteFateQF - no algorithm will be run!");
			return false;
		}
		
		String fate = mActy.findValueStringByValId(fateValId);;
		Double fateQF = mActy.findValueByValId(invalidFateQFValId);
		String taxonID = mActy.findValueStringByValId(taxonIDValId);
		Double taxonIDQF = mActy.findValueByValId(invalidTaxonIDQFValId);
		
		// populates initial value with zero [5.1.2 - 5.a (see NEON.DOC.001244)] 
		Double compFateQF = 0.0;  // zero means ok, 1 means not ok, -1 means other QFs not ok.
		
		if ( (fateQF == null) || (fateQF.compareTo(0.0) > 0) || 
			 (taxonIDQF == null) || (taxonIDQF.compareTo(0.0) > 0) ) {
			compFateQF = -1.0;
		} else {
			// implementation of 5.1.2 - 5.c.i.A (see NEON.DOC.001244)
			if( (fate == null) || fate.isEmpty() ) {
				// log.debug("REACHED HERE!!!  " + taxonID);
				if( (taxonID != null) && (!taxonID.isEmpty()) ) {
					compFateQF = 1.;
				}
			}		
		}
		msd.addReadoutValue(mActy.getStartDate(), mActy.getEndDate(), completeFateQFValId, compFateQF);
		return true;
	}

}
