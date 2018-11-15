package uk.gov.hmcts.probate.services.businessdocuments.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import org.hibernate.validator.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@JsonRootName(value = "checkAnswersSummary")
public class CheckAnswersSummary implements Serializable {

    @NotBlank
    @JsonProperty("pageTitle")
    private String pageTitle;

    @JsonProperty("mainParagraph")
    private String mainParagraph;

    @JsonProperty("sections")
    private List<Section> sections;

    public String getMainParagraph() {
        return mainParagraph;
    }

    public void setMainParagraph(String mainParagraph) {
        this.mainParagraph = mainParagraph;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }


}
