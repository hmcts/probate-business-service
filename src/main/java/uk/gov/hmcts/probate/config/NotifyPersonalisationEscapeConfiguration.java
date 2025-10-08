package uk.gov.hmcts.probate.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.probate.services.invitation.NotifyPersonalisationEscapeService;

import java.util.Map;

@Slf4j
@Configuration
public class NotifyPersonalisationEscapeConfiguration {
    private final Map<String, String> toEscape = Map.of(
        "[", "\\[",
        "]", "\\]",
        "*", "\\*",
        "#", "\\#",
        "^", "",
         "(", "\\(",
         ")", "\\)",
        "-", "\\-",
        "\\", "\\\\"
    );

    @Bean
    NotifyPersonalisationEscapeService notifyPersonalisationEscapeService() {
        return new NotifyPersonalisationEscapeService(toEscape);
    }
}
