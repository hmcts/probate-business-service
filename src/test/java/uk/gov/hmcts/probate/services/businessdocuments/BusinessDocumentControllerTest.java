package uk.gov.hmcts.probate.services.businessdocuments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.hmcts.probate.services.businessdocuments.model.CheckAnswersSummary;

@RunWith(MockitoJUnitRunner.class)
public class BusinessDocumentControllerTest {

    BusinessDocumentController businessDocumentController;

    @Mock
    PDFGenerationService pdfGenerationService;

    CheckAnswersSummary checkAnswersSummary;

    @Before
    public void setUp() {
        businessDocumentController = new BusinessDocumentController(pdfGenerationService);
        checkAnswersSummary = new CheckAnswersSummary();

    }

    @Test
    public void shouldGenerateACheckAnswersSummaryPDF() {
        byte[] result = businessDocumentController.generateCheckAnswersSummaryPDF(checkAnswersSummary, "authorisation");
    }
}
