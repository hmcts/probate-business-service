package uk.gov.hmcts.probate.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.pdf.service.client.PDFServiceClient;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PDFServiceConfigurationTest {

    @Autowired
    PDFServiceClient pdfServiceClient;

    @Autowired
    PDFServiceConfiguration pdfServiceConfiguration;

    @Test
    public void shouldInstantiatePdfServiceClass() {
        assertThat(pdfServiceClient, is(notNullValue()));
    }

    @Test
    public void shouldSetPDFGenerationServiceProperties() {
        assertThat(pdfServiceConfiguration.getTemplatesDirectory(),  equalTo("templates/pdf/"));
        assertThat(pdfServiceConfiguration.getUrl(), equalTo("http://localhost:5500"));
        assertThat(pdfServiceConfiguration.getPdfApi(), equalTo("/api/v2/pdf-generator/html"));
        assertThat(pdfServiceConfiguration.getDefaultDisplayFilename(), equalTo("dummy.pdf"));
        assertThat(pdfServiceConfiguration.getGrantSignatureBase64(), equalTo("dummy_hash"));

    }


}
