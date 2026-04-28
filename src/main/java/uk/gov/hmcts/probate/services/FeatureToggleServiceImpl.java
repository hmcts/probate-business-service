package uk.gov.hmcts.probate.services;

import com.launchdarkly.sdk.LDContext;
import com.launchdarkly.sdk.server.interfaces.LDClientInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeatureToggleServiceImpl implements FeatureToggleService {
    private final LDClientInterface ldClient;
    private final LDContext ldContext;

    static final String USE_PRIMARY_NOTIFY_KEY = "probate-use-primary-notify-key";

    public FeatureToggleServiceImpl(
        final LDClientInterface ldClient,
        final LDContext ldContext) {
        this.ldClient = ldClient;
        this.ldContext = ldContext;
    }

    private boolean isFeatureToggleOn(
        final String featureToggleCode,
        final boolean defaultValue) {
        return this.ldClient.boolVariation(featureToggleCode, this.ldContext, defaultValue);
    }

    @Override
    public boolean usePrimaryNotifyKey() {
        return isFeatureToggleOn(USE_PRIMARY_NOTIFY_KEY, true);
    }
}
