package uk.gov.hmcts.probate.services.notification.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.services.notification.NotificationService;
import uk.gov.hmcts.reform.probate.model.notification.ApplicationReceivedDetails;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

@RestController
@Tag(name = "Notification Service")
public class NotificationController {


    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);
    private static final String SESSION_MSG = "Processing applicant received email address: {}";

    @Autowired
    private NotificationService notificationService;


    @PostMapping(path = "/notification/application-received", consumes = MediaType.APPLICATION_JSON)
    public String sendApplicationReceivedNotification(@Valid @RequestBody ApplicationReceivedDetails applicationRecievedDetails,
                                                      BindingResult bindingResult
                                                      ) throws NotificationClientException, UnsupportedEncodingException {
        return sendNotification(applicationRecievedDetails, bindingResult);
    }

    private String sendNotification(ApplicationReceivedDetails encodedApplicationRecievedDetails, BindingResult bindingResult) throws UnsupportedEncodingException, NotificationClientException {
        LOGGER.info(SESSION_MSG, encodedApplicationRecievedDetails.getApplicantEmail());
        ApplicationReceivedDetails applicationRecievedDetails = notificationService.decodeURL(encodedApplicationRecievedDetails);
        notificationService.sendApplicationRecievedEmail(applicationRecievedDetails);
        return "OK";
    }

    private String getSessionId(String sessionId) {
        return sessionId.replaceAll("[\n|\r|\t]", "_");
    }
}
