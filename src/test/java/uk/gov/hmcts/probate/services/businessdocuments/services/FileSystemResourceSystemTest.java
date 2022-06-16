package uk.gov.hmcts.probate.services.businessdocuments.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileSystemResourceSystemTest {

    public static final String TEST_FILE = "templates/pdf/checkAnswersSummary.html";

    private FileSystemResourceService fileSystemResourceService;

    @BeforeEach
    public void setUp() {
        fileSystemResourceService = new FileSystemResourceService();
    }

    @Test
    public void shouldFindAFileAndConvertToString() {
        String result = fileSystemResourceService.getFileFromResourceAsString(TEST_FILE);
        assertNotNull(result);
    }

    @Test
    public void shouldNotFindAFileAndConvertToString() {
        assertThrows(FileSystemException.class, () -> {
            String result = fileSystemResourceService.getFileFromResourceAsString("missing file");
            assertNotNull(result);
        });
    }

    @Test
    public void shouldFindOptionalResult() {
        Optional<FileSystemResource> result = fileSystemResourceService.getFileSystemResource(TEST_FILE);
        assertNotNull(result);
    }

    @Test
    public void shouldNotFindOptionalResult() {
        Optional<FileSystemResource> result = fileSystemResourceService.getFileSystemResource("missing file");
        assertNotNull(result);
    }

    @Test
    public void shouldReturnNullWhenGettingFileFromResourceStringAndFileIsNotPresent() {
        FileSystemResourceService fileSystemResourceServiceSpy = Mockito.spy(new FileSystemResourceService());
        when(fileSystemResourceServiceSpy.getFileSystemResource(anyString())).thenReturn(Optional.empty());

        assertThrows(FileSystemException.class, () -> {
            String resource = fileSystemResourceServiceSpy.getFileFromResourceAsString("");
        });
    }

    @Test
    public void shouldReturnNullFromResourceStringAndIOExceptionIsThrown() {
        FileSystemResourceService fileSystemResourceServiceSpy = Mockito.spy(new FileSystemResourceService());
        File mockFile = Mockito.mock(File.class);
        FileSystemResource fileSystemResource = mock(FileSystemResource.class);
        Mockito.when(fileSystemResource.getFile()).thenReturn(mockFile);
        when(fileSystemResourceServiceSpy.getFileSystemResource(anyString()))
            .thenReturn(Optional.of(fileSystemResource));

        assertThrows(FileSystemException.class, () -> {
            String resource = fileSystemResourceServiceSpy.getFileFromResourceAsString("");
        });
    }
}
