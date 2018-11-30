package uk.gov.hmcts.probate.services.pin.unit;

import org.junit.Test;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.idgeneration.strategy.PinStrategy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestPinService {
    @Test
    public void whenStrategyIsDefaultReturnAnIdThatHasALength6() {
        IdGeneratorService pinGenerator = new IdGeneratorService(new PinStrategy());

        String pin = pinGenerator.generate();

        assertThat(pin.length(), is(6));
    }
}
