package uk.gov.hmcts.probate.services.businessdocuments.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class LegalDeclarationTest {

    public static final String VALID_LEGAL_DECLARATION_JSON = "businessdocuments/validLegalDeclaration.json";
    public static final String INVALID_LEGAL_DECLARATION_JSON = "businessdocuments/invalidLegalDeclaration.json";
    private ObjectMapper objectMapper;

    private FileSystemResourceService fileSystemResourceService;
    private Validator validator;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,true);
        fileSystemResourceService = new FileSystemResourceService();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldCreateALegalDeclarationInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_LEGAL_DECLARATION_JSON);
        LegalDeclaration legalDeclaration = objectMapper.readValue(optional.get().getFile(), LegalDeclaration.class);
        assertThat(legalDeclaration, is(notNullValue()));
        Set<ConstraintViolation<LegalDeclaration>> violations = validator.validate(legalDeclaration);
        assertThat(violations, is(empty()));

        assertThat(legalDeclaration.getDeceased(), is(equalTo("deceased")));
        assertThat(legalDeclaration.getDateCreated(), is(equalTo("date and time")));

        List<String> headers = legalDeclaration.getHeaders();
        assertThat(headers.size(), is(equalTo(3)));
        for (int i = 0; i < headers.size() ; i++) {
            assertThat(headers.get(i), is(equalTo("header" + i)));
        }

        DeclarationSection declarationSection = legalDeclaration.getSections().get(0);
        assertThat(declarationSection.getHeadingType(), is(equalTo("large")));
        assertThat(declarationSection.getTitle(), is(equalTo("section title")));

        List<DeclarationItem> items = declarationSection.getDeclarationItems();
        assertThat(items.size(), is(equalTo(1)));

        DeclarationItem item = items.get(0);
        assertThat(item.getTitle(), is(equalTo("declaration title")));

        List<String> values = item.getValues();
        assertThat(values.size(), is(equalTo(3)));
        for (int i = 0; i < values.size() ; i++) {
            assertThat(values.get(i), is(equalTo("value" + i)));
        }
    }

    @Test
    public void shouldFailToCreateALegalDeclarationInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(INVALID_LEGAL_DECLARATION_JSON);
        LegalDeclaration legalDeclaration = objectMapper.readValue(optional.get().getFile(), LegalDeclaration.class);
        Set<ConstraintViolation<LegalDeclaration>> violations = validator.validate(legalDeclaration);
        assertThat(violations, is(not(empty())));
        assertThat(violations.size(), is(equalTo(2)));
    }

    private Optional<FileSystemResource> getFile(String fileName) {
        return fileSystemResourceService.getFileSystemResource(fileName);
    }
}
