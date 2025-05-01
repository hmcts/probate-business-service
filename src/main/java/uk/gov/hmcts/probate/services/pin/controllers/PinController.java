package uk.gov.hmcts.probate.services.pin.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;
import uk.gov.hmcts.probate.services.pin.PinService;
import uk.gov.hmcts.probate.services.pin.exceptions.PhonePinException;
import uk.gov.hmcts.reform.probate.model.PhonePin;
import uk.gov.service.notify.NotificationClientException;

import java.nio.charset.StandardCharsets;

@RestController
@Tag(name = "Pin Service")
public class PinController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PinController.class);
    private static final String VALID_PHONENUMBER_CHARACTERS_REGEX = "(\\+)*[0-9]+";
    private static final String INVALID_PHONENUMBER_CHARACTERS_REGEX = "(?!^\\+)[^\\d]+";

    @Autowired
    private PinService pinService;

    //
    // The below should be removed ASAP but need to exist to provide a bridge during deployment
    //
    @RequestMapping(path = "/pin", method = RequestMethod.GET)
    public ResponseEntity<String> invite(@RequestParam String phoneNumber,
                                         @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException {
        LOGGER.warn("using unsafe GET for /pin");
        return getStringResponseEntity(phoneNumber, sessionId, Boolean.FALSE);
    }

    @PostMapping(path = "/pin")
    public ResponseEntity<String> invitePost(
            @RequestHeader("Session-Id") final String sessionId,
            @Valid @RequestBody final PhonePin phonePin,
            BindingResult bindingResult)
            throws NotificationClientException {
        if (bindingResult.hasErrors()) {
            throw new PhonePinException("PhonePin invalid");
        }
        return getStringResponseEntity(phonePin.getPhoneNumber(), sessionId, Boolean.FALSE);
    }

    //
    // The below should be removed ASAP but need to exist to provide a bridge during deployment
    //
    @RequestMapping(path = "/pin/bilingual", method = RequestMethod.GET)
    public ResponseEntity<String> inviteBilingual(@RequestParam String phoneNumber,
                                                  @RequestHeader("Session-Id") String sessionId)
        throws NotificationClientException {
        LOGGER.warn("using unsafe GET for /pin/bilingual");
        return getStringResponseEntity(phoneNumber, sessionId, Boolean.TRUE);
    }

    @PostMapping(path = "/pin/bilingual")
    public ResponseEntity<String> inviteBilingualPost(
        @RequestHeader("Session-Id") final String sessionId,
        @Valid @RequestBody final PhonePin phonePin,
        BindingResult bindingResult)
        throws NotificationClientException {
        if (bindingResult.hasErrors()) {
            throw new PhonePinException("PhonePin invalid");
        }
        return getStringResponseEntity(phonePin.getPhoneNumber(), sessionId, Boolean.TRUE);
    }

    private ResponseEntity<String> getStringResponseEntity(String phoneNumber, String sessionId, Boolean isBilingual)
        throws NotificationClientException {
        if (sessionId == null) {
            LOGGER.error("Session-Id request header not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        LOGGER.info("session: [{}] phoneNumber: [{}]", sessionId, phoneNumber);
        phoneNumber = UriUtils.decode(phoneNumber, StandardCharsets.UTF_8);
        LOGGER.info("decoded phoneNumber: [{}]", phoneNumber);
        phoneNumber = phoneNumber.replaceAll(INVALID_PHONENUMBER_CHARACTERS_REGEX, "");
        if (!phoneNumber.matches(VALID_PHONENUMBER_CHARACTERS_REGEX)) {
            LOGGER.error("Unable to validate phoneNumber parameter");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        LOGGER.info("final phoneNumber:[{}]", phoneNumber);
        return ResponseEntity.ok(pinService.generateAndSend(phoneNumber, isBilingual));
    }

    //
    // The below should be removed ASAP but need to exist to provide a bridge during deployment
    //
    @RequestMapping(path = "/pin/{phoneNumber}", method = RequestMethod.GET)
    public String inviteLegacy(@PathVariable String phoneNumber,
                               @RequestHeader("Session-Id") String sessionId) throws NotificationClientException {
        LOGGER.warn("using unsafe GET for /pin/{phoneNumber}");
        LOGGER.info("Processing session id " + sessionId);
        return pinService.generateAndSend(phoneNumber, Boolean.FALSE);
    }

}
