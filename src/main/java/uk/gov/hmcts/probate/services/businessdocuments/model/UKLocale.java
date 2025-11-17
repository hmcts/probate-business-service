package uk.gov.hmcts.probate.services.businessdocuments.model;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum UKLocale {
    ENGLISH(Locale.UK),
    WELSH(Locale.forLanguageTag("cy"));

    private final Locale locale;

    UKLocale(Locale locale) {
        this.locale = locale;
    }
}
