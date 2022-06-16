package uk.gov.hmcts.probate.services.businessdocuments.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.services.businessdocuments.services.FileSystemResourceService;
import uk.gov.hmcts.reform.probate.model.documents.DeclarationItem;
import uk.gov.hmcts.reform.probate.model.documents.DeclarationSection;
import uk.gov.hmcts.reform.probate.model.documents.LegalDeclaration;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LegalDeclarationTest {

    public static final String VALID_LEGAL_DECLARATION_JSON = "businessdocuments/validLegalDeclaration.json";
    public static final String INVALID_LEGAL_DECLARATION_JSON = "businessdocuments/invalidLegalDeclaration.json";
    private ObjectMapper objectMapper;

    private FileSystemResourceService fileSystemResourceService;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        fileSystemResourceService = new FileSystemResourceService();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldCreateALegalDeclarationInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_LEGAL_DECLARATION_JSON);
        LegalDeclaration legalDeclaration = objectMapper.readValue(optional.get().getFile(), LegalDeclaration.class);
        assertNotNull(legalDeclaration);
        Set<ConstraintViolation<LegalDeclaration>> violations = validator.validate(legalDeclaration);
        assertEquals(0, violations.size());

        assertEquals("deceased", legalDeclaration.getDeceased());
        assertEquals("date and time", legalDeclaration.getDateCreated());

        List<String> headers = legalDeclaration.getDeclarations().stream().findFirst().get().getHeaders();
        assertEquals(3, headers.size());
        for (int i = 0; i < headers.size(); i++) {
            assertEquals("header" + i, headers.get(i));
        }

        DeclarationSection declarationSection =
            legalDeclaration.getDeclarations().stream().findFirst().get().getSections().get(0);
        assertEquals("large", declarationSection.getHeadingType());
        assertEquals("section title", declarationSection.getTitle());

        List<DeclarationItem> items = declarationSection.getDeclarationItems();
        assertEquals(1, items.size());

        DeclarationItem item = items.get(0);
        assertEquals("declaration title", item.getTitle());

        List<String> values = item.getValues();
        assertEquals(3, values.size());
        for (int i = 0; i < values.size(); i++) {
            assertEquals("value" + i, values.get(i));
        }
    }

    @Test
    public void shouldFailToCreateALegalDeclarationInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(INVALID_LEGAL_DECLARATION_JSON);
        LegalDeclaration legalDeclaration = objectMapper.readValue(optional.get().getFile(), LegalDeclaration.class);
        Set<ConstraintViolation<LegalDeclaration>> violations = validator.validate(legalDeclaration);
        assertEquals(2, violations.size());
    }

    private Optional<FileSystemResource> getFile(String fileName) {
        return fileSystemResourceService.getFileSystemResource(fileName);
    }
}
