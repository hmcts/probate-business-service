package uk.gov.hmcts.probate.services.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.domain.UploadResponse;

import java.util.List;

@Service
public class DocumentService {

    @Value("${services.documentmanagement.baseUrl}")
    private String documentManagementUrl;

    private final AuthTokenGenerator authTokenGenerator;
    private final DocumentUploadClientApi documentUploadClientApi;
    private RestTemplate restTemplate;

    private static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";

    @Autowired
    public DocumentService(
            AuthTokenGenerator authTokenGenerator,
            DocumentUploadClientApi documentUploadClientApi,
            RestTemplate restTemplate
    ) {
        this.authTokenGenerator = authTokenGenerator;
        this.documentUploadClientApi = documentUploadClientApi;
        this.restTemplate = restTemplate;
    }

    public UploadResponse upload(List<MultipartFile> files,
                                 String authorizationToken, String userID) {
        return documentUploadClientApi.upload(
                authorizationToken,
                authTokenGenerator.generate(),
                userID,
                files
        );
    }

    public ResponseEntity<?> delete(String documentId) {
        restTemplate.delete(documentManagementUrl + "/" + documentId);

        HttpEntity<Object> httpEntity = deleteDocumentHeaders();

        return restTemplate.exchange(
                documentManagementUrl + "/" + documentId,
                HttpMethod.DELETE,
                httpEntity,
                String.class);
    }

    private HttpEntity<Object> deleteDocumentHeaders() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(SERVICE_AUTHORIZATION_HEADER, authTokenGenerator.generate());

        return new HttpEntity<>(httpHeaders);
    }
}
