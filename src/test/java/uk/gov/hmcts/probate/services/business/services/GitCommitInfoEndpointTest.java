package uk.gov.hmcts.probate.services.business.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.info.git.location=classpath:uk/gov/hmcts/probate/services/business/git-test.properties",
    "management.endpoints.web.exposure.include=info"})
public class GitCommitInfoEndpointTest {

    private static final String EXPECTED_COMMIT_ID_INFO_RESPONSE = "0773f12";
    private static final String EXPECTED_COMMIT_TIME_INFO_RESPONSE = "2018-05-23T13:59+1234";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }


    @Test
    public void shouldGetGitCommitInfoEndpoint() throws Exception {

        ResultMatcher ok = MockMvcResultMatchers.status().isOk();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/actuator/info");

        this.mockMvc.perform(builder)
            .andExpect(ok)
            .andExpect(jsonPath("$.git.commit.id").value(EXPECTED_COMMIT_ID_INFO_RESPONSE))
            .andExpect(jsonPath("$.git.commit.time").value(EXPECTED_COMMIT_TIME_INFO_RESPONSE));
    }
}
