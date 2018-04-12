package uk.gov.hmcts.probate.services.invitation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.validation.constraints.Size;
import java.io.Serializable;

@JsonRootName(value = "invitation")
public class Invitation implements Serializable {

    @Size(min = 2, message = "fieldMinSize")
    private String firstName;

    @Size(min = 2, message = "fieldMinSize")
    private String lastName;

    @Size(min = 5, message = "fieldMinSize")
    private String email;

    @JsonProperty("formdataId")
    private String formdataId;

    @JsonProperty("executorName")
    private String executorName;

    @JsonProperty("leadExecutorName")
    private String leadExecutorName;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFormdataId() {
        return formdataId;
    }

    public String getEmail() {
        return email;
    }

    public String getExecutorName() {
        return executorName;
    }

    public String getLeadExecutorName() {
        return leadExecutorName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
