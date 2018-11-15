package uk.gov.hmcts.probate.services.businessdocuments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public class Section implements Serializable {

    @JsonProperty("title")
    private String title;

    @JsonProperty("type")
    private String type;

    @JsonProperty("questionAndAnswers")
    private List<QuestionAndAnswerRow> questionsAndAnswers;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<QuestionAndAnswerRow> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public void setQuestionsAndAnswers(List<QuestionAndAnswerRow> questionsAndAnswers) {
        this.questionsAndAnswers = questionsAndAnswers;
    }


}
