package com.capitalone.dashboard.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public final class DateUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

	public static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static Date fromISODateTimeFormat(String isoString) {
		String iString = isoString;
		int charIndex = iString.indexOf(".");
		if (charIndex != -1) {
			iString = iString.substring(0, charIndex);
		}
		try {
			return new SimpleDateFormat(ISO_DATE_TIME_FORMAT).parse(iString);
		} catch (ParseException e) {
			LOGGER.error("Parse error of: " + iString, e);
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

	/**
	 * Evaluates whether a sprint length appears to be kanban or scrum
	 * 
	 * @param startDate
	 *            The start date of a sprint in ISO format
	 * @param endDate
	 *            The end date of a sprint in ISO format
	 * @return True indicates a scrum sprint; False indicates a Kanban sprint
	 */
	public boolean evaluateSprintLength(String startDate, String endDate, int maxKanbanIterationLength) {
		boolean sprintIndicator = false;
		Calendar startCalendar = Calendar.getInstance();
		Calendar endCalendar = Calendar.getInstance();

		if ((!StringUtils.isAnyEmpty(startDate) && !StringUtils.isAnyEmpty(endDate))
				&& (this.isInteger(startDate.substring(0, 4), 10)
						&& this.isInteger(endDate.substring(0, 4), 10)
						&& this.isInteger(startDate.substring(8, 10), 10)
						&& this.isInteger(endDate.substring(8, 10), 10)
						&& this.isInteger(startDate.substring(8, 10), 10)
						&& this.isInteger(endDate.substring(8, 10), 10))) {
			startCalendar.set(Integer.valueOf(startDate.substring(0, 4)),
					Integer.valueOf(startDate.substring(5, 7)),
					Integer.valueOf(startDate.substring(8, 10)));
			endCalendar.set(Integer.valueOf(endDate.substring(0, 4)),
					Integer.valueOf(endDate.substring(5, 7)),
					Integer.valueOf(endDate.substring(8, 10)));
			long diffMill = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();
			long diffDays = TimeUnit.DAYS.convert(diffMill, TimeUnit.MILLISECONDS);

			if (diffDays <= maxKanbanIterationLength) {
				// Scrum-enough
				sprintIndicator = true;
			}
		} else {
			// Default to kanban
			sprintIndicator = false;

		}

		return sprintIndicator;
	}

	/**
	 * Determines if string is an integer of the radix base number system
	 * provided.
	 * 
	 * @param s
	 *            String to be evaluated for integer type
	 * @param radix
	 *            Base number system (e.g., 10 = base 10 number system)
	 * @return boolean
	 */
	private boolean isInteger(String s, int radix) {
		Scanner sc = new Scanner(s.trim());
		if (!sc.hasNextInt(radix))
			return false;
		// we know it starts with a valid int, now make sure
		// there's nothing left!
		sc.nextInt(radix);
		return !sc.hasNext();
	}

}
