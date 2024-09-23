package uk.gov.hmcts.probate.services.invitation;

import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.invitation.controllers.ExecutorNotificationController;
import uk.gov.hmcts.reform.probate.model.multiapplicant.ExecutorNotification;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExecutorNotificationControllerTest {

    @Mock
    BindingResult mockBindingResult;
    @InjectMocks
    private ExecutorNotificationController executorNotificationController;

    @Mock
    private ExecutorNotificationService executorNotificationService;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    public void openMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @NotNull
    private ExecutorNotification setUpExecutorNotification() throws UnsupportedEncodingException {
        ExecutorNotification executorNotification = ExecutorNotification.builder()
            .deceasedName("firstname lastname")
            .executorName("executor lastname")
            .applicantName("applicant lastname")
            .ccdReference("0123-4567-8901-2345")
            .deceasedDod("2016-12-12")
            .build();

        when(executorNotificationService.decodeURL(executorNotification)).thenReturn(executorNotification);
        return executorNotification;
    }

    @Test
    public void shouldSendSigned() throws UnsupportedEncodingException, NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        executorNotificationController.signed(executorNotification, mockBindingResult);
        verify(executorNotificationService).sendEmail(executorNotification, Boolean.FALSE);
    }

    @Test
    public void shouldSendSignedBilingual() throws UnsupportedEncodingException, NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        executorNotificationController.signedBilingual(executorNotification, mockBindingResult);
        verify(executorNotificationService).sendEmail(executorNotification, Boolean.TRUE);
    }

    @Test
    public void shouldSendSignedAll() throws UnsupportedEncodingException, NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        executorNotificationController.allSigned(executorNotification, mockBindingResult);
        verify(executorNotificationService).sendAllSignedEmail(executorNotification, Boolean.FALSE);
    }

    @Test
    void shouldSendSignedAllBilingual() throws UnsupportedEncodingException, NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        executorNotificationController.allSignedBilingual(executorNotification, mockBindingResult);
        verify(executorNotificationService).sendAllSignedEmail(executorNotification, Boolean.TRUE);
    }

    @Test
    void shouldThrowIfFailSigned() throws UnsupportedEncodingException, NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        doThrow(new NotificationClientException("error"))
            .when(executorNotificationService).sendEmail(executorNotification, Boolean.FALSE);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,
            executorNotificationController.signed(executorNotification, mockBindingResult).getStatusCode());
    }

    @Test
    void shouldThrowIfFailSignedBilingual() throws UnsupportedEncodingException, NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        doThrow(new NotificationClientException("error"))
            .when(executorNotificationService).sendEmail(executorNotification, Boolean.TRUE);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,
            executorNotificationController.signedBilingual(executorNotification,
                mockBindingResult).getStatusCode());
    }

    @Test
    void shouldThrowIfFailAllSigned() throws UnsupportedEncodingException, NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        doThrow(new NotificationClientException("error"))
            .when(executorNotificationService).sendAllSignedEmail(executorNotification, Boolean.FALSE);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, executorNotificationController.allSigned(
            executorNotification, mockBindingResult).getStatusCode());
    }

    @Test
    void shouldThrowIfFailAllSignedBilingual() throws UnsupportedEncodingException, NotificationClientException {
        ExecutorNotification executorNotification = setUpExecutorNotification();
        doThrow(new NotificationClientException("error"))
            .when(executorNotificationService).sendAllSignedEmail(executorNotification, Boolean.TRUE);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY,
            executorNotificationController.allSignedBilingual(executorNotification,
                mockBindingResult).getStatusCode());
    }
}
