package uk.gov.hmcts.probate.services.businessvalidation.controllers.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationError;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationResponse;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationStatus;
import uk.gov.hmcts.probate.services.businessvalidation.model.FormData;
import uk.gov.hmcts.probate.services.businessvalidation.validators.BusinessValidator;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
public class BusinessValidationControllerV2 {

    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessValidationControllerV2.class);
    private BusinessValidator businessValidator;

    @Autowired
    public BusinessValidationControllerV2(BusinessValidator businessValidator) {
        this.businessValidator = businessValidator;
    }


    @RequestMapping(path = "/probateTypes/intestacy/validations", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public BusinessValidationResponse validateIntestacy(@Valid @RequestBody FormData formData,
                                               BindingResult bindingResult,
                                               @RequestHeader(AUTHORIZATION) String authorizationId,
                                               @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuthtoken) {

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<BusinessValidationError> businessErrors = businessValidator.validateForm(formData);

        boolean thereAreErrors = !fieldErrors.isEmpty() || !businessErrors.isEmpty();
        if (thereAreErrors) {
            return new BusinessValidationResponse(BusinessValidationStatus.FAILURE, fieldErrors, businessErrors);
        }

        return new BusinessValidationResponse(BusinessValidationStatus.SUCCESS, fieldErrors, Collections.emptyList());
    }
}
