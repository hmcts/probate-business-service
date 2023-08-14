package uk.gov.hmcts.probate.services.businessdocuments.controllers;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import org.json.JSONException;
import org.junit.Ignore;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.invitation.InvitationService;
import uk.gov.hmcts.probate.services.pin.PinService;
import uk.gov.service.notify.NotificationClientException;

import java.io.IOException;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Provider("probate_business_service_invite")
@RunWith(SpringRestPactRunner.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
    "server.port=8123", "spring.application.name=PACT_TEST",
    "services.pdf.service.url=http://localhost:989"
})
@Ignore
public class InvitationControllerProviderTest extends ControllerProviderTest {


    @TestTarget
    @SuppressWarnings(value = "VisibilityModifier")
    public final Target target = new HttpTarget("http", "localhost", 8123, "/");

    @MockBean
    private InvitationService invitationService;
    @MockBean(name = "identityGeneratorService")
    private IdGeneratorService idGeneratorService;
    @MockBean
    private PinService pinService;

    @State("business service sends invitation")
    public void toSendInvitation() throws IOException, JSONException, NotificationClientException {
        String inviteId = "123345453";
        when(idGeneratorService.generate(any(HashMap.class)))
            .thenReturn(inviteId);
    }


    @State("business service resends invitation")
    public void toResendInvitation() throws IOException, JSONException, NotificationClientException {

    }

    @State("business service generates pin number")
    public void toGeneratePinNumber() throws IOException, JSONException, NotificationClientException {
        String pin = "123345453";
        when(pinService.generateAndSend(anyString(), anyBoolean()))
            .thenReturn(pin);
    }

}
