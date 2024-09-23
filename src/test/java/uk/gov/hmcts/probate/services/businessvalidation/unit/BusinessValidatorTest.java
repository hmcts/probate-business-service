package uk.gov.hmcts.probate.services.businessvalidation.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationError;
import uk.gov.hmcts.probate.services.businessvalidation.model.FormData;
import uk.gov.hmcts.probate.services.businessvalidation.validators.BusinessValidator;
import uk.gov.hmcts.probate.services.businessvalidation.validators.DobBeforeDodRule;
import uk.gov.hmcts.probate.services.businessvalidation.validators.ValidationRule;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessValidatorTest {

    private ValidationRule failingRule;
    private ValidationRule passingRule;
    private BusinessValidationError validationError;

    @BeforeEach
    public void setUp() throws Exception {
        validationError =
            new BusinessValidationError().generateError(ValidationRule.BUSINESS_ERROR, DobBeforeDodRule.CODE);
        failingRule = form -> Optional.of(validationError);
        passingRule = form -> Optional.empty();

    }

    @Test
    void testWithFailingRule() throws Exception {
        BusinessValidator businessValidator = new BusinessValidator(singletonList(failingRule));

        List<BusinessValidationError> validationErrors = businessValidator.validateForm(new FormData());

        assertThat(validationErrors).contains(validationError);

    }

    @Test
    void testWithPassingRule() throws Exception {
        BusinessValidator businessValidator = new BusinessValidator(singletonList(passingRule));

        List<BusinessValidationError> validationErrors = businessValidator.validateForm(new FormData());

        assertEquals(true, validationErrors.isEmpty());
    }

    @Test
    void testWithTwoValidationRules() throws Exception {
        BusinessValidator businessValidator = new BusinessValidator(asList(passingRule, failingRule));

        List<BusinessValidationError> validationErrors = businessValidator.validateForm(new FormData());

        assertThat(validationErrors).contains(validationError);
    }
}
