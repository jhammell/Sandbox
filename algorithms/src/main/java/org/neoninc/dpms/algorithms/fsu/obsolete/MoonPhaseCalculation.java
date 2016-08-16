package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * This class calculates new moon dates for a given year. It also finds bout number for
 * a given date string in yyyy-MM-dd format. According to the calendar, Jan. 6, 2000 is 
 * a new moon date. This class is only valid to calculate new moon dates
 * when the input date is after Jan. 6, 2000. 
 * 
 * We have compared the calculated new moon dates with calendar ones for the years
 * from 2014 to 2045. the calculated new moon date may be one day off. The difference 
 * between calendar's new moon date and calculated new moon date from this program 
 * does not accumulate.
 * 
 * @author sgui May 22, 2014
 */
public class MoonPhaseCalculation {
	// Creates logger instance
	static private Logger log = Logger.getLogger(MoonPhaseCalculation.class);
	
	// The following lunation value is obtained from
	// http://en.wikipedia.org/wiki/Lunation
	static private double lunation = 29.5305882D;
	
	// As we know, Jan. 6, 2000 was a new moon date. There are 947217601000L 
	// milliseconds from Epoch, January 1, 1970 00:00:00.000 GMT (Gregorian) 
	// to 17:00 AM 00:00:01 of Jan. 6, 2000.
	static private long startMilliSeconds = 947217601000L + 8*3600000L;

	// one day's milli-seconds value
	static private double oneDayMilliSec = 86400000.0D;
	
	// Place holder for previously calculated double value
	static private double preVal = 0.0D;
	
	// According to ATBC (NEON.DOC.001244 of May 14, 2014), hardcode plus/minus
	// 14 days around new moon day. This can be read from a file as a constant
	// or obtained from other channel. The 14 days is used based on 
	// Kate Thibault's e-mail of May 22, 2014.
	//                                          S.G., May 22, 2014 
	static private int shiftDays = 14; 

	// date format to day
	static final String YMD_HYPHEN_FORMAT = "yyyy-MM-dd";

	// date format to second
	static final String YTOSECOND_HYPHEN_FORMAT = "yyyy-MM-dd HH:mm:ss";

	// format of second
	static final String SECOND_FORMAT = " 00:00:01";
	
	/**
	 * This method calculates new moon date. If the new moon date is found, the
	 * date string is returned. Otherwise, an empty string is returned.
	 *  
	 * @param year
	 * @param month: 1 is for January.
	 * @param day: Day of a month.
	 */
	private String calNewMoonDate(int year, int month, int day) {
		// date string 
		String newMoonDateStr = "";	
        Calendar cal = Calendar.getInstance();
        
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 0);

        // Get the represented date in milliseconds
        long milis2 = cal.getTimeInMillis();
        
        // Calculate difference in milliseconds
        long diff = milis2 - startMilliSeconds;
        
        // Calculate difference in days
        double diffDays = (double) diff / oneDayMilliSec;
           
        double doubleDiffDays1 = 0.0d + diffDays; 
        double doubleDiffDays2 = 1.0d + diffDays; 

        double multiTimes1 = doubleDiffDays1 / lunation;
        // For roundup discrepancy, sometimes multiTimes1 is less than preVal. 
        // In principle, they should be the same. To avoid this small discrepancy,
        // the following if-block is added.
        if(multiTimes1 < preVal && Math.abs(multiTimes1-preVal) < 1.0D) {
//        	log.debug("\nMulti times1: '" + multiTimes1 + "'    preVal: '" + preVal + "'");
        	multiTimes1 = preVal;
        }
        double multiTimes2 = doubleDiffDays2 / lunation;
        preVal = multiTimes2; 
        
        String doubleStr1 = Double.toString(multiTimes1);
        String doubleStr2 = Double.toString(multiTimes2);
        String[] tokens1 = doubleStr1.split("\\.");
        String[] tokens2 = doubleStr2.split("\\.");
        int intVal1 = Integer.parseInt(tokens1[0]);
        int intVal2 = Integer.parseInt(tokens2[0]);
        if((intVal2 - intVal1) == 1) {
        	SimpleDateFormat sdf = new SimpleDateFormat(YMD_HYPHEN_FORMAT);
        	newMoonDateStr = sdf.format(cal.getTime());            
        }
		
        return newMoonDateStr;
	}
	
    /**
     * This method calculates string array of new moon dates for a given year.
     * @param year: integer of a given year.
     * @return newMoondDates: string array of new moon dates.
     */
	public String[] calNewMoonDatesForOneYear(int year) {
	    ArrayList<String> list = new ArrayList<String>();
		Calendar cal = Calendar.getInstance(); // GregorianCalendar
		cal.set(Calendar.YEAR, year);

		String newMoon = "";
		for (int i = 1; i <= 366; i++) {
			cal.set(Calendar.DAY_OF_YEAR, i);  
			int month = cal.get(Calendar.MONTH);
			int day   = cal.get(Calendar.DAY_OF_MONTH);
//			if(year == 2013 && month == 10) {
//				log.debug("It is " + year + "-" + month + "-" + day);
//			}
			if(i < 366 || (i == 366 && month == 11)) { 
				newMoon = calNewMoonDate(year, month+1, day);
				if(newMoon.trim().length() == 0) {
					SimpleDateFormat sdf = new SimpleDateFormat(YMD_HYPHEN_FORMAT);
				} else {
//					log.debug("WOW, new moon date " + newMoon + " is found!");
					list.add(newMoon);
				}
				
//				if(i == 366 && month == 11) {
//					log.debug("Year " + year + " has 366 days.  Month: " + month + "    day of the month: " + day);
//				}
			} else if(i == 366 && month == 0) { 
//				log.debug("Year " + year + " has 365 days.");
			}
		}
		
		String[] newMoonDatesFlexible = new String[list.size()];	
		newMoonDatesFlexible = list.toArray(newMoonDatesFlexible);

		return newMoonDatesFlexible;	
	}
	
	/**
	 * This method is to return integer part of a string of a double value.
	 * @param dVal a double value
	 * @return num integer of double string's int part. 
	 */
	public int getIntFromDoubleVal(double dVal) {
		String dStr = Double.toString(dVal);

		String[] tokens = dStr.split("\\.");		
		int num = Integer.parseInt(tokens[0]);

		return num;
	}

	/**
	 * This method finds bout number for a given date string in yyyy-MM-dd format.
	 * The valid bout number should be 1 to 13. If the return value is -1, it means
	 * the given date string is out of scope of any bout range.
	 * 
	 * @param dStr: String in yyyy-MM-dd format 
	 * @return boutNum: integer of bout number.
	 */
	public int findBoutNum(String dStr) {
		int boutNum = -1;

		Date date;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf  = new SimpleDateFormat(YTOSECOND_HYPHEN_FORMAT);

		String dateStr = dStr + SECOND_FORMAT;
		try {
			date = sdf.parse(dateStr);
			cal.setTime(date);
		} catch (ParseException e1) {
			log.error("Parsing error occurs: " + e1.toString());
		}

		int year = cal.get(Calendar.YEAR);
		String[] newMoonDates = calNewMoonDatesForOneYear(year);
		boutNum = findBoutNum(dStr, newMoonDates);
//		for(String str : newMoonDates) {
//			log.debug("NEW moon date: '" + str + "'");
//		}
//		log.debug("Local - the bout number: " + boutNum);

		return boutNum;
	}

	/**
	 * This method finds bout number for a given date string in yyyy-MM-dd format.
	 * The valid bout number should be 1 to 13. If the return value is -1, it means
	 * the given date string is out of scope of any bout range.
	 * 
	 * @param dStr: String in yyyy-MM-dd format 
	 * @param newMoonDates: array of Strings. Each String is in yyyy-MM-dd format.
	 * @return boutNum: integer of bout number.
	 */
	public int findBoutNum(String dStr, String[] newMoonDates) {
		int boutNum = -1;
		
		Calendar cal  = Calendar.getInstance();
		Calendar cal1 = Calendar.getInstance(); 
		Calendar cal2 = Calendar.getInstance(); 
		Date date;
		Date dateComp;
		SimpleDateFormat sdf  = new SimpleDateFormat(YTOSECOND_HYPHEN_FORMAT);

		String dateStr = dStr + SECOND_FORMAT;
		try {
			dateComp = sdf.parse(dateStr);
			cal.setTime(dateComp);
		} catch (ParseException e1) {
			log.error("Parsing error occurs: " + e1.toString());
		}

		for (int index = 0; index < newMoonDates.length; index++) {		
			String dateInString = newMoonDates[index] + SECOND_FORMAT;
	
			try {
				date = sdf.parse(dateInString);
				cal1.setTime(date); 
				cal2.setTime(date); 
				cal1.add(Calendar.DAY_OF_YEAR, -shiftDays);	 
				cal2.add(Calendar.DAY_OF_YEAR, shiftDays);	 
				
				if(cal.compareTo(cal1) > 0 && cal.compareTo(cal2) < 0) {
					boutNum = index + 1;
					index = newMoonDates.length + 1;
				}		
			} catch (ParseException e) {
				log.error("Parsing error occurs: " + e.toString());
			}			
		}
		
		// If bout number is negative, we are going to double go-back days to 
		// find out a bout number for this date. In that way, we are sure any 
		// day will be assigned a bout number.
		if(boutNum < 0) {
			log.warn("Something days is not fully covered to calculate bout number. " +
					"Double go-back days to cover those missing days!");
			for (int index = 0; index < newMoonDates.length; index++) {		
				String dateInString = newMoonDates[index] + SECOND_FORMAT;
		
				try {
					date = sdf.parse(dateInString);
					cal1.setTime(date); 
					cal2.setTime(date); 
					cal1.add(Calendar.DAY_OF_YEAR, -shiftDays*2);	 
					cal2.add(Calendar.DAY_OF_YEAR, shiftDays);	 
					
					if(cal.compareTo(cal1) > 0 && cal.compareTo(cal2) < 0) {
						boutNum = index + 1;
						index = newMoonDates.length + 1;
					}		
				} catch (ParseException e) {
					log.error("Parsing error occurs: " + e.toString());
				}			
			}
		}
		
		return boutNum;
	}
	
	/**
	 * This method calculates bout number for Java Date input.
	 * @param date: Instance of Java Date
	 * @return days: integer of bout number.
	 */
    public int findBoutNum(java.util.Date date) {
    	int days = -1;
    	days = findBoutNum(getDateString(date));
    	
    	return days; 
    }
	
	/**
	 * This method calculates bout number for Java Calendar input.
	 * @param date: Instance of Java Calendar
	 * @return days: integer of bout number.
	 */
    public int findBoutNum(java.util.Calendar cal) {
    	int days = -1;
    	days = findBoutNum(getDateString(cal));
    	
    	return days; 
    }

	/**
	 * This method takes java.util.Date as input and return date string 
	 * in yyyy-MM-dd format.
	 * 
	 * @param date: Java Date object
	 * @return dStr: date string in yyyy-MM-dd format.
	 */
	public String getDateString(java.util.Date date) {
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
	public String getDateString(java.util.Calendar cal) {
		String dStr = "";
		
		SimpleDateFormat sdf2 = new SimpleDateFormat(YMD_HYPHEN_FORMAT);
		dStr = sdf2.format(cal.getTime()); 
		
		return dStr;
	}
}
