package uk.gov.hmcts.probate.services.document.clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.document.utils.DocumentUtils;

@Component
public class PersistenceClient {

    @Value("${services.persistence.formdata.url}")
    private String formDataPersistenceUrl;

    private DocumentUtils documentUtils;
    private RestTemplate restTemplate;

    @Autowired
    public PersistenceClient(DocumentUtils documentUtils, RestTemplate restTemplate) {
        this.documentUtils = documentUtils;
        this.restTemplate = restTemplate;
    }

    @Retryable(backoff = @Backoff(delay = 100, maxDelay = 500))
    public void updateFormData(String emailId, JsonNode documentData) {
        ObjectNode persistenceRequestBody = new ObjectMapper().createObjectNode();
        persistenceRequestBody.set("formdata", documentData);
        HttpEntity<JsonNode> persistenceRequest = documentUtils.createPersistenceRequest(persistenceRequestBody);
        try {
            restTemplate.patchForObject(formDataPersistenceUrl + "/" + emailId, persistenceRequest, ResponseEntity.class);
        } catch (HttpClientErrorException e) {
            documentUtils.logHttpClientErrorException(e);
            throw e;
        }
    }
}
