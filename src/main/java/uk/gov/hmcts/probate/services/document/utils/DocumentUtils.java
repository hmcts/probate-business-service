package uk.gov.hmcts.probate.services.document.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DocumentUtils {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DOCUMENTS = "documents";
    private static final String UPLOADS = "uploads";
    private static final String FILENAME = "filename";
    private static final String FILEURL = "url";

    public HttpEntity<JsonNode> createPersistenceRequest(JsonNode requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(requestBody, headers);
    }

    public void logHttpClientErrorException(HttpClientErrorException e) {
        logger.error("Exception while talking to probate-persistence-service: ", e);
        logger.error(e.getMessage());
    }

    public JsonNode populateDocumentObject(Map<String, String> documentData) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode documents = mapper.createObjectNode();
        ObjectNode uploads = mapper.createObjectNode();
        List<JsonNode> uploadsList = new ArrayList<>();

        for (Map.Entry<String, String> entry : documentData.entrySet()) {
            ObjectNode upload = mapper.createObjectNode();
            upload.put(FILENAME, entry.getKey());
            upload.put(FILEURL, entry.getValue());
            uploadsList.add(upload);
        }

        uploads.putArray(UPLOADS).addAll(uploadsList);
        documents.set(DOCUMENTS, uploads);
        return documents;
    }
}
