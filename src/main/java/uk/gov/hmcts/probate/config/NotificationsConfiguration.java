package uk.gov.hmcts.probate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.gov.service.notify.NotificationClient;

@Configuration
@EnableScheduling
@EnableAsync
public class NotificationsConfiguration {

    @Bean
    public NotificationClient primaryNotificationClient(
        @Value("${services.notify.apiKeyPrimary}") String primaryApiKey) {
        return new NotificationClient(primaryApiKey);
    }

    @Bean
    public NotificationClient secondaryNotificationClient(
        @Value("${services.notify.apiKeySecondary}") String secondaryApiKey) {
        return new NotificationClient(secondaryApiKey);
    }
}
