package uk.gov.hmcts.probate.services.businessdocuments.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.services.businessdocuments.services.DocumentNotificationService;
import uk.gov.hmcts.reform.probate.model.documents.DocumentNotification;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentNotificationControllerTest {

    @Mock
    BindingResult mockBindingResult;
    @InjectMocks
    private DocumentNotificationController documentNotificationController;

    @Mock
    private DocumentNotificationService documentNotificationService;
    private DocumentNotification documentNotification;

    @BeforeEach
    public void setUp() throws UnsupportedEncodingException {
        MockitoAnnotations.openMocks(this);
        documentNotification = DocumentNotification.builder()
            .ccdReference("0123-4567-8901-2345")
            .applicantName("applicant lastname")
            .deceasedName("firstname lastname")
            .email("email@email.com")
            .deceasedDod("2016-12-12")
            .citizenResponse("response")
            .expectedResponseDate("12 December 2016")
            .fileName(List.of("document.pdf")).build();
        when(documentNotificationService.decodeURL(documentNotification)).thenReturn(documentNotification);
    }

    @Test
    void shouldSendEmailForDocumentUploaded() {
        documentNotificationController.documentUpload(documentNotification, mockBindingResult);
        verify(documentNotificationService).sendEmail(documentNotification, Boolean.FALSE);
    }

    @Test
     void shouldSendSignedBilingual() {
        documentNotificationController.documentUploadBilingual(documentNotification, mockBindingResult);
        verify(documentNotificationService).sendEmail(documentNotification, Boolean.TRUE);
    }

    @Test
    void shouldSendSignedAll() {
        documentNotificationController.documentUploadIssue(documentNotification, mockBindingResult);
        verify(documentNotificationService).sendUploadIssueEmail(documentNotification, Boolean.FALSE);
    }

    @Test
    void shouldSendSignedAllBilingual() {
        documentNotificationController.documentUploadIssueBilingual(documentNotification, mockBindingResult);
        verify(documentNotificationService).sendUploadIssueEmail(documentNotification, Boolean.TRUE);
    }
}
