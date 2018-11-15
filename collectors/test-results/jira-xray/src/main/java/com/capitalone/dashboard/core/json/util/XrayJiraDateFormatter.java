package com.capitalone.dashboard.core.json.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * JiraXRay date formatter.
 */
public class XrayJiraDateFormatter {
    private final SimpleDateFormat standardDateTimeFormatter=new SimpleDateFormat("dd/MMM/yy hh:mm a");
    private final SimpleDateFormat recentDateTimeFormatter=new SimpleDateFormat("EE hh:mm a");
    private final SimpleDateFormat todayDateTimeFormatter=new SimpleDateFormat("hh:mm a");
    private final SimpleDateFormat weekInYearDateTimeFormatter=new SimpleDateFormat("w");
    private final SimpleDateFormat yearDateTimeFormatter=new SimpleDateFormat("yyyy");
    private final SimpleDateFormat hourDateTimeFormatter=new SimpleDateFormat("HH");
    private final SimpleDateFormat minuteDateTimeFormatter=new SimpleDateFormat("mm");

    public Date parse(String inputDate) throws ParseException{
        Date out=null;
        try {
            out=standardDateTimeFormatter.parse(inputDate);
        } catch (ParseException e) {
            try {
                out=recentDateTimeFormatter.parse(inputDate);
                Integer currentWeek=Integer.parseInt(weekInYearDateTimeFormatter.format(new Date()));
                Integer currentYear=Integer.parseInt(yearDateTimeFormatter.format(new Date()));
                Calendar cal=Calendar.getInstance();
                cal.setTime(out);
                cal.set(Calendar.YEAR,currentYear);
                cal.set(Calendar.WEEK_OF_YEAR,currentWeek);
                out=cal.getTime();
               } catch (ParseException e1) {
                    out = todayDateTimeParse(inputDate);
            }
        }
        return out;
    }


    private Date todayDateTimeParse(String inputDate) throws ParseException {

        ResourceBundle labels = ResourceBundle.getBundle("XrayDateFormatter", Locale.getDefault());

        if(inputDate.contains(labels.getString("today"))){
            Date givenDate=todayDateTimeFormatter.parse(inputDate.replace(labels.getString("today"),""));
            Calendar cal=Calendar.getInstance();
            cal.set(Calendar.YEAR,Integer.parseInt(yearDateTimeFormatter.format(new Date())));
            cal.set(Calendar.WEEK_OF_YEAR,Integer.parseInt(weekInYearDateTimeFormatter.format(new Date())));
            cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hourDateTimeFormatter.format(givenDate)));
            cal.set(Calendar.MINUTE,Integer.parseInt(minuteDateTimeFormatter.format(givenDate)));
            return cal.getTime();
        }
        return null;
    }


}
