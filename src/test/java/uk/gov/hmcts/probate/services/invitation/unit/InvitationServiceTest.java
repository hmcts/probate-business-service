package uk.gov.hmcts.probate.services.invitation.unit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.invitation.InvitationService;
import uk.gov.hmcts.probate.services.invitation.NotifyPersonalisationEscapeService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.ExecutorNotification;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InvitationServiceTest {

    public static final String ENCODED_INVITATION = "invitation/success.json";
    public static final String EXPECTED_DECODING = "invitation/expectedDecoding.json";

    private ObjectMapper objectMapper;
    private TestUtils utils;

    @Mock
    NotificationClient notificationClientMock;
    @Mock
    NotifyPersonalisationEscapeService notifyPersonalisationEscapeServiceMock;

    InvitationService invitationService;

    AutoCloseable closeableMocks;

    @BeforeEach
    public void setUp() {
        utils = new TestUtils();

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        closeableMocks = MockitoAnnotations.openMocks(this);

        invitationService = new InvitationService(
            notificationClientMock,
            notifyPersonalisationEscapeServiceMock);

        // i'd prefer to pass this in as part of construction but...
        ReflectionTestUtils.setField(invitationService, "inviteLink", "");
    }

    @AfterEach
    void tearDown() {
        try {
            closeableMocks.close();
        } catch (Exception e) { // nothing to do
        }
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
    void testSendIntestacyEmail() throws NotificationClientException {
        Invitation invitation = Invitation.builder().firstName("firstName").lastName("lastName").build();
        invitationService.sendIntestacyEmail("templateid",invitation, false);
        verify(notificationClientMock).sendEmail(isNull(),any(), any(), any());
    }

    @Test
    void testInvitationDecoding() throws Exception, UnsupportedEncodingException {
        Invitation encodedInvitation =
            objectMapper.readValue(utils.getJsonFromFile(ENCODED_INVITATION), Invitation.class);
        Invitation expectedDecoding =
            objectMapper.readValue(utils.getJsonFromFile(EXPECTED_DECODING), Invitation.class);

        Invitation decodedInvitation = invitationService.decodeURL(encodedInvitation);
        assertEquals(expectedDecoding.getFirstName(), decodedInvitation.getFirstName());
        assertEquals(expectedDecoding.getLastName(), decodedInvitation.getLastName());
        assertEquals(expectedDecoding.getExecutorName(), decodedInvitation.getExecutorName());
        assertEquals(expectedDecoding.getLeadExecutorName(), decodedInvitation.getLeadExecutorName());
    }

    @Test
    void verifyParametersEscaped() throws NotificationClientException {
        final String linkId = "linkId";
        final String firstName = "firstName";
        final String lastName = "lastName";
        final String leadName = "leadName";
        final String execName = "execName";
        final String email = "email";

        final Invitation invitation = new Invitation();
        invitation.setFirstName(firstName);
        invitation.setLastName(lastName);
        invitation.setLeadExecutorName(leadName);
        invitation.setExecutorName(execName);
        invitation.setEmail(email);

        when(notifyPersonalisationEscapeServiceMock.escape(any()))
                .thenAnswer(a -> a.getArgument(0));

        invitationService.sendEmail(linkId, invitation, true);

        verify(notifyPersonalisationEscapeServiceMock, times(4)).escape(any());

        ArgumentCaptor<Map<String, String>> persCaptor = ArgumentCaptor.forClass(Map.class);
        verify(notificationClientMock).sendEmail(any(), eq(email), persCaptor.capture(), eq(linkId));

        final Map<String, String> personalisation = persCaptor.getValue();
        assertAll(
            () -> assertThat(personalisation.size(), equalTo(5)),
            () -> assertThat(personalisation, hasEntry("leadExecutorName", leadName)),
            () -> assertThat(personalisation, hasEntry("executorName", execName)),
            () -> assertThat(personalisation, hasEntry("deceasedFirstName", firstName)),
            () -> assertThat(personalisation, hasEntry("deceasedLastName", lastName)),
            () -> assertThat(personalisation, hasEntry("link", linkId)));
    }

}
