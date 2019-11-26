package uk.gov.hmcts.probate.services.invitation.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.invitation.InvitationService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.service.notify.NotificationClientException;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Invite Generation Service")
public class InvitationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvitationController.class);

    @Autowired
    @Qualifier("identityGeneratorService")
    private IdGeneratorService idGeneratorService;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(path = "/invite", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public String invite(@Valid @RequestBody Invitation encodedInvitation,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId) throws NotificationClientException, UnsupportedEncodingException {
        LOGGER.info("Processing session id " + sessionId + " : " + bindingResult.getFieldErrors());
        Invitation invitation = invitationService.decodeURL(encodedInvitation);

        Map<String, String> data = new HashMap<>();
        data.put("firstName", invitation.getFirstName());
        data.put("lastName", invitation.getLastName());

        String linkId = idGeneratorService.generate(data);
        invitationService.sendEmail(linkId, invitation);
        return linkId;
    }


    @RequestMapping(path = "/invite/{inviteId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
    public String invite(@PathVariable("inviteId") String inviteId,
                         @Valid @RequestBody Invitation invitation,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId) throws NotificationClientException {
        LOGGER.info("Processing session id " + sessionId + " : " + bindingResult.getFieldErrors());
        invitationService.sendEmail(inviteId, invitation);
        return inviteId;
    }

}
