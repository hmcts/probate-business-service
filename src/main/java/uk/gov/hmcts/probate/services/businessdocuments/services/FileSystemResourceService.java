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
import java.nio.charset.Charset;
import java.util.Optional;

@Slf4j
@Component
public class FileSystemResourceService {

    public static final String BUSINESS_DOCUMENT_TEMPLATE_COULD_NOT_BE_FOUND =
        "Business Document template could not be found";

    public Optional<FileSystemResource> getFileSystemResource(String resourcePath) {
        final String secureTempDir = Files.createTempDir().getAbsolutePath();
        final File secureDir = new File(secureTempDir);
        return Optional.ofNullable(this.getClass().getClassLoader().getResourceAsStream(resourcePath))
            .map(in -> {
                try {
                    File tempFile =
                        File.createTempFile(String.valueOf(in.hashCode()), ".html", secureDir);
                    secureDir.deleteOnExit();
                    tempFile.deleteOnExit();
                    FileOutputStream out = new FileOutputStream(tempFile);
                    IOUtils.copy(in, out);
                    return new FileSystemResource(tempFile);
                } catch (IOException e) {
                    log.error("File system [ {} ] could not be found", resourcePath, e);
                    throw new FileSystemException(BUSINESS_DOCUMENT_TEMPLATE_COULD_NOT_BE_FOUND, e);
                }
            });
    }

    public String getFileFromResourceAsString(String resourcePath) {
        Optional<FileSystemResource> fileSystemResource = getFileSystemResource(resourcePath);
        if (fileSystemResource.isPresent()) {
            try {
                return FileUtils.readFileToString(fileSystemResource.get().getFile(), Charset.defaultCharset());
            } catch (IOException | NullPointerException e) {
                throw new FileSystemException(BUSINESS_DOCUMENT_TEMPLATE_COULD_NOT_BE_FOUND, e);
            }
        }
        log.error("File system [ {} ] could not be found", fileSystemResource);
        throw new FileSystemException(BUSINESS_DOCUMENT_TEMPLATE_COULD_NOT_BE_FOUND, null);
    }

}
