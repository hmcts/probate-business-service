package uk.gov.hmcts.probate.config;

import feign.Feign;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.authorisation.ServiceAuthorisationApi;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

@Configuration
public class ServiceAuthConfiguration {

    @Bean
    public ServiceAuthTokenGenerator serviceAuthTokenGenerator(
            @Value("${services.auth.provider.baseUrl}") String s2sUrl,
            @Value("${services.auth.provider.totp_secret}") String secret,
            @Value("${services.auth.provider.microservice}") String microservice) {

        final ServiceAuthorisationApi serviceAuthorisationApi = Feign.builder()
                .encoder(new JacksonEncoder())
                .contract(new SpringMvcContract())
                .target(ServiceAuthorisationApi.class, s2sUrl);
        return new ServiceAuthTokenGenerator(secret, microservice, serviceAuthorisationApi);
    }

}
