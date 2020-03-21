package uk.gov.hmcts.probate.services.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.probate.model.notification.ApplicationReceivedDetails;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private static final String PERSONALISATION_APPLICANT_NAME = "applicant_name";
    private static final String PERSONALISATION_DECEASED_NAME = "deceased_name";

    @Value("${services.notify.applicationRecieved.templateId}")
    String templateId;

    @Value("${services.notify.applicationRecieved.bilingualTemplateId}")
    String bilingualTemplateId;

    @Autowired
    private NotificationClient notificationClient;


    public void sendApplicationRecievedEmail(ApplicationReceivedDetails applicationRecievedDetails) throws NotificationClientException {
        LOGGER.info("Calling the notification client");
        notificationClient.sendEmail(applicationRecievedDetails.getBilingual() ? bilingualTemplateId : templateId, applicationRecievedDetails.getApplicantEmail(), createPersonalisation(applicationRecievedDetails), "");
        LOGGER.info("Returned from notification client");
    }


    private Map<String, String> createPersonalisation(ApplicationReceivedDetails applicationRecievedDetails) {
        LOGGER.info("Creating personalisation");
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put(PERSONALISATION_APPLICANT_NAME, applicationRecievedDetails.getApplicantName());
        personalisation.put(PERSONALISATION_DECEASED_NAME, applicationRecievedDetails.getDeceasedName());
        return personalisation;
    }

    public ApplicationReceivedDetails decodeURL(ApplicationReceivedDetails applicationRecievedDetails) throws UnsupportedEncodingException {
        LOGGER.info("Decoding URL params");
        applicationRecievedDetails.setApplicantEmail(decodeURLParam(applicationRecievedDetails.getApplicantEmail()));
        applicationRecievedDetails.setApplicantName(decodeURLParam(applicationRecievedDetails.getApplicantName()));
        applicationRecievedDetails.setDeceasedName(decodeURLParam(applicationRecievedDetails.getDeceasedName()));
        return applicationRecievedDetails;
    }

    private String decodeURLParam(String URIParam) throws UnsupportedEncodingException {
        return URLDecoder.decode(URIParam, StandardCharsets.UTF_8.toString());
    }

}
