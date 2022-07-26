package uk.gov.hmcts.probate.services.businessdocuments.exceptions;

public class PDFGenerationException extends RuntimeException {

    public PDFGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
