package uk.gov.hmcts.probate.services.businessdocuments.controllers;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import org.json.JSONException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.probate.services.businessdocuments.services.PDFGenerationService;
import uk.gov.hmcts.reform.probate.model.documents.BulkScanCoverSheet;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;
import uk.gov.hmcts.reform.probate.model.documents.LegalDeclaration;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Provider("probate_businessservice_documents")
@RunWith(SpringRestPactRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "server.port=8123", "spring.application.name=PACT_TEST",
        "services.pdf.service.url=http://localhost:989"
})
public class BusinessDocumentControllerProviderTest extends ControllerProviderTest{


    @TestTarget
    @SuppressWarnings(value = "VisibilityModifier")
    public final Target target = new HttpTarget("http", "localhost", 8123, "/");

    @MockBean
    private PDFGenerationService pdfGenerationService;

    @State({"business service returns check your answers document with success",
            "business service returns check your answers document with success"})
    public void toReturnCheckAnswersSummaryWithSuccess() throws IOException, JSONException {

        when(pdfGenerationService.generatePdf( any(CheckAnswersSummary.class)
                , any(DocumentType.class)))
                .thenReturn("".getBytes());
    }

    @State({"business service returns legal declaration document with success",
            "business service returns legal declaration document with success"})
    public void toReturnLegalDeclarationWithSuccess() throws IOException, JSONException {

        when(pdfGenerationService.generatePdf( any(LegalDeclaration.class)
                , any(DocumentType.class)))
                .thenReturn("".getBytes());
    }

    @State({"business service returns bulk scan coversheet document with success",
            "business service returns bulk scan coversheet document with success"})
    public void toReturnBulkScanCoversheetWithSuccess() throws IOException, JSONException {

        when(pdfGenerationService.generatePdf( any(BulkScanCoverSheet.class)
                , any(DocumentType.class)))
                .thenReturn("".getBytes());
    }

    @State({"business service returns validation errors for invalid bulk scan coversheet",
            "business service returns validation errors for invalid bulk scan coversheet"})
    public void toReturnErrorForInvalidBulkScanCoversheet() throws IOException, JSONException {

    }
    @State({"business service returns validation errors for invalid check answers summary",
            "business service returns validation errors for invalid check answers summary"})
    public void toReturnErrorForInvalidCheckAnswersSummary() throws IOException, JSONException {

    }
    @State({"business service returns validation errors for invalid legal declaration",
            "business service returns validation errors for invalid legal declaration"})
    public void toReturnErrorForInvalidLegalDeclaration() throws IOException, JSONException {

    }
}
