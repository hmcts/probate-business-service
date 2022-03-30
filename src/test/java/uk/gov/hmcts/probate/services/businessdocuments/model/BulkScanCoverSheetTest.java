package uk.gov.hmcts.probate.services.businessdocuments.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.services.businessdocuments.services.FileSystemResourceService;
import uk.gov.hmcts.reform.probate.model.documents.BulkScanCoverSheet;
import uk.gov.hmcts.reform.probate.model.documents.CheckListItemType;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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

    private ObjectMapper objectMapper;

    private FileSystemResourceService fileSystemResourceService;
    private Validator validator;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        fileSystemResourceService = new FileSystemResourceService();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldCreateACoverSheetInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_BULK_SCAN_COVER_SHEET_JSON);
        BulkScanCoverSheet coverSheet = objectMapper.readValue(optional.get().getFile(), BulkScanCoverSheet.class);
        assertThat(coverSheet, is(notNullValue()));
        Set<ConstraintViolation<BulkScanCoverSheet>> violations = validator.validate(coverSheet);
        assertThat(violations, is(empty()));
        assertThat(coverSheet.getTitle(), is(equalTo(BulkScanCoverSheet.DEFAULT_TITLE)));
        assertThat(coverSheet.getApplicantAddressIntro(),
            is(equalTo(BulkScanCoverSheet.DEFAULT_APPLICANT_ADDRESS_INTRO)));
        assertThat(coverSheet.getApplicantNameIntro(), is(equalTo(BulkScanCoverSheet.DEFAULT_APPLICANT_NAME_INTRO)));
        assertThat(coverSheet.getCaseReferenceIntro(), is(equalTo(BulkScanCoverSheet.DEFAULT_CASE_REFERENCE_INTRO)));
        assertThat(coverSheet.getSubmitAddressIntro(), is(equalTo(BulkScanCoverSheet.DEFAULT_SUBMIT_ADDRESS_INTRO)));
        assertThat(coverSheet.getApplicantAddress(), is(equalTo(VALID_COVER_SHEET_APPLICANT_ADDRESS_VALUE)));
        assertThat(coverSheet.getApplicantName(), is(equalTo(VALID_COVER_SHEET_APPLICANT_NAME_VALUE)));
        assertThat(coverSheet.getCaseReference(), is(equalTo(VALID_COVER_SHEET_CASE_REFERENCE_VALUE)));
        assertThat(coverSheet.getSubmitAddress(), is(equalTo(VALID_COVER_SHEET_SUBMIT_ADDRESS_VALUE)));
        assertEquals(coverSheet.getCheckListItems().get(0).getText(), VALID_COVER_SHEET_CHECKLIST_TEXT);
        assertEquals(coverSheet.getCheckListItems().get(0).getType(), CheckListItemType.TEXT_ONLY);
        assertEquals(coverSheet.getCheckListItems().get(1).getText(), VALID_COVER_SHEET_CHECKLIST_TEXT);
        assertEquals(coverSheet.getCheckListItems().get(1).getType(), CheckListItemType.TEXT_WITH_LINK);
        assertEquals(coverSheet.getCheckListItems().get(1).getUrl(), VALID_COVER_SHEET_CHECKLIST_URL);
        assertEquals(coverSheet.getCheckListItems().get(1).getBeforeLinkText(),
            VALID_COVER_SHEET_CHECKLIST_BEFORE_LINK_TEXT);
        assertEquals(coverSheet.getCheckListItems().get(1).getAfterLinkText(),
            VALID_COVER_SHEET_CHECKLIST_AFTER_LINK_TEXT);
    }

    @Test
    public void shouldCreateACoverSheetInstanceWithStaticTextOverride() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_BULK_SCAN_COVER_SHEET_STATIC_TEXT_OVERRIDE_JSON);
        BulkScanCoverSheet coverSheet = objectMapper.readValue(optional.get().getFile(), BulkScanCoverSheet.class);
        assertThat(coverSheet, is(notNullValue()));
        Set<ConstraintViolation<BulkScanCoverSheet>> violations = validator.validate(coverSheet);
        assertThat(violations, is(empty()));
        assertThat(coverSheet.getTitle(), is(equalTo("Cover Sheet")));
        assertThat(coverSheet.getApplicantAddressIntro(), is(equalTo("The applicants address")));
        assertThat(coverSheet.getApplicantNameIntro(), is(equalTo("The applicants name")));
        assertThat(coverSheet.getCaseReferenceIntro(), is(equalTo("This is the Case Reference Number")));
        assertThat(coverSheet.getSubmitAddressIntro(), is(equalTo("This is the place to send the documents")));
        assertThat(coverSheet.getApplicantAddress(), is(equalTo(VALID_COVER_SHEET_APPLICANT_ADDRESS_VALUE)));
        assertThat(coverSheet.getApplicantName(), is(equalTo(VALID_COVER_SHEET_APPLICANT_NAME_VALUE)));
        assertThat(coverSheet.getCaseReference(), is(equalTo(VALID_COVER_SHEET_CASE_REFERENCE_VALUE)));
        assertThat(coverSheet.getSubmitAddress(), is(equalTo(VALID_COVER_SHEET_SUBMIT_ADDRESS_VALUE)));
    }

    @Test
    public void shouldFailToCreateACoverSheetInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(INVALID_BULK_SCAN_COVER_SHEET_JSON);
        BulkScanCoverSheet coverSheet = objectMapper.readValue(optional.get().getFile(), BulkScanCoverSheet.class);
        Set<ConstraintViolation<BulkScanCoverSheet>> violations = validator.validate(coverSheet);
        assertThat(violations, is(not(empty())));
        assertThat(violations.size(), is(equalTo(5)));
    }

    @Test
    public void shouldProduceCorrectFormatForCoverSheetCaseReferenceNumber() throws IOException {
        BulkScanCoverSheet coverSheet = new BulkScanCoverSheet();
        coverSheet.setCaseReference("#1542-9021-4510-0350");
        assertThat(coverSheet.getCaseReference(), is(equalTo(VALID_COVER_SHEET_CASE_REFERENCE_VALUE)));
        coverSheet.setCaseReference("`#1542902145100350");
        assertThat(coverSheet.getCaseReference(), is(equalTo(VALID_COVER_SHEET_CASE_REFERENCE_VALUE)));
        coverSheet.setCaseReference("1542-9021-4510-0350");
        assertThat(coverSheet.getCaseReference(), is(equalTo(VALID_COVER_SHEET_CASE_REFERENCE_VALUE)));
        coverSheet.setCaseReference("CaseReferenceNumber:#1542-9021-4510-0350");
        assertThat(coverSheet.getCaseReference(), is(equalTo(VALID_COVER_SHEET_CASE_REFERENCE_VALUE)));
    }

    @Test
    public void shouldReturnCoversheetWithEmptyChecklistItems() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_BULK_SCAN_COVER_SHEET_NO_CHECKLIST_ITEMS);
        BulkScanCoverSheet coverSheet = objectMapper.readValue(optional.get().getFile(), BulkScanCoverSheet.class);
        assertEquals(coverSheet.getCheckListItems().toString(), "[]");
    }

    private Optional<FileSystemResource> getFile(String fileName) {
        return fileSystemResourceService.getFileSystemResource(fileName);
    }
}
