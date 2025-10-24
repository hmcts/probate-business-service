package uk.gov.hmcts.probate.services.invitation;

import org.junit.jupiter.api.Test;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UKDateFormatterTest {
    @Test
    void testFormatEnglishLocale() {
        String result = UKDateFormatter.format("2025-10-21", UKDateFormatter.ENGLISH_LOCALE);
        assertEquals("21st October 2025", result);
    }

    @Test
    void testFormatWelshLocale() {
        String result = UKDateFormatter.format("2025-10-21", UKDateFormatter.WELSH_LOCALE);
        assertEquals("21 Hydref 2025", result);
    }

    @Test
    void testFormatNullOrEmpty() {
        assertEquals("", UKDateFormatter.format(null, UKDateFormatter.ENGLISH_LOCALE));
        assertEquals("", UKDateFormatter.format("", UKDateFormatter.ENGLISH_LOCALE));
    }

    @Test
    void testFormatInvalidDate() {
        String invalidDate = "not-a-date";
        assertEquals(invalidDate, UKDateFormatter.format(invalidDate, UKDateFormatter.ENGLISH_LOCALE));
    }

    @Test
    void testFormatOtherLocale() {
        String dateStr = "2025-10-21";
        Locale otherLocale = Locale.FRANCE;
        assertEquals(dateStr, UKDateFormatter.format(dateStr, otherLocale));
    }

    @Test
    void testDaySuffixes() {
        assertEquals("1st October 2025", UKDateFormatter.format("2025-10-01", UKDateFormatter.ENGLISH_LOCALE));
        assertEquals("2nd October 2025", UKDateFormatter.format("2025-10-02", UKDateFormatter.ENGLISH_LOCALE));
        assertEquals("3rd October 2025", UKDateFormatter.format("2025-10-03", UKDateFormatter.ENGLISH_LOCALE));
        assertEquals("4th October 2025", UKDateFormatter.format("2025-10-04", UKDateFormatter.ENGLISH_LOCALE));
        assertEquals("11th October 2025", UKDateFormatter.format("2025-10-11", UKDateFormatter.ENGLISH_LOCALE));
        assertEquals("13th October 2025", UKDateFormatter.format("2025-10-13", UKDateFormatter.ENGLISH_LOCALE));
    }
}
