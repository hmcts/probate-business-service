package uk.gov.hmcts.probate.functional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public abstract class PDFIntegrationBase<T> extends IntegrationTestBase {

    /**
     * Strip out any \n or spaces to make string evaluations easier
     */
     String pdfContentAsString(String JSONFileName, String documentURL) throws IOException {
        ValidatableResponse response = RestAssured.given().relaxedHTTPSValidation().headers(utils.getHeadersWithServiceToken())
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

     String parsedString(String string) {
        return string.replaceAll(" ","").replaceAll("\\n","");
    }

     T getJSONObject(String JSONFileName, Class clazz) throws Exception{
        String jsonString = utils.getJsonFromFile(JSONFileName);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,true);
        return (T) mapper.readValue(jsonString, clazz);
    }

     void assertContent(String pdfString, String stringToFind) {
        assertThat(pdfString, containsString(parsedString(stringToFind)));
    }

}
