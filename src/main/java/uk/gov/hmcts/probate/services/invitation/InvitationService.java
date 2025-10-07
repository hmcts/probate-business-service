package uk.gov.hmcts.probate.services.invitation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
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

    @Value("${services.notify.invitedata.inviteLink}")
    String inviteLink;

    private final NotificationClient notificationClient;

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
