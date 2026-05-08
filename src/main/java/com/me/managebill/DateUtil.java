package com.me.managebill;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateUtil() {
    }

    public static LocalDate parseDate(String raw) {
        try {
            return LocalDate.parse(raw, FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new AppException("Invalid date format. Expected dd/MM/yyyy.");
        }
    }

    public static String format(LocalDate date) {
        return date.format(FORMATTER);
    }
}
