package uk.gov.hmcts.probate.services.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmcts.probate.services.invitation.model.InviteData;

@Component
public class PersistenceClient {

    @Value("${services.persistence.invitedata.url}")
    private String inviteDataPersistenceUrl;

    @Value("${services.persistence.formdata.url}")
    private String formDataPersistenceUrl;

    private RestTemplate restTemplate;


    @Autowired
    public PersistenceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(backoff = @Backoff(delay = 100, maxDelay = 500))
    public JsonNode saveInviteData(InviteData inviteData) {
        HttpEntity<JsonNode> persistenceResponse = restTemplate.postForEntity(inviteDataPersistenceUrl, inviteData, JsonNode.class);
        return persistenceResponse.getBody();
    }

    @Retryable(backoff = @Backoff(delay = 100, maxDelay = 500))
    public JsonNode getInvitesByFormdataId(String formdataId) {
        HttpEntity<JsonNode> persistenceResponse = restTemplate
                .getForEntity(inviteDataPersistenceUrl + "/search/formdata?id=" + formdataId, JsonNode.class);

        return persistenceResponse.getBody();
    }

    public JsonNode getFormdata(String formdataId) {
        HttpEntity<JsonNode> persistenceResponse = restTemplate.getForEntity(formDataPersistenceUrl+ '/' + formdataId, JsonNode.class);
        return persistenceResponse.getBody();
    }
}
