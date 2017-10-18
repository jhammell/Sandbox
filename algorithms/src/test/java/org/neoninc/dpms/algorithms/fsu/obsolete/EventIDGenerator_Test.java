package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSLocationInfo;
import org.neoninc.dpms.datastructures.DPMSMethActivity;

import static org.testng.Assert.*;

/**
 * Test driver for EventIDGenerator.class.
 * 		
 * With method activity, we should be able to get siteId. If the 
 * method activity type is bout, we should have its siteID's NID, 
 * then obtain the NID's associated valID (or locationID). Using that 
 * valID (or locationID) we should be able to get the value of siteID.
 *  
 * The same methodology applies to the method activity types of night 
 * and capture data. The code to get siteID is expected to be implemented 
 * in transition level.
 * @author sgui, May 12, 2014
 */

public class EventIDGenerator_Test {
	// Creates logger instance
	static private Logger log = Logger.getLogger(EventIDGenerator_Test.class);

	/**
	 * Test driver
	 */
	@Test
	public void generateEventID() {
		// Specifies the date for the test
		Calendar cal = Calendar.getInstance();
		cal.set(2030, Calendar.JULY, 1); 
		Date date = cal.getTime(); 

		Date tranTime = cal.getTime();
		cal.set(2014, Calendar.MAY, 21);
		
		DPMSLocationInfo site = new DPMSLocationInfo();
		site.setSiteID("SITEID");

		DPMSMethActivity inputActy = new DPMSMethActivity();
		// The date instance is used to differentiate it from transition time.
		inputActy.setStartDate(date);
		inputActy.setLocnInfo(site);

		// Test for normal algorithm execution.
		String eventID = EventIDGeneratorNIDBased.generateEventId(inputActy);
		log.debug("The eventID: '" + eventID + "'");
		
		assertTrue( eventID.compareTo("MAM.SITEID.2030.07") == 0 );

	}	
}

