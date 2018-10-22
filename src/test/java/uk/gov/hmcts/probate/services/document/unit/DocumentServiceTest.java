package uk.gov.hmcts.probate.services.document.unit;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.domain.UploadResponse;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

    @Mock
    private AuthTokenGenerator authTokenGenerator;
    @Mock
    private DocumentUploadClientApi documentUploadClientApi;
    @Mock
    private RestTemplate restTemplate;

    private DocumentService documentService;

    private static final String AUTH_TOKEN = "authToken";
    private static final String DUMMY_OAUTH_2_TOKEN = "oauth2Token";
    private static final String USER_ID = "tom@email.com";

    @Before
    public void setUp() {
        documentService = new DocumentService(authTokenGenerator, documentUploadClientApi, restTemplate);
    }

    @Test
    public void shouldUploadDocumentsToEvidenceManagement() {
        MultipartFile file = mock(MultipartFile.class);
        List<MultipartFile> files = Collections.singletonList(file);
        when(authTokenGenerator.generate()).thenReturn(AUTH_TOKEN);
        UploadResponse uploadResponse = mock(UploadResponse.class);
        when(documentService.upload(files, DUMMY_OAUTH_2_TOKEN, USER_ID)).thenReturn(uploadResponse);

        UploadResponse actualUploadResponseEmbedded = documentService.upload(files, DUMMY_OAUTH_2_TOKEN, USER_ID);
        assertThat(actualUploadResponseEmbedded, equalTo(uploadResponse));
    }

    @Test
    public void shouldDeleteDocumentFromEvidenceManagement() {
        ResponseEntity response = mock(ResponseEntity.class);
        when(authTokenGenerator.generate()).thenReturn(AUTH_TOKEN);
        when(documentService.delete("file")).thenReturn(response);

        ResponseEntity actualResponse = documentService.delete("file");
        assertThat(actualResponse, equalTo(response));
    }
}
