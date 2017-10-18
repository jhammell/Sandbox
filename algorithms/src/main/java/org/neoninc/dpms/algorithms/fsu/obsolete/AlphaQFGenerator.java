package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * generate Alpha QF
 * @author lzhang
 */
public class AlphaQFGenerator {

	static private Logger log = Logger.getLogger(AlphaQFGenerator.class);
	
	/**
	 * go through quality flags, if any flags being raised, the alpha QF will be raised
	 * 
	 * @param acty
	 * @param alphaQFValId
	 * @param msAlphaQF
	 * @param qfValIdList
	 * @return
	 */
	static public boolean generateAlphaQF(DPMSMethActivity acty, Long alphaQFValId, 
			DPMSMethStreamData msAlphaQF, ArrayList<Long> qfValIdList) {
		
		if(acty == null || alphaQFValId == null || msAlphaQF == null ||
				qfValIdList == null || qfValIdList.size() < 1) {
			log.error("Invalid input for generateAlphaQF - no algorithm will be run");
			return false;
		}
	
		Double alpha = -1.;
		for(Long qfValId : qfValIdList) {
			if(qfValId.equals(alphaQFValId)) {
				//in case the alphaQF value id was included in the quality flag list ...
				continue;
			}
			
			Double qf = acty.findValueByValId(qfValId);
			if(qf == null) {
				log.debug("quality flag " + qfValId + "was not set! Continue with next flag for Alpha QF ...");
				continue;
			}
			if(qf >= 1.) {
				alpha = 1.;
				msAlphaQF.addReadoutValue(acty.getStartDate(), acty.getEndDate(), alphaQFValId, alpha);
				return true;
			}
			if(qf == 0.) {
				alpha = 0.;
			}
		}
		msAlphaQF.addReadoutValue(acty.getStartDate(), acty.getEndDate(), alphaQFValId, alpha);
		return true;
	}
}
