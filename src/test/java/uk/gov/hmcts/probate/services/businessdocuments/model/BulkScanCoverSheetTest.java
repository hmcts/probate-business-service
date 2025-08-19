package uk.gov.hmcts.probate.services.businessdocuments.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.services.businessdocuments.services.FileSystemResourceService;
import uk.gov.hmcts.reform.probate.model.documents.BulkScanCoverSheet;
import uk.gov.hmcts.reform.probate.model.documents.CheckListItemType;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BulkScanCoverSheetTest {

    public static final String VALID_BULK_SCAN_COVER_SHEET_JSON = "businessdocuments/validBulkScanCoverSheet.json";
    public static final String VALID_BULK_SCAN_COVER_SHEET_NO_CHECKLIST_ITEMS =
        "businessdocuments/validBulkScanCoverSheetNoCheckListItems.json";
    public static final String VALID_BULK_SCAN_COVER_SHEET_STATIC_TEXT_OVERRIDE_JSON =
        "businessdocuments/validBulkScanCoverSheetStaticTextOverride.json";
    public static final String INVALID_BULK_SCAN_COVER_SHEET_JSON = "businessdocuments/invalidBulkScanCoverSheet.json";

    public static final String VALID_COVER_SHEET_APPLICANT_ADDRESS_VALUE = "20 White City\nLondon\nW12 7PD";
    public static final String VALID_COVER_SHEET_APPLICANT_NAME_VALUE = "Joe Bloggs";
    public static final String VALID_COVER_SHEET_CASE_REFERENCE_VALUE = "1542-9021-4510-0350";
    public static final String VALID_COVER_SHEET_SUBMIT_ADDRESS_VALUE =
        "Probate Service\nPO BOX 123\nExela BSP Services\nHarlow\nCM19 5QS";
    public static final String VALID_COVER_SHEET_CHECKLIST_TEXT = "item text";
    public static final String VALID_COVER_SHEET_CHECKLIST_URL = "http://example-url.com";
    public static final String VALID_COVER_SHEET_CHECKLIST_BEFORE_LINK_TEXT = "text before link";
    public static final String VALID_COVER_SHEET_CHECKLIST_AFTER_LINK_TEXT = "text after link";
    public static final String VALID_COVERSHEET_NO_DOCS_REQUIRED_TEXT = "Based on the details in the application no"
        + " documents are required. However if documents are requested from you in the future, please send them along"
        + " with this cover sheet to the address below";
    public static final String VALID_COVER_SHEET_SEGMENT_TEXT = "Link1";

    private ObjectMapper objectMapper;

    private FileSystemResourceService fileSystemResourceService;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        fileSystemResourceService = new FileSystemResourceService();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldCreateACoverSheetInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_BULK_SCAN_COVER_SHEET_JSON);
        BulkScanCoverSheet coverSheet = objectMapper.readValue(optional.get().getFile(), BulkScanCoverSheet.class);
        assertNotNull(coverSheet);
        Set<ConstraintViolation<BulkScanCoverSheet>> violations = validator.validate(coverSheet);
        assertEquals(0, violations.size());
        assertEquals(BulkScanCoverSheet.DEFAULT_TITLE, coverSheet.getTitle());
        assertEquals(BulkScanCoverSheet.DEFAULT_APPLICANT_ADDRESS_INTRO, coverSheet.getApplicantAddressIntro());
        assertEquals(BulkScanCoverSheet.DEFAULT_APPLICANT_NAME_INTRO, coverSheet.getApplicantNameIntro());
        assertEquals(BulkScanCoverSheet.DEFAULT_CASE_REFERENCE_INTRO, coverSheet.getCaseReferenceIntro());
        assertEquals(BulkScanCoverSheet.DEFAULT_SUBMIT_ADDRESS_INTRO, coverSheet.getSubmitAddressIntro());
        assertEquals(VALID_COVER_SHEET_APPLICANT_ADDRESS_VALUE, coverSheet.getApplicantAddress());
        assertEquals(VALID_COVER_SHEET_APPLICANT_NAME_VALUE, coverSheet.getApplicantName());
        assertEquals(VALID_COVER_SHEET_CASE_REFERENCE_VALUE, coverSheet.getCaseReference());
        assertEquals(VALID_COVER_SHEET_SUBMIT_ADDRESS_VALUE, coverSheet.getSubmitAddress());
        assertEquals(VALID_COVER_SHEET_CHECKLIST_TEXT, coverSheet.getCheckListItems().get(0).getText());
        assertEquals(CheckListItemType.TEXT_ONLY, coverSheet.getCheckListItems().get(0).getType());
        assertEquals(VALID_COVER_SHEET_CHECKLIST_TEXT, coverSheet.getCheckListItems().get(1).getText());
        assertEquals(CheckListItemType.TEXT_WITH_LINK, coverSheet.getCheckListItems().get(1).getType());
        assertEquals(VALID_COVER_SHEET_CHECKLIST_URL, coverSheet.getCheckListItems().get(1).getUrl());
        assertEquals(VALID_COVER_SHEET_CHECKLIST_BEFORE_LINK_TEXT, coverSheet.getCheckListItems().get(1)
            .getBeforeLinkText());
        assertEquals(VALID_COVER_SHEET_CHECKLIST_AFTER_LINK_TEXT, coverSheet.getCheckListItems().get(1)
            .getAfterLinkText());
        assertEquals(CheckListItemType.TEXT_WITH_MULTIPLE_LINKS, coverSheet.getCheckListItems().get(2)
            .getType());
        assertEquals(VALID_COVER_SHEET_SEGMENT_TEXT, coverSheet.getCheckListItems().get(2)
            .getSegments().getFirst().getText());
        assertEquals(false, coverSheet.getNoDocumentsRequired());
    }

    @Test
    void shouldCreateACoverSheetInstanceWithStaticTextOverride() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_BULK_SCAN_COVER_SHEET_STATIC_TEXT_OVERRIDE_JSON);
        BulkScanCoverSheet coverSheet = objectMapper.readValue(optional.get().getFile(), BulkScanCoverSheet.class);
        assertNotNull(coverSheet);
        Set<ConstraintViolation<BulkScanCoverSheet>> violations = validator.validate(coverSheet);
        assertEquals(0, violations.size());
        assertEquals("Cover Sheet", coverSheet.getTitle());
        assertEquals("The applicants address", coverSheet.getApplicantAddressIntro());
        assertEquals("The applicants name", coverSheet.getApplicantNameIntro());
        assertEquals("This is the Case Reference Number", coverSheet.getCaseReferenceIntro());
        assertEquals("This is the place to send the documents", coverSheet.getSubmitAddressIntro());
        assertEquals(VALID_COVER_SHEET_APPLICANT_ADDRESS_VALUE, coverSheet.getApplicantAddress());
        assertEquals(VALID_COVER_SHEET_APPLICANT_NAME_VALUE, coverSheet.getApplicantName());
        assertEquals(VALID_COVER_SHEET_CASE_REFERENCE_VALUE, coverSheet.getCaseReference());
        assertEquals(VALID_COVER_SHEET_SUBMIT_ADDRESS_VALUE, coverSheet.getSubmitAddress());
    }

    @Test
    void shouldFailToCreateACoverSheetInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(INVALID_BULK_SCAN_COVER_SHEET_JSON);
        BulkScanCoverSheet coverSheet = objectMapper.readValue(optional.get().getFile(), BulkScanCoverSheet.class);
        Set<ConstraintViolation<BulkScanCoverSheet>> violations = validator.validate(coverSheet);
        assertEquals(6, violations.size());
    }

    @Test
    void shouldProduceCorrectFormatForCoverSheetCaseReferenceNumber() throws IOException {
        BulkScanCoverSheet coverSheet = new BulkScanCoverSheet();
        coverSheet.setCaseReference("#1542-9021-4510-0350");
        assertEquals(VALID_COVER_SHEET_CASE_REFERENCE_VALUE, coverSheet.getCaseReference());
        coverSheet.setCaseReference("`#1542902145100350");
        assertEquals(VALID_COVER_SHEET_CASE_REFERENCE_VALUE, coverSheet.getCaseReference());
        coverSheet.setCaseReference("1542-9021-4510-0350");
        assertEquals(VALID_COVER_SHEET_CASE_REFERENCE_VALUE, coverSheet.getCaseReference());
        coverSheet.setCaseReference("CaseReferenceNumber:#1542-9021-4510-0350");
        assertEquals(VALID_COVER_SHEET_CASE_REFERENCE_VALUE, coverSheet.getCaseReference());
    }

    @Test
    void shouldReturnCoversheetWithEmptyChecklistItems() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_BULK_SCAN_COVER_SHEET_NO_CHECKLIST_ITEMS);
        BulkScanCoverSheet coverSheet = objectMapper.readValue(optional.get().getFile(), BulkScanCoverSheet.class);
        assertEquals("[]", coverSheet.getCheckListItems().toString());
        assertEquals(true, coverSheet.getNoDocumentsRequired());
        assertEquals(VALID_COVERSHEET_NO_DOCS_REQUIRED_TEXT, coverSheet.getNoDocumentsRequiredText());
    }

    private Optional<FileSystemResource> getFile(String fileName) {
        return fileSystemResourceService.getFileSystemResource(fileName);
    }
}
