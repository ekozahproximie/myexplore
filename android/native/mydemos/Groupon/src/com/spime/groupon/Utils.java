package com.spime.groupon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import android.test.IsolatedContext;

public class Utils {
	// TODO TimeZone Values

	private static final int DAY_LIGHT_SAVING = 1;
	private static final String STR_GMT = "GMT";
	private static final String TIME_FORMAT = "hh:mm a";
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static String TIME_AM = "AM";
	private static String TIME_PM = "PM";
	public static final String STR_COLON = ":";
	
	private static final String STR_SPACE = " ";
	private static final String STR_NULL = "";

	// Get Time Zone values in secs
	public static boolean checkIfNowAtDST(String timezone) {

		/*************************************
		 * US. 2007 energy policy DST starts: at 2:00am in standard time on the
		 * second Sunday in March DST ends: at 2:00am in daylight time on the
		 * first Sunday in November ***********************************
		 */
		boolean isDST = false;
		TimeZone iTz = TimeZone.getTimeZone(timezone);
		SimpleTimeZone stz = new SimpleTimeZone(iTz.getRawOffset(), timezone,
				Calendar.MARCH, 8, -Calendar.SUNDAY, 7200000,
				Calendar.NOVEMBER, 1, -Calendar.SUNDAY, 7200000, 3600000);

		if (stz.inDaylightTime(new Date())) {
			isDST = true;
		}
		return isDST;
	}

	static public TimeDiff getHours(long time) {

        final long timeInMillis = time;

        final int days = (int) (timeInMillis / (24L * 60 * 60 * 1000));

        int remdr = (int) (timeInMillis % (24L * 60 * 60 * 1000));

        final int hours = remdr / (60 * 60 * 1000);

        remdr %= 60 * 60 * 1000;

        final int minutes = remdr / (60 * 1000);

        remdr %= 60 * 1000;

        final int seconds = remdr / 1000;

        final int ms = remdr % 1000;
        TimeDiff diff =new TimeDiff();
        diff.iDay=days;
        diff.iHours=hours;
        diff.iMins=minutes;
        diff.iSeconds=seconds;
        diff.lMyTime=time;
        return diff;

    }
	public static Date getDateInTimeZone(Date currentDate, String timeZoneId) {
		TimeZone tz = TimeZone.getTimeZone(timeZoneId);
		Calendar mbCal = new GregorianCalendar(tz);
		mbCal.setTimeInMillis(currentDate.getTime());

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));
              
		return cal.getTime();
	}
public static TimeDiff getTimetoLeft(int timezoneinsec,String stExpiresDays){
	TimeDiff diff=null;
	 SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
     
     Date today;
	try {
		today = formatter.parse(getDateForTimeZone(timezoneinsec,true));

    
    
     Date endday = formatter.parse(stExpiresDays);


int tz_hours = timezoneinsec / (60 * 60);

		String tzvalue = STR_GMT + tz_hours;

                int iDLSHours = 0;
            if (checkIfNowAtDST(tzvalue)) {
                iDLSHours = 60 * 60 * 1000;
            }
            diff = getHours(((endday.getTime() + (timezoneinsec * 1000) + iDLSHours) - today.getTime()));
          

      

    
      
	} catch (ParseException e) {

		e.printStackTrace();
	}

	return diff;
}
	public static String getTimeZone(int timezoneinsec, boolean isTimeZoneinSec) {
		String tzvalue = null;

		if (isTimeZoneinSec) {
			int tz_hours = timezoneinsec / (60 * 60);

			tzvalue = STR_GMT + tz_hours;

			boolean isDST = checkIfNowAtDST(tzvalue);

			if (isDST) {
				tz_hours += DAY_LIGHT_SAVING;
			}

			tzvalue = STR_GMT + tz_hours;
		}
		return tzvalue;
	}

	public static String getDayTimeForTimeZone(int timezoneinsec,
			boolean isTimeZoneinSec) {
		String tzvalue = getTimeZone(timezoneinsec, isTimeZoneinSec);
		String strTz = null;

		Date date = new Date();

		TimeZone tzone = TimeZone.getTimeZone(tzvalue);

		SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT);

		formatter.setTimeZone(tzone);

		strTz = formatter.format(date);

		return strTz;
	}
	public static String getDateForTimeZone(int timezoneinsec,
			boolean isTimeZoneinSec) {
		String tzvalue = getTimeZone(timezoneinsec, isTimeZoneinSec);
		String strTz = null;

		Date date = new Date();

		TimeZone tzone = TimeZone.getTimeZone(tzvalue);

		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

		formatter.setTimeZone(tzone);

		strTz = formatter.format(date);

		return strTz;
	}
	public static String getTippedTimeZoneValue(int timezoneinsec,
			int gmtHours, int gmtMins) {

		String tzvalue = null;

		StringBuilder strTz = new StringBuilder(STR_NULL);

		String am_pm = TIME_AM;

		int tz_hours = (timezoneinsec / (60 * 60));

		tzvalue = STR_GMT + tz_hours;

		boolean isDST = checkIfNowAtDST(tzvalue);

		if (isDST) {
			tz_hours += DAY_LIGHT_SAVING;
		}

		int localTippedTZHours = gmtHours + tz_hours;

		if (localTippedTZHours > 12) {
			localTippedTZHours = localTippedTZHours -= 12;
			am_pm = TIME_PM;
		}
		localTippedTZHours = (localTippedTZHours == 0) ? 12
				: localTippedTZHours;
		strTz.append(localTippedTZHours);
		strTz.append(STR_COLON);
		if (gmtMins < 10) {
			strTz.append("0");
			strTz.append(gmtMins);
		} else {
			strTz.append(gmtMins);
		}
		strTz.append(STR_SPACE);
		strTz.append(am_pm);
		return strTz.toString();
	}
}
