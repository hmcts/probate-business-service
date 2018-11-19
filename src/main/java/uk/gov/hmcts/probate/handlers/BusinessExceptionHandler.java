package uk.gov.hmcts.probate.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.BusinessDocumentException;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.PDFGenerationException;
import uk.gov.hmcts.probate.services.businessdocuments.model.ErrorResponse;
import uk.gov.hmcts.reform.pdf.service.client.exception.PDFServiceClientException;

@Slf4j
@ControllerAdvice
@ResponseBody
class BusinessExceptionHandler {

    public static final String BUSINESS_DOC_ERROR = "Business Doc Error";
    public static final String PDF_CLIENT_ERROR = "PDF Client Error";
    public static final String FILE_SYSTEM_ERROR = "File System Error";
    public static final String PDF_GENERATION_EXCEPTION = "PDF Generation Error";


    @ExceptionHandler(BusinessDocumentException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessDocumentException exception) {

        log.warn("Business Document exception: {}", exception.getMessage(), exception);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), BUSINESS_DOC_ERROR, exception.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.valueOf(errorResponse.getCode()));
    }

    @ExceptionHandler(PDFServiceClientException.class)
    public ResponseEntity<ErrorResponse> handle(PDFServiceClientException exception) {
        log.warn("PDF Service Client exception: {}", exception.getMessage(), exception);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), PDF_CLIENT_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.valueOf(errorResponse.getCode()));
    }

    @ExceptionHandler(FileSystemException.class)
    public ResponseEntity<ErrorResponse> handle(FileSystemException exception) {
        log.warn("File system exception: {}", exception.getMessage(), exception);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), FILE_SYSTEM_ERROR, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.valueOf(errorResponse.getCode()));
    }

    @ExceptionHandler(PDFGenerationException.class)
    public ResponseEntity<ErrorResponse> handle(PDFGenerationException exception) {
        log.warn("PDF generation exception: {}", exception.getMessage(), exception);

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), PDF_GENERATION_EXCEPTION, exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.valueOf(errorResponse.getCode()));
    }

}
