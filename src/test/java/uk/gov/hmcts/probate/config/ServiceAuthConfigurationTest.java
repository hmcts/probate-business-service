package uk.gov.hmcts.probate.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ServiceAuthConfigurationTest {

    @Autowired
    AuthTokenGenerator authTokenGenerator;

    @Test
    void shouldInstantiateServiceAuthTokenGenerator() {
        assertNotNull(authTokenGenerator);
    }
}
