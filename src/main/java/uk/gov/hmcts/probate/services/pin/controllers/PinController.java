package uk.gov.hmcts.probate.services.pin.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.services.pin.PinService;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@Tag(name = "Pin Service")
public class PinController {

    private static final String VALID_PHONENUMBER_CHARACTERS_REGEX = "(\\+)*[0-9]+";
    private static final String INVALID_PHONENUMBER_CHARACTERS_REGEX = "[ \\(\\)\\[\\]-]";

    @Autowired
    private PinService pinService;

    @GetMapping(path = "/pin")
    public ResponseEntity<String> invite(@RequestParam(value = "phoneNumber") String phoneNumber,
                                         @RequestHeader("Session-Id") String sessionId)
        throws UnsupportedEncodingException {
        return getStringResponseEntity(phoneNumber, sessionId, Boolean.FALSE);
    }

    @GetMapping(path = "/pin/bilingual")
    public ResponseEntity<String> inviteBilingual(@RequestParam(value = "phoneNumber") String phoneNumber,
                                                  @RequestHeader("Session-Id") String sessionId)
        throws UnsupportedEncodingException {
        return getStringResponseEntity(phoneNumber, sessionId, Boolean.TRUE);
    }

    private ResponseEntity<String> getStringResponseEntity(String phoneNumber, String sessionId, Boolean isBilingual)
        throws UnsupportedEncodingException {
        if (sessionId == null) {
            log.error("Session-Id request header not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        log.info("Processing session id " + sessionId);
        phoneNumber = URLDecoder.decode(phoneNumber, StandardCharsets.UTF_8.toString());
        phoneNumber = phoneNumber.replaceAll(INVALID_PHONENUMBER_CHARACTERS_REGEX, "");
        if (!phoneNumber.matches(VALID_PHONENUMBER_CHARACTERS_REGEX)) {
            log.error("Unable to validate phoneNumber parameter");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        String pin = null;
        try {
            pin = pinService.generateAndSend(phoneNumber, isBilingual);
            return ResponseEntity.ok(pin);
        } catch (NotificationClientException e) {
            log.error("Unable to send sms with error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(path = "/pin/{phoneNumber}")
    public String inviteLegacy(@PathVariable String phoneNumber,
                               @RequestHeader("Session-Id") String sessionId) throws NotificationClientException {
        log.info("Processing session id " + sessionId);
        return pinService.generateAndSend(phoneNumber, Boolean.FALSE);
    }

}
