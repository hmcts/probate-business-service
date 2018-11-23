package uk.gov.hmcts.probate.services.businessvalidation.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationError;
import uk.gov.hmcts.probate.services.businessvalidation.model.Deceased;
import uk.gov.hmcts.probate.services.businessvalidation.model.FormData;

import java.util.Date;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DobBeforeDodRuleTest {

    @Mock
    private BusinessValidationError businessValidationError;
    private DobBeforeDodRule dobBeforeDodRule;
    private FormData formData;

    @Before
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

        assertThat(validationErrors.isPresent(), is(false));
    }

    @Test
    public void testValidateWithFailure() throws Exception {
        formData.getDeceased().setDateOfBirth(new Date(101));
        formData.getDeceased().setDateOfDeath(new Date(100));
        when(businessValidationError.generateError(DobBeforeDodRule.BUSINESS_ERROR, DobBeforeDodRule.CODE)).thenReturn(businessValidationError);

        Optional<BusinessValidationError> validationError = dobBeforeDodRule.validate(formData);

        validationError.ifPresent(error -> assertThat(error, is(businessValidationError)));
    }

    @Test
    public void testValidateWithEqualDates() throws Exception {
        formData.getDeceased().setDateOfBirth(new Date(100));
        formData.getDeceased().setDateOfDeath(new Date(100));

        Optional<BusinessValidationError> validationError = dobBeforeDodRule.validate(formData);

        assertThat(validationError.isPresent(), is(false));
    }
}