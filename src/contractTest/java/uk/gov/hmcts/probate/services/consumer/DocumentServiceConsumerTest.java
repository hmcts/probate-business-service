package uk.gov.hmcts.probate.services.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import au.com.dius.pact.core.model.annotations.PactFolder;
import uk.gov.hmcts.probate.BusinessApplication;
import uk.gov.hmcts.probate.services.document.DocumentService;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;
import java.util.Map;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.google.common.collect.Maps;
import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * PACT interactions for calls made from DocumentService (the Consumer)
 * to DocumentManagement Store (aka dm-store) [the Provider]
 *
 */

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@PactTestFor(providerName = "em_dm_store", port = "5006")
@PactFolder("pacts")
@SpringBootTest({
    "document_management.url : http://localhost:5006"
})
public class DocumentServiceConsumerTest {

    @MockBean
    private AuthTokenGenerator authTokenGenerator;

    @Autowired
    private DocumentService documentService ;

    @Value("${document_management.url}")
    private String documentManagementUrl;

    public static final String SOME_SERVICE_AUTHORIZATION_TOKEN = "ServiceToken";
    private static final String USER_ID = "id1";

    private static final String DOCUMENT_ID = "5c3c3906-2b51-468e-8cbb-a4002eded075";


    @BeforeEach
    public void setUpEachTest() throws InterruptedException {
        Thread.sleep(2000);
    }

    @Pact(consumer = "probate_businessService")
    RequestResponsePact deleteDocument(PactDslWithProvider builder) throws IOException {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("ServiceAuthorization", SOME_SERVICE_AUTHORIZATION_TOKEN);
        headers.put("user-id", USER_ID);

        return builder
            .given("I have existing document")
            .uponReceiving("and I request to delete the document")
            .path("/documents/" + DOCUMENT_ID)
            .query("permanent=true")
            .method("DELETE")
            .headers(headers)
            .willRespondWith()
            .status(204)
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "deleteDocument")
    public void verifyDeleteDocumentPact() throws JSONException {
        when(authTokenGenerator.generate()).thenReturn(SOME_SERVICE_AUTHORIZATION_TOKEN);

        ResponseEntity<?> responses = documentService.delete(USER_ID , DOCUMENT_ID);

        assertThat(responses.getStatusCode().is2xxSuccessful());

    }


}
