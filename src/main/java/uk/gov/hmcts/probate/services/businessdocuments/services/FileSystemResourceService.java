package uk.gov.hmcts.probate.services.businessdocuments.services;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;

@Slf4j
@Component
public class FileSystemResourceService {

    public static final String BUSINESS_DOCUMENT_TEMPLATE_COULD_NOT_BE_FOUND =
        "Business Document template could not be found";

    public Optional<FileSystemResource> getFileSystemResource(String resourcePath) {
        final File secureDir = new File(Files.createTempDir().getAbsolutePath());
        final InputStream ins = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        return Optional.ofNullable(ins)
            .map(in -> {
                FileOutputStream out = null;
                try {
                    File tempFile =
                        File.createTempFile(String.valueOf(in.hashCode()), ".html", secureDir);
                    secureDir.deleteOnExit();
                    tempFile.deleteOnExit();
                    out = new FileOutputStream(tempFile);
                    IOUtils.copy(in, out);
                    return new FileSystemResource(tempFile);
                } catch (IOException e) {
                    log.error("File system [ {} ] could not be found", resourcePath, e);
                    throw new FileSystemException(BUSINESS_DOCUMENT_TEMPLATE_COULD_NOT_BE_FOUND, e);
                } finally {
                    if (out != null) {
                        safeClose(out);
                    }
                    if (ins != null) {
                        safeCloseInputStream(in);
                    }
                }
            });
    }

    public String getFileFromResourceAsString(String resourcePath) {
        Optional<FileSystemResource> fileSystemResource = getFileSystemResource(resourcePath);
        if (fileSystemResource.isPresent()) {
            try {
                return FileUtils.readFileToString(fileSystemResource.get().getFile(), Charset.defaultCharset());
            } catch (IOException e) {
                throw new FileSystemException(BUSINESS_DOCUMENT_TEMPLATE_COULD_NOT_BE_FOUND, e);
            }
        }
        log.error("File system [ {} ] could not be found", fileSystemResource);
        throw new FileSystemException(BUSINESS_DOCUMENT_TEMPLATE_COULD_NOT_BE_FOUND, null);
    }

    private void safeClose(FileOutputStream out) {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                log.error("Error occurred during closing FileOutStream", e);
            }
        }
    }

    private void safeCloseInputStream(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.error("Error occurred during closing InputStream", e);
            }
        }
    }
}
