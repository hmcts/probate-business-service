package uk.gov.hmcts.probate.services.invitation.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.idgeneration.strategy.ConstantStrategy;
import uk.gov.hmcts.probate.services.idgeneration.strategy.ProbateStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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

    private static Stream<Arguments> applicantName() {
        return Stream.of(arguments("James", null), arguments(null, "Bond"));
    }

    @ParameterizedTest
    @MethodSource("applicantName")
    void shouldThrowExceptionWhenNameIsNull(final String firstName, final String lastName) {
        IdGeneratorService idGeneratorService = new IdGeneratorService(new ProbateStrategy());
        Map<String, String> data = new HashMap<>();
        data.put("firstName", firstName);
        data.put("lastName", lastName);

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            String id = idGeneratorService.generate(data);
        });

        assertEquals("Applicant first name or last name cannot be null.", exception.getMessage());
    }

}
