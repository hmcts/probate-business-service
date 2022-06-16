package uk.gov.hmcts.probate.services.document.unit;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.document.validators.DocumentValidation;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class DocumentValidationTest {

    private DocumentValidation documentValidation;

    @BeforeEach
    public void setUp() {
        documentValidation = new DocumentValidation();
        ReflectionTestUtils.setField(documentValidation,
            "allowedFileExtensions", ".pdf .jpeg .bmp .tif .tiff .png .pdf");
        ReflectionTestUtils.setField(documentValidation,
            "allowedMimeTypes", "image/jpeg application/pdf image/tiff image/png image/bmp");
    }

    @Test
    public void rejectInvalidFileForContentType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.pdf", "filename.pdf", "text/plain", "some xml".getBytes());
        boolean result = documentValidation.isValid(file);
        assertEquals(false, result);
    }

    @Test
    public void rejectInvalidFileForFileName() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.txt", "filename.txt", "image/jpeg", "some xml".getBytes());
        boolean result = documentValidation.isValid(file);
        assertEquals(false, result);
    }

    @Test
    public void rejectInvalidFileForFileSize() throws IOException {
        TestUtils testUtils = new TestUtils();
        MockMultipartFile file = new MockMultipartFile("filename.txt", "filename.txt", "image/jpeg",
            testUtils.getJsonFromFile("files/large_pdf.pdf").getBytes());
        boolean result = documentValidation.isValid(file);
        assertEquals(false, result);
    }

    @Test
    public void approveValidFile() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.isValid(file);
        assertEquals(true, result);
    }

    @Test
    public void rejectInvalidMimeType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.txt", "filename.txt", "text/plain", "some xml".getBytes());
        boolean result = documentValidation.validMimeType(file.getContentType());
        assertEquals(false, result);
    }

    @Test
    public void approveValidMimeType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.validMimeType(file.getContentType());
        assertEquals(true, result);
    }

    @Test
    public void rejectInvalidFileType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.txt", "filename.txt", "text/plain", "some xml".getBytes());
        boolean result = documentValidation.validFileType(file.getName());
        assertEquals(false, result);
    }

    @Test
    public void approveValidFileType() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.validFileType(file.getName());
        assertEquals(true, result);
    }

    @Test
    public void rejectInvalidFileSize() throws IOException {
        final byte[] bytes = IOUtils.toByteArray(getClass().getResourceAsStream("/files/large_pdf.pdf"));
        MockMultipartFile file = new MockMultipartFile("filename.pdf", "filename.pdf", "application/pdf", bytes);
        boolean result = documentValidation.validFileSize(file);
        assertEquals(false, result);
    }

    @Test
    public void approveValidFileSize() throws IOException {
        MockMultipartFile file =
            new MockMultipartFile("filename.png", "filename.png", "image/png", "some xml".getBytes());
        boolean result = documentValidation.validFileSize(file);
        assertEquals(true, result);
    }
}
