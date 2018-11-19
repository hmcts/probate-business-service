package uk.gov.hmcts.probate.services.businessdocuments.model;
import lombok.Getter;

@Getter
public enum DocumentType {
    CHECK_ANSWERS_SUMMARY("checkAnswersSummary"),
    LEGAL_DECLARATION("legalDeclaration");

    private final String templateName;

    DocumentType(String templateName) {
        this.templateName = templateName;
    }
}
