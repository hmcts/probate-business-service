package uk.gov.hmcts.reform.probate.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@JsonRootName(value = "PhonePin")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhonePin implements Serializable {
    @NotNull
    private final String phoneNumber;

    public PhonePin(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
