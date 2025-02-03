package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.layout.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.PDFGenerationException;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.probate.model.documents.BusinessDocument;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class PDFGenerationService {
    private static final String HTML = ".html";
    public static final int ROLE = 200;
    private final FileSystemResourceService fileSystemResourceService;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final ObjectMapper objectMapper;
    private final PDFServiceClient pdfServiceClient;

    public byte[] generatePdf(BusinessDocument businessDocument, DocumentType documentType) {

        byte[] postResult;

        try {
            postResult = generateFromHtml(businessDocument, documentType.getTemplateName());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new PDFGenerationException(e.getMessage(), e);
        }
        return postResult;
    }

    byte[] generateFromHtml(BusinessDocument businessDocument, String templateName)
        throws IOException {
        String templatePath = pdfServiceConfiguration.getTemplatesDirectory() + templateName + HTML;
        String templateAsString = fileSystemResourceService.getFileFromResourceAsString(templatePath);
        Map<String, Object> paramMap = asMap(objectMapper.writeValueAsString(businessDocument));
        // Generate PDF using existing service
        byte[] pdfBytes = pdfServiceClient.generateFromHtml(templateAsString.getBytes(), paramMap);
        // Add tagging for accessibility using iText
        ByteArrayOutputStream taggedPdfOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(taggedPdfOutputStream);
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfBytes)), writer);
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);
        document.setProperty(ROLE, StandardRoles.DOCUMENT);

        document.close();
        pdfDocument.close();
        writer.close();
        byte[] result = taggedPdfOutputStream.toByteArray();
        verifyPdfBytes(result); // Verify the integrity of the generated PDF bytes
        return result;
    }

    private void verifyPdfBytes(byte[] pdfBytes) throws IOException {
        try (PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfBytes))) {
            PdfDocument pdfDocument = new PdfDocument(reader);
            if (pdfDocument.getNumberOfPages() == 0) {
                throw new IOException("Generated PDF has no pages");
            }
        }
    }

    public Map<String, Object> asMap(String placeholderValues) {
        try {

            ObjectMapper mappy = new ObjectMapper();
            return mappy.readValue(placeholderValues,
                new TypeReference<HashMap<String, Object>>() {
                });
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new PDFGenerationException(e.getMessage(), e);
        }
    }


}
