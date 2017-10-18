package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test driver for IndividualIDGenerator.class.
 *
 * There are no value IDs to match moduleID, domainID, and uniqueID. The real
 * values of moduleID, domainID, and uniqueID are required to run this algorithm.  
 * @author sgui, May 28, 2014
 */
public class IndividualIDGenerator_Test {
	// Creates logger instance
	static private Logger log = Logger.getLogger(IndividualIDGenerator_Test.class);

	/**
	 * Test driver
	 */
	@Test
	public void generateIndividualID() {
		// Instantiates date instance
		Calendar cal = Calendar.getInstance();
		cal.set(2030, Calendar.JULY, 1);
		Date date = cal.getTime(); 

		// Assume individualID's valId to be 802L.
		Long individualIDValId = 802L;
		Long individualIDValTypeId = 803L;

		String moduleID = "MAM";
		String domainID = "D10";
		String[] uKeys = {"123", "123456", "12345678", null };
		String[] expectedResult = { "NEON.MAM.D10.000123",
									"NEON.MAM.D10.123456",
									"NEON.MAM.D10.345678",
									null };

		IndividualIDGenerator gen = new IndividualIDGenerator(individualIDValId, individualIDValTypeId, 
								moduleID, domainID);
		
		for ( int i = 0; i < uKeys.length; i++ ) {
			DPMSMethActivity acty = new DPMSMethActivity();
			// The date instance is used to differentiate it from transition time.
			acty.setStartDate(date);
			DPMSMethStreamData individualIDms = new DPMSMethStreamData();
			individualIDms.setMStreamValTypeID(individualIDValTypeId);
			acty.getMethStreams().add(individualIDms);

			boolean result = gen.runAlgorithm(acty, uKeys[i]);
			if(result) {
				String resultString = acty.findValueStringByValId(individualIDValId);
				if ( expectedResult[i] != null ) {
					if ( expectedResult[i].compareTo(resultString) != 0 ) {
						log.debug("expected/result = " + expectedResult[i] + "/" + resultString);
						AssertJUnit.assertTrue(false);
					}
				} else {
					if ( resultString != null ) {
						log.debug("expected null, got " + resultString);
						AssertJUnit.assertTrue(false);
					}
				}
			} else {
				log.error("The algorithm of IndividualIDGenerator did not run successfully!!!");
				AssertJUnit.assertTrue(result);
			}	
		} // end of for loop
	}	
}

