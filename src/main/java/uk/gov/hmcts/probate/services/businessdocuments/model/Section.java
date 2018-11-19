package uk.gov.hmcts.probate.services.businessdocuments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

public class Section implements Serializable {

    @NotBlank
    @JsonProperty("title")
    private String title;

    @NotBlank
    @JsonProperty("type")
    private String type;

    @NotEmpty
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
