//package uk.gov.hmcts.probate.services.invitation;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Matchers;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//import uk.gov.hmcts.probate.services.invitation.model.InviteData;
//
//import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.core.IsEqual.equalTo;
//import static org.mockito.ArgumentMatchers.anyObject;
//import static org.mockito.Mockito.anyString;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class PersistenceClientTest {
//
//    ObjectMapper mapper;
//
//    @Mock
//    RestTemplate restTemplate;
//
//    @InjectMocks
//    @Spy
//    PersistenceClient persistenceClient;
//
//    @Before
//    public void setUp() throws Exception {
//        mapper = new ObjectMapper();
//    }
//
//    @Test
//    public void saveInviteData() {
//        InviteData inviteData = new InviteData("link", "formdata", "test@test.com", "07777777777", "MainName");
//        JsonNode jsonNode = mapper.convertValue(inviteData, JsonNode.class);
//        when(restTemplate.postForEntity(anyString(), anyObject(), Matchers.any(Class.class)))
//                .thenReturn(new ResponseEntity<>(jsonNode, HttpStatus.OK));
//
//        JsonNode result = persistenceClient.saveInviteData(inviteData);
//
//        assertThat(result.get("id").asText(), equalTo("link"));
//        assertThat(result.get("formdataId").asText(), equalTo("formdata"));
//        assertThat(result.get("email").asText(), equalTo("test@test.com"));
//
//        assertThat(result.get("phoneNumber").asText(), equalTo("07777777777"));
//        assertThat(result.get("mainExecutorName").asText(), equalTo("MainName"));
//    }
//
//}