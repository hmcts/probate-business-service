package uk.gov.hmcts.probate.services.notification.unit;

import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.invitation.InvitationService;
import uk.gov.hmcts.probate.services.invitation.controllers.InvitationController;
import uk.gov.hmcts.probate.services.notification.NotificationService;
import uk.gov.hmcts.probate.services.notification.controllers.NotificationController;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.hmcts.reform.probate.model.notification.ApplicationReceivedDetails;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    BindingResult mockBindingResult;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSendApplicationReceivedNotification() throws UnsupportedEncodingException, NotificationClientException {

        ApplicationReceivedDetails applicationReceivedDetailsMock = setUpApplicationReceivedDetailsMock();

        notificationController.sendApplicationReceivedNotification(applicationReceivedDetailsMock, mockBindingResult);

        verify(notificationService).sendApplicationRecievedEmail(applicationReceivedDetailsMock);
    }


    @NotNull
    private ApplicationReceivedDetails setUpApplicationReceivedDetailsMock() throws UnsupportedEncodingException {
        ApplicationReceivedDetails applicationReceivedDetails = ApplicationReceivedDetails.builder()
            .applicantEmail("applicantEmail")
            .applicantName("applicantName")
            .deceasedName("deceasedName")
            .bilingual(Boolean.FALSE)
            .build();

        when(notificationService.decodeURL(applicationReceivedDetails)).thenReturn(applicationReceivedDetails);
        return applicationReceivedDetails;
    }

}
