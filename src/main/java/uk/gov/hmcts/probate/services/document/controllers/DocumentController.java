package uk.gov.hmcts.probate.services.document.controllers;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.probate.services.document.validators.DocumentValidation;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/document")
@Tag(name = "Document Service")
public class DocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);
    private DocumentService documentService;
    private DocumentValidation documentValidation;
    private final AuthTokenGenerator authTokenGenerator;

    @Autowired
    public DocumentController(DocumentService documentService,
                              DocumentValidation documentValidation,
                              AuthTokenGenerator authTokenGenerator
    ) {
        this.documentService = documentService;
        this.documentValidation = documentValidation;
        this.authTokenGenerator = authTokenGenerator;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public List<String> upload(
            @RequestHeader(value = "Authorization", required = false) String authorizationToken,
            @RequestHeader("user-id") String userID,
            @RequestPart("file") List<MultipartFile> files
    ) {
        List<String> result = new ArrayList<>();
        if (files == null || files.isEmpty()) {
            LOGGER.error("Zero files received by the API endpoint.");
            result.add("Error: no files passed");
            return result;
        }

        if (files.size() > 10) {
            LOGGER.error("Too many files passed to the API endpoint");
            result.add("Error: too many files");
            return result;
        }

        List<String> invalidFiles = files.stream()
                .filter(f -> !documentValidation.isValid(f))
                .map(f -> "Error: invalid file type")
                .collect(Collectors.toList());

        boolean noValidFilesReceived = files.stream()
                .noneMatch(f -> documentValidation.isValid(f));

        if (noValidFilesReceived) {
            LOGGER.error("No valid file types passed to the API endpoint.");
            return files.stream()
                    .map(f -> "Error: invalid file type")
                    .collect(Collectors.toList());
        }

        files = files.stream()
                .filter(f -> documentValidation.isValid(f))
                .collect(Collectors.toList());

        LOGGER.info("Uploading document");
        result = documentService
                .upload(authorizationToken, authTokenGenerator.generate(), userID, files)
                .getEmbedded()
                .getDocuments()
                .stream()
                .map(f -> f.links.self.href)
                .collect(Collectors.toList());
        result.addAll(invalidFiles);
        return result;
    }

    @DeleteMapping(value = "/delete/{documentId}")
    @ResponseBody
    public ResponseEntity<String> delete(
            @RequestHeader("user-id") String userID,
            @PathVariable("documentId") String documentId
    ) {
        return documentService.delete(userID, documentId);
    }
}
