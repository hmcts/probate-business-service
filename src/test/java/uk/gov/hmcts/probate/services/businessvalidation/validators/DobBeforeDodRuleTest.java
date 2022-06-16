package uk.gov.hmcts.probate.services.businessvalidation.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationError;
import uk.gov.hmcts.probate.services.businessvalidation.model.Deceased;
import uk.gov.hmcts.probate.services.businessvalidation.model.FormData;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class DobBeforeDodRuleTest {

    @Mock
    private BusinessValidationError businessValidationError;
    private DobBeforeDodRule dobBeforeDodRule;
    private FormData formData;

    @BeforeEach
    public void setUp() throws Exception {
        formData = new FormData();
        Deceased deceased = new Deceased();
        formData.setDeceased(deceased);

        dobBeforeDodRule = new DobBeforeDodRule(businessValidationError);
    }

    @Test
    public void testValidateWithSuccess() throws Exception {
        formData.getDeceased().setDateOfBirth(new Date(100));
        formData.getDeceased().setDateOfDeath(new Date(101));

        Optional<BusinessValidationError> validationErrors = dobBeforeDodRule.validate(formData);

        assertEquals(false, validationErrors.isPresent());
    }

    @Test
    public void testValidateWithFailure() throws Exception {
        formData.getDeceased().setDateOfBirth(new Date(101));
        formData.getDeceased().setDateOfDeath(new Date(100));
        when(businessValidationError.generateError(DobBeforeDodRule.BUSINESS_ERROR, DobBeforeDodRule.CODE))
            .thenReturn(businessValidationError);

        Optional<BusinessValidationError> validationError = dobBeforeDodRule.validate(formData);

        validationError.ifPresent(error -> assertEquals(businessValidationError, error));
    }

    @Test
    public void testValidateWithEqualDates() throws Exception {
        formData.getDeceased().setDateOfBirth(new Date(100));
        formData.getDeceased().setDateOfDeath(new Date(100));

        Optional<BusinessValidationError> validationError = dobBeforeDodRule.validate(formData);

        assertEquals(false, validationError.isPresent());
    }
}
