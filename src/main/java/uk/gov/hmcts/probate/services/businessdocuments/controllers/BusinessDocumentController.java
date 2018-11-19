package uk.gov.hmcts.probate.services.businessdocuments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.probate.services.businessdocuments.services.PDFGenerationService;
import uk.gov.hmcts.probate.services.businessdocuments.model.CheckAnswersSummary;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/businessDocument")
@RestController
public class BusinessDocumentController {

    private final PDFGenerationService pdfDocumentGenerationService;


    @PostMapping(path = "/generateCheckAnswersSummaryPDF", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<byte[]> generateCheckAnswersSummaryPDF(@Valid @RequestBody CheckAnswersSummary checkAnswersSummary, @RequestHeader("ServiceAuthorization") String authorization) {
        log.info("call to generateCheckAnswersSummaryPDF()");

        byte[] bytes = pdfDocumentGenerationService.generatePdf(checkAnswersSummary, DocumentType.CHECK_ANSWERS_SUMMARY);

        return new ResponseEntity<> (bytes, HttpStatus.OK);
    }


}
