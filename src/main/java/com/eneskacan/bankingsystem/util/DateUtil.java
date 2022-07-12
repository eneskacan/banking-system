package com.eneskacan.bankingsystem.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Long getTimestamp() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static String timestampToDate(long timestamp) {
        Timestamp ts = new Timestamp(timestamp);
        Date date = ts;
        return date.toString();
    }
}
