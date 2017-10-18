package org.neoninc.dpms.algorithms.fsu;

import org.testng.annotations.Test;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.testng.annotations.Test;

/**
 * Test driver for DaysUtil.class.
 *                      May 15, 2014
 * @author sgui 
 */

public class DaysUtil_Test {
	// Creates logger instance
	static private Logger log = Logger.getLogger("DaysUtil_Test.class");

	/**
	 * Test driver
	 */
	@Test
	public void testDaysOfYear() {
		int days = -1;
		int daysOfWaterYear = -1; 
		
		java.util.Date date = new java.util.Date();
		
		// The following code tests days of year.
		String dStr = DaysUtil.getDateString(date);
		days = DaysUtil.getDaysOfYear(date);
		log.debug("1 date string: " + dStr + "    days: '" + days + "'");
		
		java.util.Calendar cal = Calendar.getInstance();
		dStr = DaysUtil.getDateString(cal);
		days = DaysUtil.getDaysOfYear(cal);
		log.debug("2 date string: " + dStr + "    days: '" + days + "'");
		
		days = DaysUtil.getDaysOfYear(dStr);
		
		// The following code tests days of water year
		daysOfWaterYear = DaysUtil.getDaysOfWaterYear(cal);
		log.debug("3 date string: " + dStr + "    days: '" + days + "'    daysOfWaterYear: '" + daysOfWaterYear + "'");
				
		// for month: January - 0; December - 11
		cal = new GregorianCalendar(2012, 3, 5, 0, 0, 1);
		date = cal.getTime();
		daysOfWaterYear = DaysUtil.getDaysOfWaterYear(date);
		log.debug("1 daysOfWaterYear: '" + daysOfWaterYear + "'\n");
		
		cal.setTime(date);
		daysOfWaterYear = DaysUtil.getDaysOfWaterYear(cal);
		log.debug("2 daysOfWaterYear: '" + daysOfWaterYear + "'");

		dStr = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH); 
		daysOfWaterYear = DaysUtil.getDaysOfWaterYear(dStr);
		log.debug("3 year: '" + dStr + "'    daysOfWaterYear: '" + daysOfWaterYear + "'");		
	}
}
