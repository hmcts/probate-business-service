package uk.gov.hmcts.probate.services.invitation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvitationServiceTest {

    ObjectMapper mapper;

    JsonNode formData, inviteList;

    @Mock
    PersistenceClient persistenceClient;

    @InjectMocks
    InvitationService invitationService;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
    }


    @Test
    public void checkAllInvitedAgreedIsApplyingFlagNotFound() throws IOException {

        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[{\"id\":\"invite-id-1\",\"agreed\":true}]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));
    }

    @Test
    public void checkAllInvitedAgreedIsApplyingFalse() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":false,\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[{\"id\":\"invite-id-1\",\"agreed\":true}]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedIsApplyingTrueInviteIdNotFound() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true}]}}}");
        inviteList = mapper.readTree("{}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedIsApplyingTrueInviteIdEmpty() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true, \"inviteId\":\"\"}]}}}");
        inviteList = mapper.readTree("{}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedIsApplyingTrueInviteIdNull() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true, \"inviteId\": null}]}}}");
        inviteList = mapper.readTree("{}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedInviteDataNotFound() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true,\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedInviteDataEmpty() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true,\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedInviteIdNotFound() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true,\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[{\"id\":\"different-invite-id\",\"agreed\":true}]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedInviteIdFoundAgreedNull() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true,\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[{\"id\":\"invite-id-1\",\"agreed\":null}]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedInviteIdFoundAgreedFalse() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true,\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[{\"id\":\"invite-id-1\",\"agreed\":false}]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertFalse(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedInviteIdFoundAgreedTrue() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true,\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[{\"id\":\"invite-id-1\",\"agreed\":true}]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertTrue(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedInviteIdsFoundAgreedTrueMultipleApplicants() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true,\"inviteId\":\"invite-id-1\"},{\"isApplying\":true,\"inviteId\":\"invite-id-2\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[{\"id\":\"invite-id-2\",\"agreed\":true},{\"id\":\"invite-id-1\",\"agreed\":true}]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertTrue(invitationService.checkAllInvitedAgreed(""));

    }


    @Test
    public void checkAllInvitedAgreedInviteIdFoundAgreedTruePrimaryApplicant() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true,\"isApplicant\": true},{\"isApplying\":true,\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[{\"id\":\"invite-id-1\",\"agreed\":true}]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertTrue(invitationService.checkAllInvitedAgreed(""));

    }

    @Test
    public void checkAllInvitedAgreedInviteIdsFoundAgreedTruePreviouslyAgreedNoLongerPresent() throws IOException {


        formData =  mapper.readTree("{\"formdata\":{\"executors\":{\"list\":[{\"isApplying\":true,\"inviteId\":\"invite-id-1\"}]}}}");
        inviteList = mapper.readTree("{\"invitedata\":[{\"id\":\"invite-id-1\",\"agreed\":true},{\"id\":\"invite-id-2\",\"agreed\":true}]},{\"id\":\"invite-id-3\",\"agreed\":true}]}");

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        assertTrue(invitationService.checkAllInvitedAgreed(""));

    }

}