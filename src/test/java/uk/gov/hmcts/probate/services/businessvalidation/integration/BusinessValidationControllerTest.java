package uk.gov.hmcts.probate.services.businessvalidation.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.services.businessvalidation.model.BusinessValidationStatus;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.businessvalidation.validators.DobBeforeDodRule;
import uk.gov.hmcts.probate.services.businessvalidation.validators.NetIHTLessThanGrossRule;
import uk.gov.hmcts.probate.services.businessvalidation.validators.ValidationRule;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BusinessValidationControllerTest {

    private static final String VALIDATE_SERVICE_URL = "/validate";

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
        MediaType.APPLICATION_JSON.getSubtype(),
        Charset.forName("utf8"));

    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private TestUtils utils;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        Arrays.stream(converters)
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findFirst()
            .ifPresent(converter -> this.mappingJackson2HttpMessageConverter = converter);
    }

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void validatePartialApplicant() throws Exception {
        mockMvc.perform(post(VALIDATE_SERVICE_URL)
            .header("Session-Id", "1234567890")
            .content(utils.getJsonFromFile("validation/success.PartialApplicant.json"))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is(BusinessValidationStatus.SUCCESS.toString())));
    }

    @Test
    public void validateApplicantEmptyField() throws Exception {

        mockMvc.perform(post(VALIDATE_SERVICE_URL)
            .header("Session-Id", "1234567890")
            .content(utils.getJsonFromFile("validation/failure.EmptyField.json"))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is(BusinessValidationStatus.FAILURE.toString())))
            .andExpect(jsonPath("$.errors[0].param", is("applicant.firstName")))
            .andExpect(jsonPath("$.errors[0].code", is("fieldMinSize")))
            .andExpect(jsonPath("$.errors[0].msg",
                is(messageSource.getMessage("fieldMinSize", null, LocaleContextHolder.getLocale()))));

    }

    @Test
    public void validateDateOfBirthBeforeDateOfDeath() throws Exception {

        mockMvc.perform(post(VALIDATE_SERVICE_URL)
            .header("Session-Id", "1234567890")
            .content(utils.getJsonFromFile("validation/failure.DodAfterDob.json"))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is(BusinessValidationStatus.FAILURE.toString())))
            .andExpect(jsonPath("$.errors[0].param", is(ValidationRule.BUSINESS_ERROR)))
            .andExpect(jsonPath("$.errors[0].code", is(DobBeforeDodRule.CODE)))
            .andExpect(jsonPath("$.errors[0].msg", is(messageSource.getMessage("dodBeforeDob", null, Locale.UK))));

    }

    @Test
    public void validateNetIHTValueGreaterThanGross() throws Exception {

        mockMvc.perform(post(VALIDATE_SERVICE_URL)
            .header("Session-Id", "1234567890")
            .content(utils.getJsonFromFile("validation/failure.NetIHTValueGreaterThanGross.json"))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is(BusinessValidationStatus.FAILURE.toString())))
            .andExpect(jsonPath("$.errors[0].param", is(ValidationRule.BUSINESS_ERROR)))
            .andExpect(jsonPath("$.errors[0].code", is(NetIHTLessThanGrossRule.CODE)))
            .andExpect(jsonPath("$.errors[0].msg",
                is(messageSource.getMessage("ihtNetGreaterThanGross", null, LocaleContextHolder.getLocale()))));
    }


    @Test
    public void validateWithoutSessionId() throws Exception {
        mockMvc.perform(post(VALIDATE_SERVICE_URL)
            .content(utils.getJsonFromFile("validation/failure.DodAfterDob.json"))
            .contentType(contentType))
            .andExpect(status().isBadRequest());
    }
}
