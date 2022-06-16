package uk.gov.hmcts.probate.services.document.integration;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.probate.services.document.controllers.DocumentController;
import uk.gov.hmcts.probate.services.document.validators.DocumentValidation;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.domain.Document;
import uk.gov.hmcts.reform.document.domain.UploadResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DocumentControllerTest {

    private static final String EXPECTED_HREF = "http://href";

    private static final String VALID_FILE_NAME = "valid_file.png";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private AuthTokenGenerator authTokenGenerator;

    private DocumentValidation documentValidation;

    private DocumentController documentController;

    private static final String DUMMY_OAUTH_2_TOKEN = "oauth2Token";
    private static final String USER_ID = "tom@email.com";
    private static final String AUTH_TOKEN = "authToken";

    @BeforeEach
    public void setUp() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();

        when(authTokenGenerator.generate()).thenReturn(AUTH_TOKEN);
        documentValidation = new DocumentValidation();
        ReflectionTestUtils.setField(documentValidation,
            "allowedFileExtensions", ".pdf .jpeg .bmp .tif .tiff .png .pdf");
        ReflectionTestUtils.setField(documentValidation,
            "allowedMimeTypes", "image/jpeg application/pdf image/tiff image/png image/bmp");

        documentController = new DocumentController(documentService, documentValidation, authTokenGenerator);
    }

    @Test
    public void shouldReturnErrorIfThereAreNoFilesInTheRequest() {
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("Error: no files passed");

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, USER_ID, null);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldReturnErrorForEmptyFileList() {
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("Error: no files passed");

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, USER_ID, Collections.emptyList());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldReturnErrorForTooManyFiles() {
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("Error: too many files");

        MultipartFile file = Mockito.mock(MultipartFile.class);
        List<MultipartFile> files = new ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            files.add(file);
        }

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, USER_ID, files);
        assertEquals(expectedResult, actualResult);

    }

    @Test
    public void shouldReturnErrorForInvalidFileExtension() {
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add("Error: invalid file type");
        MockMultipartFile file = new MockMultipartFile("testData", "filename.txt", "text/plain", "some xml".getBytes());
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, USER_ID, files);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldDeleteFile() {
        ResponseEntity response = mock(ResponseEntity.class);
        when(documentService.delete(USER_ID, "file")).thenReturn(response);

        ResponseEntity actualResponse = documentController.delete(USER_ID, "file");
        assertEquals(response, actualResponse);
    }

    @Test
    public void shouldDeleteFileAndReturnAppropriateResponse() throws Exception {
        ResultMatcher ok = MockMvcResultMatchers.status()
            .isOk();

        this.mockMvc.perform(delete("/document/delete/document-id")
            .header("user-id", USER_ID)
            .content("[]")
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
            .andExpect(ok);
    }

    @Test
    public void shouldUploadSuccessfully() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/" + VALID_FILE_NAME));


        MockMultipartFile file = new MockMultipartFile(VALID_FILE_NAME, VALID_FILE_NAME, "image/jpeg", bytes);
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);

        UploadResponse uploadResponse = createUploadResponse();
        when(documentService.upload(eq(DUMMY_OAUTH_2_TOKEN), eq(AUTH_TOKEN), eq(USER_ID), eq(files)))
            .thenReturn(uploadResponse);

        List<String> actualResult = documentController.upload(DUMMY_OAUTH_2_TOKEN, USER_ID, files);
        assertEquals(false, actualResult.isEmpty());
    }

    private UploadResponse createUploadResponse() {
        UploadResponse response = mock(UploadResponse.class);
        UploadResponse.Embedded embedded = mock(UploadResponse.Embedded.class);
        when(response.getEmbedded()).thenReturn(embedded);
        Document document = createDocument();
        when(embedded.getDocuments()).thenReturn(Collections.singletonList(document));
        return response;
    }

    private Document createDocument() {
        Document document = new Document();
        Document.Links links = new Document.Links();
        Document.Link link = new Document.Link();
        link.href = EXPECTED_HREF;
        links.self = link;
        document.links = links;
        return document;
    }
}
