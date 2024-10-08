package uk.gov.hmcts.probate.services.businessdocuments.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import uk.gov.hmcts.probate.services.businessdocuments.services.DocumentNotificationService;
import uk.gov.hmcts.reform.probate.model.documents.DocumentNotification;

@RestController
@Tag(name = "Document Notification Service")
public class DocumentNotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentNotificationController.class);
    private static final String LOGGING_MSG = "Processing document notification : {}";

    @Autowired
    private DocumentNotificationService documentNotificationService;

    @PostMapping(path = "/document-upload-notification/bilingual", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> documentUploadBilingual(@Valid @RequestBody
                                                            DocumentNotification encodedDocumentNotification,
                                                BindingResult bindingResult) {
        LOGGER.info(LOGGING_MSG, bindingResult.getFieldErrors());
        documentNotificationService.sendEmail(encodedDocumentNotification, Boolean.TRUE);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/document-upload-notification", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> documentUpload(@Valid @RequestBody DocumentNotification encodedDocumentNotification,
                         BindingResult bindingResult) {
        LOGGER.info(LOGGING_MSG, bindingResult.getFieldErrors());
        documentNotificationService.sendEmail(encodedDocumentNotification, Boolean.FALSE);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/document-upload-issue-notification/bilingual", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> documentUploadIssueBilingual(@Valid @RequestBody
                                                                 DocumentNotification encodedDocumentNotification,
                                                   BindingResult bindingResult) {
        LOGGER.info(LOGGING_MSG, bindingResult.getFieldErrors());
        documentNotificationService.sendUploadIssueEmail(encodedDocumentNotification, Boolean.TRUE);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/document-upload-issue-notification", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> documentUploadIssue(@Valid @RequestBody DocumentNotification
                                                            encodedDocumentNotification, BindingResult bindingResult) {
        LOGGER.info(LOGGING_MSG, bindingResult.getFieldErrors());
        documentNotificationService.sendUploadIssueEmail(encodedDocumentNotification, Boolean.FALSE);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
