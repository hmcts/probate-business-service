package uk.gov.hmcts.probate.services.businessvalidation.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import jakarta.validation.Valid;
import java.io.Serializable;

@JsonRootName(value = "formdata")
public class FormData implements Serializable {

    @Valid
    private Applicant applicant;

    @Valid
    private Deceased deceased;

    private InheritanceTax iht;

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Deceased getDeceased() {
        return deceased;
    }

    public void setDeceased(Deceased deceased) {
        this.deceased = deceased;
    }

    public InheritanceTax getIht() {
        return iht;
    }

    public void setIht(InheritanceTax iht) {
        this.iht = iht;
    }
}
