package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.algorithms.fsu.obsolete.MoonPhaseCalculation;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * This class generates eventID in format of MAM.siteID.year.bout (for 
 * example, MAM.CPER.2012.07). The year and bout can be calculated from 
 * ingested date or startDate field in input method activity. The siteID 
 * is gotten from the MethActivity. It is assumed that those input data provided in transition
 * level. 
 * @author sgui, May 28, 2014
 */
public class EventIDGenerator {
	// Creates logger instance
	static private Logger log = Logger.getLogger(EventIDGenerator.class);

	// meth activity instance to attach eventID
	private DPMSMethActivity inputActy;
	
	// meth stream id for eventID 
	private Long eventIDValId;  
		
	// a new meth stream to hold eventID for this meth activity
	private DPMSMethStreamData eventIDMethStream;  

	// constant string for eventID
	static final String HEAD_STRING = "MAM.";
	
	/**
	 * This constructor instantiates eventID method stream and valIDs.
	 * 
	 * @param inputActy - activity needs to be tested
	 * @param eventIDMethStream - method stream to hold eventID value
	 * @param eventIDValId - value ID for eventID
	 */
	public EventIDGenerator(DPMSMethActivity inputActy, DPMSMethStreamData eventIDMethStream, Long eventIDValId) {
		this.inputActy = inputActy;
		this.eventIDMethStream = eventIDMethStream;
		this.eventIDValId = eventIDValId;
		// copy whatever to be published in inputActy to outputActy. 
		// This should be done somewhere else.
	}
	
	/**
	 * This method runs algorithm to generate eventID. Before calling this method,
	 * we expect the fields in this class are initialized when constructor with
	 * input parameters is called.
	 * 
	 * @return boolean: true if this algorithm runs fine. Otherwise, it returns false.
	 */
	public boolean runAlgorithm(){
		if (inputActy == null) {
			log.error("Invalid input for EventIDGenerator - no algorithm will be run.");
			return false;
		}
		String eventID = generateEvenID(inputActy);
		if(eventID == null || eventID.trim().length() < 5 || eventID.startsWith(HEAD_STRING) != true) {
			return false; 
		}
		
		DPMSMStreamReadout newRdot = new DPMSMStreamReadout();
		eventIDMethStream.getMSReadouts().add(newRdot);
		newRdot.setValueStringForValueId(eventIDValId, eventID, true);
		newRdot.setReadoutTranTime(new Date());
		
		return true;
	}

	/**
	 * This method generates eventID using DPMSMethStreamData. The format of eventID
	 * is MAM.SITE.year.bout (example: MAM.HARV.2013.06) 
	 * @param mStream: Instance of DPMSMethStreamData
	 * @return eventID: String in format MAM.SITE.year.bout 
	 */
	public String generateEvenID(DPMSMethActivity mActy) {
		String eventID = HEAD_STRING;
				
		java.util.Date date = mActy.getStartDate();
		MoonPhaseCalculation mpc = new MoonPhaseCalculation();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		
		String boutStr = "";
		int boutNum = mpc.findBoutNum(date);
		if(boutNum < 10 && boutNum >= 1) {
			boutStr = "0" + boutNum;
		} else if (boutNum < 1) {
			log.error("Bout number should NEVER be negative.");
		} else {
			boutStr = Integer.toString(boutNum); 
		}
		
		// constructs eventID 
		eventID = eventID + this.inputActy.getLocnInfo().getSiteID() + "." + Integer.toString(year) + "." + boutStr;
	
		return eventID;
	}
}

