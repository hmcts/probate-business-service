package uk.gov.hmcts.probate.services.business.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BusinessHealthConfiguration {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${services.pdf.service.url}")
    private String pdfServiceBaseUrl;

    @Value("${services.auth.provider.baseUrl}")
    private String authServiceBaseUrl;

    @Value("${idam.s2s-auth.url}")
    private String idamServiceHost;

    @Value("${document_management.url}")
    private String documentManagementHost;

    @Bean
    public BusinessHealthIndicator pdfServiceHealthIndicator() {
        return new BusinessHealthIndicator(pdfServiceBaseUrl, restTemplate);
    }

    @Bean
    public BusinessHealthIndicator authServiceHealthIndicator() {
        return new BusinessHealthIndicator(authServiceBaseUrl, restTemplate);
    }

    @Bean
    public BusinessHealthIndicator documentManagementHealthIndicator() {
        return new BusinessHealthIndicator(documentManagementHost, restTemplate);
    }

    @Bean
    public BusinessHealthIndicator idamServiceHealthIndicator() {
        return new BusinessHealthIndicator(idamServiceHost, restTemplate);
    }
}
