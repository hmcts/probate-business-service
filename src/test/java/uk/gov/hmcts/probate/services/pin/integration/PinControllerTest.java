package uk.gov.hmcts.probate.services.pin.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PinControllerTest {

    private static final String SERVICE_URL = "/pin";
    private static final String TEST_SESSION_ID = "1234567890";
    private static final String TEST_UK_PHONE_NUMBER = "(0)7700900111";
    private static final String TEST_INT_PHONE_NUMBER = "%2B447700900111";
    private static final String TEST_BAD_PHONE_NUMBER = "$447700900111";
    private static final String TEST_LARGE_PHONE_NUMBER = "%2B109001110001110";

    private MediaType contentType = new MediaType(MediaType.TEXT_PLAIN.getType(),
            MediaType.TEXT_PLAIN.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;
    @SuppressWarnings("unused")
	private HttpMessageConverter<?> mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findFirst()
                .ifPresent(converter -> this.mappingJackson2HttpMessageConverter = converter);
    }

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void generatePinFromUkNumber() throws Exception {
        mockMvc.perform(get(SERVICE_URL+"?phoneNumber="+TEST_UK_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().string(lessThanOrEqualTo("999999")));
    }
    @Test
    public void generatePinFromInternationalNumber() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?phoneNumber=" + TEST_INT_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().string(lessThanOrEqualTo("999999")));
    }
    @Test
    public void generatePinFromLargeInternationalNumber() throws Exception {
        mockMvc.perform(get(SERVICE_URL+"?phoneNumber="+TEST_LARGE_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().string(lessThanOrEqualTo("999999")));
    }
    @Test
    public void generatePinFromBadNumber() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?phoneNumber=" + TEST_BAD_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void generatePinFromBadParameterName() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?number=" + TEST_UK_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void generatePinFromMissingSessionId() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?phoneNumber=" + TEST_UK_PHONE_NUMBER)
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void inviteLegacy() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "/" + TEST_UK_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().string(lessThanOrEqualTo("999999")));
    }
}
