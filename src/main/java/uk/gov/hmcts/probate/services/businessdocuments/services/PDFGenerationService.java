package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.attach.impl.DefaultTagWorkerFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.StringLoader;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.PDFGenerationException;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;
import uk.gov.hmcts.reform.probate.model.documents.BusinessDocument;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class PDFGenerationService {
    private static final String HTML = ".html";
    //public static final int ROLE = 200;
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
        pdfServiceClient.generateFromHtml(templateAsString.getBytes(), paramMap);
        PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader()).build();
        PebbleTemplate template = engine.getTemplate(templateAsString);
        StringWriter writer = new StringWriter();
        template.evaluate(writer, paramMap);

        // Generate PDF with tags
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter pdfwriter = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(pdfwriter);
        pdfDocument.setTagged();
        // Configure for accessibility
        ConverterProperties props = new ConverterProperties();
        props.setTagWorkerFactory(new DefaultTagWorkerFactory());
        HtmlConverter.convertToPdf(writer.toString(), pdfDocument, props);
        pdfDocument.close();
        pdfwriter.close();

        return outputStream.toByteArray();
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
