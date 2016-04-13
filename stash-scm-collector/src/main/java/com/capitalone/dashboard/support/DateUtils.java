package com.capitalone.dashboard.support;

import java.util.Calendar;
import java.util.Date;

/**
 * User: Alan
 * Email: alan@hialan.com
 * Date: 16:50 4/13/16
 */
public class DateUtils {
    public static Date getDate(Date dateInstance, int offsetDays, int offsetMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateInstance);
        cal.add(Calendar.DATE, offsetDays);
        cal.add(Calendar.MINUTE, offsetMinutes);
        return cal.getTime();
    }
}
