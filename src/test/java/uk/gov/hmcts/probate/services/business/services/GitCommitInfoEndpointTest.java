package uk.gov.hmcts.probate.services.business.services;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@RunWith(SpringRunner.class)
@Configuration
@TestPropertySource(properties = {"spring.info.git.location=classpath:uk/gov/hmcts/probate/services/business/git-test.properties"})
public class GitCommitInfoEndpointTest {

	private static final String EXPECTED_COMMIT_ID_INFO_RESPONSE = "0773f129ad51c4a23a49fec96fec0888883443f6";
	private static final String EXPECTED_COMMIT_TIME_INFO_RESPONSE = "2018-05-23T13:59+1234";
	
    private MockMvc mockMvc;
    
    @Test
    public void testGitCommitInfoEndpoint()
            throws Exception {
    	
    	try { 
	    	MvcResult result = this.mockMvc.perform(get("/info"))
	                .andExpect(status().isOk()).andReturn();	
	    	String actualInfoEndpointJsonResponse = result.getResponse().getContentAsString(); 
	    	
	    	JSONParser jsonParser = new JSONParser(JSONParser.MODE_PERMISSIVE);
	    	JSONObject responseObj = (JSONObject) jsonParser.parse(actualInfoEndpointJsonResponse);
	    	JSONObject gitObject = (JSONObject) responseObj.get("git");
	    	JSONObject commitObject = (JSONObject) gitObject.get("commit");
	    	
	    	assertEquals("Test commit id response is correct.",EXPECTED_COMMIT_ID_INFO_RESPONSE, commitObject.getAsString("id"));
	    	assertEquals("Test commit time response is correct.",EXPECTED_COMMIT_TIME_INFO_RESPONSE, commitObject.getAsString("time"));
	    	
    	} catch ( Exception e ) {
    		e.printStackTrace();
    	}
    }
}

