package com.capitalone.dashboard.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

	public static final String DISPLAY_DATE_FORMAT = "dd-MMM-yyyy";
	public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
	public static final String ISO_TIME_FORMAT = "T00:00:00.000000";
	public static final String ISO_DATE_TIME_FORMATZ = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
	public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	private DateUtil() {
		// util class.
	}

	public static Date getNextBusinessDate(Date iDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(iDate);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		if (dayOfWeek == Calendar.FRIDAY) {
			calendar.add(Calendar.DATE, 3);
		} else if (dayOfWeek == Calendar.SATURDAY) {
			calendar.add(Calendar.DATE, 2);
		} else {
			calendar.add(Calendar.DATE, 1);
		}

		Date nextBusinessDate = calendar.getTime();
		return nextBusinessDate;
	}

	public static boolean isToday(Date iDate) {
		Date today = DateUtil.getTodayNoTime();
		Date inputDate = DateUtil.getDateNoTime(iDate);
		return inputDate.compareTo(today) == 0;
	}

	public static String toISODateTimeFormat(Date iDate) {
		DateFormat df = new SimpleDateFormat(ISO_DATE_FORMAT);
		String isoDateString = df.format(iDate) + ISO_TIME_FORMAT;

		return isoDateString;
	}

	public static String toISODateFormat(Date iDate) {
		Format formatter = new SimpleDateFormat(ISO_DATE_FORMAT);
		return formatter.format(iDate);
	}

	public static Date addDays(Date iDate, int amount) {
		Date newDate = iDate;
		for (int i = 0; i < amount; i++) {
			newDate = DateUtil.getNextBusinessDate(newDate);
		}
		return newDate;
	}

	public static Date getDateNoTime(Date iDate) {
		// Get Calendar object set to the date and time of the given Date object
		Calendar cal = Calendar.getInstance();
		cal.setTime(iDate);

		// Set time fields to zero
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getTodayNoTime() {
		Date today = new Date();
		return DateUtil.getDateNoTime(today);
	}

	public static String toDisplayDateFormat(Date iDate) {
		DateFormat df = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
		String displayDateString = df.format(iDate);

		return displayDateString;
	}

	public static Date fromISODateTimeFormat(String isoString) {
		String iString = isoString;
		int charIndex = iString.indexOf(".");
		if (charIndex!=-1){
			iString = iString.substring(0, charIndex);
		}
		if (iString == null)
			return null;
		Date dt = null;

		try {
			dt = new SimpleDateFormat(ISO_DATE_TIME_FORMAT).parse(iString);
		} catch (ParseException e) {
			LOGGER.error("Parsing ISO DateTime: "+ isoString, e);
		}
		return dt;
	}

	public static Date fromISODateFormat(String iString) {
		if (iString == null)
			return null;
		Date dt = null;

		try {
			dt = new SimpleDateFormat(ISO_DATE_FORMAT).parse(iString);
		} catch (ParseException e) {
			LOGGER.error("Parsing ISO DateTime: "+ iString, e);
		}

		return dt;
	}

	public static String toISODateRealTimeFormat(Date iDate) {
		DateFormat df = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);
		String isoDateString = df.format(iDate);
		return isoDateString;
	}

	public static int differenceInDays(Date newerDate, Date olderDate) {
		return (int) ((newerDate.getTime() - olderDate.getTime()) / (1000 * 60 * 60 * 24));
	}

	public static Date getDatePriorToNDays(Date fromDate, int numberOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromDate);
		calendar.add(Calendar.DAY_OF_MONTH, -1 * numberOfDays);
		Date daysAgo = calendar.getTime();
		return daysAgo;
	}

	public static Date getDatePriorToMinutes(Date fromDate, int minutes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fromDate);
		calendar.add(Calendar.MINUTE, -1 * minutes);
		Date daysAgo = calendar.getTime();
		return daysAgo;
	}

}
