package uk.gov.hmcts.probate.functional;


import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


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
        userToken = generateOpenIdToken();
        return userToken;
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
