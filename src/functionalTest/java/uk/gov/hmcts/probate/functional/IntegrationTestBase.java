package uk.gov.hmcts.probate.functional;

import net.thucydides.junit.spring.SpringIntegration;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestContextConfiguration.class)
public abstract class IntegrationTestBase {

    @Autowired
    protected TestUtils utils;

    String businessServiceUrl;
    String persistenceServiceUrl;
    String mobileNumber;

    @Autowired
    public void businessServiceConfiguration(@Value("${probate.business.url}") String businessServiceUrl,
                                             @Value("${probate.persistence.url}") String persistenceServiceUrl,
                                             @Value("${probate.notify.mobile}") String mobileNumber) {
        this.businessServiceUrl = businessServiceUrl;
        this.persistenceServiceUrl = persistenceServiceUrl;
        this.mobileNumber = mobileNumber;
    }

    @Rule
    public SpringIntegration springIntegration;

    IntegrationTestBase() {
        this.springIntegration = new SpringIntegration();
    }
}