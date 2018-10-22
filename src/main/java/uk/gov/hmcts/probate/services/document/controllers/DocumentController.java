package uk.gov.hmcts.probate.services.document.controllers;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.probate.services.document.exception.DocumentDeletionException;
import uk.gov.hmcts.probate.services.document.exception.DocumentsMissingException;
import uk.gov.hmcts.probate.services.document.exception.UnSupportedDocumentTypeException;
import uk.gov.hmcts.probate.services.document.utils.DocumentUtils;
import uk.gov.hmcts.probate.services.document.validators.DocumentValidation;
import uk.gov.hmcts.probate.services.persistence.PersistenceClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/document")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);
    private DocumentService documentService;
    private DocumentValidation documentValidation;
    private PersistenceClient persistenceClient;
    private DocumentUtils documentUtils;

    @Autowired
    public DocumentController(DocumentService documentService, DocumentValidation documentValidation,
                                PersistenceClient persistenceClient, DocumentUtils documentUtils) {
        this.documentService = documentService;
        this.documentValidation = documentValidation;
        this.persistenceClient = persistenceClient;
        this.documentUtils = documentUtils;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @ResponseBody
    public Map<String, String> upload(
            @RequestHeader(value = "Authorization", required = false) String authorizationToken,
            @RequestHeader("user-id") String userID,
            @RequestParam("file") List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            LOGGER.error("Incorrect file format or too many files passed to the API endpoint.");
            throw new DocumentsMissingException();
        }

        boolean validFiles = files.stream()
                .allMatch(f -> documentValidation.isValid(f));

        if (!validFiles || files.size() > 10) {
            LOGGER.error("Invalid file type or quantity passed to the API endpoint.");
            throw new UnSupportedDocumentTypeException();
        }

        LOGGER.info("Uploading document");
        Map<String, String> documentData =
                documentService
                    .upload(files, authorizationToken, userID)
                    .getEmbedded()
                    .getDocuments()
                    .stream()
                    .collect(Collectors.toMap(f -> f.originalDocumentName, f -> f.links.self.href));

        persistenceClient.updateFormData(userID, documentUtils.populateDocumentObject(documentData));
        return documentData;
    }

    @DeleteMapping(value = "/delete/{documentId}")
    @ResponseBody
    public ResponseEntity<?> delete(
            @PathVariable("documentId") String documentId
    ) {
        ResponseEntity response =  documentService.delete(documentId);

        if (response.getStatusCode().is4xxClientError()) {
            LOGGER.error("An error occurred whilst trying to delete document. Check the document is valid or try again later.");
            throw new DocumentDeletionException();
        }

        return response;
    }
}