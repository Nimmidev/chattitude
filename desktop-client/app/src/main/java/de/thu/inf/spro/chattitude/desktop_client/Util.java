package de.thu.inf.spro.chattitude.desktop_client;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {

    private static void setToStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static String getRelativeDateTime(Date date) {
        Calendar today = Calendar.getInstance();
        setToStartOfDay(today);

        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        if (!date.before(today.getTime())) {
            format = DateFormat.getTimeInstance(DateFormat.SHORT);
        }

        return format.format(date);
    }
}
