package uk.gov.hmcts.probate.functional;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.gov.service.notify.SendSmsResponse;
import static org.hamcrest.Matchers.equalTo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SerenityJUnit5Extension.class)
public class BusinessServiceInvitationControllerTests extends IntegrationTestBase {



    private static final String SESSION_ID = "tom@email.com";
    private static boolean isInitialized = false;
    private SendSmsResponse smsResponse;

    @BeforeAll
    public void setUp() {
        if (isInitialized) {
            return;
        }
        populateFormDataTable();
        isInitialized = true;
    }

    @AfterEach
    public void tearDown() {

    }

    private void populateFormDataTable() {
        RestAssured.baseURI = persistenceServiceUrl;
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Session-Id", SESSION_ID);
        request.body(utils.getJsonFromFile("formDataMultiples.json"));
        //Response response = request.post("/formdata");
    }

    @Test
    public void testInviteSuccess() {
        validateInviteSuccess(SESSION_ID, "inviteDataValid.json");
    }

    @Test
    public void testInviteFailure() {
        validateInviteFailure("invalid_id", "inviteDataInvalid.json");
    }

    @Test
    public void testInviteResendSuccess() {
        validateInviteResendSuccess(SESSION_ID, "inviteDataValid.json");
    }

    @Test
    public void testInviteResendFailure() {
        validateInviteResendFailure("invalid_id", "emptyInviteData.json");
    }

    @Test
    @Disabled
    public void testInvitesAllAgreedSuccess() {
        validateInvitesAllAgreedSuccess(SESSION_ID);
    }

    @Test
    @Disabled
    public void testInvitesAllAgreedFailure() {
        validateInvitesAllAgreedFailure();
    }

    private void validateInviteSuccess(String sessionId, String jsonFileName) {
        RestAssured.given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(sessionId))
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(businessServiceUrl + "/invite")
            .then().assertThat().statusCode(200);
    }

    private void validateInviteFailure(String sessionId, String jsonFileName) {
        Response response = RestAssured.given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(sessionId))
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(businessServiceUrl + "/invite")
            .thenReturn();

        response.then().assertThat().statusCode(400)
            .and().body("error", equalTo("Bad Request"));
    }

    private void validateInviteResendSuccess(String sessionId, String jsonFileName) {
        RestAssured.given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(sessionId))
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(businessServiceUrl + "/invite/" + sessionId)
            .then().assertThat().statusCode(200);
    }

    private void validateInviteResendFailure(String sessionId, String jsonFileName) {
        Response response = RestAssured.given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(sessionId))
            .body(utils.getJsonFromFile(jsonFileName))
            .when().post(businessServiceUrl + "/invite/invalid_id")
            .thenReturn();

        response.then().assertThat().statusCode(500)
            .and().body("error", equalTo("Internal Server Error"))
            .and().extract().response().prettyPrint();
    }

    private void validateInvitesAllAgreedSuccess(String formdataId) {
        RestAssured.given().relaxedHTTPSValidation()
            .when().get(businessServiceUrl + "/invites/allAgreed/" + formdataId)
            .then().assertThat().statusCode(200);
    }

    private void validateInvitesAllAgreedFailure() {
        Response response = RestAssured.given().relaxedHTTPSValidation()
            .when().get(businessServiceUrl + "/invites/allAgreed/invalid_id")
            .thenReturn();

        response.then().assertThat().statusCode(500);
    }

    @Test
    public void testInviteBilingualSuccess() {
        RestAssured.given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .body(utils.getJsonFromFile("inviteDataValid.json"))
            .when().post(businessServiceUrl + "/invite/bilingual")
            .then().assertThat().statusCode(200);
    }

    @Test
    public void testInviteBilingualFailure() {
        Response response = RestAssured.given().relaxedHTTPSValidation()
            .headers(utils.getHeaders(SESSION_ID))
            .body(utils.getJsonFromFile("inviteDataInvalid.json"))
            .when().post(businessServiceUrl + "/invite/bilingual")
            .thenReturn();

        response.then().assertThat().statusCode(400)
            .and().body("error", equalTo("Bad Request"));
    }
}
