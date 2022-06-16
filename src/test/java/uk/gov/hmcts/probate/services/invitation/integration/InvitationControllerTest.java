package uk.gov.hmcts.probate.services.invitation.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.persistence.PersistenceClient;
import uk.gov.service.notify.NotificationClient;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class InvitationControllerTest {

    private static final String SERVICE_URL = "/invite";
    private static final String BILINGULAL_SERVICE_URL = "/invite/bilingual";

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

    @MockBean
    private PersistenceClient persistenceClient;

    @MockBean
    private NotificationClient notificationClient;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        Arrays.stream(converters)
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findFirst()
            .ifPresent(converter -> this.mappingJackson2HttpMessageConverter = converter);
    }

    @BeforeEach
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void generateLinkId() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(SERVICE_URL)
            .header("Session-Id", "1234567890")
            .content(utils.getJsonFromFile("invitation/success.json"))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("king-michael-i")));
    }


    @Test
    public void resendInvitation() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(SERVICE_URL + "/2321312312")
            .header("Session-Id", "1234567890")
            .content(utils.getJsonFromFile("invitation/success.json"))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("2321312312")));
    }

    @Test
    public void sendInvitation() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(SERVICE_URL + "/2321312312")
            .header("Session-Id", "1234567890")
            .content(utils.getJsonFromFile("invitation/success.json"))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("2321312312")));
    }

    @Test
    public void sendBilingualInvitation() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(BILINGULAL_SERVICE_URL + "/2321312312")
            .header("Session-Id", "1234567890")
            .content(utils.getJsonFromFile("invitation/success.json"))
            .contentType(contentType))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("2321312312")));
    }

}
