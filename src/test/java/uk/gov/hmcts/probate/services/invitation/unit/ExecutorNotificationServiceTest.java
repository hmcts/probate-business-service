package uk.gov.hmcts.probate.services.invitation.unit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.invitation.ExecutorNotificationService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.ExecutorNotification;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ExecutorNotificationServiceTest {

    public static final String ENCODED_EXEC_NOTIFICATION = "invitation/executorNotification.json";
    public static final String EXPECTED_DECODING = "invitation/expectedDecodingExecutorNotification.json";
    public ObjectMapper objectMapper;
    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
    }

    @Test
    void testExecutorNotificationDecoding() throws Exception, UnsupportedEncodingException {
        ExecutorNotificationService executorNotificationService = new ExecutorNotificationService();

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
