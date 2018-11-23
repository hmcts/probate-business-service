package uk.gov.hmcts.probate.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BusinessHealthConfiguration {

    @Value("${services.persistence.baseUrl}")
    private String servicePersistenceBaseUrl;

    @Bean
    public BusinessHealthIndicator persistenceServiceHealthIndicator(@Autowired RestTemplate restTemplate) {
        return new BusinessHealthIndicator(servicePersistenceBaseUrl, restTemplate);
    }
}
