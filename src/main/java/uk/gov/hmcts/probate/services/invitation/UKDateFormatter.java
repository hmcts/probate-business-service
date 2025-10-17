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
                String suffix;
                switch (day) {
                    case 1: case 21: case 31: suffix = "st"; break;
                    case 2: case 22: suffix = "nd"; break;
                    case 3: case 23: suffix = "rd"; break;
                    default: suffix = "th";
                }
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
}
