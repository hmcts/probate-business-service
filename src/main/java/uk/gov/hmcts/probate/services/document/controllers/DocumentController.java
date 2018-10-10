package uk.gov.hmcts.probate.services.document.controllers;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.probate.services.document.validators.DocumentValidation;

import java.util.List;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/documents")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);
    private DocumentService documentService;
    private DocumentValidation documentValidation;

    @Autowired
    public DocumentController(DocumentService documentService, DocumentValidation documentValidation) {
        this.documentService = documentService;
        this.documentValidation = documentValidation;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public List<String> upload(
            @RequestHeader(value = "Authorization", required = false) String authorizationToken,
            @RequestHeader("user-id") String userID,
            @RequestParam("file") List<MultipartFile> files
    ) {
        boolean validFiles = files.stream()
                .allMatch(f -> documentValidation.isValid(f));

        if (files == null || files.isEmpty() || !validFiles || files.size() > 10) {
            LOGGER.error("Incorrect file format or too many files passed to the API endpoint.");
            throw new RuntimeException();
        }

        LOGGER.info("Uploading document");
        return documentService
                .upload(files, authorizationToken, userID)
                .getEmbedded()
                .getDocuments()
                .stream()
                .map(f -> f.links.self.href)
                .collect(Collectors.toList());
    }
}
