package uk.gov.hmcts.probate.functional;

import io.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
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
        SerenityRest.given().relaxedHTTPSValidation()
                .headers(utils.getHeaders(SESSION_ID))
                .when().get(businessServiceUrl + "/pin/" + phoneNumber)
                .then().assertThat().statusCode(200);
    }

    private void validatePinFailure(String phoneNumber) {
        Response response = SerenityRest.given().relaxedHTTPSValidation()
                .headers(utils.getHeaders(SESSION_ID))
                .when().get(businessServiceUrl + "/pin/" + phoneNumber)
                .thenReturn();

        response.then().assertThat().statusCode(500)
                .and().body("error", equalTo("Internal Server Error"))
                .and().body("message", containsString("Status code: 400"));
    }
}
