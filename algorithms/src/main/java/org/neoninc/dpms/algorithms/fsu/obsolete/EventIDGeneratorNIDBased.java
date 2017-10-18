package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.Calendar;
import org.apache.log4j.Logger;
import org.neoninc.dpms.algorithms.fsu.obsolete.MoonPhaseCalculation;
import org.neoninc.dpms.datastructures.DPMSMethActivity;

/**
 * This class generates eventID in format of MAM.siteID.year.bout (for 
 * example, MAM.CPER.2012.07). The year and bout can be calculated from 
 * ingested date or startDate field in input method activity. The siteID 
 * is gotten from the MethActivity. It is assumed that those input data provided in transition
 * level. 
 * @author sgui, May 28, 2014
 */
public class EventIDGeneratorNIDBased {
	// Creates logger instance
	static private Logger log = Logger.getLogger(EventIDGeneratorNIDBased.class);

	// constant string for eventID
	static final String HEAD_STRING = "MAM.";

	/**
	 * This method generates eventID using DPMSMethStreamData. The format of eventID
	 * is MAM.SITE.year.bout (example: MAM.HARV.2013.06) 
	 * @param mStream: Instance of DPMSMethStreamData
	 * @return eventID: String in format MAM.SITE.year.bout 
	 */
	static public String generateEventId(DPMSMethActivity mActy) {
		String eventID = null;
				
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
			return null;
		} else {
			boutStr = Integer.toString(boutNum); 
		}
		
		String siteID = mActy.getLocnInfo().getSiteID();
		
		// constructs eventID 
		eventID = HEAD_STRING + siteID + "." + Integer.toString(year) + "." + boutStr;
	
		return eventID;
	}
}

