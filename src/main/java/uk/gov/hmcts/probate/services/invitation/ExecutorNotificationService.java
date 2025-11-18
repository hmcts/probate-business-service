package uk.gov.hmcts.probate.services.invitation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorNotificationService.class);

    @Value("${services.notify.executorNotification.templateId}")
    String templateId;

    @Value("${services.notify.executorNotification.bilingualTemplateId}")
    String bilingualTemplateId;

    @Value("${services.notify.executorNotification.allSignedTemplateId}")
    String allSignedTemplateId;

    @Value("${services.notify.executorNotification.allSignedBilingualTemplateId}")
    String allSignedBilingualTemplateId;

    @Value("${services.notify.executorNotification.coApplicantDisagreeTemplateId}")
    String coApplicantDisagreeTemplateId;

    @Value("${services.notify.executorNotification.coApplicantDisagreeBilingualTemplateId}")
    String coApplicantDisagreeBilingualTemplateId;

    @Value("${services.notify.executorNotification.coApplicantTemplateId}")
    String coApplicantTemplateId;

    @Value("${services.notify.executorNotification.coApplicantBilingualTemplateId}")
    String coApplicantBilingualTemplateId;

    @Value("${services.notify.executorNotification.coApplicantAllSignedTemplateId}")
    String coApplicantAllSignedTemplateId;

    @Value("${services.notify.executorNotification.coApplicantAllSignedBilingualTemplateId}")
    String coApplicantAllSignedBilingualTemplateId;
    @Autowired
    private NotificationClient notificationClient;

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

    public void sendCoApplicantEmail(ExecutorNotification executorNotification, Boolean isBilingual)
        throws NotificationClientException {
        LOGGER.info("sending CoApplicant notification email");
        notificationClient.sendEmail(isBilingual ? coApplicantBilingualTemplateId : coApplicantTemplateId,
            executorNotification.getEmail(), createCoApplicantPersonalisation(executorNotification), null);
    }

    public void sendCoApplicantDisagreeEmail(ExecutorNotification executorNotification, Boolean isBilingual)
        throws NotificationClientException {
        LOGGER.info("sending CoApplicant Disagree notification email");
        notificationClient.sendEmail(isBilingual
                ? coApplicantDisagreeBilingualTemplateId : coApplicantDisagreeTemplateId,
            executorNotification.getEmail(), createCoApplicantPersonalisation(executorNotification), null);
    }

    public void sendCoApplicantAllSignedEmail(ExecutorNotification executorNotification, Boolean isBilingual)
        throws NotificationClientException {
        LOGGER.info("sending CoApplicant all signed email");
        notificationClient.sendEmail(isBilingual
                ? coApplicantAllSignedBilingualTemplateId : coApplicantAllSignedTemplateId,
                executorNotification.getEmail(), createCoApplicantPersonalisation(executorNotification), null);
    }

    private Map<String, String> createCoApplicantPersonalisation(ExecutorNotification executorNotification) {
        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put("applicant_name", executorNotification.getApplicantName());
        personalisation.put("co_applicant_name", executorNotification.getExecutorName());
        personalisation.put("deceased_name", executorNotification.getDeceasedName());
        personalisation.put("deceased_dod", executorNotification.getDeceasedDod());
        personalisation.put("ccd_reference", executorNotification.getCcdReference());
        return personalisation;
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
        executorNotification.setEmail(executorNotification.getEmail());
        return executorNotification;
    }

    private String decodeURLParam(String uriParam) throws UnsupportedEncodingException {
        return URLDecoder.decode(uriParam, StandardCharsets.UTF_8.toString());
    }
}
