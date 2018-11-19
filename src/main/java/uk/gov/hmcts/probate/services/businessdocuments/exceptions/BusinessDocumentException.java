package uk.gov.hmcts.probate.services.businessdocuments.exceptions;

public class BusinessDocumentException extends RuntimeException{

    public BusinessDocumentException(String message) {
        super(message);
    }

    public BusinessDocumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
