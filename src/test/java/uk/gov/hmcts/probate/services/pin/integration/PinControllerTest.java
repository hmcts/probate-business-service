package uk.gov.hmcts.probate.services.pin.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendSmsResponse;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class PinControllerTest {

    private static final String SERVICE_URL = "/pin";
    private static final String BILINGUAL_URL = "/pin/bilingual";
    private static final String TEST_SESSION_ID = "1234567890";
    private static final String TEST_UK_PHONE_NUMBER = "(0)7700900111";
    private static final String TEST_INT_PHONE_NUMBER = "%2B447700900111";
    private static final String TEST_BAD_PHONE_NUMBER = "$447700900111";
    private static final String TEST_LARGE_PHONE_NUMBER = "%2B109001110001110";
    private SendSmsResponse smsResponse;


    private MediaType contentType = new MediaType(MediaType.TEXT_PLAIN.getType(),
        MediaType.TEXT_PLAIN.getSubtype(),
        Charset.forName("utf8"));

    private MockMvc mockMvc;
    @SuppressWarnings("unused")
    private HttpMessageConverter<?> mappingJackson2HttpMessageConverter;

    @MockBean
    private NotificationClient notificationClient;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        Arrays.stream(converters)
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findFirst()
            .ifPresent(converter -> this.mappingJackson2HttpMessageConverter = converter);
    }

    @BeforeEach
    public void setup() throws NotificationClientException {
        when(notificationClient.sendSms(any(), any(), any(), any())).thenReturn(smsResponse);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void generatePinFromUkNumber() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?phoneNumber=" + TEST_UK_PHONE_NUMBER)
            .header("Session-Id", TEST_SESSION_ID)
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    @Test
    void generatePinFromUkNumberPost() throws Exception {
        mockMvc.perform(post(SERVICE_URL)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"PhonePin\": {\"phoneNumber\": \"" + TEST_UK_PHONE_NUMBER + "\"}}"))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    @Test
    void generatePinFromInternationalNumber() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?phoneNumber=" + TEST_INT_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    @Test
    void generatePinFromInternationalNumberPost() throws Exception {
        mockMvc.perform(post(SERVICE_URL)
            .header("Session-Id", TEST_SESSION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"PhonePin\": {\"phoneNumber\": \"" + TEST_INT_PHONE_NUMBER + "\"}}"))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    @Test
    void generatePinFromLargeInternationalNumber() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?phoneNumber=" + TEST_LARGE_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    @Test
    void generatePinFromLargeInternationalNumberPost() throws Exception {
        mockMvc.perform(post(SERVICE_URL)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"PhonePin\": {\"phoneNumber\": \"" + TEST_LARGE_PHONE_NUMBER + "\"}}"))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    @Test
    void generatePinFromBadNumber() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?phoneNumber=" + TEST_BAD_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    @Test
    void generatePinFromBadNumberPost() throws Exception {
        mockMvc.perform(post(SERVICE_URL)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"PhonePin\": {\"phoneNumber\": \"" + TEST_BAD_PHONE_NUMBER + "\"}}"))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    @Test
    void generatePinFromBadParameterName() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?number=" + TEST_UK_PHONE_NUMBER)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
            .andExpect(status().isBadRequest());
    }

    @Test
    void generatePinFromBadParameterNamePost() throws Exception {
        mockMvc.perform(post(SERVICE_URL)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"PhonePin\": {\"number\": \"" + TEST_UK_PHONE_NUMBER + "\"}}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void generatePinFromMissingSessionId() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "?phoneNumber=" + TEST_UK_PHONE_NUMBER)
                .contentType(contentType))
            .andExpect(status().isBadRequest());
    }

    @Test
    void generatePinFromMissingSessionIdPost() throws Exception {
        mockMvc.perform(post(SERVICE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"PhonePin\": {\"phoneNumber\": \"" + TEST_UK_PHONE_NUMBER + "\"}}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void inviteLegacy() throws Exception {
        mockMvc.perform(get(SERVICE_URL + "/" + TEST_UK_PHONE_NUMBER)
            .header("Session-Id", TEST_SESSION_ID)
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    private static Stream<String> phoneNumber() {
        return Stream.of(TEST_UK_PHONE_NUMBER, TEST_INT_PHONE_NUMBER, TEST_LARGE_PHONE_NUMBER);
    }

    @ParameterizedTest
    @MethodSource("phoneNumber")
    void inviteBilingual(final String phoneNumber) throws Exception {
        mockMvc.perform(get(BILINGUAL_URL + "?phoneNumber=" + phoneNumber)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }

    @ParameterizedTest
    @MethodSource("phoneNumber")
    void inviteBilingualPost(final String phoneNumber) throws Exception {
        mockMvc.perform(post(BILINGUAL_URL)
                .header("Session-Id", TEST_SESSION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"PhonePin\": {\"phoneNumber\": \"" + phoneNumber + "\"}}")
            )
            .andExpect(status().isOk())
            .andExpect(content().string(lessThanOrEqualTo("999999")));
    }
}
