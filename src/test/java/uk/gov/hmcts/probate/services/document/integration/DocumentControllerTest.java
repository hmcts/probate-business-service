package uk.gov.hmcts.probate.services.document.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.probate.services.document.controllers.DocumentController;
import uk.gov.hmcts.probate.services.document.utils.DocumentUtils;
import uk.gov.hmcts.probate.services.document.validators.DocumentValidation;
import uk.gov.hmcts.probate.services.persistence.PersistenceClient;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DocumentService documentService;

    @MockBean
    private DocumentValidation documentValidation;

    @MockBean
    private PersistenceClient persistenceClient;

    @MockBean
    private DocumentUtils documentUtils;

    private DocumentController documentController;

    private static final String DUMMY_OAUTH_2_TOKEN = "oauth2Token";
    private static final String USER_ID = "tom@email.com";

    @Test
    public void shouldThrowDocumentsMissingExceptionIfThereAreNoFilesInTheRequest() throws Exception {
        mockMvc.perform(post("/document/upload")
                .header("authorizationToken", DUMMY_OAUTH_2_TOKEN)
                .header("user-id", USER_ID)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldThrowDocumentsMissingExceptionForEmptyFileList() throws Exception {
        mockMvc.perform(post("/document/upload")
                .header("authorizationToken", DUMMY_OAUTH_2_TOKEN)
                .header("user-id", USER_ID)
                .content("[]")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isNotFound());
    }


//    @Test(expected = UnSupportedDocumentTypeException.class)
//    public void shouldThrowUnSupportedDocumentTypeExceptionForTooManyFiles() {
//        MultipartFile file = Mockito.mock(MultipartFile.class);
//        List<MultipartFile> files = new ArrayList<>();
//        for(int i = 1; i <= 11; i ++) {
//            files.add(file);
//        }
//        documentController.upload(DUMMY_OAUTH_2_TOKEN, USER_ID, files);
//    }
//
//    @Test(expected = UnSupportedDocumentTypeException.class)
//    public void shouldThrowUnSupportedDocumentTypeExceptionInvalidFileExtension() {
//        MockMultipartFile file = new MockMultipartFile("testData", "filename.txt", "text/plain", "some xml".getBytes());
//        List<MultipartFile> files = new ArrayList<>();
//        files.add(file);
//        documentController.upload(DUMMY_OAUTH_2_TOKEN, USER_ID, files);
//    }
//
//    @Test
//    public void shouldDeleteFile() {
//        ResponseEntity response = mock(ResponseEntity.class);
//        when(documentService.delete("file")).thenReturn(response);
//        //when(documentController.delete("file")).thenReturn(response);
//
//        ResponseEntity actualResponse = documentController.delete("file");
//        assertThat(actualResponse, equalTo(response));
//    }
}
