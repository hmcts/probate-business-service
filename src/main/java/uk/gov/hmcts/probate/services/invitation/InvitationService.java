package uk.gov.hmcts.probate.services.invitation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.persistence.PersistenceClient;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

@Slf4j
@Component
public class InvitationService {

    @Value("${services.notify.invitedata.templateId}")
    String templateId;

    @Value("${services.notify.invitedata.bilingualTemplateId}")
    String bilingualTemplateId;

    @Value("${services.notify.invitedata.intestacyTemplateId}")
    String intestacyTemplateId;

    @Value("${services.notify.invitedata.bilingualIntestacyTemplateId}")
    String bilingualIntestacyTemplateId;

    @Value("${services.notify.invitedata.inviteLink}")
    String inviteLink;

    @Value("${services.notify.invitedata.intestacyLink}")
    String intestacyLink;

    private final NotificationClient notificationClient;

    @Autowired
    private PersistenceClient persistenceClient;

    private final NotifyPersonalisationEscapeService notifyPersonalisationEscapeService;

    public InvitationService(
            final NotificationClient notificationClient,
            final NotifyPersonalisationEscapeService notifyPersonalisationEscapeService) {
        this.notificationClient = notificationClient;
        this.notifyPersonalisationEscapeService = notifyPersonalisationEscapeService;
    }

    public void sendEmail(String linkId, Invitation invitation, Boolean isBilingual)
        throws NotificationClientException {
        String notifyTemplate = Boolean.TRUE.equals(isBilingual) ? bilingualTemplateId : templateId;
        log.info("Sending email for case {} with template {}", invitation.getFormdataId(), notifyTemplate);
        notificationClient.sendEmail(notifyTemplate, invitation.getEmail(),
            createPersonalisation(linkId, invitation), linkId);
    }

    public void sendIntestacyEmail(String linkId, Invitation invitation, Boolean isBilingual)
        throws NotificationClientException {
        String notifyTemplate = Boolean.TRUE.equals(isBilingual) ? bilingualIntestacyTemplateId : intestacyTemplateId;
        log.info("Send intestacy email for case {} with template {}", invitation.getFormdataId(), intestacyTemplateId);
        notificationClient.sendEmail(intestacyTemplateId, invitation.getEmail(),
            createIntestacyPersonalisation(linkId, invitation), linkId);
    }

    private Map<String, String> createIntestacyPersonalisation(String linkId, Invitation inviteData) {
        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put("applicant_name", inviteData.getLeadExecutorName());
        personalisation.put("deceasedName", inviteData.getFirstName() + ' ' + inviteData.getLastName());
        personalisation.put("co-app_name", inviteData.getExecutorName());
        personalisation.put("link", intestacyLink + linkId);

        return personalisation;
    }

    private Map<String, String> createPersonalisation(String linkId, Invitation inviteData) {
        HashMap<String, String> personalisation = new HashMap<>();

        // alias for length and readability
        final UnaryOperator<String> esc = notifyPersonalisationEscapeService::escape;

        personalisation.put("executorName", esc.apply(inviteData.getExecutorName()));
        personalisation.put("leadExecutorName", esc.apply(inviteData.getLeadExecutorName()));
        personalisation.put("deceasedFirstName", esc.apply(inviteData.getFirstName()));
        personalisation.put("deceasedLastName", esc.apply(inviteData.getLastName()));
        personalisation.put("link", inviteLink + linkId);

        return personalisation;
    }

    public Invitation decodeURL(Invitation invitation) throws UnsupportedEncodingException {
        invitation.setExecutorName(decodeURLParam(invitation.getExecutorName()));
        invitation.setFirstName(decodeURLParam(invitation.getFirstName()));
        invitation.setLastName(decodeURLParam(invitation.getLastName()));
        invitation.setLeadExecutorName(decodeURLParam(invitation.getLeadExecutorName()));
        return invitation;
    }

    private String decodeURLParam(String uriParam) throws UnsupportedEncodingException {
        return URLDecoder.decode(uriParam, StandardCharsets.UTF_8.toString());
    }
}
