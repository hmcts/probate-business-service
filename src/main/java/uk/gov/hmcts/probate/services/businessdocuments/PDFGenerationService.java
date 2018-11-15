package uk.gov.hmcts.probate.services.businessdocuments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.services.businessdocuments.model.CheckAnswersSummary;
import uk.gov.hmcts.probate.services.exceptions.PDFGenerationException;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class PDFGenerationService {
    public static final String HTML = ".html";
    private final FileSystemResourceService fileSystemResourceService;
    private final PDFServiceConfiguration pdfServiceConfiguration;
    private final ObjectMapper objectMapper;
    private final PDFServiceClient pdfServiceClient;

    public byte[] generatePdf(CheckAnswersSummary checkAnswersSummary, DocumentType documentType) {

        byte[] postResult;

        try {
            postResult = generateFromHtml(checkAnswersSummary, documentType.getTemplateName());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new PDFGenerationException(e.getMessage(), e);
        }
        return postResult;
    }

    private byte[] generateFromHtml(CheckAnswersSummary checkAnswersSummary, String templateName) throws JsonProcessingException {

        String templatePath = pdfServiceConfiguration.getTemplatesDirectory() + templateName + HTML;
        String templateAsString = fileSystemResourceService.getFileFromResourceAsString(templatePath);

        Map<String, Object> paramMap = asMap(objectMapper.writeValueAsString(checkAnswersSummary));

        return pdfServiceClient.generateFromHtml(templateAsString.getBytes(), paramMap);
    }

    private Map<String, Object> asMap(String placeholderValues) {
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
