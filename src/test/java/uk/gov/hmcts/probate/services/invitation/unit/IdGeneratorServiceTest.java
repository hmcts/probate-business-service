package uk.gov.hmcts.probate.services.invitation.unit;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.idgeneration.strategy.ConstantStrategy;
import uk.gov.hmcts.probate.services.idgeneration.strategy.ProbateStrategy;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdGeneratorServiceTest {

    @Test
    public void whenStrategyIsConstantReturnConstant() {
        IdGeneratorService idGeneratorService = new IdGeneratorService(new ConstantStrategy());

        String id = idGeneratorService.generate(new HashMap<>());

        assertEquals(ConstantStrategy.CONSTANT, id);
    }

    @Test
    public void whenStrategyIsProbateReturnAnIdThatContainsTheApplicantName() {
        IdGeneratorService idGeneratorService = new IdGeneratorService(new ProbateStrategy());
        Map<String, String> data = new HashMap<>();
        data.put("firstName", "Dumitru");
        data.put("lastName", "Panaghiea");

        String id = idGeneratorService.generate(data);

        assertThat(id).contains("dumitru-panaghiea");
    }

    @Test
    public void removeSpecialCharactersFromGeneratedId() {
        IdGeneratorService idGeneratorService = new IdGeneratorService(new ProbateStrategy());
        Map<String, String> data = new HashMap<>();
        data.put("firstName", "Fred!£$%^&*():@~");
        data.put("lastName", "Wood-Wood");

        String id = idGeneratorService.generate(data);

        assertThat(id).contains("fred-wood-wood");
    }
}
