package com.capitalone.dashboard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

	public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";


	public static Date fromISODateTimeFormat(String isoString) {
		String iString = isoString;
		int charIndex = iString.indexOf(".");
		if (charIndex != -1){
			iString = iString.substring(0, charIndex);
		}
		try {
			return new SimpleDateFormat(ISO_DATE_TIME_FORMAT).parse(iString);
		} catch (ParseException e) {
			LOGGER.error("Parse error of: "+ iString, e);
            return null;
		}
	}


	public static String toISODateRealTimeFormat(Date iDate) {
		DateFormat df = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);
		return df.format(iDate);
	}


	public static Date getDatePriorToMinutes(Date fromDate, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromDate);
		calendar.add(Calendar.MINUTE, -1 * minutes);
		return calendar.getTime();
	}

    /**
     * Generates and retrieves the change date that occurs a minute prior to the
     * specified change date in ISO format.
     *
     * @param changeDateISO
     *            A given change date in ISO format
     * @return The ISO-formatted date/time stamp for a minute prior to the given
     *         change date
     */
    public static String getChangeDateMinutePrior(String changeDateISO, int priorMinutes) {
        return DateUtil.toISODateRealTimeFormat(DateUtil.getDatePriorToMinutes(
                DateUtil.fromISODateTimeFormat(changeDateISO), priorMinutes));
    }

}
