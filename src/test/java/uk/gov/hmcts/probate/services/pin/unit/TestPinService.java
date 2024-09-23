package uk.gov.hmcts.probate.services.pin.unit;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.idgeneration.strategy.PinStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestPinService {
    @Test
    void whenStrategyIsDefaultReturnAnIdThatHasALength6() {
        IdGeneratorService pinGenerator = new IdGeneratorService(new PinStrategy());

        String pin = pinGenerator.generate();

        assertEquals(6, pin.length());
    }
}
