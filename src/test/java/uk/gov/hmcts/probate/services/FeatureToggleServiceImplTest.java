package uk.gov.hmcts.probate.services;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FeatureToggleServiceImplTest {
    @Mock
    LDClientInterface ldClientMock;
    @Mock
    LDContext ldContextMock;

    FeatureToggleServiceImpl featureToggleService;

    AutoCloseable closeableMocks;

    @BeforeEach
    void setUp() {
        closeableMocks = MockitoAnnotations.openMocks(this);

        featureToggleService = new FeatureToggleServiceImpl(
            ldClientMock,
            ldContextMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void lookupFromLDWhenUsePrimaryNotifyKeyQueried(final boolean expected) {
        when(ldClientMock.boolVariation(
            eq(FeatureToggleServiceImpl.USE_PRIMARY_NOTIFY_KEY),
            eq(ldContextMock),
            anyBoolean()))
            .thenReturn(expected);

        final boolean actual = featureToggleService.usePrimaryNotifyKey();

        verify(ldClientMock).boolVariation(
            eq(FeatureToggleServiceImpl.USE_PRIMARY_NOTIFY_KEY),
            eq(ldContextMock),
            anyBoolean());
        assertThat(actual, equalTo(expected));
    }
}
