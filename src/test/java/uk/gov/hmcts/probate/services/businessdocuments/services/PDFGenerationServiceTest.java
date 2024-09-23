package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestOperations;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.PDFGenerationException;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class PDFGenerationServiceTest {

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

    @BeforeEach
    public void setUp() {
        try {
            when(objectMapper.writeValueAsString(Mockito.any(CheckAnswersSummary.class))).thenReturn(someJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        pdfGenerationService =
            new PDFGenerationService(fileSystemResourceService, pdfServiceConfiguration, objectMapper,
                pdfServiceClient);
        when(pdfServiceConfiguration.getTemplatesDirectory()).thenReturn("templateDirectory");
        when(fileSystemResourceService.getFileFromResourceAsString(Mockito.anyString())).thenReturn("templateAsString");
    }

    @Test
    void shouldCatchJsonProcessingExceptionAndRethrowAsPDFGenerationException() throws Exception {
        try {
            when(objectMapper.writeValueAsString(Mockito.any(CheckAnswersSummary.class))).thenReturn("");
            assertThrows(PDFGenerationException.class, () -> {
                byte[] pdfInBytes =
                    pdfGenerationService.generatePdf(mockCheckAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldThrowFileSystemException() throws Exception {
        when(fileSystemResourceService.getFileFromResourceAsString(Mockito.anyString()))
            .thenThrow(new FileSystemException("File System Exception"));
        assertThrows(FileSystemException.class, () -> {
            byte[] pdfInBytes =
                pdfGenerationService.generatePdf(mockCheckAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
        });
    }

    @Test
    void shouldProcessAValidPDFRequest() throws Exception {
        byte[] pdfInBytes =
            pdfGenerationService.generatePdf(mockCheckAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
        verify(pdfServiceClient).generateFromHtml(Mockito.any(), Mockito.anyMap());
    }

}
