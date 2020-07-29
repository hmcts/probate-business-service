package uk.gov.hmcts.probate.functional;

import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.given;

@RunWith(SpringIntegrationSerenityRunner.class)
public class BusinessServicePinControllerTests extends IntegrationTestBase {

    private static final String SESSION_ID = "tom@email.com";
    private static final String INVALID_NUMBER = "not_a_number";

    @Test
    public void testInviteSuccess() {
    	validatePinSuccess(mobileNumber);
    }

    @Test
    public void testInviteFailure() {
        validatePinFailure(INVALID_NUMBER);
    }

    private void validatePinSuccess(String phoneNumber) {    	
        given().relaxedHTTPSValidation()
                .headers(utils.getHeaders(SESSION_ID))
                .when().get(businessServiceUrl + "/pin/?phoneNumber=" + phoneNumber)
                .then().assertThat().statusCode(200);
    }

    private void validatePinFailure(String phoneNumber) {
        Response response = given().relaxedHTTPSValidation()
                .headers(utils.getHeaders(SESSION_ID))
                .when().get(businessServiceUrl + "/pin/?phoneNumber=" + phoneNumber)
                .thenReturn();
        response.then().assertThat().statusCode(400);
    }
}
