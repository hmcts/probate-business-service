package uk.gov.hmcts.probate.services.pin.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.hmcts.probate.services.pin.PinService;
import uk.gov.service.notify.NotificationClientException;

@RestController
public class PinController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PinController.class);
    private static final String VALID_PHONENUMBER_CHARACTERS_REGEX = "(\\+)*[0-9]+";
    private static final String INVALID_PHONENUMBER_CHARACTERS_REGEX = "[ \\(\\)\\[\\]-]";
    
    @Autowired
    private PinService pinService;

    @RequestMapping(path = "/pin", method = RequestMethod.GET)
    public ResponseEntity<String> invite(@RequestParam String phoneNumber,
                         @RequestHeader("Session-Id") String sessionId) throws NotificationClientException, UnsupportedEncodingException {
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
		return ResponseEntity.ok(pinService.generateAndSend(phoneNumber));
    }

    @RequestMapping(path = "/pin/{phoneNumber}", method = RequestMethod.GET)
    public String inviteLegacy(@PathVariable String phoneNumber,
                           @RequestHeader("Session-Id") String sessionId) throws NotificationClientException {     
	    LOGGER.info("Processing session id " + sessionId);
        return pinService.generateAndSend(phoneNumber);
    }
    
}
