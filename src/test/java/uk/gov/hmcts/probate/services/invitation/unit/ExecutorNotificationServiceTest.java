package uk.gov.hmcts.probate.services.invitation.unit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.invitation.ExecutorNotificationService;
import uk.gov.hmcts.probate.services.invitation.NotifyPersonalisationEscapeService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.ExecutorNotification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExecutorNotificationServiceTest {

    public static final String ENCODED_EXEC_NOTIFICATION = "invitation/executorNotification.json";
    public static final String EXPECTED_DECODING = "invitation/expectedDecodingExecutorNotification.json";


    @Mock
    NotificationClient notificationClientMock;
    @Mock
    NotifyPersonalisationEscapeService notifyPersonalisationEscapeServiceMock;

    ExecutorNotificationService executorNotificationService;

    AutoCloseable closeableMocks;

    ObjectMapper objectMapper;
    TestUtils utils;

    @BeforeEach
    public void setUp() {
        utils = new TestUtils();

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        closeableMocks = MockitoAnnotations.openMocks(this);

        when(notifyPersonalisationEscapeServiceMock.escape(any()))
                .thenAnswer(i -> i.getArgument(0, String.class));

        executorNotificationService = new ExecutorNotificationService(
                notificationClientMock,
                notifyPersonalisationEscapeServiceMock);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeableMocks.close();
    }

    private ExecutorNotification setUpExecutorNotification() {
        return ExecutorNotification.builder()
            .email("email@email.com")
            .deceasedName("firstname lastname")
            .executorName("executor lastname")
            .applicantName("applicant lastname")
            .ccdReference("0123-4567-8901-2345")
            .deceasedDod("2016-12-12")
            .email("email@email.com")
            .build();
    }

    @Test
    void testSendEmail() throws NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        executorNotificationService.sendEmail(executorNotification, false);
        verify(notificationClientMock).sendEmail(isNull(),eq(executorNotification.getEmail()), any(), isNull());
    }

    @Test
    void testSendAllEmail() throws NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        executorNotificationService.sendAllSignedEmail(executorNotification, false);
        verify(notificationClientMock).sendEmail(isNull(),eq(executorNotification.getEmail()), any(), isNull());
    }

    @Test
    void testExecutorNotificationDecoding() throws Exception, UnsupportedEncodingException {
        ExecutorNotification encodedExecutorNotification =
            objectMapper.readValue(utils.getJsonFromFile(ENCODED_EXEC_NOTIFICATION), ExecutorNotification.class);
        ExecutorNotification expectedDecoding =
            objectMapper.readValue(utils.getJsonFromFile(EXPECTED_DECODING), ExecutorNotification.class);

        ExecutorNotification decodedExecutorNotification =
            executorNotificationService.decodeURL(encodedExecutorNotification);
        assertEquals(expectedDecoding.getExecutorName(), decodedExecutorNotification.getExecutorName());
        assertEquals(expectedDecoding.getApplicantName(), decodedExecutorNotification.getApplicantName());
        assertEquals(expectedDecoding.getDeceasedName(), decodedExecutorNotification.getDeceasedName());
        assertEquals(expectedDecoding.getDeceasedDod(), decodedExecutorNotification.getDeceasedDod());
        assertEquals(expectedDecoding.getCcdReference(), decodedExecutorNotification.getCcdReference());
        assertEquals(expectedDecoding.getEmail(), decodedExecutorNotification.getEmail());
    }
}
