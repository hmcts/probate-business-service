package uk.gov.hmcts.probate.services.invitation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.invitation.model.InviteData;
import uk.gov.hmcts.probate.services.persistence.PersistenceClient;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceClientTest {

    private static final String INVITE_URL = "http://invite:9999";

    private ObjectMapper mapper;

    @Mock
    private RestTemplate restTemplate;

    private PersistenceClient persistenceClient;

    @Before
    public void setUp() {
        persistenceClient = new PersistenceClient();
        ReflectionTestUtils.setField(persistenceClient, "inviteDataPersistenceUrl", INVITE_URL);
        ReflectionTestUtils.setField(persistenceClient, "restTemplate", restTemplate);
        mapper = new ObjectMapper();
    }


    @Test
    public void saveInviteData() {
        InviteData inviteData = new InviteData("link", "formdata", "test@test.com", "07777777777", "MainName");
        JsonNode jsonNode = mapper.convertValue(inviteData, JsonNode.class);
        when(restTemplate.postForEntity(INVITE_URL, inviteData, JsonNode.class))
                .thenReturn(new ResponseEntity<>(jsonNode, HttpStatus.OK));

        JsonNode result = persistenceClient.saveInviteData(inviteData);

        assertThat(result.get("id").asText(), equalTo("link"));
        assertThat(result.get("formdataId").asText(), equalTo("formdata"));
        assertThat(result.get("email").asText(), equalTo("test@test.com"));

        assertThat(result.get("phoneNumber").asText(), equalTo("07777777777"));
        assertThat(result.get("mainExecutorName").asText(), equalTo("MainName"));
    }
}
