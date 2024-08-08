package uk.gov.hmcts.probate.services.invitation;

import org.springframework.beans.factory.annotation.Autowired;
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

@Component
public class ExecutorNotificationService {

    @Value("${services.notify.executorNotification.templateId}")
    String templateId;

    @Value("${services.notify.executorNotification.bilingualTemplateId}")
    String bilingualTemplateId;

    @Value("${services.notify.executorNotification.allSignedTemplateId}")
    String allSignedTemplateId;

    @Value("${services.notify.executorNotification.allSignedBilingualTemplateId}")
    String allSignedBilingualTemplateId;

    @Autowired
    private NotificationClient notificationClient;

    public void sendEmail(ExecutorNotification executorNotification, Boolean isBilingual)
        throws NotificationClientException {
        notificationClient.sendEmail(isBilingual ? bilingualTemplateId : templateId, executorNotification.getEmail(),
            createPersonalisation(executorNotification), null);
    }

    public void sendAllSignedEmail(ExecutorNotification executorNotification, Boolean isBilingual)
        throws NotificationClientException {
        notificationClient.sendEmail(isBilingual ? allSignedBilingualTemplateId : allSignedTemplateId,
            executorNotification.getEmail(), createPersonalisation(executorNotification), null);
    }

    private Map<String, String> createPersonalisation(ExecutorNotification executorNotification) {
        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put("executor_name", executorNotification.getExecutorName());
        personalisation.put("applicant_name", executorNotification.getApplicantName());
        personalisation.put("deceased_name", executorNotification.getDeceasedName());
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
        return executorNotification;
    }

    private String decodeURLParam(String uriParam) throws UnsupportedEncodingException {
        return URLDecoder.decode(uriParam, StandardCharsets.UTF_8.toString());
    }
}
