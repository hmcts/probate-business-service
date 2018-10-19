package uk.gov.hmcts.probate.services.document.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.probate.services.document.clients.PersistenceClient;
import uk.gov.hmcts.probate.services.document.exception.DocumentsMissingException;
import uk.gov.hmcts.probate.services.document.exception.UnSupportedDocumentTypeException;
import uk.gov.hmcts.probate.services.document.utils.DocumentUtils;
import uk.gov.hmcts.probate.services.document.validators.DocumentValidation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/documents")
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
}

//      Required return format to both frontend and Persistence Patch:
//      documents: {
//        uploads: [
//          {
//              filename: 'will.pdf',
//              url: ''
//          },
//          {
//              filename: 'death-certificate.pdf',
//              url: ''
//          },
//          {
//              filename: 'death-certificate.pdf',
//              url: ''
//          }
//        ]
//      }
