package uk.gov.hmcts.probate.services.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.domain.Classification;
import uk.gov.hmcts.reform.document.domain.UploadResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Service
public class DocumentService {

    @Value("${services.documentmanagement.baseUrl}")
    private String documentManagementUrl;

    private final AuthTokenGenerator authTokenGenerator;
    private RestTemplate restTemplate;

    private static final String CLASSIFICATION = "classification";
    private static final String FILES = "files";
    private static final String DOCUMENTS_PATH = "/documents";
    private static final String SERVICE_AUTHORIZATION = "ServiceAuthorization";
    private static final String USER_ID = "user-id";

    @Autowired
    public DocumentService(
            AuthTokenGenerator authTokenGenerator,
            RestTemplate restTemplate
    ) {
        this.authTokenGenerator = authTokenGenerator;
        this.restTemplate = restTemplate;
    }

    public UploadResponse upload(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
            @RequestHeader(SERVICE_AUTHORIZATION) String serviceAuth,
            @RequestHeader(USER_ID) String userId,
            @RequestPart List<MultipartFile> files
    ) {
        MultiValueMap<String, Object> parameters = prepareRequest(files);
        HttpHeaders httpHeaders = setHttpHeaders(authorisation, serviceAuth, userId);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parameters, httpHeaders);

        return uploadDocuments(documentManagementUrl + DOCUMENTS_PATH, httpEntity);
    }

    private UploadResponse uploadDocuments(String url, HttpEntity httpEntity) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String responseBody = restTemplate.postForObject(url, httpEntity, String.class);
            return objectMapper.readValue(responseBody, UploadResponse.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private HttpHeaders setHttpHeaders(String authorizationToken, String serviceAuth, String userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, authorizationToken);
        headers.add(SERVICE_AUTHORIZATION, serviceAuth);
        headers.add(USER_ID, userId);

        headers.set(HttpHeaders.CONTENT_TYPE, MULTIPART_FORM_DATA_VALUE);

        return headers;
    }

    private static MultiValueMap<String, Object> prepareRequest(List<MultipartFile> files) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        files.stream()
                .map(DocumentService::buildPartFromFile)
                .forEach(file -> parameters.add(FILES, file));
        parameters.add(CLASSIFICATION, Classification.RESTRICTED.name());
        return parameters;
    }

    private static HttpEntity<Resource> buildPartFromFile(MultipartFile file) {
        return new HttpEntity<>(buildByteArrayResource(file), buildPartHeaders(file));
    }

    private static HttpHeaders buildPartHeaders(MultipartFile file) {
        requireNonNull(file.getContentType());
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.getContentType()));
        return headers;
    }

    private static ByteArrayResource buildByteArrayResource(MultipartFile file) {
        try {
            return new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
        } catch (IOException ioException) {
            throw new IllegalStateException(ioException);
        }
    }

    public ResponseEntity<?> delete(String userID, String documentId) {
        HttpEntity<Object> httpEntity = deleteDocumentHeaders(userID);

        return restTemplate.exchange(
                documentManagementUrl + DOCUMENTS_PATH + "/" + documentId,
                HttpMethod.DELETE,
                httpEntity,
                String.class);
    }

    private HttpEntity<Object> deleteDocumentHeaders(String userID) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(SERVICE_AUTHORIZATION, authTokenGenerator.generate());
        headers.add(USER_ID, userID);

        return new HttpEntity<>(headers);
    }
}
