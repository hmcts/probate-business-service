package uk.gov.hmcts.probate.services.businessvalidation.model;

import com.fasterxml.jackson.annotation.JsonRootName;

import jakarta.validation.constraints.Size;
import java.io.Serializable;

@JsonRootName(value = "applicant")
public class Applicant implements Serializable {

    @Size(min = 2, message = "fieldMinSize")
    private String firstName;

    @Size(min = 2, message = "fieldMinSize")
    private String lastName;

    @Size(min = 2, message = "fieldMinSize")
    private String address;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
