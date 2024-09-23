package uk.gov.hmcts.probate.services.invitation.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.invitation.InvitationService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.service.notify.NotificationClientException;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Invite Generation Service")
public class InvitationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationController.class);
    private static final String SESSION_MSG = "Processing session id {} : {}";

    @Autowired
    @Qualifier("identityGeneratorService")
    private IdGeneratorService idGeneratorService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(path = "/invite/bilingual", consumes = MediaType.APPLICATION_JSON)
    public String inviteBilingual(@Valid @RequestBody Invitation encodedInvitation,
                                  BindingResult bindingResult,
                                  @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        return sendInvitation(encodedInvitation, bindingResult, sessionId, Boolean.TRUE);
    }

    @PostMapping(path = "/invite/bilingual/{inviteId}", consumes = MediaType.APPLICATION_JSON)
    public String inviteBilingual(@PathVariable("inviteId") String inviteId,
                                  @Valid @RequestBody Invitation invitation,
                                  BindingResult bindingResult,
                                  @RequestHeader("Session-Id") String sessionId) throws NotificationClientException {
        LOGGER.info(SESSION_MSG, getSessionId(sessionId), bindingResult.getFieldErrors());
        invitationService.sendEmail(inviteId, invitation, Boolean.TRUE);
        return inviteId;
    }

    @PostMapping(path = "/invite", consumes = MediaType.APPLICATION_JSON)
    public String invite(@Valid @RequestBody Invitation encodedInvitation,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        return sendInvitation(encodedInvitation, bindingResult, sessionId, Boolean.FALSE);
    }


    @PostMapping(path = "/invite/{inviteId}", consumes = MediaType.APPLICATION_JSON)
    public String invite(@PathVariable("inviteId") String inviteId,
                         @Valid @RequestBody Invitation invitation,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId) throws NotificationClientException {
        LOGGER.info(SESSION_MSG, getSessionId(sessionId), bindingResult.getFieldErrors());
        invitationService.sendEmail(inviteId, invitation, Boolean.FALSE);
        return inviteId;
    }

    private String sendInvitation(Invitation encodedInvitation, BindingResult bindingResult, String sessionId,
                                  Boolean isBlingual) throws UnsupportedEncodingException, NotificationClientException {
        LOGGER.info(SESSION_MSG, getSessionId(sessionId), bindingResult.getFieldErrors());
        Invitation invitation = invitationService.decodeURL(encodedInvitation);

        Map<String, String> data = new HashMap<>();
        data.put("firstName", invitation.getFirstName());
        data.put("lastName", invitation.getLastName());

        String linkId = idGeneratorService.generate(data);
        invitationService.sendEmail(linkId, invitation, isBlingual);
        return linkId;
    }

    private String getSessionId(String sessionId) {
        return sessionId.replaceAll("[\n|\r|\t]", "_");
    }
}
