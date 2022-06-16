package uk.gov.hmcts.probate.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PDFServiceConfigurationTest {

    @Autowired
    PDFServiceClient pdfServiceClient;

    @Autowired
    PDFServiceConfiguration pdfServiceConfiguration;

    @Test
    public void shouldInstantiatePdfServiceClass() {
        assertNotNull(pdfServiceClient);
    }

    @Test
    public void shouldSetPDFGenerationServiceProperties() {
        assertEquals("templates/pdf/", pdfServiceConfiguration.getTemplatesDirectory());
        assertEquals("http://localhost:5500", pdfServiceConfiguration.getUrl());
        assertEquals("/api/v2/pdf-generator/html", pdfServiceConfiguration.getPdfApi());

    }


}
