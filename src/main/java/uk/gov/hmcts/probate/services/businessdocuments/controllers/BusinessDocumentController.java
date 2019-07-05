package uk.gov.hmcts.probate.services.businessdocuments.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.probate.services.businessdocuments.model.BulkScanCoverSheet;
import uk.gov.hmcts.probate.services.businessdocuments.model.CheckAnswersSummary;
import uk.gov.hmcts.probate.services.businessdocuments.model.DocumentType;
import uk.gov.hmcts.probate.services.businessdocuments.model.LegalDeclaration;
import uk.gov.hmcts.probate.services.businessdocuments.services.PDFGenerationService;

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

    @PostMapping(path = "/generateLegalDeclarationPDF", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<byte[]> generateLegalDeclarationPDF(@Valid @RequestBody LegalDeclaration legalDeclaration, @RequestHeader("ServiceAuthorization") String authorization) {
        log.info("call to generateLegalDeclarationPDF()");

        byte[] bytes = pdfDocumentGenerationService.generatePdf(legalDeclaration, DocumentType.LEGAL_DECLARATION);

        return new ResponseEntity<> (bytes, HttpStatus.OK);
    }

    @PostMapping(path = "/generateBulkScanCoverSheetPDF", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<byte[]> generateBulkScanCoverSheetPDF(@Valid @RequestBody BulkScanCoverSheet coverSheet, @RequestHeader("ServiceAuthorization") String authorization) {
        log.info("call to generateBulkScanCoverSheetPDF()");

        byte[] bytes = pdfDocumentGenerationService.generatePdf(coverSheet, DocumentType.BULK_SCAN_COVER_SHEET);

        return new ResponseEntity<> (bytes, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex) {
        log.error("BusinessDocumentController failed to execute: {}", ex.getLocalizedMessage(), ex);
        return new ResponseEntity("Error while generating PDF document.", HttpStatus.BAD_REQUEST);
    }
}
