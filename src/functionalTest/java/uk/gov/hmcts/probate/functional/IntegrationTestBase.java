package uk.gov.hmcts.probate.functional;

import net.thucydides.junit.spring.SpringIntegration;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
public abstract class IntegrationTestBase {

    @Autowired
    protected TestUtils utils;

    String businessServiceUrl;
    String persistenceServiceUrl;
    String pdfServiceUrl;
    String mobileNumber;
    protected String documentManagementUrl;

    @Autowired
    public void businessServiceConfiguration(@Value("${probate.business.url}") String businessServiceUrl,
                                             @Value("${probate.persistence.url}") String persistenceServiceUrl,
                                             @Value("${probate.pdfservice.url}") String pdfServiceUrl,
                                             @Value("${probate.notify.mobile}") String mobileNumber,
                                             @Value("${probate.document_management.ur}") String documentManagementUrl) {
        this.businessServiceUrl = businessServiceUrl;
        this.persistenceServiceUrl = persistenceServiceUrl;
        this.pdfServiceUrl = pdfServiceUrl;
        this.mobileNumber = mobileNumber;
        this.documentManagementUrl = documentManagementUrl;
    }

    @Rule
    public SpringIntegration springIntegration;

    IntegrationTestBase() {
        this.springIntegration = new SpringIntegration();
    }
}
