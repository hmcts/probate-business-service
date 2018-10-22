package uk.gov.hmcts.probate.services.document.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class DocumentDeletionException extends RuntimeException {
}