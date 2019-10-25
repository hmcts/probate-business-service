package uk.gov.hmcts.probate.services.invitation;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.invitation.controllers.InvitationController;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InviteControllerTest {

    @InjectMocks
    private InvitationController invitationController;

    @Mock
    private IdGeneratorService idGeneratorService;

    @Mock
    private InvitationService invitationService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    BindingResult mockBindingResult;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSendInvitationAndGenerateId() throws UnsupportedEncodingException, NotificationClientException {

        Invitation invitation = Invitation.builder().firstName("firstName").lastName("lastName").build();
        Map<String, String> data = new HashMap<>();
        data.put("firstName", invitation.getFirstName());
        data.put("lastName", invitation.getLastName());

        when(invitationService.decodeURL(invitation)).thenReturn(invitation);
        when(idGeneratorService.generate(data)).thenReturn("1233445");

        invitationController.invite(invitation, mockBindingResult, "");

        verify(idGeneratorService).generate(data);
        verify(invitationService).sendEmail("1233445", invitation);
    }

    @Test
    public void shouldReSendInvitation() throws UnsupportedEncodingException, NotificationClientException {

        Invitation invitation = Invitation.builder().firstName("firstName").lastName("lastName").build();

        invitationController.invite("1233445", invitation, mockBindingResult, "");

        verify(invitationService).sendEmail("1233445", invitation);
    }
}
