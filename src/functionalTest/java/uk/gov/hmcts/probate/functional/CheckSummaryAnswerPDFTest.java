package uk.gov.hmcts.probate.functional;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.gov.hmcts.reform.probate.model.documents.CheckAnswersSummary;

/**
 * Test class to compare the contents of the inbound JSON file against the content of the generated
 * PDF document. Since we have limited functionality for testing by using PDFBox we simply test to
 * see if the strings of content exist in the pdf document when its generated.
 *
 * We concaternate the question and answer together before we look for the corresponding pdf string.
 * When we have a multi answer question we will look for all strings answers belonging to that question.
 */
@RunWith(SerenityRunner.class)
public class CheckSummaryAnswerPDFTest extends PDFIntegrationBase<CheckAnswersSummary> {

    private static final String SIMPLE_SUMMARY = "checkAnswersSimpleSummary.json";
    private static final String MULTITIPLE_EXECUTORS = "checkAnswersMultipleExecutorsSummary.json";
    private static final String CHECK_ANSWERS_SUMMARY_PDF_URL = "/businessDocument/generateCheckAnswersSummaryPDF";
    private static final String CHECK_ANSWERS_WITH_ALIAS = "checkAnswersWithAliasNames.json";


    @Test
    public void shouldPassSimpleSummary() throws Exception {
        String pdfContentAsString = pdfContentAsString(SIMPLE_SUMMARY, CHECK_ANSWERS_SUMMARY_PDF_URL);
        CheckAnswersSummary checkAnswersSummary = getJSONObject(SIMPLE_SUMMARY, CheckAnswersSummary.class);
        validatePDFContent(pdfContentAsString, checkAnswersSummary);
    }

    @Test
    public void shouldPassMultipleExecutorsSummary() throws Exception {
        String pdfContentAsString = pdfContentAsString(MULTITIPLE_EXECUTORS, CHECK_ANSWERS_SUMMARY_PDF_URL);
        CheckAnswersSummary checkAnswersSummary = getJSONObject(MULTITIPLE_EXECUTORS, CheckAnswersSummary.class);
        validatePDFContent(pdfContentAsString, checkAnswersSummary);
    }

    @Test
    public void shouldPassAliasNamesSummary() throws Exception {
        String pdfContentAsString = pdfContentAsString(CHECK_ANSWERS_WITH_ALIAS, CHECK_ANSWERS_SUMMARY_PDF_URL);
        CheckAnswersSummary checkAnswersSummary = getJSONObject(CHECK_ANSWERS_WITH_ALIAS, CheckAnswersSummary.class);
        validatePDFContent(pdfContentAsString, checkAnswersSummary);
    }

    private void validatePDFContent(String pdfContentAsString, CheckAnswersSummary checkAnswersSummary) {
        assertContent(pdfContentAsString, checkAnswersSummary.getPageTitle());
        assertContent(pdfContentAsString, checkAnswersSummary.getMainParagraph());

        checkAnswersSummary.getSections().forEach(section -> {
            assertContent(pdfContentAsString, section.getTitle());
            section.getQuestionsAndAnswers().forEach(questionAndAnswer -> {
                assertContent(pdfContentAsString, questionAndAnswer.getQuestion());
                String question = questionAndAnswer.getQuestion();
                for (String answer : questionAndAnswer.getAnswers()) {
                    question = question + answer;
                    assertContent(pdfContentAsString, question);
                };
            });
        });
    }

}
