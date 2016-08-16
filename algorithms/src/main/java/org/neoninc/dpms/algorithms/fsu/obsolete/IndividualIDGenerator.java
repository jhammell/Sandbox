package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * This class generates individualID in format of 
 *     NEON.moduleID.domainID.uniqueKey 
 * (for example, NEON.MAM.D10.10012). The moduleID, domainID, and uniqueKey 
 * are input data that should be obtained in transition level by CDS calls. 
 * It is assumed that these input data are in place when underneath ATBS'c
 * algorithm is called. 
 * 
 * There are no value IDs to match moduleID, domainID, and uniqueID. The real
 * values of moduleID, domainID, and uniqueID are required to run this algorithm.  
 * @author sgui, May 28, 2014
 */
public class IndividualIDGenerator {
	// Creates logger instance
	static private Logger log = Logger.getLogger(IndividualIDGenerator.class);
	
	// L1 ValID and ValTypeID for individualID 
	private Long l1IndividualIDValId;  
	private Long l1IndividualIDValTypeId; 
	
	// String for moduleID 
	private String moduleID;  
	
	// String for domainID 
	private String domainID;  

	// constant string
	final String HEAD_STRING = "NEON.";
	
	/**
	 * Constructor
	 * @param individualIDValId - value ID for individualID
	 * @param individualIDValTypeId - value type ID for individualID
	 * @param moduleID - String of moduleID
	 * @param domainID - String of domainID
	 */
	public IndividualIDGenerator(Long l1IndividualIDValId, Long l1IndividualIDValTypeId, 
			String moduleID, String domainID) {
		this.l1IndividualIDValId = l1IndividualIDValId;
		this.l1IndividualIDValTypeId = l1IndividualIDValTypeId;
		this.moduleID = moduleID;
		this.domainID = domainID;
	}
	
	/**
	 * This method runs algorithm to generate individualID and put it int
	 * the MethStream identified by l1IndividualIDValId and l1IndividualIDValTypeId.
	 * 
	 * @return boolean: true if this algorithm runs fine. Otherwise, false.
	 */
	public boolean runAlgorithm(DPMSMethActivity l1Acty, String uniqueKey) {
		// individualID = NEON.MMM.DDD.KKKKKK
		//  where MMM = 3-character moduleID
		//		DDD = 3-character domainID
		//		KKKKKK = 6-character unique key
		// total length = 19 characters.
		// For Small Mammals, MMM = "MAM" and key = tagID.

		// Latest Small Mammal ingest workbook says tagID can contain characters.
		// In order to NOT lose any of the tagID information (such as left or right ear tag),
		// uniqueKey is now treated as a string rather than an integer and is padded out to 6
		// characters with '0' characters.
		// Small Mammal ATBD also says that if tagID is null, individualID should be null.
		
		boolean ok = false;
		String individualID = null;
		if ( (moduleID != null) && (moduleID.length() == 3) &&
				(domainID != null) && (domainID.length() == 3) ) {
			if ( (uniqueKey != null) && (! uniqueKey.isEmpty()) ) {
				individualID = CreateIndividualID(uniqueKey);
			}
			ok = true;
		}
		
		DPMSMethStreamData l1MethStream = l1Acty.findMethStreamByValTypeID(l1IndividualIDValTypeId);
		l1MethStream.addReadoutValueString(l1Acty.getStartDate(), l1Acty.getEndDate(), 
						l1IndividualIDValId, individualID);	

		return ok;
	}
	
	/**
	 * Create and return an individualID.
	 * @return
	 */
	String CreateIndividualID(String uniqueKey) {
		if ( (uniqueKey == null) || (uniqueKey.isEmpty()) ) return null;
		StringBuilder sb = new StringBuilder("000000");
		int len = uniqueKey.length();
		if ( len > 6 ) sb.replace(0,6,uniqueKey.substring(len-6, len));
		else sb.replace(6-len, 6, uniqueKey);
		return HEAD_STRING + moduleID + "." + domainID + "." + sb.toString();
	}

}


