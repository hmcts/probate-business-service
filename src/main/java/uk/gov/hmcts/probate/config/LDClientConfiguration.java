package uk.gov.hmcts.probate.config;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.LDClient;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LDClientConfiguration {
    @Bean
    public LDClientInterface ldClient(
        @Value("${ld.sdk_key}") final String ldSdkKey) {
        return new LDClient(ldSdkKey);
    }

    @Bean
    public LDContext ldContext(
        @Value("${ld.user.key}") final String ldUserKey,
        @Value("${ld.user.firstName}") final String ldUserFirstName,
        @Value("${ld.user.lastName}") final String ldUserLastName) {
        final String contextName = new StringBuilder()
            .append(ldUserFirstName)
            .append(" ")
            .append(ldUserLastName)
            .toString();
        return LDContext.builder(ldUserKey)
            .name(contextName)
            .kind("application")
            .set("timestamp", String.valueOf(System.currentTimeMillis()))
            .build();
    }
}
