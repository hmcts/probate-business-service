package uk.gov.hmcts.probate.services.invitation.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.invitation.ExecutorNotificationService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.ExecutorNotification;
import uk.gov.service.notify.NotificationClientException;

import jakarta.validation.Valid;
import java.io.UnsupportedEncodingException;

@RestController
@Tag(name = "Executor Notification Service")
public class ExecutorNotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorNotificationController.class);
    private static final String LOGGING_MSG = "Processing executor notification : {}";

    @Autowired
    private ExecutorNotificationService executorNotificationService;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(path = "/executor-notification/bilingual", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signedBilingual(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                                                BindingResult bindingResult)
        throws NotificationClientException, UnsupportedEncodingException {
        try {
            sendNotification(encodedExecutorNotification, bindingResult, Boolean.TRUE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotificationClientException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping(path = "/executor-notification", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> signed(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                         BindingResult bindingResult)
        throws NotificationClientException, UnsupportedEncodingException {
        try {
            LOGGER.info("executor-notification endpoint hit");
            sendNotification(encodedExecutorNotification, bindingResult, Boolean.FALSE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotificationClientException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping(path = "/executor-notification/all", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> allSigned(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                         BindingResult bindingResult)
        throws NotificationClientException, UnsupportedEncodingException {
        try {
            LOGGER.info("executor all signed endpoint hit");
            sendAllSignedNotification(encodedExecutorNotification, bindingResult, Boolean.FALSE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotificationClientException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping(path = "/executor-notification/all-bilingual", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> allSignedBilingual(@Valid @RequestBody ExecutorNotification encodedExecutorNotification,
                            BindingResult bindingResult)
        throws NotificationClientException, UnsupportedEncodingException {
        try {
            sendAllSignedNotification(encodedExecutorNotification, bindingResult, Boolean.TRUE);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotificationClientException e) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private void sendNotification(ExecutorNotification encodedExecutorNotification,
                                  BindingResult bindingResult,
                                  Boolean isBlingual) throws UnsupportedEncodingException, NotificationClientException {
        LOGGER.info(LOGGING_MSG, bindingResult.getFieldErrors());
        ExecutorNotification executorNotification = executorNotificationService.decodeURL(encodedExecutorNotification);

        executorNotificationService.sendEmail(executorNotification, isBlingual);
    }

    private void sendAllSignedNotification(ExecutorNotification encodedExecutorNotification,
                                           BindingResult bindingResult, Boolean isBlingual)
        throws UnsupportedEncodingException, NotificationClientException {
        LOGGER.info(LOGGING_MSG, bindingResult.getFieldErrors());
        ExecutorNotification executorNotification = executorNotificationService.decodeURL(encodedExecutorNotification);

        executorNotificationService.sendAllSignedEmail(executorNotification, isBlingual);
    }
}
