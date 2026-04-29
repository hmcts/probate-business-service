package uk.gov.hmcts.probate.services.pin.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.probate.services.pin.PinService;
import uk.gov.hmcts.probate.services.pin.exceptions.PhonePinException;
import uk.gov.hmcts.reform.probate.model.PhonePin;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@Tag(name = "Pin Service")
public class PinController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PinController.class);
    private static final String VALID_PHONENUMBER_CHARACTERS_REGEX = "(\\+)*[0-9]+";
    private static final String INVALID_PHONENUMBER_CHARACTERS_REGEX = "(?!^\\+)[^\\d]+";

    @Autowired
    private PinService pinService;

    @PostMapping(path = "/pin")
    public ResponseEntity<String> invitePost(
            @RequestHeader("Session-Id") final String sessionId,
            @Valid @RequestBody final PhonePin phonePin,
            BindingResult bindingResult)
            throws NotificationClientException, UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            throw new PhonePinException("PhonePin invalid");
        }
        return getStringResponseEntity(phonePin.getPhoneNumber(), sessionId, Boolean.FALSE);
    }

    @PostMapping(path = "/pin/bilingual")
    public ResponseEntity<String> inviteBilingualPost(
        @RequestHeader("Session-Id") final String sessionId,
        @Valid @RequestBody final PhonePin phonePin,
        BindingResult bindingResult)
        throws NotificationClientException, UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            throw new PhonePinException("PhonePin invalid");
        }
        return getStringResponseEntity(phonePin.getPhoneNumber(), sessionId, Boolean.TRUE);
    }

    private ResponseEntity<String> getStringResponseEntity(String phoneNumber, String sessionId, Boolean isBilingual)
        throws UnsupportedEncodingException, NotificationClientException {
        if (sessionId == null) {
            LOGGER.error("Session-Id request header not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        LOGGER.info("Processing session id " + sessionId);
        phoneNumber = URLDecoder.decode(phoneNumber, StandardCharsets.UTF_8.toString());
        phoneNumber = phoneNumber.replaceAll(INVALID_PHONENUMBER_CHARACTERS_REGEX, "");
        if (!phoneNumber.matches(VALID_PHONENUMBER_CHARACTERS_REGEX)) {
            LOGGER.error("Unable to validate phoneNumber parameter");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(pinService.generateAndSend(phoneNumber, isBilingual));
    }

}
