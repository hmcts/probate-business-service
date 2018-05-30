package uk.gov.hmcts.probate;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = SmokeTestConfiguration.class)
public class SmokeTests {

    @Value("${probate.business.url}")
    private String url;

    private RestAssuredConfig config;

    @Before
    public void setUp() {
        RestAssured.useRelaxedHTTPSValidation();
        config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 60000)
                        .setParam("http.socket.timeout", 60000)
                        .setParam("http.connection-manager.timeout", 60000));
    }

    @Test
    public void shouldGetOkStatusFromHealthEndpointForProbusinessService() {
        given().config(config)
                .when()
                .get(url + "/health")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Ignore
    @Test
    public void shouldGetOkStatusFromInfoEndpointForProbusinessService() {
        given().config(config)
                .when()
                .get(url + "/info")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("git.commit.id", notNullValue())
                .body("git.commit.time", notNullValue());
    }    
}
