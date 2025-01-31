package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
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
import uk.gov.hmcts.probate.services.businessvalidation.validators.CustomEventListener;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.probate.model.documents.BusinessDocument;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

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
        String templateName = "testTemplate";
        String templatePath = "/templates/" + templateName + ".html";
        String templateContent = "<html><body>Test PDF Content</body></html>";
        byte[] mockPdfBytes = createValidPdfBytes();
        String businessDocumentJson = "{\"key\": \"value\"}";
        Map<String, Object> paramMap = Map.of("key", "value");

        // Mock dependencies
        when(pdfServiceConfiguration.getTemplatesDirectory()).thenReturn("/templates/");
        when(fileSystemResourceService.getFileFromResourceAsString(templatePath)).thenReturn(templateContent);
        when(objectMapper.writeValueAsString(businessDocument)).thenReturn(businessDocumentJson);
        when(pdfServiceClient.generateFromHtml(templateContent.getBytes(), paramMap)).thenReturn(mockPdfBytes);

        byte[] result = pdfGenerationService.generateFromHtml(businessDocument, templateName);

        // Assertions
        assertNotNull(result, "Generated PDF should not be null");
        assertTrue(result.length > 0, "Generated PDF should not be empty");

        // Verify
        verify(fileSystemResourceService).getFileFromResourceAsString(templatePath);
        verify(objectMapper).writeValueAsString(businessDocument);
        verify(pdfServiceClient).generateFromHtml(templateContent.getBytes(), paramMap);
    }

    private byte[] createValidPdfBytes() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Test PDF Content"));
        document.close();
        return outputStream.toByteArray();
    }

    @Test
    void shouldTagTablesForAccessibility() throws Exception {
        // Create a mock PdfDocument with a table and cells
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        Table table = new Table(1);
        Cell cell = new Cell().add(new Paragraph("Test Cell"));
        table.addCell(cell);
        document.add(table);
        document.close();


        PdfDocument readPdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(outputStream
            .toByteArray())));

        pdfGenerationService.tagTablesForAccessibility(readPdfDocument);

        for (int i = 1; i <= readPdfDocument.getNumberOfPages(); i++) {
            PdfPage page = readPdfDocument.getPage(i);
            CustomEventListener listener = new CustomEventListener();
            PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
            processor.processPageContent(page);

            for (IElement element : listener.getElements()) {
                if (element instanceof Table) {
                    Table taggedTable = (Table) element;
                    assertEquals(StandardRoles.TABLE, taggedTable.getAccessibilityProperties().getRole());

                    for (IElement cellElement : taggedTable.getChildren()) {
                        if (cellElement instanceof Cell) {
                            assertEquals(StandardRoles.TD, ((Cell) cellElement).getAccessibilityProperties().getRole());
                        }
                    }
                }
            }
        }

        readPdfDocument.close();
    }
}

