
package uk.gov.hmcts.probate.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.Decoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class ServiceAuthHealthDecoder implements Decoder {

    private final ObjectMapper objectMapper = new ObjectMapper()
        .disable(DeserializationFeature.UNWRAP_ROOT_VALUE);

    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.body() == null) {
            return null;
        }

        try (var inputStream = response.body().asInputStream()) {
            return objectMapper.readValue(
                inputStream,
                objectMapper.constructType(type));
        }
    }
}
