package uk.gov.hmcts.probate.services.notification.unit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.notification.NotificationService;
import uk.gov.hmcts.reform.probate.model.notification.ApplicationReceivedDetails;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    NotificationService notificationService;

    @MockBean
    NotificationClient notificationClient;

    @Autowired
    private TestUtils utils;

    public ObjectMapper objectMapper;
    public static final String ENCODED_APPLICATION_RECEIVED = "notification/success.json";
    public static final String EXPECTED_DECODING = "notification/expectedDecoding.json";

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
    }

    @Test
    public void shouldNotifyApplicationReceived() throws NotificationClientException {

        ApplicationReceivedDetails applicationReceivedDetails = ApplicationReceivedDetails.builder()
            .applicantEmail("applicantEmail")
            .applicantName("applicant name")
            .deceasedName("deceased name").bilingual(Boolean.FALSE).build();

        notificationService.sendApplicationRecievedEmail(applicationReceivedDetails);

        Mockito.verify(notificationClient).sendEmail(anyString(), anyString(), any(Map.class), anyString());
    }

    @Test
    public void testApplicationReceivedDetailsDecoding() throws Exception, UnsupportedEncodingException {

        ApplicationReceivedDetails encodedApplicationReceivedDetails = objectMapper.readValue(utils.getJSONFromFile(ENCODED_APPLICATION_RECEIVED), ApplicationReceivedDetails.class);
        ApplicationReceivedDetails expectedDecoding = objectMapper.readValue(utils.getJSONFromFile(EXPECTED_DECODING), ApplicationReceivedDetails.class);

        ApplicationReceivedDetails decodedArd = notificationService.decodeURL(encodedApplicationReceivedDetails);
        assertThat(decodedArd.getApplicantEmail(), equalTo(expectedDecoding.getApplicantEmail()));
        assertThat(decodedArd.getApplicantName(), equalTo(expectedDecoding.getApplicantName()));
        assertThat(decodedArd.getDeceasedName(), equalTo(expectedDecoding.getDeceasedName()));

    }
}
