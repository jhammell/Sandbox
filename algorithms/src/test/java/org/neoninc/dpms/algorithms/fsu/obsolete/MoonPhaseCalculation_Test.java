package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;
//import org.junit.Test;

/**
 * Test driver for MoonPhaseCalculation.class.
 * 
 * @author sgui May 12, 2014
 */

public class MoonPhaseCalculation_Test {
	// Creates logger instance
	static private Logger log = Logger.getLogger(MoonPhaseCalculation_Test.class);
	
	/**
	 * Test driver
	 */
	@Test
	public void testNewMoonDatesAndBoutNum() {
		MoonPhaseCalculation mpc = new MoonPhaseCalculation();

/**		
		String dStr = "2014-11-18";
//		dStr = "2014-07-28";
		int boutNum = mpc.findBoutNum(dStr);
		log.debug("main - the bout number: " + boutNum);
		
		// for month: January - 0; December - 11
		java.util.Calendar cal = new GregorianCalendar(2014, 10, 18, 0, 0, 1);
		java.util.Date date = cal.getTime();
		boutNum = mpc.findBoutNum(date);
		log.debug("1 boutNum: '" + boutNum + "'\n");
		
		cal.setTime(date);
		boutNum = mpc.findBoutNum(cal);
		log.debug("2 boutNum: '" + boutNum + "'");

		dStr = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH); 
		boutNum = mpc.findBoutNum(dStr);
		log.debug("3 year: '" + dStr + "'    boutNum: '" + boutNum + "'");		
**/

		String[] newMoonDates = null;
		for(int year=2010; year < 2050; year++) {
			newMoonDates = mpc.calNewMoonDatesForOneYear(year);
			for(String str : newMoonDates) {
				log.debug("New moon date: " + str);
			}
		}
		
		// dStr is in format yyyy-MM-dd 
		String dStr = "2014-02-06";
		int boutNum = mpc.findBoutNum(dStr);
		log.debug(dStr + "    bout number: '" + boutNum + "'");
		dStr = "2049-08-20";
		boutNum = mpc.findBoutNum(dStr);
		log.debug(dStr + "    bout number: '" + boutNum + "'");

		// No bout number is found for 2033-08-11
		// No bout number is found for 2033-08-11
		dStr = "2033-08-09";
		boutNum = mpc.findBoutNum(dStr);
		log.debug(dStr + "    bout number: '" + boutNum + "'");
		dStr = "2033-08-10";
		boutNum = mpc.findBoutNum(dStr);
		log.debug(dStr + "    bout number: '" + boutNum + "'");
		// No bout number is found for 2033-08-11
		dStr = "2033-08-11";
		boutNum = mpc.findBoutNum(dStr);
		log.debug(dStr + "    bout number: '" + boutNum + "'");

		dStr = "2033-08-09";
		boutNum = mpc.findBoutNum(dStr);
		log.debug(dStr + "    bout number: '" + boutNum + "'");
		dStr = "2033-08-12";
		boutNum = mpc.findBoutNum(dStr);
		log.debug(dStr + "    bout number: '" + boutNum + "'");

		dStr = "2037-04-30";
		boutNum = mpc.findBoutNum(dStr);
		log.debug(dStr + "    bout number: '" + boutNum + "'");
		dStr = "2037-05-01";
		boutNum = mpc.findBoutNum(dStr);
		log.debug(dStr + "    bout number: '" + boutNum + "'");
	
	}
}

