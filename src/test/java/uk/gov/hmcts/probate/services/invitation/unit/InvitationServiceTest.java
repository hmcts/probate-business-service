package uk.gov.hmcts.probate.services.invitation.unit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.invitation.InvitationService;
import uk.gov.hmcts.probate.services.invitation.model.Invitation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.UnsupportedEncodingException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InvitationServiceTest {

    @Autowired
    private TestUtils utils;

    public ObjectMapper objectMapper;
    public static final String ENCODED_INVITATION = "invitation/success.json";
    public static final String EXPECTED_DECODING = "invitation/expectedDecoding.json";

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE,true);
    }

    @Test
    public void testInvitationDecoding() throws Exception, UnsupportedEncodingException {
        InvitationService invitationService = new InvitationService();

        Invitation encodedInvitation = objectMapper.readValue(utils.getJSONFromFile(ENCODED_INVITATION), Invitation.class);
        Invitation expectedDecoding = objectMapper.readValue(utils.getJSONFromFile(EXPECTED_DECODING), Invitation.class);

        Invitation decodedInvitation = invitationService.decodeURL(encodedInvitation);
        assertThat(decodedInvitation.getFirstName(), equalTo(expectedDecoding.getFirstName()));
        assertThat(decodedInvitation.getLastName(), equalTo(expectedDecoding.getLastName()));
        assertThat(decodedInvitation.getExecutorName(), equalTo(expectedDecoding.getExecutorName()));
        assertThat(decodedInvitation.getLeadExecutorName(), equalTo(expectedDecoding.getLeadExecutorName()));
    }
}
