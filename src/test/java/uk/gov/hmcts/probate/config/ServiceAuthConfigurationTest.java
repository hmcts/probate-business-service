package uk.gov.hmcts.probate.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.reform.authorisation.generators.ServiceAuthTokenGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceAuthConfigurationTest {

    @Autowired
    ServiceAuthTokenGenerator serviceAuthTokenGenerator;

    @Test
    public void shouldInstantiateServiceAuthTokenGenerator() {
        assertThat(serviceAuthTokenGenerator, is(notNullValue()));
    }
}
