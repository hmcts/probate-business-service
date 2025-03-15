package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.layout.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestOperations;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.PDFGenerationException;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.probate.model.documents.BusinessDocument;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
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
    @Mock
    private BusinessDocument businessDocument;

    private String someJSON = "{\"test\":\"json\"}";

    @BeforeEach
    public void setUp() {
        try {
            when(objectMapper.writeValueAsString(any(CheckAnswersSummary.class))).thenReturn(someJSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        pdfGenerationService =
            spy(new PDFGenerationService(fileSystemResourceService, pdfServiceConfiguration, objectMapper,
                pdfServiceClient));
        when(pdfServiceConfiguration.getTemplatesDirectory()).thenReturn("templateDirectory");
        when(fileSystemResourceService.getFileFromResourceAsString(anyString())).thenReturn("templateAsString");
    }

    @Test
    void shouldCatchJsonProcessingExceptionAndRethrowAsPDFGenerationException() throws Exception {
        try {
            when(objectMapper.writeValueAsString(any(CheckAnswersSummary.class))).thenReturn("");
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
        when(fileSystemResourceService.getFileFromResourceAsString(anyString()))
            .thenThrow(new FileSystemException("File System Exception"));
        assertThrows(FileSystemException.class, () -> {
            byte[] pdfInBytes =
                pdfGenerationService.generatePdf(mockCheckAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
        });
    }

    @Test
    void shouldProcessAValidPDFRequest() throws Exception {
        byte[] mockPdfBytes = new byte[]{1, 2, 3, 4};

        doReturn(mockPdfBytes).when(pdfGenerationService).generateFromHtml(any(), any());

        // Call the method under test
        byte[] pdfInBytes = pdfGenerationService.generatePdf(mockCheckAnswersSummary,
            DocumentType.CHECK_ANSWERS_SUMMARY);

        // Assertions
        assertNotNull(pdfInBytes, "Generated PDF should not be null");
        assertTrue(pdfInBytes.length > 0, "Generated PDF should not be empty");

        // Verify that the private method was called (indirectly)
        verify(pdfGenerationService).generatePdf(mockCheckAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
    }

    @Test
    void shouldThrowPDFGenerationExceptionWhenGenerateFromHtmlFails() throws Exception {
        doThrow(new JsonProcessingException("JSON Processing Failed") {})
            .when(pdfGenerationService)
            .generateFromHtml(any(), any());

        PDFGenerationException exception = assertThrows(PDFGenerationException.class, () -> {
            pdfGenerationService.generatePdf(mockCheckAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);
        });


        assertNotNull(exception, "Exception should not be null");
        assertEquals("JSON Processing Failed", exception.getCause().getMessage(),
            "Exception message should match");

        verify(pdfGenerationService).generateFromHtml(any(), any());
    }

    @Test
    void shouldGeneratePDFSuccessfully() throws Exception {
        // Given
        String templateName = "testTemplate";
        String templatePath = "/templates/" + templateName + ".html";
        String templateContent = "<html><body><table><tr><td>column1</td><td>column2</td></tr></table></body></html>";
        String businessDocumentJson = "{\"key\": \"value\"}";
        byte[] mockPdfBytes = createValidPdfBytes(templateContent);

        // Mock dependencies
        when(pdfServiceConfiguration.getTemplatesDirectory()).thenReturn("/templates/");
        when(fileSystemResourceService.getFileFromResourceAsString(templatePath)).thenReturn(templateContent);
        when(objectMapper.writeValueAsString(any())).thenReturn(businessDocumentJson);
        when(pdfServiceClient.generateFromHtml(any(), any())).thenReturn(mockPdfBytes);

        // When
        byte[] result = pdfGenerationService.generateFromHtml(businessDocument, templateName);

        // Then
        assertNotNull(result, "Generated PDF should not be null");
        assertTrue(result.length > 0, "Generated PDF should not be empty");

        // Verify dependencies were called
        verify(fileSystemResourceService).getFileFromResourceAsString(templatePath);
        verify(objectMapper).writeValueAsString(any());
        verify(pdfServiceClient).generateFromHtml(any(), any());

        // Verify the generated PDF contains valid accessibility elements
        verifyPdfAccessibility(result);
    }

    private byte[] createValidPdfBytes(String htmlContent) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setTagged(); // Enable accessibility
        Document document = new Document(pdfDocument);

        ConverterProperties properties = new ConverterProperties();
        HtmlConverter.convertToPdf(new ByteArrayInputStream(htmlContent.getBytes()), pdfDocument, properties);

        document.close();
        pdfDocument.close();
        return outputStream.toByteArray();
    }

    private void verifyPdfAccessibility(byte[] pdfBytes) throws IOException {
        try (PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes))) {
            PdfDocument pdfDocument = new PdfDocument(reader);
            assertTrue(pdfDocument.isTagged(), "PDF should be tagged for accessibility");
            assertTrue(pdfDocument.getNumberOfPages() > 0, "PDF should have at least one page");
        }
    }
}

