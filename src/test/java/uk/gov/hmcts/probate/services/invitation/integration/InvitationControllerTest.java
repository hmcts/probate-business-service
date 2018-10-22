package uk.gov.hmcts.probate.services.invitation.integration;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.persistence.PersistenceClient;
import uk.gov.service.notify.NotificationClient;

import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvitationControllerTest {

    private static final String SERVICE_URL = "/invite";

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

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void generateLinkId() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(SERVICE_URL)
                .header("Session-Id", "1234567890")
                .content(utils.getJSONFromFile("invitation/success.json"))
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("king-michael-i")));
    }


    @Test
    public void checkAllAgreed() throws Exception {
        when(persistenceClient.getInvitesByFormdataId(any(String.class))).thenReturn(JsonNodeFactory.instance.objectNode().put("agreed", true));
        when(persistenceClient.getFormdata(any(String.class))).thenReturn(JsonNodeFactory.instance.objectNode().put("declarationCheckbox", "true"));

        ResultActions resultActions = mockMvc.perform(get("/invites/allAgreed/123")
                .header("Session-Id", "1234567890")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("true")));
    }

    @Test
    public void checkInviteeNotAgreed() throws Exception {
        final String AGREED = null;
        when(persistenceClient.getInvitesByFormdataId(any(String.class))).thenReturn(JsonNodeFactory.instance.objectNode().put("agreed", AGREED));
        when(persistenceClient.getFormdata(any(String.class))).thenReturn(JsonNodeFactory.instance.objectNode().put("declarationCheckbox", "true"));

        ResultActions resultActions = mockMvc.perform(get("/invites/allAgreed/123")
                .header("Session-Id", "1234567890")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("false")));
    }

    @Test
    public void checkInviteeDisAgreed() throws Exception {
        when(persistenceClient.getInvitesByFormdataId(any(String.class))).thenReturn(JsonNodeFactory.instance.objectNode().put("agreed", false));
        when(persistenceClient.getFormdata(any(String.class))).thenReturn(JsonNodeFactory.instance.objectNode().put("declarationCheckbox", "false"));

        ResultActions resultActions = mockMvc.perform(get("/invites/allAgreed/123")
                .header("Session-Id", "1234567890")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("false")));
    }

    @Test
    public void checkMainApplicantRedeclared() throws Exception {
        when(persistenceClient.getInvitesByFormdataId(any(String.class))).thenReturn(JsonNodeFactory.instance.objectNode().put("agreed", true));
        when(persistenceClient.getFormdata(any(String.class))).thenReturn(JsonNodeFactory.instance.objectNode());

        ResultActions resultActions = mockMvc.perform(get("/invites/allAgreed/123")
                .header("Session-Id", "1234567890")
                .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("false")));
    }
}