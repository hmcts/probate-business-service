package uk.gov.hmcts.probate.functional;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.thucydides.core.annotations.Pending;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringIntegrationSerenityRunner.class)
public class BusinessServiceDocumentControllerTests extends IntegrationTestBase {

    private static final String VALID_FILE_NAME = "valid_file.png";
    private static final String INVALID_FILE_NAME = "invalid.txt";

    @Before
    public void setUp(){
        RestAssured.baseURI = businessServiceUrl;
        RestAssured.defaultParser = Parser.JSON;
    }

    @Test
    @Pending
    public void shouldUploadValidDocument() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/" + VALID_FILE_NAME));

        given()
                .relaxedHTTPSValidation()
                .headers(utils.getDocumentUploadHeaders())
                .multiPart("file", VALID_FILE_NAME, bytes, "image/png")
                .contentType("multipart/form-data")
        .when()
                .post("/document/upload")
        .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @Pending
    public void shouldNotUploadInvalidDocument() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/" + INVALID_FILE_NAME));

        given()
                .relaxedHTTPSValidation()
                .headers(utils.getDocumentUploadHeaders())
                .multiPart("file", INVALID_FILE_NAME, bytes, "text/plain")
                .contentType("multipart/form-data")
        .when()
                .post(businessServiceUrl + "/document/upload")
        .then()
                .body(containsString("Error: invalid file type"));
    }

    @Test
    @Pending
    public void shouldThrowErrorWhenNoFilesArePosted() {
        given()
                .relaxedHTTPSValidation()
                .headers(utils.getDocumentUploadHeaders())
                .multiPart("file", "myFile")
                .contentType("multipart/form-data")
        .when()
                .post(businessServiceUrl + "/document/upload")
        .then()
                .body(containsString("Error: no files passed"));
    }

    @Test
    public void shouldReturnServerErrorForInvalidToken() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/valid_file.png"));

        given()
                .relaxedHTTPSValidation()
                .headers(utils.getDocumentUploadHeadersWithInvalidToken())
                .multiPart("file", "myFile", bytes, "image/png")
                .contentType("multipart/form-data")
        .when()
                .post(businessServiceUrl + "/document/upload")
        .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @Pending
    public void shouldReturnServerErrorForInvalidCredentials() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/valid_file.png"));

        given()
                .relaxedHTTPSValidation()
                .headers(utils.getDocumentUploadHeadersWithInvalidUserId())
                .multiPart("file", "myFile", bytes, "image/png")
                .contentType("multipart/form-data")
        .when()
                .post(businessServiceUrl + "/document/upload")
        .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Test
    @Pending
    public void shouldReturn404ForInvalidUrlRoute() {
        given()
                .relaxedHTTPSValidation()
                .headers(utils.getDocumentUploadHeaders())
        .when()
                .post("/document/invalid_path")
        .then()
                .assertThat().statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @Pending
    public void shouldDeleteValidDocument() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/" + VALID_FILE_NAME));
        List<String> urls = (ArrayList) given()
                .relaxedHTTPSValidation()
                .headers(utils.getDocumentUploadHeaders())
                .multiPart("file", VALID_FILE_NAME, bytes, "image/png")
                .contentType("multipart/form-data")
                .when()
                .post("/document/upload")
                .then().extract().response().getBody().jsonPath().get("");

        String id = StringUtils.substringAfterLast(urls.get(0), "/");

        given()
                .relaxedHTTPSValidation()
                .headers(utils.getDocumentUploadHeaders())
        .when()
                .delete("/document/delete/" + id)
        .then()
                .assertThat().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @Pending
    public void shouldDeleteInvalidDocument() {
        given()
                .relaxedHTTPSValidation()
                .headers(utils.getDocumentUploadHeaders())
        .when()
                .delete("/document/delete/invalid_file_path")
        .then()
                .assertThat().statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
