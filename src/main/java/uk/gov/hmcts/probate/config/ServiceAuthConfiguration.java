package uk.gov.hmcts.probate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGeneratorFactory;

@Configuration
public class ServiceAuthConfiguration {
    @Bean
    public AuthTokenGenerator authTokenGenerator(
            @Value("${services.auth.provider.totp_secret}") String secret,
            @Value("${services.auth.provider.microservice}") String microService,
            final ServiceAuthorisationApi serviceAuthorisationApi) {
        return AuthTokenGeneratorFactory.createDefaultGenerator(secret, microService, serviceAuthorisationApi);
    }
}
