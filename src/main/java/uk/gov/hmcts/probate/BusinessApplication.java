package uk.gov.hmcts.probate;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.businessvalidation.validators.ValidationRule;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.idgeneration.strategy.PinStrategy;
import uk.gov.hmcts.probate.services.idgeneration.strategy.ProbateStrategy;
import uk.gov.hmcts.reform.document.DocumentManagementClientAutoConfiguration;
import uk.gov.service.notify.NotificationClient;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@Configuration
@EnableFeignClients(basePackages = {"uk.gov.hmcts.reform.document", "uk.gov.hmcts.reform.authorisation"})
@EnableAutoConfiguration(exclude = {DocumentManagementClientAutoConfiguration.class})
@PropertySource(value = "git.properties", ignoreResourceNotFound = true)
@OpenAPIDefinition(
    info = @Info(
        title = "Probate Business service",
        version = "1.0",
        description = "Provides data validation and other services",
        license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")
    )
)
public class BusinessApplication {

    @Value("${services.notify.apiKey}")
    String notificationApiKey;

    public static void main(String[] args) {
        SpringApplication.run(BusinessApplication.class, args);
    }


    @Bean
    List<ValidationRule> validationRules(@Autowired ValidationRule dobBeforeDodRule, @Autowired ValidationRule netIHTLessThanGrossRule) {
        List<ValidationRule> validationRules = new ArrayList<>();
        validationRules.add(dobBeforeDodRule);
        validationRules.add(netIHTLessThanGrossRule);
        return validationRules;
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }


    @Bean
    IdGeneratorService identityGeneratorService() {
        return new IdGeneratorService(new ProbateStrategy());
    }

    @Bean
    IdGeneratorService pinGeneratorService() {
        return new IdGeneratorService(new PinStrategy());
    }

    @Bean
    NotificationClient notificationClient() {
        return new NotificationClient(notificationApiKey);
    }
}
