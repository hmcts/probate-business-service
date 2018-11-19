package uk.gov.hmcts.probate.services.businessdocuments.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;
import uk.gov.hmcts.probate.services.businessdocuments.services.FileSystemResourceService;

import java.io.File;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileSystemResourceSystemTest {

    public static final String TEST_FILE = "templates/pdf/checkAnswersSummary.html";

    private FileSystemResourceService fileSystemResourceService;

    @Before
    public void setUp() {
        fileSystemResourceService = new FileSystemResourceService();
    }

    @Test
    public void shouldFindAFileAndConvertToString() {
        String result = fileSystemResourceService.getFileFromResourceAsString(TEST_FILE);
        assertThat(result, is(notNullValue()));
    }

    @Test(expected = FileSystemException.class)
    public void shouldNotFindAFileAndConvertToString() {
        String result = fileSystemResourceService.getFileFromResourceAsString("missing file");
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void shouldFindOptionalResult() {
        Optional<FileSystemResource> result = fileSystemResourceService.getFileSystemResource(TEST_FILE);
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void shouldNotFindOptionalResult() {
        Optional<FileSystemResource> result = fileSystemResourceService.getFileSystemResource("missing file");
        assertThat(result, is(notNullValue()));
    }

    @Test(expected = FileSystemException.class)
    public void shouldReturnNullWhenGettingFileFromResourceStringAndFileIsNotPresent() {
        FileSystemResourceService fileSystemResourceServiceSpy = Mockito.spy(new FileSystemResourceService());
        when(fileSystemResourceServiceSpy.getFileSystemResource(anyString())).thenReturn(Optional.empty());

        String resource = fileSystemResourceServiceSpy.getFileFromResourceAsString("");
    }

    @Test(expected = FileSystemException.class)
    public void shouldReturnNullFromResourceStringAndIOExceptionIsThrown() {
        FileSystemResourceService fileSystemResourceServiceSpy = Mockito.spy(new FileSystemResourceService());
        File mockFile = Mockito.mock(File.class);
        FileSystemResource fileSystemResource = mock(FileSystemResource.class);
        Mockito.when(fileSystemResource.getFile()).thenReturn(mockFile);
        when(fileSystemResourceServiceSpy.getFileSystemResource(anyString())).thenReturn(Optional.of(fileSystemResource));

        String resource = fileSystemResourceServiceSpy.getFileFromResourceAsString("");
    }
}
