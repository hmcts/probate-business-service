package uk.gov.hmcts.probate.services.businessdocuments.services;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.businessdocuments.exceptions.FileSystemException;



import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Component
@SuppressWarnings("squid:S5443")
public class FileSystemResourceService {

    public static final String BUSINESS_DOCUMENT_TEMPLATE_COULD_NOT_BE_FOUND =
        "Business Document template could not be found";


    public Optional<FileSystemResource> getFileSystemResource(String resourcePath) {
        final InputStream ins = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
        return Optional.ofNullable(ins)
            .map(in -> {
                FileOutputStream out = null;
                try (ins) {
                    Path secureDir = Files.createTempDirectory("");
                    Path tempFile = Files.createTempFile(
                        Paths.get(secureDir.toAbsolutePath().toString()),"",".html");
                    secureDir.toFile().deleteOnExit();
                    tempFile.toFile().deleteOnExit();
                    out = new FileOutputStream(tempFile.toFile());
                    IOUtils.copy(in, out);
                    return new FileSystemResource(tempFile.toFile());
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
