package uk.gov.hmcts.probate.services.idgeneration;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.idgeneration.strategy.IdGenerationStrategy;

import java.util.Collections;
import java.util.Map;

public class IdGeneratorService {

    private IdGenerationStrategy idGenerationStrategy;

    public IdGeneratorService(IdGenerationStrategy idGenerationStrategy) {
        this.idGenerationStrategy = idGenerationStrategy;
    }

    public String generate(Map<String, String> data) {
        return idGenerationStrategy.generate(data);
    }

    public String generate() {
        return idGenerationStrategy.generate(Collections.emptyMap());
    }

}
