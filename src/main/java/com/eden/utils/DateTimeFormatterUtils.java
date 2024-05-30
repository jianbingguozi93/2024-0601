package com.eden.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeFormatterUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd EEEE HH:mm:ss", Locale.JAPANESE);

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}
