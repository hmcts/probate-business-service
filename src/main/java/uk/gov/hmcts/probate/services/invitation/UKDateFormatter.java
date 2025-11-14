package uk.gov.hmcts.probate.services.invitation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.probate.services.businessdocuments.model.UKLocale;

@Service
public class UKDateFormatter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UKDateFormatter.class);

    public String format(String dateStr, UKLocale ukLocale) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "";
        }
        try {
            LocalDate date = LocalDate.parse(dateStr);
            int day = date.getDayOfMonth();
            if (ukLocale == UKLocale.ENGLISH) {
                String suffix = getDaySuffix(day);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy",
                    UKLocale.ENGLISH.getLocale());
                String formattedDate = date.format(formatter);
                return day + suffix + " " + formattedDate;
            } else if (ukLocale == UKLocale.WELSH) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy",
                    UKLocale.WELSH.getLocale());
                return date.format(formatter);
            } else {
                return dateStr;
            }
        } catch (Exception e) {
            LOGGER.error("UKDateFormatter - Error formatting date: {}", dateStr, e);
            return dateStr;
        }
    }

    private String getDaySuffix(int day) {
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
