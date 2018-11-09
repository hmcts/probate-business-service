//package uk.gov.hmcts.probate.functional;
//
//import io.restassured.response.Response;
//import net.serenitybdd.junit.runners.SerenityRunner;
//import net.serenitybdd.rest.SerenityRest;
//import org.apache.commons.io.IOUtils;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpStatus;
//import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
//import org.junit.matchers.JUnitMatchers.*;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.BDDMockito.given;
//
//@RunWith(SerenityRunner.class)
//public class BusinessServiceDocumentControllerTests extends IntegrationTestBase {
//
//    private static final String AUTH_TOKEN = "authToken";
//
//    private static final String DUMMY_OAUTH_2_TOKEN = "oauth2Token";
//
//    private static final String USER_ID = "tom@email.com";
//
//    @MockBean
//    private AuthTokenGenerator authTokenGenerator;
//
//    @Before
//    public void setUp() throws Exception {
//        given(authTokenGenerator.generate()).willReturn(AUTH_TOKEN);
//    }
//
//    @After
//    public void tearDown() {
//
//    }
//
////    @Test
////    public void testValidDocument() throws IOException {
////        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/valid_file.png"));
////
////        Response response = SerenityRest.given()
////                .headers(utils.getDocumentUploadHeaders("jbhvhvjhvjh", AUTH_TOKEN, "tom@email.com"))
////                .multiPart("file", "myFile", bytes, "image/png")
////                .contentType("multipart/form-data")
////                .post(businessServiceUrl + "/document/upload")
////                .andReturn();
////
////        Assert.assertEquals(HttpStatus.OK.value(), response.statusCode());
////    }
//
//    @Test
//    public void testInvalidDocumentType() throws IOException {
//        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/invalid.txt"));
//
//        Response response = SerenityRest.given()
//                .headers(utils.getDocumentUploadHeaders("jbhvhvjhvjh", AUTH_TOKEN, "tom@email.com"))
//                .multiPart("file", "myFile", bytes, "text/plain")
//                .contentType("multipart/form-data")
//                .post(businessServiceUrl + "/document/upload")
//                .andReturn();
//        Assert.assertTrue(response.body().prettyPrint().contains("Error: invalid file type"));
//    }
//
//    @Test
//    public void testNoDocumentsUploaded() throws IOException {
//        Response response = SerenityRest.given()
//                .headers(utils.getDocumentUploadHeaders("jbhvhvjhvjh", AUTH_TOKEN, "tom@email.com"))
//                .multiPart("file", "myFile")
//                .contentType("multipart/form-data")
//                .post(businessServiceUrl + "/document/upload")
//                .andReturn();
//        Assert.assertTrue(response.body().prettyPrint().contains("Error: no files passed"));
//    }
//
////
////    @Test
////    public void testInvalidDocumentSize() {
////        List<File> files = new ArrayList<>();
////        File invalidDocument = new File("functionalTest/resources/files/valid_file.png");
////        files.add(invalidDocument);
////
////        SerenityRest.given().relaxedHTTPSValidation()
////                .headers(utils.getDocumentManagementHeaders(DUMMY_OAUTH_2_TOKEN, USER_ID))
////                .body(files)
////                .when().post(businessServiceUrl + "/document/upload")
////                .then().assertThat().statusCode(404);
////    }
//
//
//
////    @Test
////    public void testInvalidToken() {
////        List<File> files = new ArrayList<>();
////        File invalidDocument = new File("functionalTest/resources/files/large_invalid_file.pdf");
////        files.add(invalidDocument);
////
////        SerenityRest.given().relaxedHTTPSValidation()
////                .headers(utils.getDocumentManagementHeaders("invalid_token", USER_ID))
////                .body(files)
////                .when().post(businessServiceUrl + "/document/upload")
////                .then().assertThat().statusCode(401);
////    }
////
////    @Test
////    public void testInvalidCredentials() {}
//
//    @Test
//    public void testInvalidUrlRoute() {
//        SerenityRest.given().relaxedHTTPSValidation()
//                .headers(utils.getDocumentUploadHeaders("jbhvhvjhvjh", AUTH_TOKEN, "tom@email.com"))
//                .when().post(businessServiceUrl + "/document/invalid_path")
//                .then().assertThat().statusCode(404);
//    }
//
////    @Test
////    public void testDeleteValidDocument() {
////        SerenityRest.given().relaxedHTTPSValidation()
////                .headers(utils.getDocumentManagementHeaders(DUMMY_OAUTH_2_TOKEN, USER_ID))
////                .when().delete(businessServiceUrl + "/document/delete/file_path")
////                .then().assertThat().statusCode(204);
////    }
//
//    @Test
//    public void testDeleteInvalidDocument() {
//        SerenityRest.given().relaxedHTTPSValidation()
//                .headers(utils.getDocumentDeleteHeaders("jbhvhvjhvjh", "tom@email.com"))
//                .when().delete(businessServiceUrl + "/document/delete/invalid_file_path")
//                .then().assertThat().statusCode(500);
//    }
//}
