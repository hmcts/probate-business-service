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
        "[", "&lbrack;",
        "]", "&rbrack;",
        "*", "&ast;",
        "#", "&num;",
        "^", "&Hat;",
         // notify handles lpar and rpar in a weird way, so we use the hex escape for them
         "(", "&#x28;",
         ")", "&#x29;"
    );

    @Bean
    NotifyPersonalisationEscapeService notifyPersonalisationEscapeService() {
        return new NotifyPersonalisationEscapeService(toEscape);
    }
}
