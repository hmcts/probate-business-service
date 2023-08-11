package uk.gov.hmcts.probate.services.businessdocuments.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.services.businessdocuments.services.FileSystemResourceService;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;
import uk.gov.hmcts.reform.probate.model.documents.QuestionAndAnswerRow;
import uk.gov.hmcts.reform.probate.model.documents.Section;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CheckAnswersSummaryTest {

    public static final String VALID_CHECK_ANSWERS_SUMMARY_JSON = "businessdocuments/validCheckAnswersSummary.json";
    public static final String INVALID_CHECK_ANSWERS_SUMMARY_JSON = "businessdocuments/invalidCheckAnswersSummary.json";
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
    public void shouldCreateACheckAnswersSummaryInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_CHECK_ANSWERS_SUMMARY_JSON);
        CheckAnswersSummary checkAnswerSummary =
            objectMapper.readValue(optional.get().getFile(), CheckAnswersSummary.class);
        assertNotNull(checkAnswerSummary);
        Set<ConstraintViolation<CheckAnswersSummary>> violations = validator.validate(checkAnswerSummary);
        assertEquals(0, violations.size());

        assertEquals("page title", checkAnswerSummary.getPageTitle());
        assertEquals("main paragraph", checkAnswerSummary.getMainParagraph());

        Section section = checkAnswerSummary.getSections().get(0);
        assertEquals("section title", section.getTitle());
        assertEquals("heading-medium", section.getType());

        QuestionAndAnswerRow row = section.getQuestionsAndAnswers().get(0);
        assertEquals("question 1", row.getQuestion());
        assertEquals("answer 1", row.getAnswers().get(0));

        row = section.getQuestionsAndAnswers().get(1);
        assertEquals("question 2", row.getQuestion());
        assertEquals("not answered", row.getAnswers().get(0));
    }

    @Test
    public void shouldFailToCreateACheckAnswersSummaryInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(INVALID_CHECK_ANSWERS_SUMMARY_JSON);
        CheckAnswersSummary checkAnswerSummary =
            objectMapper.readValue(optional.get().getFile(), CheckAnswersSummary.class);
        Set<ConstraintViolation<CheckAnswersSummary>> violations = validator.validate(checkAnswerSummary);
        assertEquals(2, violations.size());
    }

    private Optional<FileSystemResource> getFile(String fileName) {
        return fileSystemResourceService.getFileSystemResource(fileName);
    }
}
