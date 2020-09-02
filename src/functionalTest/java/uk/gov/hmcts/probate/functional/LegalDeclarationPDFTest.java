package uk.gov.hmcts.probate.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.reform.probate.model.documents.LegalDeclaration;

/**
 * Test class to compare the contents of the inbound JSON file against the content of the generated
 * PDF document. Since we have limited functionality for testing by using PDFBox we simply test to
 * see if the strings of content exist in the pdf document when its generated.
 *
 * We concaternate the question and answer together before we look for the corresponding pdf string.
 * When we have a multi answer question we will look for all strings answers belonging to that question.
 */
@RunWith(SpringIntegrationSerenityRunner.class)
public class LegalDeclarationPDFTest extends PDFIntegrationBase<LegalDeclaration> {

    private static final String SIMPLE_LEGAL_DECLARTION = "validLegalDeclaration.json";
    private static final String MULTIPLE_LEGAL_DECLARTION = "validLegalDeclarationForMultipleExecutors.json";
    private final String CHECK_LEGAL_DECLARATION_PDF_URL = "/businessDocument/generateLegalDeclarationPDF";

    @Test
    public void shouldProduceDeclarationForSingleExecutor() throws Exception {
        String pdfContentAsString = pdfContentAsString(SIMPLE_LEGAL_DECLARTION, CHECK_LEGAL_DECLARATION_PDF_URL);
        LegalDeclaration legalDeclaration = getJSONObject(SIMPLE_LEGAL_DECLARTION, LegalDeclaration.class);
        validatePDFContent(pdfContentAsString, legalDeclaration);
    }

    @Test
    public void shouldProduceDeclarationForMultipleExecutors() throws Exception {
        String pdfContentAsString = pdfContentAsString(MULTIPLE_LEGAL_DECLARTION, CHECK_LEGAL_DECLARATION_PDF_URL);
        LegalDeclaration legalDeclaration = getJSONObject(MULTIPLE_LEGAL_DECLARTION, LegalDeclaration.class);
        validatePDFContent(pdfContentAsString, legalDeclaration);
    }


    private void validatePDFContent(String pdfContentAsString, LegalDeclaration legalDeclaration) {
        assertContent(pdfContentAsString, legalDeclaration.getDateCreated());
        assertContent(pdfContentAsString, legalDeclaration.getDeceased());

        legalDeclaration.getDeclarations().forEach(declaration -> {declaration.getHeaders().forEach(header -> {
            assertContent(pdfContentAsString, header.toUpperCase());
        });
     });

        legalDeclaration.getDeclarations().forEach(declaration -> {declaration.getSections().forEach(section -> {
            assertContent(pdfContentAsString, section.getTitle());
            section.getDeclarationItems().forEach(declarationItem -> {
                assertContent(pdfContentAsString, declarationItem.getTitle());
                declarationItem.getValues().forEach(value -> {
                    assertContent(pdfContentAsString, value);
                });
            });
        });
     });
  }


}
