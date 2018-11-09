package uk.gov.hmcts.probate.services.document.unit;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.ir.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.domain.UploadResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

    @Mock
    private AuthTokenGenerator authTokenGenerator;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ObjectMapper objectMapper;

    private DocumentService documentService;

    private static final String AUTH_TOKEN = "authToken";
    private static final String DUMMY_OAUTH_2_TOKEN = "oauth2Token";
    private static final String USER_ID = "tom@email.com";

    @Before
    public void setUp() {
        documentService = new DocumentService(authTokenGenerator, restTemplate);
    }

//    @Test
//    public void shouldUploadDocumentsToEvidenceManagement() throws IOException {
//        MultipartFile file = mock(MultipartFile.class);
//        byte[] bytes = new byte[10];
//        when(file.getBytes()).thenReturn(bytes);
//        when(file.getContentType()).thenReturn("application/pdf");
//        List<MultipartFile> files = Collections.singletonList(file);
//        when(authTokenGenerator.generate()).thenReturn(AUTH_TOKEN);
//        UploadResponse uploadResponse = mock(UploadResponse.class);
//        HttpEntity httpEntity = mock(HttpEntity.class);
//        when(restTemplate.postForObject("url", httpEntity, String.class)).thenReturn("response");
//        when(objectMapper.readValue(any(String.class), eq(UploadResponse.class))).thenReturn(uploadResponse);
//        when(documentService.upload(DUMMY_OAUTH_2_TOKEN, AUTH_TOKEN, USER_ID, files)).thenReturn(uploadResponse);
//
//        UploadResponse actualUploadResponseEmbedded = documentService.upload(DUMMY_OAUTH_2_TOKEN, AUTH_TOKEN, USER_ID, files);
//        assertThat(actualUploadResponseEmbedded, equalTo(uploadResponse));
//    }

    @Test
    public void shouldDeleteDocumentFromEvidenceManagement() {
        ResponseEntity response = mock(ResponseEntity.class);
        when(authTokenGenerator.generate()).thenReturn(AUTH_TOKEN);
        when(documentService.delete(USER_ID, "file")).thenReturn(response);

        ResponseEntity actualResponse = documentService.delete(USER_ID, "file");
        assertThat(actualResponse, equalTo(response));
    }
}
