package uk.gov.hmcts.probate.services.invitation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UKDateFormatter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UKDateFormatter.class);
    public static final Locale ENGLISH_LOCALE = Locale.UK;
    public static final Locale WELSH_LOCALE = Locale.forLanguageTag("cy");

    public static String format(String dateStr, Locale locale) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "";
        }
        try {
            LocalDate date = LocalDate.parse(dateStr);
            int day = date.getDayOfMonth();
            if (locale.equals(ENGLISH_LOCALE)) {
                String suffix = getDaySuffix(day);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", ENGLISH_LOCALE);
                String formattedDate = date.format(formatter);
                return day + suffix + " " + formattedDate;
            } else if (locale.equals(WELSH_LOCALE)) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", WELSH_LOCALE);
                return date.format(formatter);
            } else {
                return dateStr;
            }
        } catch (Exception e) {
            LOGGER.error("UKDateFormatter - Error formatting date: {}", dateStr, e);
            return dateStr;
        }
    }

    private static String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
}
