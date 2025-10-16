package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.invitation.NotifyPersonalisationEscapeService;
import uk.gov.hmcts.reform.probate.model.documents.DocumentNotification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentNotificationServiceTest {

    public static final String ENCODED_EXEC_NOTIFICATION = "businessdocuments/documentNotification.json";
    public static final String EXPECTED_DECODING = "businessdocuments/expectedDecodingDocumentNotification.json";

    @Mock
    NotificationClient notificationClientMock;
    @Mock
    NotifyPersonalisationEscapeService notifyPersonalisationEscapeServiceMock;

    DocumentNotificationService documentNotificationService;

    AutoCloseable closeableMocks;


    DocumentNotification documentNotification;
    ObjectMapper objectMapper;
    TestUtils utils;


    @BeforeEach
    void setUp() {
        utils = new TestUtils();

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        documentNotification = DocumentNotification.builder()
            .ccdReference("0123-4567-8901-2345")
            .applicantName("applicant lastname")
            .deceasedName("firstname lastname")
            .email("email@email.com")
            .deceasedDod("2016-12-12")
            .citizenResponse("response")
            .expectedResponseDate("2016-12-12")
            .fileName(List.of("document.pdf")).build();

        closeableMocks = MockitoAnnotations.openMocks(this);

        when(notifyPersonalisationEscapeServiceMock.escape(any()))
            .thenAnswer(i -> i.getArgument(0, String.class));

        documentNotificationService = new DocumentNotificationService(
                notificationClientMock,
                notifyPersonalisationEscapeServiceMock);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeableMocks.close();
    }

    @Test
    void testSendEmailForDocumentUploaded() throws NotificationClientException {
        documentNotificationService.sendEmail(documentNotification, false);
        verify(notificationClientMock).sendEmail(isNull(),eq(documentNotification.getEmail()), any(), isNull());
    }

    @Test
    void testSendEmailForDocumentUploadIssue() throws NotificationClientException {
        documentNotificationService.sendUploadIssueEmail(documentNotification, false);
        verify(notificationClientMock).sendEmail(isNull(),eq(documentNotification.getEmail()), any(), isNull());
    }

    @Test
    void shouldThrowExceptionWhenSendingNotification() throws NotificationClientException {
        doThrow(new NotificationClientException("error"))
            .when(notificationClientMock).sendEmail(isNull(), eq(documentNotification.getEmail()), any(), isNull());
        documentNotificationService.sendEmail(documentNotification, false);
    }

    @Test
    void shouldThrowExceptionWhenSendingNotificationForUploadIssue() throws NotificationClientException {
        doThrow(new NotificationClientException("error"))
            .when(notificationClientMock).sendEmail(isNull(), eq(documentNotification.getEmail()), any(), isNull());
        documentNotificationService.sendUploadIssueEmail(documentNotification, false);
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
        assertEquals(expectedDecoding.getExpectedResponseDate(),
            decodedDocumentNotification.getExpectedResponseDate());
    }



    @Test
    void convertDateShouldReturnFormattedDateWithStSuffix() {
        String result = documentNotificationService.convertDate("2023-01-01");
        assertEquals("1st January 2023", result);
    }

    @Test
    void convertDateShouldReturnFormattedDateWithNdSuffix() {
        String result = documentNotificationService.convertDate("2023-02-02");
        assertEquals("2nd February 2023", result);
    }

    @Test
    void convertDateShouldReturnFormattedDateWithRdSuffix() {
        String result = documentNotificationService.convertDate("2023-03-03");
        assertEquals("3rd March 2023", result);
    }

    @Test
    void convertDateShouldReturnFormattedDateWithThSuffix() {
        String result = documentNotificationService.convertDate("2023-04-04");
        assertEquals("4th April 2023", result);
    }

    @Test
    void convertDateShouldReturnNullForNullInput() {
        String result = documentNotificationService.convertDate(null);
        assertNull(result);
    }

    @Test
    void convertDateShouldReturnNullForEmptyStringInput() {
        String result = documentNotificationService.convertDate("");
        assertNull(result);
    }

    @Test
    void convertDateShouldReturnNullForInvalidDateFormat() {
        String result = documentNotificationService.convertDate("invalid-date");
        assertNull(result);
    }
}
