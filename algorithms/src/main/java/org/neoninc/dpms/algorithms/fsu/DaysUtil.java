package org.neoninc.dpms.algorithms.fsu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * This utility class calculates days of year and days of water year.
 * @author sgui,  May 15, 2014          
 */
public class DaysUtil {
	// Creates logger instance
	static private Logger log = Logger.getLogger("DaysUtil.class");
	
	// date format to day
	static final String YMD_HYPHEN_FORMAT = "yyyy-MM-dd";

	// date format to day
	static final String YMD_SLASH_FORMAT = "MM/dd/yyyy HH:mm:ss";

	// date format to second
	static final String YTOSECOND_HYPHEN_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// format of second
	static final String SECOND_FORMAT = " 00:00:01";
	
	// constant days between Sept. 1 and Dec. 31 (inclusive)
	static final int NUMBER_OF_DAYS = 122;

	// constant for specific format
	static final String SPECIFIC_FORMAT = "-09-30 00:00:01"; 
	
	/**
	 * This method takes java.util.Date as input and return date string 
	 * in yyyy-MM-dd format.
	 * 
	 * @param date: Java Date object
	 * @return dStr: date string in yyyy-MM-dd format.
	 */
	public static String getDateString(java.util.Date date) {
		String dStr = "";

		SimpleDateFormat sdf2 = new SimpleDateFormat(YMD_HYPHEN_FORMAT);
		dStr = sdf2.format(date); 
		
		return dStr;
	}
	
	/**
	 * This method takes java.util.Calendar as input and return date string 
	 * in yyyy-MM-dd format.
	 * 
	 * @param date: Java Date object
	 * @return dStr: date string in yyyy-MM-dd format.
	 */
	public static String getDateString(java.util.Calendar cal) {
		String dStr = "";
		
		SimpleDateFormat sdf2 = new SimpleDateFormat(YMD_HYPHEN_FORMAT);
		dStr = sdf2.format(cal.getTime()); 
		
		return dStr;
	}
	
	/**
	 * This method returns days of year for a given date string in format
	 * of yyyy-MM-dd.
	 * 
	 * @param dStr: String of date in yyyy-MM-dd format.
	 * @return days: integer of days of year
	 */
	public static int getDaysOfYear(String dStr) {
		int days = -1;
		SimpleDateFormat sdf  = new SimpleDateFormat(YTOSECOND_HYPHEN_FORMAT);

		String dateInString = dStr + SECOND_FORMAT;

		Calendar cal = Calendar.getInstance();
		Date date;
		try {
			date = sdf.parse(dateInString);
			cal.setTime(date);
			days = cal.get(Calendar.DAY_OF_YEAR);
		} catch (ParseException e) {
			e.printStackTrace();
			log.error("Parsing error: " + e.toString());
		}
		
		return days;
	}
	
	/**
	 * This method calculates days of year for Java Date input.
	 * @param date: Instance of Java Date
	 * @return days: integer of days of year.
	 */
    public static int getDaysOfYear(java.util.Date date) {
    	int days = -1;
    	days = getDaysOfYear(getDateString(date));
    	
    	return days; 
    }
	
	/**
	 * This method calculates days of year for Java Calendar input.
	 * @param date: Instance of Java Calendar
	 * @return days: integer of days of year.
	 */
    public static int getDaysOfYear(java.util.Calendar cal) {
    	int days = -1;
    	days = getDaysOfYear(getDateString(cal));
    	
    	return days; 
    }
	
    /**
     * Days of water year starts on Oct. 1 and ends on Sept. 30.
     *  
     * This method calculates days of water year. Each year, the total days
     * between Oct. 1 and Dec. 31 are 122 days.
     *   if (the current date is between Oct. 1 (included) and Dec. 31 (included)) {
     *       days of water year = days_of_current_date of year - days_of_Sept_30 of year
     *   } else {
     *       days of water year = days_of_current_date of year + 122
     *   }
     * 
     * @param cal: Instance of java.util.Calendar
     * @return daysOfWaterYear: integer of days of water year
     */
	public static int getDaysOfWaterYear(java.util.Calendar cal) {
		int daysOfWaterYear = -1;
		int year = cal.get(Calendar.YEAR);
		Calendar calSept = Calendar.getInstance();
		
		SimpleDateFormat sdf  = new SimpleDateFormat(YTOSECOND_HYPHEN_FORMAT);
		String dStrSept = Integer.toString(year) + SPECIFIC_FORMAT;
		
		try {
			calSept.setTime(sdf.parse(dStrSept));			
			if(cal.compareTo(calSept) > 0) {
				daysOfWaterYear = cal.get(Calendar.DAY_OF_YEAR) - calSept.get(Calendar.DAY_OF_YEAR); 
			} else {
				daysOfWaterYear = cal.get(Calendar.DAY_OF_YEAR) + NUMBER_OF_DAYS;
			}		
		} catch (ParseException e) {
			e.printStackTrace();
			log.error("Parsing error: " + e.toString());
		}
		
		return daysOfWaterYear; 
	}
	
    /**
     * Days of water year starts on Oct. 1 and ends on Sept. 30.
     *  
     * @param cal: Instance of java.util.Date
     * @return daysOfWaterYear: integer of days of water year
     */
	public static int getDaysOfWaterYear(java.util.Date date) {
		int daysOfWaterYear = -1;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		daysOfWaterYear = getDaysOfWaterYear(cal);
		
		return daysOfWaterYear; 
	}
		
    /**
     * Days of water year starts on Oct. 1 and ends on Sept. 30.
     *  
     * @param dStr: String in format yyyy-MM-dd
     * @return daysOfWaterYear: integer of days of water year
     */
	public static int getDaysOfWaterYear(String dStr) {
		int daysOfWaterYear = -1;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf  = new SimpleDateFormat(YTOSECOND_HYPHEN_FORMAT);
		
		try {
			cal.setTime(sdf.parse(dStr + SECOND_FORMAT));			
			daysOfWaterYear = getDaysOfWaterYear(cal);
		} catch (ParseException e) {
			e.printStackTrace();
			log.error("Parsing error: " + e.toString());
		}
		
		return daysOfWaterYear; 
	}

	/**
	 * This method transfers date string in format MM/dd/yyyy to
	 * date string in format yyyy-MM-dd.
	 *  
	 * @param dStr: Date string in format MM/dd/yyyy
	 * @return resDateString: Date string in format yyyy-MM-dd
	 */
	private String changeDateStringFromSlashToHyphen(String dStr) {
		String resDateStr = "";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf  = new SimpleDateFormat(YMD_SLASH_FORMAT);
		
		try {
			cal.setTime(sdf.parse(dStr + SECOND_FORMAT));	
			resDateStr = new SimpleDateFormat(YMD_HYPHEN_FORMAT).format(cal.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			log.error("Parsing error: " + e.toString());
		}
		
		return resDateStr;
	}	
}
