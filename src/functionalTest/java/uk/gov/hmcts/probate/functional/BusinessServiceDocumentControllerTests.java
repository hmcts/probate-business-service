package uk.gov.hmcts.probate.functional;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;

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
    public void testValidDocument() {
        List<File> files = new ArrayList<>();
        File validDocument = new File("valid file");
        files.add(validDocument);

        SerenityRest.given().relaxedHTTPSValidation()
                .headers(utils.getDocumentManagementHeaders(DUMMY_OAUTH_2_TOKEN, USER_ID))
                .body(files)
                .when().post(businessServiceUrl + "/documents/upload")
                .then().assertThat().statusCode(200);
    }

    @Test
    public void testInvalidDocumentType() {
        List<File> files = new ArrayList<>();
        File invalidDocument = new File("invalid for any reason");
        files.add(invalidDocument);

        SerenityRest.given().relaxedHTTPSValidation()
                .headers(utils.getDocumentManagementHeaders(DUMMY_OAUTH_2_TOKEN, USER_ID))
                .body(files)
                .when().post(businessServiceUrl + "/documents/upload")
                .then().assertThat().statusCode(404);
    }

    @Test
    public void testInvalidDocumentSize() {
        List<File> files = new ArrayList<>();
        File invalidDocument = new File("too large");
        files.add(invalidDocument);

        SerenityRest.given().relaxedHTTPSValidation()
                .headers(utils.getDocumentManagementHeaders(DUMMY_OAUTH_2_TOKEN, USER_ID))
                .body(files)
                .when().post(businessServiceUrl + "/documents/upload")
                .then().assertThat().statusCode(404);
    }

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
