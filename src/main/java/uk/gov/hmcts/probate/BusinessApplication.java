package uk.gov.hmcts.probate;

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
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
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
@EnableSwagger2
@EnableFeignClients(basePackages = {"uk.gov.hmcts.reform.document", "uk.gov.hmcts.reform.authorisation"})
@EnableAutoConfiguration(exclude = {DocumentManagementClientAutoConfiguration.class})
@PropertySource(value = "git.properties", ignoreResourceNotFound = true)
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

    @Bean
    public Docket validationApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Validation Service")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("uk.gov.hmcts.probate.services.businessvalidation.controllers"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket idGenerationApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Invite Generation Service")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("uk.gov.hmcts.probate.services.invitation.controllers"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Probate Business service")
                .description("Provides data validation and other services")
                .license("MIT License")
                .version("1.0")
                .build();
    }
}
