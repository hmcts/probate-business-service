package uk.gov.hmcts.probate.services.businessdocuments.controllers;

import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

@PactBroker(scheme = "${pact.broker.scheme}", host = "${pact.broker.baseUrl}", port = "${pact.broker.port}", tags = {
    "${pact.broker.consumer.tag}"})
@IgnoreNoPactsToVerify
public abstract class ControllerProviderTest {

    @Before
    public void setUpTest() {
        System.getProperties().setProperty("pact.verifier.publishResults", "true");
    }

    protected JSONObject createJsonObject(String fileName) throws JSONException, IOException {
        File file = getFile(fileName);
        String jsonString = new String(Files.readAllBytes(file.toPath()));
        return new JSONObject(jsonString);
    }


    private File getFile(String fileName) throws FileNotFoundException {
        return ResourceUtils.getFile(this.getClass().getResource("/json/" + fileName));
    }
}
