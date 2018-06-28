package uk.gov.hmcts.probate.services.invitation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvitationServiceTest {

    ObjectMapper mapper;

    JsonNode formData, inviteList;

    @Mock
    PersistenceClient persistenceClient;

    TestUtils utils;

    @InjectMocks
    InvitationService invitationService;

    @Before
    public void setUp() throws Exception {
        mapper = new ObjectMapper();
        utils = new TestUtils();
        inviteList = mapper.readTree(utils.getJSONFromFile("invitation/inviteList.json"));
        formData = mapper.readTree(utils.getJSONFromFile("invitation/formdata.json"));
    }

    @Test
    public void checkAllInvitedAgreed() throws IOException {

        when(persistenceClient.getInvitesByFormdataId(anyString()))
                .thenReturn(inviteList);

        when(persistenceClient.getFormdata(anyString()))
                .thenReturn(formData);

        assertTrue(invitationService.checkAllInvitedAgreed(""));


    }

}