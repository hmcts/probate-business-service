package uk.gov.hmcts.probate.services.invitation.unit;

import org.junit.Test;
import uk.gov.hmcts.probate.services.idgeneration.strategy.ConstantStrategy;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.idgeneration.strategy.ProbateStrategy;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class IdGeneratorServiceTest {

    @Test
    public void whenStrategyIsConstantReturnConstant() {
        IdGeneratorService idGeneratorService = new IdGeneratorService(new ConstantStrategy());

        String id = idGeneratorService.generate(new HashMap<>());

        assertThat(id, equalTo(ConstantStrategy.CONSTANT));
    }

    @Test
    public void whenStrategyIsProbateReturnAnIdThatContainsTheApplicantName() {
        IdGeneratorService idGeneratorService = new IdGeneratorService(new ProbateStrategy());
        Map<String, String> data = new HashMap<>();
        data.put("firstName", "Dumitru");
        data.put("lastName", "Panaghiea");

        String id = idGeneratorService.generate(data);

        assertThat(id, containsString("dumitru-panaghiea"));
    }

    @Test
    public void removeSpecialCharactersFromGeneratedId() {
        IdGeneratorService idGeneratorService = new IdGeneratorService(new ProbateStrategy());
        Map<String, String> data = new HashMap<>();
        data.put("firstName", "Fred!£$%^&*():@~");
        data.put("lastName", "Wood-Wood");

        String id = idGeneratorService.generate(data);

        assertThat(id, containsString("fred-wood-wood"));
    }
}
