package uk.gov.hmcts.probate.services.businessvalidation.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationError;
import uk.gov.hmcts.probate.services.businessvalidation.model.FormData;
import uk.gov.hmcts.probate.services.businessvalidation.model.InheritanceTax;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NetIHTValueLessThanGrossRuleTest {

    @Mock
    private BusinessValidationError businessValidationError;
    private FormData formData;
    private NetIHTLessThanGrossRule netIHTLessThanGrossRule;

    @Before
    public void setUp() throws Exception {
        netIHTLessThanGrossRule = new NetIHTLessThanGrossRule(businessValidationError);
        formData = new FormData();
        InheritanceTax iht = new InheritanceTax();
        formData.setIht(iht);
    }

    @Test
    public void testValidateWithSuccess() throws Exception {
        formData.getIht().setGrossValue(123123);
        formData.getIht().setNetValue(1212);

        Optional<BusinessValidationError> validationError = netIHTLessThanGrossRule.validate(formData);

        assertThat(validationError.isPresent(), is(false));
    }

    @Test
    public void testValidateWithFailure() throws Exception {
        formData.getIht().setGrossValue(123);
        formData.getIht().setNetValue(12123);

        when(businessValidationError.generateError(NetIHTLessThanGrossRule.BUSINESS_ERROR, NetIHTLessThanGrossRule.CODE)).thenReturn(businessValidationError);

        Optional<BusinessValidationError> validationError = netIHTLessThanGrossRule.validate(formData);

        validationError.ifPresent(error -> assertThat(error, is(businessValidationError)));
    }

    @Test
    public void testValidateWithEqualDates() throws Exception {
        formData.getIht().setGrossValue(123);
        formData.getIht().setNetValue(123);

        Optional<BusinessValidationError> validationError = netIHTLessThanGrossRule.validate(formData);

        assertThat(validationError.isPresent(), is(false));
    }
}