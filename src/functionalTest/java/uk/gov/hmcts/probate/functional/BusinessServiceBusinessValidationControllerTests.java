package uk.gov.hmcts.probate.functional;

import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import io.restassured.RestAssured;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringIntegrationSerenityRunner.class)
public class BusinessServiceBusinessValidationControllerTests extends IntegrationTestBase {

    private static final String SESSION_ID = "tom@email.com";
    private static final String JSON_FILE_NAME = "formData.json";
    private static final String INVALID_JSON_FILE_NAME = "formDataMultiples.json";

    @Test
    public void testValidateSuccess() {
        validateSuccess(SESSION_ID, JSON_FILE_NAME);
    }

    @Test
    public void testValidateFailure() {
        validateFailure(INVALID_JSON_FILE_NAME, 400, "JSON parse error");
    }

    private void validateSuccess(String sessionId, String jsonFileName) {
        RestAssured.given().relaxedHTTPSValidation()
                .headers(utils.getHeaders(sessionId))
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(businessServiceUrl + "/validate")
                .then().assertThat().statusCode(200);
    }

    private void validateFailure(String jsonFileName, int errorCode, String errorMsg) {
        Response response = RestAssured.given().relaxedHTTPSValidation()
                .headers(utils.getHeaders(SESSION_ID))
                .body(utils.getJsonFromFile(jsonFileName))
                .when().post(businessServiceUrl + "/validate")
                .thenReturn();

        response.then().assertThat().statusCode(errorCode)
                .and().body("error", equalTo("Bad Request"))
                .and().body("message", containsString(errorMsg));
    }

}
