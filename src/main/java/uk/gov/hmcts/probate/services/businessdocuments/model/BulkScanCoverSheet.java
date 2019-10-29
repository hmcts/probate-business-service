
package uk.gov.hmcts.probate.services.businessdocuments.model;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import uk.gov.hmcts.reform.probate.model.documents.BusinessDocument;

@JsonRootName(value = "bulkScanCoverSheet")
public class BulkScanCoverSheet implements BusinessDocument {

	public static final String DEFAULT_TITLE = "Download Cover Sheet";
	public static final String DEFAULT_APPLICANT_NAME_INTRO = "Your name";
	public static final String DEFAULT_APPLICANT_ADDRESS_INTRO = "Your address";
	public static final String DEFAULT_CASE_REFERENCE_INTRO = "Your unique reference\nnumber is";
	public static final String DEFAULT_SUBMIT_ADDRESS_INTRO =
			"Please send this cover sheet along with your document(s) to the address shown below";

	private static final char CASE_REFERENCE_SEPARATOR_CHAR = '-';
	private static final int CASE_REFERENCE_SEPARATOR_INTERVAL_NUMBER = 4;

	@NotBlank
    @JsonProperty("title")
    private String title = DEFAULT_TITLE;

	@NotBlank
    @JsonProperty("applicantAddressIntro")
    private String applicantAddressIntro = DEFAULT_APPLICANT_ADDRESS_INTRO;

	@NotBlank
	@JsonProperty("applicantNameIntro")
	private String applicantNameIntro = DEFAULT_APPLICANT_NAME_INTRO;

	@NotBlank
	@JsonProperty("applicantName")
	private String applicantName;

	@NotBlank
    @JsonProperty("applicantAddress")
    private String applicantAddress;

	@NotBlank
    @JsonProperty("caseReferenceIntro")
    private String caseReferenceIntro = DEFAULT_CASE_REFERENCE_INTRO;

	@NotBlank
    @JsonProperty("caseReference")
    private String caseReference;

	@NotBlank
    @JsonProperty("submitAddressIntro")
    private String submitAddressIntro = DEFAULT_SUBMIT_ADDRESS_INTRO;

	@NotBlank
    @JsonProperty("submitAddress")
    private String submitAddress;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApplicantAddressIntro() {
		return applicantAddressIntro;
	}

	public String getApplicantNameIntro() {
		return applicantNameIntro;
	}

	public void setApplicantAddressIntro(String applicantAddressIntro) {
		this.applicantAddressIntro = applicantAddressIntro;
	}

	public void setApplicantNameIntro(String applicantNameIntro) {
		this.applicantNameIntro = applicantNameIntro;
	}

	public String getCaseReferenceIntro() {
		return caseReferenceIntro;
	}

	public void setCaseReferenceIntro(String caseReferenceIntro) {
		this.caseReferenceIntro = caseReferenceIntro;
	}

	public String getSubmitAddressIntro() {
		return submitAddressIntro;
	}

	public void setSubmitAddressIntro(String submitAddressIntro) {
		this.submitAddressIntro = submitAddressIntro;
	}

	public String getApplicantAddress() {
		return applicantAddress;
	}

	public void setApplicantAddress(String applicantAddress) {
		this.applicantAddress = applicantAddress;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public String getCaseReference() {
		return caseReference;
	}

	public void setCaseReference(String caseReference) {
		this.caseReference = addCaseReferenceHyphens(caseReference);
	}

	public String getSubmitAddress() {
		return submitAddress;
	}

	public void setSubmitAddress(String submitAddress) {
		this.submitAddress = submitAddress;
	}

	private String addCaseReferenceHyphens(String number) {
		number = number.replaceAll("[^\\d.]", "");
		StringBuilder sb = new StringBuilder(number);
		for(int i = 0; i+1 < number.length() / CASE_REFERENCE_SEPARATOR_INTERVAL_NUMBER; i++) {
		    sb.insert(((i + 1) * CASE_REFERENCE_SEPARATOR_INTERVAL_NUMBER) + i, CASE_REFERENCE_SEPARATOR_CHAR);
		}

		return sb.toString();
	}
}