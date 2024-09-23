package uk.gov.hmcts.probate.functional;

import io.restassured.response.Response;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.given;

@ExtendWith(SerenityJUnit5Extension.class)
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
            .when().get(businessServiceUrl + "/pin?phoneNumber=" + phoneNumber)
            .then().assertThat().statusCode(200);
    }

    private void validatePinFailure(String phoneNumber) {
        Response response = given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin?phoneNumber=" + phoneNumber)
            .thenReturn();
        response.then().assertThat().statusCode(400);
    }

    @Test
    public void testValidatePinFailurePhoneNumberWithNoEnoughDigits() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin?phoneNumber=" + 34)
            .then().assertThat().statusCode(500)
            .extract().response().prettyPrint();
    }

    @Test
    public void testInviteWithPhoneNumber() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin/" + mobileNumber)
            .then().assertThat().statusCode(200);
    }

    @Test
    public void testInviteWithInvaidPhoneNumber() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin/" + INVALID_NUMBER)
            .then().assertThat().statusCode(500)
            .extract().response().prettyPrint();
    }

    @Test
    public void testInvitebilingual() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin/bilingual?phoneNumber=" + mobileNumber)
            .then().assertThat().statusCode(200);
    }

    @Test
    public void testInvitebilingualWithInvalidPhoneNumber() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin/bilingual?phoneNumber=" + INVALID_NUMBER)
            .then().assertThat().statusCode(400);
    }

    @Test
    public void testInviteBilingualPhoneNumberWithNoEnoughDigits() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin/bilingual?phoneNumber=" + 34)
            .then().assertThat().statusCode(500)
            .extract().response().prettyPrint();
    }


}
