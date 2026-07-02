package uk.gov.hmcts.probate.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.functional.model.ClientAuthorizationCodeResponse;
import uk.gov.hmcts.probate.functional.model.ClientAuthorizationResponse;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import javax.crypto.SecretKey;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;

@Component
@Slf4j
public class BusinessServiceServiceAuthTokenGenerator {

    @Value("${idam.oauth2.client.id}")
    private String clientId;

    @Value("${idam.oauth2.client.secret}")
    private String clientSecret;

    @Value("${idam.oauth2.redirect_uri}")
    private String redirectUri;

    @Value("${service.name}")
    private String serviceName;

    @Value("${service.auth.provider.base.url}")
    private String baseServiceAuthUrl;

    @Value("${user.auth.provider.oauth2.url}")
    private String baseServiceOauth2Url;

    @Autowired
    private ServiceAuthTokenGenerator tokenGenerator;

    private static final String JWT_KEY = "jwtKey";

    public String generateServiceToken() {
        return tokenGenerator.generate();
    }

    public String getUserId() {
        String clientToken = generateClientToken();

        String withoutSignature = clientToken.substring(0, clientToken.lastIndexOf('.') + 1);
        Claims claims = Jwts.parser()
            .verifyWith(getSecretSigningKey())
            .build()
            .parseSignedClaims(withoutSignature)
            .getPayload();

        return claims.get("id", String.class);
    }

    private SecretKey getSecretSigningKey() {
        final byte[] keyBytes = Decoders.BASE64URL.decode(JWT_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateClientToken() {
        String code = generateClientCode();
        String token = "";

        String jsonResponse = post(baseServiceOauth2Url + "/oauth2/token?code=" + code
            + "&client_secret=" + clientSecret
            + "&client_id=" + clientId
            + "&redirect_uri=" + redirectUri
            + "&grant_type=authorization_code")
            .body().asString();

        ObjectMapper mapper = new ObjectMapper();

        try {
            token = mapper.readValue(jsonResponse, ClientAuthorizationResponse.class).accessToken;
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return token;
    }

    private String generateClientCode() {
        String code = "";
        String jsonResponse = given()
            .relaxedHTTPSValidation()
            .header("Authorization", "Basic dGVzdEBURVNULkNPTToxMjM=")
            .post(baseServiceOauth2Url + "/oauth2/authorize?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri)
            .asString();

        ObjectMapper mapper = new ObjectMapper();

        try {
            code = mapper.readValue(jsonResponse, ClientAuthorizationCodeResponse.class).code;
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return code;
    }

    public void createNewUser() {
        given().headers("Content-type", "application/json")
            .relaxedHTTPSValidation()
            .body(
                "{ \"email\":\"test@TEST.COM\", \"forename\":\"test@TEST.COM\",\"surname\":\"test@TEST.COM\","
                    + "\"password\":\"123\",\"continue-url\":\"test\"}")
            .post(baseServiceOauth2Url + "/testing-support/accounts");
    }
}
