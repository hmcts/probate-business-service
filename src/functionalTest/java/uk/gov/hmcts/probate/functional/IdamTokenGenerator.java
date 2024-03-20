package uk.gov.hmcts.probate.functional;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class IdamTokenGenerator {

    @Value("${idam.oauth2.redirect_uri}")
    private String redirectUri;

    @Value("${idam.username}")
    private String idamUsername;

    @Value("${idam.userpassword}")
    private String idamPassword;

    @Value("${idam.secret}")
    private String secret;

    @Value("${user.auth.provider.oauth2.url}")
    private String idamUserBaseUrl;

    private String userToken;

    private String idamCreateUrl() {
        return idamUserBaseUrl + "/testing-support/accounts";
    }

    public String getUserId() {
        String userIdLocal = "" + RestAssured.given()
            .header("Authorization", userToken)
            .get(idamUserBaseUrl + "/details")
            .body()
            .path("id");

        return userIdLocal;
    }


    public String generateUserTokenWithNoRoles() {
        userToken = generateClientCode();
        return userToken;
    }

    private String generateClientToken() {
        String code = generateClientCode();
        String token = "";

        token = RestAssured.given().post(idamUserBaseUrl + "/oauth2/token?code=" + code
            + "&client_secret=" + secret
            + "&client_id=probate"
            + "&redirect_uri=" + redirectUri
            + "&grant_type=authorization_code")
            .body().path("access_token");
        return "Bearer " + token;
    }

    private String generateClientCode() {
        String code = "";

        final String encoded = Base64.getEncoder().encodeToString((idamUsername + ":" + idamPassword).getBytes());

        code = RestAssured.given().baseUri(idamUserBaseUrl)
            .header("Authorization", "Basic " + encoded)
            .post("/oauth2/authorize?response_type=code&client_id=probate&redirect_uri=" + redirectUri)
            .body().path("code");

        return code;

    }

    public String generateOpenIdToken() {
        JsonPath jp = RestAssured.given().relaxedHTTPSValidation().post(idamUserBaseUrl + "/o/token?"
                + "client_secret=" + secret
                + "&client_id==probate"
                + "&redirect_uri=" + redirectUri
                + "&username=" + idamUsername
                + "&password=" + idamPassword
                + "&grant_type=password&scope=openid")
            .body().jsonPath();
        String token = jp.get("access_token");

        return token;
    }
}
