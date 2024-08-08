package uk.gov.hmcts.probate.services.invitation.unit;

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
import uk.gov.hmcts.probate.services.invitation.ExecutorNotificationService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.ExecutorNotification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ExecutorNotificationServiceTest {

    public static final String ENCODED_EXEC_NOTIFICATION = "invitation/executorNotification.json";
    public static final String EXPECTED_DECODING = "invitation/expectedDecodingExecutorNotification.json";
    public ObjectMapper objectMapper;

    public static final String templateId = "templateId";
    public static final String bilingualTemplateId = "bilingualTemplateId";

    @Autowired
    private TestUtils utils;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private ExecutorNotificationService executorNotificationService;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
    }

    private ExecutorNotification setUpExecutorNotification() {
        ExecutorNotification executorNotification = ExecutorNotification.builder()
            .deceasedName("firstname lastname")
            .executorName("executor lastname")
            .applicantName("applicant lastname")
            .ccdReference("0123-4567-8901-2345")
            .deceasedDod("2016-12-12")
            .build();

        return executorNotification;
    }

    @Test
    void testSendEmail() throws NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        Boolean isBilingual = Boolean.FALSE;
        executorNotificationService.sendEmail(executorNotification, isBilingual);

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
    }
}
