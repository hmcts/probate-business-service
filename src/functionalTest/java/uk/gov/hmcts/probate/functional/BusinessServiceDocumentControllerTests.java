package uk.gov.hmcts.probate.functional;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class BusinessServiceDocumentControllerTests extends IntegrationTestBase {

    public static final String AUTH_TOKEN = "authToken";

    public static final String DUMMY_OAUTH_2_TOKEN = "oauth2Token";

    public static final String USER_ID = "tom@email.com";

    @MockBean
    private AuthTokenGenerator authTokenGenerator;

    @Before
    public void setUp() throws Exception {
        given(authTokenGenerator.generate()).willReturn(AUTH_TOKEN);
    }

    @Test
    public void testValidDocument() {}

    @Test
    public void testInvalidDocumentType() {}

    @Test
    public void testInvalidDocumentSize() {}

    @Test
    public void testInvalidToken() {}

    @Test
    public void testInvalidCredentials() {}

    @Test
    public void testInvalidUrlRoute() {
        SerenityRest.given().relaxedHTTPSValidation()
                .headers(utils.getDocumentManagementHeaders(DUMMY_OAUTH_2_TOKEN, USER_ID))
                .when().post(businessServiceUrl + "/documents/invalid_path")
                .then().assertThat().statusCode(404);
    }
}
