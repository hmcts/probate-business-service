package uk.gov.hmcts.probate.functional;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class BusinessServiceInvitationControllerTests extends IntegrationTestBase {

    private static final String SESSION_ID = "tom@email.com";
    private static boolean isInitialized = false;

    @Before
    public void setUp() {
        if (isInitialized) return;
        populateFormDataTable();
        isInitialized = true;
    }

    @After
    public void tearDown() {

    }

    private void populateFormDataTable() {
        RestAssured.baseURI = persistenceServiceUrl;
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Session-Id", SESSION_ID);
        request.body(utils.getJsonFromFile("formDataMultiples.json"));
        Response response = request.post("/formdata");
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
    public void testInvitesAllAgreedSuccess() {
        validateInvitesAllAgreedSuccess(SESSION_ID);
    }

    @Test
    public void testInvitesAllAgreedFailure() {
        validateInvitesAllAgreedFailure();
    }

    private void validateInviteSuccess(String sessionId, String jsonFileName) {
        SerenityRest.given().relaxedHTTPSValidation()
                .headers(utils.getHeaders(sessionId))
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(businessServiceUrl + "/invite")
                .then().assertThat().statusCode(200);
    }

    private void validateInviteFailure(String sessionId, String jsonFileName) {
        Response response = SerenityRest.given().relaxedHTTPSValidation()
                .headers(utils.getHeaders(sessionId))
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(businessServiceUrl + "/invite")
                .thenReturn();

        response.then().assertThat().statusCode(500)
                .and().body("error", equalTo("Internal Server Error"))
                .and().body("message", equalTo("500 null"));
    }

    private void validateInvitesAllAgreedSuccess(String formdataId) {
        SerenityRest.given().relaxedHTTPSValidation()
                .when().get(businessServiceUrl + "/invites/allAgreed/" + formdataId)
                .then().assertThat().statusCode(200);
    }

    private void validateInvitesAllAgreedFailure() {
        Response response = SerenityRest.given().relaxedHTTPSValidation()
                .when().get(businessServiceUrl + "/invites/allAgreed/invalid_id")
                .thenReturn();

        response.then().assertThat().statusCode(500)
                .and().body("error", equalTo("Internal Server Error"))
                .and().body("message", equalTo("404 null"));
    }
}