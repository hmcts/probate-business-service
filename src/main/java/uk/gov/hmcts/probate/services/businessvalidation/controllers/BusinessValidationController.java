package uk.gov.hmcts.probate.services.businessvalidation.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationError;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationResponse;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationStatus;
import uk.gov.hmcts.probate.services.businessvalidation.model.FormData;
import uk.gov.hmcts.probate.services.businessvalidation.validators.BusinessValidator;

import jakarta.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@RestController
@Tag(name = "Validation Service")
public class BusinessValidationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessValidationController.class);
    private BusinessValidator businessValidator;

    @Autowired
    public BusinessValidationController(BusinessValidator businessValidator) {
        this.businessValidator = businessValidator;
    }

    @RequestMapping(path = "/validate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public BusinessValidationResponse validate(@Valid @RequestBody FormData formData,
                                               BindingResult bindingResult,
                                               @RequestHeader("Session-Id") String sessionId) {
        LOGGER.info("Processing session id " + sessionId + " : " + bindingResult.getFieldErrors());

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<BusinessValidationError> businessErrors = businessValidator.validateForm(formData);

        boolean thereAreErrors = !fieldErrors.isEmpty() || !businessErrors.isEmpty();
        if (thereAreErrors) {
            return new BusinessValidationResponse(BusinessValidationStatus.FAILURE, fieldErrors, businessErrors);
        }

        return new BusinessValidationResponse(BusinessValidationStatus.SUCCESS, fieldErrors, Collections.emptyList());
    }
}
