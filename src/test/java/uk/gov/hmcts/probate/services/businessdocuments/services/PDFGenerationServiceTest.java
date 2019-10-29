package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestOperations;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.PDFGenerationException;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;

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
    private CheckAnswersSummary mockCheckAnswersSummary;

    private PDFGenerationService pdfGenerationService;

    @Mock
    private PDFServiceClient pdfServiceClient;

    private String someJSON = "{\"test\":\"json\"}";

    @Before
    public void setUp() {
        try {
            when(objectMapper.writeValueAsString(Mockito.any(CheckAnswersSummary.class))).thenReturn(someJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        pdfGenerationService = new PDFGenerationService(fileSystemResourceService, pdfServiceConfiguration, objectMapper, pdfServiceClient);
        when(pdfServiceConfiguration.getTemplatesDirectory()).thenReturn("templateDirectory");
        when(fileSystemResourceService.getFileFromResourceAsString(Mockito.anyString())).thenReturn("templateAsString");
    }

    @Test(expected = PDFGenerationException.class)
    public void shouldCatchJsonProcessingExceptionAndRethrowAsPDFGenerationException () throws Exception {
        try {
            when(objectMapper.writeValueAsString(Mockito.any(CheckAnswersSummary.class))).thenReturn("");
            byte[] pdfInBytes = pdfGenerationService.generatePdf(mockCheckAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = FileSystemException.class)
    public void shouldThrowFileSystemException () throws Exception {
        when(fileSystemResourceService.getFileFromResourceAsString(Mockito.anyString())).thenThrow(new FileSystemException("File System Exception"));
        byte[] pdfInBytes = pdfGenerationService.generatePdf(mockCheckAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
    }

    @Test
    public void shouldProcessAValidPDFRequest() throws Exception {
        byte[] pdfInBytes = pdfGenerationService.generatePdf(mockCheckAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
        verify(pdfServiceClient).generateFromHtml(Mockito.any(), Mockito.anyMap());
    }

}
