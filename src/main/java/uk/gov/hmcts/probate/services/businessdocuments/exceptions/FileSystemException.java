package uk.gov.hmcts.probate.services.businessdocuments.exceptions;

public class FileSystemException extends RuntimeException {

    public FileSystemException(String message) {
        super(message);
    }

    public FileSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
