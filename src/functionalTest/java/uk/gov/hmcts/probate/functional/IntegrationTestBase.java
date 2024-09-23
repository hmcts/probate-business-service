package uk.gov.hmcts.probate.functional;

import net.serenitybdd.junit.spring.integration.SpringIntegrationMethodRule;
import net.serenitybdd.junit5.SerenityJUnit5Extension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(SerenityJUnit5Extension.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
public abstract class IntegrationTestBase {

    public SpringIntegrationMethodRule springIntegration;
    @Autowired
    protected TestUtils utils;
    String businessServiceUrl;
    String persistenceServiceUrl;
    String pdfServiceUrl;
    String mobileNumber;

    IntegrationTestBase() {
        this.springIntegration = new SpringIntegrationMethodRule();
    }

    @Autowired
    public void businessServiceConfiguration(@Value("${probate.business.url}") String businessServiceUrl,
                                             @Value("${probate.persistence.url}") String persistenceServiceUrl,
                                             @Value("${probate.pdfservice.url}") String pdfServiceUrl,
                                             @Value("${probate.notify.mobile}") String mobileNumber) {
        this.businessServiceUrl = businessServiceUrl;
        this.persistenceServiceUrl = persistenceServiceUrl;
        this.pdfServiceUrl = pdfServiceUrl;
        this.mobileNumber = mobileNumber;
    }
}
