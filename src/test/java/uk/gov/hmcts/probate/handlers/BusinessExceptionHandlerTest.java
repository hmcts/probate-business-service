package uk.gov.hmcts.probate.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import uk.gov.hmcts.probate.handlers.BusinessExceptionHandler;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.BusinessDocumentException;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.PDFGenerationException;
import uk.gov.hmcts.probate.services.businessdocuments.model.ErrorResponse;
import uk.gov.hmcts.reform.pdf.service.client.exception.PDFServiceClientException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class BusinessExceptionHandlerTest {

    private static final String EXCEPTION_MESSAGE = "Message";

    private PDFServiceClientException pdfClientException;

    @Mock
    Exception exception;

    private FileSystemException fileSystemException;

    private BusinessDocumentException businessDocumentException;

    private PDFGenerationException pdfGenerationException;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @Mock
    private WebRequest mockWebRequest;
    @Mock
    private HttpHeaders mockHttpHeaders;

    @InjectMocks
    private BusinessExceptionHandler businessExceptionHandler;

    @Before
    public void setup() {
        initMocks(this);
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
    public void shouldConvertBusinessDocumentExceptionsToInternalServerErrorCodes() {
        businessDocumentException = new BusinessDocumentException(EXCEPTION_MESSAGE, exception);
        ResponseEntity<ErrorResponse> response = businessExceptionHandler.handle(businessDocumentException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(BusinessExceptionHandler.BUSINESS_DOC_ERROR, response.getBody().getError());
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
