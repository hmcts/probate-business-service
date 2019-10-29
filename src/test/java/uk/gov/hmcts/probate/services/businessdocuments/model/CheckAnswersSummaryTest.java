package uk.gov.hmcts.probate.services.businessdocuments.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import uk.gov.hmcts.probate.services.businessdocuments.services.FileSystemResourceService;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;
import uk.gov.hmcts.reform.probate.model.documents.QuestionAndAnswerRow;
import uk.gov.hmcts.reform.probate.model.documents.Section;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class CheckAnswersSummaryTest {

    public static final String VALID_CHECK_ANSWERS_SUMMARY_JSON = "businessdocuments/validCheckAnswersSummary.json";
    public static final String INVALID_CHECK_ANSWERS_SUMMARY_JSON = "businessdocuments/invalidCheckAnswersSummary.json";
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
    public void shouldCreateACheckAnswersSummaryInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(VALID_CHECK_ANSWERS_SUMMARY_JSON);
        CheckAnswersSummary checkAnswerSummary = objectMapper.readValue(optional.get().getFile(), CheckAnswersSummary.class);
        assertThat(checkAnswerSummary, is(notNullValue()));
        Set<ConstraintViolation<CheckAnswersSummary>> violations = validator.validate(checkAnswerSummary);
        assertThat(violations, is(empty()));

        assertThat(checkAnswerSummary.getPageTitle(), is(equalTo("page title")));
        assertThat(checkAnswerSummary.getMainParagraph(), is(equalTo("main paragraph")));

        Section section = checkAnswerSummary.getSections().get(0);
        assertThat(section.getTitle(), is(equalTo("section title")));
        assertThat(section.getType(), is(equalTo("heading-medium")));

        QuestionAndAnswerRow row = section.getQuestionsAndAnswers().get(0);
        assertThat(row.getQuestion(), is(equalTo("question 1")));
        assertThat(row.getAnswers().get(0), is(equalTo("answer 1")));

        row = section.getQuestionsAndAnswers().get(1);
        assertThat(row.getQuestion(), is(equalTo("question 2")));
        assertThat(row.getAnswers().get(0), is(equalTo("not answered")));
    }

    @Test
    public void shouldFailToCreateACheckAnswersSummaryInstance() throws IOException {
        Optional<FileSystemResource> optional = getFile(INVALID_CHECK_ANSWERS_SUMMARY_JSON);
        CheckAnswersSummary checkAnswerSummary = objectMapper.readValue(optional.get().getFile(), CheckAnswersSummary.class);
        Set<ConstraintViolation<CheckAnswersSummary>> violations = validator.validate(checkAnswerSummary);
        assertThat(violations, is(not(empty())));
        assertThat(violations.size(), is(equalTo(2)));
    }

    private Optional<FileSystemResource> getFile(String fileName) {
        return fileSystemResourceService.getFileSystemResource(fileName);
    }
}
