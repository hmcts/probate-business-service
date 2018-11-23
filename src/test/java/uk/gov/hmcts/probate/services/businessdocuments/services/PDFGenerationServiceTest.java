package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.services.businessdocuments.model.CheckAnswersSummary;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.PDFGenerationException;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.probate.services.businessdocuments.services.FileSystemResourceService;
import uk.gov.hmcts.probate.services.businessdocuments.services.PDFGenerationService;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PDFGenerationServiceTest {

    @Mock
    private RestOperations restOperations;

    @Mock
    private FileSystemResourceService fileSystemResourceService;

    @Mock
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CheckAnswersSummary checkAnswerSummary;

    private PDFGenerationService pdfGenerationService;

    @Mock
    private PDFServiceClient pdfServiceClient;

    private String someJSON = "{\"test\":\"json\"}";

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        pdfGenerationService = new PDFGenerationService(fileSystemResourceService, pdfServiceConfiguration, objectMapper, pdfServiceClient);
        when(pdfServiceConfiguration.getTemplatesDirectory()).thenReturn("templateDirectory");
        when(fileSystemResourceService.getFileFromResourceAsString(Mockito.anyString())).thenReturn("templateAsString");
    }

    @Test(expected = PDFGenerationException.class)
    public void shouldCatchJsonProcessingExceptionAndRethrowAsPDFGenerationException () throws Exception {
        byte[] pdfInBytes = pdfGenerationService.generatePdf(checkAnswerSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
    }

    @Test(expected = FileSystemException.class)
    public void shouldThrowFileSystemException () throws Exception {
        when(fileSystemResourceService.getFileFromResourceAsString(Mockito.anyString())).thenThrow(new FileSystemException("File System Exception"));
        byte[] pdfInBytes = pdfGenerationService.generatePdf(checkAnswerSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
    }

    @Test
    public void shouldProcessAValidPDFRequest() throws Exception {
        CheckAnswersSummary checkAnswersSummary = new CheckAnswersSummary();
        byte[] pdfInBytes = pdfGenerationService.generatePdf(checkAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
        verify(pdfServiceClient).generateFromHtml(Mockito.any(), Mockito.anyMap());
    }

    @Test
    public void shouldThrowPDFGenerationExceptionOnAaMapMethod() {
       // byte[] pdfInBytes = pdfGenerationService.generatePdf(checkAnswerSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
    }
}
