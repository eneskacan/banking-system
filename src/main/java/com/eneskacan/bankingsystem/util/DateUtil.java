package com.eneskacan.bankingsystem.util;

import java.sql.Timestamp;

public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
