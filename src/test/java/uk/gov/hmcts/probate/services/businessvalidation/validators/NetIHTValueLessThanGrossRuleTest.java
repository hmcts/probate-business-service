package uk.gov.hmcts.probate.services.businessvalidation.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationError;
import uk.gov.hmcts.probate.services.businessvalidation.model.FormData;
import uk.gov.hmcts.probate.services.businessvalidation.model.InheritanceTax;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class NetIHTValueLessThanGrossRuleTest {

    @Mock
    private BusinessValidationError businessValidationError;
    private FormData formData;
    private NetIHTLessThanGrossRule netIHTLessThanGrossRule;

    @BeforeEach
    public void setUp() throws Exception {
        netIHTLessThanGrossRule = new NetIHTLessThanGrossRule(businessValidationError);
        formData = new FormData();
        InheritanceTax iht = new InheritanceTax();
        formData.setIht(iht);
    }

    @Test
    void testValidateWithSuccess() throws Exception {
        formData.getIht().setGrossValue(123123);
        formData.getIht().setNetValue(1212);

        Optional<BusinessValidationError> validationError = netIHTLessThanGrossRule.validate(formData);

        assertEquals(false, validationError.isPresent());
    }

    @Test
    void testValidateWithFailure() throws Exception {
        formData.getIht().setGrossValue(123);
        formData.getIht().setNetValue(12123);

        when(
            businessValidationError.generateError(NetIHTLessThanGrossRule.BUSINESS_ERROR, NetIHTLessThanGrossRule.CODE))
            .thenReturn(businessValidationError);

        Optional<BusinessValidationError> validationError = netIHTLessThanGrossRule.validate(formData);

        validationError.ifPresent(error -> assertEquals(businessValidationError, error));
    }

    @Test
    void testValidateWithEqualDates() throws Exception {
        formData.getIht().setGrossValue(123);
        formData.getIht().setNetValue(123);

        Optional<BusinessValidationError> validationError = netIHTLessThanGrossRule.validate(formData);

        assertEquals(false, validationError.isPresent());
    }
}
