package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.reform.probate.model.documents.DocumentNotification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DocumentNotificationServiceTest {

    public static final String ENCODED_EXEC_NOTIFICATION = "businessdocuments/documentNotification.json";
    public static final String EXPECTED_DECODING = "businessdocuments/expectedDecodingDocumentNotification.json";
    public ObjectMapper objectMapper;

    @Autowired
    private TestUtils utils;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private DocumentNotificationService documentNotificationService;
    private DocumentNotification documentNotification;

    @BeforeEach
     void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        documentNotification = DocumentNotification.builder()
            .ccdReference("0123-4567-8901-2345")
            .applicantName("applicant lastname")
            .deceasedName("firstname lastname")
            .email("email@email.com")
            .deceasedDod("2016-12-12")
            .citizenResponse("response")
            .citizenResponseSubmittedDate("2016-12-12")
            .fileName(List.of("document.pdf")).build();
    }

    @Test
    void testSendEmailForDocumentUploaded() throws NotificationClientException {
        documentNotificationService.sendEmail(documentNotification, false);
        verify(notificationClient).sendEmail(isNull(),eq(documentNotification.getEmail()), any(), isNull());
    }

    @Test
    void testSendEmailForDocumentUploadIssue() throws NotificationClientException, UnsupportedEncodingException {
        documentNotificationService.sendUploadIssueEmail(documentNotification, false);
        verify(notificationClient).sendEmail(isNull(),eq(documentNotification.getEmail()), any(), isNull());
    }

    @Test
    void shouldThrowExceptionWhenSendingNotification() throws NotificationClientException {
        doThrow(new NotificationClientException("error"))
            .when(notificationClient).sendEmail(isNull(), eq(documentNotification.getEmail()), any(), isNull());
        documentNotificationService.sendEmail(documentNotification, false);
    }

    @Test
    void testDocumentNotificationDecoding() throws Exception {
        DocumentNotification encodedDocumentNotification =
            objectMapper.readValue(utils.getJsonFromFile(ENCODED_EXEC_NOTIFICATION), DocumentNotification.class);
        DocumentNotification expectedDecoding =
            objectMapper.readValue(utils.getJsonFromFile(EXPECTED_DECODING), DocumentNotification.class);

        DocumentNotification decodedDocumentNotification =
            documentNotificationService.decodeURL(encodedDocumentNotification);

        assertEquals(expectedDecoding.getApplicantName(), decodedDocumentNotification.getApplicantName());
        assertEquals(expectedDecoding.getDeceasedName(), decodedDocumentNotification.getDeceasedName());
        assertEquals(expectedDecoding.getDeceasedDod(), decodedDocumentNotification.getDeceasedDod());
        assertEquals(expectedDecoding.getCcdReference(), decodedDocumentNotification.getCcdReference());
        assertEquals(expectedDecoding.getEmail(), decodedDocumentNotification.getEmail());
        assertEquals(expectedDecoding.getCitizenResponse(), decodedDocumentNotification.getCitizenResponse());
        assertEquals(expectedDecoding.getFileName(), decodedDocumentNotification.getFileName());
        assertEquals(expectedDecoding.getCitizenResponseSubmittedDate(),
            decodedDocumentNotification.getCitizenResponseSubmittedDate());
    }
}
