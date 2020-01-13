package uk.gov.hmcts.probate.services.invitation.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.invitation.InvitationService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

@RestController
@Tag(name = "Invite Generation Service")
public class InvitationController {

    @Autowired
    @Qualifier("identityGeneratorService")
    private IdGeneratorService idGeneratorService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(path = "/invite/bilingual", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public String inviteBilingual(@Valid @RequestBody Invitation encodedInvitation,
                                  BindingResult bindingResult,
                                  @RequestHeader("Session-Id") String sessionId) throws NotificationClientException, UnsupportedEncodingException {
        return sendInvitation(encodedInvitation, bindingResult, sessionId, Boolean.TRUE);
    }

    @RequestMapping(path = "/invite", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public String invite(@Valid @RequestBody Invitation encodedInvitation,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId) throws NotificationClientException, UnsupportedEncodingException {
        return sendInvitation(encodedInvitation, bindingResult, sessionId, Boolean.FALSE);
    }


    @RequestMapping(path = "/invite/{inviteId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public String invite(@PathVariable("inviteId") String inviteId,
                         @Valid @RequestBody Invitation invitation,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId) throws NotificationClientException {
        invitationService.sendEmail(inviteId, invitation, Boolean.FALSE);
        return inviteId;
    }

    @RequestMapping(path = "/invite/bilingual/{inviteId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public String inviteBilingual(@PathVariable("inviteId") String inviteId,
                                  @Valid @RequestBody Invitation invitation,
                                  BindingResult bindingResult,
                                  @RequestHeader("Session-Id") String sessionId) throws NotificationClientException {
        invitationService.sendEmail(inviteId, invitation, Boolean.TRUE);
        return inviteId;
    }

    private String sendInvitation(Invitation encodedInvitation, BindingResult bindingResult, String sessionId, Boolean isBlingual) throws UnsupportedEncodingException, NotificationClientException {
        Invitation invitation = invitationService.decodeURL(encodedInvitation);

        Map<String, String> data = new HashMap<>();
        data.put("firstName", invitation.getFirstName());
        data.put("lastName", invitation.getLastName());

        String linkId = idGeneratorService.generate(data);
        invitationService.sendEmail(linkId, invitation, isBlingual);
        return linkId;
    }
}
