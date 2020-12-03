package uk.gov.hmcts.probate.services.consumer;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.annotations.PactFolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.json.JSONException;
import org.junit.After;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.config.PDFServiceConfiguration;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.probate.services.businessdocuments.services.FileSystemResourceService;
import uk.gov.hmcts.probate.services.businessdocuments.services.PDFGenerationService;
import uk.gov.hmcts.reform.pdf.service.client.GeneratePdfRequest;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;
import uk.gov.hmcts.reform.probate.model.documents.Section;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "rpePdfService_PDFGenerationEndpointV2", port = "5500")
@PactFolder("pacts")
@SpringBootTest
public class PdfServiceConsumerTest {

    private static final String HTML = ".html";

    @Autowired
    PDFGenerationService pdfGenerationService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private PDFServiceConfiguration pdfServiceConfiguration;

    @Autowired
    private FileSystemResourceService fileSystemResourceService;

    @BeforeEach
    public void setUpEachTest() throws InterruptedException, IOException {
        Thread.sleep(2000);
    }

    @After
    void teardown() {
        Executor.closeIdleConnections();
    }

    // TBD consumer 'Name'
    @Pact(provider = "rpePdfService_PDFGenerationEndpointV2", consumer = "probate_businessService")
    RequestResponsePact generatePdfFromTemplate(PactDslWithProvider builder) throws JSONException, IOException {
        // @formatter:off

        return builder
            .given("A request to generate a Probate PDF document")
            .uponReceiving("A request to generate a Probate PDF document")
            .method("POST")
            //.headers(SERVICE_AUTHORIZATION_HEADER, someServiceAuthToken)
            .body(createJsonObject(buildGenerateDocumentRequest(DocumentType.CHECK_ANSWERS_SUMMARY.getTemplateName(), answersSummary())),
                "application/vnd.uk.gov.hmcts.pdf-service.v2+json;charset=UTF-8")
            .path("/pdfs")
            .willRespondWith()
            .withBinaryData("".getBytes(), "application/octet-stream")
            .matchHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/pdf")
            .status(HttpStatus.SC_OK)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "generatePdfFromTemplate")
    public void verifyGeneratePdfFromTemplatePact() throws IOException, JSONException {
        byte[] response = pdfGenerationService.generatePdf(answersSummary(), DocumentType.CHECK_ANSWERS_SUMMARY);

    }

    protected String createJsonObject(Object obj) throws JSONException, IOException {
        return objectMapper.writeValueAsString(obj);
    }

    private CheckAnswersSummary answersSummary() {
        CheckAnswersSummary summary = new CheckAnswersSummary();
        summary.setMainParagraph("paragraph");
        summary.setPageTitle("title");

        List<Section> sectionList = new ArrayList<Section>();
        Section section = new Section();
        section.setTitle("title");
        sectionList.add(section);

        summary.setSections(sectionList);

        return summary;
    }

    private GeneratePdfRequest buildGenerateDocumentRequest(String templateName, CheckAnswersSummary businessDocument)
        throws JsonProcessingException {
        String templatePath = pdfServiceConfiguration.getTemplatesDirectory() + templateName + HTML;
        String templateAsString = fileSystemResourceService.getFileFromResourceAsString(templatePath);

        Map<String, Object> paramMap = pdfGenerationService.asMap(objectMapper.writeValueAsString(businessDocument));
        return new GeneratePdfRequest(templateAsString, paramMap);

    }
}
