package uk.gov.hmcts.probate.services.invitation.unit;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.probate.services.businessvalidation.util.TestUtils;
import uk.gov.hmcts.probate.services.invitation.InvitationService;
import uk.gov.hmcts.reform.probate.model.multiapplicant.Invitation;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class InvitationServiceTest {

    public static final String ENCODED_INVITATION = "invitation/success.json";
    public static final String EXPECTED_DECODING = "invitation/expectedDecoding.json";
    public ObjectMapper objectMapper;
    @Autowired
    private TestUtils utils;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
    }

    @Test
    void testInvitationDecoding() throws Exception, UnsupportedEncodingException {
        InvitationService invitationService = new InvitationService();

        Invitation encodedInvitation =
            objectMapper.readValue(utils.getJsonFromFile(ENCODED_INVITATION), Invitation.class);
        Invitation expectedDecoding =
            objectMapper.readValue(utils.getJsonFromFile(EXPECTED_DECODING), Invitation.class);

        Invitation decodedInvitation = invitationService.decodeURL(encodedInvitation);
        assertEquals(expectedDecoding.getFirstName(), decodedInvitation.getFirstName());
        assertEquals(expectedDecoding.getLastName(), decodedInvitation.getLastName());
        assertEquals(expectedDecoding.getExecutorName(), decodedInvitation.getExecutorName());
        assertEquals(expectedDecoding.getLeadExecutorName(), decodedInvitation.getLeadExecutorName());
    }
}
