package uk.gov.hmcts.probate.services.document.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.domain.UploadResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class DocumentServiceTest {

    private static final String SERVICE_AUTH_TOKEN = "authTokenXXXXX";
    private static final String DUMMY_OAUTH_2_TOKEN = "oauth2Token";
    private static final String USER_ID = "33";
    private static final String DOCUMENT_ENDPOINT = "/documents";
    private static final String DOCUMENT_ID = "DOC-ID-12345";
    private static final String DOCUMENT_MANAGEMENT_URL = "http://document-management";
    private static final String DOCUMENT_DELETE_URL = DOCUMENT_MANAGEMENT_URL + DOCUMENT_ENDPOINT
        + "/" + DOCUMENT_ID + "?permanent=true";
    private static final String SERVICE_AUTHORIZATION_KEY = "ServiceAuthorization";
    private static final String USER_ID_KEY = "user-id";
    @Mock
    private AuthTokenGenerator authTokenGenerator;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ResponseEntity responseEntity;
    private DocumentService documentService;

    @BeforeEach
    public void setUp() {
        documentService = new DocumentService(authTokenGenerator, restTemplate);
        ReflectionTestUtils.setField(documentService, "documentManagementUrl", DOCUMENT_MANAGEMENT_URL);

        when(authTokenGenerator.generate()).thenReturn(SERVICE_AUTH_TOKEN);
    }

    @Test
    public void shouldUploadDocumentsToEvidenceManagement() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        String response = new String(Files.readAllBytes(Paths.get("src/test/resources/files", "response.json")));

        List<MultipartFile> files = Collections.singletonList(file);
        when(restTemplate.postForObject(eq(DOCUMENT_MANAGEMENT_URL + DOCUMENT_ENDPOINT), any(HttpEntity.class),
            eq(String.class))).thenReturn(response);

        UploadResponse actualUploadResponse =
            documentService.upload(DUMMY_OAUTH_2_TOKEN, SERVICE_AUTH_TOKEN, USER_ID, files);
        assertEquals(UploadResponse.class, actualUploadResponse.getClass());
    }

    @Test
    public void shouldDeleteDocumentFromEvidenceManagement() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SERVICE_AUTHORIZATION_KEY, SERVICE_AUTH_TOKEN);
        headers.add(USER_ID_KEY, USER_ID);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        when(restTemplate.exchange(eq(DOCUMENT_DELETE_URL), eq(HttpMethod.DELETE), eq(httpEntity), eq(String.class)))
            .thenReturn(responseEntity);

        ResponseEntity actualResponse = documentService.delete(USER_ID, DOCUMENT_ID);
        assertEquals(responseEntity, actualResponse);
        verify(restTemplate, times(1))
            .exchange(eq(DOCUMENT_DELETE_URL), eq(HttpMethod.DELETE), eq(httpEntity), eq(String.class));
    }
}
