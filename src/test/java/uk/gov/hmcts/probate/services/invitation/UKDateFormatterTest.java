package uk.gov.hmcts.probate.services.invitation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UKDateFormatterTest {
    private final UKDateFormatter ukDateFormatter = new UKDateFormatter();

    @Test
    void testFormatEnglishLocale() {
        String result = ukDateFormatter.format("2025-10-21", UKDateFormatter.UKLocale.ENGLISH);
        assertEquals("21st October 2025", result);
    }

    @Test
    void testFormatWelshLocale() {
        String result = ukDateFormatter.format("2025-10-21", UKDateFormatter.UKLocale.WELSH);
        assertEquals("21 Hydref 2025", result);
    }

    @Test
    void testFormatNullOrEmpty() {
        assertEquals("", ukDateFormatter.format(null, UKDateFormatter.UKLocale.ENGLISH));
        assertEquals("", ukDateFormatter.format("", UKDateFormatter.UKLocale.ENGLISH));
    }

    @Test
    void testFormatInvalidDate() {
        String invalidDate = "not-a-date";
        assertEquals(invalidDate, ukDateFormatter.format(invalidDate, UKDateFormatter.UKLocale.ENGLISH));
    }

    @Test
    void testDaySuffixes() {
        assertEquals("1st October 2025", ukDateFormatter.format("2025-10-01",
            UKDateFormatter.UKLocale.ENGLISH));
        assertEquals("2nd October 2025", ukDateFormatter.format("2025-10-02",
            UKDateFormatter.UKLocale.ENGLISH));
        assertEquals("3rd October 2025", ukDateFormatter.format("2025-10-03",
            UKDateFormatter.UKLocale.ENGLISH));
        assertEquals("4th October 2025", ukDateFormatter.format("2025-10-04",
            UKDateFormatter.UKLocale.ENGLISH));
        assertEquals("11th October 2025", ukDateFormatter.format("2025-10-11",
            UKDateFormatter.UKLocale.ENGLISH));
        assertEquals("13th October 2025", ukDateFormatter.format("2025-10-13",
            UKDateFormatter.UKLocale.ENGLISH));
    }
}
