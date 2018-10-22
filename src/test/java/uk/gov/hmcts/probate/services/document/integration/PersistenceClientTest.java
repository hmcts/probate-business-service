package uk.gov.hmcts.probate.services.document.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.document.utils.DocumentUtils;
import uk.gov.hmcts.probate.services.persistence.PersistenceClient;

import static org.mockito.Matchers.endsWith;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceClientTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private DocumentUtils documentUtils;
    @InjectMocks
    private PersistenceClient persistenceClient;

    @Test
    public void updateFormDataSuccessTest() {
        HttpEntity<JsonNode> persistenceReq = new HttpEntity<>(new TextNode("requestBody"), new HttpHeaders());
        when(documentUtils.createPersistenceRequest(any())).thenReturn(persistenceReq);

        persistenceClient.updateFormData("emailId", new TextNode("requestBody"));
        verify(restTemplate, times(1)).patchForObject(endsWith("/emailId"), eq(persistenceReq), anyObject());
    }
}
