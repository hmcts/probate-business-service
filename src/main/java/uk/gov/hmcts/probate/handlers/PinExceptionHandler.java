package uk.gov.hmcts.probate.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.hmcts.probate.services.businessdocuments.model.ErrorResponse;
import uk.gov.hmcts.probate.services.pin.exceptions.PhonePinException;

@Slf4j
@ControllerAdvice
@ResponseBody
public class PinExceptionHandler {
    public static final String PHONE_PIN_EXCEPTION = "PhonePin Error";

    @ExceptionHandler(PhonePinException.class)
    public ResponseEntity<ErrorResponse> handle(PhonePinException exception) {
        log.warn("PhonePin exception: {}", exception.getMessage(), exception);

        ErrorResponse errorResponse =
            new ErrorResponse(HttpStatus.BAD_REQUEST.value(), PHONE_PIN_EXCEPTION, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.valueOf(errorResponse.getCode()));
    }
}
