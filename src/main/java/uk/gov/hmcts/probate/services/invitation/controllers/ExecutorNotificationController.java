package uk.gov.hmcts.probate.services.invitation.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.invitation.ExecutorNotificationService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.ExecutorNotification;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.service.notify.NotificationClientException;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Executor Notification Service")
public class ExecutorNotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorNotificationController.class);
    private static final String SESSION_MSG = "Processing session id {} : {}";

    @Autowired
    private ExecutorNotificationService executorNotificationService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(path = "/executor-notification/bilingual", consumes = MediaType.APPLICATION_JSON)
    public String signedBilingual(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                                  BindingResult bindingResult,
                                  @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        return sendNotification(encodedExecutorNotification, bindingResult, sessionId, Boolean.TRUE);
    }

    @GetMapping(path = "/executor-notification", consumes = MediaType.APPLICATION_JSON)
    public String signed(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        return sendNotification(encodedExecutorNotification, bindingResult, sessionId, Boolean.FALSE);
    }

    @GetMapping(path = "/executor-notification/all", consumes = MediaType.APPLICATION_JSON)
    public String allSigned(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        return sendAllSignedNotification(encodedExecutorNotification, bindingResult, sessionId, Boolean.FALSE);
    }

    @GetMapping(path = "/executor-notification/all-bilingual", consumes = MediaType.APPLICATION_JSON)
    public String allSignedBilingual(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                            BindingResult bindingResult,
                            @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        return sendAllSignedNotification(encodedExecutorNotification, bindingResult, sessionId, Boolean.TRUE);
    }

    private String sendNotification(ExecutorNotification encodedExecutorNotification, BindingResult bindingResult, String sessionId,
                                  Boolean isBlingual) throws UnsupportedEncodingException, NotificationClientException {
        LOGGER.info(SESSION_MSG, getSessionId(sessionId), bindingResult.getFieldErrors());
        ExecutorNotification executorNotification = executorNotificationService.decodeURL(encodedExecutorNotification);

        executorNotificationService.sendEmail(executorNotification, isBlingual);
        return ResponseEntity.ok().toString();
    }

    private String sendAllSignedNotification(ExecutorNotification encodedExecutorNotification, BindingResult bindingResult, String sessionId,
                                    Boolean isBlingual) throws UnsupportedEncodingException, NotificationClientException {
        LOGGER.info(SESSION_MSG, getSessionId(sessionId), bindingResult.getFieldErrors());
        ExecutorNotification executorNotification = executorNotificationService.decodeURL(encodedExecutorNotification);

        executorNotificationService.sendAllSignedEmail(executorNotification, isBlingual);
        return ResponseEntity.ok().toString();
    }

    private String getSessionId(String sessionId) {
        return sessionId.replaceAll("[\n|\r|\t]", "_");
    }
}
