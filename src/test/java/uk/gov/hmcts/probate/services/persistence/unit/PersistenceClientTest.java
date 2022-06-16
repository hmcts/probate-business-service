package uk.gov.hmcts.probate.services.persistence.unit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.persistence.PersistenceClient;
import uk.gov.hmcts.reform.probate.model.multiapplicant.InviteData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class PersistenceClientTest {

    private static final String INVITE_URL = "http://invite:9999";
    private static final String FORMDATA_URL = "http://formdata:9999";

    private ObjectMapper mapper;

    @Mock
    private RestTemplate restTemplate;

    private PersistenceClient persistenceClient;

    @BeforeEach
    public void setUp() {
        persistenceClient = new PersistenceClient(restTemplate);
        ReflectionTestUtils.setField(persistenceClient, "inviteDataPersistenceUrl", INVITE_URL);
        ReflectionTestUtils.setField(persistenceClient, "formDataPersistenceUrl", FORMDATA_URL);
        ReflectionTestUtils.setField(persistenceClient, "restTemplate", restTemplate);
        mapper = new ObjectMapper();
    }

    @Test
    public void saveInviteData() {
        InviteData inviteData = new InviteData("link", "formdata", "MainName", "07777777777", "test@test.com", false);
        JsonNode jsonNode = mapper.convertValue(inviteData, JsonNode.class);
        when(restTemplate.postForEntity(INVITE_URL, inviteData, JsonNode.class))
            .thenReturn(new ResponseEntity<>(jsonNode, HttpStatus.OK));

        JsonNode result = persistenceClient.saveInviteData(inviteData);

        assertEquals("link", result.get("id").asText());
        assertEquals("formdata", result.get("formdataId").asText());
        assertEquals("test@test.com", result.get("email").asText());
        assertEquals("07777777777", result.get("phoneNumber").asText());
        assertEquals("MainName", result.get("mainExecutorName").asText());
    }

    @Test
    public void getInvitesByFormData() {
        InviteData inviteData = new InviteData("link", "formdata", "MainName", "07777777777", "test@test.com", false);
        JsonNode jsonNode = mapper.convertValue(inviteData, JsonNode.class);
        when(restTemplate.getForEntity(INVITE_URL + "/search/formdata?id=formdata", JsonNode.class))
            .thenReturn(new ResponseEntity<>(jsonNode, HttpStatus.OK));

        JsonNode result = persistenceClient.getInvitesByFormdataId("formdata");

        assertEquals("link", result.get("id").asText());
        assertEquals("formdata", result.get("formdataId").asText());
        assertEquals("test@test.com", result.get("email").asText());
        assertEquals("07777777777", result.get("phoneNumber").asText());
        assertEquals("MainName", result.get("mainExecutorName").asText());
    }

    @Test
    public void getFormData() {
        InviteData inviteData = new InviteData("link", "formdata", "MainName", "07777777777", "test@test.com", false);
        JsonNode jsonNode = mapper.convertValue(inviteData, JsonNode.class);
        when(restTemplate.getForEntity(FORMDATA_URL + "/formdata", JsonNode.class))
            .thenReturn(new ResponseEntity<>(jsonNode, HttpStatus.OK));

        JsonNode result = persistenceClient.getFormdata("formdata");

        assertEquals("link", result.get("id").asText());
        assertEquals("formdata", result.get("formdataId").asText());
        assertEquals("test@test.com", result.get("email").asText());
        assertEquals("07777777777", result.get("phoneNumber").asText());
        assertEquals("MainName", result.get("mainExecutorName").asText());
    }
}
