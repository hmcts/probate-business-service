package uk.gov.hmcts.probate.functional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.probate.services.businessdocuments.model.CheckAnswersSummary;
import uk.gov.hmcts.probate.services.businessdocuments.model.QuestionAndAnswerRow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Test class to compare the contents of the inbound JSON file against the content of the generated
 * PDF document. Since we have limited functionality for testing by using PDFBox we simply test to
 * see if the strings of content exist in the pdf document when its generated.
 *
 * We concaternate the question and answer together before we look for the corresponding pdf string.
 * When we have a multi answer question we will look for all strings answers belonging to that question.
 */
@RunWith(SerenityRunner.class)
public class CheckSummaryAnswerPDFTest extends IntegrationTestBase {

    private static final String SIMPLE_SUMMARY = "checkAnswersSimpleSummary.json";
    private static final String MULTITIPLE_EXECUTORS = "checkAnswersMultipleExecutorsSummary.json";
    private static final String CHECK_ANSWERS_SUMMARY_PDF_URL = "/businessDocument/generateCheckAnswersSummaryPDF";
    private static final String CHECK_ANSWERS_WITH_ALIAS = "checkAnswersWithAliasNames.json";


    @Test
    public void shouldPassSimpleSummary() throws Exception {
        String pdfContentAsString = pdfContentAsString(SIMPLE_SUMMARY, CHECK_ANSWERS_SUMMARY_PDF_URL);
        CheckAnswersSummary checkAnswersSummary = getCheckAnswersSummaryFromJSON(SIMPLE_SUMMARY);
        validatePDFContent(pdfContentAsString, checkAnswersSummary);
    }

    @Test
    public void shouldPassMultipleExecutorsSummary() throws Exception {
        String pdfContentAsString = pdfContentAsString(MULTITIPLE_EXECUTORS, CHECK_ANSWERS_SUMMARY_PDF_URL);
        CheckAnswersSummary checkAnswersSummary = getCheckAnswersSummaryFromJSON(MULTITIPLE_EXECUTORS);
        validatePDFContent(pdfContentAsString, checkAnswersSummary);
    }

    @Test
    public void shouldPassAliasNamesSummary() throws Exception {
        String pdfContentAsString = pdfContentAsString(CHECK_ANSWERS_WITH_ALIAS, CHECK_ANSWERS_SUMMARY_PDF_URL);
        CheckAnswersSummary checkAnswersSummary = getCheckAnswersSummaryFromJSON(CHECK_ANSWERS_WITH_ALIAS);
        validatePDFContent(pdfContentAsString, checkAnswersSummary);
    }

    private void validatePDFContent(String pdfContentAsString, CheckAnswersSummary checkAnswersSummary) {
        assertThat(pdfContentAsString, containsString(parsedString(checkAnswersSummary.getPageTitle())));
        assertThat(pdfContentAsString, containsString(parsedString(checkAnswersSummary.getMainParagraph())));

        checkAnswersSummary.getSections().forEach(section -> {
            assertThat(pdfContentAsString, containsString(parsedString(section.getTitle())));
            assertQuestionsAndAnswers(pdfContentAsString, section.getQuestionsAndAnswers());
        });
    }

    private void assertQuestionsAndAnswers(String pdfContentAsString, List<QuestionAndAnswerRow> questionAndAnswerRows) {
        questionAndAnswerRows.forEach(questionAndAnswerRow -> {
            assertThat(pdfContentAsString, containsString(parsedString(questionAndAnswerRow.getQuestion())));
            assertAnswers(pdfContentAsString, questionAndAnswerRow);
        });
    }

    private void assertAnswers(String pdfContentAsString, QuestionAndAnswerRow questionAndAnswerRow) {
        StringBuilder question = new StringBuilder(questionAndAnswerRow.getQuestion());

        for (String answer : questionAndAnswerRow.getAnswers()) {
            question.append(answer);
            assertThat(pdfContentAsString, containsString(parsedString(question.toString())));
        };
    }

    private CheckAnswersSummary getCheckAnswersSummaryFromJSON(String JSONFileName) throws IOException {
        String jsonString = utils.getJsonFromFile(JSONFileName);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,true);
        return mapper.readValue(jsonString, CheckAnswersSummary.class);
    }

    private String pdfContentAsString(String JSONFileName, String documentURL) throws IOException {
        ValidatableResponse response = SerenityRest.given().relaxedHTTPSValidation().headers(utils.getHeadersWithServiceToken())
                .body(utils.getJsonFromFile(JSONFileName))
                .when().post(businessServiceUrl + documentURL)
                .then().assertThat().statusCode(200);

        PDDocument pdfDocument = PDDocument.load(new ByteArrayInputStream(response.extract().asByteArray()));
        try {
            return new PDFTextStripper().getText(pdfDocument).replaceAll("\\n","").replaceAll(" ","");
        } finally {
            pdfDocument.close();
        }
    }

    private String parsedString(String string) {
        return string.replaceAll(" ","").replaceAll("\\n","");
    }

}
