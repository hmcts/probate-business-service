package uk.gov.hmcts.probate.services.pin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.probate.services.idgeneration.IdGeneratorService;
import uk.gov.hmcts.probate.services.notification.NotificationClientProvider;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

import java.util.HashMap;
import java.util.Map;

@Component
public class PinService {

    @Value("${services.notify.pin.templateId}")
    String templateId;

    @Value("${services.notify.pin.bilingualTemplateId}")
    String bilingualTemplateId;

    @Autowired
    private NotificationClientProvider notificationClientProvider;

    @Autowired
    private IdGeneratorService pinGeneratorService;


    public String generateAndSend(String phoneNumber, Boolean isBilingual) throws NotificationClientException {
        String pin = pinGeneratorService.generate();
        this.getClient()
            .sendSms(isBilingual ? bilingualTemplateId : templateId, phoneNumber, createPersonalisation(pin), pin);
        return pin;
    }

    private Map<String, String> createPersonalisation(String pin) {
        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put("pin", pin);
        return personalisation;
    }

    private NotificationClient getClient() {
        return notificationClientProvider.getClient();
    }
}
