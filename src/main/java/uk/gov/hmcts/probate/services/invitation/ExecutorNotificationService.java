package uk.gov.hmcts.probate.services.invitation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.probate.model.multiapplicant.ExecutorNotification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

@Component
public class ExecutorNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorNotificationService.class);

    @Value("${services.notify.executorNotification.templateId}")
    String templateId;

    @Value("${services.notify.executorNotification.bilingualTemplateId}")
    String bilingualTemplateId;

    @Value("${services.notify.executorNotification.allSignedTemplateId}")
    String allSignedTemplateId;

    @Value("${services.notify.executorNotification.allSignedBilingualTemplateId}")
    String allSignedBilingualTemplateId;

    private final NotificationClient notificationClient;
    private final NotifyPersonalisationEscapeService notifyPersonalisationEscapeService;

    public ExecutorNotificationService(
            final NotificationClient notificationClient,
            final NotifyPersonalisationEscapeService notifyPersonalisationEscapeService) {
        this.notificationClient = notificationClient;
        this.notifyPersonalisationEscapeService = notifyPersonalisationEscapeService;
    }

    public void sendEmail(ExecutorNotification executorNotification, Boolean isBilingual)
        throws NotificationClientException {
        LOGGER.info("sending executor notification email");
        notificationClient.sendEmail(isBilingual ? bilingualTemplateId : templateId, executorNotification.getEmail(),
            createPersonalisation(executorNotification), null);
    }

    public void sendAllSignedEmail(ExecutorNotification executorNotification, Boolean isBilingual)
        throws NotificationClientException {
        LOGGER.info("sending executor all signed email");
        notificationClient.sendEmail(isBilingual ? allSignedBilingualTemplateId : allSignedTemplateId,
            executorNotification.getEmail(), createPersonalisation(executorNotification), null);
    }

    private Map<String, String> createPersonalisation(ExecutorNotification executorNotification) {
        HashMap<String, String> personalisation = new HashMap<>();

        // alias for length and readability
        final UnaryOperator<String> esc = notifyPersonalisationEscapeService::escape;

        final String execName = esc.apply(executorNotification.getExecutorName());
        final String applName = esc.apply(executorNotification.getApplicantName());
        final String decdName = esc.apply(executorNotification.getDeceasedName());

        personalisation.put("executor_name", execName);
        personalisation.put("applicant_name", applName);
        personalisation.put("deceased_name", decdName);
        personalisation.put("deceased_dod", executorNotification.getDeceasedDod());
        personalisation.put("ccd_reference", executorNotification.getCcdReference());
        return personalisation;
    }

    public ExecutorNotification decodeURL(ExecutorNotification executorNotification)
        throws UnsupportedEncodingException {
        executorNotification.setExecutorName(decodeURLParam(executorNotification.getExecutorName()));
        executorNotification.setDeceasedName(decodeURLParam(executorNotification.getDeceasedName()));
        executorNotification.setDeceasedDod(decodeURLParam(executorNotification.getDeceasedDod()));
        executorNotification.setApplicantName(decodeURLParam(executorNotification.getApplicantName()));
        executorNotification.setCcdReference(executorNotification.getCcdReference());
        executorNotification.setEmail(executorNotification.getEmail());
        return executorNotification;
    }

    private String decodeURLParam(String uriParam) throws UnsupportedEncodingException {
        return URLDecoder.decode(uriParam, StandardCharsets.UTF_8.toString());
    }
}
