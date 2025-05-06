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

    private String getPinBody(final String phoneNumber) {
        return String.format(
            """
            {
                "PhonePin": {
                    "phoneNumber": "%s"
                }
            }
            """,
            phoneNumber);
    }

    private void validatePinSuccess(String phoneNumber) {
        final String body = getPinBody(phoneNumber);

        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .body(body)
            .when()
            .post(businessServiceUrl + "/pin")
            .then().log().ifValidationFails()
            .assertThat().statusCode(200);
    }

    private void validatePinFailure(String phoneNumber) {
        final String body = getPinBody(phoneNumber);

        Response response = given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .body(body)
            .when().post(businessServiceUrl + "/pin")
            .thenReturn();
        response.then().log().ifValidationFails()
            .assertThat().statusCode(400);
    }

    @Test
    public void testValidatePinFailurePhoneNumberWithNoEnoughDigits() {
        final String body = getPinBody("34");

        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .body(body)
            .when().post(businessServiceUrl + "/pin")
            .then().log().ifValidationFails()
            .assertThat().statusCode(500);
    }

    @Test
    public void testInvitebilingual() {
        final String body = getPinBody(mobileNumber);

        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .body(body)
            .when().post(businessServiceUrl + "/pin/bilingual")
            .then().log().ifValidationFails()
            .assertThat().statusCode(200);
    }

    @Test
    public void testInvitebilingualWithInvalidPhoneNumber() {
        final String body = getPinBody(INVALID_NUMBER);

        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .body(body)
            .when().post(businessServiceUrl + "/pin/bilingual")
            .then().log().ifValidationFails()
            .assertThat().statusCode(400);
    }

    @Test
    public void testInviteBilingualPhoneNumberWithNoEnoughDigits() {
        final String body = getPinBody("34");
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .body(body)
            .when().post(businessServiceUrl + "/pin/bilingual")
            .then().log().ifValidationFails()
            .assertThat().statusCode(500);
    }

    @Test void testInviteGetWithParamSucceeds() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin?phoneNumber=" + mobileNumber)
            .then().log().ifValidationFails()
            .assertThat().statusCode(200);
    }

    @Test void testBilingualInviteGetWithParamSucceeds() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin/bilingual?phoneNumber=" + mobileNumber)
            .then().log().ifValidationFails()
            .assertThat().statusCode(200);
    }

    @Test
    public void testInviteGetWithUrlSucceeds() {
        given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .when().get(businessServiceUrl + "/pin/" + mobileNumber)
            .then().log().ifValidationFails()
            .assertThat().statusCode(200);
    }


}
