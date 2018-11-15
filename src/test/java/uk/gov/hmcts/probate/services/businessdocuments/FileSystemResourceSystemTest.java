package uk.gov.hmcts.probate.services.businessdocuments;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.services.exceptions.FileSystemException;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.notNull;

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
}
