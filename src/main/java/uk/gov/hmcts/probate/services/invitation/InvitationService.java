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

@Slf4j
@Component
public class InvitationService {

    @Value("${services.notify.invitedata.templateId}")
    String templateId;

    @Value("${services.notify.invitedata.bilingualTemplateId}")
    String bilingualTemplateId;

    @Value("${services.notify.invitedata.inviteLink}")
    String inviteLink;

    @Autowired
    private PersistenceClient persistenceClient;

    @Autowired
    private NotificationClient notificationClient;

    public void sendEmail(String linkId, Invitation invitation, Boolean isBilingual)
        throws NotificationClientException {
        String notifyTemplate = Boolean.TRUE.equals(isBilingual) ? bilingualTemplateId : templateId;
        log.info("Sending email for case {} with template {}", invitation.getFormdataId(), notifyTemplate);
        notificationClient.sendEmail(notifyTemplate, invitation.getEmail(),
            createPersonalisation(linkId, invitation), linkId);
    }


    private Map<String, String> createPersonalisation(String linkId, Invitation inviteData) {
        HashMap<String, String> personalisation = new HashMap<>();

        personalisation.put("executorName", inviteData.getExecutorName());
        personalisation.put("leadExecutorName", inviteData.getLeadExecutorName());
        personalisation.put("deceasedFirstName", inviteData.getFirstName());
        personalisation.put("deceasedLastName", inviteData.getLastName());
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
