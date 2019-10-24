package uk.gov.hmcts.probate.services.invitation;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.persistence.PersistenceClient;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.hmcts.reform.probate.model.multiapplicant.InviteData;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InvitationService {

    @Value("${services.notify.invitedata.templateId}")
    String templateId;

    @Value("${services.notify.invitedata.inviteLink}")
    String inviteLink;

    @Autowired
    private PersistenceClient persistenceClient;

    @Autowired
    private NotificationClient notificationClient;

    public void sendEmail(String linkId, Invitation invitation) throws NotificationClientException {
        notificationClient.sendEmail(templateId, invitation.getEmail(), createPersonalisation(linkId, invitation), linkId);
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

    public boolean checkAllInvitedAgreed(String formdataId) {
        JsonNode invitesByFormdataId = persistenceClient.getInvitesByFormdataId(formdataId);
        List<String> invitesStatusList = invitesByFormdataId.findValuesAsText("agreed");

        return invitesStatusList != null && !invitesStatusList.contains("false") && !invitesStatusList.contains("null");
    }

    public boolean checkMainApplicantAgreed(String formdataId) {
        JsonNode formdata = persistenceClient.getFormdata(formdataId);
        JsonNode declared = formdata.findPath("declarationCheckbox");

        return declared.asBoolean(false);
    }

    public Invitation decodeURL(Invitation invitation) throws UnsupportedEncodingException {
        invitation.setExecutorName(decodeURLParam(invitation.getExecutorName()));
        invitation.setFirstName(decodeURLParam(invitation.getFirstName()));
        invitation.setLastName(decodeURLParam(invitation.getLastName()));
        invitation.setLeadExecutorName(decodeURLParam(invitation.getLeadExecutorName()));
        return invitation;
    }

    private String decodeURLParam(String URIParam) throws UnsupportedEncodingException {
        return URLDecoder.decode(URIParam, StandardCharsets.UTF_8.toString());
    }
}
