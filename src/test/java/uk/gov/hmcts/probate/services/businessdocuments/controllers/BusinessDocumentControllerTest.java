package uk.gov.hmcts.probate.services.businessdocuments.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.businessdocuments.services.PDFGenerationService;
import uk.gov.hmcts.reform.probate.model.documents.BulkScanCoverSheet;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;
import uk.gov.hmcts.reform.probate.model.documents.Declaration;
import uk.gov.hmcts.reform.probate.model.documents.LegalDeclaration;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class BusinessDocumentControllerTest {

    BusinessDocumentController businessDocumentController;

    @Mock
    PDFGenerationService pdfGenerationService;

    CheckAnswersSummary checkAnswersSummary;

    LegalDeclaration legalDeclaration;

    BulkScanCoverSheet coverSheet;

    Declaration lastDeclaration;

    @BeforeEach
    public void setUp() {
        businessDocumentController = new BusinessDocumentController(pdfGenerationService);
        checkAnswersSummary = new CheckAnswersSummary();
        legalDeclaration = new LegalDeclaration();
        lastDeclaration = new Declaration();
        legalDeclaration.setDeclarations(Arrays.asList(new Declaration(), lastDeclaration));
        coverSheet = new BulkScanCoverSheet();
    }

    @Test
    public void shouldGenerateACheckAnswersSummaryPDF() {
        ResponseEntity<byte[]> result =
            businessDocumentController.generateCheckAnswersSummaryPDF(checkAnswersSummary, "authorisation");
    }

    @Test
    public void shouldGenerateALegalDeclarationPDF() {
        ResponseEntity<byte[]> result =
            businessDocumentController.generateLegalDeclarationPDF(legalDeclaration, "authorisation");

    }

    @Test
    public void shouldGetLastDeclaration() {
        Optional<Declaration> result = businessDocumentController.getLastDeclaration(legalDeclaration);
        result.ifPresent(declaration -> assertEquals(lastDeclaration, declaration));
    }

    @Test
    public void shouldGenerateABulkScanCoverSheetPDF() {
        ResponseEntity<byte[]> result =
            businessDocumentController.generateBulkScanCoverSheetPDF(coverSheet, "authorisation");
    }
}
