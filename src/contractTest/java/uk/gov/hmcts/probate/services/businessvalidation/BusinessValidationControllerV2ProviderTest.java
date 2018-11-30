package uk.gov.hmcts.probate.services.businessvalidation;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import org.junit.Before;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationError;
import uk.gov.hmcts.probate.services.businessvalidation.model.FormData;
import uk.gov.hmcts.probate.services.businessvalidation.validators.BusinessValidator;

import java.util.Arrays;
import java.util.LinkedList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRestPactRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
		"server.port=8888"
})
@Provider("probate_businessservice_validationservice")
@PactBroker(host="${pact.broker.baseUrl}", port = "${pact.broker.port}")
public class BusinessValidationControllerV2ProviderTest {

	@MockBean
	private BusinessValidator businessValidator;

	@Before
	public void setUpTest(){
		System.getProperties().setProperty("pact.verifier.publishResults" , "true");
	}

	@TestTarget
	@SuppressWarnings(value = "VisibilityModifier")
	public final Target target = new HttpTarget("http", "localhost", 8888, "/");


	@State({"provider validates formdata object",
					"provider validates formdata with success"})
	public void toValidateFormDataSuccess() {

		when(businessValidator.validateForm(any(FormData.class))).thenReturn(new LinkedList<>());
	}

    @State({"provider validates formdata object",
            "provider validates formdata with errors"})
    public void toValidateFormDataErrors() {

        BusinessValidationError  bve = new BusinessValidationError().generateError("applicant.firstName","fieldMinSize");
        when(businessValidator.validateForm(any(FormData.class))).thenReturn(Arrays.asList(bve));
    }

}