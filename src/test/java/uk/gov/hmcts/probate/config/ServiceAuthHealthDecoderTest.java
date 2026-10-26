
package uk.gov.hmcts.probate.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.authorisation.healthcheck.InternalHealth;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceAuthHealthDecoderTest {

    private final ServiceAuthHealthDecoder decoder = new ServiceAuthHealthDecoder();

    @Test
    void shouldDecodeHealthResponseWithoutRootWrapper() throws IOException {
        Response response = createResponse(
            "{\"status\":\"UP\"}");

        Object result = decoder.decode(response, InternalHealth.class);

        InternalHealth health = assertInstanceOf(InternalHealth.class, result);
        assertEquals("UP", health.getStatus().getCode());
    }

    @Test
    void shouldDecodeDownHealthResponse() throws IOException {
        Response response = createResponse(
            "{\"status\":\"DOWN\"}");

        Object result = decoder.decode(response, InternalHealth.class);

        InternalHealth health = assertInstanceOf(InternalHealth.class, result);
        assertEquals("DOWN", health.getStatus().getCode());
    }

    @Test
    void shouldReturnNullWhenResponseBodyIsNull() throws IOException {
        Response response = Response.builder()
            .status(204)
            .reason("No Content")
            .request(createRequest())
            .build();

        Object result = decoder.decode(response, InternalHealth.class);

        assertNull(result);
    }

    @Test
    void shouldThrowExceptionWhenResponseBodyIsInvalid() {
        Response response = createResponse(
            "{\"status\":");

        assertThrows(IOException.class,
            () -> decoder.decode(response, InternalHealth.class));
    }

    @Test
    void shouldNotRequireRootUnwrapping() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.UNWRAP_ROOT_VALUE);

        assertFalse(objectMapper.isEnabled(
            DeserializationFeature.UNWRAP_ROOT_VALUE));

        Response response = createResponse(
            "{\"status\":\"UP\"}");

        InternalHealth health = assertInstanceOf(
            InternalHealth.class,
            decoder.decode(response, InternalHealth.class));

        assertEquals("UP", health.getStatus().getCode());
    }

    private Response createResponse(String body) {
        return Response.builder()
            .status(200)
            .reason("OK")
            .request(createRequest())
            .headers(Collections.singletonMap(
                "Content-Type",
                Collections.singletonList(
                    "application/vnd.spring-boot.actuator.v3+json")))
            .body(body, StandardCharsets.UTF_8)
            .build();
    }

    private Request createRequest() {
        return Request.create(
            Request.HttpMethod.GET,
            "/health",
            Collections.emptyMap(),
            null,
            StandardCharsets.UTF_8);
    }
}
