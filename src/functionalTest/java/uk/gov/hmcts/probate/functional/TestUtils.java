package uk.gov.hmcts.probate.functional;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@ContextConfiguration(classes = TestContextConfiguration.class)
@Component
@Slf4j
public class TestUtils {

    public static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    public static final String CONTENT_TYPE = "Content-Type";
    @Autowired
    protected BusinessServiceServiceAuthTokenGenerator serviceAuthTokenGenerator;

    @Value("${user.id.url}")
    private String userId;

    private String serviceToken;

    @PostConstruct
    public void init() {
        serviceToken = serviceAuthTokenGenerator.generateServiceToken();

        if (userId == null || userId.isEmpty()) {
            serviceAuthTokenGenerator.createNewUser();
            userId = serviceAuthTokenGenerator.getUserId();
        }
    }

    public String getJsonFromFile(String fileName) {
        try {
            File file = ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }


    public Headers getHeaders(String sessionId) {
        return Headers.headers(
                new Header(CONTENT_TYPE, ContentType.JSON.toString()),
                new Header("Session-ID", sessionId));
    }


    public Headers getHeaders() {
        return getHeaders(serviceToken);
    }

    public Headers getHeadersWithServiceToken(String serviceToken) {
        return Headers.headers(
                new Header(SERVICE_AUTHORIZATION, serviceToken),
                new Header(CONTENT_TYPE, ContentType.JSON.toString()));
    }

    public Headers getHeadersWithServiceToken() {
        return Headers.headers(
                new Header(SERVICE_AUTHORIZATION, serviceToken),
                new Header(CONTENT_TYPE, ContentType.JSON.toString()));
    }

    public Headers getHeadersWithUserId() {
        return getHeadersWithUserId(serviceToken, userId);
    }

    private Headers getHeadersWithUserId(String serviceToken, String userId) {
        return Headers.headers(
                new Header(SERVICE_AUTHORIZATION, serviceToken),
                new Header(CONTENT_TYPE, ContentType.JSON.toString()),
                new Header("user-id", userId));
    }


}