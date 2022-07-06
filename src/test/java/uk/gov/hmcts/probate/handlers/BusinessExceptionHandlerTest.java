package uk.gov.hmcts.probate.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.PDFGenerationException;
import uk.gov.hmcts.probate.services.businessdocuments.model.ErrorResponse;
import uk.gov.hmcts.reform.pdf.service.client.exception.PDFServiceClientException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class BusinessExceptionHandlerTest {

    private static final String EXCEPTION_MESSAGE = "Message";
    @Mock
    Exception exception;
    private PDFServiceClientException pdfClientException;
    private FileSystemException fileSystemException;

    private PDFGenerationException pdfGenerationException;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @Mock
    private WebRequest mockWebRequest;
    @Mock
    private HttpHeaders mockHttpHeaders;

    @InjectMocks
    private BusinessExceptionHandler businessExceptionHandler;

    @BeforeEach
    public void setup() {
        openMocks(this);
    }

    @Test
    public void shouldConvertPDFServiceClientExceptionsToInternalServerErrorCodes() {
        pdfClientException = new PDFServiceClientException(EXCEPTION_MESSAGE, exception);

        ResponseEntity<ErrorResponse> response = businessExceptionHandler.handle(pdfClientException);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(BusinessExceptionHandler.PDF_CLIENT_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

    @Test
    public void shouldConvertFileSytemExceptionsToInternalServerErrorCodes() {
        fileSystemException = new FileSystemException(EXCEPTION_MESSAGE, exception);
        ResponseEntity<ErrorResponse> response = businessExceptionHandler.handle(fileSystemException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(BusinessExceptionHandler.FILE_SYSTEM_ERROR, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

    @Test
    public void shouldConvertPDFGenerationExceptionsToInternalServerErrorCodes() {
        pdfGenerationException = new PDFGenerationException(EXCEPTION_MESSAGE, exception);
        ResponseEntity<ErrorResponse> response = businessExceptionHandler.handle(pdfGenerationException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(BusinessExceptionHandler.PDF_GENERATION_EXCEPTION, response.getBody().getError());
        assertEquals(EXCEPTION_MESSAGE, response.getBody().getMessage());
    }

}
