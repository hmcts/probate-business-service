package uk.gov.hmcts.probate.services.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.document.DocumentUploadClientApi;
import uk.gov.hmcts.reform.document.domain.UploadResponse;

import java.util.List;

@Service
public class DocumentService {

    private final AuthTokenGenerator authTokenGenerator;
    private final DocumentUploadClientApi documentUploadClientApi;

    @Autowired
    public DocumentService(
            AuthTokenGenerator authTokenGenerator,
            DocumentUploadClientApi documentUploadClientApi
    ) {
        this.authTokenGenerator = authTokenGenerator;
        this.documentUploadClientApi = documentUploadClientApi;
    }

    public UploadResponse upload(List<MultipartFile> files,
                                 String authorizationToken, String userID) {
        return documentUploadClientApi.upload(
                authorizationToken,
                authTokenGenerator.generate(),
                userID,
                files
        );
    }
}
