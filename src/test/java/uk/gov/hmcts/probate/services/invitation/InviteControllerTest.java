package uk.gov.hmcts.probate.services.invitation;

import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.invitation.controllers.InvitationController;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InviteControllerTest {

    @Mock
    BindingResult mockBindingResult;
    @InjectMocks
    private InvitationController invitationController;
    @Mock
    private IdGeneratorService idGeneratorService;
    @Mock
    private InvitationService invitationService;

    @BeforeEach
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSendInvitationAndGenerateId() throws NotificationClientException {

        Invitation invitation = setUpInvitationMock();

        invitationController.invite(invitation, mockBindingResult, "");

        verify(invitationService).sendEmail("1233445", invitation, Boolean.FALSE);
    }

    @Test
    void shouldReSendInvitation() throws NotificationClientException {

        Invitation invitation = Invitation.builder().firstName("firstName").lastName("lastName").build();

        invitationController.invite("1233445", invitation, mockBindingResult, "");

        verify(invitationService).sendEmail("1233445", invitation, Boolean.FALSE);
    }

    @Test
    void shouldSendInvitation() throws NotificationClientException {

        Invitation invitation = setUpInvitationMock();

        invitationController.invite(invitation, mockBindingResult, "");

        verify(invitationService).sendEmail("1233445", invitation, Boolean.FALSE);
    }

    @NotNull
    private Invitation setUpInvitationMock() {
        Invitation invitation = Invitation.builder().firstName("firstName").lastName("lastName").build();
        Map<String, String> data = new HashMap<>();
        data.put("firstName", invitation.getFirstName());
        data.put("lastName", invitation.getLastName());
        when(idGeneratorService.generate(data)).thenReturn("1233445");
        return invitation;
    }

    @Test
    void shouldSendInvitationWithId() throws NotificationClientException {

        Invitation invitation = setUpInvitationMock();
        invitationController.invite(invitation, mockBindingResult, "");

        verify(invitationService).sendEmail("1233445", invitation, Boolean.FALSE);
    }

    @Test
    void shouldSendBilingualInvitation() throws NotificationClientException {

        Invitation invitation = Invitation.builder().firstName("firstName").lastName("lastName").build();

        invitationController.inviteBilingual("1233445", invitation, mockBindingResult, "");

        verify(invitationService).sendEmail("1233445", invitation, Boolean.TRUE);
    }

    @Test
    void shouldSendBilingualInvitationWithId() throws NotificationClientException {

        Invitation invitation = setUpInvitationMock();

        invitationController.inviteBilingual("1233445", invitation, mockBindingResult, "");

        verify(invitationService).sendEmail("1233445", invitation, Boolean.TRUE);
    }

    @Test
    void shouldSendBilingualInviteGenerateId() throws NotificationClientException {

        Invitation invitation = setUpInvitationMock();

        invitationController.inviteBilingual(invitation, mockBindingResult, "");

        verify(invitationService).sendEmail("1233445", invitation, Boolean.TRUE);
    }
}
