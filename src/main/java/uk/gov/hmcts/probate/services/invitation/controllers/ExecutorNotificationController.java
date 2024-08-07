package uk.gov.hmcts.probate.services.invitation.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.invitation.ExecutorNotificationService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.ExecutorNotification;
import uk.gov.service.notify.NotificationClientException;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;

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
    public ResponseEntity<Void> signedBilingual(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                                                BindingResult bindingResult,
                                                @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        try {
            sendNotification(encodedExecutorNotification, bindingResult, sessionId, Boolean.TRUE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotificationClientException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping(path = "/executor-notification", consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<Void> signed(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        try {
            sendNotification(encodedExecutorNotification, bindingResult, sessionId, Boolean.FALSE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotificationClientException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping(path = "/executor-notification/all", consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<Void> allSigned(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                         BindingResult bindingResult,
                         @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        try {
            sendAllSignedNotification(encodedExecutorNotification, bindingResult, sessionId, Boolean.FALSE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotificationClientException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping(path = "/executor-notification/all-bilingual", consumes = MediaType.APPLICATION_JSON)
    public ResponseEntity<Void> allSignedBilingual(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                            BindingResult bindingResult,
                            @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException, UnsupportedEncodingException {
        try {
            sendAllSignedNotification(encodedExecutorNotification, bindingResult, sessionId, Boolean.TRUE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotificationClientException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private void sendNotification(ExecutorNotification encodedExecutorNotification,
                                  BindingResult bindingResult, String sessionId,
                                  Boolean isBlingual) throws UnsupportedEncodingException, NotificationClientException {
        LOGGER.info(SESSION_MSG, getSessionId(sessionId), bindingResult.getFieldErrors());
        ExecutorNotification executorNotification = executorNotificationService.decodeURL(encodedExecutorNotification);

        executorNotificationService.sendEmail(executorNotification, isBlingual);
    }

    private void sendAllSignedNotification(ExecutorNotification encodedExecutorNotification,
                                           BindingResult bindingResult, String sessionId, Boolean isBlingual)
        throws UnsupportedEncodingException, NotificationClientException {
        LOGGER.info(SESSION_MSG, getSessionId(sessionId), bindingResult.getFieldErrors());
        ExecutorNotification executorNotification = executorNotificationService.decodeURL(encodedExecutorNotification);

        executorNotificationService.sendAllSignedEmail(executorNotification, isBlingual);
    }

    private String getSessionId(String sessionId) {
        return sessionId.replaceAll("[\n|\r|\t]", "_");
    }
}
