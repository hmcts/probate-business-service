package uk.gov.hmcts.probate.functional;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.reform.probate.model.documents.BulkScanCoverSheet;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Test class to compare the contents of the inbound JSON file against the content of the generated
 * PDF document. Since we have limited functionality for testing by using PDFBox we simply test to
 * see if the strings of content exist in the pdf document when its generated.
 * 
 * Particular attention is applied to the CCD reference number, applicants address and registry 
 * values being correct in the PDF from test form data.
 */
@RunWith(SerenityRunner.class)
public class BulkScanCoverSheetPDFTest extends PDFIntegrationBase<BulkScanCoverSheet> {

    private static final String BULK_SCAN_COVER_SHEET_PDF_URL = "/businessDocument/generateBulkScanCoverSheetPDF";
    private static final String BULK_SCAN_COVER_SHEET_VALUES = "bulkScanCoverSheet.json";

    @Test
    public void shouldPassCoreValues() throws Exception {
        String pdfContentAsString = pdfContentAsString(BULK_SCAN_COVER_SHEET_VALUES, BULK_SCAN_COVER_SHEET_PDF_URL);
        BulkScanCoverSheet bulkScanCoverSheet = getJSONObject(BULK_SCAN_COVER_SHEET_VALUES, BulkScanCoverSheet.class);
        validatePDFContent(pdfContentAsString, bulkScanCoverSheet);
    }

    private void validatePDFContent(String pdfContentAsString, BulkScanCoverSheet bulkScanCoverSheet) {
    	assertContent(pdfContentAsString, bulkScanCoverSheet.getApplicantAddressIntro());
        assertContent(pdfContentAsString, bulkScanCoverSheet.getApplicantNameIntro());
        assertContent(pdfContentAsString, bulkScanCoverSheet.getApplicantAddress());
        assertContent(pdfContentAsString, bulkScanCoverSheet.getApplicantName());
        assertContent(pdfContentAsString, BulkScanCoverSheet.DEFAULT_CASE_REFERENCE_INTRO);
        assertContent(pdfContentAsString, bulkScanCoverSheet.getCaseReference());
        assertContent(pdfContentAsString, BulkScanCoverSheet.DEFAULT_SUBMIT_ADDRESS_INTRO);
        assertContent(pdfContentAsString, bulkScanCoverSheet.getSubmitAddress());
    }
    
}
